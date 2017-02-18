/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.siesta;

import java.util.stream.Stream;

@SuppressWarnings("unused")
public interface Condition<T> {
    String sql(Scope scope);

    Stream<Object> args();
}
