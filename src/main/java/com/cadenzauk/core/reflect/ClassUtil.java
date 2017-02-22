/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.core.reflect;

import com.cadenzauk.core.stream.StreamUtil;
import com.cadenzauk.core.util.OptionalUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Stream;

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

    public static Optional<Field> findField(Class<?> klass, String fieldName) {
        return superclasses(klass)
            .map(cls -> declaredField(cls, fieldName))
            .flatMap(StreamUtil::of)
            .findFirst();
    }

    public static Optional<Class<?>> superclass(Class<?> klass) {
        return Optional.ofNullable(klass.getSuperclass());
    }

    public static Stream<Class<?>> superclasses(Class<?> klass) {
        return Stream.concat(Stream.of(klass), superclass(klass).map(ClassUtil::superclasses).orElseGet(Stream::empty));
    }

    public static <A extends Annotation, T> boolean hasAnnotation(Class<?> klass, Class<A> annotationClass) {
        return klass.getAnnotation(annotationClass) != null;
    }

    public static <A extends Annotation, T> Optional<A> annotation(Class<T> klass, Class<A> annotationClass) {
        return Optional.ofNullable(klass.getAnnotation(annotationClass));
    }

    static <T> Constructor<T> constructor(Class<T> klass) {
        try {
            return klass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(klass + " does not have a constructor with no arguments.");
        }
    }
}
