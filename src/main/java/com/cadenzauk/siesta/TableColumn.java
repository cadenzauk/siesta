/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import java.util.Optional;
import java.util.function.Function;

public interface TableColumn<T, R> {
    String name();
    DataType<T> dataType();
    Class<R> rowClass();
    boolean primaryKey();
    Function<R, Optional<T>> getter();
}
