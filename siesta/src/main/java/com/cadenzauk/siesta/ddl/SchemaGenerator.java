/*
 * Copyright (c) 2018 Cadenza United Kingdom Limited
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.cadenzauk.siesta.ddl;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.lang.CompositeAutoCloseable;
import com.cadenzauk.core.reflect.Factory;
import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.core.reflect.util.ClassUtil;
import com.cadenzauk.core.reflect.util.ConstructorUtil;
import com.cadenzauk.core.sql.QualifiedName;
import com.cadenzauk.core.sql.ResultSetUtil;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Dialect;
import com.cadenzauk.siesta.SequenceInfo;
import com.cadenzauk.siesta.TempTableInfo;
import com.cadenzauk.siesta.ddl.action.Action;
import com.cadenzauk.siesta.ddl.action.ActionPipeline;
import com.cadenzauk.siesta.ddl.definition.action.DropForeignKeyAction;
import com.cadenzauk.siesta.ddl.definition.action.DropSequenceAction;
import com.cadenzauk.siesta.ddl.definition.action.DropTableAction;
import com.cadenzauk.siesta.ddl.definition.intercept.CreateIndexGenerator;
import com.cadenzauk.siesta.ddl.definition.intercept.CreateSequenceGenerator;
import com.cadenzauk.siesta.ddl.definition.intercept.CreateTableGenerator;
import com.cadenzauk.siesta.ddl.definition.intercept.DropForeignKeyGenerator;
import com.cadenzauk.siesta.ddl.definition.intercept.DropSequenceGenerator;
import com.cadenzauk.siesta.ddl.definition.intercept.DropTableGenerator;
import com.cadenzauk.siesta.ddl.log.action.CreateActionLogTable;
import com.cadenzauk.siesta.ddl.log.intercept.ActionFilter;
import com.cadenzauk.siesta.ddl.log.intercept.ActionLogGenerator;
import com.cadenzauk.siesta.ddl.log.intercept.ActionLogRecorder;
import com.cadenzauk.siesta.ddl.log.intercept.ActionLogTableCreator;
import com.cadenzauk.siesta.ddl.sql.intercept.SqlActionExecutor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.cadenzauk.core.sql.QualifiedName.matchesCatalogAndSchema;

public class SchemaGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(SchemaGenerator.class);
    private final ActionPipeline pipeline;
    private final boolean dropFirst;
    private final Dialect dialect;

    public SchemaGenerator(boolean dropFirst, Dialect dialect) {
        this.dropFirst = dropFirst;
        this.dialect = dialect;
        pipeline = new ActionPipeline();
        pipeline.addInterceptor(new ActionLogTableCreator());
        pipeline.addInterceptor(new ActionFilter());
        pipeline.addInterceptor(new ActionLogGenerator());
        pipeline.addInterceptor(new CreateSequenceGenerator());
        pipeline.addInterceptor(new CreateTableGenerator());
        pipeline.addInterceptor(new DropSequenceGenerator());
        pipeline.addInterceptor(new DropForeignKeyGenerator());
        pipeline.addInterceptor(new DropTableGenerator());
        pipeline.addInterceptor(new CreateIndexGenerator());
        pipeline.addInterceptor(new SqlActionExecutor());
        pipeline.addInterceptor(new ActionLogRecorder());
    }

    public void generate(Database database, SchemaDefinition schemaDefinition) {
        if (dropFirst) {
            long dropped = dropAll(database);
            LOG.debug("Dropped {} objects", dropped);
        }
        Stream<Action> actions = Stream.concat(
            Stream.of(new CreateActionLogTable(database.defaultCatalog(), database.defaultSchema())),
            schemaDefinition.actions());
        long count = pipeline
            .process(database, actions)
            .count();
        LOG.debug("Processed {} actions", count);
    }

    private long dropAll(Database database) {
        try (CompositeAutoCloseable closer = new CompositeAutoCloseable()) {
            SchemaMetadata metadata = new SchemaMetadata(database.getDefaultSqlExecutor().metadata(closer), dialect);
            Stream<Action> dropSequences = dropSequenceActions(database, closer);
            Stream<Action> dropForeignKeys = dropForeignKeyActions(database, closer, metadata);
            Stream<Action> dropGlobalTempTables = dropGlobalTempTableActions(database, closer, metadata);
            Stream<Action> dropTables = dropTableActions(database, closer, metadata);
            Stream<Action> actionStream = Stream.of(dropSequences, dropForeignKeys, dropGlobalTempTables, dropTables).flatMap(Function.identity());
            return pipeline.process(database, actionStream).count();
        }
    }

    private Stream<Action> dropTableActions(Database database, CompositeAutoCloseable closer, SchemaMetadata metadata) {
        return closer.add(metadata.tableNames(database.defaultCatalog(), database.defaultSchema()))
            .map(dialect::fixQualifiedName)
            .map(t -> DropTableAction.newBuilder()
                .logged(false)
                .catalog(t.catalog())
                .schemaName(t.schema())
                .tableName(t.name())
                .build());
    }

    private Stream<Action> dropGlobalTempTableActions(Database database, CompositeAutoCloseable closer, SchemaMetadata metadata) {
        return closer.add(globalTempTableStream(database))
            .map(t -> DropTableAction.newBuilder()
                .logged(false)
                .catalog(t.catalog())
                .schemaName(t.schema())
                .tableName(t.name())
                .build());
    }

    private Stream<Action> dropForeignKeyActions(Database database, CompositeAutoCloseable closer, SchemaMetadata metadata) {

        return closer.add(metadata.foreignKeyNames(database.defaultCatalog(), database.defaultSchema()))
            .map(t -> DropForeignKeyAction.newBuilder()
                .logged(false)
                .catalog(t.catalog())
                .schemaName(t.schema())
                .tableName(t.tableName())
                .constraintName(Optional.of(t.name()))
                .build());
    }

    private Stream<Action> dropSequenceActions(Database database, CompositeAutoCloseable closer) {
        if (!dialect.sequenceInfo().supportsSequences()) {
            return Stream.empty();
        }
        return closer.add(sequenceStream(database))
            .filter(matchesCatalogAndSchema(database.defaultCatalog(), database.defaultSchema()))
            .map(t -> DropSequenceAction.newBuilder()
                .logged(false)
                .catalog(t.catalog())
                .schemaName(t.schema())
                .sequenceName(t.name())
                .build());
    }

    private Stream<QualifiedName> globalTempTableStream(Database database) {
        TempTableInfo tempTableInfo = database.dialect().tempTableInfo();
        String sql = tempTableInfo.listGlobalSql();
        return StringUtils.isBlank(sql)
            ? Stream.empty()
            : database
                .getDefaultSqlExecutor()
                .stream(
                    sql,
                    new Object[0],
                    rs -> new QualifiedName(ResultSetUtil.getString(rs, "TEMP_TABLE_CATALOG"), ResultSetUtil.getString(rs, "TEMP_TABLE_SCHEMA_NAME"), ResultSetUtil.getString(rs, "TEMP_TABLE_NAME")));
    }

    private Stream<QualifiedName> sequenceStream(Database database) {
        SequenceInfo sequenceInfo = database.dialect().sequenceInfo();
        return database.getDefaultSqlExecutor().stream(
            sequenceInfo.listSql(),
            new Object[0],
            rs -> new QualifiedName(ResultSetUtil.getString(rs, "SEQUENCE_CATALOG"), ResultSetUtil.getString(rs, "SEQUENCE_SCHEMA"), ResultSetUtil.getString(rs, "SEQUENCE_NAME")));
    }

    public <T> void generate(Database database, Function1<T,SchemaDefinition> schemaDefinition) {
        MethodInfo<T,SchemaDefinition> methodInfo = MethodInfo.of(schemaDefinition);
        T schemaDefintionFactory = schemaConstructor(methodInfo.referringClass()).get();
        generate(database, schemaDefinition.apply(schemaDefintionFactory));
    }

    private <T> Supplier<T> schemaConstructor(Constructor<T> ctor) {
        return () -> ConstructorUtil.newInstance(ctor, dialect);
    }

    private <T> Supplier<T> schemaConstructor(Class<T> schemaClass) {
        return ClassUtil.constructor(schemaClass, Dialect.class)
            .map(this::schemaConstructor)
            .orElseGet(() -> Factory.forClass(schemaClass));
    }


}
