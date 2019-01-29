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

import com.cadenzauk.core.junit.NullValue;
import com.cadenzauk.core.junit.TestCase;
import com.cadenzauk.core.junit.TestCaseArgumentsProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static com.cadenzauk.core.testutil.IsUtilityClass.isUtilityClass;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class StringUtilTest {
    @Test
    void isUtility() {
        assertThat(StringUtil.class, isUtilityClass());
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"null", ""})
    @TestCase({"", ""})
    @TestCase({"a", "a"})
    @TestCase({"B", "b"})
    @TestCase({"abc", "abc"})
    @TestCase({"ABC", "aBC"})
    @TestCase({" DEF", " DEF"})
    void lowercaseFirst(String input, String expected) {
        assertThat(StringUtil.lowercaseFirst(input), is(expected));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"null", ""})
    @TestCase({"", ""})
    @TestCase({"a", "A"})
    @TestCase({"B", "B"})
    @TestCase({"abc", "Abc"})
    @TestCase({" def", " def"})
    void uppercaseFirst(String input, String expected) {
        assertThat(StringUtil.uppercaseFirst(input), is(expected));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @NullValue("<NULL>")
    @TestCase({"<NULL>", ""})
    @TestCase({"", ""})
    @TestCase({"a", "A"})
    @TestCase({"B", "B"})
    @TestCase({"abc", "ABC"})
    @TestCase({" def", " DEF"})
    @TestCase({"aBC", "A_BC"})
    @TestCase({"camelCase", "CAMEL_CASE"})
    @TestCase({"camelCaseWithTLA", "CAMEL_CASE_WITH_TLA"})
    @TestCase({"camelCaseWithTLAInMiddle", "CAMEL_CASE_WITH_TLA_IN_MIDDLE"})
    @TestCase({"tlaAtStart", "TLA_AT_START"})
    @TestCase({"NotCamelCase", "NOT_CAMEL_CASE"})
    @TestCase({"TLANotCamelCase", "TLA_NOT_CAMEL_CASE"})
    void camelToUpper(String input, String expectedResult) {
        assertThat(StringUtil.camelToUpper(input), is(expectedResult));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"null", ""})
    @TestCase({"", ""})
    @TestCase({"-128", "80"})
    @TestCase({"-1", "ff"})
    @TestCase({"0", "00"})
    @TestCase({"15", "0f"})
    @TestCase({"127", "7f"})
    @TestCase({"0, 127, 10", "007f0a"})
    @TestCase({"0xde,0xad,0xbe,0xef", "deadbeef"})
    void hex(byte[] input, String expectedResult) {
        assertThat(StringUtil.hex(input), is(expectedResult));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"null", ""})
    @TestCase({"", ""})
    @TestCase({"-128", "200"})
    @TestCase({"-1", "377"})
    @TestCase({"0", "000"})
    @TestCase({"15", "017"})
    @TestCase({"127", "177"})
    @TestCase({"0, 127, 10", "000177012"})
    void octal(byte[] input, String expectedResult) {
        assertThat(StringUtil.octal(input), is(expectedResult));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"", "null", ""})
    @TestCase({"", "", ""})
    @TestCase({"null", "", ""})
    @TestCase({"null", "null", ""})

    @TestCase({"null", "A", "A"})
    @TestCase({"", "A", "A"})
    @TestCase({"A", "", "A"})
    @TestCase({"A", "null", "A"})

    @TestCase({"null", "xyz", "xyz"})
    @TestCase({"", "xyz", "xyz"})
    @TestCase({"z", "xy", "xyz"})
    @TestCase({"yz", "x", "xyz"})
    @TestCase({"xyz", "", "xyz"})
    @TestCase({"xyz", "null", "xyz"})
    void prepend(String input, String prefix, String expectedOutput) {
        String result = StringUtil.prepend(prefix).apply(input);

        assertThat(result, is(expectedOutput));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"", "null", ""})
    @TestCase({"", "", ""})
    @TestCase({"null", "", ""})
    @TestCase({"null", "null", ""})

    @TestCase({"null", "A", "A"})
    @TestCase({"", "A", "A"})
    @TestCase({"A", "", "A"})
    @TestCase({"A", "null", "A"})

    @TestCase({"null", "xyz", "xyz"})
    @TestCase({"", "xyz", "xyz"})
    @TestCase({"x", "yz", "xyz"})
    @TestCase({"xy", "z", "xyz"})
    @TestCase({"xyz", "", "xyz"})
    @TestCase({"xyz", "null", "xyz"})
    void append(String input, String suffix, String expectedOutput) {
        String result = StringUtil.append(suffix).apply(input);

        assertThat(result, is(expectedOutput));
    }
}
