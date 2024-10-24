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

import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.core.util.Lazy;
import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.catalog.Column;
import com.cadenzauk.siesta.catalog.ForeignKeyReference;
import com.cadenzauk.siesta.catalog.Table;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class RegularTableAlias<R> extends TableAlias<R> {
    private final Table<R> table;
    private final Optional<String> aliasName;
    private final Lazy<List<ProjectionColumn<?>>> projectionColumns = new Lazy<>(this::computeProjectionColumns);

    private RegularTableAlias(Table<R> table, Optional<String> aliasName) {
        this.table = table;
        this.aliasName = aliasName;
    }

    @Override
    public String qualifiedTableName() {
        return table.qualifiedName();
    }

    private List<ProjectionColumn<?>> computeProjectionColumns() {
        return table.columns()
            .map(this::projectionColumn)
            .collect(toList());
    }

    private <T> ProjectionColumn<T> projectionColumn(Column<T,R> c) {
        return c.toProjection(this, Optional.empty());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        RegularTableAlias<?> alias = (RegularTableAlias<?>) o;

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
    public <T> String columnSql(Scope scope, ColumnSpecifier<T> columnSpecifier) {
        Column<?,R> column = OptionalUtil.orGet(
            columnSpecifier.asReferringMethodInfo(type()).map(table::column),
            () -> table.columns().filter(it -> it.columnName().equals(columnSpecifier.columnName(scope))).findFirst()
        ).orElseThrow(IllegalArgumentException::new);
        return column.sql(this);
    }

    @Override
    public <T> String columnSqlWithLabel(Scope scope, ColumnSpecifier<T> columnSpecifier, Optional<String> label) {
        Column<T,R> column = columnSpecifier.asReferringMethodInfo(type())
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
    public <T> Optional<AliasColumn<T>> findAliasColumn(Scope scope, ColumnSpecifier<T> columnSpecifier) {
        return table
            .columns()
            .flatMap(ac -> ac.as(columnSpecifier.effectiveType()))
            .filter(ac -> columnSpecifier.specifies(scope, ac))
            .findFirst();
    }

    @Override
    public <P> Optional<ForeignKeyReference<R,P>> foreignKey(Alias<P> parent, Optional<String> name) {
        Optional<RegularTableAlias<P>> parentAsTableAlias = OptionalUtil.as(new TypeToken<RegularTableAlias<P>>() {}, parent);
        return parentAsTableAlias
            .flatMap(p -> table.foreignKey(p.table(), name));
    }

    @Override
    public Stream<ProjectionColumn<?>> projectionColumns(Scope scope) {
        return projectionColumns.get().stream();
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return Stream.empty();
    }

    @Override
    public RowMapperFactory<R> rowMapperFactory() {
        return table.rowMapperFactory(this, Optional.empty());
    }

    public DynamicRowMapperFactory<R> dynamicRowMapperFactory() {
        return table.dynamicRowMapperFactory(this);
    }

    public static <U> RegularTableAlias<U> of(Table<U> table) {
        return new RegularTableAlias<>(table, Optional.empty());
    }

    public static <U> RegularTableAlias<U> of(Table<U> table, String aliasName) {
        return new RegularTableAlias<>(table, Optional.of(aliasName));
    }
}
