/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta.expression;

import com.cadenzauk.siesta.*;

import java.util.stream.Stream;

public class CompleteExpression<T> implements Expression {
    private final TypedExpression<T> lhs;
    private final Condition<T> rhs;

    public CompleteExpression(TypedExpression<T> lhs, Condition<T> rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public String sql(Scope scope) {
        return lhs.sql(scope) + " " + rhs.sql(scope);
    }

    @Override
    public Stream<Object> args() {
        return rhs.args();
    }
}
