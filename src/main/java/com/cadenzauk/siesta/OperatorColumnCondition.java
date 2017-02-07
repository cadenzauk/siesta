/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import java.util.stream.Stream;

public class OperatorColumnCondition<T, R> implements Condition<T> {
    private final String operator;
    private final Alias<R> alias;
    private final Column<T,R> column;

    public OperatorColumnCondition(String operator, Alias<R> alias, Column<T,R> column) {
        this.operator = operator;
        this.alias = alias;
        this.column = column;
    }

    @Override
    public String sql() {
        return operator + " " + alias.inExpression(column);
    }

    @Override
    public Stream<Object> args() {
        return Stream.empty();
    }
}
