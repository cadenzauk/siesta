/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.core.reflect;

import com.cadenzauk.core.stream.StreamUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.cadenzauk.core.lang.StringUtil.uppercaseFirst;

public class Setter {
    private static final Function<String,String> WITH_X = s -> "with" + uppercaseFirst(s);
    private static final Function<String,String> SET_X = s -> "set" + uppercaseFirst(s);
    private static final Function<String,String> X = Function.identity();

    public static <T, V> BiConsumer<T, V> forField(Class<T> targetClass, Class<V> fieldType, Field field) {
        Optional<Method> setterMethod = Stream.of(WITH_X, SET_X, X)
            .map(x -> ClassUtil.declaredMethod(targetClass, x.apply(field.getName()), fieldType))
            .flatMap(StreamUtil::of)
            .findFirst();

        return setterMethod
            .map(Setter::<T, V>fromMethod)
            .orElseGet(() -> Setter.fromField(field));
    }

    public static <T, V> BiConsumer<T, Optional<V>> forField(Class<T> targetClass, Class<Optional> fieldType, Class<V> argType, Field field) {
        Optional<Method> setterMethod = Stream.of(WITH_X, SET_X, X)
            .map(x -> ClassUtil.declaredMethod(targetClass, x.apply(field.getName()), fieldType))
            .flatMap(StreamUtil::of)
            .findFirst();

        return setterMethod
            .map(Setter::<T, Optional<V>>fromMethod)
            .orElseGet(() -> Setter.fromField(field));
    }

    private static <T, V> BiConsumer<T, V> fromMethod(Method method) {
        return (t, v) -> MethodUtil.invoke(method, t, v);
    }

    private static <T, V> BiConsumer<T, V> fromField(Field field) {
        return (t, v) -> FieldUtil.set(field, t, v);
    }
}
