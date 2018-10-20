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

package com.cadenzauk.siesta.grammar.expression;

import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.grammar.select.Select;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static co.unruly.matchers.StreamMatchers.contains;
import static com.cadenzauk.core.mockito.MockUtil.when;
import static com.cadenzauk.siesta.grammar.expression.ExistsExpression.exists;
import static com.cadenzauk.siesta.grammar.expression.ExistsExpression.notExists;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
class ExistsExpressionTest {
    @Mock
    private Scope scope;

    @Mock
    private Select<Integer> expression;

    @Test
    void existsSql() {
        when(expression.sql(scope)).thenReturn("(select 1 from dual)");
        ExistsExpression sut = exists(expression);

        String result = sut.sql(scope);

        assertThat(result, is("exists (select 1 from dual)"));
    }

    @Test
    void notExistsSql() {
        when(expression.sql(scope)).thenReturn("(select 1 from dual)");
        ExistsExpression sut = notExists(expression);

        String result = sut.sql(scope);

        assertThat(result, is("not exists (select 1 from dual)"));
    }

    @Test
    void existsArgs() {
        when(expression.args(scope)).thenReturn(Stream.of(BigDecimal.ONE, "2", 3, 4L));
        ExistsExpression sut = exists(expression);

        Stream<Object> result = sut.args(scope);

        assertThat(result, contains(BigDecimal.ONE, "2", 3, 4L));
    }

    @Test
    void notExistsArgs() {
        when(expression.args(scope)).thenReturn(Stream.of(BigDecimal.ZERO, "2", 4, 6L));
        ExistsExpression sut = notExists(expression);

        Stream<Object> result = sut.args(scope);

        assertThat(result, contains(BigDecimal.ZERO, "2", 4, 6L));
    }

    @Test
    void existsPrecedence() {
        ExistsExpression sut = exists(expression);

        Precedence result = sut.precedence();

        assertThat(result, is(Precedence.UNARY));
    }
    @Test
    void notExistsPrecedence() {
        ExistsExpression sut = notExists(expression);

        Precedence result = sut.precedence();

        assertThat(result, is(Precedence.UNARY));
    }
}