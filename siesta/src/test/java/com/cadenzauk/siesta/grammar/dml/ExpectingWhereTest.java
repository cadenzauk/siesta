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

package com.cadenzauk.siesta.grammar.dml;

import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Transaction;
import com.cadenzauk.siesta.model.WidgetRow;
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

import static com.cadenzauk.siesta.grammar.expression.StringFunctions.lower;
import static com.cadenzauk.siesta.grammar.expression.StringFunctions.upper;
import static com.cadenzauk.siesta.grammar.expression.TypedExpression.column;
import static org.apache.commons.lang3.ArrayUtils.toArray;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExpectingWhereTest {
    @Mock
    private Transaction transaction;

    @Captor
    private ArgumentCaptor<String> sql;

    @Captor
    private ArgumentCaptor<Object[]> args;

    private static Arguments whereTestCase(BiFunction<Alias<WidgetRow>,ExpectingWhere,ExecutableStatementClause> whereClause, String expectedSql, Object[] expectedArgs) {
        return arguments(whereClause, expectedSql, expectedArgs);
    }

    private static Stream<Arguments> parametersForWhere() {
        return Stream.of(
            whereTestCase(
                (a, s) -> s.where(upper(WidgetRow::name)).isEqualTo("BOB"),
                "upper(w.NAME) = ?",
                toArray("BOB")),
            whereTestCase(
                (a, s) -> s.where(WidgetRow::manufacturerId).isEqualTo(1L),
                "w.MANUFACTURER_ID = ?",
                toArray(1L)),
            whereTestCase(
                (a, s) -> s.where("w", WidgetRow::manufacturerId).isEqualTo(1L),
                "w.MANUFACTURER_ID = ?",
                toArray(1L)),
            whereTestCase(
                (a, s) -> s.where(a, WidgetRow::manufacturerId).isEqualTo(1L),
                "w.MANUFACTURER_ID = ?",
                toArray(1L)),
            whereTestCase(
                (a, s) -> s.where(WidgetRow::description).isEqualTo("Fred"),
                "w.DESCRIPTION = ?",
                toArray("Fred")),
            whereTestCase(
                (a, s) -> s.where("w", WidgetRow::description).isEqualTo("Fred"),
                "w.DESCRIPTION = ?",
                toArray("Fred")),
            whereTestCase(
                (a, s) -> s.where(a, WidgetRow::description).isEqualTo("Fred"),
                "w.DESCRIPTION = ?",
                toArray("Fred")),
            whereTestCase(
                (a, s) -> s
                    .where(WidgetRow::manufacturerId).isEqualTo(1L)
                    .or(WidgetRow::manufacturerId).isEqualTo(2L)
                    .or("w", WidgetRow::manufacturerId).isEqualTo(3L)
                    .or(a, WidgetRow::manufacturerId).isEqualTo(4L)
                    .or(WidgetRow::description).isEqualTo("A")
                    .or("w", WidgetRow::description).isEqualTo("B")
                    .or(a, WidgetRow::description).isEqualTo("C")
                    .or(lower(a, WidgetRow::description)).isEqualTo("d"),
                "w.MANUFACTURER_ID = ? or w.MANUFACTURER_ID = ? or w.MANUFACTURER_ID = ? or w.MANUFACTURER_ID = ? or " +
                    "w.DESCRIPTION = ? or w.DESCRIPTION = ? or w.DESCRIPTION = ? or lower(w.DESCRIPTION) = ?",
                toArray(1L, 2L, 3L, 4L, "A", "B", "C", "d")),
            whereTestCase(
                (a, s) -> s
                    .where(WidgetRow::manufacturerId).isNotEqualTo(1L)
                    .and(WidgetRow::manufacturerId).isNotEqualTo(2L)
                    .and("w", WidgetRow::manufacturerId).isNotEqualTo(3L)
                    .and(a, WidgetRow::manufacturerId).isNotEqualTo(4L)
                    .and(WidgetRow::description).isNotEqualTo("A")
                    .and("w", WidgetRow::description).isNotEqualTo("B")
                    .and(a, WidgetRow::description).isNotEqualTo("C")
                    .and(upper(a, WidgetRow::description)).isNotEqualTo("D"),
                "w.MANUFACTURER_ID <> ? and w.MANUFACTURER_ID <> ? and w.MANUFACTURER_ID <> ? and w.MANUFACTURER_ID <> ? " +
                    "and w.DESCRIPTION <> ? and w.DESCRIPTION <> ? and w.DESCRIPTION <> ? and upper(w.DESCRIPTION) <> ?",
                toArray(1L, 2L, 3L, 4L, "A", "B", "C", "D")),
            whereTestCase(
                (a, s) -> s
                    .where(WidgetRow::manufacturerId).isEqualTo(1L)
                    .or(WidgetRow::name).isEqualTo("Fred")
                    .and(WidgetRow::description).isNotEqualTo("Foo")
                    .or(WidgetRow::description).isLessThan("A")
                    .or(WidgetRow::description).isGreaterThan("Z")
                    .and(WidgetRow::widgetId).isGreaterThanOrEqualTo(3L),
                "w.MANUFACTURER_ID = ? or w.NAME = ? and w.DESCRIPTION <> ? or w.DESCRIPTION < ? or w.DESCRIPTION > ? and w.WIDGET_ID >= ?",
                toArray(1L, "Fred", "Foo", "A", "Z", 3L)),
            whereTestCase(
                (a, s) -> s
                    .where(column(a, WidgetRow::manufacturerId).isEqualTo(1L)
                        .or(a, WidgetRow::manufacturerId).isEqualTo(2L))
                    .and(column(a, WidgetRow::name).isEqualTo("Fred")
                        .or(a, WidgetRow::name).isEqualTo("Barney")),
                "(w.MANUFACTURER_ID = ? or w.MANUFACTURER_ID = ?) and (w.NAME = ? or w.NAME = ?)",
                toArray(1L, 2L, "Fred", "Barney")),
            whereTestCase(
                (a, s) -> s
                    .where(column(a, WidgetRow::manufacturerId).isEqualTo(1L)
                        .or(a, WidgetRow::manufacturerId).isEqualTo(2L))
                    .or(column(a, WidgetRow::name).isEqualTo("Fred")
                        .or(a, WidgetRow::name).isEqualTo("Barney")),
                "(w.MANUFACTURER_ID = ? or w.MANUFACTURER_ID = ?) or (w.NAME = ? or w.NAME = ?)",
                toArray(1L, 2L, "Fred", "Barney")),
            whereTestCase(
                (a, s) -> s
                    .where(column(a, WidgetRow::manufacturerId).isEqualTo(1L)
                        .and(a, WidgetRow::manufacturerId).isEqualTo(2L))
                    .or(column(a, WidgetRow::name).isEqualTo("Fred")
                        .and(a, WidgetRow::name).isEqualTo("Barney")),
                "w.MANUFACTURER_ID = ? and w.MANUFACTURER_ID = ? or w.NAME = ? and w.NAME = ?",
                toArray(1L, 2L, "Fred", "Barney"))
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForWhere")
    void where(BiFunction<Alias<WidgetRow>,ExpectingWhere,ExecutableStatementClause> whereClause, String expectedSql, Object[] expectedArgs) {
        Database database = Database.newBuilder()
            .defaultSchema("SIESTA")
            .build();
        Alias<WidgetRow> w = database.table(WidgetRow.class).as("w");

        whereClause.apply(w, database.delete(w)).execute(transaction);

        verify(transaction).update(sql.capture(), args.capture());
        assertThat(sql.getValue(), is("delete from SIESTA.WIDGET w where " + expectedSql));
        assertThat(args.getValue(), is(expectedArgs));
    }
}
