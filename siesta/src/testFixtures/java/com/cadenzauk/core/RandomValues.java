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
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Calendar.APRIL;
import static java.util.Calendar.AUGUST;
import static java.util.Calendar.DECEMBER;
import static java.util.Calendar.FEBRUARY;
import static java.util.Calendar.JANUARY;
import static java.util.Calendar.JULY;
import static java.util.Calendar.JUNE;
import static java.util.Calendar.MARCH;
import static java.util.Calendar.MAY;
import static java.util.Calendar.NOVEMBER;
import static java.util.Calendar.OCTOBER;
import static java.util.Calendar.SEPTEMBER;

public abstract class RandomValues extends UtilityClass {
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public static char randomChar() {
        return (char) RANDOM.nextInt(Character.MIN_VALUE, Character.MAX_VALUE + 1);
    }

    public static char randomChar(char from, char to) {
        return (char) (from + RANDOM.nextInt(to - from + 1));
    }

    public static String randomStringOf(int length, char from, char to) {
        char[] chars = new char[length];
        for (int i = 0; i < length; ++i) {
            chars[i] = randomChar(from, to);
        }
        return new String(chars);
    }

    public static short randomShort() {
        return (short) RANDOM.nextInt(Short.MIN_VALUE, Short.MAX_VALUE + 1);
    }

    public static byte randomByte() {
        return (byte) RANDOM.nextInt(Byte.MIN_VALUE, Byte.MAX_VALUE + 1);
    }

    public static BigDecimal randomBigDecimal(int scale, int precision) {
        return new BigDecimal(RandomStringUtils.randomNumeric(1, scale) + "." + RandomStringUtils.randomNumeric(precision));
    }

    @SuppressWarnings("MagicConstant")
    public static LocalDate randomLocalDate() {
        int year = RANDOM.nextInt(1970, 2036);
        int month = randomOf(JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER);
        int day = RANDOM.nextInt(1, new GregorianCalendar(year, month, 1).getActualMaximum(Calendar.DAY_OF_MONTH) + 1);
        return LocalDate.of(year, month + 1, day);
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

    public static boolean[] randomBooleanArray(int minLengthInclusive, int maxLengthExclusive) {
        int length = RANDOM.nextInt(minLengthInclusive, maxLengthExclusive);
        boolean[] booleans = new boolean[length];
        for (int i = 0; i < booleans.length; i++) {
            booleans[i] = RANDOM.nextBoolean();
        }
        return booleans;
    }

    public static char[] randomCharArray(int minLengthInclusive, int maxLengthExclusive) {
        int length = RANDOM.nextInt(minLengthInclusive, maxLengthExclusive);
        char[] chars = new char[length];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = randomChar();
        }
        return chars;
    }

    public static byte[] randomByteArray(int minLengthInclusive, int maxLengthExclusive) {
        int length = RANDOM.nextInt(minLengthInclusive, maxLengthExclusive);
        byte[] bytes = new byte[length];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = randomByte();
        }
        return bytes;
    }

    public static short[] randomShortArray(int minLengthInclusive, int maxLengthExclusive) {
        int length = RANDOM.nextInt(minLengthInclusive, maxLengthExclusive);
        short[] shorts = new short[length];
        for (int i = 0; i < shorts.length; i++) {
            shorts[i] = randomShort();
        }
        return shorts;
    }

    public static float[] randomFloatArray(int minLengthInclusive, int maxLengthExclusive) {
        int length = RANDOM.nextInt(minLengthInclusive, maxLengthExclusive);
        float[] floats = new float[length];
        for (int i = 0; i < floats.length; i++) {
            floats[i] = RANDOM.nextFloat();
        }
        return floats;
    }

    @SafeVarargs
    public static <T> T randomOf(T... values) {
        return values[RANDOM.nextInt(0, values.length)];
    }
}
