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
import com.cadenzauk.core.lang.RuntimeInstantiationException;
import com.cadenzauk.core.reflect.Factory;
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.SqlExecutor;
import com.cadenzauk.siesta.test.model.SalespersonRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ObjectArrayArguments;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static com.cadenzauk.siesta.grammar.expression.StringFunctions.length;
import static com.cadenzauk.siesta.grammar.expression.StringFunctions.lower;
import static com.cadenzauk.siesta.grammar.expression.StringFunctions.substr;
import static com.cadenzauk.siesta.grammar.expression.StringFunctions.upper;
import static com.cadenzauk.siesta.grammar.expression.TypedExpression.value;
import static com.cadenzauk.siesta.test.model.TestDatabase.testDatabase;
import static org.apache.commons.lang3.ArrayUtils.toArray;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class StringFunctionsTest extends MockitoTest {
    @Mock
    private Scope scope;

    @Mock
    private TypedExpression<String> expression1;

    @Mock
    private TypedExpression<Integer> expression2;

    @Mock
    private SqlExecutor sqlExecutor;

    @Captor
    private ArgumentCaptor<String> sql;

    @Captor
    private ArgumentCaptor<Object[]> args;

    @Captor
    private ArgumentCaptor<RowMapper<?>> rowMapper;

    @Test
    void cannotInstantiate() {
        calling(() -> Factory.forClass(StringFunctions.class).get())
            .shouldThrow(RuntimeException.class)
            .withCause(InvocationTargetException.class)
            .withCause(RuntimeInstantiationException.class);
    }

    @Test
    void substrSql() {
        when(expression1.sql(scope)).thenReturn("sql1");
        when(expression2.sql(scope)).thenReturn("sql2");
        TypedExpression<String> sut = substr(expression1, expression2);

        String result = sut.sql(scope);

        assertThat(result, is("substr(sql1, sql2)"));
        verify(expression1).sql(scope);
        verify(expression2).sql(scope);
        verifyNoMoreInteractions(expression1, expression2);
    }

    private static Arguments testCase(Function<Alias<SalespersonRow>,TypedExpression<?>> sutSupplier, String expectedSql, Object[] expectedArgs) {
        return ObjectArrayArguments.create(sutSupplier, expectedSql, expectedArgs);
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> parametersFunctionTest() {
        return Stream.of(
            testCase(s -> upper("ABC"), "upper(?)", toArray("ABC")),
            testCase(s -> upper(lower("ABC")), "upper(lower(?))", toArray("ABC")),
            testCase(s -> upper(SalespersonRow::surname), "upper(s.SURNAME)", toArray()),
            testCase(s -> upper(SalespersonRow::middleNames), "upper(s.MIDDLE_NAMES)", toArray()),
            testCase(s -> upper("s", SalespersonRow::surname), "upper(s.SURNAME)", toArray()),
            testCase(s -> upper("s", SalespersonRow::middleNames), "upper(s.MIDDLE_NAMES)", toArray()),
            testCase(s -> upper(s, SalespersonRow::surname), "upper(s.SURNAME)", toArray()),
            testCase(s -> upper(s, SalespersonRow::middleNames), "upper(s.MIDDLE_NAMES)", toArray()),

            testCase(s -> lower("ABC"), "lower(?)", toArray("ABC")),
            testCase(s -> lower(upper("ABC")), "lower(upper(?))", toArray("ABC")),
            testCase(s -> lower(SalespersonRow::surname), "lower(s.SURNAME)", toArray()),
            testCase(s -> lower(SalespersonRow::middleNames), "lower(s.MIDDLE_NAMES)", toArray()),
            testCase(s -> lower("s", SalespersonRow::surname), "lower(s.SURNAME)", toArray()),
            testCase(s -> lower("s", SalespersonRow::middleNames), "lower(s.MIDDLE_NAMES)", toArray()),
            testCase(s -> lower(s, SalespersonRow::surname), "lower(s.SURNAME)", toArray()),
            testCase(s -> lower(s, SalespersonRow::middleNames), "lower(s.MIDDLE_NAMES)", toArray()),

            testCase(s -> length("ABC"), "length(?)", toArray("ABC")),
            testCase(s -> length(lower("ABC")), "length(lower(?))", toArray("ABC")),
            testCase(s -> length(SalespersonRow::surname), "length(s.SURNAME)", toArray()),
            testCase(s -> length(SalespersonRow::middleNames), "length(s.MIDDLE_NAMES)", toArray()),
            testCase(s -> length("s", SalespersonRow::surname), "length(s.SURNAME)", toArray()),
            testCase(s -> length("s", SalespersonRow::middleNames), "length(s.MIDDLE_NAMES)", toArray()),
            testCase(s -> length(s, SalespersonRow::surname), "length(s.SURNAME)", toArray()),
            testCase(s -> length(s, SalespersonRow::middleNames), "length(s.MIDDLE_NAMES)", toArray()),

            testCase(s -> substr("ABC", 1), "substr(?, ?)", toArray("ABC", 1)),
            testCase(s -> substr(upper(SalespersonRow::surname), 2), "substr(upper(s.SURNAME), ?)", toArray(2)),
            testCase(s -> substr(SalespersonRow::surname, 3), "substr(s.SURNAME, ?)", toArray(3)),
            testCase(s -> substr(SalespersonRow::middleNames, 4), "substr(s.MIDDLE_NAMES, ?)", toArray(4)),
            testCase(s -> substr("s", SalespersonRow::surname, 3), "substr(s.SURNAME, ?)", toArray(3)),
            testCase(s -> substr("s", SalespersonRow::middleNames, 4), "substr(s.MIDDLE_NAMES, ?)", toArray(4)),
            testCase(s -> substr(s, SalespersonRow::surname, 3), "substr(s.SURNAME, ?)", toArray(3)),
            testCase(s -> substr(s, SalespersonRow::middleNames, 4), "substr(s.MIDDLE_NAMES, ?)", toArray(4)),
            testCase(s -> substr(s.column(SalespersonRow::middleNames), value(5)), "substr(s.MIDDLE_NAMES, ?)", toArray(5)),
            testCase(s -> substr("ABCD", 1, 2), "substr(?, ?, ?)", toArray("ABCD", 1, 2)),

            testCase(s -> substr(upper(SalespersonRow::surname), 2, 1), "substr(upper(s.SURNAME), ?, ?)", toArray(2, 1)),
            testCase(s -> substr(SalespersonRow::surname, 3, 2), "substr(s.SURNAME, ?, ?)", toArray(3, 2)),
            testCase(s -> substr(SalespersonRow::middleNames, 4, 2), "substr(s.MIDDLE_NAMES, ?, ?)", toArray(4, 2)),
            testCase(s -> substr("s", SalespersonRow::surname, 3, 2), "substr(s.SURNAME, ?, ?)", toArray(3, 2)),
            testCase(s -> substr("s", SalespersonRow::middleNames, 4, 2), "substr(s.MIDDLE_NAMES, ?, ?)", toArray(4, 2)),
            testCase(s -> substr(s, SalespersonRow::surname, 3, 2), "substr(s.SURNAME, ?, ?)", toArray(3, 2)),
            testCase(s -> substr(s, SalespersonRow::middleNames, 4, 2), "substr(s.MIDDLE_NAMES, ?, ?)", toArray(4, 2)),
            testCase(s -> substr(s.column(SalespersonRow::middleNames), value(5), value(3)), "substr(s.MIDDLE_NAMES, ?, ?)", toArray(5, 3))
        );
    }

    @ParameterizedTest
    @MethodSource(names = "parametersFunctionTest")
    void functionTest(Function<Alias<SalespersonRow>,TypedExpression<?>> sutSupplier, String expectedSql, Object[] expectedArgs) {
        MockitoAnnotations.initMocks(this);
        Database database = testDatabase();
        Alias<SalespersonRow> alias = database.table(SalespersonRow.class).as("s");

        database.from(alias)
            .select(sutSupplier.apply(alias), "foo")
            .list(sqlExecutor);

        verify(sqlExecutor).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select " + expectedSql + " as foo from TEST.SALESPERSON as s"));
        assertThat(args.getValue(), is(expectedArgs));
    }
}
