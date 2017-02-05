/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.stream.Collectors.joining;

public class Table<R, B> {
    private final Class<R> rowClass;
    private final String schema;
    private final String tableName;
    private final Supplier<B> newBuilder;
    private final Function<B,R> buildRow;
    private final List<TableColumn<?,R,B>> columns;

    private Table(Builder<R,B> builder) {
        rowClass = builder.rowClass;
        schema = builder.schema;
        tableName = builder.tableName;
        newBuilder = builder.newBuilder;
        buildRow = builder.buildRow;
        columns = builder.columns;
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

    public Supplier<B> newBuilder() {
        return newBuilder;
    }

    public Function<B,R> buildRow() {
        return buildRow;
    }

    public List<TableColumn<?,R,B>> columns() {
        return columns;
    }

    public void insert(JdbcTemplate jdbcTemplate, R row) {
        String sql = String.format("insert into %s.%s (%s) values (%s)",
            schema,
            tableName,
            columns.stream().map(c -> c.column().name()).collect(joining(", ")),
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

    public RowMapper<R> rowMapper(String prefix) {
        return rowMapper(Optional.ofNullable(prefix));
    }

    public static <R, B> Builder<R,B> aTable(String schema, String tableName, Supplier<B> newBuilder, Function<B,R> buildRow, Class<R> rowClass) {
        return new Builder<>(rowClass, schema, tableName, newBuilder, buildRow);
    }

    public static <R> Builder<R,R> aTable(String schema, String tableName, Supplier<R> newRow, Class<R> rowClass) {
        return new Builder<>(rowClass, schema, tableName, newRow, Function.identity());
    }

    public String qualifiedName() {
        return schema + "." + tableName();
    }

    public Alias<R> as(String alias) {
        return new Alias<>(this, alias);
    }

    public static final class Builder<R, B> {
        private final Class<R> rowClass;
        private final String schema;
        private final String tableName;
        private final Supplier<B> newBuilder;
        private final Function<B,R> buildRow;
        private final List<TableColumn<?,R,B>> columns = new ArrayList<>();

        private Builder(Class<R> rowClass, String schema, String tableName, Supplier<B> newBuilder, Function<B,R> buildRow) {
            this.rowClass = rowClass;
            this.schema = schema;
            this.tableName = tableName;
            this.newBuilder = newBuilder;
            this.buildRow = buildRow;
        }

        public Table<R,B> build() {
            return new Table(this);
        }

        public <T> Builder<R,B> mandatory(Column<T,R> column, Function<R,T> getter, BiConsumer<B,T> setter) {
            return mandatory(column, getter, setter, Optional.empty());
        }

        public <T> Builder<R,B> mandatory(Column<T,R> column, Function<R,T> getter, BiConsumer<B,T> setter, Consumer<TableColumn.Builder<T,R,B>> init) {
            return mandatory(column, getter, setter, Optional.of(init));
        }

        public <T> Builder<R,B> mandatory(Column<T,R> column, Function<R,T> getter, BiConsumer<B,T> setter, Optional<Consumer<TableColumn.Builder<T,R,B>>> init) {
            TableColumn.Builder<T,R,B> columnBuilder = TableColumn.mandatory(column, getter, setter);
            init.ifPresent(x -> x.accept(columnBuilder));
            columns.add(columnBuilder.build());
            return this;
        }

        public <T> Builder<R,B> optional(Column<T,R> column, Function<R,Optional<T>> getter, BiConsumer<B,Optional<T>> setter) {
            return optional(column, getter, setter, Optional.empty());
        }

        public <T> Builder<R,B> optional(Column<T,R> column, Function<R,Optional<T>> getter, BiConsumer<B,Optional<T>> setter, Consumer<TableColumn.Builder<T,R,B>> init) {
            return optional(column, getter, setter, Optional.ofNullable(init));
        }

        private <T> Builder<R,B> optional(Column<T,R> column, Function<R,Optional<T>> getter, BiConsumer<B,Optional<T>> setter, Optional<Consumer<TableColumn.Builder<T,R,B>>> init) {
            TableColumn.Builder<T,R,B> columnBuilder = TableColumn.optional(column, getter, setter);
            init.ifPresent(x -> x.accept(columnBuilder));
            columns.add(columnBuilder.build());
            return this;
        }
    }
}
