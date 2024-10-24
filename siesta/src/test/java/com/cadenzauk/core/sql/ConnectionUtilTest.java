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

package com.cadenzauk.core.sql;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static com.cadenzauk.core.testutil.IsUtilityClass.isUtilityClass;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConnectionUtilTest {
    @Mock
    private Connection connection;

    @Mock
    private DatabaseMetaData metadata;

    @Mock
    private Statement statement;

    @Mock
    private PreparedStatement preparedStatement;

    @Test
    void utilityClass() {
        assertThat(ConnectionUtil.class, isUtilityClass());
    }

    @Test
    void getMetaData() throws SQLException {
        when(connection.getMetaData()).thenReturn(metadata);

        DatabaseMetaData result = ConnectionUtil.getMetaData(connection);

        verify(connection).getMetaData();
        verifyNoMoreInteractions(connection);
        assertThat(result, sameInstance(metadata));
    }

    @Test
    void getMetaDataThatThrows() throws SQLException {
        String message = RandomStringUtils.randomAlphabetic(50);
        when(connection.getMetaData()).thenThrow(new SQLException(message));

        calling(() -> ConnectionUtil.getMetaData(connection))
            .shouldThrow(RuntimeSqlException.class)
            .withCause(SQLException.class)
            .withMessage(is(message));

        verify(connection).getMetaData();
        verifyNoMoreInteractions(connection);
    }

    @Test
    void commit() throws SQLException {
        ConnectionUtil.commit(connection);

        verify(connection).commit();
        verifyNoMoreInteractions(connection);
    }

    @Test
    void commitThatThrows() throws SQLException {
        String message = RandomStringUtils.randomAlphabetic(50);
        doThrow(new SQLException(message)).when(connection).commit();

        calling(() -> ConnectionUtil.commit(connection))
            .shouldThrow(RuntimeSqlException.class)
            .withCause(SQLException.class)
            .withMessage(is(message));

        verify(connection).commit();
        verifyNoMoreInteractions(connection);
    }

    @Test
    void rollback() throws SQLException {
        ConnectionUtil.rollback(connection);

        verify(connection).rollback();
        verifyNoMoreInteractions(connection);
    }

    @Test
    void rollbackThatThrows() throws SQLException {
        String message = RandomStringUtils.randomAlphabetic(50);
        doThrow(new SQLException(message)).when(connection).rollback();

        calling(() -> ConnectionUtil.rollback(connection))
            .shouldThrow(RuntimeSqlException.class)
            .withCause(SQLException.class)
            .withMessage(is(message));

        verify(connection).rollback();
        verifyNoMoreInteractions(connection);
    }

    @Test
    void execute() throws SQLException {
        String sql = RandomStringUtils.randomAlphabetic(30);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.execute(sql)).thenReturn(true);

        boolean result = ConnectionUtil.execute(connection, sql);

        verify(connection).createStatement();
        verify(statement).execute(sql);
        verify(statement).close();
        verifyNoMoreInteractions(connection, statement);
        assertThat(result, is(true));
    }

    @Test
    void executeThatThrowsInCreateStatement() throws SQLException {
        String message = RandomStringUtils.randomAlphabetic(50);
        when(connection.createStatement()).thenThrow(new SQLException(message));

        calling(() -> ConnectionUtil.execute(connection, RandomStringUtils.randomAlphabetic(30)))
            .shouldThrow(RuntimeSqlException.class)
            .withCause(SQLException.class)
            .withMessage(is(message));

        verify(connection).createStatement();
        verifyNoMoreInteractions(connection);
    }

    @Test
    void executeThatThrowsInStatementExecute() throws SQLException {
        String message = RandomStringUtils.randomAlphabetic(50);
        String sql = RandomStringUtils.randomAlphabetic(30);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.execute(sql)).thenThrow(new SQLException(message));

        calling(() -> ConnectionUtil.execute(connection, sql))
            .shouldThrow(RuntimeSqlException.class)
            .withCause(SQLException.class)
            .withMessage(is(message));

        verify(connection).createStatement();
        verify(statement).execute(sql);
        verify(statement).close();
        verifyNoMoreInteractions(connection, statement);
    }

    @Test
    void prepare() throws SQLException {
        String sql = RandomStringUtils.randomAlphabetic(30);
        when(connection.prepareStatement(any())).thenReturn(preparedStatement);

        PreparedStatement actual = ConnectionUtil.prepare(connection, sql);

        verify(connection).prepareStatement(sql);
        verifyNoMoreInteractions(connection);
        assertThat(actual, sameInstance(preparedStatement));
    }

    @Test
    void prepareThatThrows() throws SQLException {
        String sql = RandomStringUtils.randomAlphabetic(30);
        when(connection.prepareStatement(sql)).thenThrow(new SQLException("Syntax error"));

        calling(() -> ConnectionUtil.prepare(connection, sql))
            .shouldThrow(RuntimeSqlException.class)
            .withCause(SQLException.class)
            .withMessage(is("Syntax error"));

        verify(connection).prepareStatement(sql);
    }
}
