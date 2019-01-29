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

import com.google.common.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static com.cadenzauk.core.testutil.IsUtilityClass.isUtilityClass;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class TypeUtilTest {
    @Test
    void isUtility() {
        assertThat(TypeUtil.class, isUtilityClass());
    }

    private static Stream<Arguments> parametersForBoxedType() {
        return Stream.of(
            arguments(Long.TYPE, Long.class),
            arguments(Integer.TYPE, Integer.class),
            arguments(Short.TYPE, Short.class),
            arguments(Byte.TYPE, Byte.class),
            arguments(Double.TYPE, Double.class),
            arguments(Float.TYPE, Float.class),
            arguments(Character.TYPE, Character.class),
            arguments(Boolean.TYPE, Boolean.class)
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForBoxedType")
    void boxedType(Class<?> unboxed, Class<?> expected) {
        Class<?> result = TypeUtil.boxedType(unboxed);

        assertThat(result, equalTo(expected));
    }

    @ParameterizedTest
    @MethodSource("parametersForBoxedType")
    void boxedTypeAsTypeToken(Class<?> unboxed, Class<?> expected) {
        TypeToken<?> result = TypeUtil.boxedType(TypeToken.of(unboxed));

        assertThat(result, equalTo(TypeToken.of(expected)));
    }

    private static Stream<Arguments> parametersForPrimitiveComponentType() {
        return Stream.of(
            arguments(long[].class, Long.TYPE),
            arguments(int[].class, Integer.TYPE),
            arguments(short[].class, Short.TYPE),
            arguments(byte[].class, Byte.TYPE),
            arguments(double[].class, Double.TYPE),
            arguments(float[].class, Float.TYPE),
            arguments(char[].class, Character.TYPE),
            arguments(boolean[].class, Boolean.TYPE)
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForPrimitiveComponentType")
    void primitiveComponentType(Class<?> array, Class<?> expected) {
        Type result = TypeUtil.primitiveComponentType(array);

        assertThat(result, equalTo(expected));
    }

    @Test
    void primitiveComponentOfNonPrimitiveThrows() {
        calling(() -> TypeUtil.primitiveComponentType(Byte[].class))
            .shouldThrow(IllegalArgumentException.class)
            .withMessage("Byte[] is not a primative array.");
    }

    @SuppressWarnings("unused")
    private Map<Long,Character> longCharacterMap;
    @SuppressWarnings("unused")
    private Optional<String> optionalString;
    @SuppressWarnings("unused")
    private List<Integer> integerList;

    private static Stream<Arguments> parametersForActualTypeArgument() {
        return Stream.of(
            arguments(ClassUtil.getDeclaredField(TypeUtilTest.class, "optionalString").getGenericType(), 0, String.class),
            arguments(ClassUtil.getDeclaredField(TypeUtilTest.class, "integerList").getGenericType(), 0, Integer.class),
            arguments(ClassUtil.getDeclaredField(TypeUtilTest.class, "longCharacterMap").getGenericType(), 0, Long.class),
            arguments(ClassUtil.getDeclaredField(TypeUtilTest.class, "longCharacterMap").getGenericType(), 1, Character.class)
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForActualTypeArgument")
    void actualTypeArgument(Type input, int index, Class<?> expected) {
        Class<?> result = TypeUtil.actualTypeArgument((ParameterizedType) input, index);

        assertThat(result, equalTo(expected));
    }

    @ParameterizedTest
    @MethodSource("parametersForFindPrimitiveArrayType")
    void findPrimitiveArrayType(Type primitiveType, Optional<Class<?>> expected) {
        Optional<Class<?>> result = TypeUtil.findPrimitiveArrayType(primitiveType);

        assertThat(result, is(expected));
    }

    private static Stream<Arguments> parametersForFindPrimitiveArrayType() {
        return Stream.of(
            arguments(Long.TYPE, Optional.of(long[].class)),
            arguments(Integer.TYPE, Optional.of(int[].class)),
            arguments(Short.TYPE, Optional.of(short[].class)),
            arguments(Byte.TYPE, Optional.of(byte[].class)),
            arguments(Double.TYPE, Optional.of(double[].class)),
            arguments(Float.TYPE, Optional.of(float[].class)),
            arguments(Character.TYPE, Optional.of(char[].class)),
            arguments(Boolean.TYPE, Optional.of(boolean[].class)),
            arguments(String.class, Optional.of(String[].class)),
            arguments(BigDecimal.class, Optional.empty()),
            arguments(String[].class, Optional.empty())
        );
    }
}