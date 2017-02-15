/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta.expression;

import com.cadenzauk.siesta.Scope;

import java.util.stream.Stream;

public class AndExpression implements Expression {
    private final Expression lhs;
    private final Expression rhs;

    public AndExpression(Expression lhs, Expression rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public String sql(Scope scope) {
        return String.format("(%s) and (%s)", lhs.sql(scope), rhs.sql(scope));
    }

    @Override
    public Stream<Object> args() {
        return Stream.concat(lhs.args(), rhs.args());
    }
}
