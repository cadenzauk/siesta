/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.siesta.catalog;

import com.cadenzauk.siesta.DataType;
import com.cadenzauk.siesta.RowMapper;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Column<T, R> {
    String name();

    DataType<T> dataType();

    RowMapper<T> rowMapper(String label);

    Class<R> rowClass();

    <U> Stream<Column<U,R>> as(DataType<U> requiredDataType);

    boolean primaryKey();

    Function<R,Optional<T>> getter();
}
