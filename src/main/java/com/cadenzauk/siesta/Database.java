/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import com.cadenzauk.siesta.catalog.Table;
import com.cadenzauk.siesta.name.UppercaseUnderscores;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class Database {
    private final Map<Class<?>, Table<?>> metadataCache = new ConcurrentHashMap<>();
    private final String defaultSchema;
    private final NamingStrategy namingStrategy;

    private Database(Builder builder) {
        defaultSchema = builder.defaultSchema;
        namingStrategy = builder.namingStrategy;
    }

    public String defaultSchema() {
        return defaultSchema;
    }

    public NamingStrategy namingStrategy() {
        return namingStrategy;
    }

    @SuppressWarnings("unchecked")
    public <R> Table<R> table(Class<R> rowClass) {
        return table(rowClass, Function.identity());
    }

    @SuppressWarnings("unchecked")
    public <R> void insert(JdbcTemplate jdbcTemplate, R row) {
        Class<R> rowClass = (Class<R>)row.getClass();
        table(rowClass).insert(jdbcTemplate, row);
    }

    public <R> Select1<R,R> from(Class<R> rowClass) {
        return Select.from(table(rowClass));
    }

    public <R> Select1<R,R> from(Alias<R> alias) {
        return Select.from(alias);
    }

    public <R> Select1<R,R> from(Class<R> rowClass, String alias) {
        return Select.from(table(rowClass).as(alias));
    }

    @SuppressWarnings("unchecked")
    public <R,B> Table<R> table(Class<R> rowClass, Function<Table.Builder<R,R>,Table.Builder<R,B>> init) {
        return (Table<R>) metadataCache.computeIfAbsent(rowClass, k -> {
            Table.Builder<R, R> builder = new Table.Builder<>(this, rowClass, rowClass, Function.identity());
            return init.apply(builder).build();
        });
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private String defaultSchema;
        private NamingStrategy namingStrategy = new UppercaseUnderscores();

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

        public Database build() {
            return new Database(this);
        }
    }
}
