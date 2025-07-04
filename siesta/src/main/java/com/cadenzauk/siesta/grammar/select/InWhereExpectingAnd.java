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
import com.cadenzauk.siesta.grammar.expression.ColumnExpressionBuilder;
import com.cadenzauk.siesta.grammar.expression.ExpressionBuilder;
import com.cadenzauk.siesta.grammar.expression.Label;
import com.cadenzauk.siesta.grammar.expression.ResolvedColumn;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.grammar.expression.UnresolvedColumn;

public class InWhereExpectingAnd<RT> extends ExpectingGroupBy<RT> {
    public InWhereExpectingAnd(SelectStatement<RT> statement) {
        super(statement);
    }

    public InWhereExpectingAnd<RT> and(BooleanExpression expression) {
        return andWhere(expression);
    }

    public <T> ExpressionBuilder<T,InWhereExpectingAnd<RT>> and(TypedExpression<T> lhs) {
        return ExpressionBuilder.of(lhs, this::andWhere);
    }

    public <T> ExpressionBuilder<T,InWhereExpectingAnd<RT>> and(Label<T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::andWhere);
    }

    public <T> ExpressionBuilder<T,InWhereExpectingAnd<RT>> and(String alias, Label<T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::andWhere);
    }

    public <T, R> ColumnExpressionBuilder<T,R,InWhereExpectingAnd<RT>> and(Function1<R,T> lhs) {
        return ColumnExpressionBuilder.of(UnresolvedColumn.of(lhs), this::andWhere);
    }

    public <T, R> ColumnExpressionBuilder<T,R,InWhereExpectingAnd<RT>> and(FunctionOptional1<R,T> lhs) {
        return ColumnExpressionBuilder.of(UnresolvedColumn.of(lhs), this::andWhere);
    }

    public <T, R> ColumnExpressionBuilder<T,R,InWhereExpectingAnd<RT>> and(String alias, Function1<R,T> lhs) {
        return ColumnExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::andWhere);
    }

    public <T, R> ColumnExpressionBuilder<T,R,InWhereExpectingAnd<RT>> and(String alias, FunctionOptional1<R,T> lhs) {
        return ColumnExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::andWhere);
    }

    public <T, R> ColumnExpressionBuilder<T,R,InWhereExpectingAnd<RT>> and(Alias<R> alias, Function1<R,T> lhs) {
        return ColumnExpressionBuilder.of(ResolvedColumn.of(alias, lhs), this::andWhere);
    }

    public <T, R> ColumnExpressionBuilder<T,R,InWhereExpectingAnd<RT>> and(Alias<R> alias, FunctionOptional1<R,T> lhs) {
        return ColumnExpressionBuilder.of(ResolvedColumn.of(alias, lhs), this::andWhere);
    }

    public InWhereExpectingAnd<RT> or(BooleanExpression expression) {
        return orWhere(expression);
    }

    public <T> ExpressionBuilder<T,InWhereExpectingAnd<RT>> or(TypedExpression<T> lhs) {
        return ExpressionBuilder.of(lhs, this::orWhere);
    }

    public <T> ExpressionBuilder<T,InWhereExpectingAnd<RT>> or(Label<T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::orWhere);
    }

    public <T> ExpressionBuilder<T,InWhereExpectingAnd<RT>> or(String alias, Label<T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::orWhere);
    }

    public <T, R> ColumnExpressionBuilder<T,R,InWhereExpectingAnd<RT>> or(Function1<R,T> lhs) {
        return ColumnExpressionBuilder.of(UnresolvedColumn.of(lhs), this::orWhere);
    }

    public <T, R> ColumnExpressionBuilder<T,R,InWhereExpectingAnd<RT>> or(FunctionOptional1<R,T> lhs) {
        return ColumnExpressionBuilder.of(UnresolvedColumn.of(lhs), this::orWhere);
    }

    public <T, R> ColumnExpressionBuilder<T,R,InWhereExpectingAnd<RT>> or(String alias, Function1<R,T> lhs) {
        return ColumnExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::orWhere);
    }

    public <T, R> ColumnExpressionBuilder<T,R,InWhereExpectingAnd<RT>> or(String alias, FunctionOptional1<R,T> lhs) {
        return ColumnExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::orWhere);
    }

    public <T, R> ColumnExpressionBuilder<T,R,InWhereExpectingAnd<RT>> or(Alias<R> alias, Function1<R,T> lhs) {
        return ColumnExpressionBuilder.of(ResolvedColumn.of(alias, lhs), this::orWhere);
    }

    public <T, R> ColumnExpressionBuilder<T,R,InWhereExpectingAnd<RT>> or(Alias<R> alias, FunctionOptional1<R,T> lhs) {
        return ColumnExpressionBuilder.of(ResolvedColumn.of(alias, lhs), this::orWhere);
    }

    private InWhereExpectingAnd<RT> andWhere(BooleanExpression newClause) {
        statement.andWhere(newClause);
        return this;
    }

    private InWhereExpectingAnd<RT> orWhere(BooleanExpression newClause) {
        statement.orWhere(newClause);
        return this;
    }
}
