/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.siesta;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.core.reflect.FieldInfo;
import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.siesta.catalog.Column;
import com.cadenzauk.siesta.catalog.Table;
import com.cadenzauk.siesta.dialect.AnsiDialect;
import com.cadenzauk.siesta.name.UppercaseUnderscores;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class Database {
    private final Map<Class<?>,Table<?>> metadataCache = new ConcurrentHashMap<>();
    private final String defaultSchema;
    private final NamingStrategy namingStrategy;
    private final Dialect dialect;

    private Database(Builder builder) {
        defaultSchema = builder.defaultSchema;
        namingStrategy = builder.namingStrategy;
        dialect = builder.dialect;
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

    @SuppressWarnings("unchecked")
    public <R> Table<R> table(Class<R> rowClass) {
        return table(rowClass, Function.identity());
    }

    @SuppressWarnings("unchecked")
    public <R, B> Table<R> table(Class<R> rowClass, Function<Table.Builder<R,R>,Table.Builder<R,B>> init) {
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

    private <R, T> Optional<String> nameFromMethodAnnotation(FieldInfo<R,T> fieldInfo) {
        return MethodInfo.ofGetter(fieldInfo)
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

    @SuppressWarnings("unchecked")
    public <R> void insert(JdbcTemplate jdbcTemplate, R row) {
        Class<R> rowClass = (Class<R>) row.getClass();
        table(rowClass).insert(jdbcTemplate, row);
    }

    public <R> Select1<R> from(Class<R> rowClass) {
        return Select.from(this, table(rowClass));
    }

    public <R> Select1<R> from(Alias<R> alias) {
        return Select.from(this, alias);
    }

    public <R> Select1<R> from(Class<R> rowClass, String alias) {
        return Select.from(this, table(rowClass).as(alias));
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private String defaultSchema;
        private NamingStrategy namingStrategy = new UppercaseUnderscores();
        private Dialect dialect = new AnsiDialect();

        private Builder() {
        }

        public Builder defaultSchema(String val) {
            defaultSchema = val;
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

        public Database build() {
            return new Database(this);
        }
    }
}
