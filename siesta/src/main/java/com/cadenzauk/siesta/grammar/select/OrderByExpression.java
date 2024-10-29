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

import com.cadenzauk.siesta.Order;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;

import java.util.stream.Stream;

public class OrderByExpression<T> implements OrderingClause {
    private final TypedExpression<T> expression;
    private final Order order;

    public OrderByExpression(TypedExpression<T> expression, Order order) {
        this.expression = expression;
        this.order = order;
    }

    public String sql(Scope scope) {
        return expression.sql(scope) + " " + order.sql(scope);
    }

    public Stream<Object> args(Scope scope) {
        return expression.args(scope);
    }
}
