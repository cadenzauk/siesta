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

package com.cadenzauk.siesta.catalog;

import com.cadenzauk.core.reflect.FieldInfo;
import com.cadenzauk.core.reflect.Setter;
import com.cadenzauk.core.reflect.util.ClassUtil;
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.DataType;
import com.cadenzauk.siesta.Database;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class TableColumn<T, R, B> implements Column<T,R> {
    private final String name;
    private final DataType<T> dataType;
    private final TypeToken<R> rowType;
    private final Function<R,Optional<T>> getter;
    private final BiConsumer<B,Optional<T>> setter;
    private final boolean primaryKey;

    private TableColumn(Builder<T,R,B> builder) {
        name = builder.name;
        dataType = builder.dataType;
        rowType = builder.rowType;
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
    public RowMapper<T> rowMapper(Database database, String label) {
        return rs -> dataType.get(rs, label, database).orElse(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<R> rowClass() {
        return (Class<R>) rowType.getRawType();
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

    public ResultSetValue<B> extract(Database db, ResultSet rs, String label) {
        Optional<T> value = dataType.get(rs, label, db);
        return new ResultSetValue<B>() {
            @Override
            public boolean isPresent() {
                return value.isPresent();
            }

            @Override
            public void apply(B builder) {
                setter.accept(builder, value);
            }
        };
    }

    public String label(String prefix) {
        return prefix + name;
    }

    public static <T, R, B> TableColumn<T,R,B> fromField(Database database, TypeToken<R> rowType, TypeToken<B> builderType, Field field) {
        @SuppressWarnings("unchecked") FieldInfo<R,T> fieldInfo = (FieldInfo<R,T>) FieldInfo.of(rowType.getRawType(), field);
        DataType<T> dataType = database.dataTypeOf(fieldInfo)
            .orElseThrow(() -> new IllegalArgumentException("Unable to determine the data type for " + fieldInfo));
        Field builderField = ClassUtil.findField(builderType.getRawType(), fieldInfo.name())
            .orElseThrow(() -> new IllegalArgumentException("Builder class " + builderType + " does not have a field " + fieldInfo.name() + "."));
        return optional(
            database.columnNameFor(fieldInfo),
            dataType,
            fieldInfo.declaringType(),
            fieldInfo.optionalGetter(),
            Setter.forField(builderType, fieldInfo.effectiveType(), builderField))
            .build();
    }

    static <T, R, B> Builder<T,R,B> mandatory(String name, DataType<T> dataType, TypeToken<R> rowType, Function<R,T> getter, BiConsumer<B,T> setter) {
        return new Builder<>(name, dataType, rowType, row -> Optional.ofNullable(getter.apply(row)), (b, v) -> setter.accept(b, v.orElseThrow(NoSuchElementException::new)));
    }

    static <T, R, B> Builder<T,R,B> optional(String name, DataType<T> dataType, TypeToken<R> rowClass, Function<R,Optional<T>> getter, BiConsumer<B,Optional<T>> setter) {
        return new Builder<>(name, dataType, rowClass, getter, setter);
    }

    public static final class Builder<T, R, B> {
        private final String name;
        private final DataType<T> dataType;
        private final TypeToken<R> rowType;
        private final Function<R,Optional<T>> getter;
        private final BiConsumer<B,Optional<T>> setter;
        private boolean primaryKey;

        private Builder(String name, DataType<T> dataType, TypeToken<R> rowType, Function<R,Optional<T>> getter, BiConsumer<B,Optional<T>> setter) {
            this.name = name;
            this.dataType = dataType;
            this.rowType = rowType;
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

    interface ResultSetValue<B> {
        boolean isPresent();

        void apply(B builder);
    }
}
