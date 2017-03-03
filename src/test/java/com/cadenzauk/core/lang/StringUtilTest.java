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

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class StringUtilTest {
    @SuppressWarnings("unused")
    private Object[] parametersForUppercaseFirst() {
        return new Object[] {
            new Object[] {null, ""},
            new Object[] {"", ""},
            new Object[] {"a", "A"},
            new Object[] {"B", "B"},
            new Object[] {"abc", "Abc"},
            new Object[] {" def", " def"}
        };
    }

    @Test
    @Parameters
    public void uppercaseFirst(String input, String expectedResult) {
        assertThat(StringUtil.uppercaseFirst(input), is(expectedResult));
    }

    @SuppressWarnings("unused")
    private Object[] parametersForCamelToUpper() {
        return new Object[] {
            new Object[] {null, ""},
            new Object[] {"", ""},
            new Object[] {"a", "A"},
            new Object[] {"B", "B"},
            new Object[] {"abc", "ABC"},
            new Object[] {" def", " DEF"},
            new Object[] {"aBC", "A_BC"},
            new Object[] {"camelCase", "CAMEL_CASE"},
            new Object[] {"camelCaseWithTLA", "CAMEL_CASE_WITH_TLA"},
            new Object[] {"camelCaseWithTLAInMiddle", "CAMEL_CASE_WITH_TLA_IN_MIDDLE"},
            new Object[] {"tlaAtStart", "TLA_AT_START"},
            new Object[] {"NotCamelCase", "NOT_CAMEL_CASE"},
            new Object[] {"TLANotCamelCase", "TLA_NOT_CAMEL_CASE"},
        };
    }

    @Test
    @Parameters
    public void camelToUpper(String input, String expectedResult) throws Exception {
        assertThat(StringUtil.camelToUpper(input), is(expectedResult));
    }

}