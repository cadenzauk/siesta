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

package com.cadenzauk.siesta.dialect;

import com.cadenzauk.siesta.Dialect;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static com.cadenzauk.core.lang.StringUtil.hex;
import static java.util.stream.Collectors.joining;

public class AnsiDialect implements Dialect {
    @Override
    public String selectivity(double s) {
        return "";
    }

    @Override
    public boolean requiresFromDual() {
        return true;
    }

    @Override
    public String dual() {
        return "DUAL";
    }

    @Override
    public String currentTimestamp() {
        return "current_timestamp";
    }

    @Override
    public String year(String arg) {
        return String.format("year(%s)", arg);
    }

    @Override
    public String month(String arg) {
        return String.format("month(%s)", arg);
    }

    @Override
    public String day(String arg) {
        return String.format("day(%s)", arg);
    }

    @Override
    public String hour(String arg) {
        return String.format("hour(%s)", arg);
    }

    @Override
    public String minute(String arg) {
        return String.format("minute(%s)", arg);
    }

    @Override
    public String second(String arg) {
        return String.format("second(%s)", arg);
    }

    @Override
    public String today() {
        return "current_date";
    }

    @Override
    public boolean supportsMultiInsert() {
        return false;
    }

    @Override
    public String concat(Stream<String> sql) {
        return sql.collect(joining(" || "));
    }

    @Override
    public String binaryLiteral(byte[] bytes) {
        return String.format("X'%s'", hex(bytes));
    }

    @Override
    public String byteLiteral(byte val) {
        return String.format("cast(%d as smallint)", val);
    }

    @Override
    public String dateLiteral(LocalDate date) {
        return String.format("DATE '%s'", date.format(DateTimeFormatter.ISO_DATE));
    }

    @Override
    public String floatLiteral(float val) {
        return String.format("cast(%s as real)", val);
    }

    @Override
    public String smallIntLiteral(short val) {
        return String.format("cast(%d as smallint)", val);
    }

    @Override
    public String timestampLiteral(LocalDateTime date, ZoneId databaseTimeZone) {
        return String.format("TIMESTAMP '%s'", date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")));
    }

    @Override
    public String timestampWithTimeZoneLiteral(ZonedDateTime date, ZoneId databaseTimeZone) {
        ZonedDateTime localDateTime = date.withZoneSameInstant(databaseTimeZone);
        return String.format("TIMESTAMP '%s'", localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")));
    }

    @Override
    public String stringLiteral(String val) {
        return "'" + val.replaceAll("'", "''") + "'";
    }

    @Override
    public String dateParameter(LocalDate val) {
        return "cast(? as date)";
    }

    @Override
    public String timestampParameter(LocalDateTime val) {
        return "cast(? as timestamp)";
    }

    @Override
    public String timestampWithTimeZoneParameter(ZonedDateTime val) {
        return "cast(? as timestamp)";
    }
}
