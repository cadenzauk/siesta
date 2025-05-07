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

package com.cadenzauk.siesta.grammar.expression.olap;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Order;
import com.cadenzauk.siesta.grammar.expression.Label;
import com.cadenzauk.siesta.grammar.expression.ResolvedColumn;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.grammar.expression.UnresolvedColumn;
import com.cadenzauk.siesta.grammar.select.OrderByExpression;

public class InOlapOrderBy<T> extends InOlapFunction<T> {
    InOlapOrderBy(OlapFunction<T> function) {
        super(function);
    }

    public <V> InOlapOrderBy<T> then(TypedExpression<V> expression) {
        function.addOrderBy(new OrderByExpression<>(expression, Order.ASC));
        return this;
    }

    public <V> InOlapOrderBy<T> then(Label<V> label) {
        function.addOrderBy(new OrderByExpression<>(UnresolvedColumn.of(label), Order.ASC));
        return this;
    }

    public <V> InOlapOrderBy<T> then(String alias, Label<V> label) {
        function.addOrderBy(new OrderByExpression<>(UnresolvedColumn.of(alias, label), Order.ASC));
        return this;
    }

    public <R, V> InOlapOrderBy<T> then(Function1<R,V> method) {
        function.addOrderBy(new OrderByExpression<>(UnresolvedColumn.of(method), Order.ASC));
        return this;
    }

    public <R, V> InOlapOrderBy<T> then(FunctionOptional1<R,V> method) {
        function.addOrderBy(new OrderByExpression<>(UnresolvedColumn.of(method), Order.ASC));
        return this;
    }

    public <R, V> InOlapOrderBy<T> then(String alias, Function1<R,V> method) {
        function.addOrderBy(new OrderByExpression<>(UnresolvedColumn.of(alias, method), Order.ASC));
        return this;
    }

    public <R, V> InOlapOrderBy<T> then(String alias, FunctionOptional1<R,V> method) {
        function.addOrderBy(new OrderByExpression<>(UnresolvedColumn.of(alias, method), Order.ASC));
        return this;
    }

    public <R, V> InOlapOrderBy<T> then(Alias<R> alias, Function1<R,V> method) {
        function.addOrderBy(new OrderByExpression<>(ResolvedColumn.of(alias, method), Order.ASC));
        return this;
    }

    public <R, V> InOlapOrderBy<T> then(Alias<R> alias, FunctionOptional1<R,V> method) {
        function.addOrderBy(new OrderByExpression<>(ResolvedColumn.of(alias, method), Order.ASC));
        return this;
    }

    public <V> InOlapOrderBy<T> then(TypedExpression<V> expression, Order order) {
        function.addOrderBy(new OrderByExpression<>(expression, order));
        return this;
    }

    public <V> InOlapOrderBy<T> then(Label<V> label, Order order) {
        function.addOrderBy(new OrderByExpression<>(UnresolvedColumn.of(label), order));
        return this;
    }

    public <V> InOlapOrderBy<T> then(String alias, Label<V> label, Order order) {
        function.addOrderBy(new OrderByExpression<>(UnresolvedColumn.of(alias, label), order));
        return this;
    }

    public <R, V> InOlapOrderBy<T> then(Function1<R,V> method, Order order) {
        function.addOrderBy(new OrderByExpression<>(UnresolvedColumn.of(method), order));
        return this;
    }

    public <R, V> InOlapOrderBy<T> then(FunctionOptional1<R,V> method, Order order) {
        function.addOrderBy(new OrderByExpression<>(UnresolvedColumn.of(method), order));
        return this;
    }

    public <R, V> InOlapOrderBy<T> then(String alias, Function1<R,V> method, Order order) {
        function.addOrderBy(new OrderByExpression<>(UnresolvedColumn.of(alias, method), order));
        return this;
    }

    public <R, V> InOlapOrderBy<T> then(String alias, FunctionOptional1<R,V> method, Order order) {
        function.addOrderBy(new OrderByExpression<>(UnresolvedColumn.of(alias, method), order));
        return this;
    }

    public <R, V> InOlapOrderBy<T> then(Alias<R> alias, Function1<R,V> method, Order order) {
        function.addOrderBy(new OrderByExpression<>(ResolvedColumn.of(alias, method), order));
        return this;
    }

    public <R, V> InOlapOrderBy<T> then(Alias<R> alias, FunctionOptional1<R,V> method, Order order) {
        function.addOrderBy(new OrderByExpression<>(ResolvedColumn.of(alias, method), order));
        return this;
    }

}
