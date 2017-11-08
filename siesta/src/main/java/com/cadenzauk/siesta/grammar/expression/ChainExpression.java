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

package com.cadenzauk.siesta.grammar.expression;

import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.catalog.Column;
import com.google.common.reflect.TypeToken;

import java.util.Optional;
import java.util.stream.Stream;

public class ChainExpression<P, T, R> implements ColumnExpression<T,R> {
    private final ColumnExpression<P,R> lhs;
    private final MethodInfo<P,T> field;

    public ChainExpression(ColumnExpression<P,R> lhs, MethodInfo<P,T> field) {
        this.lhs = lhs;
        this.field = field;
    }

    @Override
    public String label(Scope scope) {
        Alias<R> alias = resolve(scope);
        return alias.inSelectClauseLabel(column(scope).columnName());
    }

    @Override
    public RowMapper<T> rowMapper(Scope scope, Optional<String> label) {
        return rs -> scope.database().getDataTypeOf(field).get(rs, label.orElseGet(() -> label(scope)), scope.database()).orElse(null);
    }

    @Override
    public TypeToken<T> type() {
        return TypeToken.of(field.effectiveClass());
    }

    @Override
    public String sqlWithLabel(Scope scope, Optional<String> label) {
        Alias<R> alias = resolve(scope);
        return String.format("%s as %s", alias.inSelectClauseSql(columnName(scope)), label.orElseGet(() -> label(scope)));
    }

    @Override
    public String sql(Scope scope) {
        Alias<R> alias = resolve(scope);
        return alias.inSelectClauseSql(columnName(scope));
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return Stream.empty();
    }

    @Override
    public Precedence precedence() {
        return Precedence.COLUMN;
    }

    @Override
    public String columnName(Scope scope) {
        Column<T,P> column = column(scope);
        return column.columnName();
    }

    private Column<T,P> column(Scope scope) {
        return lhs.findColumn(scope, field.effectiveType(), field.propertyName())
            .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public Alias<R> resolve(Scope scope) {
        return lhs.resolve(scope);
    }

    @Override
    public <V> Optional<Column<V,T>> findColumn(Scope scope, TypeToken<V> type, String propertyName) {
        Column<T,P> parent = column(scope);
        return parent.findColumn(type, propertyName);
    }
}
