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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.cadenzauk.core.lang.StringUtil.hex;
import static java.util.stream.Collectors.joining;

public class SqlServerDialect extends AnsiDialect {
    private static final Pattern SELECT_PATTERN = Pattern.compile("(select (distinct )?)");

    @Override
    public String today() {
        return "getdate()";
    }

    @Override
    public boolean supportsMultiInsert() {
        return true;
    }

    @Override
    public boolean requiresFromDual() {
        return false;
    }

    @Override
    public String hour(String arg) {
        return String.format("datepart(hour, %s)", arg);
    }

    @Override
    public String minute(String arg) {
        return String.format("datepart(minute, %s)", arg);
    }

    @Override
    public String second(String arg) {
        return String.format("datepart(second, %s)", arg);
    }

    @Override
    public String concat(Stream<String> sql) {
        return "concat(" + sql.collect(joining(", ")) + ")";
    }

    @Override
    public String dateLiteral(LocalDate date) {
        return String.format("cast('%s' as date)", date.format(DateTimeFormatter.ISO_DATE));
    }

    @Override
    public String timestampLiteral(LocalDateTime localDateTime, ZoneId databaseTimeZone) {
        return String.format("cast('%s' as datetime2(6))", localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")));
    }

    @Override
    public String timestampWithTimeZoneLiteral(ZonedDateTime date, ZoneId databaseTimeZone) {
        ZonedDateTime localDateTime = date.withZoneSameInstant(databaseTimeZone);
        return String.format("cast('%s' as datetime2(6))", localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")));
    }

    @Override
    public String timestampParameter(LocalDateTime val) {
        return "cast(? as datetime2)";
    }

    @Override
    public String timestampWithTimeZoneParameter(ZonedDateTime val) {
        return "cast(? as datetime2)";
    }

    @Override
    public String binaryLiteral(byte[] bytes) {
        return String.format("0x%s", hex(bytes));
    }

    @Override
    public String byteLiteral(byte val) {
        return String.format("cast(%d as tinyint)", val & 0xff);
    }

    @Override
    public String fetchFirst(String sql, long n) {
        return SELECT_PATTERN.matcher(sql).replaceFirst("$1top " + n + " ");
    }

    @Override
    public String tinyintType() {
        return "tinyint";
    }

    @Override
    public String timestampType(Optional<Integer> prec) {
        return prec
            .map(p -> String.format("datetime2(%d)", p))
            .orElse("datetime2");
    }

    @Override
    public String nextFromSequence(String catalog, String schema, String sequenceName) {
        return "next value for " + qualifiedName(catalog, schema, sequenceName);
    }
}
