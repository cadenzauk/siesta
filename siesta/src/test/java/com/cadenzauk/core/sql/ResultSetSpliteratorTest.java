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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultSetSpliteratorTest {
    @Mock
    private ResultSet resultSet;

    @Mock
    private RowMapper<String> rowMapper;

    @Mock
    private Consumer<String> action;

    @Mock
    private Runnable atEnd;

    @Test
    void tryAdvanceWhenHaveNext() throws SQLException {
        when(resultSet.next()).thenReturn(true);
        when(rowMapper.mapRow(resultSet)).thenReturn("Bob");
        ResultSetSpliterator<String> sut = new ResultSetSpliterator<>(resultSet, rowMapper);

        boolean result = sut.tryAdvance(action);

        verify(resultSet).next();
        verify(rowMapper).mapRow(resultSet);
        verify(action).accept("Bob");
        verifyNoMoreInteractions(resultSet, rowMapper, action);
        assertThat(result, is(true));
    }

    @Test
    void tryAdvanceWhenNoNext() throws SQLException {
        when(resultSet.next()).thenReturn(false);
        ResultSetSpliterator<String> sut = new ResultSetSpliterator<>(resultSet, rowMapper);

        boolean result = sut.tryAdvance(action);

        verify(resultSet).next();
        verifyNoMoreInteractions(resultSet, rowMapper, action);
        assertThat(result, is(false));
    }

    @Test
    void tryAdvanceWhenNextThrows() throws SQLException {
        when(resultSet.next()).thenThrow(new SQLException("Connection lost"));
        ResultSetSpliterator<String> sut = new ResultSetSpliterator<>(resultSet, rowMapper);

        calling(() -> sut.tryAdvance(action))
            .shouldThrow(RuntimeSqlException.class)
            .withCause(SQLException.class)
            .withMessage(is("Connection lost"));

        verify(resultSet).next();
        verifyNoMoreInteractions(resultSet, rowMapper, action);
    }

    @Test
    void tryAdvanceWhenResultSetExhausted() throws SQLException {
        when(resultSet.next())
                .thenReturn(false)
                .thenThrow(new SQLException("ResultSet closed"));
        ResultSetSpliterator<String> sut = new ResultSetSpliterator<>(resultSet, rowMapper, atEnd);

        boolean result = sut.tryAdvance(action);

        verify(resultSet).next();
        verify(atEnd).run();
        verifyNoMoreInteractions(resultSet, rowMapper, action, atEnd);
        assertThat(result, is(false));

        boolean resultOnceExhausted = sut.tryAdvance(action);

        verifyNoMoreInteractions(resultSet, rowMapper, action, atEnd);
        assertThat(resultOnceExhausted, is(false));
    }

}