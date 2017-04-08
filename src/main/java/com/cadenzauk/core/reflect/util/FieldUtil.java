/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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

    private static Optional<ParameterizedType> genericType(Field field) {
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
