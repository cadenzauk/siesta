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

package com.cadenzauk.core;

import com.cadenzauk.core.util.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.ThreadLocalRandom;

public abstract class RandomValues extends UtilityClass {
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public static short randomShort() {
        return (short) RANDOM.nextInt(Short.MIN_VALUE, Short.MAX_VALUE + 1);
    }

    public static byte randomByte() {
        return (byte) RANDOM.nextInt(Byte.MIN_VALUE, Byte.MAX_VALUE + 1);
    }

    public static BigDecimal randomBigDecimal(int scale, int precision) {
        return new BigDecimal(RandomStringUtils.randomNumeric(1, scale) + "." + RandomStringUtils.randomNumeric(precision));
    }

    public static LocalDate randomLocalDate() {
        int year = RANDOM.nextInt(1970, 2036);
        int month = RANDOM.nextInt(1, 13);
        int day = RANDOM.nextInt(1, new GregorianCalendar(year, month - 1, 1).getActualMaximum(Calendar.DAY_OF_MONTH) + 1);
        return LocalDate.of(year, month, day);
    }

    public static LocalTime randomLocalTime() {
        int hour = RANDOM.nextInt(0, 24);
        int minute = RANDOM.nextInt(0, 60);
        int second = RANDOM.nextInt(0, 60);
        return LocalTime.of(hour, minute, second);
    }

    public static LocalDateTime randomLocalDateTime() {
        return LocalDateTime.of(randomLocalDate(), randomLocalTime());
    }

    public static ZonedDateTime randomZonedDateTime(ZoneId zone) {
        return ZonedDateTime.of(randomLocalDate(), randomLocalTime(), zone);
    }
}
