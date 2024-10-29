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

package com.cadenzauk.siesta.grammar.select;

import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.ColumnSpecifier;
import com.cadenzauk.siesta.Order;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.grammar.expression.UnresolvedColumn;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static co.unruly.matchers.OptionalMatchers.contains;
import static co.unruly.matchers.OptionalMatchers.empty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderingTest {
    @Mock
    private Scope scope;

    @Mock
    private Alias<Object> alias;

    @Test
    void orderingOfOptionalAliasAndColumnNameAndOrderIsHasSpecifiedValues() {
        Ordering result = Ordering.of(Optional.of("TBL"), "SOME_COLUMN", Order.ASC_NULLS_FIRST);

        assertThat(result.alias(), contains("TBL"));
        assertThat(result.column(), is("SOME_COLUMN"));
        assertThat(result.order(), is(Order.ASC_NULLS_FIRST));
    }

    @Test
    void orderingOfAliasAndColumnNameAndOrderIsHasSpecifiedValues() {
        Ordering result = Ordering.of("TBL", "SOME_COLUMN", Order.ASC_NULLS_FIRST);

        assertThat(result.alias(), contains("TBL"));
        assertThat(result.column(), is("SOME_COLUMN"));
        assertThat(result.order(), is(Order.ASC_NULLS_FIRST));
    }

    @Test
    void orderingOfAliasAndColumnNameIsHasOrderAsc() {
        Ordering result = Ordering.of("TBL", "SOME_COLUMN");

        assertThat(result.alias(), contains("TBL"));
        assertThat(result.column(), is("SOME_COLUMN"));
        assertThat(result.order(), is(Order.ASC));
    }

    @Test
    void orderingOfColumnNameAndOrderIsHasSpecifiedValuesAndEmptyAlias() {
        Ordering result = Ordering.of("SOME_COLUMN", Order.DESC_NULLS_FIRST);

        assertThat(result.alias(), empty());
        assertThat(result.column(), is("SOME_COLUMN"));
        assertThat(result.order(), is(Order.DESC_NULLS_FIRST));
    }

    @Test
    void orderingOfJustColumnNameHasEmptyAliasAndIsAscending() {
        Ordering result = Ordering.of("SOME_COLUMN");

        assertThat(result.alias(), empty());
        assertThat(result.column(), is("SOME_COLUMN"));
        assertThat(result.order(), is(Order.ASC));
    }

    @Test
    void toExpressionOfOrderingWithEmptyAliasHasEmptyAlias() {
        Ordering sut = Ordering.of("SOME_COLUMN");
        when(scope.findAlias(any(ColumnSpecifier.class), any())).thenAnswer(it -> alias);

        UnresolvedColumn<Object> result = sut.toExpression();

        assertThat(result.resolve(scope), equalTo(alias));
        verify(scope).findAlias(any(), eq(Optional.empty()));
    }

    @Test
    void toExpressionOfOrderingWithAliasHasSpecofiedAlias() {
        Ordering sut = Ordering.of("TBL", "SOME_COLUMN");
        when(scope.findAlias(any(ColumnSpecifier.class), any())).thenAnswer(it -> alias);

        UnresolvedColumn<Object> result = sut.toExpression();

        assertThat(result.resolve(scope), equalTo(alias));
        verify(scope).findAlias(any(), eq(Optional.of("TBL")));
    }
}
