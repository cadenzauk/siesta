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

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.NamingStrategy;
import com.google.common.reflect.TypeToken;

import java.sql.ResultSet;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class EmbeddedColumn<T, R, B> extends ColumnMapping<T,T> implements TableColumn<T,R,B> {
    private final String propertyName;
    private final String columnName;
    private final Function<R,Optional<T>> getter;
    private final BiConsumer<B,Optional<T>> setter;

    private EmbeddedColumn(Builder<T,R,B> builder) {
        super(builder);
        propertyName = builder.propertyName;
        columnName = builder.columnName;
        getter = builder.getter;
        setter = builder.setter;
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
        return columns().mapToInt(Column::count).sum();
    }

    @Override
    public String sql() {
        return columns()
            .map(Column::columnName)
            .collect(joining(", "));
    }

    @Override
    public String sql(Alias<?> alias) {
        return columns()
            .map(c -> alias.inSelectClauseSql(c.columnName()))
            .collect(joining(", "));
    }

    @Override
    public String sqlWithLabel(Alias<?> alias, Optional<String> label) {
        return columns()
            .map(c -> String.format("%s as %s", alias.inSelectClauseSql(c.columnName()), label(alias, label, c)))
            .collect(joining(", "));
    }

    private String label(Alias<?> alias, Optional<String> label, TableColumn<?,T,T> c) {
        NamingStrategy naming = alias.table().database().namingStrategy();
        return label.map(l -> naming.embeddedName(l, c.columnName()))
            .orElseGet(() -> alias.inSelectClauseLabel(c.columnName()));
    }

    @Override
    public RowMapper<T> rowMapper(Alias<?> alias, Optional<String> label) {
        return super.rowMapper(alias);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U> Stream<Column<U,R>> as(TypeToken<U> requiredDataType) {
        return requiredDataType.getRawType().isAssignableFrom(rowType().getRawType())
            ? Stream.of((Column<U,R>) this)
            : Stream.empty();
    }

    @Override
    public Function<R,Optional<T>> getter() {
        return getter;
    }

    @Override
    public <V> Optional<Column<V,T>> findColumn(TypeToken<V> type, String propertyName) {
        return super.findColumn(type, propertyName);
    }

    @Override
    public Stream<Object> toDatabase(Database database, Optional<T> v) {
        return toDatabase(v);
    }

    @Override
    public ResultSetValue<B> extract(Alias<?> alias, ResultSet rs, Optional<String> label) {
        Optional<T> value = Optional.ofNullable(rowMapper(alias, label).mapRow(rs));
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

    static <T, R, B> Builder<T,R,B> mandatory(Database database, String name, TypeToken<T> rowType, Function1<R,T> getter, BiConsumer<B,T> setter) {
        return new Builder<>(database, name, rowType, r -> Optional.ofNullable(getter.apply(r)), (b, v) -> setter.accept(b, v.orElse(null)));
    }

    static <T, R, B> Builder<T,R,B> optional(Database database, String name, TypeToken<T> rowType, FunctionOptional1<R,T> getter, BiConsumer<B,Optional<T>> setter) {
        return new Builder<>(database, name, rowType, getter, setter);
    }

    public static final class Builder<T, R, B> extends ColumnMapping.Builder<T,T,Builder<T,R,B>> {
        private final String propertyName;
        private final Function<R,Optional<T>> getter;
        private final BiConsumer<B,Optional<T>> setter;
        private String columnName;

        private Builder(Database database, String propertyName, TypeToken<T> rowType, Function<R,Optional<T>> getter, BiConsumer<B,Optional<T>> setter) {
            super(database, rowType, rowType, Function.identity());
            this.propertyName = propertyName;
            this.getter = getter;
            this.setter = setter;
            columnName(database.namingStrategy().columnName(propertyName));
        }

        public Builder<T,R,B> columnName(String columnName) {
            this.columnName = columnName;
            childPrefix(Optional.of(columnName));
            return this;
        }

        public EmbeddedColumn<T,R,B> build() {
            finish();
            return new EmbeddedColumn<>(this);
        }
    }
}
