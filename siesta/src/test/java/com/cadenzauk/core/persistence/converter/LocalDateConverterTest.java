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

package com.cadenzauk.core.persistence.converter;

import com.cadenzauk.core.lang.UncheckedAutoCloseable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

import static com.cadenzauk.core.testutil.TemporalTestUtil.withTimeZone;
import static java.time.Month.DECEMBER;
import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static java.time.Month.MARCH;
import static java.time.Month.NOVEMBER;
import static java.time.Month.OCTOBER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class LocalDateConverterTest {
    private static Stream<Arguments> parameters() {
        return Stream.of(
            Arguments.of("America/Anchorage", localDate(1, JANUARY, 1900), date(1, JANUARY, 1900, "America/Anchorage")),
            Arguments.of("America/New_York", localDate(29, FEBRUARY, 2008), date(29, FEBRUARY, 2008, "America/New_York")),
            Arguments.of("America/New_York", localDate(3, NOVEMBER, 2007), date(3, NOVEMBER, 2007, "America/New_York")),
            Arguments.of("America/New_York", localDate(4, NOVEMBER, 2007), date(4, NOVEMBER, 2007, "America/New_York")),
            Arguments.of("America/New_York", localDate(5, NOVEMBER, 2007), date(5, NOVEMBER, 2007, "America/New_York")),
            Arguments.of("UTC", localDate(31, DECEMBER, 2010), date(31, DECEMBER, 2010, "UTC")),
            Arguments.of("UTC", localDate(1, JANUARY, 2011), date(1, JANUARY, 2011, "UTC")),
            Arguments.of("UTC", localDate(12, JANUARY, 2014), date(12, JANUARY, 2014, "UTC")),
            Arguments.of("UTC", localDate(31, DECEMBER, 9999), date(31, DECEMBER, 9999, "UTC")),
            Arguments.of("UTC", localDate(15, OCTOBER, 1582), date(15, OCTOBER, 1582, "UTC")),
            Arguments.of("Europe/London", localDate(25, MARCH, 2017), date(25, MARCH, 2017, "Europe/London")),
            Arguments.of("Europe/London", localDate(26, MARCH, 2017), date(26, MARCH, 2017, "Europe/London")),
            Arguments.of("Europe/London", localDate(27, MARCH, 2017), date(27, MARCH, 2017, "Europe/London")),
            Arguments.of("Europe/Berlin", localDate(15, JANUARY, 2001), date(15, JANUARY, 2001, "Europe/Berlin")),
            Arguments.of("Pacific/Apia", localDate(31, DECEMBER, 2100), date(31, DECEMBER, 2100, "Pacific/Apia")),
            Arguments.of("Pacific/Apia", null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void convertToDatabaseColumn(String timeZone, LocalDate input, Date expected) {
        try (UncheckedAutoCloseable ignored = withTimeZone(timeZone)) {
            LocalDateConverter sut = new LocalDateConverter();

            Date actual = sut.convertToDatabaseColumn(input);

            assertThat(actual, is(expected));
        }
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void convertToEntityAttribute(String timeZone, LocalDate expected, Date input) {
        try (UncheckedAutoCloseable ignored = withTimeZone(timeZone)) {
            LocalDateConverter sut = new LocalDateConverter();

            LocalDate actual = sut.convertToEntityAttribute(input);

            assertThat(actual, is(expected));
        }
    }

    private static LocalDate localDate(int day, Month month, int year) {
        return LocalDate.of(year, month, day);
    }

    private static Date date(int day, Month month, int year, String timeZone) {
        return new Date(ZonedDateTime.of(localDate(day, month, year), LocalTime.MIDNIGHT, ZoneId.of(timeZone)).toInstant().toEpochMilli());
    }
}