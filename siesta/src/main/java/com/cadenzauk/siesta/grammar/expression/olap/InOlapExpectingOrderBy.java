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
import com.cadenzauk.siesta.grammar.select.Ordering;

public class InOlapExpectingOrderBy<T> extends InOlapFunction<T> {
    InOlapExpectingOrderBy(OlapFunction<T> function) {
        super(function);
    }

    public <V> InOlapOrderBy<T> orderBy(TypedExpression<V> expression) {
        function.addOrderBy(new Ordering<>(expression, Order.ASC));
        return new InOlapOrderBy<>(function);
    }

    public <V> InOlapOrderBy<T> orderBy(Label<V> label) {
        function.addOrderBy(new Ordering<>(UnresolvedColumn.of(label), Order.ASC));
        return new InOlapOrderBy<>(function);
    }

    public <R, V> InOlapOrderBy<T> orderBy(Function1<R,V> method) {
        function.addOrderBy(new Ordering<>(UnresolvedColumn.of(method), Order.ASC));
        return new InOlapOrderBy<>(function);
    }

    public <R, V> InOlapOrderBy<T> orderBy(FunctionOptional1<R,V> method) {
        function.addOrderBy(new Ordering<>(UnresolvedColumn.of(method), Order.ASC));
        return new InOlapOrderBy<>(function);
    }

    public <R, V> InOlapOrderBy<T> orderBy(String alias, Function1<R,V> method) {
        function.addOrderBy(new Ordering<>(UnresolvedColumn.of(alias, method), Order.ASC));
        return new InOlapOrderBy<>(function);
    }

    public <R, V> InOlapOrderBy<T> orderBy(String alias, FunctionOptional1<R,V> method) {
        function.addOrderBy(new Ordering<>(UnresolvedColumn.of(alias, method), Order.ASC));
        return new InOlapOrderBy<>(function);
    }

    public <R, V> InOlapOrderBy<T> orderBy(Alias<R> alias, Function1<R,V> method) {
        function.addOrderBy(new Ordering<>(ResolvedColumn.of(alias, method), Order.ASC));
        return new InOlapOrderBy<>(function);
    }

    public <R, V> InOlapOrderBy<T> orderBy(Alias<R> alias, FunctionOptional1<R,V> method) {
        function.addOrderBy(new Ordering<>(ResolvedColumn.of(alias, method), Order.ASC));
        return new InOlapOrderBy<>(function);
    }

    public <V> InOlapOrderBy<T> orderBy(TypedExpression<V> expression, Order order) {
        function.addOrderBy(new Ordering<>(expression, order));
        return new InOlapOrderBy<>(function);
    }

    public <V> InOlapOrderBy<T> orderBy(String columnName, Order order) {
        function.addOrderBy(new Ordering<>(UnresolvedColumn.of(columnName), order));
        return new InOlapOrderBy<>(function);
    }

    public <V> InOlapOrderBy<T> orderBy(Label<V> label, Order order) {
        function.addOrderBy(new Ordering<>(UnresolvedColumn.of(label), order));
        return new InOlapOrderBy<>(function);
    }

    public <R, V> InOlapOrderBy<T> orderBy(Function1<R,V> method, Order order) {
        function.addOrderBy(new Ordering<>(UnresolvedColumn.of(method), order));
        return new InOlapOrderBy<>(function);
    }

    public <R, V> InOlapOrderBy<T> orderBy(FunctionOptional1<R,V> method, Order order) {
        function.addOrderBy(new Ordering<>(UnresolvedColumn.of(method), order));
        return new InOlapOrderBy<>(function);
    }

    public <R, V> InOlapOrderBy<T> orderBy(String alias, Function1<R,V> method, Order order) {
        function.addOrderBy(new Ordering<>(UnresolvedColumn.of(alias, method), order));
        return new InOlapOrderBy<>(function);
    }

    public <R, V> InOlapOrderBy<T> orderBy(String alias, FunctionOptional1<R,V> method, Order order) {
        function.addOrderBy(new Ordering<>(UnresolvedColumn.of(alias, method), order));
        return new InOlapOrderBy<>(function);
    }

    public <R, V> InOlapOrderBy<T> orderBy(Alias<R> alias, Function1<R,V> method, Order order) {
        function.addOrderBy(new Ordering<>(ResolvedColumn.of(alias, method), order));
        return new InOlapOrderBy<>(function);
    }

    public <R, V> InOlapOrderBy<T> orderBy(Alias<R> alias, FunctionOptional1<R,V> method, Order order) {
        function.addOrderBy(new Ordering<>(ResolvedColumn.of(alias, method), order));
        return new InOlapOrderBy<>(function);
    }

}
