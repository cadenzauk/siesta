/*
 * Copyright (c) 2024 Cadenza United Kingdom Limited
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

package com.cadenzauk.siesta;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderTest {
    @Mock
    private Scope scope;
    @Mock
    private Dialect dialect;

    @ParameterizedTest
    @EnumSource(Order.class)
    void sqlIsDelegatedToDialect(Order order) {
        when(scope.dialect()).thenReturn(dialect);
        when(dialect.orderSql(any())).thenReturn("SOME ORDER");

        String result = order.sql(scope);

        verify(dialect).orderSql(order);
        assertThat(result, is("SOME ORDER"));
    }

    @ParameterizedTest
    @CsvSource({
        "ASC, asc",
        "DESC, desc",
        "ASC_NULLS_FIRST, asc nulls first",
        "DESC_NULLS_FIRST, desc nulls first",
        "ASC_NULLS_LAST, asc nulls last",
        "DESC_NULLS_LAST, desc nulls last"
    })
    void orderWithNullClauseReturnsFullSql(Order order, String expected) {
        String result = Order.orderWithNullClause(order);

        assertThat(result, is(expected));
    }

    @ParameterizedTest
    @CsvSource({
        "ASC, asc",
        "DESC, desc",
        "ASC_NULLS_FIRST, asc",
        "DESC_NULLS_FIRST, desc",
        "ASC_NULLS_LAST, asc",
        "DESC_NULLS_LAST, desc"
    })
    void orderWithoutNullClauseReturnsJustAscOrDesc(Order order, String expected) {
        String result = Order.orderWithoutNullClause(order);

        assertThat(result, is(expected));
    }
}
