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
import com.cadenzauk.core.lang.UncheckedAutoCloseable;
import com.cadenzauk.core.reflect.FieldInfo;
import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.core.sql.RuntimeSqlException;
import com.cadenzauk.core.sql.exception.SqlExceptionConstructor;
import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.catalog.Column;
import com.cadenzauk.siesta.catalog.Table;
import com.cadenzauk.siesta.dialect.AnsiDialect;
import com.cadenzauk.siesta.dialect.function.FunctionName;
import com.cadenzauk.siesta.dialect.function.FunctionSpec;
import com.cadenzauk.siesta.grammar.dml.Delete;
import com.cadenzauk.siesta.grammar.dml.ExpectingWhere;
import com.cadenzauk.siesta.grammar.dml.InSetExpectingWhere;
import com.cadenzauk.siesta.grammar.dml.Update;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.grammar.select.CommonTableExpression;
import com.cadenzauk.siesta.grammar.select.CommonTableExpressionBuilder;
import com.cadenzauk.siesta.grammar.select.ExpectingJoin1;
import com.cadenzauk.siesta.grammar.select.InProjectionExpectingComma1;
import com.cadenzauk.siesta.grammar.select.Select;
import com.cadenzauk.siesta.name.UppercaseUnderscores;
import com.cadenzauk.siesta.type.DbType;
import com.cadenzauk.siesta.type.DbTypeAdapter;
import com.cadenzauk.siesta.type.DbTypeId;
import com.cadenzauk.siesta.type.EnumByName;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Database {
    private static final Logger LOG = LoggerFactory.getLogger(Database.class);

    private final Map<TypeToken<?>,Table<?>> metadataCache = new ConcurrentHashMap<>();
    private final DataTypeRegistry dataTypeRegistry;
    private final String defaultCatalog;
    private final String defaultSchema;
    private final NamingStrategy namingStrategy;
    private final Dialect dialect;
    private final Optional<SqlExecutor> defaultSqlExecutor;
    private final ZoneId databaseTimeZone;

    private Database(Builder builder) {
        dataTypeRegistry = new DataTypeRegistry();
        defaultCatalog = builder.defaultCatalog;
        defaultSchema = builder.defaultSchema;
        namingStrategy = builder.namingStrategy;
        dialect = builder.dialect();
        defaultSqlExecutor = builder.defaultSqlExecutor;
        databaseTimeZone = builder.databaseTimeZone;

        builder.customizations.forEach(c -> c.accept(dialect));
        builder.dataTypes.forEach(d -> d.accept(dataTypeRegistry));
        builder.tables.forEach((k, v) -> v.apply(this));
    }

    public String defaultCatalog() {
        return defaultCatalog;
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

    public Sequence<Integer> sequence(String name) {
        return sequence(Integer.class, defaultCatalog, defaultSchema, name);
    }

    public <T extends Number> Sequence<T> sequence(Class<T> valueClass, String name) {
        return sequence(valueClass, defaultCatalog, defaultSchema, name);
    }

    @SuppressWarnings("WeakerAccess")
    public <T extends Number> Sequence<T> sequence(Class<T> valueClass, String catalog, String schema, String name) {
        return Sequence.<T>newBuilder()
            .database(this)
            .catalog(catalog)
            .schema(schema)
            .sequenceName(name)
            .dataType(getDataTypeOf(valueClass))
            .build();
    }

    public UncheckedAutoCloseable withLockTimeout(long time, TimeUnit unit) {
        return withLockTimeout(getDefaultSqlExecutor(), time, unit);
    }

    @SuppressWarnings("WeakerAccess")
    public UncheckedAutoCloseable withLockTimeout(SqlExecutor executor, long time, TimeUnit unit) {
        String sql = dialect.setLockTimeout(time, unit);
        LOG.debug(sql);
        execute(sql, () -> executor.update(sql));
        return () -> {
            String resetSql = dialect.resetLockTimeout();
            LOG.debug(resetSql);
            execute(resetSql, () -> executor.update(resetSql));
        };
    }

    public UncheckedAutoCloseable withLockTimeout(Transaction transaction, long time, TimeUnit unit) {
        String sql = dialect.setLockTimeout(time, unit);
        LOG.debug(sql);
        execute(sql, () -> transaction.execute(sql, new Object[0]));
        return () -> {
            String resetSql = dialect.resetLockTimeout();
            LOG.debug(resetSql);
            execute(sql, () -> transaction.execute(resetSql, new Object[0]));
        };
    }

    @SuppressWarnings("unchecked")
    public <R> Table<R> table(Class<R> rowClass) {
        return table(TypeToken.of(rowClass), Function.identity());
    }

    public <R> Table<R> table(TypeToken<R> rowType) {
        return table(rowType, Function.identity());
    }

    public ZoneId databaseTimeZone() {
        return databaseTimeZone;
    }

    public <T> T execute(String sql, Supplier<T> statement) {
        try {
            return statement.get();
        } catch (RuntimeSqlException exception) {
            translateException(sql, exception);
            throw exception;
        }
    }

    public <T> T translateException(String sql, Throwable throwable) {
        OptionalUtil.as(RuntimeSqlException.class, throwable)
            .ifPresent(e -> {
                throw dialect.exceptionTranslator().translate(sql, e.getCause());
            });
        throw new RuntimeException(String.format("Error while executing '%s'.", sql), throwable);
    }

    @SuppressWarnings("unchecked")
    private <R, B> Table<R> table(TypeToken<R> rowType, Function<Table.Builder<R,R>,Table.Builder<R,B>> init) {
        return (Table<R>) metadataCache.computeIfAbsent(rowType, k -> {
            Table.Builder<R,R> builder = new Table.Builder<>(this, rowType, rowType, Function.identity());
            return init.apply(builder).build();
        });
    }

    public <R, T> String columnNameFor(MethodInfo<R,T> getterMethod) {
        return nameFromMethodAnnotation(getterMethod)
            .orElseGet(() -> nameFromFieldAnnotation(getterMethod)
                .orElseGet(() -> namingStrategy().columnName(getterMethod.propertyName())));
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

    public <T> DataType<T> getDataTypeOf(TypeToken<T> valueClass) {
        return dataTypeRegistry.dataTypeOf(valueClass)
            .orElseThrow(() -> new RuntimeException("Unable to determine the type of " + valueClass));
    }

    @SuppressWarnings("WeakerAccess")
    public <T, R> Optional<DataType<T>> dataTypeOf(MethodInfo<R,T> getterInfo) {
        return dataTypeRegistry.dataTypeOf(getterInfo.effectiveClass());
    }

    public <T, R> Optional<DataType<T>> dataTypeOf(FieldInfo<R,T> fieldInfo) {
        return dataTypeRegistry.dataTypeOf(fieldInfo.effectiveClass());
    }

    private <R, T> Optional<String> nameFromMethodAnnotation(FieldInfo<R,T> fieldInfo) {
        return MethodInfo.findGetterForField(fieldInfo)
            .flatMap(this::nameFromMethodAnnotation);
    }

    private <R, T> Optional<String> nameFromMethodAnnotation(MethodInfo<R,T> getterMethod) {
        return getterMethod.annotation(javax.persistence.Column.class)
            .map(javax.persistence.Column::name)
            .filter(StringUtils::isNotBlank);
    }

    private <R, T> Optional<String> nameFromFieldAnnotation(MethodInfo<R,T> getterMethod) {
        return FieldInfo.ofGetter(getterMethod)
            .flatMap(this::nameFromFieldAnnotation);
    }

    private <R, T> Optional<String> nameFromFieldAnnotation(FieldInfo<R,T> f) {
        return f.annotation(javax.persistence.Column.class)
            .map(javax.persistence.Column::name)
            .filter(StringUtils::isNotBlank);
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
        return table(methodInfo.referringClass()).column(methodInfo);
    }

    public SqlExecutor getDefaultSqlExecutor() {
        return defaultSqlExecutor.orElseThrow(() -> new IllegalStateException("Default SQL executor has not been set."));
    }

    public Transaction beginTransaction() {
        return getDefaultSqlExecutor().beginTransaction();
    }

    public CommonTableExpressionBuilder with(String name) {
        return new CommonTableExpressionBuilder(this, name);
    }

    public <T> InProjectionExpectingComma1<T> select(TypedExpression<T> what) {
        return from(Dual.class).select(what);
    }

    public <T> InProjectionExpectingComma1<T> select(TypedExpression<T> what, String label) {
        return from(Dual.class).select(what, label);
    }

    public <R> ExpectingJoin1<R> from(Class<R> rowClass) {
        return Select.from(this, table(rowClass));
    }

    public <R> ExpectingJoin1<R> from(Alias<R> alias) {
        return Select.from(this, alias);
    }

    public <R> ExpectingJoin1<R> from(CommonTableExpression<R> cte, String aliasName) {
        return Select.from(this, cte, aliasName);
    }

    public <R> ExpectingJoin1<R> from(CommonTableExpression<R> cte) {
        return Select.from(this, cte);
    }

    public <R> ExpectingJoin1<R> from(Class<R> rowClass, String alias) {
        return Select.from(this, table(rowClass).as(alias));
    }

    @SuppressWarnings("unchecked")
    public <R> int insert(R... rows) {
        return insert(getDefaultSqlExecutor(), rows);
    }

    @SuppressWarnings("unchecked")
    public <R> int insert(SqlExecutor sqlExecutor, R... rows) {
        if (rows.length == 0) {
            return 0;
        }
        Class<R> rowClass = (Class<R>) rows[0].getClass();
        return table(rowClass).insert(sqlExecutor, rows);
    }

    @SuppressWarnings("unchecked")
    public <R> int insert(Transaction transaction, R... rows) {
        if (rows.length == 0) {
            return 0;
        }
        Class<R> rowClass = (Class<R>) rows[0].getClass();
        return table(rowClass).insert(transaction, rows);
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

    @SuppressWarnings("UnusedReturnValue")
    public <R> int update(R row) {
        return update(getDefaultSqlExecutor(), row);
    }

    @SuppressWarnings("unchecked")
    public <R> int update(SqlExecutor sqlExecutor, R row) {
        Class<R> rowClass = (Class<R>) row.getClass();
        return table(rowClass).update(sqlExecutor, row);
    }

    @SuppressWarnings({"unchecked", "UnusedReturnValue"})
    public <R> int update(Transaction transaction, R row) {
        Class<R> rowClass = (Class<R>) row.getClass();
        return table(rowClass).update(transaction, row);
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

    @SuppressWarnings("UnusedReturnValue")
    public <R> int delete(R row) {
        return delete(getDefaultSqlExecutor(), row);
    }

    @SuppressWarnings("unchecked")
    public <R> int delete(SqlExecutor sqlExecutor, R row) {
        Class<R> rowClass = (Class<R>) row.getClass();
        return table(rowClass).delete(sqlExecutor, row);
    }

    @SuppressWarnings({"unchecked", "UnusedReturnValue"})
    public <R> int delete(Transaction transaction, R row) {
        Class<R> rowClass = (Class<R>) row.getClass();
        return table(rowClass).delete(transaction, row);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private String defaultCatalog = "";
        private String defaultSchema = "";
        private NamingStrategy namingStrategy = new UppercaseUnderscores();
        private Optional<Dialect> dialect = Optional.empty();
        private Optional<SqlExecutor> defaultSqlExecutor = Optional.empty();
        private ZoneId databaseTimeZone = ZoneId.systemDefault();
        private final List<Consumer<Dialect>> customizations = new ArrayList<>();
        private final List<Consumer<DataTypeRegistry>> dataTypes = new ArrayList<>();
        private final Map<TypeToken<?>,TableInitializer<?,?>> tables = new HashMap<>();

        private Builder() {
        }

        public Builder defaultCatalog(String val) {
            defaultCatalog = val;
            return this;
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
            dialect = Optional.of(val);
            return this;
        }

        public Builder databaseTimeZone(ZoneId val) {
            databaseTimeZone = val;
            return this;
        }

        public <T> Builder function(FunctionName functionName, FunctionSpec functionSpec) {
            customizations.add(dialect -> dialect.registerFunction(functionName, functionSpec));
            return this;
        }

        @SuppressWarnings("WeakerAccess")
        public <T> Builder type(Class<T> javaClass, DbTypeId<T> dbTypeId, DbType<T> dbType) {
            customizations.add(dialect -> dialect.registerType(dbTypeId, dbType));
            dataTypes.add(reg -> reg.register(new DataType<>(javaClass, dbTypeId)));
            return this;
        }

        public <T, D> Builder adapter(Class<T> javaClass, DbTypeId<D> dbTypeId, Function<T,D> toDb, Function<D,T> fromDb) {
            return type(javaClass, DbTypeId.of(javaClass), new DbTypeAdapter<>(dbTypeId, toDb, fromDb));
        }

        public <T extends Enum<T>> Builder enumByName(Class<T> javaClass) {
            return type(javaClass, EnumByName.id(javaClass), new EnumByName<>(javaClass));
        }

        public <R, B> Builder table(Class<R> rowClass, Consumer<Table.Builder<R,?>> init) {
            return table(TypeToken.of(rowClass), init);
        }

        public Builder exception(String sqlState, SqlExceptionConstructor constructor) {
            customizations.add(dialect -> dialect.exception(sqlState, constructor));
            return this;
        }

        public Builder exception(int errorCode, SqlExceptionConstructor constructor) {
            customizations.add(dialect -> dialect.exception(errorCode, constructor));
            return this;
        }

        public Builder exception(String sqlState, int errorCode, SqlExceptionConstructor constructor) {
            customizations.add(dialect -> dialect.exception(sqlState, errorCode, constructor));
            return this;
        }

        @SuppressWarnings({"unchecked", "WeakerAccess"})
        public <R> Builder table(TypeToken<R> rowType, Consumer<Table.Builder<R,?>> init) {
            TableInitializer<?,?> existing = tables.get(rowType);
            TableInitializer<?,?> tableInitializer =
                existing == null
                    ? TableInitializer.of(rowType, init)
                    : ((TableInitializer<R,?>) existing).concat(init);
            tables.put(rowType, tableInitializer);
            return this;
        }

        private Dialect dialect() {
            return dialect.orElseGet(this::detectDialect);
        }

        private Dialect detectDialect() {
            return defaultSqlExecutor
                .map(SqlExecutor::dialect)
                .orElseGet(AnsiDialect::new);
        }

        public Database build() {
            return new Database(this);
        }
    }

    private static class TableInitializer<R, B> {
        private final TypeToken<R> rowType;
        private final Function<Table.Builder<R,R>,Table.Builder<R,B>> initializer;

        private TableInitializer(TypeToken<R> rowType, Function<Table.Builder<R,R>,Table.Builder<R,B>> initializer) {
            this.rowType = rowType;
            this.initializer = initializer;
        }

        @SuppressWarnings("UnusedReturnValue")
        private Table<R> apply(Database database) {
            return database.table(rowType, initializer);
        }

        private TableInitializer<R,B> concat(Consumer<Table.Builder<R,?>> next) {
            Function<Table.Builder<R,R>,Table.Builder<R,B>> newInitializer = b -> {
                Table.Builder<R,B> builder = initializer.apply(b);
                next.accept(builder);
                return builder;
            };
            return new TableInitializer<>(rowType, newInitializer);
        }

        private static <R> TableInitializer<R,?> of(TypeToken<R> rowType, Consumer<Table.Builder<R,?>> init) {
            return new TableInitializer<>(rowType, Function.identity()).concat(init);
        }
    }
}
