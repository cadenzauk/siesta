/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

@FunctionalInterface
public interface TestSupplier<T> {
    Condition<T> get(Scope scope);
}
