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

import com.cadenzauk.core.lang.RuntimeInstantiationException;
import com.cadenzauk.core.reflect.Factory;
import com.cadenzauk.core.tuple.Tuple;
import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.core.tuple.Tuple3;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class TypeUtilTest {
    @Test
    void cannotInstantiate() {
        calling(() -> Factory.forClass(TypeUtil.class).get())
            .shouldThrow(RuntimeException.class)
            .withCause(InvocationTargetException.class)
            .withCause(RuntimeInstantiationException.class);
    }

    private Stream<Tuple2<Class<?>,Class<?>>> parametersForBoxedType() {
        return Stream.of(
            Tuple.of(Long.TYPE, Long.class),
            Tuple.of(Integer.TYPE, Integer.class),
            Tuple.of(Short.TYPE, Short.class),
            Tuple.of(Byte.TYPE, Byte.class),
            Tuple.of(Double.TYPE, Double.class),
            Tuple.of(Float.TYPE, Float.class),
            Tuple.of(Character.TYPE, Character.class),
            Tuple.of(Boolean.TYPE, Boolean.class)
        );
    }

    @TestFactory
    Stream<DynamicTest> boxedType() {
        return parametersForBoxedType().map(p -> DynamicTest.dynamicTest(p.toString(), () -> boxedType(p.item1(), p.item2())));
    }

    private void boxedType(Class<?> unboxed, Class<?> expected) {
        Class<?> result = TypeUtil.boxedType(unboxed);

        assertThat(result, equalTo(expected));
    }

    @SuppressWarnings("unused")
    private Map<Long,Character> longCharacterMap;
    @SuppressWarnings("unused")
    private Optional<String> optionalString;
    @SuppressWarnings("unused")
    private List<Integer> integerList;

    private Stream<Tuple3<Type,Integer,Class<?>>> parametersForActualTypeArgument() {
        return Stream.of(
            Tuple.of(ClassUtil.getDeclaredField(getClass(), "optionalString").getGenericType(), 0, String.class),
            Tuple.of(ClassUtil.getDeclaredField(getClass(), "integerList").getGenericType(), 0, Integer.class),
            Tuple.of(ClassUtil.getDeclaredField(getClass(), "longCharacterMap").getGenericType(), 0, Long.class),
            Tuple.of(ClassUtil.getDeclaredField(getClass(), "longCharacterMap").getGenericType(), 1, Character.class)
        );
    }

    @TestFactory
    Stream<DynamicTest> actualTypeArgument() {
        return parametersForActualTypeArgument().map(p -> DynamicTest.dynamicTest(p.toString(), () -> actualTypeArgument(p.item1(), p.item2(), p.item3())));
    }

    private void actualTypeArgument(Type input, int index, Class<?> expected) {
        Class<?> result = TypeUtil.actualTypeArgument((ParameterizedType) input, index);

        assertThat(result, equalTo(expected));
    }

}