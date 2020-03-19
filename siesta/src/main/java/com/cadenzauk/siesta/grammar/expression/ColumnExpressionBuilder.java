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

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.siesta.ProjectionColumn;
import com.cadenzauk.siesta.Scope;

import java.util.Optional;
import java.util.function.Function;

public class ColumnExpressionBuilder<T, R, N> extends ExpressionBuilder<T, N> {
    private final ColumnExpression<T> lhs;

    private ColumnExpressionBuilder(ColumnExpression<T> lhs, Function<BooleanExpression,N> onComplete) {
        super(lhs, onComplete);
        this.lhs = lhs;
    }

    @Override
    public ProjectionColumn<T> toProjectionColumn(Scope scope, Optional<String> label) {
        return lhs.toProjectionColumn(scope, label);
    }

    @Override
    public String sqlWithLabel(Scope scope, Optional<String> label) {
        return lhs.sqlWithLabel(scope, label);
    }

    public <C> ColumnExpressionBuilder<C,R,N> dot(Function1<T,C> field) {
        return new ColumnExpressionBuilder<>(new ChainExpression<>(lhs, MethodInfo.of(field)), onComplete());
    }

    public <C> ColumnExpressionBuilder<C,R,N> dot(FunctionOptional1<T,C> field) {
        return new ColumnExpressionBuilder<>(new ChainExpression<>(lhs, MethodInfo.of(field)), onComplete());
    }

    public static <T, R, N> ColumnExpressionBuilder<T,R,N> of(ColumnExpression<T> column, Function<BooleanExpression,N> onComplete) {
        return new ColumnExpressionBuilder<>(column, onComplete);
    }
}
