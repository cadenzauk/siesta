/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.core.function;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Function;

public interface FunctionOptional1<T,U> extends Function<T,Optional<U>>, Serializable {
}
