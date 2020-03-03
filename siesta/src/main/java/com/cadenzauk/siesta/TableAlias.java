/*
 * Copyright (c) 2019 Cadenza United Kingdom Limited
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

package com.cadenzauk.siesta;

import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.catalog.Column;
import com.cadenzauk.siesta.catalog.ForeignKeyReference;
import com.cadenzauk.siesta.catalog.Table;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class TableAlias<R> extends Alias<R> {
    private final Table<R> table;
    private final Optional<String> aliasName;

    private TableAlias(Table<R> table, Optional<String> aliasName) {
        this.table = table;
        this.aliasName = aliasName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TableAlias<?> alias = (TableAlias<?>) o;

        return new EqualsBuilder()
            .append(table.qualifiedName(), alias.table.qualifiedName())
            .append(aliasName, alias.aliasName)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(table.qualifiedName())
            .append(aliasName)
            .toHashCode();
    }

    @Override
    public TypeToken<R> type() {
        return table.rowType();
    }

    @Override
    public Database database() {
        return table.database();
    }

    @Override
    protected boolean isDual() {
        return table.rowType().getRawType() == Dual.class;
    }

    @Override
    public String withoutAlias() {
        return table.qualifiedName();
    }

    @Override
    public String inFromClauseSql() {
        if (isDual()) {
            return table.database().dialect().dual();
        }
        return aliasName
            .map(a -> String.format("%s %s", table.qualifiedName(), a))
            .orElseGet(table::qualifiedName);
    }

    @Override
    public <T> String columnSql(MethodInfo<?,T> getterMethod) {
        Column<T,R> column = getterMethod.asReferring(type())
            .map(table::column)
            .orElseThrow(IllegalArgumentException::new);
        return column.sql(this);
    }

    @Override
    public <T> String columnSqlWithLabel(MethodInfo<?,T> getterMethod, Optional<String> label) {
        Column<T,R> column = getterMethod.asReferring(type())
            .map(table::column)
            .orElseThrow(IllegalArgumentException::new);
        return column.sqlWithLabel(this, label);
    }

    @Override
    public String inSelectClauseSql(Scope scope) {
        return table.columns()
            .map(c -> c.sqlWithLabel(this, Optional.empty()))
            .collect(joining(", "));
    }

    @Override
    public String inSelectClauseSql(String columnName) {
        return String.format("%s.%s", aliasName.orElseGet(table::qualifiedName), columnName);
    }

    @Override
    public String inSelectClauseSql(String columnName, Optional<String> label) {
        return String.format("%s.%s %s", aliasName.orElseGet(table::qualifiedName), columnName, label.orElseGet(() -> inSelectClauseLabel(columnName)));
    }

    @Override
    protected String columnLabelPrefix() {
        return aliasName.orElseGet(table::tableName);
    }

    public Table<R> table() {
        return table;
    }

    public Optional<String> aliasName() {
        return aliasName;
    }

    @Override
    public <T> Optional<ProjectionColumn<T>> findColumn(MethodInfo<?,T> method) {
        return method.asReferring(type())
            .map(this::column)
            .map(c -> new ProjectionColumn<>(c.columnName(), inSelectClauseLabel(c.columnName()), c.rowMapperFactory(this, Optional.empty())));
    }

    private <T> Column<T,R> column(MethodInfo<R,T> methodInfo) {
        return table().column(methodInfo);
    }

    @Override
    public <P> Optional<ForeignKeyReference<R,P>> foreignKey(Alias<P> parent, Optional<String> name) {
        Optional<TableAlias<P>> parentAsTableAlias = OptionalUtil.as(new TypeToken<TableAlias<P>>() {}, parent);
        return parentAsTableAlias
            .flatMap(p -> table.foreignKey(p.table(), name));
    }

    @Override
    public Stream<ProjectionColumn<?>> projectionColumns(Scope scope) {
        return table.columns()
            .map(c -> new ProjectionColumn<>(c.columnName(), inSelectClauseLabel(c.columnName()), c.rowMapperFactory(this, Optional.empty())));
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return Stream.empty();
    }

    @Override
    public RowMapperFactory<R> rowMapperFactory() {
        return table.rowMapperFactory(this, Optional.empty());
    }

    @Override
    public <T> RowMapperFactory<T> rowMapperFactoryFor(MethodInfo<?,T> getterMethod, Optional<String> defaultLabel) {
        Column<T,R> column = getterMethod.asReferring(type())
            .map(table::column)
            .orElseThrow(IllegalArgumentException::new);
        return column.rowMapperFactory(this, defaultLabel);
    }

    @Override
    public Stream<Alias<?>> as(MethodInfo<?,?> getter, Optional<String> requiredAlias) {
        if (requiredAlias.isPresent()) {
            if (requiredAlias.equals(aliasName)) {
                if (getter.referringClass().isAssignableFrom(type().getRawType())) {
                    return Stream.of(this);
                }
                throw new IllegalArgumentException("Alias " + columnLabelPrefix() + " is an alias for " + type() + " and not " + getter.referringClass());
            } else {
                return Stream.empty();
            }
        }
        if (getter.referringClass().isAssignableFrom(type().getRawType())) {
            return Stream.of(this);
        }
        return Stream.empty();
    }

    public DynamicRowMapperFactory<R> dynamicRowMapperFactory() {
        return table.dynamicRowMapperFactory(this);
    }

    public static <U> TableAlias<U> of(Table<U> table) {
        return new TableAlias<>(table, Optional.empty());
    }

    public static <U> TableAlias<U> of(Table<U> table, String aliasName) {
        return new TableAlias<>(table, Optional.of(aliasName));
    }
}
