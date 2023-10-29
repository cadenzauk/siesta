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

import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.projection.AliasColumns;
import com.cadenzauk.siesta.projection.ExpressionProjection;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface Projection<R> {
    String sql(Scope scope);

    Stream<Object> args(Scope scope);

    Stream<ProjectionColumn<?>> columns(Scope scope);

    Stream<String> resultingColumnNames(Scope scope);

    <T> Optional<ProjectionColumn<T>> findColumn(Scope scope, ColumnSpecifier<T> columnSpecifier);

    RowMapperFactory<R> rowMapperFactory(Scope scope);

    Projection<R> distinct();

    List<Projection<?>> components();

    boolean includes(ColumnSpecifier<?> columnSpecifier);

    static <T> Projection<T> of(boolean distinct, TypedExpression<T> column, Optional<String> label) {
        return new ExpressionProjection<>(distinct, column, label);
    }

    static <T> Projection<T> of(TypedExpression<T> column, Optional<String> label) {
        return new ExpressionProjection<>(false, column, label);
    }

    static <R1> Projection<R1> of(boolean distinct, Alias<R1> alias) {
        return new AliasColumns<>(distinct, alias);
    }

    static <R1> Projection<R1> of(Alias<R1> alias) {
        return new AliasColumns<>(false, alias);
    }
}
