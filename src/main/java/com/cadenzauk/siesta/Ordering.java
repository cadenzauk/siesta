/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

public class Ordering<T, R> {
    private final TypedExpression<T> expression;
    private final Order order;

    public Ordering(TypedExpression<T> expression, Order order) {
        this.expression = expression;
        this.order = order;
    }

    public String sql(Scope scope) {
        return expression.sql(scope) + " " + order.sql();
    }
}
