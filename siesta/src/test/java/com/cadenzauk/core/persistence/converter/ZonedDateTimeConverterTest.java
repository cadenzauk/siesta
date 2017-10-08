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
import com.cadenzauk.core.time.LocalDateUtil;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static com.cadenzauk.core.testutil.TemporalTestUtil.withTimeZone;
import static java.time.Month.DECEMBER;
import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static java.time.Month.MARCH;
import static java.time.Month.NOVEMBER;
import static java.time.Month.OCTOBER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class ZonedDateTimeConverterTest {
    private static Stream<Arguments> parameters() {
        return Stream.of(
            Arguments.of("America/Anchorage", localDate(1, JANUARY, 1900)),
            Arguments.of("America/New_York", localDate(29, FEBRUARY, 2008)),
            Arguments.of("America/New_York", localDate(3, NOVEMBER, 2007)),
            Arguments.of("America/New_York", localDate(4, NOVEMBER, 2007)),
            Arguments.of("America/New_York", localDate(5, NOVEMBER, 2007)),
            Arguments.of("UTC", localDate(31, DECEMBER, 2010)),
            Arguments.of("UTC", localDate(1, JANUARY, 2011)),
            Arguments.of("UTC", localDate(12, JANUARY, 2014)),
            Arguments.of("UTC", localDate(31, DECEMBER, 9999)),
            Arguments.of("UTC", localDate(15, OCTOBER, 1582)),
            Arguments.of("Europe/London", localDate(25, MARCH, 2017)),
            Arguments.of("Europe/London", localDate(26, MARCH, 2017)),
            Arguments.of("Europe/London", localDate(27, MARCH, 2017)),
            Arguments.of("Europe/Berlin", localDate(15, JANUARY, 2001)),
            Arguments.of("Pacific/Apia", localDate(31, DECEMBER, 2100)),
            Arguments.of("Pacific/Apia", null)
        );
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void convertToDatabaseColumn(String timeZone, LocalDate local) {
        ZonedDateTime input = zonedDateTime(timeZone, local);
        Timestamp expected = timestamp(timeZone, local);

        try (UncheckedAutoCloseable ignored = withTimeZone(timeZone)) {
            ZonedDateTimeConverter sut = new ZonedDateTimeConverter(ZoneId.of(timeZone));

            Timestamp actual = sut.convertToDatabaseColumn(input);

            assertThat(actual, is(expected));
        }
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void convertToEntityAttribute(String timeZone, LocalDate local) {
        Timestamp input = timestamp(timeZone, local);
        ZonedDateTime expected = zonedDateTime(timeZone, local);

        try (UncheckedAutoCloseable ignored = withTimeZone(timeZone)) {
            ZonedDateTimeConverter sut = new ZonedDateTimeConverter(ZoneId.of(timeZone));

            ZonedDateTime actual = sut.convertToEntityAttribute(input);

            assertThat(actual, is(expected));
        }
    }

    private static Stream<String> timezones() {
        return Stream.of(
            "America/Anchorage",
            "America/New_York",
            "UTC",
            "Europe/London",
            "Europe/Berlin",
            "Pacific/Apia"
        );
    }

    @ParameterizedTest
    @MethodSource("timezones")
    void convertToDatabaseColumnPreGregorianShouldThrow(String timeZone) {
        ZonedDateTime input = ZonedDateTime.of(LocalDateUtil.START_OF_GREGORIAN_CALENDAR, LocalTime.MIN, ZoneId.of(timeZone)).minusNanos(1);

        try (UncheckedAutoCloseable ignored = withTimeZone(timeZone)) {
            ZonedDateTimeConverter sut = new ZonedDateTimeConverter(ZoneId.of(timeZone));

            calling(() -> sut.convertToDatabaseColumn(input))
                .shouldThrow(IllegalArgumentException.class);
        }
    }

    @ParameterizedTest
    @MethodSource("timezones")
    void convertToDatabaseColumnGregorianShouldNotThrow(String timeZone) {
        ZonedDateTime input = ZonedDateTime.of(LocalDateUtil.START_OF_GREGORIAN_CALENDAR, LocalTime.MIN, ZoneId.of(timeZone));

        try (UncheckedAutoCloseable ignored = withTimeZone(timeZone)) {
            ZonedDateTimeConverter sut = new ZonedDateTimeConverter(ZoneId.of(timeZone));

            Timestamp result = sut.convertToDatabaseColumn(input);

            assertThat(result, notNullValue());
        }
    }

    @ParameterizedTest
    @MethodSource("timezones")
    void convertToEntityAttributePreGregorianShouldThrow(String timeZone) {
        ZonedDateTime dateTime = ZonedDateTime.of(LocalDateUtil.START_OF_GREGORIAN_CALENDAR, LocalTime.MIN, ZoneId.of(timeZone)).minusNanos(1);
        Timestamp input = Timestamp.from(dateTime.toInstant());

        try (UncheckedAutoCloseable ignored = withTimeZone(timeZone)) {
            ZonedDateTimeConverter sut = new ZonedDateTimeConverter(ZoneId.of(timeZone));

            calling(() -> sut.convertToEntityAttribute(input))
                .shouldThrow(IllegalArgumentException.class);
        }
    }

    @ParameterizedTest
    @MethodSource("timezones")
    void convertToEntityAttributeGregorianShouldNotThrow(String timeZone) {
        ZonedDateTime dateTime = ZonedDateTime.of(LocalDateUtil.START_OF_GREGORIAN_CALENDAR, LocalTime.MIN, ZoneId.of(timeZone));
        Timestamp input = Timestamp.from(dateTime.toInstant());

        try (UncheckedAutoCloseable ignored = withTimeZone(timeZone)) {
            ZonedDateTimeConverter sut = new ZonedDateTimeConverter(ZoneId.of(timeZone));

            ZonedDateTime result = sut.convertToEntityAttribute(input);

            assertThat(result, notNullValue());
        }
    }

    private static LocalDate localDate(int day, Month month, int year) {
        return LocalDate.of(year, month, day);
    }

    @Nullable
    private ZonedDateTime zonedDateTime(String timeZone, LocalDate localDate) {
        return localDate == null ? null : ZonedDateTime.of(localDate, LocalTime.MIDNIGHT, ZoneId.of(timeZone));
    }

    @Nullable
    private Timestamp timestamp(String timeZone, LocalDate localDate) {
        return localDate == null ? null : new Timestamp(zonedDateTime(timeZone, localDate).toInstant().toEpochMilli());
    }
}