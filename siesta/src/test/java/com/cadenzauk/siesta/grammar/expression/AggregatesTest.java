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

import com.cadenzauk.siesta.model.SalespersonRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static com.cadenzauk.core.testutil.IsUtilityClass.isUtilityClass;
import static com.cadenzauk.siesta.grammar.expression.Aggregates.avg;
import static com.cadenzauk.siesta.grammar.expression.Aggregates.count;
import static com.cadenzauk.siesta.grammar.expression.Aggregates.countDistinct;
import static com.cadenzauk.siesta.grammar.expression.Aggregates.max;
import static com.cadenzauk.siesta.grammar.expression.Aggregates.min;
import static com.cadenzauk.siesta.grammar.expression.Aggregates.sum;
import static com.cadenzauk.siesta.grammar.expression.StringFunctions.lower;
import static org.apache.commons.lang3.ArrayUtils.toArray;
import static org.hamcrest.MatcherAssert.assertThat;

class AggregatesTest extends FunctionTest {
    @Test
    void isUtility() {
        assertThat(Aggregates.class, isUtilityClass());
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> parametersForFunctionTest() {
        return Stream.of(
            testCase(s -> max("ABC"), "max(?)", toArray("ABC")),
            testCase(s -> max(lower("ABC")), "max(lower(?))", toArray("ABC")),
            testCase(s -> max(SalespersonRow::surname), "max(s.SURNAME)", toArray()),
            testCase(s -> max(SalespersonRow::middleNames), "max(s.MIDDLE_NAMES)", toArray()),
            testCase(s -> max("s", SalespersonRow::surname), "max(s.SURNAME)", toArray()),
            testCase(s -> max("s", SalespersonRow::middleNames), "max(s.MIDDLE_NAMES)", toArray()),
            testCase(s -> max(s, SalespersonRow::surname), "max(s.SURNAME)", toArray()),
            testCase(s -> max(s, SalespersonRow::middleNames), "max(s.MIDDLE_NAMES)", toArray()),

            testCase(s -> min("ABC"), "min(?)", toArray("ABC")),
            testCase(s -> min(lower("ABC")), "min(lower(?))", toArray("ABC")),
            testCase(s -> min(SalespersonRow::surname), "min(s.SURNAME)", toArray()),
            testCase(s -> min(SalespersonRow::middleNames), "min(s.MIDDLE_NAMES)", toArray()),
            testCase(s -> min("s", SalespersonRow::surname), "min(s.SURNAME)", toArray()),
            testCase(s -> min("s", SalespersonRow::middleNames), "min(s.MIDDLE_NAMES)", toArray()),
            testCase(s -> min(s, SalespersonRow::surname), "min(s.SURNAME)", toArray()),
            testCase(s -> min(s, SalespersonRow::middleNames), "min(s.MIDDLE_NAMES)", toArray()),

            testCase(s -> sum(1), "sum(?)", toArray(1)),
            testCase(s -> sum(TypedExpression.value(1.2)), "sum(?)", toArray(1.2)),
            testCase(s -> sum(SalespersonRow::numberOfSales), "sum(s.NUMBER_OF_SALES)", toArray()),
            testCase(s -> sum(SalespersonRow::commission), "sum(s.COMMISSION)", toArray()),
            testCase(s -> sum("s", SalespersonRow::numberOfSales), "sum(s.NUMBER_OF_SALES)", toArray()),
            testCase(s -> sum("s", SalespersonRow::commission), "sum(s.COMMISSION)", toArray()),
            testCase(s -> sum(s, SalespersonRow::numberOfSales), "sum(s.NUMBER_OF_SALES)", toArray()),
            testCase(s -> sum(s, SalespersonRow::commission), "sum(s.COMMISSION)", toArray()),

            testCase(s -> avg(1), "avg(?)", toArray(1)),
            testCase(s -> avg(TypedExpression.value(1.2)), "avg(?)", toArray(1.2)),
            testCase(s -> avg(SalespersonRow::numberOfSales), "avg(s.NUMBER_OF_SALES)", toArray()),
            testCase(s -> avg(SalespersonRow::commission), "avg(s.COMMISSION)", toArray()),
            testCase(s -> avg("s", SalespersonRow::numberOfSales), "avg(s.NUMBER_OF_SALES)", toArray()),
            testCase(s -> avg("s", SalespersonRow::commission), "avg(s.COMMISSION)", toArray()),
            testCase(s -> avg(s, SalespersonRow::numberOfSales), "avg(s.NUMBER_OF_SALES)", toArray()),
            testCase(s -> avg(s, SalespersonRow::commission), "avg(s.COMMISSION)", toArray()),

            testCase(s -> count(), "count(*)", toArray()),

            testCase(s -> countDistinct(lower("ABC")), "count(distinct lower(?))", toArray("ABC")),
            testCase(s -> countDistinct(SalespersonRow::surname), "count(distinct s.SURNAME)", toArray()),
            testCase(s -> countDistinct(SalespersonRow::middleNames), "count(distinct s.MIDDLE_NAMES)", toArray()),
            testCase(s -> countDistinct("s", SalespersonRow::surname), "count(distinct s.SURNAME)", toArray()),
            testCase(s -> countDistinct("s", SalespersonRow::middleNames), "count(distinct s.MIDDLE_NAMES)", toArray()),
            testCase(s -> countDistinct(s, SalespersonRow::surname), "count(distinct s.SURNAME)", toArray()),
            testCase(s -> countDistinct(s, SalespersonRow::middleNames), "count(distinct s.MIDDLE_NAMES)", toArray())
        );
    }
}