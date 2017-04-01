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

package com.cadenzauk.core.lang;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class StringUtilTest {
    @TestFactory
    Stream<DynamicTest> uppercaseFirst() {
        return Stream.of(
            uppercaseFirst(null, ""),
            uppercaseFirst("", ""),
            uppercaseFirst("a", "A"),
            uppercaseFirst("B", "B"),
            uppercaseFirst("abc", "Abc"),
            uppercaseFirst(" def", " def")
        );
    }

    private DynamicTest uppercaseFirst(String input, String expected) {
        return DynamicTest.dynamicTest(input + "->" + expected, () -> assertThat(StringUtil.uppercaseFirst(input), is(expected)));
    }

    @TestFactory
    Stream<DynamicTest> camelToUpper() {
        return Stream.of(
            camelToUpper(null, ""),
            camelToUpper("", ""),
            camelToUpper("a", "A"),
            camelToUpper("B", "B"),
            camelToUpper("abc", "ABC"),
            camelToUpper(" def", " DEF"),
            camelToUpper("aBC", "A_BC"),
            camelToUpper("camelCase", "CAMEL_CASE"),
            camelToUpper("camelCaseWithTLA", "CAMEL_CASE_WITH_TLA"),
            camelToUpper("camelCaseWithTLAInMiddle", "CAMEL_CASE_WITH_TLA_IN_MIDDLE"),
            camelToUpper("tlaAtStart", "TLA_AT_START"),
            camelToUpper("NotCamelCase", "NOT_CAMEL_CASE"),
            camelToUpper("TLANotCamelCase", "TLA_NOT_CAMEL_CASE")
        );
    }

    private DynamicTest camelToUpper(String input, String expectedResult) {
        return DynamicTest.dynamicTest(input + "->" + expectedResult, () -> assertThat(StringUtil.camelToUpper(input), is(expectedResult)));
    }

}