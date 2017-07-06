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

package com.cadenzauk.core.sql;

import com.cadenzauk.core.util.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.cadenzauk.core.time.LocalDateUtil.START_OF_GREGORIAN_CALENDAR;

public class TimestampUtil extends UtilityClass {
    @NotNull
    public static Timestamp valueOf(ZoneId dbZoneId, @NotNull ZonedDateTime zonedDateTime) {
        if (zonedDateTime.toLocalDate().isBefore(START_OF_GREGORIAN_CALENDAR)) {
            throw new IllegalArgumentException("Cannot convert date/times before the start of the Gregorian calendar to timestamps.");
        }
        ZonedDateTime localDateTime = zonedDateTime.withZoneSameInstant(dbZoneId);
        return Timestamp.valueOf(localDateTime.toLocalDateTime());
    }

    @NotNull
    public static ZonedDateTime toZonedDateTime(@NotNull Timestamp timestamp, ZoneId dbZoneId, @NotNull ZoneId zoneId) {
        if (ZonedDateTime.ofInstant(timestamp.toInstant(), zoneId).toLocalDate().isBefore(START_OF_GREGORIAN_CALENDAR)) {
            throw new IllegalArgumentException("Cannot convert timestamps before the start of the Gregorian calendar to date/time.");
        }
        ZonedDateTime localDateTime = ZonedDateTime.ofInstant(timestamp.toInstant(), dbZoneId);
        return ZonedDateTime.ofInstant(localDateTime.toInstant(), zoneId);
    }

    @NotNull
    public static LocalDateTime toLocalDateTime(@NotNull Timestamp timestamp, ZoneId dbZoneId) {
        return toZonedDateTime(timestamp, dbZoneId, ZoneId.systemDefault()).toLocalDateTime();
    }
}
