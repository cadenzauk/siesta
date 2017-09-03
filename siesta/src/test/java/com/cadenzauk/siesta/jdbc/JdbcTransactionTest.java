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

package com.cadenzauk.siesta.jdbc;

import com.cadenzauk.core.MockitoTest;
import com.cadenzauk.core.RandomValues;
import com.cadenzauk.core.sql.RowMapper;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class JdbcTransactionTest extends MockitoTest {
    @Mock
    private JdbcSqlExecutor sqlExecutor;

    @Mock
    private Connection connection;

    @Test
    void connection() throws SQLException {
        when(sqlExecutor.connect()).thenReturn(connection);
        JdbcTransaction sut = new JdbcTransaction(sqlExecutor);

        Connection result = sut.connection();

        assertThat(result, sameInstance(connection));
        verify(connection).setAutoCommit(false);
        verifyNoMoreInteractions(sqlExecutor, connection);
    }

    @Test
    void commit() throws SQLException {
        when(sqlExecutor.connect()).thenReturn(connection);
        JdbcTransaction sut = new JdbcTransaction(sqlExecutor);

        sut.commit();

        verify(connection).setAutoCommit(false);
        verify(connection).commit();
        verifyNoMoreInteractions(sqlExecutor, connection);
    }

    @Test
    void rollback() throws SQLException {
        when(sqlExecutor.connect()).thenReturn(connection);
        JdbcTransaction sut = new JdbcTransaction(sqlExecutor);

        sut.rollback();

        verify(connection).setAutoCommit(false);
        verify(connection).rollback();
        verifyNoMoreInteractions(sqlExecutor, connection);
    }

    @Test
    void query() throws SQLException {
        when(sqlExecutor.connect()).thenReturn(connection);
        JdbcTransaction sut = new JdbcTransaction(sqlExecutor);
        String sql = RandomStringUtils.randomAlphabetic(20, 30);
        Object[] args = new Object[0];
        RowMapper<String> rowMapper = s -> "Hello";
        List<String> list = ImmutableList.of("A", "B");
        when(sqlExecutor.query(sut, sql, args, rowMapper)).thenReturn(list);

        List<String> result = sut.query(sql, args, rowMapper);

        assertThat(result, sameInstance(list));
        verify(connection).setAutoCommit(false);
        verify(sqlExecutor).query(sut, sql, args, rowMapper);
        verifyNoMoreInteractions(sqlExecutor, connection);
    }

    @Test
    void stream() throws SQLException {
        when(sqlExecutor.connect()).thenReturn(connection);
        JdbcTransaction sut = new JdbcTransaction(sqlExecutor);
        String sql = RandomStringUtils.randomAlphabetic(20, 30);
        Object[] args = new Object[0];
        RowMapper<String> rowMapper = s -> "Hello";
        Stream<String> stream = Stream.of("Hello");
        when(sqlExecutor.stream(sut, sql, args, rowMapper)).thenReturn(stream);

        Stream<String> result = sut.stream(sql, args, rowMapper);

        assertThat(result, sameInstance(stream));
        verify(connection).setAutoCommit(false);
        verify(sqlExecutor).stream(sut, sql, args, rowMapper);
        verifyNoMoreInteractions(sqlExecutor, connection);
    }

    @Test
    void update() throws SQLException {
        when(sqlExecutor.connect()).thenReturn(connection);
        JdbcTransaction sut = new JdbcTransaction(sqlExecutor);
        String sql = RandomStringUtils.randomAlphabetic(20, 30);
        Object[] args = new Object[0];
        int rowsUpdated = RandomValues.randomShort();
        when(sqlExecutor.update(sut, sql, args)).thenReturn(rowsUpdated);

        int result = sut.update(sql, args);

        assertThat(result, is(rowsUpdated));
        verify(connection).setAutoCommit(false);
        verify(sqlExecutor).update(sut, sql, args);
        verifyNoMoreInteractions(sqlExecutor, connection);
    }

    @Test
    void close() throws SQLException {
        when(sqlExecutor.connect()).thenReturn(connection);
        JdbcTransaction sut = new JdbcTransaction(sqlExecutor);

        sut.close();

        verify(connection).setAutoCommit(false);
        verify(connection).commit();
        verify(connection).close();
        verifyNoMoreInteractions(sqlExecutor, connection);
    }
}