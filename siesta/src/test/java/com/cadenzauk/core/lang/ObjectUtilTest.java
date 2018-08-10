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

package com.cadenzauk.core.lang;

import com.cadenzauk.core.junit.TestCase;
import com.cadenzauk.core.junit.TestCaseArgumentsProvider;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ObjectUtilTest {

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"null", "null", "null"})
    @TestCase({"null", "B", "B"})
    @TestCase({"A", "B", "A"})
    @TestCase({"A", "null", "A"})
    void coalesce2(String a, String b, String expected) {
        String result = ObjectUtil.coalesce(a, b);

        assertThat(result, is(expected));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"1", "2", "3", "1"})
    @TestCase({"null", "2", "3", "2"})
    @TestCase({"null", "null", "3", "3"})
    @TestCase({"null", "null", "null", "null"})
    @TestCase({"1", "null", "3", "1"})
    @TestCase({"1", "null", "null", "1"})
    @TestCase({"1", "2", "null", "1"})
    @TestCase({"null", "2", "null", "2"})
    @TestCase({"null", "2", "3", "2"})
    @TestCase({"null", "null", "3", "3"})
    void coalesce3(Integer a, Integer b, Integer c, Integer expected) {
        Integer result = ObjectUtil.coalesce(a, b, c);

        assertThat(result, is(expected));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"1", "2", "3", "4", "1"})
    @TestCase({"null", "2", "3", "4", "2"})
    @TestCase({"null", "null", "3", "4", "3"})
    @TestCase({"null", "null", "null", "4", "4"})
    @TestCase({"null", "null", "null", "null", "null"})

    @TestCase({"1", "null", "3", "4", "1"})
    @TestCase({"1", "null", "null", "4", "1"})
    @TestCase({"1", "null", "null", "null", "1"})
    @TestCase({"1", "2", "null", "null", "1"})
    @TestCase({"1", "null", "3", "null", "1"})
    @TestCase({"1", "null", "null", "4", "1"})

    @TestCase({"null", "2", "3", "4", "2"})
    @TestCase({"null", "2", "null", "4", "2"})
    @TestCase({"null", "2", "3", "null", "2"})
    @TestCase({"null", "2", "null", "null", "2"})

    @TestCase({"null", "null", "3", "4", "3"})
    @TestCase({"null", "null", "3", "null", "3"})

    @TestCase({"null", "null", "null", "4", "4"})
    void coalesce4(Long a, Long b, Long c, Long d, Long expected) {
        Long result = ObjectUtil.coalesce(a, b, c, d);

        assertThat(result, is(expected));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)

    @TestCase({"empty", "null"})

    @TestCase({"{null}", "null"})
    @TestCase({"A", "A"})

    @TestCase({"null,null", "null"})
    @TestCase({"null,B", "B"})
    @TestCase({"A,B", "A"})
    @TestCase({"A,null", "A"})

    @TestCase({"1,2,3", "1"})
    @TestCase({"null,2,3", "2"})
    @TestCase({"null,null,3", "3"})
    @TestCase({"null,null,null", "null"})
    @TestCase({"1,null,3", "1"})
    @TestCase({"1,null,null", "1"})
    @TestCase({"1,2,null", "1"})
    @TestCase({"null,2,null", "2"})
    @TestCase({"null,2,3", "2"})
    @TestCase({"null,null,3", "3"})

    @TestCase({"1,2,3,4", "1"})
    @TestCase({"null,2,3,4", "2"})
    @TestCase({"null,null,3,4", "3"})
    @TestCase({"null,null,null,4", "4"})
    @TestCase({"null,null,null,null", "null"})
    @TestCase({"1,null,3,4", "1"})
    @TestCase({"1,null,null,4", "1"})
    @TestCase({"1,null,null,null", "1"})
    @TestCase({"1,2,null,null", "1"})
    @TestCase({"1,null,3,null", "1"})
    @TestCase({"1,null,null,4", "1"})
    @TestCase({"null,2,3,4", "2"})
    @TestCase({"null,2,null,4", "2"})
    @TestCase({"null,2,3,null", "2"})
    @TestCase({"null,2,null,null", "2"})
    @TestCase({"null,null,3,4", "3"})
    @TestCase({"null,null,3,null", "3"})
    @TestCase({"null,null,null,4", "4"})

    @TestCase({"1,2,3,4,5", "1"})
    @TestCase({"null,2,3,4,5", "2"})
    @TestCase({"null,null,3,4,5", "3"})
    @TestCase({"null,null,null,4,5", "4"})
    @TestCase({"null,null,null,null,5", "5"})
    @TestCase({"null,null,null,null", "null"})
    @TestCase({"1,null,3,4,5", "1"})
    @TestCase({"1,null,null,4,5", "1"})
    @TestCase({"1,null,null,null,5", "1"})
    @TestCase({"1,null,null,null,null", "1"})
    @TestCase({"1,2,null,null,null", "1"})
    @TestCase({"1,null,3,null,null", "1"})
    @TestCase({"1,null,null,4,null", "1"})
    @TestCase({"1,null,null,null,5", "1"})
    @TestCase({"null,2,3,4,5", "2"})
    @TestCase({"null,2,null,4,5", "2"})
    @TestCase({"null,2,3,null,5", "2"})
    @TestCase({"null,2,3,4,null", "2"})
    @TestCase({"null,2,null,null,null", "2"})
    @TestCase({"null,null,3,4,5", "3"})
    @TestCase({"null,null,3,null,null", "3"})
    @TestCase({"null,null,null,4,null", "4"})
    @TestCase({"null,null,null,null,5", "5"})

    void coalesceN(String[] input, String expected) {
        String result = ObjectUtil.coalesce(input);

        assertThat(result, is(expected));
    }
}