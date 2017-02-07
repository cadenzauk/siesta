/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.core.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

public class ClassUtil {
    public static Optional<Method> getDeclaredMethod(Class<?> klass, String name, Class<?>... parameterTypes) {
        try {
            return Optional.of(klass.getDeclaredMethod(name, parameterTypes));
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    public static Optional<Field> getDeclaredField(Class<?> klass, String fieldName) {
        try {
            return Optional.of(klass.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            return Optional.empty();
        }
    }
}
