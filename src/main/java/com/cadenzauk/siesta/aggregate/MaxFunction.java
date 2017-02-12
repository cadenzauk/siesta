/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta.aggregate;

import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.TypedExpression;
import org.springframework.jdbc.core.RowMapper;

public class MaxFunction<T> implements TypedExpression<T> {
    private final TypedExpression<T> arg;

    public MaxFunction(TypedExpression<T> arg) {
        this.arg = arg;
    }

    @Override
    public String sql(Scope scope) {
        return String.format("max(%s)", arg.sql(scope));
    }

    @Override
    public String label(Scope scope) {
        return String.format("max_%s", arg.label(scope));
    }

    @Override
    public RowMapper<T> rowMapper(String label) {
        return arg.rowMapper(label);
    }

}
