/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.core.reflect;

import com.cadenzauk.core.reflect.util.ClassUtil;
import com.cadenzauk.core.reflect.util.FieldUtil;
import com.cadenzauk.core.reflect.util.MethodUtil;
import com.cadenzauk.core.stream.StreamUtil;
import com.cadenzauk.core.util.UtilityClass;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.cadenzauk.core.lang.StringUtil.uppercaseFirst;
import static com.cadenzauk.core.reflect.util.TypeUtil.actualTypeArgument;

public final class Setter extends UtilityClass {
    private static final Function<String,String> WITH_X = s -> "with" + uppercaseFirst(s);
    private static final Function<String,String> SET_X = s -> "set" + uppercaseFirst(s);
    private static final Function<String,String> X = Function.identity();

    public static <T, V> BiConsumer<T, Optional<V>> forField(Class<T> targetClass, Class<V> argType, Field field) {
        Optional<Method> setterMethod = Stream.of(WITH_X, SET_X, X)
            .map(f -> f.apply(field.getName()))
            .flatMap(name -> ClassUtil.declaredMethods(targetClass)
                .filter(method -> method.getName().equals(name))
                .filter(method -> method.getParameterCount() == 1)
                .filter(method -> method.getParameterTypes()[0] == Optional.class || method.getParameterTypes()[0] == argType))
            .findFirst();

        return setterMethod
            .map(m -> fromMethod(targetClass, argType, m))
            .orElseGet(() -> Setter.fromField(targetClass, argType, field));
    }

    private static <T, V> BiConsumer<T, Optional<V>> fromMethod(Class<T> targetClass, Class<V> argType, Method method) {
        Class<?> parameterType = method.getParameterTypes()[0];
        if (argType.isAssignableFrom(parameterType)) {
            return (t, v) -> MethodUtil.invoke(method, t, v.orElse(null));
        }
        if (parameterType == Optional.class && actualTypeArgument((ParameterizedType) method.getGenericParameterTypes()[0], 0).isAssignableFrom(argType)) {
            return (t, v) -> MethodUtil.invoke(method, t, v);
        }
        throw new IllegalArgumentException(String.format("Cannot convert %s into a BiConsumer<%s,Optional<%s>>.", method, targetClass, argType));
    }

    private static <T, V> BiConsumer<T, Optional<V>> fromField(Class<T> targetClass, Class<V> argType, Field field) {
        if (argType.isAssignableFrom(field.getType())) {
            return (t, v) -> FieldUtil.set(field, t, v.orElse(null));
        }
        if (field.getType() == Optional.class && actualTypeArgument((ParameterizedType) field.getGenericType(), 0).isAssignableFrom(argType)) {
            return (t, v) -> FieldUtil.set(field, t, v);
        }
        throw new IllegalArgumentException(String.format("Cannot convert %s into a BiConsumer<%s,Optional<%s>>.", field, targetClass, argType));
    }
}
