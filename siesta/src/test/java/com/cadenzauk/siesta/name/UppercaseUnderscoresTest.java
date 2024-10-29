/*
 * Copyright (c) 2024 Cadenza United Kingdom Limited
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

package com.cadenzauk.siesta.name;

import com.cadenzauk.siesta.NamingStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class UppercaseUnderscoresTest {

    @ParameterizedTest
    @CsvSource({
        "MyClass,MY_CLASS",
        "UUIDList,UUID_LIST",
        "Foo,FOO",
        "anOddClass,AN_ODD_CLASS",
        "an_odder_class,AN_ODDER_CLASS",
    })
    void tableNameProducesTheExpectedResult(String rowClass, String expected) {
        NamingStrategy sut = new UppercaseUnderscores();

        String result = sut.tableName(rowClass);

        assertThat(result, is(expected));
    }

    @ParameterizedTest
    @CsvSource({
        "foo,FOO",
        "fooBar,FOO_BAR",
        "personUUID, PERSON_UUID",
        "isDeleted,IS_DELETED",
        "uuid,UUID",
    })
    void columnNameProducesTheExpectedResult(String fieldName, String expected) {
        NamingStrategy sut = new UppercaseUnderscores();

        String result = sut.columnName(fieldName);

        assertThat(result, is(expected));
    }

    @ParameterizedTest
    @CsvSource({
        "A,B,A_B",
        "'',B,_B",
    })
    void embeddedNameIsTheTwoColumnsJoinedByAnUnderscore(String parent, String child, String expected) {
        NamingStrategy sut = new UppercaseUnderscores();

        String result = sut.embeddedName(parent, child);

        assertThat(result, is(expected));
    }

    @ParameterizedTest
    @CsvSource({
        "PERSON_NAME, personName",
        "PERSON, person"
    })
    void propertyNameProducesTheExpectedResult(String columnName, String expected) {
        NamingStrategy sut = new UppercaseUnderscores();

        String result = sut.propertyName(columnName);

        assertThat(result, is(expected));
    }
}
