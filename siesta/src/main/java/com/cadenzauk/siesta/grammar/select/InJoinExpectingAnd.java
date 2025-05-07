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

package com.cadenzauk.siesta.grammar.select;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.grammar.expression.BooleanExpression;
import com.cadenzauk.siesta.grammar.expression.ExpressionBuilder;
import com.cadenzauk.siesta.grammar.expression.Label;
import com.cadenzauk.siesta.grammar.expression.ResolvedColumn;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.grammar.expression.UnresolvedColumn;

public abstract class InJoinExpectingAnd<S extends InJoinExpectingAnd<S,RT>, RT> extends ExpectingSelect<RT> {
    protected InJoinExpectingAnd(SelectStatement<RT> statement) {
        super(statement);
    }

    public S and(BooleanExpression expression) {
        return onAnd(expression);
    }

    public <T> ExpressionBuilder<T,S> and(TypedExpression<T> lhs) {
        return ExpressionBuilder.of(lhs, this::onAnd);
    }

    public <T> ExpressionBuilder<T,S> and(Label<T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::onAnd);
    }

    public <T> ExpressionBuilder<T,S> and(String alias, Label<T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::onAnd);
    }

    public <T, R> ExpressionBuilder<T,S> and(Function1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::onAnd);
    }

    public <T, R> ExpressionBuilder<T,S> and(FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::onAnd);
    }

    public <T, R> ExpressionBuilder<T,S> and(String alias, Function1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::onAnd);
    }

    public <T, R> ExpressionBuilder<T,S> and(String alias, FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::onAnd);
    }

    public <T, R> ExpressionBuilder<T,S> and(Alias<R> alias, Function1<R,T> lhs) {
        return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), this::onAnd);
    }

    public <T, R> ExpressionBuilder<T,S> and(Alias<R> alias, FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), this::onAnd);
    }

    public S or(BooleanExpression expression) {
        return onOr(expression);
    }

    public <T> ExpressionBuilder<T,S> or(TypedExpression<T> lhs) {
        return ExpressionBuilder.of(lhs, this::onOr);
    }

    public <T> ExpressionBuilder<T,S> or(Label<T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::onOr);
    }

    public <T> ExpressionBuilder<T,S> or(String alias, Label<T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::onOr);
    }

    public <T, R> ExpressionBuilder<T,S> or(Function1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::onOr);
    }

    public <T, R> ExpressionBuilder<T,S> or(FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::onOr);
    }

    public <T, R> ExpressionBuilder<T,S> or(String alias, Function1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::onOr);
    }

    public <T, R> ExpressionBuilder<T,S> or(String alias, FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::onOr);
    }

    public <T, R> ExpressionBuilder<T,S> or(Alias<R> alias, Function1<R,T> lhs) {
        return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), this::onOr);
    }

    public <T, R> ExpressionBuilder<T,S> or(Alias<R> alias, FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), this::onOr);
    }

    @SuppressWarnings("unchecked")
    private S onAnd(BooleanExpression rhs) {
        statement.from().appendAnd(rhs);
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    private S onOr(BooleanExpression rhs) {
        statement.from().appendOr(rhs);
        return (S) this;
    }
}
