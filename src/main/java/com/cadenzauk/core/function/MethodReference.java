/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.core.function;

import java.io.Serializable;
import java.util.function.Function;

@FunctionalInterface
public interface MethodReference<T,U> extends Function<T,U>, Serializable {
}
