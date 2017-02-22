/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.siesta.expression.condition;

import com.cadenzauk.siesta.Condition;
import com.cadenzauk.siesta.Scope;

import java.util.Optional;
import java.util.stream.Stream;

public class OperatorValueCondition<T, R> implements Condition<T> {
    private final String operator;
    private final T value;
    private final Optional<Double> selectivity;

    public OperatorValueCondition(String operator, T value, Optional<Double> selectivity) {
        this.operator = operator;
        this.value = value;
        this.selectivity = selectivity;
    }

    @Override
    public String sql(Scope scope) {
        return operator + " ?" + selectivity.map(scope.database().dialect()::selectivity).orElse("");
    }

    @Override
    public Stream<Object> args() {
        return Stream.of(value);
    }
}
