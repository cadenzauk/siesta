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
import com.cadenzauk.core.reflect.util.TypeUtil;
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.Alias;
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

public class PrimitiveColumn<T, R, B> implements TableColumn<T,R,B> {
    private final String propertyName;
    private final String columnName;
    private final DataType<T> dataType;
    private final Function<R,Optional<T>> getter;
    private final BiConsumer<B,Optional<T>> setter;

    private PrimitiveColumn(Builder<T,R,B> builder) {
        propertyName = builder.propertyName;
        columnName = builder.columnName;
        dataType = builder.dataType;
        getter = builder.getter;
        setter = builder.setter;
    }

    @Override
    public Function<R,Optional<T>> getter() {
        return getter;
    }

    @Override
    public Stream<Object> toDatabase(Database database, Optional<T> v) {
        return Stream.of(dataType.toDatabase(database, v));
    }

    @Override
    public <V> Optional<Column<V,T>> findColumn(TypeToken<V> type, String propertyName) {
        return Optional.empty();
    }

    @Override
    public String propertyName() {
        return propertyName;
    }

    @Override
    public String columnName() {
        return columnName;
    }

    @Override
    public int count() {
        return 1;
    }

    @Override
    public String sql() {
        return columnName;
    }

    @Override
    public String sql(Alias<?> alias) {
        return alias.inSelectClauseSql(columnName);
    }

    @Override
    public String sqlWithLabel(Alias<?> alias, Optional<String> label) {
        return String.format("%s as %s", alias.inSelectClauseSql(columnName), label.orElseGet(() -> alias.inSelectClauseLabel(columnName)));
    }

    @Override
    public RowMapper<T> rowMapper(Alias<?> alias, Optional<String> label) {
        return rs -> dataType.get(rs, label.orElseGet(() -> alias.inSelectClauseLabel(columnName)), alias.table().database()).orElse(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U> Stream<Column<U,R>> as(TypeToken<U> requiredDataType) {
        return TypeUtil.boxedType(requiredDataType.getRawType()).isAssignableFrom(TypeUtil.boxedType(dataType.javaClass()))
            ? Stream.of((Column<U,R>) this)
            : Stream.empty();
    }

    @Override
    public ResultSetValue<B> extract(Alias<?> alias, ResultSet rs, Optional<String> label) {
        Optional<T> value = dataType.get(rs, label.orElseGet(() -> alias.inSelectClauseLabel(columnName)), alias.table().database());
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

    @Override
    public String label(String prefix) {
        return prefix + columnName;
    }

    public static <T, R, B> PrimitiveColumn<T,R,B> fromField(Database database, TypeToken<B> builderType, FieldInfo<R,T> fieldInfo) {
        DataType<T> dataType = database
            .dataTypeOf(fieldInfo).orElseThrow(() -> new IllegalArgumentException("Unable to determine the data type for " + fieldInfo));
        Field builderField = ClassUtil.findField(builderType.getRawType(), fieldInfo.name())
            .orElseThrow(() -> new IllegalArgumentException("Builder class " + builderType + " does not have a field " + fieldInfo.name() + "."));
        String columnName = database.columnNameFor(fieldInfo);
        return optional(
            database,
            fieldInfo.name(),
            dataType,
            fieldInfo.optionalGetter(),
            Setter.forField(builderType, fieldInfo.effectiveClass(), builderField)
        ).columnName(columnName).build();
    }

    static <T, R, B> Builder<T,R,B> mandatory(Database database, String fieldName, DataType<T> dataType, Function<R,T> getter, BiConsumer<B,T> setter) {
        return new Builder<>(database, fieldName, dataType, row -> Optional.ofNullable(getter.apply(row)), (b, v) -> setter.accept(b, v.orElseThrow(NoSuchElementException::new)));
    }

    static <T, R, B> Builder<T,R,B> optional(Database database, String fieldName, DataType<T> dataType, Function<R,Optional<T>> getter, BiConsumer<B,Optional<T>> setter) {
        return new Builder<>(database, fieldName, dataType, getter, setter);
    }

    public static final class Builder<T, R, B> {
        private final String propertyName;
        private String columnName;
        private final DataType<T> dataType;
        private final Function<R,Optional<T>> getter;
        private final BiConsumer<B,Optional<T>> setter;

        private Builder(Database database, String propertyName, DataType<T> dataType, Function<R,Optional<T>> getter, BiConsumer<B,Optional<T>> setter) {
            this.propertyName = propertyName;
            this.dataType = dataType;
            this.getter = getter;
            this.setter = setter;
            this.columnName = database.namingStrategy().columnName(this.propertyName);
        }

        public Builder<T,R,B> columnName(String val) {
            columnName = val;
            return this;
        }

        public PrimitiveColumn<T,R,B> build() {
            return new PrimitiveColumn<>(this);
        }
    }
}
