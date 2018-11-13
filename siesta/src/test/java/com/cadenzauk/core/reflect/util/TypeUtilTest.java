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

class TypeUtilTest {
    @Test
    void isUtility() {
        assertThat(TypeUtil.class, isUtilityClass());
    }

    private static Stream<Arguments> parametersForBoxedType() {
        return Stream.of(
            Arguments.of(Long.TYPE, Long.class),
            Arguments.of(Integer.TYPE, Integer.class),
            Arguments.of(Short.TYPE, Short.class),
            Arguments.of(Byte.TYPE, Byte.class),
            Arguments.of(Double.TYPE, Double.class),
            Arguments.of(Float.TYPE, Float.class),
            Arguments.of(Character.TYPE, Character.class),
            Arguments.of(Boolean.TYPE, Boolean.class)
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
            Arguments.of(long[].class, Long.TYPE),
            Arguments.of(int[].class, Integer.TYPE),
            Arguments.of(short[].class, Short.TYPE),
            Arguments.of(byte[].class, Byte.TYPE),
            Arguments.of(double[].class, Double.TYPE),
            Arguments.of(float[].class, Float.TYPE),
            Arguments.of(char[].class, Character.TYPE),
            Arguments.of(boolean[].class, Boolean.TYPE)
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
            Arguments.of(ClassUtil.getDeclaredField(TypeUtilTest.class, "optionalString").getGenericType(), 0, String.class),
            Arguments.of(ClassUtil.getDeclaredField(TypeUtilTest.class, "integerList").getGenericType(), 0, Integer.class),
            Arguments.of(ClassUtil.getDeclaredField(TypeUtilTest.class, "longCharacterMap").getGenericType(), 0, Long.class),
            Arguments.of(ClassUtil.getDeclaredField(TypeUtilTest.class, "longCharacterMap").getGenericType(), 1, Character.class)
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
            Arguments.of(Long.TYPE, Optional.of(long[].class)),
            Arguments.of(Integer.TYPE, Optional.of(int[].class)),
            Arguments.of(Short.TYPE, Optional.of(short[].class)),
            Arguments.of(Byte.TYPE, Optional.of(byte[].class)),
            Arguments.of(Double.TYPE, Optional.of(double[].class)),
            Arguments.of(Float.TYPE, Optional.of(float[].class)),
            Arguments.of(Character.TYPE, Optional.of(char[].class)),
            Arguments.of(Boolean.TYPE, Optional.of(boolean[].class)),
            Arguments.of(String.class, Optional.of(String[].class)),
            Arguments.of(BigDecimal.class, Optional.empty()),
            Arguments.of(String[].class, Optional.empty())
        );
    }
}