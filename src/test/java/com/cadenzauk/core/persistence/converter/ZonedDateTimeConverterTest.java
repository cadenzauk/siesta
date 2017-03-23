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
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static java.time.Month.DECEMBER;
import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static java.time.Month.MARCH;
import static java.time.Month.NOVEMBER;
import static java.time.Month.OCTOBER;
import static org.apache.commons.lang3.ArrayUtils.toArray;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class ZonedDateTimeConverterTest extends TemporalConverterTest {
    @SuppressWarnings("unused")
    private Object[] parameters() {
        return ArrayUtils.<Object>toArray(
            toArray("America/Anchorage", localDate(1, JANUARY, 1900), localDate(1, JANUARY, 1900)),
            toArray("America/New_York", localDate(29, FEBRUARY, 2008), localDate(29, FEBRUARY, 2008)),
            toArray("America/New_York", localDate(3, NOVEMBER, 2007), localDate(3, NOVEMBER, 2007)),
            toArray("America/New_York", localDate(4, NOVEMBER, 2007), localDate(4, NOVEMBER, 2007)),
            toArray("America/New_York", localDate(5, NOVEMBER, 2007), localDate(5, NOVEMBER, 2007)),
            toArray("UTC", localDate(31, DECEMBER, 2010), localDate(31, DECEMBER, 2010)),
            toArray("UTC", localDate(1, JANUARY, 2011), localDate(1, JANUARY, 2011)),
            toArray("UTC", localDate(12, JANUARY, 2014), localDate(12, JANUARY, 2014)),
            toArray("UTC", localDate(31, DECEMBER, 9999), localDate(31, DECEMBER, 9999)),
            toArray("UTC", localDate(15, OCTOBER, 1582), localDate(15, OCTOBER, 1582)),
            toArray("Europe/London", localDate(25, MARCH, 2017), localDate(25, MARCH, 2017)),
            toArray("Europe/London", localDate(26, MARCH, 2017), localDate(26, MARCH, 2017)),
            toArray("Europe/London", localDate(27, MARCH, 2017), localDate(27, MARCH, 2017)),
            toArray("Europe/Berlin", localDate(15, JANUARY, 2001), localDate(15, JANUARY, 2001)),
            toArray("Pacific/Apia", localDate(31, DECEMBER, 2100), localDate(31, DECEMBER, 2100)),
            toArray("Pacific/Apia", null, null)
        );
    }

    @SuppressWarnings("unused")
    private Object[] timezones() {
        return toArray(
            "America/Anchorage",
            "America/New_York",
            "UTC",
            "Europe/London",
            "Europe/Berlin",
            "Pacific/Apia"
        );
    }

    @Test
    @Parameters(method = "parameters")
    public void convertToDatabaseColumn(String timeZone, LocalDate inputLocal, LocalDate expectedLocal) {
        ZonedDateTime input = zonedDateTime(timeZone, inputLocal);
        Timestamp expected = timestamp(timeZone, expectedLocal);

        try (UncheckedAutoCloseable ignored = withTimeZone(timeZone)) {
            ZonedDateTimeConverter sut = new ZonedDateTimeConverter(ZoneId.of(timeZone));

            Timestamp actual = sut.convertToDatabaseColumn(input);

            assertThat(actual, is(expected));
        }
    }

    @Test
    @Parameters(method = "parameters")
    public void convertToEntityAttribute(String timeZone, LocalDate expectedLocal, LocalDate inputLocal) {
        Timestamp input = timestamp(timeZone, inputLocal);
        ZonedDateTime expected = zonedDateTime(timeZone, expectedLocal);

        try (UncheckedAutoCloseable ignored = withTimeZone(timeZone)) {
            ZonedDateTimeConverter sut = new ZonedDateTimeConverter(ZoneId.of(timeZone));

            ZonedDateTime actual = sut.convertToEntityAttribute(input);

            assertThat(actual, is(expected));
        }
    }

    @Test
    @Parameters(method = "timezones")
    public void convertToDatabaseColumnPreGregorianShouldThrow(String timeZone) {
        ZonedDateTime input = ZonedDateTime.of(LocalDateUtil.START_OF_GREGORIAN_CALENDAR, LocalTime.MIN, ZoneId.of(timeZone)).minusNanos(1);

        try (UncheckedAutoCloseable ignored = withTimeZone(timeZone)) {
            ZonedDateTimeConverter sut = new ZonedDateTimeConverter(ZoneId.of(timeZone));

            calling(() -> sut.convertToDatabaseColumn(input))
                .shouldThrow(IllegalArgumentException.class);
        }
    }

    @Test
    @Parameters(method = "timezones")
    public void convertToDatabaseColumnGregorianShouldNotThrow(String timeZone) {
        ZonedDateTime input = ZonedDateTime.of(LocalDateUtil.START_OF_GREGORIAN_CALENDAR, LocalTime.MIN, ZoneId.of(timeZone));

        try (UncheckedAutoCloseable ignored = withTimeZone(timeZone)) {
            ZonedDateTimeConverter sut = new ZonedDateTimeConverter(ZoneId.of(timeZone));

            Timestamp result = sut.convertToDatabaseColumn(input);

            assertThat(result, notNullValue());
        }
    }

    @Test
    @Parameters(method = "timezones")
    public void convertToEntityAttributePreGregorianShouldThrow(String timeZone) {
        ZonedDateTime dateTime = ZonedDateTime.of(LocalDateUtil.START_OF_GREGORIAN_CALENDAR, LocalTime.MIN, ZoneId.of(timeZone)).minusNanos(1);
        Timestamp input = Timestamp.from(dateTime.toInstant());

        try (UncheckedAutoCloseable ignored = withTimeZone(timeZone)) {
            ZonedDateTimeConverter sut = new ZonedDateTimeConverter(ZoneId.of(timeZone));

            calling(() -> sut.convertToEntityAttribute(input))
                .shouldThrow(IllegalArgumentException.class);
        }
    }

    @Test
    @Parameters(method = "timezones")
    public void convertToEntityAttributeGregorianShouldNotThrow(String timeZone) {
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