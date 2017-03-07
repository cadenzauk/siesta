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

package com.cadenzauk.siesta.grammar.update;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.expression.ResolvedColumn;
import com.cadenzauk.siesta.expression.TypedExpression;
import com.cadenzauk.siesta.expression.UnresolvedColumn;
import com.cadenzauk.siesta.grammar.ExpressionBuilder;

public class SetClause<U> extends Clause<U> {
    public SetClause(UpdateStatement<U> statement) {
        super(statement);
    }

    public <T> SetExpressionBuilder<T,SetClause<U>> set(Function1<U,T> lhs) {
        return SetExpressionBuilder.of(UnresolvedColumn.of(lhs), statement::addSet);
    }

    public <T> SetExpressionBuilder<T,SetClause<U>> set(FunctionOptional1<U,T> lhs) {
        return SetExpressionBuilder.of(UnresolvedColumn.of(lhs), statement::addSet);
    }

    public <T> ExpressionBuilder<T,WhereClause<U>> where(TypedExpression<T> lhs) {
        return ExpressionBuilder.of(lhs, statement::setWhereClause);
    }

    public <T, R> ExpressionBuilder<T,WhereClause<U>> where(Function1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(lhs), statement::setWhereClause);
    }

    public <T, R> ExpressionBuilder<T,WhereClause<U>> where(FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(lhs), statement::setWhereClause);
    }

    public <T, R> ExpressionBuilder<T,WhereClause<U>> where(String alias, Function1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), statement::setWhereClause);
    }

    public <T, R> ExpressionBuilder<T,WhereClause<U>> where(String alias, FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), statement::setWhereClause);
    }

    public <T, R> ExpressionBuilder<T,WhereClause<U>> where(Alias<R> alias, Function1<R,T> lhs) {
        return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), statement::setWhereClause);
    }

    public <T, R> ExpressionBuilder<T,WhereClause<U>> where(Alias<R> alias, FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), statement::setWhereClause);
    }

}
