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
import com.cadenzauk.siesta.Alias;

public abstract class BooleanExpression implements Expression {
    public abstract BooleanExpression appendOr(BooleanExpression expression);
    public abstract BooleanExpression appendAnd(BooleanExpression expression);

    public BooleanExpression and(BooleanExpression lhs) {
        return this.appendAnd(lhs);
    }

    public <T> ExpressionBuilder<T,BooleanExpression> and(TypedExpression<T> lhs) {
        return ExpressionBuilder.of(lhs, this::appendAnd);
    }

    public <T, R> ExpressionBuilder<T,BooleanExpression> and(Function1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::appendAnd);
    }

    public <T, R> ExpressionBuilder<T,BooleanExpression> and(FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::appendAnd);
    }

    public <T, R> ExpressionBuilder<T,BooleanExpression> and(String alias, Function1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::appendAnd);
    }

    public <T, R> ExpressionBuilder<T,BooleanExpression> and(String alias, FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::appendAnd);
    }

    public <T, R> ExpressionBuilder<T,BooleanExpression> and(Alias<R> alias, Function1<R,T> lhs) {
        return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), this::appendAnd);
    }

    public <T, R> ExpressionBuilder<T,BooleanExpression> and(Alias<R> alias, FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), this::appendAnd);
    }

    public BooleanExpression or(BooleanExpression lhs) {
        return this.appendOr(lhs);
    }

    public <T> ExpressionBuilder<T,BooleanExpression> or(TypedExpression<T> lhs) {
        return ExpressionBuilder.of(lhs, this::appendOr);
    }

    public <T, R> ExpressionBuilder<T,BooleanExpression> or(Function1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::appendOr);
    }

    public <T, R> ExpressionBuilder<T,BooleanExpression> or(FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::appendOr);
    }

    public <T, R> ExpressionBuilder<T,BooleanExpression> or(String alias, Function1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::appendOr);
    }

    public <T, R> ExpressionBuilder<T,BooleanExpression> or(String alias, FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::appendOr);
    }

    public <T, R> ExpressionBuilder<T,BooleanExpression> or(Alias<R> alias, Function1<R,T> lhs) {
        return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), this::appendOr);
    }

    public <T, R> ExpressionBuilder<T,BooleanExpression> or(Alias<R> alias, FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), this::appendOr);
    }
}
