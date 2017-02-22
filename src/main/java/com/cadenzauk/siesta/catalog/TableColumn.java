/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.siesta.catalog;

import com.cadenzauk.core.reflect.ClassUtil;
import com.cadenzauk.core.reflect.FieldInfo;
import com.cadenzauk.core.reflect.Getter;
import com.cadenzauk.core.reflect.Setter;
import com.cadenzauk.siesta.DataType;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.NamingStrategy;
import com.cadenzauk.siesta.RowMapper;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class TableColumn<T, R, B> implements Column<T,R> {
    private final String name;
    private final DataType<T> dataType;
    private final Class<R> rowClass;
    private final Function<R,Optional<T>> getter;
    private final BiConsumer<B,Optional<T>> setter;
    private final boolean primaryKey;

    private TableColumn(Builder<T,R,B> builder) {
        name = builder.name;
        dataType = builder.dataType;
        rowClass = builder.rowClass;
        getter = builder.getter;
        setter = builder.setter;
        primaryKey = builder.primaryKey;
    }

    public Function<R,Optional<T>> getter() {
        return getter;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public DataType<T> dataType() {
        return dataType;
    }

    @Override
    public RowMapper<T> rowMapper(String label) {
        return (rs, i) -> dataType.get(rs, label).orElse(null);
    }

    @Override
    public Class<R> rowClass() {
        return rowClass;
    }

    @SuppressWarnings("unchecked")
    public <U> Stream<Column<U,R>> as(DataType<U> requiredDataType) {
        return dataType == requiredDataType
            ? Stream.of((Column<U,R>) this)
            : Stream.empty();
    }

    @Override
    public boolean primaryKey() {
        return primaryKey;
    }

    public void extract(ResultSet rs, B builder, Optional<String> prefix) {
        Optional<T> value = dataType.get(rs, prefix.map(s -> s + name).orElse(name));
        setter.accept(builder, value);
    }

    public static <R, B> TableColumn<?,R,B> fromField(Database namingStrategy, Class<R> rowClass, Class<B> builderClass, Field field) {
        String fieldName = field.getName();
        Field builderField = ClassUtil.findField(builderClass, fieldName)
            .orElseThrow(() -> new IllegalArgumentException("Builder class " + builderClass + " does not have a field " + fieldName + "."));

        String columnName = namingStrategy.columnNameFor(FieldInfo.of(rowClass, field, Object.class));
        Class<?> fieldType = field.getType();
        if (fieldType == Long.class || fieldType == Long.TYPE) {
            return mandatory(columnName, DataType.LONG, rowClass, Getter.forField(rowClass, Long.class, field), Setter.forField(builderClass, Long.class, builderField)).build();
        }
        if (fieldType == Integer.class || fieldType == Integer.TYPE) {
            return mandatory(columnName, DataType.INTEGER, rowClass, Getter.forField(rowClass, Integer.class, field), Setter.forField(builderClass, Integer.class, builderField)).build();
        }
        if (fieldType == String.class) {
            return mandatory(columnName, DataType.STRING, rowClass, Getter.forField(rowClass, String.class, field), Setter.forField(builderClass, String.class, builderField)).build();
        }
        if (fieldType == Optional.class) {
            ParameterizedType genericType = (ParameterizedType) field.getGenericType();
            Type argType = genericType.getActualTypeArguments()[0];
            if (argType == String.class) {
                return optional(columnName, DataType.STRING, rowClass, Getter.forField(rowClass, Optional.class, String.class, field), Setter.forField(builderClass, Optional.class, String.class, builderField)).build();
            }
            if (argType == Integer.class) {
                return optional(columnName, DataType.INTEGER, rowClass, Getter.forField(rowClass, Optional.class, Integer.class, field), Setter.forField(builderClass, Optional.class, Integer.class, builderField)).build();
            }
        }
        return null;
    }

    static <T, R, B> Builder<T,R,B> mandatory(String name, DataType<T> dataType, Class<R> rowClass, Function<R,T> getter, BiConsumer<B,T> setter) {
        return new Builder<>(name, dataType, rowClass, row -> Optional.ofNullable(getter.apply(row)), (b, v) -> setter.accept(b, v.orElseThrow(NoSuchElementException::new)));
    }

    static <T, R, B> Builder<T,R,B> optional(String name, DataType<T> dataType, Class<R> rowClass, Function<R,Optional<T>> getter, BiConsumer<B,Optional<T>> setter) {
        return new Builder<>(name, dataType, rowClass, getter, setter);
    }

    public static final class Builder<T, R, B> {
        private final String name;
        private final DataType<T> dataType;
        private final Class<R> rowClass;
        private final Function<R,Optional<T>> getter;
        private final BiConsumer<B,Optional<T>> setter;
        private boolean primaryKey;

        private Builder(String name, DataType<T> dataType, Class<R> rowClass, Function<R,Optional<T>> getter, BiConsumer<B,Optional<T>> setter) {
            this.name = name;
            this.dataType = dataType;
            this.rowClass = rowClass;
            this.getter = getter;
            this.setter = setter;
        }

        public Builder<T,R,B> primaryKey() {
            primaryKey = true;
            return this;
        }

        public TableColumn<T,R,B> build() {
            return new TableColumn<>(this);
        }
    }
}
