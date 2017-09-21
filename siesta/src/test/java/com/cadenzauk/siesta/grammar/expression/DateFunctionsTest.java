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

import com.cadenzauk.core.RandomValues;
import com.cadenzauk.siesta.model.TestRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

import static com.cadenzauk.core.testutil.IsUtilityClass.isUtilityClass;
import static com.cadenzauk.siesta.grammar.expression.DateFunctions.addDays;
import static com.cadenzauk.siesta.grammar.expression.DateFunctions.day;
import static com.cadenzauk.siesta.grammar.expression.DateFunctions.dayDiff;
import static com.cadenzauk.siesta.grammar.expression.DateFunctions.hour;
import static com.cadenzauk.siesta.grammar.expression.DateFunctions.hourDiff;
import static com.cadenzauk.siesta.grammar.expression.DateFunctions.minute;
import static com.cadenzauk.siesta.grammar.expression.DateFunctions.month;
import static com.cadenzauk.siesta.grammar.expression.DateFunctions.second;
import static com.cadenzauk.siesta.grammar.expression.DateFunctions.year;
import static com.cadenzauk.siesta.grammar.expression.TypedExpression.literal;
import static org.apache.commons.lang3.ArrayUtils.toArray;
import static org.hamcrest.MatcherAssert.assertThat;

