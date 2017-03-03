/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.core.reflect.util;

import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.core.util.UtilityClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Optional;

public final class FieldUtil extends UtilityClass {
    public static void set(Field field, Object target, Object value) {
        field.setAccessible(true);
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object get(Field field, Object target) {
        field.setAccessible(true);
        try {
            return field.get(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<ParameterizedType> genericType(Field field) {
        return OptionalUtil.as(ParameterizedType.class, field.getGenericType());
    }

    public static Optional<Class<?>> genericTypeArgument(Field field, int index) {
        return genericType(field)
            .map(gt -> TypeUtil.actualTypeArgument(gt, index));
    }

    public static boolean hasAnnotation(Class<? extends Annotation> annotationClass, Field field) {
        return field.getAnnotation(annotationClass) != null;
    }

    public static <A extends Annotation> Optional<A> annotation(Class<A> annotationClass, Field field) {
        return Optional.ofNullable(field.getAnnotation(annotationClass));
    }
}
