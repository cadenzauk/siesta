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
import com.cadenzauk.core.lang.CompositeAutoCloseable;
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.core.sql.RuntimeSqlException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static org.apache.commons.lang3.ArrayUtils.toArray;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class JdbcSqlExecutorTest extends MockitoTest {
    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private JdbcTransaction transaction;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private RowMapper<String> rowMapper;

    @BeforeEach
    void wireUpMocks() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @Test
    void query() throws SQLException {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(rowMapper.mapRow(resultSet)).thenReturn("Fred").thenReturn("Barney").thenThrow(new AssertionError("Unexpected call to row mapper"));
        JdbcSqlExecutor sut = JdbcSqlExecutor.of(dataSource);
        String sql = "select name from foo where bar = ?";

        List<String> result = sut.query(connection, sql, toArray(2L), rowMapper);

        verify(connection).prepareStatement(sql);
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setObject(1, 2L);
        verify(preparedStatement).setFetchSize(0);
        verify(preparedStatement).close();
        verify(resultSet, times(3)).next();
        verify(resultSet).close();
        verify(rowMapper, times(2)).mapRow(resultSet);
        verifyNoMoreInteractions(connection, preparedStatement, resultSet, rowMapper);
        assertThat(result, contains("Fred", "Barney"));
    }

    @Test
    void stream() throws SQLException {
        CompositeAutoCloseable closeable = new CompositeAutoCloseable();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(rowMapper.mapRow(resultSet)).thenReturn("Fred").thenReturn("Barney").thenThrow(new AssertionError("Unexpected call to row mapper"));
        JdbcSqlExecutor sut = JdbcSqlExecutor.of(dataSource, 100);
        String sql = "select name from foo where bar = ?";

        List<String> result;
        try (Stream<String> stream = sut.stream(connection, sql, toArray("Bob"), rowMapper, closeable)) {
            result = stream.collect(Collectors.toList());
        }

        verify(connection).prepareStatement(sql);
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setObject(1, "Bob");
        verify(preparedStatement).setFetchSize(100);
        verify(preparedStatement).close();
        verify(resultSet, times(3)).next();
        verify(resultSet).close();
        verify(rowMapper, times(2)).mapRow(resultSet);
        verifyNoMoreInteractions(connection, preparedStatement, resultSet, rowMapper);
        assertThat(result, contains("Fred", "Barney"));
    }

    @Test
    void streamWhenMapperThrows() throws SQLException {
        CompositeAutoCloseable closeable = new CompositeAutoCloseable();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(rowMapper.mapRow(resultSet)).thenReturn("Fred").thenThrow(new IllegalArgumentException("Exception while mapping"));
        JdbcSqlExecutor sut = JdbcSqlExecutor.of(dataSource);
        String sql = "select name from foo where bar = ?";

        calling(() -> {
            try (Stream<String> stream = sut.stream(connection, sql, toArray("Bob", "Burt"), rowMapper, closeable)) {
                return stream.collect(Collectors.toList());
            }
        })
            .shouldThrow(IllegalArgumentException.class)
            .withMessage(is("Exception while mapping"));

        verify(connection).prepareStatement(sql);
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setObject(1, "Bob");
        verify(preparedStatement).setObject(2, "Burt");
        verify(preparedStatement).setFetchSize(0);
        verify(preparedStatement).close();
        verify(resultSet, times(2)).next();
        verify(resultSet).close();
        verify(rowMapper, times(2)).mapRow(resultSet);
        verifyNoMoreInteractions(connection, preparedStatement, resultSet, rowMapper);
    }

    @Test
    void streamWhenNextThrows() throws SQLException {
        CompositeAutoCloseable closeable = new CompositeAutoCloseable();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenThrow(new SQLException("Connection lost"));
        when(rowMapper.mapRow(resultSet)).thenReturn("Fred");
        JdbcSqlExecutor sut = JdbcSqlExecutor.of(dataSource);
        String sql = "select name from foo where bar = ?";

        calling(() -> {
            try (Stream<String> stream = sut.stream(connection, sql, toArray("Bob", "Burt"), rowMapper, closeable)) {
                return stream.collect(Collectors.toList());
            }
        })
            .shouldThrow(RuntimeSqlException.class)
            .withCause(SQLException.class)
            .withMessage(is("Connection lost"));

        verify(connection).prepareStatement(sql);
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setObject(1, "Bob");
        verify(preparedStatement).setObject(2, "Burt");
        verify(preparedStatement).setFetchSize(0);
        verify(preparedStatement).close();
        verify(resultSet, times(2)).next();
        verify(resultSet).close();
        verify(rowMapper).mapRow(resultSet);
        verifyNoMoreInteractions(connection, preparedStatement, resultSet, rowMapper);
    }

    @Test
    void streamWhenExecuteThrows() throws SQLException {
        CompositeAutoCloseable closeable = new CompositeAutoCloseable();
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("No permission"));
        JdbcSqlExecutor sut = JdbcSqlExecutor.of(dataSource);
        String sql = "select name from foo where bar = 'fred'";

        calling(() -> {
            try (Stream<String> stream = sut.stream(connection, sql, toArray(), rowMapper, closeable)) {
                return stream.collect(Collectors.toList());
            }
        })
            .shouldThrow(RuntimeException.class)
            .withCause(SQLException.class)
            .withMessage(is("No permission"));

        verify(connection).prepareStatement(sql);
        verify(preparedStatement).executeQuery();
        verify(preparedStatement).setFetchSize(0);
        verify(preparedStatement).close();
        verifyNoMoreInteractions(connection, preparedStatement, resultSet, rowMapper);
    }

    @Test
    void update() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(5);
        JdbcSqlExecutor sut = JdbcSqlExecutor.of(dataSource);
        String sql = "update foo set num = ?";

        int result = sut.update(connection, sql, toArray(3));

        verify(connection).prepareStatement(sql);
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).setObject(1, 3);
        verify(preparedStatement).close();
        verifyNoMoreInteractions(connection, preparedStatement, resultSet, rowMapper);
        assertThat(result, is(5));
    }

    @Test
    void updateWhenExecuteThrows() throws SQLException {
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Update failed."));
        JdbcSqlExecutor sut = JdbcSqlExecutor.of(dataSource);
        String sql = "update foo set num = ?";

        calling(() -> sut.update(connection, sql, toArray(3)))
            .shouldThrow(RuntimeSqlException.class)
            .withCause(SQLException.class)
            .withMessage(is("Update failed."));

        verify(connection).prepareStatement(sql);
        verify(preparedStatement).executeUpdate();
        verify(preparedStatement).setObject(1, 3);
        verify(preparedStatement).close();
        verifyNoMoreInteractions(connection, preparedStatement, resultSet, rowMapper);
    }

    @Test
    void updateWhenPrepareThrows() throws SQLException {
        String sql = "update foo set num = ?";
        when(connection.prepareStatement(sql)).thenThrow(new SQLException("Syntax error."));
        JdbcSqlExecutor sut = JdbcSqlExecutor.of(dataSource);

        calling(() -> sut.update(connection, sql, toArray(3)))
            .shouldThrow(RuntimeSqlException.class)
            .withCause(SQLException.class)
            .withMessage(is("Syntax error."));

        verify(connection).prepareStatement(sql);
        verifyNoMoreInteractions(connection, preparedStatement, resultSet, rowMapper);
    }
}