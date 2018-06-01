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

import com.cadenzauk.core.lang.RuntimeInstantiationException;
import com.cadenzauk.core.reflect.Factory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PreparedStatementUtilTest {
    @Mock
    private PreparedStatement preparedStatement;

    @Test
    void cannotInstantiate() {
        calling(() -> Factory.forClass(PreparedStatementUtil.class).get())
            .shouldThrow(RuntimeException.class)
            .withCause(InvocationTargetException.class)
            .withCause(RuntimeInstantiationException.class);
    }

    @Test
    void setObject() throws SQLException {
        PreparedStatementUtil.setObject(preparedStatement, 3, "Foo");

        verify(preparedStatement).setObject(3, "Foo");
        verifyNoMoreInteractions(preparedStatement);
    }

    @Test
    void setObjectThatThrows() throws SQLException {
        doThrow(new SQLException("Bad param")).when(preparedStatement).setObject(anyInt(), any());

        calling(() -> PreparedStatementUtil.setObject(preparedStatement, 3, "Foo"))
            .shouldThrow(RuntimeSqlException.class)
            .withCause(SQLException.class)
            .withMessage(is("Bad param"));

        verify(preparedStatement).setObject(3, "Foo");
        verifyNoMoreInteractions(preparedStatement);
    }

    @Test
    void executeUpdate() throws SQLException {
        when(preparedStatement.executeUpdate()).thenReturn(121);

        int result = PreparedStatementUtil.executeUpdate(preparedStatement);

        verify(preparedStatement).executeUpdate();
        verifyNoMoreInteractions(preparedStatement);
        assertThat(result, is(121));
    }

    @Test
    void executeUpdateThatThrows() throws SQLException {
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Update failed"));

        calling(() -> PreparedStatementUtil.executeUpdate(preparedStatement))
            .shouldThrow(RuntimeSqlException.class)
            .withCause(SQLException.class)
            .withMessage(is("Update failed"));

        verify(preparedStatement).executeUpdate();
        verifyNoMoreInteractions(preparedStatement);
    }
}