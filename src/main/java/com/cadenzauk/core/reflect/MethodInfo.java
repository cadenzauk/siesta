/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.core.reflect;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.siesta.Alias;

import javax.persistence.Column;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

public class MethodInfo<C, R> {
    private final Class<C> declaringClass;
    private final Method method;
    private final Class<?> actualType;
    private final Class<R> effectiveType;

    private MethodInfo(Class<C> declaringClass, Method method, Class<?> actualType, Class<R> effectiveType) {
        this.declaringClass = declaringClass;
        this.method = method;
        this.actualType = actualType;
        this.effectiveType = effectiveType;
    }

    public Class<C> declaringClass() {
        return declaringClass;
    }

    public Method method() {
        return method;
    }

    public Class<?> actualType() {
        return actualType;
    }

    public Class<R> effectiveType() {
        return effectiveType;
    }

    public <A extends Annotation> Optional<A> annotation(Class<A> annotationClass) {
        return Optional.ofNullable(method.getAnnotation(annotationClass));
    }

    public static <R, T> Optional<MethodInfo<R,T>> ofGetter(FieldInfo<R,T> fieldInfo) {
        return Arrays.stream(fieldInfo.declaringClass().getDeclaredMethods())
            .filter(m -> m.getReturnType() == fieldInfo.fieldType())
            .filter(m -> Getter.isGetter(m, fieldInfo.field()))
            .findAny()
            .map(m -> new MethodInfo<>(fieldInfo.declaringClass(), m, fieldInfo.fieldType(), fieldInfo.effectiveType()));

    }

    @SuppressWarnings("unchecked")
    public static <C, F> MethodInfo<C,F> of(Function1<C,F> getter) {
        Method method = MethodUtil.fromReference(getter);
        return new MethodInfo<C,F>((Class<C>) method.getDeclaringClass(), method, method.getReturnType(), (Class<F>) method.getReturnType());
    }

    @SuppressWarnings("unchecked")
    public static <C, F> MethodInfo<C,F> of(FunctionOptional1<C,F> getter) {
        Method method = MethodUtil.fromReference(getter);
        ParameterizedType genericType = (ParameterizedType) method.getGenericReturnType();
        Type argType = genericType.getActualTypeArguments()[0];
        return new MethodInfo<C,F>((Class<C>) method.getDeclaringClass(), method, method.getReturnType(), (Class<F>) argType);
    }
}
