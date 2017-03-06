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

import com.cadenzauk.siesta.From;
import com.cadenzauk.siesta.Order;
import com.cadenzauk.siesta.SqlExecutor;
import com.cadenzauk.siesta.expression.Expression;
import com.cadenzauk.siesta.expression.TypedExpression;

import java.util.List;
import java.util.Optional;

public interface SelectStatement<RT> extends TypedExpression<RT> {
    List<RT> list(SqlExecutor sqlExecutor);

    Optional<RT> optional(SqlExecutor sqlExecutor);

    String sql();

    void andWhere(Expression newClause);

    WhereClause<RT> setWhereClause(Expression e);

    From from();

    <T> void addOrderBy(TypedExpression<T> column, Order ascending);
}
