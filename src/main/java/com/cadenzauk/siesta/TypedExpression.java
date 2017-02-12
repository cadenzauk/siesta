/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import org.springframework.jdbc.core.RowMapper;

import java.util.stream.Stream;

public interface TypedExpression<T> {
    String sql(Scope scope);

    String label(Scope scope);

    RowMapper<T> rowMapper(String label);

    default Stream<Object> args() {
        return Stream.empty();
    }
}