class DateFunctionsTest extends FunctionTest {
    @Test
    void isUtility() {
        assertThat(DateFunctions.class, isUtilityClass());
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> parametersForFunctionTest() {
        LocalDate randomLocalDate = RandomValues.randomLocalDate();
        LocalDateTime randomLocalDateTime = RandomValues.randomLocalDateTime();
        ZonedDateTime randomZonedDateTime = RandomValues.randomZonedDateTime(ZoneId.systemDefault());
        return Stream.of(
            testCase(s -> DateFunctions.currentDate(), "current_date", toArray()),
            testCase(s -> DateFunctions.currentTimestampLocal(), "current_timestamp", toArray()),
            testCase(s -> DateFunctions.currentTimestamp(), "current_timestamp", toArray()),

            testCase(s -> year(randomLocalDate), "year(cast(? as date))", toArray(Date.valueOf(randomLocalDate))),
            testCase(s -> year(randomLocalDateTime), "year(cast(? as timestamp))", toArray(Timestamp.valueOf(randomLocalDateTime))),
            testCase(s -> year(randomZonedDateTime), "year(cast(? as timestamp))", toArray(Timestamp.valueOf(randomZonedDateTime.toLocalDateTime()))),
            testCase(s -> year(TypedExpression.value(randomZonedDateTime)), "year(cast(? as timestamp))", toArray(Timestamp.valueOf(randomZonedDateTime.toLocalDateTime()))),
            testCase(s -> year(TestRow::localDateReq), "year(s.LOCAL_DATE_REQ)", toArray()),
            testCase(s -> year(TestRow::localDateOpt), "year(s.LOCAL_DATE_OPT)", toArray()),
            testCase(s -> year("s", TestRow::localDateReq), "year(s.LOCAL_DATE_REQ)", toArray()),
            testCase(s -> year("s", TestRow::localDateOpt), "year(s.LOCAL_DATE_OPT)", toArray()),
            testCase(s -> year(s, TestRow::localDateReq), "year(s.LOCAL_DATE_REQ)", toArray()),
            testCase(s -> year(s, TestRow::localDateOpt), "year(s.LOCAL_DATE_OPT)", toArray()),
            
            testCase(s -> month(randomLocalDate), "month(cast(? as date))", toArray(Date.valueOf(randomLocalDate))),
            testCase(s -> month(randomLocalDateTime), "month(cast(? as timestamp))", toArray(Timestamp.valueOf(randomLocalDateTime))),
            testCase(s -> month(randomZonedDateTime), "month(cast(? as timestamp))", toArray(Timestamp.valueOf(randomZonedDateTime.toLocalDateTime()))),
            testCase(s -> month(TypedExpression.value(randomZonedDateTime)), "month(cast(? as timestamp))", toArray(Timestamp.valueOf(randomZonedDateTime.toLocalDateTime()))),
            testCase(s -> month(TestRow::localDateTimeReq), "month(s.LOCAL_DATE_TIME_REQ)", toArray()),
            testCase(s -> month(TestRow::localDateTimeOpt), "month(s.LOCAL_DATE_TIME_OPT)", toArray()),
            testCase(s -> month("s", TestRow::localDateTimeReq), "month(s.LOCAL_DATE_TIME_REQ)", toArray()),
            testCase(s -> month("s", TestRow::localDateTimeOpt), "month(s.LOCAL_DATE_TIME_OPT)", toArray()),
            testCase(s -> month(s, TestRow::localDateTimeReq), "month(s.LOCAL_DATE_TIME_REQ)", toArray()),
            testCase(s -> month(s, TestRow::localDateTimeOpt), "month(s.LOCAL_DATE_TIME_OPT)", toArray()),
            
            testCase(s -> day(randomLocalDate), "day(cast(? as date))", toArray(Date.valueOf(randomLocalDate))),
            testCase(s -> day(randomLocalDateTime), "day(cast(? as timestamp))", toArray(Timestamp.valueOf(randomLocalDateTime))),
            testCase(s -> day(randomZonedDateTime), "day(cast(? as timestamp))", toArray(Timestamp.valueOf(randomZonedDateTime.toLocalDateTime()))),
            testCase(s -> day(TypedExpression.value(randomZonedDateTime)), "day(cast(? as timestamp))", toArray(Timestamp.valueOf(randomZonedDateTime.toLocalDateTime()))),
            testCase(s -> day(TestRow::utcDateTimeReq), "day(s.UTC_DATE_TIME_REQ)", toArray()),
            testCase(s -> day(TestRow::utcDateTimeOpt), "day(s.UTC_DATE_TIME_OPT)", toArray()),
            testCase(s -> day("s", TestRow::utcDateTimeReq), "day(s.UTC_DATE_TIME_REQ)", toArray()),
            testCase(s -> day("s", TestRow::utcDateTimeOpt), "day(s.UTC_DATE_TIME_OPT)", toArray()),
            testCase(s -> day(s, TestRow::utcDateTimeReq), "day(s.UTC_DATE_TIME_REQ)", toArray()),
            testCase(s -> day(s, TestRow::utcDateTimeOpt), "day(s.UTC_DATE_TIME_OPT)", toArray()),

            testCase(s -> hour(randomLocalDateTime), "hour(cast(? as timestamp))", toArray(Timestamp.valueOf(randomLocalDateTime))),
            testCase(s -> hour(randomZonedDateTime), "hour(cast(? as timestamp))", toArray(Timestamp.valueOf(randomZonedDateTime.toLocalDateTime()))),
            testCase(s -> hour(TypedExpression.value(randomZonedDateTime)), "hour(cast(? as timestamp))", toArray(Timestamp.valueOf(randomZonedDateTime.toLocalDateTime()))),
            testCase(s -> hour(TestRow::localDateTimeReq), "hour(s.LOCAL_DATE_TIME_REQ)", toArray()),
            testCase(s -> hour(TestRow::localDateTimeOpt), "hour(s.LOCAL_DATE_TIME_OPT)", toArray()),
            testCase(s -> hour("s", TestRow::localDateTimeReq), "hour(s.LOCAL_DATE_TIME_REQ)", toArray()),
            testCase(s -> hour("s", TestRow::localDateTimeOpt), "hour(s.LOCAL_DATE_TIME_OPT)", toArray()),
            testCase(s -> hour(s, TestRow::localDateTimeReq), "hour(s.LOCAL_DATE_TIME_REQ)", toArray()),
            testCase(s -> hour(s, TestRow::localDateTimeOpt), "hour(s.LOCAL_DATE_TIME_OPT)", toArray()),

            testCase(s -> minute(randomLocalDateTime), "minute(cast(? as timestamp))", toArray(Timestamp.valueOf(randomLocalDateTime))),
            testCase(s -> minute(randomZonedDateTime), "minute(cast(? as timestamp))", toArray(Timestamp.valueOf(randomZonedDateTime.toLocalDateTime()))),
            testCase(s -> minute(TypedExpression.value(randomZonedDateTime)), "minute(cast(? as timestamp))", toArray(Timestamp.valueOf(randomZonedDateTime.toLocalDateTime()))),
            testCase(s -> minute(TestRow::utcDateTimeReq), "minute(s.UTC_DATE_TIME_REQ)", toArray()),
            testCase(s -> minute(TestRow::utcDateTimeOpt), "minute(s.UTC_DATE_TIME_OPT)", toArray()),
            testCase(s -> minute("s", TestRow::utcDateTimeReq), "minute(s.UTC_DATE_TIME_REQ)", toArray()),
            testCase(s -> minute("s", TestRow::utcDateTimeOpt), "minute(s.UTC_DATE_TIME_OPT)", toArray()),
            testCase(s -> minute(s, TestRow::utcDateTimeReq), "minute(s.UTC_DATE_TIME_REQ)", toArray()),
            testCase(s -> minute(s, TestRow::utcDateTimeOpt), "minute(s.UTC_DATE_TIME_OPT)", toArray()),

            testCase(s -> second(randomLocalDateTime), "second(cast(? as timestamp))", toArray(Timestamp.valueOf(randomLocalDateTime))),
            testCase(s -> second(randomZonedDateTime), "second(cast(? as timestamp))", toArray(Timestamp.valueOf(randomZonedDateTime.toLocalDateTime()))),
            testCase(s -> second(TypedExpression.value(randomZonedDateTime)), "second(cast(? as timestamp))", toArray(Timestamp.valueOf(randomZonedDateTime.toLocalDateTime()))),
            testCase(s -> second(TestRow::localDateTimeReq), "second(s.LOCAL_DATE_TIME_REQ)", toArray()),
            testCase(s -> second(TestRow::localDateTimeOpt), "second(s.LOCAL_DATE_TIME_OPT)", toArray()),
            testCase(s -> second("s", TestRow::localDateTimeReq), "second(s.LOCAL_DATE_TIME_REQ)", toArray()),
            testCase(s -> second("s", TestRow::localDateTimeOpt), "second(s.LOCAL_DATE_TIME_OPT)", toArray()),
            testCase(s -> second(s, TestRow::localDateTimeReq), "second(s.LOCAL_DATE_TIME_REQ)", toArray()),
            testCase(s -> second(s, TestRow::localDateTimeOpt), "second(s.LOCAL_DATE_TIME_OPT)", toArray()),
            testCase(s -> addDays(TypedExpression.value(randomZonedDateTime), 1), "dateadd(day, ?, cast(? as timestamp))", toArray(1, Timestamp.valueOf(randomZonedDateTime.toLocalDateTime()))),

            testCase(s -> addDays(TypedExpression.value(randomZonedDateTime), literal(4)), "dateadd(day, 4, cast(? as timestamp))", toArray(Timestamp.valueOf(randomZonedDateTime.toLocalDateTime()))),
            testCase(s -> addDays(TestRow::localDateTimeReq, 1), "dateadd(day, ?, s.LOCAL_DATE_TIME_REQ)", toArray(1)),
            testCase(s -> addDays(TestRow::localDateTimeOpt, 1), "dateadd(day, ?, s.LOCAL_DATE_TIME_OPT)", toArray(1)),
            testCase(s -> addDays("s", TestRow::localDateTimeReq, 2), "dateadd(day, ?, s.LOCAL_DATE_TIME_REQ)", toArray(2)),
            testCase(s -> addDays("s", TestRow::localDateTimeOpt, 2), "dateadd(day, ?, s.LOCAL_DATE_TIME_OPT)", toArray(2)),
            testCase(s -> addDays(s, TestRow::localDateTimeReq, 3), "dateadd(day, ?, s.LOCAL_DATE_TIME_REQ)", toArray(3)),
            testCase(s -> addDays(s, TestRow::localDateTimeOpt, 3), "dateadd(day, ?, s.LOCAL_DATE_TIME_OPT)", toArray(3)),

            testCase(s -> hourDiff(TypedExpression.value(randomZonedDateTime), literal(LocalDate.of(2013, 12, 25))), "datediff(hour, DATE '2013-12-25', cast(? as timestamp))", toArray(Timestamp.valueOf(randomZonedDateTime.toLocalDateTime()))),
            testCase(s -> hourDiff(TestRow::localDateTimeReq, LocalDate.of(2013, 12, 25)), "datediff(hour, cast(? as date), s.LOCAL_DATE_TIME_REQ)", toArray(Date.valueOf(LocalDate.of(2013, 12, 25)))),
            testCase(s -> hourDiff(TestRow::localDateTimeOpt, LocalDate.of(2013, 12, 25)), "datediff(hour, cast(? as date), s.LOCAL_DATE_TIME_OPT)", toArray(Date.valueOf(LocalDate.of(2013, 12, 25)))),
            testCase(s -> hourDiff("s", TestRow::localDateTimeReq, LocalDate.of(2013, 12, 25)), "datediff(hour, cast(? as date), s.LOCAL_DATE_TIME_REQ)", toArray(Date.valueOf(LocalDate.of(2013, 12, 25)))),
            testCase(s -> hourDiff("s", TestRow::localDateTimeOpt, LocalDate.of(2013, 12, 25)), "datediff(hour, cast(? as date), s.LOCAL_DATE_TIME_OPT)", toArray(Date.valueOf(LocalDate.of(2013, 12, 25)))),
            testCase(s -> hourDiff(s, TestRow::localDateTimeReq, LocalDate.of(2013, 12, 25)), "datediff(hour, cast(? as date), s.LOCAL_DATE_TIME_REQ)", toArray(Date.valueOf(LocalDate.of(2013, 12, 25)))),
            testCase(s -> hourDiff(s, TestRow::localDateTimeOpt, LocalDate.of(2013, 12, 25)), "datediff(hour, cast(? as date), s.LOCAL_DATE_TIME_OPT)", toArray(Date.valueOf(LocalDate.of(2013, 12, 25)))),

            testCase(s -> dayDiff(TypedExpression.value(randomZonedDateTime), literal(LocalDate.of(2013, 12, 25))), "cast(? as timestamp) - DATE '2013-12-25'", toArray(Timestamp.valueOf(randomZonedDateTime.toLocalDateTime()))),
            testCase(s -> dayDiff(TestRow::localDateTimeReq, LocalDate.of(2013, 12, 25)), "s.LOCAL_DATE_TIME_REQ - cast(? as date)", toArray(Date.valueOf(LocalDate.of(2013, 12, 25)))),
            testCase(s -> dayDiff(TestRow::localDateTimeOpt, LocalDate.of(2013, 12, 25)), "s.LOCAL_DATE_TIME_OPT - cast(? as date)", toArray(Date.valueOf(LocalDate.of(2013, 12, 25)))),
            testCase(s -> dayDiff("s", TestRow::localDateTimeReq, LocalDate.of(2013, 12, 25)), "s.LOCAL_DATE_TIME_REQ - cast(? as date)", toArray(Date.valueOf(LocalDate.of(2013, 12, 25)))),
            testCase(s -> dayDiff("s", TestRow::localDateTimeOpt, LocalDate.of(2013, 12, 25)), "s.LOCAL_DATE_TIME_OPT - cast(? as date)", toArray(Date.valueOf(LocalDate.of(2013, 12, 25)))),
            testCase(s -> dayDiff(s, TestRow::localDateTimeReq, LocalDate.of(2013, 12, 25)), "s.LOCAL_DATE_TIME_REQ - cast(? as date)", toArray(Date.valueOf(LocalDate.of(2013, 12, 25)))),
            testCase(s -> dayDiff(s, TestRow::localDateTimeOpt, LocalDate.of(2013, 12, 25)), "s.LOCAL_DATE_TIME_OPT - cast(? as date)", toArray(Date.valueOf(LocalDate.of(2013, 12, 25))))
        );
    }
}