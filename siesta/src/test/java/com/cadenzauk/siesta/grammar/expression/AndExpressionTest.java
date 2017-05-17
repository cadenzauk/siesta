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

package com.cadenzauk.siesta.grammar.expression;

import com.cadenzauk.core.MockitoTest;
import com.cadenzauk.core.reflect.util.ClassUtil;
import com.cadenzauk.core.reflect.util.FieldUtil;
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.Scope;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.stream.Stream;

import static org.apache.commons.lang3.ArrayUtils.toArray;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class AndExpressionTest extends MockitoTest {
    @Mock
    private Scope scope;

    @Mock
    private BooleanExpression lhs;

    @Mock
    private BooleanExpression rhs;

    @Mock
    private BooleanExpression appended;

    @Mock
    private RowMapper<String> rowMapper;

    @Test
    void sql() {
        when(lhs.sql(scope)).thenReturn("lhsSql");
        when(rhs.sql(scope)).thenReturn("rhsSql");
        when(lhs.precedence()).thenReturn(Precedence.OR);
        when(rhs.precedence()).thenReturn(Precedence.UNARY);
        AndExpression sut = new AndExpression(lhs, rhs);

        String sql = sut.sql(scope);

        assertThat(sql, is("(lhsSql) and rhsSql"));
        verify(lhs).sql(scope);
        verify(rhs).sql(scope);
        verify(lhs).precedence();
        verify(rhs).precedence();
        verifyNoMoreInteractions(lhs, rhs, scope);
    }

    @Test
    void args() {
        when(lhs.args(scope)).thenReturn(Stream.of("ABC", 124L));
        when(rhs.args(scope)).thenReturn(Stream.of(BigDecimal.ONE, null));
        AndExpression sut = new AndExpression(lhs, rhs);

        Object[] args = sut.args(scope).toArray();

        assertThat(args, is(toArray("ABC", 124L, BigDecimal.ONE, null)));
        verify(lhs).args(scope);
        verify(rhs).args(scope);
        verifyNoMoreInteractions(lhs, rhs, scope);
    }

    @Test
    void precedence() {
        AndExpression sut = new AndExpression(lhs, rhs);

        Precedence precedence = sut.precedence();

        assertThat(precedence, is(Precedence.AND));
        verifyNoMoreInteractions(lhs, rhs, scope);
    }

    @Test
    void appendOr() {
        AndExpression sut = new AndExpression(lhs, rhs);

        BooleanExpression result = sut.appendOr(appended);

        assertThat(result, instanceOf(OrExpression.class));
        assertThat((Iterable<BooleanExpression>)FieldUtil.get("expressions", result), contains(sut, appended));
        verifyNoMoreInteractions(lhs, rhs, scope, appended);
    }

    @SuppressWarnings("unchecked")
    @Test
    void appendAnd() {
        AndExpression sut = new AndExpression(lhs, rhs);

        BooleanExpression result = sut.appendAnd(appended);

        assertThat(result, sameInstance(sut));
        assertThat((Iterable<BooleanExpression>)FieldUtil.get("expressions", sut), contains(lhs, rhs, appended));
        verifyNoMoreInteractions(lhs, rhs, scope, appended);
    }

}