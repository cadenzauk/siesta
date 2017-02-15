/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta.expression;

import com.cadenzauk.siesta.Scope;
import org.springframework.jdbc.core.RowMapper;

public interface TypedExpression<T> extends Expression {
    String label(Scope scope);

    RowMapper<T> rowMapper(Scope scope, String label);
}
