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
import com.cadenzauk.siesta.Order;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;

public abstract class ExpectingOrderBy<RT> extends ExpectingEndOfStatement<RT> {
    public ExpectingOrderBy(Select<RT> select) {
        super(select);
    }

    public <T> InOrderByExpectingThen<RT> orderBy(TypedExpression<T> expression) {
        return new InOrderByExpectingThen<>(statement).then(expression);
    }

    public <T, R> InOrderByExpectingThen<RT> orderBy(Function1<R,T> columnGetter) {
        return new InOrderByExpectingThen<>(statement).then(columnGetter);
    }

    public <T, R> InOrderByExpectingThen<RT> orderBy(FunctionOptional1<R,T> columnGetter) {
        return new InOrderByExpectingThen<>(statement).then(columnGetter);
    }

    public <T, R> InOrderByExpectingThen<RT> orderBy(String alias, Function1<R,T> columnGetter) {
        return new InOrderByExpectingThen<>(statement).then(alias, columnGetter);
    }

    public <T, R> InOrderByExpectingThen<RT> orderBy(String alias, FunctionOptional1<R,T> columnGetter) {
        return new InOrderByExpectingThen<>(statement).then(alias, columnGetter);
    }

    public <T, R> InOrderByExpectingThen<RT> orderBy(Alias<R> alias, Function1<R,T> columnGetter) {
        return new InOrderByExpectingThen<>(statement).then(alias, columnGetter);
    }

    public <T, R> InOrderByExpectingThen<RT> orderBy(Alias<R> alias, FunctionOptional1<R,T> columnGetter) {
        return new InOrderByExpectingThen<>(statement).then(alias, columnGetter);
    }

    public <T> InOrderByExpectingThen<RT> orderBy(TypedExpression<T> expression, Order order) {
        return new InOrderByExpectingThen<>(statement).then(expression, order);
    }

    public <T, R> InOrderByExpectingThen<RT> orderBy(Function1<R,T> columnGetter, Order order) {
        return new InOrderByExpectingThen<>(statement).then(columnGetter, order);
    }

    public <T, R> InOrderByExpectingThen<RT> orderBy(FunctionOptional1<R,T> columnGetter, Order order) {
        return new InOrderByExpectingThen<>(statement).then(columnGetter, order);
    }

    public <T, R> InOrderByExpectingThen<RT> orderBy(String alias, Function1<R,T> columnGetter, Order order) {
        return new InOrderByExpectingThen<>(statement).then(alias, columnGetter, order);
    }

    public <T, R> InOrderByExpectingThen<RT> orderBy(String alias, FunctionOptional1<R,T> columnGetter, Order order) {
        return new InOrderByExpectingThen<>(statement).then(alias, columnGetter, order);
    }

    public <T, R> InOrderByExpectingThen<RT> orderBy(Alias<R> alias, Function1<R,T> columnGetter, Order order) {
        return new InOrderByExpectingThen<>(statement).then(alias, columnGetter, order);
    }

    public <T, R> InOrderByExpectingThen<RT> orderBy(Alias<R> alias, FunctionOptional1<R,T> columnGetter, Order order) {
        return new InOrderByExpectingThen<>(statement).then(alias, columnGetter, order);
    }
}
