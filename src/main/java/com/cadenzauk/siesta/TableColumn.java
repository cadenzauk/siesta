/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import java.sql.ResultSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class TableColumn<T, R, B> {
    private final Column<T, R> column;
    private final Function<R, Optional<T>> getter;
    private final BiConsumer<B, Optional<T>> setter;
    private final boolean primaryKey;

    private TableColumn(Builder<T, R, B> builder) {
        column = builder.column;
        getter = builder.getter;
        setter = builder.setter;
        primaryKey = builder.primaryKey;
    }

    public Column<T, R> column() {
        return column;
    }

    public Function<R, Optional<T>> getter() {
        return getter;
    }

    public BiConsumer<B, Optional<T>> setter() {
        return setter;
    }

    public boolean primaryKey() {
        return primaryKey;
    }

    public static <T, R, B> Builder<T, R, B>  mandatory(Column<T, R> column, Function<R, T> getter, BiConsumer<B, T> setter) {
        return new Builder<>(column, row -> Optional.ofNullable(getter.apply(row)), (b, v) -> setter.accept(b, v.orElseThrow(NoSuchElementException::new)));
    }

    public static <T, R, B> Builder<T, R, B>  optional(Column<T, R> column, Function<R, Optional<T>> getter, BiConsumer<B, Optional<T>> setter) {
        return new Builder<>(column, getter, setter);
    }

    public void extract(ResultSet rs, B builder, Optional<String> prefix) {
        Optional<T> value = column.dataType().get(rs, prefix.map(s -> s + column.name()).orElse(column.name()));
        setter.accept(builder, value);
    }

    public static final class Builder<T, R, B> {
        private final Column<T, R> column;
        private final Function<R, Optional<T>> getter;
        private final BiConsumer<B, Optional<T>> setter;
        private boolean primaryKey;

        private Builder(Column<T, R> column, Function<R, Optional<T>> getter, BiConsumer<B, Optional<T>> setter) {
            this.column = column;
            this.getter = getter;
            this.setter = setter;
        }

        public Builder<T, R, B> primaryKey() {
            primaryKey = true;
            return this;
        }

        public TableColumn<T, R, B> build() {
            return new TableColumn<>(this);
        }
    }
}
