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

import com.cadenzauk.core.MockitoTest;
import com.cadenzauk.core.lang.RuntimeInstantiationException;
import com.cadenzauk.core.reflect.Factory;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class ConnectionUtilTest extends MockitoTest {
    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement expected;

    @Test
    void cannotInstantiate() {
        calling(() -> Factory.forClass(ConnectionUtil.class).get())
            .shouldThrow(RuntimeException.class)
            .withCause(InvocationTargetException.class)
            .withCause(RuntimeInstantiationException.class);
    }

    @Test
    void prepare() throws SQLException {
        when(connection.prepareStatement(any())).thenReturn(expected);

        PreparedStatement actual = ConnectionUtil.prepare(connection, "select * from bob");

        verify(connection).prepareStatement("select * from bob");
        verifyNoMoreInteractions(connection);
        assertThat(actual, sameInstance(expected));
    }

    @Test
    void prepareThatThrows() throws SQLException {
        when(connection.prepareStatement("select * from invalid")).thenThrow(new SQLException("Syntax error"));

        calling(() -> ConnectionUtil.prepare(connection, "select * from invalid"))
            .shouldThrow(RuntimeSqlException.class)
            .withCause(SQLException.class)
            .withMessage(is("Syntax error"));

        verify(connection).prepareStatement("select * from invalid");
    }
}