/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.core.function;

import java.io.Serializable;
import java.util.function.BiFunction;

public interface Function2<T,U,R> extends BiFunction<T,U,R>, Serializable{
}
