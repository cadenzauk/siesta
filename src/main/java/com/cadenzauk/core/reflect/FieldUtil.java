/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.core.reflect;

import javax.persistence.Transient;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class FieldUtil {
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

    public static boolean hasAnnotation(Class<? extends Annotation> annotationClass, Field field) {
        return field.getAnnotation(annotationClass) != null;
    }
}
