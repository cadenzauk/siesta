/*
 * Copyright (c) 2020 Cadenza United Kingdom Limited
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

import com.cadenzauk.core.junit.TestCase;
import com.cadenzauk.core.junit.TestCaseArgumentsProvider;
import com.cadenzauk.core.testutil.FluentAssert;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ResultSetMetaDataUtilTest {
    @Mock
    private ResultSetMetaData rsm;

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"foo", "foo", "1"})
    @TestCase({"foo,bar", "bar", "2"})
    @TestCase({"foo,bar,fubar,baz", "fubar", "3"})
    @TestCase({"foo,bar,FUBAR,fubar", "fubar", "3"})
    void findColumnWithLabelFindsCorrectColumn(String[] labels, String label, int expected) throws SQLException {
        when(rsm.getColumnCount()).thenReturn(labels.length);
        for (int i = 0; i < expected; ++i) {
            doReturn(labels[i]).when(rsm).getColumnLabel(i + 1);
        }

        int result = ResultSetMetaDataUtil.findColumnWithLabel(rsm, label);

        assertThat(result, is(expected));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"foo", "fooo", "No such column as fooo in result set."})
    @TestCase({"foo,bar", "baz", "No such column as baz in result set."})
    void whenColumnNotFoundExceptionThrown(String[] labels, String label, String message) throws SQLException {
        when(rsm.getColumnCount()).thenReturn(labels.length);
        for (int i = 0; i < labels.length; ++i) {
            doReturn(labels[i]).when(rsm).getColumnLabel(i + 1);
        }

        FluentAssert.calling(() -> ResultSetMetaDataUtil.findColumnWithLabel(rsm, label))
            .shouldThrow(RuntimeSqlException.class)
            .withMessage(message);
    }
}