/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.siesta.catalog;

import com.cadenzauk.core.reflect.*;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.RowMapper;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.Transient;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class Table<R> {
    private final Database database;
    private final Class<R> rowClass;
    private final String schema;
    private final String tableName;
    private final Impl<?> impl;

    private <B> Table(Builder<R, B> builder) {
        database = builder.database;
        rowClass = builder.rowClass;
        schema = builder.schema;
        tableName = builder.tableName;
        impl = new Impl<>(builder.newBuilder, builder.buildRow, builder.columns);
    }

    public Class<R> rowClass() {
        return rowClass;
    }

    public Database catalog() {
        return database;
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
        return impl.rowMapper(Optional.of(s));
    }

    public <T> RowMapper<Optional<T>> rowMapper(String s, Column<T, R> column) {
        return impl.rowMapper(Optional.of(s), column);
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

    public <T> Column<T,R> column(Function<R,T> getter) {
        String columnName = database.namingStrategy().columnName(MethodUtil.fromReference(rowClass, getter).getName());
        //noinspection unchecked
        return (Column<T,R>) columns()
            .filter(x -> StringUtils.equalsIgnoreCase(x.column().name(), columnName))
            .map(TableColumn::column)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(""));
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

        public <T> RowMapper<Optional<T>> rowMapper(Optional<String> prefix, Column<T, R> column) {
            return (rs, i) ->
                column.dataType().get(rs, prefix.map(s -> s + column.name()).orElse(column.name()));
        }
    }

    public static final class Builder<R, B> {
        private final Database database;
        private final Class<R> rowClass;
        private final Class<B> builderClass;
        private final Function<B, R> buildRow;
        private final Set<String> excludedFields = new HashSet<>();
        private final List<RowBuilderColumn<?, R, B>> columns = new ArrayList<>();
        private String schema;
        private String tableName;
        private Supplier<B> newBuilder;

        public Builder(Database database, Class<R> rowClass, Class<B> builderClass, Function<B,R> buildRow) {
            this.database = database;
            this.rowClass = rowClass;
            this.builderClass = builderClass;
            this.buildRow = buildRow;

            Optional<javax.persistence.Table> tableAnnotation = ClassUtil.getAnnotation(javax.persistence.Table.class, rowClass);
            this.schema = tableAnnotation
                .map(javax.persistence.Table::schema)
                .orElse(database.defaultSchema());
            this.tableName = tableAnnotation
                .map(javax.persistence.Table::name)
                .orElseGet(() -> database.namingStrategy().tableName(rowClass.getSimpleName()));
        }

        public Table<R> build() {
            if (newBuilder == null) {
                this.newBuilder = Factory.forClass(builderClass);
            }
            Arrays.stream(rowClass.getDeclaredFields())
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .filter(f -> !FieldUtil.hasAnnotation(Transient.class, f))
                .filter(f -> !excludedFields.contains(f.getName()))
                .forEach(f -> columns.add(RowBuilderColumn.fromField(database.namingStrategy(), rowClass, builderClass, f)));
            return new Table<>(this);
        }

        public Builder<R, B> schema(String val) {
            schema = val;
            return this;
        }

        public Builder<R, B> tableName(String val) {
            tableName = val;
            return this;
        }

        public <BB> Builder<R, BB> builder(Class<BB> builderClass, Function<BB,R> buildRow) {
            return new Builder<>(database, rowClass, builderClass, buildRow)
                .schema(schema)
                .tableName(tableName);
        }

        public <T> Builder<R, B> mandatory(Column<T, R> column, Function<R, T> getter, BiConsumer<B, T> setter) {
            excludedFields.add(MethodUtil.fromReference(rowClass, getter).getName());
            return mandatory(column, getter, setter, Optional.empty());
        }

        public <T> Builder<R, B> mandatory(Column<T, R> column, Function<R, T> getter, BiConsumer<B, T> setter, Consumer<RowBuilderColumn.Builder<T, R, B>> init) {
            excludedFields.add(MethodUtil.fromReference(rowClass, getter).getName());
            return mandatory(column, getter, setter, Optional.of(init));
        }

        public <T> Builder<R, B> mandatory(Column<T, R> column, Function<R, T> getter, BiConsumer<B, T> setter, Optional<Consumer<RowBuilderColumn.Builder<T, R, B>>> init) {
            RowBuilderColumn.Builder<T, R, B> columnBuilder = RowBuilderColumn.mandatory(column, getter, setter);
            init.ifPresent(x -> x.accept(columnBuilder));
            columns.add(columnBuilder.build());
            return this;
        }

        public <T> Builder<R, B> optional(Column<T, R> column, Function<R, Optional<T>> getter, BiConsumer<B, Optional<T>> setter) {
            excludedFields.add(MethodUtil.fromReference(rowClass, getter).getName());
            return optional(column, getter, setter, Optional.empty());
        }

        public <T> Builder<R, B> optional(Column<T, R> column, Function<R, Optional<T>> getter, BiConsumer<B, Optional<T>> setter, Consumer<RowBuilderColumn.Builder<T, R, B>> init) {
            excludedFields.add(MethodUtil.fromReference(rowClass, getter).getName());
            return optional(column, getter, setter, Optional.ofNullable(init));
        }

        private <T> Builder<R, B> optional(Column<T, R> column, Function<R, Optional<T>> getter, BiConsumer<B, Optional<T>> setter, Optional<Consumer<RowBuilderColumn.Builder<T, R, B>>> init) {
            excludedFields.add(MethodUtil.fromReference(rowClass, getter).getName());
            RowBuilderColumn.Builder<T, R, B> columnBuilder = RowBuilderColumn.optional(column, getter, setter);
            init.ifPresent(x -> x.accept(columnBuilder));
            columns.add(columnBuilder.build());
            return this;
        }
    }
}
