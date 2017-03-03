/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.core.reflect;

import com.cadenzauk.core.reflect.util.ClassUtil;
import com.cadenzauk.core.reflect.util.ConstructorUtil;
import com.cadenzauk.core.util.UtilityClass;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;

public final class Factory extends UtilityClass {
    public static <T> Supplier<T> forClass(Class<T> klass) {
        return ClassUtil.constructor(klass)
            .map(Factory::invoke)
            .orElseThrow(() -> new IllegalArgumentException("No default constructor for " + klass));
    }

    private static <T> Supplier<T> invoke(Constructor<T> ctor) {
        return () -> ConstructorUtil.newInstance(ctor);
    }
}
