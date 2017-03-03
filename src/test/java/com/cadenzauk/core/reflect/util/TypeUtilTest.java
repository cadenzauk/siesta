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
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class TypeUtilTest {
    @Test
    public void cannotInstantiate() {
        calling(() -> Factory.forClass(TypeUtil.class).get())
            .shouldThrow(RuntimeException.class)
            .withCause(InvocationTargetException.class)
            .withCause(RuntimeInstantiationException.class);
    }

    @SuppressWarnings("unused")
    private Object[] parametersForBoxedType() {
        return new Object[]{
            new Object[]{Long.TYPE, Long.class},
            new Object[]{Integer.TYPE, Integer.class},
            new Object[]{Short.TYPE, Short.class},
            new Object[]{Byte.TYPE, Byte.class},
            new Object[]{Double.TYPE, Double.class},
            new Object[]{Float.TYPE, Float.class},
            new Object[]{Character.TYPE, Character.class},
            new Object[]{Boolean.TYPE, Boolean.class},
        };
    }

    @Test
    @Parameters
    public void boxedType(Class<?> unboxed, Class<?> expected) {
        Class<?> result = TypeUtil.boxedType(unboxed);

        assertThat(result, equalTo(expected));
    }

    @SuppressWarnings("unused")
    private Map<Long,Character> longCharacterMap;
    @SuppressWarnings("unused")
    private Optional<String> optionalString;
    @SuppressWarnings("unused")
    private List<Integer> integerList;

    @SuppressWarnings("unused")
    private Object[] parametersForActualTypeArgument() {
        return new Object[]{
            new Object[]{
                ClassUtil.getDeclaredField(getClass(), "optionalString").getGenericType(),
                0,
                String.class
            },
            new Object[]{
                ClassUtil.getDeclaredField(getClass(), "integerList").getGenericType(),
                0,
                Integer.class
            },
            new Object[]{
                ClassUtil.getDeclaredField(getClass(), "longCharacterMap").getGenericType(),
                0,
                Long.class
            },
            new Object[]{
                ClassUtil.getDeclaredField(getClass(), "longCharacterMap").getGenericType(),
                1,
                Character.class
            },
        };
    }

    @Test
    @Parameters
    public void actualTypeArgument(ParameterizedType input, int index, Class<?> expected) {
        Class<?> result = TypeUtil.actualTypeArgument(input, index);

        assertThat(result, equalTo(expected));
    }

}