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
import com.cadenzauk.core.tuple.Tuple;
import com.cadenzauk.core.tuple.Tuple2;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static java.time.Month.DECEMBER;
import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static java.time.Month.MARCH;
import static java.time.Month.NOVEMBER;
import static java.time.Month.OCTOBER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class ZonedDateTimeConverterTest extends TemporalConverterTest {
    private Stream<Tuple2<String,LocalDate>> parameters() {
        return Stream.of(
            Tuple.of("America/Anchorage", localDate(1, JANUARY, 1900)),
            Tuple.of("America/New_York", localDate(29, FEBRUARY, 2008)),
            Tuple.of("America/New_York", localDate(3, NOVEMBER, 2007)),
            Tuple.of("America/New_York", localDate(4, NOVEMBER, 2007)),
            Tuple.of("America/New_York", localDate(5, NOVEMBER, 2007)),
            Tuple.of("UTC", localDate(31, DECEMBER, 2010)),
            Tuple.of("UTC", localDate(1, JANUARY, 2011)),
            Tuple.of("UTC", localDate(12, JANUARY, 2014)),
            Tuple.of("UTC", localDate(31, DECEMBER, 9999)),
            Tuple.of("UTC", localDate(15, OCTOBER, 1582)),
            Tuple.of("Europe/London", localDate(25, MARCH, 2017)),
            Tuple.of("Europe/London", localDate(26, MARCH, 2017)),
            Tuple.of("Europe/London", localDate(27, MARCH, 2017)),
            Tuple.of("Europe/Berlin", localDate(15, JANUARY, 2001)),
            Tuple.of("Pacific/Apia", localDate(31, DECEMBER, 2100)),
            Tuple.of("Pacific/Apia", null)
        );
    }

    @TestFactory
    Stream<DynamicTest> convertToDatabaseColumn() {
        return parameters().map(p -> dynamicTest(p.toString(), () -> convertToDatabaseColumn(p.item1(), p.item2())));
    }

    private void convertToDatabaseColumn(String timeZone, LocalDate local) {
        ZonedDateTime input = zonedDateTime(timeZone, local);
        Timestamp expected = timestamp(timeZone, local);

        try (UncheckedAutoCloseable ignored = withTimeZone(timeZone)) {
            ZonedDateTimeConverter sut = new ZonedDateTimeConverter(ZoneId.of(timeZone));

            Timestamp actual = sut.convertToDatabaseColumn(input);

            assertThat(actual, is(expected));
        }
    }

    @TestFactory
    Stream<DynamicTest> convertToEntityAttribute() {
        return parameters().map(p -> dynamicTest(p.toString(), () -> convertToEntityAttribute(p.item1(), p.item2())));

    }

    private void convertToEntityAttribute(String timeZone, LocalDate local) {
        Timestamp input = timestamp(timeZone, local);
        ZonedDateTime expected = zonedDateTime(timeZone, local);

        try (UncheckedAutoCloseable ignored = withTimeZone(timeZone)) {
            ZonedDateTimeConverter sut = new ZonedDateTimeConverter(ZoneId.of(timeZone));

            ZonedDateTime actual = sut.convertToEntityAttribute(input);

            assertThat(actual, is(expected));
        }
    }

    private Stream<String> timezones() {
        return Stream.of(
            "America/Anchorage",
            "America/New_York",
            "UTC",
            "Europe/London",
            "Europe/Berlin",
            "Pacific/Apia"
        );
    }

    @TestFactory
    Stream<DynamicTest> convertToDatabaseColumnPreGregorianShouldThrow() {
        return timezones().map(p -> dynamicTest(p, () -> convertToDatabaseColumnPreGregorianShouldThrow(p)));
    }

    private void convertToDatabaseColumnPreGregorianShouldThrow(String timeZone) {
        ZonedDateTime input = ZonedDateTime.of(LocalDateUtil.START_OF_GREGORIAN_CALENDAR, LocalTime.MIN, ZoneId.of(timeZone)).minusNanos(1);

        try (UncheckedAutoCloseable ignored = withTimeZone(timeZone)) {
            ZonedDateTimeConverter sut = new ZonedDateTimeConverter(ZoneId.of(timeZone));

            calling(() -> sut.convertToDatabaseColumn(input))
                .shouldThrow(IllegalArgumentException.class);
        }
    }

    @TestFactory
    Stream<DynamicTest> convertToDatabaseColumnGregorianShouldNotThrow() {
        return timezones().map(p -> dynamicTest(p, () -> convertToDatabaseColumnGregorianShouldNotThrow(p)));
    }

    private void convertToDatabaseColumnGregorianShouldNotThrow(String timeZone) {
        ZonedDateTime input = ZonedDateTime.of(LocalDateUtil.START_OF_GREGORIAN_CALENDAR, LocalTime.MIN, ZoneId.of(timeZone));

        try (UncheckedAutoCloseable ignored = withTimeZone(timeZone)) {
            ZonedDateTimeConverter sut = new ZonedDateTimeConverter(ZoneId.of(timeZone));

            Timestamp result = sut.convertToDatabaseColumn(input);

            assertThat(result, notNullValue());
        }
    }

    @TestFactory
    Stream<DynamicTest> convertToEntityAttributePreGregorianShouldThrow() {
        return timezones().map(p -> dynamicTest(p, () -> convertToEntityAttributePreGregorianShouldThrow(p)));
    }

    private void convertToEntityAttributePreGregorianShouldThrow(String timeZone) {
        ZonedDateTime dateTime = ZonedDateTime.of(LocalDateUtil.START_OF_GREGORIAN_CALENDAR, LocalTime.MIN, ZoneId.of(timeZone)).minusNanos(1);
        Timestamp input = Timestamp.from(dateTime.toInstant());

        try (UncheckedAutoCloseable ignored = withTimeZone(timeZone)) {
            ZonedDateTimeConverter sut = new ZonedDateTimeConverter(ZoneId.of(timeZone));

            calling(() -> sut.convertToEntityAttribute(input))
                .shouldThrow(IllegalArgumentException.class);
        }
    }

    @TestFactory
    Stream<DynamicTest> convertToEntityAttributeGregorianShouldNotThrow() {
        return timezones().map(p -> dynamicTest(p, () -> convertToEntityAttributeGregorianShouldNotThrow(p)));
    }

    private void convertToEntityAttributeGregorianShouldNotThrow(String timeZone) {
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