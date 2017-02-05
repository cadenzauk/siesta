/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.core.function;

@FunctionalInterface
public interface Function3<T1, T2, T3, R> {
    R apply(T1 p1, T2 p2, T3 p3);
}
