/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.core.reflect;

import com.cadenzauk.core.stream.StreamUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.cadenzauk.core.lang.StringUtil.uppercaseFirst;

public class Getter {
    private static final Function<String,String> X = Function.identity();
    private static final Function<String,String> GET_X = s -> "get" + uppercaseFirst(s);
    private static final Function<String,String> IS_X = s -> "is" + uppercaseFirst(s);

    public static <T, V> Function<T, V> forField(Class<T> targetClass, Class<V> fieldType, Field field) {
        Optional<Method> getterMethod = getMethods()
            .map(x -> ClassUtil.getDeclaredMethod(targetClass, x.apply(field.getName()), fieldType))
            .flatMap(StreamUtil::of)
            .findFirst();

        return getterMethod
            .map(m -> Getter.<T,V>fromMethod(fieldType, m))
            .orElseGet(() -> Getter.fromField(fieldType, field));
    }

    public static <T, V> Function<T, Optional<V>> forField(Class<T> targetClass, Class<Optional> fieldType, Class<V> argType, Field field) {
        Optional<Method> getterMethod = getMethods()
            .map(x -> ClassUtil.getDeclaredMethod(targetClass, x.apply(field.getName()), fieldType))
            .flatMap(StreamUtil::of)
            .findFirst();

        return getterMethod
            .map(m -> Getter.<T,V>fromMethodOptional(argType, m))
            .orElseGet(() -> Getter.fromFieldOptional(argType, field));
    }

    @NotNull
    private static Stream<Function<String, String>> getMethods() {
        return Stream.of(GET_X, X, IS_X);
    }

    private static <T, V> Function<T, V> fromMethod(Class<V> fieldType, Method method) {
        return t -> fieldType.cast(MethodUtil.invoke(method, t));
    }

    @SuppressWarnings("unchecked")
    private static <T,V> Function<T, Optional<V>> fromMethodOptional(Class<V> argType, Method method) {
        return t -> ((Optional<Object>)MethodUtil.invoke(method, t)).map(argType::cast);
    }

    private static <T, V> Function<T, V> fromField(Class<V> fieldType, Field field) {
        return t -> fieldType.cast(FieldUtil.get(field, t));
    }

    @SuppressWarnings("unchecked")
    private static <T, V> Function<T,Optional<V>> fromFieldOptional(Class<V> argType, Field field) {
        return t -> ((Optional<Object>)FieldUtil.get(field, t)).map(argType::cast);
    }
}
