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

import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.dialect.AnsiDialect;
import com.cadenzauk.siesta.model.WidgetRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.cadenzauk.siesta.grammar.expression.CoalesceFunction.coalesce;
import static com.cadenzauk.siesta.model.TestDatabase.testDatabase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoalesceFunctionTest {
    @Mock
    private Scope scope;

    @Mock
    private TypedExpression<String> expression1;

    @Mock
    private TypedExpression<String> expression2;

    @Mock
    private RowMapper<String> rowMapper;

    @Test
    void sql() {
        when(expression1.sql(scope)).thenReturn("sql1");
        when(expression2.sql(scope)).thenReturn("sql2");
        CoalesceFunction<String> sut = coalesce(expression1).orElse(expression2);

        String result = sut.sql(scope);

        assertThat(result, is("coalesce(sql1, sql2)"));
        verify(expression1).sql(scope);
        verify(expression2).sql(scope);
        verifyNoMoreInteractions(expression1, expression2);
    }

    @Test
    void args() {
        when(expression1.args(scope)).thenAnswer(z -> Stream.of("arg1"));
        when(expression2.args(scope)).thenAnswer(z -> Stream.of("arg2", "arg3"));
        CoalesceFunction<String> sut = coalesce(expression1).orElse(expression2);

        Object[] result = sut.args(scope).toArray();

        assertThat(result, arrayWithSize(3));
        assertThat(result[0], is("arg1"));
        assertThat(result[1], is("arg2"));
        assertThat(result[2], is("arg3"));
        verify(expression1).args(scope);
        verify(expression2).args(scope);
        verifyNoMoreInteractions(expression1, expression2);
    }

    @Test
    void precedence() {
        CoalesceFunction<String> sut = coalesce(expression1).orElse(expression2);

        Precedence precedence = sut.precedence();

        assertThat(precedence, is(Precedence.UNARY));
        verifyNoMoreInteractions(expression1, expression2);
    }

    @Test
    void label() {
        CoalesceFunction<String> sut1 = coalesce(expression1).orElse(expression2);
        CoalesceFunction<String> sut2 = coalesce(expression1).orElse(expression2);
        when(scope.newLabel()).thenReturn(345L).thenReturn(245L);

        String label1 = sut1.label(scope);
        String label2 = sut1.label(scope);
        String label3 = sut2.label(scope);

        assertThat(label1, is("coalesce_345"));
        assertThat(label2, is("coalesce_345"));
        assertThat(label3, is("coalesce_245"));
    }

    @Test
    void rowMapper() {
        CoalesceFunction<String> sut = coalesce(expression1).orElse(expression2);
        when(expression1.rowMapper(scope, Optional.of("custom_label"))).thenReturn(rowMapper);

        RowMapper<String> result = sut.rowMapper(scope, Optional.of("custom_label"));

        assertThat(result, sameInstance(rowMapper));
        verify(expression1).rowMapper(scope, Optional.of("custom_label"));
        verifyNoMoreInteractions(expression1, expression2);
    }

    private static Arguments coalesceTestCase(Function<Alias<WidgetRow>,CoalesceFunction<String>> f, String expected) {
        return arguments(f, expected);
    }

    private static Stream<Arguments> parametersForCoalesce() {
        return Stream.of(
            coalesceTestCase(w -> coalesce(WidgetRow::description).orElse("Bob"), "coalesce(w.DESCRIPTION, ?)"),
            coalesceTestCase(w -> coalesce(WidgetRow::name).orElse("Bob"), "coalesce(w.NAME, ?)"),
            coalesceTestCase(w -> coalesce(WidgetRow::name).orElse(WidgetRow::description), "coalesce(w.NAME, w.DESCRIPTION)"),
            coalesceTestCase(w -> coalesce(WidgetRow::description).orElse(WidgetRow::name), "coalesce(w.DESCRIPTION, w.NAME)"),
            coalesceTestCase(w -> coalesce("w", WidgetRow::description).orElse("Bob"), "coalesce(w.DESCRIPTION, ?)"),
            coalesceTestCase(w -> coalesce("w", WidgetRow::name).orElse("Bob"), "coalesce(w.NAME, ?)"),
            coalesceTestCase(w -> coalesce("w", WidgetRow::name).orElse("w", WidgetRow::description), "coalesce(w.NAME, w.DESCRIPTION)"),
            coalesceTestCase(w -> coalesce("w", WidgetRow::description).orElse("w", WidgetRow::name), "coalesce(w.DESCRIPTION, w.NAME)"),
            coalesceTestCase(w -> coalesce(w, WidgetRow::description).orElse("Bob"), "coalesce(w.DESCRIPTION, ?)"),
            coalesceTestCase(w -> coalesce(w, WidgetRow::name).orElse("Bob"), "coalesce(w.NAME, ?)"),
            coalesceTestCase(w -> coalesce(w, WidgetRow::name).orElse(w, WidgetRow::description), "coalesce(w.NAME, w.DESCRIPTION)"),
            coalesceTestCase(w -> coalesce(w, WidgetRow::description).orElse(w, WidgetRow::name), "coalesce(w.DESCRIPTION, w.NAME)")
        );
    }

    @ParameterizedTest(name = "{index}: {1}")
    @MethodSource({"parametersForCoalesce"})
    void coalesceTest(Function<Alias<WidgetRow>,CoalesceFunction<String>> f, String expected) {
            Database database = testDatabase(new AnsiDialect());
            Alias<WidgetRow> w = database.table(WidgetRow.class).as("w");

            String sql = database.from(w)
                .select(f.apply(w), "name")
                .where(WidgetRow::manufacturerId).isEqualTo(2L)
                .sql();

            assertThat(sql, is("select " + expected + " as name " +
                "from SIESTA.WIDGET w " +
                "where w.MANUFACTURER_ID = ?"));
    }
}