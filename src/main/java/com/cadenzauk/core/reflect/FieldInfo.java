/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.core.reflect;

import com.cadenzauk.core.reflect.util.ClassUtil;
import com.cadenzauk.core.reflect.util.FieldUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static com.cadenzauk.core.reflect.util.FieldUtil.genericTypeArgument;

public class FieldInfo<C, F> {
    private final Class<C> declaringClass;
    private final Field field;
    private final Class<F> effectiveType;
    private final Function<C,Optional<F>> optionalGetter;

    private FieldInfo(Class<C> declaringClass, Field field, Class<F> effectiveType) {
        Objects.requireNonNull(declaringClass, "declaringClass");
        Objects.requireNonNull(field, "field");
        Objects.requireNonNull(effectiveType, "effectiveType");
        this.declaringClass = declaringClass;
        this.field = field;
        this.effectiveType = effectiveType;
        this.optionalGetter = Getter.forField(declaringClass, effectiveType, field);
    }

    public Class<C> declaringClass() {
        return declaringClass;
    }

    public Field field() {
        return field;
    }

    public String name() {
        return field.getName();
    }

    public Class<?> fieldType() {
        return field.getType();
    }

    public Class<F> effectiveType() {
        return effectiveType;
    }

    public Function<C, Optional<F>> optionalGetter() {
        return optionalGetter;
    }

    public <A extends Annotation> Optional<A> annotation(Class<A> annotation) {
        return FieldUtil.annotation(annotation, field);
    }

    @SuppressWarnings("unchecked")
    public static <R> FieldInfo<R,?> of(Class<R> declaringClass, Field field) {
        if (field.getType() == Optional.class) {
            return genericTypeArgument(field, 0)
                .map(cls -> of(declaringClass, field, cls))
                .orElseThrow(() -> new IllegalArgumentException("Unable to determine the type of Optional field " + field.getName() + " in " + declaringClass));
        }
        return of(declaringClass, field, field.getType());
    }

    public static <R, T> FieldInfo<R,T> of(Class<R> rowClass, Field field, Class<T> fieldType) {
        return new FieldInfo<>(rowClass, field, fieldType);
    }

    public static <C, F> FieldInfo<C,F> of(Class<C> objectClass, String fieldName, Class<F> fieldType) {
        return ClassUtil.findField(objectClass, fieldName)
            .filter(f -> isCompatibleWith(f, fieldType))
            .map(f -> of(objectClass, f, fieldType))
            .orElseThrow(() -> new NoSuchElementException("No field called " + fieldName + " of type " + fieldType + " in " + objectClass));
    }

    public static <C, F> Optional<FieldInfo<C,F>> ofGetter(MethodInfo<C,F> getter) {
        return Arrays.stream(getter.declaringClass().getDeclaredFields())
            .filter(f -> getter.actualType().isAssignableFrom(f.getType()))
            .filter(f -> Getter.isGetter(getter.method(), f))
            .findAny()
            .map(f -> of(getter.declaringClass(), f, getter.effectiveType()));
    }

    private static boolean isCompatibleWith(Field field, Class<?> targetType) {
        if (targetType.isAssignableFrom(field.getType())) {
            return true;
        }
        if (field.getType() == Optional.class) {
            return genericTypeArgument(field, 0)
                .map(targetType::isAssignableFrom)
                .orElse(false);
        }
        return false;
    }
}
