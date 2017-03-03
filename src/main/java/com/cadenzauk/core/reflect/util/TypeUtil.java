/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.core.reflect.util;

import com.cadenzauk.core.util.UtilityClass;
import com.google.common.collect.ImmutableMap;

import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.Optional;

public final class TypeUtil extends UtilityClass {
    private static final Map<Class<?>,Class<?>> PRIMITIVE_TO_BOXED = ImmutableMap.<Class<?>,Class<?>>builder()
        .put(Long.TYPE, Long.class)
        .put(Integer.TYPE, Integer.class)
        .put(Short.TYPE, Short.class)
        .put(Byte.TYPE, Byte.class)
        .put(Character.TYPE, Character.class)
        .put(Boolean.TYPE, Boolean.class)
        .put(Double.TYPE, Double.class)
        .put(Float.TYPE, Float.class)
        .build();

    @SuppressWarnings("unchecked")
    public static <V> Class<V> boxedType(Class<V> primitiveType) {
        return Optional.ofNullable((Class<V>)PRIMITIVE_TO_BOXED.get(primitiveType))
            .orElse(primitiveType);
    }

    public static Class<?> actualTypeArgument(ParameterizedType parameterizedType, int i) {
        return (Class<?>) parameterizedType.getActualTypeArguments()[i];
    }
}
