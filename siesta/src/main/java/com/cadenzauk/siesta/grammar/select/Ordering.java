/*
 * Copyright (c) 2024 Cadenza United Kingdom Limited
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
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.grammar.expression.UnresolvedColumn;

import java.util.Optional;

public class Ordering {
    private final Optional<String> alias;
    private final String column;
    private final Order order;

    private Ordering(Optional<String> alias, String column, Order order) {
        this.alias = alias;
        this.column = column;
        this.order = order;
    }

    public UnresolvedColumn<Object> toExpression() {
        return alias.map(a -> UnresolvedColumn.of(a, column)).orElseGet(() -> UnresolvedColumn.of(column));
    }

    public Optional<String> alias() {
        return alias;
    }

    public String column() {
        return column;
    }

    public Order order() {
        return order;
    }

    public static Ordering of(Optional<String> alias, String column, Order order) {
        return new Ordering(alias, column, order);
    }

    public static Ordering of(String alias, String column, Order order) {
        return new Ordering(Optional.of(alias), column, order);
    }

    public static Ordering of(String alias, String column) {
        return new Ordering(Optional.of(alias), column, Order.ASC);
    }

    public static Ordering of(String column, Order order) {
        return new Ordering(Optional.empty(), column, order);
    }

    public static Ordering of(String column) {
        return new Ordering(Optional.empty(), column, Order.ASC);
    }
}
