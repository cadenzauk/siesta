/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta.expression.condition;

import com.cadenzauk.siesta.Condition;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.expression.TypedExpression;

import java.util.stream.Stream;

public class OperatorExpressionCondition<T> implements Condition<T> {
    private final String operator;
    private final TypedExpression<T> expression;

    public OperatorExpressionCondition(String operator, TypedExpression<T> expression) {
        this.operator = operator;
        this.expression = expression;
    }

    @Override
    public String sql(Scope scope) {
        return operator + " " + expression.sql(scope);
    }

    @Override
    public Stream<Object> args() {
        return expression.args();
    }
}
