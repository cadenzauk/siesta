/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta.expression;

import com.cadenzauk.siesta.Scope;

import java.util.stream.Stream;

public interface Expression {
    String sql(Scope scope);

    Stream<Object> args();
}
