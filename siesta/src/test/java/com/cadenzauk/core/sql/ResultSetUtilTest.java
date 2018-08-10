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

import com.cadenzauk.core.junit.TestCase;
import com.cadenzauk.core.junit.TestCaseArgumentsProvider;
import com.cadenzauk.core.mockito.MockUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;

import static com.cadenzauk.core.testutil.IsUtilityClass.isUtilityClass;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ArrayUtils.subarray;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultSetUtilTest {
    @Mock
    private ResultSet resultSet;

    @Mock
    private RowMapper<String> rowMapper;

    @Test
    void isUtility() {
        assertThat(ResultSetUtil.class, isUtilityClass());
    }

    @Test
    void getString() throws SQLException {
        String column = RandomStringUtils.randomAlphabetic(10);
        String expectedValue = RandomStringUtils.random(40);
        when(resultSet.getString(column)).thenReturn(expectedValue);

        String result = ResultSetUtil.getString(resultSet, column);

        assertThat(result, is(expectedValue));
        verify(resultSet, times(1)).getString(column);
        verifyNoMoreInteractions(resultSet);
    }

    @Test
    void close() throws SQLException {
        ResultSetUtil.close(resultSet);

        verify(resultSet, times(1)).close();
        verifyNoMoreInteractions(resultSet);
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase("true")
    @TestCase("false")
    void stream(boolean close) throws SQLException {
        int count = RandomUtils.nextInt(5, 10);
        String[] mappedRows = Stream.generate(() -> RandomStringUtils.randomAlphabetic(30)).limit(count).toArray(String[]::new);
        MockUtil.when(rowMapper.mapRow(resultSet)).thenReturn(mappedRows);
        MockUtil.when(resultSet.next()).thenReturn(count, true).thenReturn(false);

        Stream<String> result = ResultSetUtil.stream(resultSet, rowMapper);
        List<String> items = result.collect(toList());
        if (close) {
            result.close();
        }

        assertThat(items, contains(mappedRows));
        verify(resultSet, times(count + 1)).next();
        verify(rowMapper, times(count)).mapRow(resultSet);
        verify(resultSet, times(1)).close();
        verifyNoMoreInteractions(resultSet, rowMapper);
    }

    @Test
    void streamEarlyExit() throws SQLException {
        int count = RandomUtils.nextInt(5, 10);
        int limit = RandomUtils.nextInt(1, 4);
        String[] mappedRows = Stream.generate(() -> RandomStringUtils.randomAlphabetic(30)).limit(count).toArray(String[]::new);
        MockUtil.when(rowMapper.mapRow(resultSet)).thenReturn(mappedRows);
        MockUtil.when(resultSet.next()).thenReturn(count, true).thenReturn(false);

        Stream<String> result = ResultSetUtil.stream(resultSet, rowMapper);
        List<String> items = result.limit(limit).collect(toList());
        result.close();

        assertThat(items, contains(subarray(mappedRows, 0, limit)));
        verify(resultSet, times(limit)).next();
        verify(rowMapper, times(limit)).mapRow(resultSet);
        verify(resultSet, times(1)).close();
        verifyNoMoreInteractions(resultSet, rowMapper);
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase("true")
    @TestCase("false")
    void streamSupplier(boolean close) throws SQLException {
        int count = RandomUtils.nextInt(5, 10);
        String[] mappedRows = Stream.generate(() -> RandomStringUtils.random(30)).limit(count).toArray(String[]::new);
        MockUtil.when(rowMapper.mapRow(resultSet)).thenReturn(mappedRows);
        MockUtil.when(resultSet.next()).thenReturn(count, true).thenReturn(false);

        Stream<String> result = ResultSetUtil.stream(() -> resultSet, rowMapper);
        List<String> items = result.collect(toList());
        if (close) {
            result.close();
        }

        assertThat(items, contains(mappedRows));
        verify(resultSet, times(count + 1)).next();
        verify(rowMapper, times(count)).mapRow(resultSet);
        verify(resultSet, times(1)).close();
        verifyNoMoreInteractions(resultSet, rowMapper);
    }

    @Test
    void streamSupplierEarlyExit() throws SQLException {
        int count = RandomUtils.nextInt(5, 10);
        int limit = RandomUtils.nextInt(1, 4);
        String[] mappedRows = Stream.generate(() -> RandomStringUtils.randomAlphabetic(30)).limit(count).toArray(String[]::new);
        MockUtil.when(rowMapper.mapRow(resultSet)).thenReturn(mappedRows);
        MockUtil.when(resultSet.next()).thenReturn(count, true).thenReturn(false);

        Stream<String> result = ResultSetUtil.stream(() -> resultSet, rowMapper);
        List<String> items = result.limit(limit).collect(toList());
        result.close();

        assertThat(items, contains(subarray(mappedRows, 0, limit)));
        verify(resultSet, times(limit)).next();
        verify(rowMapper, times(limit)).mapRow(resultSet);
        verify(resultSet, times(1)).close();
        verifyNoMoreInteractions(resultSet, rowMapper);
    }
}