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

class StringUtilTest {
    private static Stream<Arguments> parametersForUppercaseFirst() {
        return Stream.of(
            Arguments.of(null, ""),
            Arguments.of("", ""),
            Arguments.of("a", "A"),
            Arguments.of("B", "B"),
            Arguments.of("abc", "Abc"),
            Arguments.of(" def", " def")
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForUppercaseFirst")
    void uppercaseFirst(String input, String expected) {
        assertThat(StringUtil.uppercaseFirst(input), is(expected));
    }

    private static Stream<Arguments> parametersForCamelToUpper() {
        return Stream.of(
            Arguments.of(null, ""),
            Arguments.of("", ""),
            Arguments.of("a", "A"),
            Arguments.of("B", "B"),
            Arguments.of("abc", "ABC"),
            Arguments.of(" def", " DEF"),
            Arguments.of("aBC", "A_BC"),
            Arguments.of("camelCase", "CAMEL_CASE"),
            Arguments.of("camelCaseWithTLA", "CAMEL_CASE_WITH_TLA"),
            Arguments.of("camelCaseWithTLAInMiddle", "CAMEL_CASE_WITH_TLA_IN_MIDDLE"),
            Arguments.of("tlaAtStart", "TLA_AT_START"),
            Arguments.of("NotCamelCase", "NOT_CAMEL_CASE"),
            Arguments.of("TLANotCamelCase", "TLA_NOT_CAMEL_CASE")
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForCamelToUpper")
    void camelToUpper(String input, String expectedResult) {
        assertThat(StringUtil.camelToUpper(input), is(expectedResult));
    }

    private static Stream<Arguments> parametersForHex() {
        return Stream.of(
            Arguments.of(null, ""),
            Arguments.of(new byte[0], ""),
            Arguments.of(new byte[] { -128 }, "80"),
            Arguments.of(new byte[] { -1 }, "ff"),
            Arguments.of(new byte[] { 0 }, "00"),
            Arguments.of(new byte[] { 15 }, "0f"),
            Arguments.of(new byte[] { 127 }, "7f"),
            Arguments.of(new byte[] { 0, 127, 10 }, "007f0a"),
            Arguments.of(new byte[] { (byte)0xde, (byte)0xad, (byte)0xbe, (byte)0xef }, "deadbeef")
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForHex")
    void hex(byte[] input, String expectedResult) {
        assertThat(StringUtil.hex(input), is(expectedResult));
    }

    private static Stream<Arguments> parametersForOctal() {
        return Stream.of(
            Arguments.of(null, ""),
            Arguments.of(new byte[0], ""),
            Arguments.of(new byte[] { -128 }, "200"),
            Arguments.of(new byte[] { -1 }, "377"),
            Arguments.of(new byte[] { 0 }, "000"),
            Arguments.of(new byte[] { 15 }, "017"),
            Arguments.of(new byte[] { 127 }, "177"),
            Arguments.of(new byte[] { 0, 127, 10 }, "000177012")
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForOctal")
    void octal(byte[] input, String expectedResult) {
        assertThat(StringUtil.octal(input), is(expectedResult));
    }
}
