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

package com.cadenzauk.core.sql;

import com.cadenzauk.core.junit.NullValue;
import com.cadenzauk.core.junit.TestCase;
import com.cadenzauk.core.junit.TestCaseArgumentsProvider;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ForeignKeyNameTest {
    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @NullValue("NULL")
    @TestCase({"A.B.C.D", "A", "B", "C", "D"})
    @TestCase({"B.C.D", "NULL", "B", "C", "D"})
    @TestCase({"C.D", "NULL", "NULL", "C", "D"})
    @TestCase({"D", "NULL", "NULL", "NULL", "D"})
    void parseString(String input, String expectedCatalog, String expectedSchema, String expectedTable, String expectedName) {
        ForeignKeyName result = ForeignKeyName.parseString(input);

        assertThat(result.catalog(), is(Optional.ofNullable(expectedCatalog)));
        assertThat(result.schema(), is(Optional.ofNullable(expectedSchema)));
        assertThat(result.tableName(), is(Optional.ofNullable(expectedTable)));
        assertThat(result.name(), is(expectedName));
    }
}