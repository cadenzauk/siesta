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
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.NamingStrategy;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class EmbeddedColumn<T, R, B> implements TableColumn<T,R,B> {
    private final Table<T> table;
    private final Function<R,Optional<T>> getter;
    private final BiConsumer<B,Optional<T>> setter;
    private final String name;

    private EmbeddedColumn(String name, Table<T> table, Function<R,Optional<T>> getter, BiConsumer<B,Optional<T>> setter) {
        this.name = name;
        this.table = table;
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int count() {
        return table.columns().mapToInt(Column::count).sum();
    }

    @Override
    public String sql() {
        NamingStrategy naming = table.database().namingStrategy();
        return table.columns()
            .map(c -> naming.embeddedName(name, c.name()))
            .collect(joining(", "));
    }

    @Override
    public String sql(Alias<R> alias) {
        NamingStrategy naming = table.database().namingStrategy();
        return table.columns()
            .map(c -> alias.inSelectClauseSql(naming.embeddedName(name, c.name())))
            .collect(joining(", "));
    }

    @Override
    public String sqlWithLabel(Alias<R> alias, String label) {
        NamingStrategy naming = table.database().namingStrategy();
        return table.columns()
            .map(c -> String.format("%s as %s", alias.inSelectClauseSql(naming.embeddedName(name, c.name())), naming.embeddedName(label, c.name())))
            .collect(joining(", "));
    }

    @Override
    public RowMapper<T> rowMapper(Database database, String label) {
        return table.rowMapper(label + "_");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U> Stream<Column<U,R>> as(Class<U> requiredDataType) {
        return requiredDataType.isAssignableFrom(table.rowType().getRawType())
            ? Stream.of((Column<U,R>) this)
            : Stream.empty();
    }

    @Override
    public Function<R,Optional<T>> getter() {
        return getter;
    }

    @Override
    public Stream<Object> toDatabase(Database database, Optional<T> v) {
        return table.toDatabase(v);
    }

    @Override
    public ResultSetValue<B> extract(Database db, ResultSet rs, String label) {
        Optional<T> value = Optional.ofNullable(rowMapper(db, label).mapRow(rs));
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
    public String label(String labelPrefix) {
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T, R, B> TableColumn<T,R,B> fromField(Database database, TypeToken<R> rowType, TypeToken<B> builderType, Field field) {
        FieldInfo<R,T> fieldInfo = (FieldInfo<R,T>) FieldInfo.of(rowType, field);
        Field builderField = ClassUtil.findField(builderType.getRawType(), fieldInfo.name())
            .orElseThrow(() -> new IllegalArgumentException("Builder class " + builderType + " does not have a field " + fieldInfo.name() + "."));
        return new EmbeddedColumn<T,R,B>(
            database.columnNameFor(fieldInfo),
            database.table(fieldInfo.effectiveType()),
            fieldInfo.optionalGetter(),
            Setter.forField(builderType, fieldInfo.effectiveType(), builderField));
    }
}
