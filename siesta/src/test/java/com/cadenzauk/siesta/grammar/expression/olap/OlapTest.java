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

import java.util.function.Function;
import java.util.stream.Stream;

import static com.cadenzauk.core.testutil.IsUtilityClass.isUtilityClass;
import static com.cadenzauk.siesta.grammar.expression.StringFunctions.length;
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
            testCase(s -> Olap.sum(s, SalespersonRow::commission), "sum(s.COMMISSION) over ()")
        );
    }

    private static Arguments testCase(Function<Alias<SalespersonRow>,InOlapExpectingPartitionBy<?>> f, String expectedSql) {
        return arguments(f, expectedSql);
    }
}