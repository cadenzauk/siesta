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
import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.DynamicRowMapper;
import com.cadenzauk.siesta.SqlExecutor;
import com.cadenzauk.siesta.Transaction;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class Table<R> {
    private static final Logger LOG = LoggerFactory.getLogger(Table.class);
    private final Database database;
    private final TypeToken<R> rowType;
    private final String catalog;
    private final String schema;
    private final String tableName;
    private final ColumnMapping<R,?> columnMapping;

    private <B> Table(Builder<R,B> builder) {
        database = builder.database;
        rowType = builder.rowType;
        catalog = builder.catalog;
        schema = builder.schema;
        tableName = builder.tableName;
        columnMapping = new ColumnMapping<>(builder);
    }

    public TypeToken<R> rowType() {
        return rowType;
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

    public Stream<Column<?,R>> columns() {
        return columnMapping.columns().map(Function.identity());
    }

    public Stream<Object> toDatabase(Optional<R> r) {
        return columnMapping.toDatabase(r);
    }

    public RowMapper<R> rowMapper(Alias<?> alias) {
        return columnMapping.rowMapper(alias);
    }

    public DynamicRowMapper<R> dynamicRowMapper(Alias<?> alias) {
        return columnMapping.dynamicRowMapper(alias);
    }

    public String qualifiedName() {
        return database.dialect().qualifiedName(catalog, schema, tableName());
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

    public <T> Column<T,R> column(MethodInfo<R,T> methodInfo) {
        String propertyName = methodInfo.propertyName();
        return findColumn(methodInfo.effectiveType(), propertyName)
            .orElseThrow(() -> new IllegalArgumentException("No column for property " + propertyName + " in " + qualifiedName()));
    }

    @SuppressWarnings("unchecked")
    @SafeVarargs
    private final int performInsert(SqlExecutor sqlExecutor, R... rows) {
        if (rows.length == 0) {
            return 0;
        }
        String sql = sql(rows);
        Object[] args = columnMapping.args(rows);
        return sqlExecutor.update(sql, args);
    }

    @SuppressWarnings("unchecked")
    @SafeVarargs
    private final int performInsert(Transaction transaction, R... rows) {
        if (rows.length == 0) {
            return 0;
        }
        String sql = sql(rows);
        Object[] args = columnMapping.args(rows);
        return transaction.update(sql, args);
    }

    private String sql(R[] rows) {
        String sql = String.format("insert into %s (%s) values %s",
            qualifiedName(),
            columns().map(Column::sql).collect(joining(", ")),
            IntStream.range(0, rows.length)
                .mapToObj(i -> "(" + IntStream.range(0, columns().mapToInt(Column::count).sum()).mapToObj(j -> "?").collect(joining(", ")) + ")")
                .collect(joining(", ")));
        LOG.debug(sql);
        return sql;
    }

    private <T> Optional<Column<T,R>> findColumn(TypeToken<T> dataType, String propertyName) {
        return columnsOfType(dataType)
            .filter(c -> StringUtils.equals(c.propertyName(), propertyName))
            .findFirst();
    }

    private <T> Stream<Column<T,R>> columnsOfType(TypeToken<T> dataType) {
        return columns().flatMap(c -> c.as(dataType));
    }

    public static final class Builder<R, B> extends ColumnMapping.Builder<R, B, Builder<R, B>> {
        private final Database database;
        private final TypeToken<R> rowType;
        private String catalog;
        private String schema;
        private String tableName;

        public Builder(Database database, TypeToken<R> rowType, TypeToken<B> builderType, Function<B,R> buildRow) {
            super(database, rowType, builderType, buildRow);
            this.database = database;
            this.rowType = rowType;

            Optional<javax.persistence.Table> tableAnnotation = ClassUtil.superclasses(rowType.getRawType())
                .map(cls -> ClassUtil.annotation(cls, javax.persistence.Table.class))
                .flatMap(StreamUtil::of)
                .findFirst();
            this.catalog = tableAnnotation
                .map(javax.persistence.Table::catalog)
                .flatMap(OptionalUtil::ofBlankable)
                .orElse(database.defaultCatalog());
            this.schema = tableAnnotation
                .map(javax.persistence.Table::schema)
                .flatMap(OptionalUtil::ofBlankable)
                .orElse(database.defaultSchema());
            this.tableName = tableAnnotation
                .map(javax.persistence.Table::name)
                .flatMap(OptionalUtil::ofBlankable)
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

        public <BB> Builder<R,BB> builder(Function1<BB,R> buildRow) {
            MethodInfo<BB,R> buildMethod = MethodInfo.of(buildRow);
            return new Builder<>(database, rowType, buildMethod.declaringType(), buildRow)
                .catalog(catalog)
                .schema(schema)
                .tableName(tableName);
        }

    }
}
