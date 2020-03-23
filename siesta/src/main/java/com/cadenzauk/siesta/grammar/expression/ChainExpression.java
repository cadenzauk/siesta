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
import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.AliasColumn;
import com.cadenzauk.siesta.ColumnSpecifier;
import com.cadenzauk.siesta.ProjectionColumn;
import com.cadenzauk.siesta.Scope;
import com.google.common.reflect.TypeToken;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class ChainExpression<P, T> implements ColumnExpression<T> {
    private final ColumnExpression<P> lhs;
    private final MethodInfo<P,T> field;

    public ChainExpression(ColumnExpression<P> lhs, MethodInfo<P,T> field) {
        this.lhs = lhs;
        this.field = field;
    }

    @Override
    public String label(Scope scope) {
        Alias<?> alias = resolve(scope);
        return alias.inSelectClauseLabel(column(scope).columnName());
    }

    @Override
    public RowMapperFactory<T> rowMapperFactory(Scope scope) {
        return (prefix, label) -> rs -> scope.database().getDataTypeOf(field).get(rs, prefix + label.orElseGet(() -> label(scope)), scope.database()).orElse(null);
    }

    @Override
    public ProjectionColumn<T> toProjectionColumn(Scope scope, Optional<String> label) {
        Alias<?> alias = resolve(scope);
        AliasColumn<T> column = column(scope);
        return column.toProjection(alias, label);
    }

    @Override
    public TypeToken<T> type() {
        return TypeToken.of(field.effectiveClass());
    }

    @Override
    public String sqlWithLabel(Scope scope, Optional<String> label) {
        Alias<?> alias = resolve(scope);
        return String.format("%s as %s", alias.inSelectClauseSql(columnName(scope)), label.orElseGet(() -> label(scope)));
    }

    @Override
    public String sql(Scope scope) {
        Alias<?> alias = resolve(scope);
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
        AliasColumn<T> column = column(scope);
        return column.columnName();
    }

    private AliasColumn<T> column(Scope scope) {
        return lhs.findColumn(scope, field.effectiveType(), field.propertyName())
            .orElseThrow(() -> new IllegalArgumentException("Unable to find field " + field.propertyName() + " in " + field.effectiveType()));
    }

    @Override
    public Alias<?> resolve(Scope scope) {
        return lhs.resolve(scope);
    }

    @Override
    public <V> Optional<AliasColumn<V>> findColumn(Scope scope, TypeToken<V> type, String propertyName) {
        AliasColumn<T> parent = column(scope);
        return parent.findColumn(type, propertyName);
    }

    @Override
    public <X> boolean includes(ColumnSpecifier<X> getter) {
        return getter.method()
            .map(m -> Objects.equals(field.method(), m))
            .orElse(false);
    }
}
