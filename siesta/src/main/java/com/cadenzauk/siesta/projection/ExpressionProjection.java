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

package com.cadenzauk.siesta.projection;

import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.siesta.ColumnSpecifier;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.ProjectionColumn;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ExpressionProjection<T> implements Projection<T> {
    private final boolean distinct;
    private final TypedExpression<T> expression;
    private final Optional<String> label;

    public ExpressionProjection(boolean distinct, TypedExpression<T> expression, Optional<String> label) {
        this.distinct = distinct;
        this.expression = expression;
        this.label = label;
    }

    @Override
    public String sql(Scope scope) {
        return (distinct ? "distinct " : "") + expression.sqlWithLabel(scope, label);
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return expression.args(scope);
    }

    @Override
    public Stream<ProjectionColumn<?>> columns(Scope scope) {
        return Stream.of(projectionColumn(scope));
    }

    private ProjectionColumn<T> projectionColumn(Scope scope) {
        return new ProjectionColumn<>(expression.type(), expression.sql(scope), label.orElseGet(() -> expression.label(scope)), expression.rowMapperFactory(scope, label));
    }

    @Override
    public <T2> Optional<ProjectionColumn<T2>> findColumn(Scope scope, ColumnSpecifier<T2> columnSpecifier) {
        return projectionColumn(scope)
            .as(columnSpecifier.effectiveClass())
            .filter(x -> includes(columnSpecifier))
            .findAny();
    }

    @Override
    public RowMapperFactory<T> rowMapperFactory(Scope scope) {
        return expression.rowMapperFactory(scope, label);
    }

    @Override
    public Projection<T> distinct() {
        return new ExpressionProjection<>(true, expression, label);
    }

    @Override
    public List<Projection<?>> components() {
        return ImmutableList.of(this);
    }

    @Override
    public boolean includes(ColumnSpecifier<?> columnSpecifier) {
        return columnSpecifier.asEffective(expression.type())
            .map(cs -> cs.isSpecificationFor(expression, label))
            .orElse(false);
    }
}
