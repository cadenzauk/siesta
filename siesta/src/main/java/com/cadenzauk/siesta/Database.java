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

package com.cadenzauk.siesta;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.core.reflect.FieldInfo;
import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.siesta.catalog.Column;
import com.cadenzauk.siesta.catalog.Table;
import com.cadenzauk.siesta.dialect.AnsiDialect;
import com.cadenzauk.siesta.grammar.dml.Delete;
import com.cadenzauk.siesta.grammar.dml.ExpectingWhere;
import com.cadenzauk.siesta.grammar.dml.InSetExpectingWhere;
import com.cadenzauk.siesta.grammar.dml.Update;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.grammar.select.ExpectingJoin1;
import com.cadenzauk.siesta.grammar.select.InProjectionExpectingComma1;
import com.cadenzauk.siesta.grammar.select.Select;
import com.cadenzauk.siesta.name.UppercaseUnderscores;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class Database {
    private final Map<Class<?>,Table<?>> metadataCache = new ConcurrentHashMap<>();
    private final DataTypeRegistry dataTypeRegistry = new DataTypeRegistry();
    private final String defaultSchema;
    private final NamingStrategy namingStrategy;
    private final Dialect dialect;
    private final Optional<SqlExecutor> defaultSqlExecutor;
    private final ZoneId databaseTimeZone;

    private Database(Builder builder) {
        defaultSchema = builder.defaultSchema;
        namingStrategy = builder.namingStrategy;
        dialect = builder.dialect;
        defaultSqlExecutor = builder.defaultSqlExecutor;
        databaseTimeZone = builder.databaseTimeZone;
        builder.tables.forEach(t -> t.accept(this));
    }

    public String defaultSchema() {
        return defaultSchema;
    }

    public NamingStrategy namingStrategy() {
        return namingStrategy;
    }

    public Dialect dialect() {
        return dialect;
    }

    public String dateLiteral(LocalDate val) {
        return dialect().dateLiteral(val);
    }

    public String timestampLiteral(LocalDateTime val) {
        return dialect().timestampLiteral(val, databaseTimeZone);
    }

    public String timestampWithTimeZoneLiteral(ZonedDateTime val) {
        return dialect().timestampWithTimeZoneLiteral(val, databaseTimeZone);
    }

    @SuppressWarnings("unchecked")
    public <R> Table<R> table(Class<R> rowClass) {
        return table(rowClass, Function.identity());
    }

    public <T> InProjectionExpectingComma1<T> select(TypedExpression<T> what) {
        return from(Dual.class).select(what);
    }

    public <T> InProjectionExpectingComma1<T> select(TypedExpression<T> what, String label) {
        return from(Dual.class).select(what, label);
    }

    public ZoneId databaseTimeZone() {
        return databaseTimeZone;
    }

    @SuppressWarnings("unchecked")
    private <R, B> Table<R> table(Class<R> rowClass, Function<Table.Builder<R,R>,Table.Builder<R,B>> init) {
        return (Table<R>) metadataCache.computeIfAbsent(rowClass, k -> {
            Table.Builder<R,R> builder = new Table.Builder<>(this, rowClass, rowClass, Function.identity());
            return init.apply(builder).build();
        });
    }

    public <R, T> String columnNameFor(MethodInfo<R,T> getterMethod) {
        return nameFromMethodAnnotation(getterMethod)
            .orElseGet(() -> nameFromFieldAnnotation(getterMethod)
                .orElseGet(() -> namingStrategy().columnName(getterMethod.method().getName())));
    }

    public <R, T> String columnNameFor(FieldInfo<R,T> fieldInfo) {
        return nameFromMethodAnnotation(fieldInfo)
            .orElseGet(() -> nameFromFieldAnnotation(fieldInfo)
                .orElseGet(() -> namingStrategy().columnName(fieldInfo.field().getName())));
    }

    public <T, R> DataType<T> getDataTypeOf(MethodInfo<R,T> getterInfo) {
        return dataTypeOf(getterInfo)
            .orElseThrow(() -> new RuntimeException("Unable to determine the type of " + getterInfo));
    }

    public <T> DataType<T> getDataTypeOf(T value) {
        return dataTypeRegistry.dataTypeOf(value)
            .orElseThrow(() -> new RuntimeException("Unable to determine the type of " + value));
    }

    public <T> DataType<T> getDataTypeOf(Class<T> valueClass) {
        return dataTypeRegistry.dataTypeOf(valueClass)
            .orElseThrow(() -> new RuntimeException("Unable to determine the type of " + valueClass));
    }

    public <T, R> Optional<DataType<T>> dataTypeOf(MethodInfo<R,T> getterInfo) {
        return dataTypeRegistry.dataTypeOf(getterInfo.effectiveType());
    }

    public <T, R> Optional<DataType<T>> dataTypeOf(FieldInfo<R,T> fieldInfo) {
        return dataTypeRegistry.dataTypeOf(fieldInfo.effectiveType());
    }

    private <R, T> Optional<String> nameFromMethodAnnotation(FieldInfo<R,T> fieldInfo) {
        return MethodInfo.findGetterForField(fieldInfo)
            .flatMap(this::nameFromMethodAnnotation);
    }

    private <R, T> Optional<String> nameFromMethodAnnotation(MethodInfo<R,T> getterMethod) {
        return getterMethod.annotation(javax.persistence.Column.class)
            .map(javax.persistence.Column::name);
    }

    private <R, T> Optional<String> nameFromFieldAnnotation(MethodInfo<R,T> getterMethod) {
        return FieldInfo.ofGetter(getterMethod)
            .flatMap(this::nameFromFieldAnnotation);
    }

    private <R, T> Optional<String> nameFromFieldAnnotation(FieldInfo<R,T> f) {
        return f.annotation(javax.persistence.Column.class)
            .map(javax.persistence.Column::name);
    }

    public <T, R> Column<T,R> column(Function1<R,T> getter) {
        MethodInfo<R,T> methodInfo = MethodInfo.of(getter);
        return column(methodInfo);
    }

    public <T, R> Column<T,R> column(FunctionOptional1<R,T> getter) {
        MethodInfo<R,T> methodInfo = MethodInfo.of(getter);
        return column(methodInfo);
    }

    public <T, R> Column<T,R> column(MethodInfo<R,T> methodInfo) {
        return table(methodInfo.declaringClass()).column(methodInfo);
    }

    public SqlExecutor getDefaultSqlExecutor() {
        return defaultSqlExecutor.orElseThrow(() -> new IllegalStateException("Default SQL executor has not been set."));
    }

    @SuppressWarnings("unchecked")
    public <R> void insert(SqlExecutor sqlExecutor, R... rows) {
        if (rows.length == 0) {
            return;
        }
        Class<R> rowClass = (Class<R>) rows[0].getClass();
        table(rowClass).insert(sqlExecutor, rows);
    }

    @SuppressWarnings("unchecked")
    public <R> void insert(R... rows) {
        insert(getDefaultSqlExecutor(), rows);
    }

    public <R> ExpectingJoin1<R> from(Class<R> rowClass) {
        return Select.from(this, table(rowClass));
    }

    public <R> ExpectingJoin1<R> from(Alias<R> alias) {
        return Select.from(this, alias);
    }

    public <R> ExpectingJoin1<R> from(Class<R> rowClass, String alias) {
        return Select.from(this, table(rowClass).as(alias));
    }

    public <U> InSetExpectingWhere<U> update(Alias<U> alias) {
        return Update.update(this, alias);
    }

    public <U> InSetExpectingWhere<U> update(Class<U> rowClass) {
        return Update.update(this, table(rowClass));
    }

    public <U> InSetExpectingWhere<U> update(Class<U> rowClass, String alias) {
        return Update.update(this, table(rowClass).as(alias));
    }

    public <D> ExpectingWhere delete(Alias<D> alias) {
        return Delete.delete(this, alias);
    }

    public <D> ExpectingWhere delete(Class<D> rowClass) {
        return Delete.delete(this, table(rowClass));
    }

    public <D> ExpectingWhere delete(Class<D> rowClass, String alias) {
        return Delete.delete(this, table(rowClass).as(alias));
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private String defaultSchema;
        private NamingStrategy namingStrategy = new UppercaseUnderscores();
        private Dialect dialect = new AnsiDialect();
        private Optional<SqlExecutor> defaultSqlExecutor = Optional.empty();
        private ZoneId databaseTimeZone = ZoneId.systemDefault();
        private final List<Consumer<Database>> tables = new ArrayList<>();

        private Builder() {
        }

        public Builder defaultSchema(String val) {
            defaultSchema = val;
            return this;
        }

        public Builder defaultSqlExecutor(SqlExecutor val) {
            defaultSqlExecutor = Optional.ofNullable(val);
            return this;
        }

        public Builder namingStrategy(NamingStrategy val) {
            namingStrategy = val;
            return this;
        }

        public Builder dialect(Dialect val) {
            dialect = val;
            return this;
        }

        public Builder databaseTimeZone(ZoneId val) {
            databaseTimeZone = val;
            return this;
        }

        public <R, B> Builder table(Class<R> rowClass, Function<Table.Builder<R,R>,Table.Builder<R,B>> init) {
            tables.add(database -> database.table(rowClass, init));
            return this;
        }

        public Database build() {
            return new Database(this);
        }
    }
}
