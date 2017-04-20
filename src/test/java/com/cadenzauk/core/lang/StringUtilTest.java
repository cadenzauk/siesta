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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.params.provider.ObjectArrayArguments.create;

class StringUtilTest {
    @SuppressWarnings("unused")
    static Stream<Arguments> parametersForUppercaseFirst() {
        return Stream.of(
            create(null, ""),
            create("", ""),
            create("a", "A"),
            create("B", "B"),
            create("abc", "Abc"),
            create(" def", " def")
        );
    }

    @ParameterizedTest
    @MethodSource(names = "parametersForUppercaseFirst")
    void uppercaseFirst(String input, String expected) {
        assertThat(StringUtil.uppercaseFirst(input), is(expected));
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> parametersForCamelToUpper() {
        return Stream.of(
            create(null, ""),
            create("", ""),
            create("a", "A"),
            create("B", "B"),
            create("abc", "ABC"),
            create(" def", " DEF"),
            create("aBC", "A_BC"),
            create("camelCase", "CAMEL_CASE"),
            create("camelCaseWithTLA", "CAMEL_CASE_WITH_TLA"),
            create("camelCaseWithTLAInMiddle", "CAMEL_CASE_WITH_TLA_IN_MIDDLE"),
            create("tlaAtStart", "TLA_AT_START"),
            create("NotCamelCase", "NOT_CAMEL_CASE"),
            create("TLANotCamelCase", "TLA_NOT_CAMEL_CASE")
        );
    }

    @ParameterizedTest
    @MethodSource(names = "parametersForCamelToUpper")
    void camelToUpper(String input, String expectedResult) {
        assertThat(StringUtil.camelToUpper(input), is(expectedResult));
    }
}
