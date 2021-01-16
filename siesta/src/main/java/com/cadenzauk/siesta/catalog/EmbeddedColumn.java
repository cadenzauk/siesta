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
import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.AliasColumn;
import com.cadenzauk.siesta.ColumnSpecifier;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.NamingStrategy;
import com.cadenzauk.siesta.ProjectionColumn;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.ddl.definition.action.ColumnDataType;
import com.google.common.reflect.TypeToken;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class EmbeddedColumn<T, TB, R, RB> implements TableColumn<T,R,RB>, ColumnCollection<T> {
    private final String propertyName;
    private final TypeToken<T> type;
    private final String columnName;
    private final boolean identifier;
    private final boolean insertable;
    private final boolean updatable;
    private final Function<R,Optional<T>> getter;
    private final BiConsumer<RB,Optional<T>> setter;
    private final ColumnMapping<T,TB> columnMapping;

    private EmbeddedColumn(Builder<T,TB,R,RB> builder) {
        propertyName = builder.propertyName;
        type = builder.type;
        identifier = builder.identifier;
        insertable = builder.insertable;
        updatable = builder.updatable;
        columnName = builder.columnName;
        getter = builder.getter;
        setter = builder.setter;
        columnMapping = new ColumnMapping<>(builder);
    }

    @Override
    public String propertyName() {
        return propertyName;
    }

    @Override
    public TypeToken<T> type() {
        return type;
    }

    @Override
    public String columnName() {
        return columnName;
    }

    @Override
    public boolean identifier() {
        return false;
    }

    @Override
    public boolean insertable() {
        return insertable;
    }

    @Override
    public boolean updatable() {
        return updatable && !identifier;
    }

    @Override
    public int count() {
        return columns().mapToInt(Column::count).sum();
    }

    @Override
    public ColumnDataType<T> columnType() {
        throw new UnsupportedOperationException();
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

    @Override
    public ProjectionColumn<T> toProjection(Alias<?> alias, Optional<String> label) {
        List<ProjectionColumn<?>> components = columns()
            .map(c -> c.toProjection(alias, Optional.of(label(alias, label, c))))
            .collect(Collectors.toList());
        return new ProjectionColumn<>(type, propertyName, columnName, sql(alias), label.orElseGet(() -> alias.inSelectClauseLabel(columnName)), rowMapperFactory(alias, label), components);
    }

    @Override
    public Stream<String> idSql(Alias<?> alias) {
        return identifier
            ? columns().map(c -> String.format("%s = ?", alias.inSelectClauseSql(c.columnName())))
            : Stream.empty();
    }

    @Override
    public Stream<Object> idArgs(Database database, R row) {
        return identifier
            ? columns().flatMap(c -> c.insertArgs(database, getter.apply(row)))
            : Stream.empty();
    }

    @Override
    public Stream<String> insertColumnSql() {
        return insertable
            ? columns().flatMap(Column::insertColumnSql)
            : Stream.empty();
    }

    @Override
    public Stream<String> insertArgsSql() {
        return insertable
            ? columns().flatMap(Column::insertArgsSql)
            : Stream.empty();
    }

    @Override
    public Stream<Object> insertArgs(Database database, Optional<R> row) {
        return insertable
            ? columns().flatMap(c -> c.insertArgs(database, row.flatMap(getter)))
            : Stream.empty();
    }

    @Override
    public Stream<String> updateSql() {
        return updatable
            ? columns().flatMap(Column::updateSql)
            : Stream.empty();
    }

    @Override
    public Stream<Object> updateArgs(Database database, R row) {
        return Stream.empty();
    }

    @Override
    public TypeToken<T> rowType() {
        return columnMapping.rowType();
    }

    @Override
    public Stream<Column<?,T>> columns() {
        return columnMapping.columns();
    }

    @Override
    public <T1> Column<T1,T> column(MethodInfo<T,T1> methodInfo) {
        return columnMapping.column(methodInfo);
    }

    @Override
    public <T1> ColumnCollection<T1> embedded(MethodInfo<T,T1> methodInfo) {
        return columnMapping.embedded(methodInfo);
    }

    public static <T> String label(Alias<?> alias, Optional<String> label, Column<?,T> c) {
        NamingStrategy naming = alias.database().namingStrategy();
        return label.map(l -> naming.embeddedName(l, c.columnName()))
            .orElseGet(() -> alias.inSelectClauseLabel(c.columnName()));
    }

    @Override
    public RowMapperFactory<T> rowMapperFactory(Alias<?> alias, Optional<String> defaultLabel) {
        return columnMapping.rowMapperFactory(alias, defaultLabel);
    }

    @Override
    public <U> Stream<AliasColumn<U>> as(TypeToken<U> requiredDataType) {
        return asColumn(requiredDataType).map(Function.identity());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U> Stream<Column<U,R>> asColumn(TypeToken<U> requiredDataType) {
        return requiredDataType.getRawType().isAssignableFrom(rowType().getRawType())
            ? Stream.of((Column<U,R>) this)
            : Stream.empty();
    }

    @Override
    public Function<R,Optional<T>> getter() {
        return getter;
    }

    @Override
    public <V> Optional<AliasColumn<V>> findColumn(TypeToken<V> type, String propertyName) {
        return columnMapping.findColumn(type, propertyName).map(Function.identity());
    }

    @Override
    public boolean includes(Scope scope, ColumnSpecifier<?> columnSpecifier) {
        return false;
    }

    @Override
    public Stream<Object> toDatabase(Database database, Optional<T> v) {
        return columns().flatMap(c -> c.rowToDatabase(database, v));
    }

    @Override
    public ResultSetValue<RB> extract(Alias<?> alias, ResultSet rs, String prefix, Optional<String> label) {
        Optional<T> value = Optional.ofNullable(rowMapperFactory(alias, label).rowMapper(prefix, Optional.empty()).mapRow(rs));
        return new ResultSetValue<RB>() {
            @Override
            public boolean isPresent() {
                return value.isPresent();
            }

            @Override
            public void apply(RB builder) {
                setter.accept(builder, value);
            }
        };
    }

    @Override
    public String label(String prefix) {
        return prefix + columnName;
    }

    @Override
    public Stream<Column<?,?>> primitiveColumns() {
        return columnMapping.primitiveColumns();
    }

    static <T, R, B> Builder<T,T,R,B> mandatory(Database database, String name, TypeToken<T> rowType, Function1<R,T> getter, BiConsumer<B,T> setter) {
        return new Builder<>(database, name, rowType, rowType, r -> Optional.ofNullable(getter.apply(r)), (b, v) -> setter.accept(b, v.orElse(null)), Function.identity());
    }

    static <T, R, B> Builder<T,T,R,B> optional(Database database, String name, TypeToken<T> rowType, FunctionOptional1<R,T> getter, BiConsumer<B,Optional<T>> setter) {
        return new Builder<>(database, name, rowType, rowType, getter, setter, Function.identity());
    }

    public static final class Builder<T, TB, R, RB> extends ColumnMapping.Builder<T,TB,Builder<T,TB,R,RB>> {
        private final String propertyName;
        private final Function<R,Optional<T>> getter;
        private final BiConsumer<RB,Optional<T>> setter;
        public TypeToken<T> type;
        private boolean identifier = false;
        private boolean insertable = true;
        private boolean updatable = true;
        private String columnName;

        private Builder(Database database, String propertyName, TypeToken<T> rowType, TypeToken<TB> builderType, Function<R,Optional<T>> getter, BiConsumer<RB,Optional<T>> setter, Function<TB,T> buildRow) {
            super(database, rowType, builderType, buildRow);
            this.propertyName = propertyName;
            this.getter = getter;
            this.setter = setter;
            columnName(database.columnName(propertyName));
        }

        public <BB> Builder<T,BB,R,RB> builder(Function1<BB,T> buildRow) {
            MethodInfo<BB,T> buildMethod = MethodInfo.of(buildRow);
            return new Builder<>(database, propertyName, rowType, buildMethod.referringType(), getter, setter, buildRow)
                .type(type)
                .identifier(identifier)
                .insertable(insertable)
                .updatable(updatable)
                .columnName(columnName);
        }

        public Builder<T,TB,R,RB> type(TypeToken<T> val) {
            this.type = val;
            return this;
        }

        public Builder<T,TB,R,RB> identifier(boolean val) {
            this.identifier = val;
            return this;
        }

        public Builder<T,TB,R,RB> insertable(boolean val) {
            this.insertable = val;
            return this;
        }

        public Builder<T,TB,R,RB> updatable(boolean val) {
            this.updatable = val;
            return this;
        }

        public Builder<T,TB,R,RB> columnName(String columnName) {
            this.columnName = columnName;
            childPrefix(Optional.of(columnName));
            return this;
        }

        public EmbeddedColumn<T,TB,R,RB> build() {
            finish();
            return new EmbeddedColumn<>(this);
        }
    }
}
