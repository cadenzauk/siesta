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

package com.cadenzauk.siesta;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

public interface Dialect {
    String selectivity(double s);

    boolean requiresFromDual();

    String dual();

    String currentTimestamp();

    String year(String arg);

    String month(String arg);

    String day(String arg);

    String hour(String arg);

    String minute(String arg);

    String second(String arg);

    String today();

    boolean supportsMultiInsert();

    String concat(Stream<String> sql);

    String binaryLiteral(byte[] bytes);

    String byteLiteral(byte val);

    String dateLiteral(LocalDate date);

    String floatLiteral(float val);

    String smallIntLiteral(short val);

    String timestampLiteral(LocalDateTime date, ZoneId databaseTimeZone);

    String timestampWithTimeZoneLiteral(ZonedDateTime date, ZoneId databaseTimeZone);

    String stringLiteral(String val);

    String dateParameter(LocalDate val);

    String timestampParameter(LocalDateTime val);

    String timestampWithTimeZoneParameter(ZonedDateTime val);

    String fetchFirst(String sql, long n);
}
