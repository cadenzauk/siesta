/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta.condition;

import com.cadenzauk.siesta.Condition;
import com.cadenzauk.siesta.Scope;

import java.util.stream.Stream;

public class OperatorValueCondition<T, R> implements Condition<T> {
    private final String operator;
    private final T value;

    public OperatorValueCondition(String operator, T value) {
        this.operator = operator;
        this.value = value;
    }

    @Override
    public String sql(Scope scope) {
        return operator + " ?";
    }

    @Override
    public Stream<Object> args() {
        return Stream.of(value);
    }
}
