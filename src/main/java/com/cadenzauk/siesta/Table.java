/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import com.google.common.collect.ImmutableList;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class Table<R> {
    private final Class<R> rowClass;
    private final String schema;
    private final String tableName;
    private final Impl<?> impl;

    private <B> Table(Builder<R, B> builder) {
        rowClass = builder.rowClass;
        schema = builder.schema;
        tableName = builder.tableName;
        impl = new Impl<>(builder.newBuilder, builder.buildRow, builder.columns);
    }

    public Class<R> rowClass() {
        return rowClass;
    }

    public String schema() {
        return schema;
    }

    public String tableName() {
        return tableName;
    }

    public Stream<TableColumn<?, R>> columns() {
        return impl.columns.stream().map(Function.identity());
    }

    public RowMapper<R> rowMapper(String s) {
        return impl.rowMapper();
    }

    public String qualifiedName() {
        return schema + "." + tableName();
    }

    public Alias<R> as(String alias) {
        return new Alias<>(this, alias);
    }

    public void insert(JdbcTemplate jdbcTemplate, R row) {
        impl.insert(jdbcTemplate, row);
    }

    public static <R, B> Builder<R, B> aTable(String schema, String tableName, Supplier<B> newBuilder, Function<B, R> buildRow, Class<R> rowClass) {
        return new Builder<>(rowClass, schema, tableName, newBuilder, buildRow);
    }

    public static <R> Builder<R, R> aTable(String schema, String tableName, Supplier<R> newRow, Class<R> rowClass) {
        return new Builder<>(rowClass, schema, tableName, newRow, Function.identity());
    }

    private class Impl<B> {
        private final Supplier<B> newBuilder;
        private final Function<B, R> buildRow;
        private final List<RowBuilderColumn<?, R, B>> columns;

        public Impl(Supplier<B> newBuilder, Function<B, R> buildRow, List<RowBuilderColumn<?, R, B>> columns) {
            this.newBuilder = newBuilder;
            this.buildRow = buildRow;
            this.columns = ImmutableList.copyOf(columns);
        }

        public void insert(JdbcTemplate jdbcTemplate, R row) {
            String sql = String.format("insert into %s.%s (%s) values (%s)",
                schema,
                tableName,
                columns.stream().map(TableColumn::name).collect(joining(", ")),
                columns.stream().map(c -> "?").collect(joining(", ")));

            Object[] args = columns
                .stream()
                .map(c -> c.getter().apply(row).orElse(null))
                .toArray();

            jdbcTemplate.update(sql, args);
        }

        public RowMapper<R> rowMapper() {
            return rowMapper(Optional.empty());
        }

        public RowMapper<R> rowMapper(Optional<String> prefix) {
            return (rs, i) -> {
                B builder = newBuilder.get();
                columns.forEach(c -> c.extract(rs, builder, prefix));
                return buildRow.apply(builder);
            };
        }
    }

    public static final class Builder<R, B> {
        private final Class<R> rowClass;
        private final String schema;
        private final String tableName;
        private final Supplier<B> newBuilder;
        private final Function<B, R> buildRow;
        private final List<RowBuilderColumn<?, R, B>> columns = new ArrayList<>();

        private Builder(Class<R> rowClass, String schema, String tableName, Supplier<B> newBuilder, Function<B, R> buildRow) {
            this.rowClass = rowClass;
            this.schema = schema;
            this.tableName = tableName;
            this.newBuilder = newBuilder;
            this.buildRow = buildRow;
        }

        public Table<R> build() {
            return new Table<>(this);
        }

        public Table<R> buildReflectively() {
            Class<B> builderClass = (Class<B>) newBuilder.get().getClass();
            Arrays.stream(rowClass.getDeclaredFields())
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .forEach(f -> columns.add(RowBuilderColumn.fromField(rowClass, builderClass, f)));
            return new Table<>(this);
        }

        public <T> Builder<R, B> mandatory(Column<T, R> column, Function<R, T> getter, BiConsumer<B, T> setter) {
            return mandatory(column, getter, setter, Optional.empty());
        }

        public <T> Builder<R, B> mandatory(Column<T, R> column, Function<R, T> getter, BiConsumer<B, T> setter, Consumer<RowBuilderColumn.Builder<T, R, B>> init) {
            return mandatory(column, getter, setter, Optional.of(init));
        }

        public <T> Builder<R, B> mandatory(Column<T, R> column, Function<R, T> getter, BiConsumer<B, T> setter, Optional<Consumer<RowBuilderColumn.Builder<T, R, B>>> init) {
            RowBuilderColumn.Builder<T, R, B> columnBuilder = RowBuilderColumn.mandatory(column, getter, setter);
            init.ifPresent(x -> x.accept(columnBuilder));
            columns.add(columnBuilder.build());
            return this;
        }

        public <T> Builder<R, B> optional(Column<T, R> column, Function<R, Optional<T>> getter, BiConsumer<B, Optional<T>> setter) {
            return optional(column, getter, setter, Optional.empty());
        }

        public <T> Builder<R, B> optional(Column<T, R> column, Function<R, Optional<T>> getter, BiConsumer<B, Optional<T>> setter, Consumer<RowBuilderColumn.Builder<T, R, B>> init) {
            return optional(column, getter, setter, Optional.ofNullable(init));
        }

        private <T> Builder<R, B> optional(Column<T, R> column, Function<R, Optional<T>> getter, BiConsumer<B, Optional<T>> setter, Optional<Consumer<RowBuilderColumn.Builder<T, R, B>>> init) {
            RowBuilderColumn.Builder<T, R, B> columnBuilder = RowBuilderColumn.optional(column, getter, setter);
            init.ifPresent(x -> x.accept(columnBuilder));
            columns.add(columnBuilder.build());
            return this;
        }
    }
}
