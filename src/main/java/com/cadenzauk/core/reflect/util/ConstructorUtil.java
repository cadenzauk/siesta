/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.core.reflect.util;

import com.cadenzauk.core.util.UtilityClass;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class ConstructorUtil extends UtilityClass {
    public static <T> T newInstance(Constructor<T> ctor, Object... args) {
        try {
            ctor.setAccessible(true);
            return ctor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
