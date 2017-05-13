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

    @SuppressWarnings("unchecked")
    public static <V> Class<V> boxedType(Class<V> primitiveType) {
        return Optional.ofNullable((Class<V>) PRIMITIVE_TO_BOXED.get(primitiveType))
            .orElse(primitiveType);
    }

    public static Class<?> actualTypeArgument(ParameterizedType parameterizedType, int i) {
        return (Class<?>) parameterizedType.getActualTypeArguments()[i];
    }

    public static String toString(Type type) {
        return OptionalUtil.as(ParameterizedType.class, type)
            .map(p -> toString(p.getRawType()) + "<" + Arrays.stream(p.getActualTypeArguments()).map(TypeUtil::toString).collect(joining(",")) + ">")
            .orElseGet(() -> OptionalUtil.as(Class.class, type)
                .map(Class::getSimpleName)
                .orElseGet(type::getTypeName));
    }
}
