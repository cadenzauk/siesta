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

package com.cadenzauk.siesta;

import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.core.stream.StreamUtil;
import com.cadenzauk.siesta.catalog.Column;
import com.cadenzauk.siesta.catalog.ForeignKeyReference;
import com.cadenzauk.siesta.grammar.select.CommonTableExpression;
import com.google.common.reflect.TypeToken;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class CteAlias<RT> extends Alias<RT> {
    private final CommonTableExpression<RT> commonTableExpression;
    private final Optional<String> aliasName;

    public CteAlias(CommonTableExpression<RT> commonTableExpression, Optional<String> aliasName) {
        this.commonTableExpression = commonTableExpression;
        this.aliasName = aliasName;
    }


    @Override
    public String inSelectClauseSql(Scope scope) {
        return commonTableExpression.table().columns()
            .map(c -> c.sqlWithLabel(this, Optional.empty()))
            .collect(joining(", "));
    }

    @Override
    public String inSelectClauseSql(String columnName) {
        return String.format("%s.%s", aliasName().orElseGet(commonTableExpression::name), columnName);
    }

    @Override
    public String inSelectClauseSql(String columnName, Optional<String> label) {
        return String.format("%s.%s %s", aliasName().orElseGet(commonTableExpression::name), columnName, label.orElseGet(() -> this.inSelectClauseLabel(columnName)));
    }

    @Override
    protected String columnLabelPrefix() {
        return aliasName().orElseGet(commonTableExpression::name);
    }

    @Override
    public Optional<String> aliasName() {
        return aliasName;
    }

    @Override
    public RowMapperFactory<RT> rowMapperFactory() {
        return commonTableExpression.table().rowMapperFactory(this, Optional.empty());
    }

    @Override
    public <T> RowMapperFactory<T> rowMapperFactoryFor(ColumnSpecifier<T> columnSpecifier, Optional<String> defaultLabel) {
        Column<T,RT> column = columnSpecifier.asReferringMethodInfo(type())
            .map(this::column)
            .orElseThrow(IllegalArgumentException::new);
        return column.rowMapperFactory(this, defaultLabel);
    }

    @Override
    public Stream<Alias<?>> as(Scope scope, ColumnSpecifier<?> columnSpecifier, Optional<String> requiredAlias) {
        Stream<CteAlias<RT>> aliasStream = requiredAlias
            .map(a -> StreamUtil.of(columnSpecifier.referringClass())
                .flatMap(r -> as(r, a))
                .map(x -> this))
            .orElseGet(() -> StreamUtil.of(columnSpecifier.referringClass())
                .flatMap(this::as)
                .map(x -> this));
        return aliasStream.map(Function.identity());
    }

    @Override
    public TypeToken<RT> type() {
        return commonTableExpression.table().rowType();
    }

    @Override
    public Database database() {
        return commonTableExpression.table().database();
    }

    @Override
    public <T> Optional<ProjectionColumn<T>> findColumn(Scope scope, ColumnSpecifier<T> method) {
        return Optional.empty();
    }

    @Override
    public <P> Optional<ForeignKeyReference<RT,P>> foreignKey(Alias<P> parent, Optional<String> name) {
        return Optional.empty();
    }

    @Override
    public Stream<ProjectionColumn<?>> projectionColumns(Scope scope) {
        return commonTableExpression.table()
            .columns()
            .map(this::makeColumn);
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return Stream.empty();
    }

    private <T> ProjectionColumn<T> makeColumn(Column<T,RT> c) {
        return new ProjectionColumn<>(c.type(), c.columnName(), inSelectClauseLabel(c.columnName()), c.rowMapperFactory(this, Optional.empty()));
    }

    private <T> Column<T,RT> column(MethodInfo<RT,T> method) {
        return commonTableExpression.table().column(method);
    }

    @Override
    protected boolean isDual() {
        return false;
    }

    @Override
    public String withoutAlias() {
        return commonTableExpression.name();
    }

    @Override
    public String inFromClauseSql() {
        return aliasName()
            .map(a -> String.format("%s %s", commonTableExpression.name(), a))
            .orElseGet(commonTableExpression::name);
    }

    @Override
    public <T> String columnSql(Scope scope, ColumnSpecifier<T> columnSpecifier) {
        Column<T,RT> column = columnSpecifier.asReferringMethodInfo(type())
            .map(this::column)
            .orElseThrow(IllegalArgumentException::new);
        return column.sql(this);
    }

    @Override
    public <T> String columnSqlWithLabel(ColumnSpecifier<T> columnSpecifier, Optional<String> label) {
        Column<T,RT> column = columnSpecifier.asReferringMethodInfo(type())
            .map(this::column)
            .orElseThrow(IllegalArgumentException::new);
        return column.sqlWithLabel(this, label);
    }
}
