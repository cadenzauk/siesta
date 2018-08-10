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

package com.cadenzauk.core.util;

import com.cadenzauk.core.RandomValues;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;

import static com.cadenzauk.core.testutil.IsUtilityClass.isUtilityClass;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.is;

class ArrayUtilTest {
    @Test
    void isUtility() {
        assertThat(ArrayUtil.class, isUtilityClass());
    }

    @Test
    void newArrayClass() {
        int size = RandomUtils.nextInt(5, 20);

        ZonedDateTime[] result = ArrayUtil.newArray(ZonedDateTime.class, size);

        assertThat(result, arrayWithSize(size));
    }

    @Test
    void newArrayTypeToken() {
        int size = RandomUtils.nextInt(5, 20);

        List<Integer>[] result = ArrayUtil.newArray(new TypeToken<List<Integer>>() {}, size);

        assertThat(result, arrayWithSize(size));
    }

    @Test
    void generator() {
        int size = RandomUtils.nextInt(5, 20);

        IntFunction<List<String>[]> result = ArrayUtil.generator(List.class);

        assertThat(result.apply(size), arrayWithSize(size));
    }

    @Test
    void arrayOfString() {
        int size = RandomUtils.nextInt(10, 20);
        String value = RandomStringUtils.randomAlphabetic(10);

        String[] result = ArrayUtil.arrayOf(size, value);

        assertThat(result.length, is(size));
        Arrays.stream(result).forEach(s -> assertThat(s, is(value)));
    }

    @Test
    void arrayOfChar() {
        int size = RandomUtils.nextInt(10, 20);
        char value = RandomValues.randomChar();

        char[] result = ArrayUtil.arrayOf(size, value);

        assertThat(result.length, is(size));
        ArrayUtil.stream(result).forEach(s -> assertThat(s, is(value)));
    }

    @Test
    void arrayOfBoolean() {
        int size = RandomUtils.nextInt(10, 20);
        boolean value = RandomUtils.nextBoolean();

        boolean[] result = ArrayUtil.arrayOf(size, value);

        assertThat(result.length, is(size));
        ArrayUtil.stream(result).forEach(s -> assertThat(s, is(value)));
    }

    @Test
    void arrayOfByte() {
        int size = RandomUtils.nextInt(10, 20);
        byte value = RandomValues.randomByte();

        byte[] result = ArrayUtil.arrayOf(size, value);

        assertThat(result.length, is(size));
        ArrayUtil.stream(result).forEach(s -> assertThat(s, is(value)));
    }

    @Test
    void arrayOfShort() {
        int size = RandomUtils.nextInt(10, 20);
        short value = RandomValues.randomShort();

        short[] result = ArrayUtil.arrayOf(size, value);

        assertThat(result.length, is(size));
        ArrayUtil.stream(result).forEach(s -> assertThat(s, is(value)));
    }

    @Test
    void arrayOfInteger() {
        int size = RandomUtils.nextInt(10, 20);
        int value = RandomUtils.nextInt(1000, 2000);

        int[] result = ArrayUtil.arrayOf(size, value);

        assertThat(result.length, is(size));
        Arrays.stream(result).forEach(s -> assertThat(s, is(value)));
    }

    @Test
    void arrayOfLong() {
        int size = RandomUtils.nextInt(10, 20);
        long value = RandomUtils.nextLong(100000, 200000);

        long[] result = ArrayUtil.arrayOf(size, value);

        assertThat(result.length, is(size));
        Arrays.stream(result).forEach(s -> assertThat(s, is(value)));
    }

    @Test
    void arrayOfFloat() {
        int size = RandomUtils.nextInt(10, 20);
        float value = RandomUtils.nextFloat(100000, 200000);

        float[] result = ArrayUtil.arrayOf(size, value);

        assertThat(result.length, is(size));
        ArrayUtil.stream(result).forEach(s -> assertThat(s, is(value)));
    }

    @Test
    void arrayOfDouble() {
        int size = RandomUtils.nextInt(10, 20);
        double value = RandomUtils.nextDouble(100000, 200000);

        double[] result = ArrayUtil.arrayOf(size, value);

        assertThat(result.length, is(size));
        Arrays.stream(result).forEach(s -> assertThat(s, is(value)));
    }

    @Test
    void streamBoolean() {
        boolean[] booleans = RandomValues.randomBooleanArray(10, 20);

        Boolean[] resultAsArray = ArrayUtil.stream(booleans).toArray(Boolean[]::new);

        assertThat(resultAsArray.length, is(booleans.length));
        for (int i = 0; i < resultAsArray.length; i++) {
            assertThat(resultAsArray[i], is(booleans[i]));
        }
    }

    @Test
    void streamByte() {
        byte[] bytes = RandomValues.randomByteArray(10, 20);

        Byte[] resultAsArray = ArrayUtil.stream(bytes).toArray(Byte[]::new);

        assertThat(resultAsArray.length, is(bytes.length));
        for (int i = 0; i < resultAsArray.length; i++) {
            assertThat(resultAsArray[i], is(bytes[i]));
        }
    }

    @Test
    void streamChar() {
        char[] chars = RandomValues.randomCharArray(10, 20);

        Character[] resultAsArray = ArrayUtil.stream(chars).toArray(Character[]::new);

        assertThat(resultAsArray.length, is(chars.length));
        for (int i = 0; i < resultAsArray.length; i++) {
            assertThat(resultAsArray[i], is(chars[i]));
        }
    }

    @Test
    void streamShort() {
        short[] shorts = RandomValues.randomShortArray(10, 20);

        Short[] resultAsArray = ArrayUtil.stream(shorts).toArray(Short[]::new);

        assertThat(resultAsArray.length, is(shorts.length));
        for (int i = 0; i < resultAsArray.length; i++) {
            assertThat(resultAsArray[i], is(shorts[i]));
        }
    }

    @Test
    void streamFloat() {
        float[] shorts = RandomValues.randomFloatArray(10, 20);

        Float[] resultAsArray = ArrayUtil.stream(shorts).toArray(Float[]::new);

        assertThat(resultAsArray.length, is(shorts.length));
        for (int i = 0; i < resultAsArray.length; i++) {
            assertThat(resultAsArray[i], is(shorts[i]));
        }
    }
}