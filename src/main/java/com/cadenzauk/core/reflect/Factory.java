/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.core.reflect;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;

public class Factory {
    public static <T> Supplier<T> forClass(Class<T> c1ass) {
        Constructor<T> ctor = ClassUtil.constructor(c1ass);
        return () -> ConstructorUtil.newInstance(ctor);
    }
}
