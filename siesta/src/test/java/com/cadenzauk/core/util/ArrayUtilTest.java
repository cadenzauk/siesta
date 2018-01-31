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

import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.IntFunction;

import static com.cadenzauk.core.testutil.IsUtilityClass.isUtilityClass;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;

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

}