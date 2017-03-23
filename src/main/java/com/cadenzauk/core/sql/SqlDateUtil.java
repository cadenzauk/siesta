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

import java.sql.Date;
import java.time.LocalDate;

import static com.cadenzauk.core.time.LocalDateUtil.START_OF_GREGORIAN_CALENDAR;

public class SqlDateUtil extends UtilityClass {
    @NotNull
    public static Date valueOf(@NotNull LocalDate localDate) {
        if (localDate.isBefore(START_OF_GREGORIAN_CALENDAR)) {
            throw new IllegalArgumentException("Cannot convert local dates before the start of the Gregorian calendar to SQL dates.");
        }
        return Date.valueOf(localDate);
    }

    @NotNull
    public static LocalDate toLocalDate(@NotNull Date date) {
        if (date.toLocalDate().isBefore(START_OF_GREGORIAN_CALENDAR)) {
            throw new IllegalArgumentException("Cannot convert SQL dates before the start of the Gregorian calendar to local dates.");
        }
        return date.toLocalDate();
    }
}
