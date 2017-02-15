/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
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