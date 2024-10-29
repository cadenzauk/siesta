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

import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Order;
import com.cadenzauk.siesta.RegularTableAlias;
import com.cadenzauk.siesta.Transaction;
import com.cadenzauk.siesta.dialect.AnsiDialect;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.model.SalespersonRow;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.BiFunction;
import java.util.stream.Stream;

import static com.cadenzauk.siesta.grammar.expression.StringFunctions.upper;
import static com.cadenzauk.siesta.grammar.expression.TypedExpression.literal;
import static com.cadenzauk.siesta.model.TestDatabase.testDatabase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExpectingOrderByTest {
    @Mock
    private Transaction transaction;
    @Captor
    private ArgumentCaptor<String> sql;
    @Captor
    private ArgumentCaptor<Object[]> args;
    @Captor
    private ArgumentCaptor<RowMapper<?>> rowMapper;

    private static Arguments orderByTestCase(String label, BiFunction<ExpectingOrderBy<Integer>, RegularTableAlias<SalespersonRow>,InOrderByExpectingThen<Integer>> method, String expectedSql) {
        BiFunction<ExpectingOrderBy<Integer>, RegularTableAlias<SalespersonRow>, InOrderByExpectingThen<Integer>> namedMethod = new BiFunction<>() {
                @Override
                public String toString() {
                    return label;
                }

                @Override
                public InOrderByExpectingThen<Integer> apply(ExpectingOrderBy<Integer> integerExpectingOrderBy, RegularTableAlias<SalespersonRow> salespersonRowRegularTableAlias) {
                    return method.apply(integerExpectingOrderBy, salespersonRowRegularTableAlias);
                }
            };
        return arguments(namedMethod, expectedSql);
    }

    private static Stream<Arguments> argsForOrderBy() {
        return Stream.of(
            orderByTestCase("colno", (s, a) -> s.orderBy(1), "1 asc"),
            orderByTestCase("colno, ASC", (s, a) -> s.orderBy(1, Order.ASC), "1 asc"),
            orderByTestCase("colno, ASC", (s, a) -> s.orderBy(1, Order.ASC_NULLS_FIRST), "1 asc nulls first"),
            orderByTestCase("colno, ASC", (s, a) -> s.orderBy(1, Order.ASC_NULLS_LAST), "1 asc nulls last"),
            orderByTestCase("colno, DESC", (s, a) -> s.orderBy(1, Order.DESC), "1 desc"),
            orderByTestCase("colno, DESC", (s, a) -> s.orderBy(1, Order.DESC_NULLS_FIRST), "1 desc nulls first"),
            orderByTestCase("colno, DESC", (s, a) -> s.orderBy(1, Order.DESC_NULLS_LAST), "1 desc nulls last"),
            orderByTestCase("func", (s, a) -> s.orderBy(SalespersonRow::surname), "s.SURNAME asc"),
            orderByTestCase("func, ASC", (s, a) -> s.orderBy(SalespersonRow::surname, Order.ASC), "s.SURNAME asc"),
            orderByTestCase("func, DESC", (s, a) -> s.orderBy(SalespersonRow::surname, Order.DESC), "s.SURNAME desc"),
            orderByTestCase("optfun", (s, a) -> s.orderBy(SalespersonRow::middleNames), "s.MIDDLE_NAMES asc"),
            orderByTestCase("optfun, ASC", (s, a) -> s.orderBy(SalespersonRow::middleNames, Order.ASC), "s.MIDDLE_NAMES asc"),
            orderByTestCase("optfun, DESC", (s, a) -> s.orderBy(SalespersonRow::middleNames, Order.DESC), "s.MIDDLE_NAMES desc"),
            orderByTestCase("aliasname, func", (s, a) -> s.orderBy("s", SalespersonRow::surname), "s.SURNAME asc"),
            orderByTestCase("aliasname, func, ASC", (s, a) -> s.orderBy("s", SalespersonRow::surname, Order.ASC), "s.SURNAME asc"),
            orderByTestCase("aliasname, func, DESC", (s, a) -> s.orderBy("s", SalespersonRow::surname, Order.DESC), "s.SURNAME desc"),
            orderByTestCase("aliasname, optfun", (s, a) -> s.orderBy("s", SalespersonRow::middleNames), "s.MIDDLE_NAMES asc"),
            orderByTestCase("aliasname, optfun, ASC", (s, a) -> s.orderBy("s", SalespersonRow::middleNames, Order.ASC), "s.MIDDLE_NAMES asc"),
            orderByTestCase("aliasname, optfun, DESC", (s, a) -> s.orderBy("s", SalespersonRow::middleNames, Order.DESC), "s.MIDDLE_NAMES desc"),
            orderByTestCase("alias, func", (s, a) -> s.orderBy(a, SalespersonRow::surname), "s.SURNAME asc"),
            orderByTestCase("alias, func, ASC", (s, a) -> s.orderBy(a, SalespersonRow::surname, Order.ASC), "s.SURNAME asc"),
            orderByTestCase("alias, func, DESC", (s, a) -> s.orderBy(a, SalespersonRow::surname, Order.DESC), "s.SURNAME desc"),
            orderByTestCase("alias, optfun", (s, a) -> s.orderBy(a, SalespersonRow::middleNames), "s.MIDDLE_NAMES asc"),
            orderByTestCase("alias, optfun, ASC", (s, a) -> s.orderBy(a, SalespersonRow::middleNames, Order.ASC), "s.MIDDLE_NAMES asc"),
            orderByTestCase("alias, optfun, DESC", (s, a) -> s.orderBy(a, SalespersonRow::middleNames, Order.DESC), "s.MIDDLE_NAMES desc"),
            orderByTestCase("colname", (s, a) -> s.orderBy("MIDDLE_NAMES"), "s.MIDDLE_NAMES asc"),
            orderByTestCase("colname, ASC", (s, a) -> s.orderBy("MIDDLE_NAMES", Order.ASC), "s.MIDDLE_NAMES asc"),
            orderByTestCase("colname, DESC", (s, a) -> s.orderBy("MIDDLE_NAMES", Order.DESC), "s.MIDDLE_NAMES desc"),
            orderByTestCase("aliasname, colname", (s, a) -> s.orderBy("s", "MIDDLE_NAMES"), "s.MIDDLE_NAMES asc"),
            orderByTestCase("aliasname, colname, ASC", (s, a) -> s.orderBy("s", "MIDDLE_NAMES", Order.ASC), "s.MIDDLE_NAMES asc"),
            orderByTestCase("aliasname, colname, DESC", (s, a) -> s.orderBy("s", "MIDDLE_NAMES", Order.DESC), "s.MIDDLE_NAMES desc"),
            orderByTestCase("alias, colname", (s, a) -> s.orderBy(a, "MIDDLE_NAMES"), "s.MIDDLE_NAMES asc"),
            orderByTestCase("alias, colname, ASC", (s, a) -> s.orderBy(a, "MIDDLE_NAMES", Order.ASC), "s.MIDDLE_NAMES asc"),
            orderByTestCase("alias, colname, DESC", (s, a) -> s.orderBy(a, "MIDDLE_NAMES", Order.DESC), "s.MIDDLE_NAMES desc")
        );
    }

    @ParameterizedTest
    @MethodSource("argsForOrderBy")
    void testOrderBy(BiFunction<ExpectingOrderBy<Integer>, Alias<SalespersonRow>,InOrderByExpectingThen<Integer>> method, String expectedSql) {
        Database database = testDatabase(new AnsiDialect());
        Alias<SalespersonRow> alias = database.table(SalespersonRow.class).as("s");

        method.apply(database.from(alias).select(literal(1), "ONE"), alias).list(transaction);

        verify(transaction).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select 1 as ONE from SIESTA.SALESPERSON s order by " + expectedSql));
        assertThat(args.getValue(), arrayWithSize(0));
    }
}
