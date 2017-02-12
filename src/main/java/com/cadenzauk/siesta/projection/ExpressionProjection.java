/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta.projection;

import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.TypedExpression;

public class ExpressionProjection<T> implements Projection {
    private final TypedExpression<T> expression;

    public ExpressionProjection(TypedExpression<T> expression) {
        this.expression = expression;
    }

    @Override
    public String sql(Scope scope) {
        return String.format("%s as %s", expression.sql(scope), expression.label(scope));
    }
}
