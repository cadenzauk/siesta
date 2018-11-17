/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited
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

package com.cadenzauk.siesta.catalog;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.core.reflect.util.ClassUtil;
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.core.stream.StreamUtil;
import com.cadenzauk.core.tuple.Tuple;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.DynamicRowMapper;
import com.cadenzauk.siesta.ForeignKey;
import com.cadenzauk.siesta.ForeignKeys;
import com.cadenzauk.siesta.Reference;
import com.cadenzauk.siesta.SqlExecutor;
import com.cadenzauk.siesta.Transaction;
import com.cadenzauk.siesta.grammar.InvalidForeignKeyException;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class Table<R> implements ColumnCollection<R> {
    private static final Logger LOG = LoggerFactory.getLogger(Table.class);
    private final Database database;
    private final TypeToken<R> rowType;
    private final String catalog;
    private final String schema;
    private final String tableName;
    private final ColumnMapping<R,?> columnMapping;
    private final List<ForeignKeyReference<R,?>> foreignKeys;

    private <B> Table(Builder<R,B> builder) {
        database = builder.database;
        rowType = builder.rowType;
        catalog = builder.catalog;
        schema = builder.schema;
        tableName = builder.tableName;
        columnMapping = new ColumnMapping<>(builder);
        foreignKeys = builder.foreignKeys
            .stream()
            .map(fk -> fk.childTable(this))
            .map(ForeignKeyReference.Builder::build)
            .collect(Collectors.collectingAndThen(toList(), ImmutableList::copyOf));
    }

    public Database database() {
        return database;
    }

    public String schema() {
        return schema;
    }

    public String tableName() {
        return tableName;
    }

    @Override
    public TypeToken<R> rowType() {
        return rowType;
    }

    @Override
    public Stream<Column<?,R>> columns() {
        return columnMapping.columns();
    }

    @Override
    public <T> Column<T,R> column(MethodInfo<R,T> methodInfo) {
        return columnMapping.column(methodInfo);
    }

    @Override
    public <T> ColumnCollection<T> embedded(MethodInfo<R,T> methodInfo) {
        return columnMapping.embedded(methodInfo);
    }

    @Override
    public RowMapper<R> rowMapper(Alias<?> alias) {
        return columnMapping.rowMapper(alias);
    }

    public DynamicRowMapper<R> dynamicRowMapper(Alias<?> alias) {
        return columnMapping.dynamicRowMapper(alias);
    }

    public String qualifiedName() {
        return database.dialect().qualifiedTableName(catalog, schema, tableName);
    }

    public Alias<R> as(String alias) {
        return Alias.of(this, alias);
    }

    public int insert(SqlExecutor sqlExecutor, R[] rows) {
        if (database().dialect().supportsMultiInsert()) {
            return performInsert(sqlExecutor, rows);
        } else {
            return Arrays.stream(rows)
                .mapToInt(r -> performInsert(sqlExecutor, r))
                .sum();
        }
    }

    public int insert(Transaction transaction, R[] rows) {
        if (database().dialect().supportsMultiInsert()) {
            return performInsert(transaction, rows);
        } else {
            return Arrays.stream(rows)
                .mapToInt(r -> performInsert(transaction, r))
                .sum();
        }
    }

    public int update(SqlExecutor sqlExecutor, R row) {
        if (row == null) {
            return 0;
        }
        String sql = updateSql();
        Object[] args = columnMapping.updateArgs(row);
        return database.execute(sql, () -> sqlExecutor.update(sql, args));
    }

    public int update(Transaction transaction, R row) {
        if (row == null) {
            return 0;
        }
        String sql = updateSql();
        Object[] args = columnMapping.updateArgs(row);
        return database.execute(sql, () -> transaction.update(sql, args));
    }

    public int delete(SqlExecutor sqlExecutor, R row) {
        if (row == null) {
            return 0;
        }
        String sql = deleteSql();
        Object[] args = columnMapping.deleteArgs(row);
        return database.execute(sql, () -> sqlExecutor.update(sql, args));
    }

    public int delete(Transaction transaction, R row) {
        if (row == null) {
            return 0;
        }
        String sql = deleteSql();
        Object[] args = columnMapping.deleteArgs(row);
        return database.execute(sql, () -> transaction.update(sql, args));
    }

    public <P> Optional<ForeignKeyReference<R,P>> foreignKey(Table<P> parent, Optional<String> name) {
        return foreignKeys
            .stream()
            .flatMap(fk -> fk.toParent(parent, name))
            .limit(2)
            .map(Optional::of)
            .reduce(Optional.empty(), (a, b) -> {
                if (a.isPresent()) {
                    throw new InvalidForeignKeyException(String.format("More than one foreign key from %s to %s.  Specify the one you want with onForeignKey(<name>).",
                        qualifiedName(),
                        parent.qualifiedName()));
                }
                return b;
            });
    }

    @SuppressWarnings("unchecked")
    public <F> Optional<Column<F,R>> findColumn(Class<F> fieldClass, String propertyName) {
        return columns()
            .filter(c -> StringUtils.equals(propertyName, c.propertyName()))
            .map(x -> (Column<F,R>)x)
            .findFirst();
    }

    @SuppressWarnings("unchecked")
    @SafeVarargs
    private final int performInsert(SqlExecutor sqlExecutor, R... rows) {
        if (rows.length == 0) {
            return 0;
        }
        String sql = insertSql(rows);
        Object[] args = columnMapping.insertArgs(rows);
        return database.execute(sql, () -> sqlExecutor.update(sql, args));
    }

    @SuppressWarnings("unchecked")
    @SafeVarargs
    private final int performInsert(Transaction transaction, R... rows) {
        if (rows.length == 0) {
            return 0;
        }
        String sql = insertSql(rows);
        Object[] args = columnMapping.insertArgs(rows);
        return database.execute(sql, () -> transaction.update(sql, args));
    }

    private String insertSql(R[] rows) {
        String sql = String.format("insert into %s (%s) values %s",
            qualifiedName(),
            columns().flatMap(Column::insertColumnSql).collect(joining(", ")),
            IntStream.range(0, rows.length)
                .mapToObj(i -> "(" + columns().flatMap(Column::insertArgsSql).collect(joining(", ")) + ")")
                .collect(joining(", ")));
        LOG.debug(sql);
        return sql;
    }

    private String updateSql() {
        Alias<R> alias = Alias.of(this);
        String sql = String.format("update %s set %s where %s",
            qualifiedName(),
            columns().flatMap(Column::updateSql).collect(joining(", ")),
            columns().flatMap(c -> c.idSql(alias)).collect(joining(" and ")));
        LOG.debug(sql);
        return sql;
    }

    private String deleteSql() {
        Alias<R> alias = Alias.of(this);
        String sql = String.format("delete from %s where %s",
            qualifiedName(),
            columns().flatMap(c -> c.idSql(alias)).collect(joining(" and ")));
        LOG.debug(sql);
        return sql;
    }

    public static final class Builder<R, B> extends ColumnMapping.Builder<R,B,Builder<R,B>> {
        private final Database database;
        private final TypeToken<R> rowType;
        private String catalog;
        private String schema;
        private String tableName;
        private final List<ForeignKeyReference.Builder<R,?>> foreignKeys = new ArrayList<>();

        public Builder(Database database, TypeToken<R> rowType, TypeToken<B> builderType, Function<B,R> buildRow) {
            super(database, rowType, builderType, buildRow);
            this.database = database;
            this.rowType = rowType;

            this.catalog = tableAnnotations(rowType)
                .map(javax.persistence.Table::catalog)
                .flatMap(StreamUtil::ofBlankable)
                .findFirst()
                .orElse(database.defaultCatalog());
            this.schema = tableAnnotations(rowType)
                .map(javax.persistence.Table::schema)
                .flatMap(StreamUtil::ofBlankable)
                .findFirst()
                .orElse(database.defaultSchema());
            this.tableName = tableAnnotations(rowType)
                .map(javax.persistence.Table::name)
                .flatMap(StreamUtil::ofBlankable)
                .findFirst()
                .orElseGet(() -> database.namingStrategy().tableName(rowType.getRawType().getSimpleName()));
        }

        public Table<R> build() {
            finish();
            return new Table<>(this);
        }

        public Builder<R,B> catalog(String val) {
            catalog = val;
            return this;
        }

        public Builder<R,B> schema(String val) {
            schema = val;
            return this;
        }

        public Builder<R,B> tableName(String val) {
            tableName = val;
            return this;
        }

        public <P> Builder<R,B> foreignKey(Class<P> parentClass, Function<ForeignKeyReference.Builder<R,P>,ForeignKeyReference.Builder<R,P>> init) {
            ForeignKeyReference.Builder<R,P> fkBuilder = ForeignKeyReference.<R,P>newBuilder()
                .parentClass(parentClass);
            foreignKeys.add(init.apply(fkBuilder));
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public <BB> Builder<R,BB> builder(Function1<BB,R> buildRow) {
            MethodInfo<BB,R> buildMethod = MethodInfo.of(buildRow);
            return new Builder<>(database, rowType, buildMethod.referringType(), buildRow)
                .catalog(catalog)
                .schema(schema)
                .tableName(tableName);
        }

        @Override
        protected void finish() {
            super.finish();

            ClassUtil.superclasses(rowType.getRawType())
                .map(cls -> ClassUtil.annotation(cls, ForeignKeys.class))
                .flatMap(StreamUtil::of)
                .flatMap(fk -> Arrays.stream(fk.value()))
                .forEach(this::buildForeignKey);
        }

        private void buildForeignKey(ForeignKey foreignKey) {
            this.foreignKey(foreignKey.parent(), x -> buildForeignKeyReferences(foreignKey.references(), x.name(foreignKey.name())));
        }

        private <T> ForeignKeyReference.Builder<R,T> buildForeignKeyReferences(Reference[] references, ForeignKeyReference.Builder<R,T> fkBuilder) {
            Arrays.stream(references)
                .map(ref -> {
                    Column<Object,R> childColumn = findColumn(ref.property())
                        .orElseThrow(IllegalArgumentException::new);
                    return Tuple.of(childColumn, ref.parentProperty());
                })
                .forEach(ref -> fkBuilder.column(ref.item1()).references(ref.item2()));
            return fkBuilder;
        }

        private static <R> Stream<javax.persistence.Table> tableAnnotations(TypeToken<R> rowType) {
            return ClassUtil.superclasses(rowType.getRawType())
                .map(cls -> ClassUtil.annotation(cls, javax.persistence.Table.class))
                .flatMap(StreamUtil::of);
        }
    }
}
