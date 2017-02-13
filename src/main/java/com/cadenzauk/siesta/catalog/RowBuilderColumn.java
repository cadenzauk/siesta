/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta.catalog;

import com.cadenzauk.core.reflect.ClassUtil;
import com.cadenzauk.core.reflect.Getter;
import com.cadenzauk.core.reflect.Setter;
import com.cadenzauk.siesta.DataType;
import com.cadenzauk.siesta.NamingStrategy;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.cadenzauk.siesta.catalog.Column.aColumn;

public class RowBuilderColumn<T, R, B> implements TableColumn<T, R> {
    private final Column<T, R> column;
    private final Function<R, Optional<T>> getter;
    private final BiConsumer<B, Optional<T>> setter;
    private final boolean primaryKey;

    private RowBuilderColumn(Builder<T, R, B> builder) {
        column = builder.column;
        getter = builder.getter;
        setter = builder.setter;
        primaryKey = builder.primaryKey;
    }

    public Function<R, Optional<T>> getter() {
        return getter;
    }

    @Override
    public Column<T, R> column() {
        return column;
    }

    @Override
    public String name() {
        return column.name();
    }

    @Override
    public DataType<T> dataType() {
        return column.dataType();
    }

    @Override
    public Class<R> rowClass() {
        return column.rowClass();
    }

    public boolean primaryKey() {
        return primaryKey;
    }

    public void extract(ResultSet rs, B builder, Optional<String> prefix) {
        Optional<T> value = column.dataType().get(rs, prefix.map(s -> s + column.name()).orElse(column.name()));
        setter.accept(builder, value);
    }

    public static <R, B> RowBuilderColumn<?, R, B> fromField(NamingStrategy namingStrategy, Class<R> rowClass, Class<B> builderClass, Field field) {
        String fieldName = field.getName();
        Field builderField = ClassUtil.declaredField(builderClass, fieldName).orElseThrow(() -> new IllegalArgumentException("Builder class " + builderClass + " does not have a field " + fieldName + "."));
        String columnName = namingStrategy.columnName(fieldName);
        Class<?> fieldType = field.getType();
        if (fieldType == Long.class || fieldType == Long.TYPE) {
            return mandatory(aColumn(columnName, DataType.LONG, rowClass), Getter.forField(rowClass, Long.class, field), Setter.forField(builderClass, Long.class, builderField)).build();
        }
        if (fieldType == Integer.class || fieldType == Integer.TYPE) {
            return mandatory(aColumn(columnName, DataType.INTEGER, rowClass), Getter.forField(rowClass, Integer.class, field), Setter.forField(builderClass, Integer.class, builderField)).build();
        }
        if (fieldType == String.class) {
            return mandatory(aColumn(columnName, DataType.STRING, rowClass), Getter.forField(rowClass, String.class, field), Setter.forField(builderClass, String.class, builderField)).build();
        }
        if (fieldType == Optional.class) {
            ParameterizedType genericType = (ParameterizedType)field.getGenericType();
            Type argType = genericType.getActualTypeArguments()[0];
            if (argType == String.class) {
                return optional(aColumn(columnName, DataType.STRING, rowClass), Getter.forField(rowClass, Optional.class, String.class, field), Setter.forField(builderClass, Optional.class, String.class, builderField)).build();
            }
        }
        return null;
    }

    public static <T, R, B> Builder<T, R, B>  mandatory(Column<T, R> column, Function<R, T> getter, BiConsumer<B, T> setter) {
        return new Builder<>(column, row -> Optional.ofNullable(getter.apply(row)), (b, v) -> setter.accept(b, v.orElseThrow(NoSuchElementException::new)));
    }

    public static <T, R, B> Builder<T, R, B>  optional(Column<T, R> column, Function<R, Optional<T>> getter, BiConsumer<B, Optional<T>> setter) {
        return new Builder<>(column, getter, setter);
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

        public RowBuilderColumn<T, R, B> build() {
            return new RowBuilderColumn<>(this);
        }
    }
}
