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
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

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

    private static final Map<Class<?>,Class<?>> ARRAY_TO_PRIMITIVE = ImmutableMap.<Class<?>,Class<?>>builder()
        .put(long[].class, Long.TYPE)
        .put(int[].class, Integer.TYPE)
        .put(short[].class, Short.TYPE)
        .put(byte[].class, Byte.TYPE)
        .put(char[].class, Character.TYPE)
        .put(boolean[].class, Boolean.TYPE)
        .put(double[].class, Double.TYPE)
        .put(float[].class, Float.TYPE)
        .put(String[].class, String.class)
        .build();

    @SuppressWarnings("unchecked")
    public static <V> Class<V> boxedType(Class<V> primitiveType) {
        return Optional.ofNullable((Class<V>) PRIMITIVE_TO_BOXED.get(primitiveType))
            .orElse(primitiveType);
    }

    @SuppressWarnings("unchecked")
    public static <V> TypeToken<V> boxedType(TypeToken<V> primitiveType) {
        return Optional.ofNullable((Class<V>) PRIMITIVE_TO_BOXED.get(primitiveType.getRawType()))
            .map(TypeToken::of)
            .orElse(primitiveType);
    }

    @SuppressWarnings({"SuspiciousMethodCalls"})
    public static Class<?> primitiveComponentType(Type primitiveArrayType) {
        return Optional.ofNullable(ARRAY_TO_PRIMITIVE.get(primitiveArrayType))
            .orElseThrow(() -> new IllegalArgumentException(toString(primitiveArrayType) + " is not a primative array."));
    }

    public static Optional<Class<?>> findPrimitiveArrayType(Type primitiveComponentType) {
        return ARRAY_TO_PRIMITIVE
            .entrySet()
            .stream()
            .filter(e -> e.getValue().equals(primitiveComponentType))
            .findFirst()
            .map(Map.Entry::getKey);
    }

    public static Class<?> actualTypeArgument(ParameterizedType parameterizedType, int i) {
        return (Class<?>) parameterizedType.getActualTypeArguments()[i];
    }

    public static String toString(Type type) {
        return
            OptionalUtil.as(GenericArrayType.class, type)
                .map(g -> toString(g.getGenericComponentType()) + "[]")
                .orElseGet(() ->
                    OptionalUtil.as(ParameterizedType.class, type)
                        .map(p -> toString(p.getRawType()) + "<" + Arrays.stream(p.getActualTypeArguments()).map(TypeUtil::toString).collect(joining(",")) + ">")
                        .orElseGet(() -> OptionalUtil.as(Class.class, type)
                            .map(Class::getSimpleName)
                            .orElseGet(type::getTypeName)));
    }
}
