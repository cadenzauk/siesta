/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.core.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Supplier;

public class ClassUtil {
    public static Optional<Method> declaredMethod(Class<?> klass, String name, Class<?>... parameterTypes) {
        try {
            return Optional.of(klass.getDeclaredMethod(name, parameterTypes));
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    public static Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<Field> declaredField(Class<?> klass, String fieldName) {
        try {
            return Optional.of(klass.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            return Optional.empty();
        }
    }

    static <T> Constructor<T> constructor(Class<T> klass) {
        try {
            return klass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(klass + " does not have a constructor with no arguments.");
        }
    }

    public static <A extends Annotation, T> Optional<A> getAnnotation(Class<A> annotationClass, Class<T> targetClass) {
        return Optional.ofNullable(targetClass.getAnnotation(annotationClass));
    }
}
