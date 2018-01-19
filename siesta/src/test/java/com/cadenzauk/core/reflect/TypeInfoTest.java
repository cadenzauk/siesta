/*
 * Copyright (c) 2018 Cadenza United Kingdom Limited
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

package com.cadenzauk.core.reflect;

import com.cadenzauk.core.junit.TestCase;
import com.cadenzauk.core.junit.TestCaseArgumentsProvider;
import com.cadenzauk.core.reflect.util.ClassUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class TypeInfoTest {
    private static final int OPTIONAL_PARAMETER = 0;
    private static final int INT_PARAMETER = 1;
    private static final int ZONED_DATE_TIME_PARAMETER = 2;
    private static final int LIST_ARRAY_PARAMETER = 3;
    private static final int FLOAT_ARRAY_PARAMETER = 4;
    private static final int MAP_PARAMETER = 5;

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({OPTIONAL_PARAMETER + "", "Optional<String>"})
    @TestCase({INT_PARAMETER + "", "int"})
    @TestCase({ZONED_DATE_TIME_PARAMETER + "", "ZonedDateTime"})
    @TestCase({LIST_ARRAY_PARAMETER + "", "List<Optional<UUID>>[]"})
    @TestCase({FLOAT_ARRAY_PARAMETER + "", "float[]"})
    @TestCase({MAP_PARAMETER + "", "Map<Short,BigDecimal>"})
    void toStringTest(int arg, String expected) {
        TypeInfo sut = TypeInfo.ofParameter(randomMethod(), arg);

        String result = sut.toString();

        assertThat(result, equalTo(expected));
    }

    @Test
    void arrayComponentTypeGeneric() {
        TypeInfo sut = TypeInfo.ofParameter(randomMethod(), LIST_ARRAY_PARAMETER);

        TypeInfo result = sut.arrayComponentType();

        assertThat(result.rawClass(), equalTo(List.class));
        assertThat(result.actualTypeArgument(0).rawClass(), equalTo(Optional.class));
        assertThat(result.actualTypeArgument(0).actualTypeArgument(0).rawClass(), equalTo(UUID.class));
    }

    @Test
    void arrayComponentTypePrimitive() {
        TypeInfo sut = TypeInfo.ofParameter(randomMethod(), FLOAT_ARRAY_PARAMETER);

        TypeInfo result = sut.arrayComponentType();

        assertThat(result.rawClass(), equalTo(Float.TYPE));
    }

    @Test
    void arrayComponentTypeThrowsIfNotArray() {
        TypeInfo sut = TypeInfo.ofParameter(randomMethod(), MAP_PARAMETER);

        calling(sut::arrayComponentType)
            .shouldThrow(NoSuchElementException.class)
            .withMessage("Map<Short,BigDecimal> is not an array type.");
    }

    @Test
    void actualTypeArgument() {
        TypeInfo sut = TypeInfo.ofParameter(randomMethod(), OPTIONAL_PARAMETER);

        TypeInfo result = sut.actualTypeArgument(0);

        assertThat(result.rawClass(), equalTo(String.class));
    }

    @Test
    void actualTypeArgumentInRange() {
        TypeInfo sut = TypeInfo.ofParameter(randomMethod(), MAP_PARAMETER);

        TypeInfo result = sut.actualTypeArgument(1);

        assertThat(result.rawClass(), equalTo(BigDecimal.class));
    }

    @Test
    void actualTypeArgumentOutOfRange() {
        TypeInfo sut = TypeInfo.ofParameter(randomMethod(), MAP_PARAMETER);

        calling(() -> sut.actualTypeArgument(2))
            .shouldThrow(NoSuchElementException.class)
            .withMessage("Map<Short,BigDecimal> does not have type argument 2.");
    }

    @Test
    void actualTypeArgumentNonGenericType() {
        TypeInfo sut = TypeInfo.ofParameter(randomMethod(), INT_PARAMETER);

        calling(() -> sut.actualTypeArgument(0))
            .shouldThrow(NoSuchElementException.class)
            .withMessage("int is not a parameterized type so does not have actual type arguments.");
    }

    @Test
    void rawClassGeneric() {
        TypeInfo sut = TypeInfo.ofParameter(randomMethod(), OPTIONAL_PARAMETER);

        Class<?> result = sut.rawClass();

        assertThat(result, equalTo(Optional.class));
    }

    @Test
    void rawClassPrimitive() {
        TypeInfo sut = TypeInfo.ofParameter(randomMethod(), INT_PARAMETER);

        Class<?> result = sut.rawClass();

        assertThat(result, equalTo(Integer.TYPE));
    }

    @Test
    void rawClass() {
        TypeInfo sut = TypeInfo.ofParameter(randomMethod(), ZONED_DATE_TIME_PARAMETER);

        Class<?> result = sut.rawClass();

        assertThat(result, equalTo(ZonedDateTime.class));
    }

    @Test
    void rawClassOfGenericArray() {
        TypeInfo sut = TypeInfo.ofParameter(randomMethod(), LIST_ARRAY_PARAMETER);

        Class<?> result = sut.rawClass();

        assertThat(result, equalTo(List[].class));
    }

    @Test
    void rawClassOfPrimitiveArray() {
        TypeInfo sut = TypeInfo.ofParameter(randomMethod(), FLOAT_ARRAY_PARAMETER);

        Class<?> result = sut.rawClass();

        assertThat(result, equalTo(float[].class));
    }

    @Test
    void ofParameter() {
        Method randomMethod = randomMethod();

        TypeInfo result = TypeInfo.ofParameter(randomMethod, OPTIONAL_PARAMETER);

        assertThat(result.rawClass(), equalTo(Optional.class));
        assertThat(result.actualTypeArgument(0).rawClass(), equalTo(String.class));
    }

    private Method randomMethod() {
        return ClassUtil.getDeclaredMethod(getClass(), "randomMethod", Optional.class, Integer.TYPE, ZonedDateTime.class, List[].class, float[].class, Map.class);
    }

    @SuppressWarnings("unused")
    private void randomMethod(Optional<String> optString, int i, ZonedDateTime ts, List<Optional<UUID>>[] strings, float[] f, Map<Short,BigDecimal> map) {
    }
}