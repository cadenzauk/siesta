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

import com.cadenzauk.siesta.model.TestRow;
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
    static Stream<Arguments> parametersForFunctionTest() {
        return Stream.of(
            testCase(s -> max("ABC"), "max(?)", toArray("ABC")),
            testCase(s -> max(lower("ABC")), "max(lower(?))", toArray("ABC")),
            testCase(s -> max(TestRow::stringReq), "max(s.STRING_REQ)", toArray()),
            testCase(s -> max(TestRow::stringOpt), "max(s.STRING_OPT)", toArray()),
            testCase(s -> max("s", TestRow::stringReq), "max(s.STRING_REQ)", toArray()),
            testCase(s -> max("s", TestRow::stringOpt), "max(s.STRING_OPT)", toArray()),
            testCase(s -> max(s, TestRow::stringReq), "max(s.STRING_REQ)", toArray()),
            testCase(s -> max(s, TestRow::stringOpt), "max(s.STRING_OPT)", toArray()),

            testCase(s -> min("ABC"), "min(?)", toArray("ABC")),
            testCase(s -> min(lower("ABC")), "min(lower(?))", toArray("ABC")),
            testCase(s -> min(TestRow::stringReq), "min(s.STRING_REQ)", toArray()),
            testCase(s -> min(TestRow::stringOpt), "min(s.STRING_OPT)", toArray()),
            testCase(s -> min("s", TestRow::stringReq), "min(s.STRING_REQ)", toArray()),
            testCase(s -> min("s", TestRow::stringOpt), "min(s.STRING_OPT)", toArray()),
            testCase(s -> min(s, TestRow::stringReq), "min(s.STRING_REQ)", toArray()),
            testCase(s -> min(s, TestRow::stringOpt), "min(s.STRING_OPT)", toArray()),

            testCase(s -> sum(1), "sum(?)", toArray(1)),
            testCase(s -> sum(TypedExpression.value(1.2)), "sum(?)", toArray(1.2)),
            testCase(s -> sum(TestRow::integerReq), "sum(s.INTEGER_REQ)", toArray()),
            testCase(s -> sum(TestRow::decimalOpt), "sum(s.DECIMAL_OPT)", toArray()),
            testCase(s -> sum("s", TestRow::integerReq), "sum(s.INTEGER_REQ)", toArray()),
            testCase(s -> sum("s", TestRow::decimalOpt), "sum(s.DECIMAL_OPT)", toArray()),
            testCase(s -> sum(s, TestRow::integerReq), "sum(s.INTEGER_REQ)", toArray()),
            testCase(s -> sum(s, TestRow::decimalOpt), "sum(s.DECIMAL_OPT)", toArray()),

            testCase(s -> avg(1), "avg(?)", toArray(1)),
            testCase(s -> avg(TypedExpression.value(1.2)), "avg(?)", toArray(1.2)),
            testCase(s -> avg(TestRow::integerReq), "avg(s.INTEGER_REQ)", toArray()),
            testCase(s -> avg(TestRow::decimalOpt), "avg(s.DECIMAL_OPT)", toArray()),
            testCase(s -> avg("s", TestRow::integerReq), "avg(s.INTEGER_REQ)", toArray()),
            testCase(s -> avg("s", TestRow::decimalOpt), "avg(s.DECIMAL_OPT)", toArray()),
            testCase(s -> avg(s, TestRow::integerReq), "avg(s.INTEGER_REQ)", toArray()),
            testCase(s -> avg(s, TestRow::decimalOpt), "avg(s.DECIMAL_OPT)", toArray()),

            testCase(s -> count(), "count(*)", toArray()),

            testCase(s -> countDistinct(lower("ABC")), "count(distinct lower(?))", toArray("ABC")),
            testCase(s -> countDistinct(TestRow::stringReq), "count(distinct s.STRING_REQ)", toArray()),
            testCase(s -> countDistinct(TestRow::stringOpt), "count(distinct s.STRING_OPT)", toArray()),
            testCase(s -> countDistinct("s", TestRow::stringReq), "count(distinct s.STRING_REQ)", toArray()),
            testCase(s -> countDistinct("s", TestRow::stringOpt), "count(distinct s.STRING_OPT)", toArray()),
            testCase(s -> countDistinct(s, TestRow::stringReq), "count(distinct s.STRING_REQ)", toArray()),
            testCase(s -> countDistinct(s, TestRow::stringOpt), "count(distinct s.STRING_OPT)", toArray())
        );
    }
}