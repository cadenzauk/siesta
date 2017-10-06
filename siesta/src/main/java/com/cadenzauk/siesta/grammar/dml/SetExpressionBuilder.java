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

package com.cadenzauk.siesta.grammar.dml;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.grammar.expression.ResolvedColumn;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.grammar.expression.UnresolvedColumn;

import static com.cadenzauk.siesta.grammar.expression.TypedExpression.value;

public class SetExpressionBuilder<T,U> extends InSetExpectingWhere<T> {
    private final Update<T> statement;

    SetExpressionBuilder(Update<T> statement) {
        super(statement);
        this.statement = statement;
    }

    public SetExpressionBuilder<T,U> plus(U val) {
        statement.plus(value(val));
        return this;
    }

    public SetExpressionBuilder<T,U> plus(TypedExpression<U> val) {
        statement.plus(val);
        return this;
    }

    public <R> SetExpressionBuilder<T,U> plus(Function1<R,U> val) {
        statement.plus(UnresolvedColumn.of(val));
        return this;
    }

    public <R> SetExpressionBuilder<T,U> plus(FunctionOptional1<R,U> val) {
        statement.plus(UnresolvedColumn.of(val));
        return this;
    }

    public <R> SetExpressionBuilder<T,U> plus(String alias, Function1<R,U> val) {
        statement.plus(UnresolvedColumn.of(alias, val));
        return this;
    }

    public <R> SetExpressionBuilder<T,U> plus(String alias, FunctionOptional1<R,U> val) {
        statement.plus(UnresolvedColumn.of(alias, val));
        return this;
    }

    public <R> SetExpressionBuilder<T,U> plus(Alias<R> alias, Function1<R,U> val) {
        statement.plus(ResolvedColumn.of(alias, val));
        return this;
    }

    public <R> SetExpressionBuilder<T,U> plus(Alias<R> alias, FunctionOptional1<R,U> val) {
        statement.plus(ResolvedColumn.of(alias, val));
        return this;
    }

    public SetExpressionBuilder<T,U> minus(U val) {
        statement.minus(value(val));
        return this;
    }

    public SetExpressionBuilder<T,U> minus(TypedExpression<U> val) {
        statement.minus(val);
        return this;
    }

    public <R> SetExpressionBuilder<T,U> minus(Function1<R,U> val) {
        statement.minus(UnresolvedColumn.of(val));
        return this;
    }

    public <R> SetExpressionBuilder<T,U> minus(FunctionOptional1<R,U> val) {
        statement.minus(UnresolvedColumn.of(val));
        return this;
    }

    public <R> SetExpressionBuilder<T,U> minus(String alias, Function1<R,U> val) {
        statement.minus(UnresolvedColumn.of(alias, val));
        return this;
    }

    public <R> SetExpressionBuilder<T,U> minus(String alias, FunctionOptional1<R,U> val) {
        statement.minus(UnresolvedColumn.of(alias, val));
        return this;
    }

    public <R> SetExpressionBuilder<T,U> minus(Alias<R> alias, Function1<R,U> val) {
        statement.minus(ResolvedColumn.of(alias, val));
        return this;
    }

    public <R> SetExpressionBuilder<T,U> minus(Alias<R> alias, FunctionOptional1<R,U> val) {
        statement.minus(ResolvedColumn.of(alias, val));
        return this;
    }

    public SetExpressionBuilder<T,U> times(U val) {
        statement.times(value(val));
        return this;
    }

    public SetExpressionBuilder<T,U> times(TypedExpression<U> val) {
        statement.times(val);
        return this;
    }

    public <R> SetExpressionBuilder<T,U> times(Function1<R,U> val) {
        statement.times(UnresolvedColumn.of(val));
        return this;
    }

    public <R> SetExpressionBuilder<T,U> times(FunctionOptional1<R,U> val) {
        statement.times(UnresolvedColumn.of(val));
        return this;
    }

    public <R> SetExpressionBuilder<T,U> times(String alias, Function1<R,U> val) {
        statement.times(UnresolvedColumn.of(alias, val));
        return this;
    }

    public <R> SetExpressionBuilder<T,U> times(String alias, FunctionOptional1<R,U> val) {
        statement.times(UnresolvedColumn.of(alias, val));
        return this;
    }

    public <R> SetExpressionBuilder<T,U> times(Alias<R> alias, Function1<R,U> val) {
        statement.times(ResolvedColumn.of(alias, val));
        return this;
    }

    public <R> SetExpressionBuilder<T,U> times(Alias<R> alias, FunctionOptional1<R,U> val) {
        statement.times(ResolvedColumn.of(alias, val));
        return this;
    }

    public SetExpressionBuilder<T,U> dividedBy(U val) {
        statement.dividedBy(value(val));
        return this;
    }

    public SetExpressionBuilder<T,U> dividedBy(TypedExpression<U> val) {
        statement.dividedBy(val);
        return this;
    }

    public <R> SetExpressionBuilder<T,U> dividedBy(Function1<R,U> val) {
        statement.dividedBy(UnresolvedColumn.of(val));
        return this;
    }

    public <R> SetExpressionBuilder<T,U> dividedBy(FunctionOptional1<R,U> val) {
        statement.dividedBy(UnresolvedColumn.of(val));
        return this;
    }

    public <R> SetExpressionBuilder<T,U> dividedBy(String alias, Function1<R,U> val) {
        statement.dividedBy(UnresolvedColumn.of(alias, val));
        return this;
    }

    public <R> SetExpressionBuilder<T,U> dividedBy(String alias, FunctionOptional1<R,U> val) {
        statement.dividedBy(UnresolvedColumn.of(alias, val));
        return this;
    }

    public <R> SetExpressionBuilder<T,U> dividedBy(Alias<R> alias, Function1<R,U> val) {
        statement.dividedBy(ResolvedColumn.of(alias, val));
        return this;
    }

    public <R> SetExpressionBuilder<T,U> dividedBy(Alias<R> alias, FunctionOptional1<R,U> val) {
        statement.dividedBy(ResolvedColumn.of(alias, val));
        return this;
    }

}
