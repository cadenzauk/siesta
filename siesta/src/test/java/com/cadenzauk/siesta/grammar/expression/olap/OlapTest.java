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

package com.cadenzauk.siesta.grammar.expression.olap;

import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.dialect.AnsiDialect;
import com.cadenzauk.siesta.grammar.expression.Precedence;
import com.cadenzauk.siesta.model.SalespersonRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.cadenzauk.core.testutil.IsUtilityClass.isUtilityClass;
import static com.cadenzauk.siesta.grammar.expression.StringFunctions.length;
import static com.cadenzauk.siesta.grammar.expression.StringFunctions.upper;
import static com.cadenzauk.siesta.model.TestDatabase.testDatabase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class OlapTest {
    @Test
    void isUtility() {
        assertThat(Olap.class, isUtilityClass());
    }

    @ParameterizedTest(name = "{index}: {1}")
    @MethodSource("parametersForOlapTest")
    void olapFunction(Function<Alias<SalespersonRow>,InOlapExpectingPartitionBy<?>> f, String expectedSql) {
        Database database = testDatabase(new AnsiDialect());
        Alias<SalespersonRow> s = database.table(SalespersonRow.class).as("s");
        Scope scope = new Scope(database, s);

        InOlapExpectingPartitionBy<?> result = f.apply(s);

        assertThat(result.sql(scope), is(expectedSql));
        assertThat(result.precedence(), is(Precedence.UNARY));
    }

    private static Stream<Arguments> parametersForOlapTest() {
        return Stream.of(
            testCase(s -> Olap.rowNumber(), "row_number() over ()"),

            testCase(s -> Olap.sum(length(SalespersonRow::surname)), "sum(length(s.SURNAME)) over ()"),
            testCase(s -> Olap.sum(SalespersonRow::numberOfSales), "sum(s.NUMBER_OF_SALES) over ()"),
            testCase(s -> Olap.sum(SalespersonRow::commission), "sum(s.COMMISSION) over ()"),
            testCase(s -> Olap.sum("s", SalespersonRow::numberOfSales), "sum(s.NUMBER_OF_SALES) over ()"),
            testCase(s -> Olap.sum("s", SalespersonRow::commission), "sum(s.COMMISSION) over ()"),
            testCase(s -> Olap.sum(s, SalespersonRow::numberOfSales), "sum(s.NUMBER_OF_SALES) over ()"),
            testCase(s -> Olap.sum(s, SalespersonRow::commission), "sum(s.COMMISSION) over ()"),

            testCase(s -> Olap.firstValue(upper(SalespersonRow::surname)), "first_value(upper(s.SURNAME)) over ()"),
            testCase(s -> Olap.firstValue(SalespersonRow::numberOfSales), "first_value(s.NUMBER_OF_SALES) over ()"),
            testCase(s -> Olap.firstValue(SalespersonRow::commission), "first_value(s.COMMISSION) over ()"),
            testCase(s -> Olap.firstValue("s", SalespersonRow::numberOfSales), "first_value(s.NUMBER_OF_SALES) over ()"),
            testCase(s -> Olap.firstValue("s", SalespersonRow::commission), "first_value(s.COMMISSION) over ()"),
            testCase(s -> Olap.firstValue(s, SalespersonRow::numberOfSales), "first_value(s.NUMBER_OF_SALES) over ()"),
            testCase(s -> Olap.firstValue(s, SalespersonRow::commission), "first_value(s.COMMISSION) over ()"),

            testCase(s -> Olap.lastValue(upper(SalespersonRow::surname)), "last_value(upper(s.SURNAME)) over ()"),
            testCase(s -> Olap.lastValue(SalespersonRow::numberOfSales), "last_value(s.NUMBER_OF_SALES) over ()"),
            testCase(s -> Olap.lastValue(SalespersonRow::commission), "last_value(s.COMMISSION) over ()"),
            testCase(s -> Olap.lastValue("s", SalespersonRow::numberOfSales), "last_value(s.NUMBER_OF_SALES) over ()"),
            testCase(s -> Olap.lastValue("s", SalespersonRow::commission), "last_value(s.COMMISSION) over ()"),
            testCase(s -> Olap.lastValue(s, SalespersonRow::numberOfSales), "last_value(s.NUMBER_OF_SALES) over ()"),
            testCase(s -> Olap.lastValue(s, SalespersonRow::commission), "last_value(s.COMMISSION) over ()"),

            testCase(s -> Olap.lead(upper(SalespersonRow::surname)), "lead(upper(s.SURNAME)) over ()"),
            testCase(s -> Olap.lead(SalespersonRow::numberOfSales), "lead(s.NUMBER_OF_SALES) over ()"),
            testCase(s -> Olap.lead(SalespersonRow::commission), "lead(s.COMMISSION) over ()"),
            testCase(s -> Olap.lead("s", SalespersonRow::numberOfSales), "lead(s.NUMBER_OF_SALES) over ()"),
            testCase(s -> Olap.lead("s", SalespersonRow::commission), "lead(s.COMMISSION) over ()"),
            testCase(s -> Olap.lead(s, SalespersonRow::numberOfSales), "lead(s.NUMBER_OF_SALES) over ()"),
            testCase(s -> Olap.lead(s, SalespersonRow::commission), "lead(s.COMMISSION) over ()"),

            testCase(s -> Olap.lead(upper(SalespersonRow::surname), -1), "lead(upper(s.SURNAME), -1) over ()"),
            testCase(s -> Olap.lead(SalespersonRow::numberOfSales, -2), "lead(s.NUMBER_OF_SALES, -2) over ()"),
            testCase(s -> Olap.lead(SalespersonRow::commission, -3), "lead(s.COMMISSION, -3) over ()"),
            testCase(s -> Olap.lead("s", SalespersonRow::numberOfSales, 4), "lead(s.NUMBER_OF_SALES, 4) over ()"),
            testCase(s -> Olap.lead("s", SalespersonRow::commission, 5), "lead(s.COMMISSION, 5) over ()"),
            testCase(s -> Olap.lead(s, SalespersonRow::numberOfSales, 6), "lead(s.NUMBER_OF_SALES, 6) over ()"),
            testCase(s -> Olap.lead(s, SalespersonRow::commission, 7), "lead(s.COMMISSION, 7) over ()"),

            testCase(s -> Olap.lead(upper(SalespersonRow::surname), -1, "NOT FOUND"), "lead(upper(s.SURNAME), -1, 'NOT FOUND') over ()"),
            testCase(s -> Olap.lead(SalespersonRow::numberOfSales, -2, 0), "lead(s.NUMBER_OF_SALES, -2, 0) over ()"),
            testCase(s -> Olap.lead(SalespersonRow::commission, -3, BigDecimal.TEN), "lead(s.COMMISSION, -3, 10) over ()"),
            testCase(s -> Olap.lead("s", SalespersonRow::numberOfSales, 4, 1), "lead(s.NUMBER_OF_SALES, 4, 1) over ()"),
            testCase(s -> Olap.lead("s", SalespersonRow::commission, 5, BigDecimal.ZERO), "lead(s.COMMISSION, 5, 0) over ()"),
            testCase(s -> Olap.lead(s, SalespersonRow::numberOfSales, 6, 0), "lead(s.NUMBER_OF_SALES, 6, 0) over ()"),
            testCase(s -> Olap.lead(s, SalespersonRow::commission, 7, BigDecimal.ZERO), "lead(s.COMMISSION, 7, 0) over ()"),

            testCase(s -> Olap.lag(upper(SalespersonRow::surname)), "lag(upper(s.SURNAME)) over ()"),
            testCase(s -> Olap.lag(SalespersonRow::numberOfSales), "lag(s.NUMBER_OF_SALES) over ()"),
            testCase(s -> Olap.lag(SalespersonRow::commission), "lag(s.COMMISSION) over ()"),
            testCase(s -> Olap.lag("s", SalespersonRow::numberOfSales), "lag(s.NUMBER_OF_SALES) over ()"),
            testCase(s -> Olap.lag("s", SalespersonRow::commission), "lag(s.COMMISSION) over ()"),
            testCase(s -> Olap.lag(s, SalespersonRow::numberOfSales), "lag(s.NUMBER_OF_SALES) over ()"),
            testCase(s -> Olap.lag(s, SalespersonRow::commission), "lag(s.COMMISSION) over ()"),

            testCase(s -> Olap.lag(upper(SalespersonRow::surname), -1), "lag(upper(s.SURNAME), -1) over ()"),
            testCase(s -> Olap.lag(SalespersonRow::numberOfSales, -2), "lag(s.NUMBER_OF_SALES, -2) over ()"),
            testCase(s -> Olap.lag(SalespersonRow::commission, -3), "lag(s.COMMISSION, -3) over ()"),
            testCase(s -> Olap.lag("s", SalespersonRow::numberOfSales, 4), "lag(s.NUMBER_OF_SALES, 4) over ()"),
            testCase(s -> Olap.lag("s", SalespersonRow::commission, 5), "lag(s.COMMISSION, 5) over ()"),
            testCase(s -> Olap.lag(s, SalespersonRow::numberOfSales, 6), "lag(s.NUMBER_OF_SALES, 6) over ()"),
            testCase(s -> Olap.lag(s, SalespersonRow::commission, 7), "lag(s.COMMISSION, 7) over ()"),

            testCase(s -> Olap.lag(upper(SalespersonRow::surname), -1, "NOT FOUND"), "lag(upper(s.SURNAME), -1, 'NOT FOUND') over ()"),
            testCase(s -> Olap.lag(SalespersonRow::numberOfSales, -2, 0), "lag(s.NUMBER_OF_SALES, -2, 0) over ()"),
            testCase(s -> Olap.lag(SalespersonRow::commission, -3, BigDecimal.TEN), "lag(s.COMMISSION, -3, 10) over ()"),
            testCase(s -> Olap.lag("s", SalespersonRow::numberOfSales, 4, 1), "lag(s.NUMBER_OF_SALES, 4, 1) over ()"),
            testCase(s -> Olap.lag("s", SalespersonRow::commission, 5, BigDecimal.ZERO), "lag(s.COMMISSION, 5, 0) over ()"),
            testCase(s -> Olap.lag(s, SalespersonRow::numberOfSales, 6, 0), "lag(s.NUMBER_OF_SALES, 6, 0) over ()"),
            testCase(s -> Olap.lag(s, SalespersonRow::commission, 7, BigDecimal.ZERO), "lag(s.COMMISSION, 7, 0) over ()")
        );
    }

    private static Arguments testCase(Function<Alias<SalespersonRow>,InOlapExpectingPartitionBy<?>> f, String expectedSql) {
        return arguments(f, expectedSql);
    }
}
