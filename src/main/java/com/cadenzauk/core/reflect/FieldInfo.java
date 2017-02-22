/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.core.reflect;

import javax.persistence.Column;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

public class FieldInfo<C, F> {
    private final Class<C> declaringClass;
    private final Field field;
    private final Class<?> fieldType;
    private final Class<F> effectiveType;

    private FieldInfo(Class<C> declaringClass, Field field, Class<?> fieldType, Class<F> effectiveType) {
        this.declaringClass = declaringClass;
        this.field = field;
        this.fieldType = fieldType;
        this.effectiveType = effectiveType;
    }

    public Class<C> declaringClass() {
        return declaringClass;
    }

    public Field field() {
        return field;
    }

    public Class<?> fieldType() {
        return fieldType;
    }

    public Class<F> effectiveType() {
        return effectiveType;
    }

    public <A extends Annotation> Optional<A> annotation(Class<A> annotation) {
        return Optional.ofNullable(field.getAnnotation(annotation));
    }

    public static <R, T> FieldInfo<R,T> of(Class<R> rowClass, Field field, Class<T> fieldType) {
        return new FieldInfo<>(rowClass, field, field.getType(), fieldType);
    }

    public static <C, F> Optional<FieldInfo<C,F>> ofGetter(MethodInfo<C,F> getter) {
        return Arrays.stream(getter.declaringClass().getDeclaredFields())
            .filter(f -> f.getType() == getter.actualType())
            .filter(f -> Getter.isGetter(getter.method(), f))
            .findAny()
            .map(f -> new FieldInfo<>(getter.declaringClass(), f, getter.actualType(), getter.effectiveType()));
    }
}
