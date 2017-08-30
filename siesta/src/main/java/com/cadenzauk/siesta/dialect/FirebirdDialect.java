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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class FirebirdDialect extends AnsiDialect {
    @Override
    public String dual() {
        return "RDB$DATABASE";
    }

    @Override
    public String year(String arg) {
        return String.format("extract(year from %s)", arg);
    }

    @Override
    public String month(String arg) {
        return String.format("extract(month from %s)", arg);
    }

    @Override
    public String day(String arg) {
        return String.format("extract(day from %s)", arg);
    }

    @Override
    public String hour(String arg) {
        return String.format("extract(hour from %s)", arg);
    }

    @Override
    public String minute(String arg) {
        return String.format("extract(minute from %s)", arg);
    }

    @Override
    public String second(String arg) {
        return String.format("extract(second from %s)", arg);
    }

    @Override
    public String timestampLiteral(LocalDateTime date, ZoneId databaseTimeZone) {
        return String.format("TIMESTAMP '%s'", date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS")));
    }

    @Override
    public String timestampWithTimeZoneLiteral(ZonedDateTime date, ZoneId databaseTimeZone) {
        ZonedDateTime localDateTime = date.withZoneSameInstant(databaseTimeZone);
        return String.format("TIMESTAMP '%s'", localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS")));
    }

    @Override
    public String integerParameter(int val) {
        return "cast(? as integer)";
    }

    @Override
    public String fetchFirst(String sql, long n) {
        return String.format("%s rows %d", sql, n);
    }
//
//    @Override
//    public String binaryType(int size) {
//        return String.format("varbinary", size);
//    }

    @Override
    public String timestampType(Optional<Integer> prec) {
        return "timestamp";
    }

    @Override
    public String qualifiedTableName(String catalog, String schema, String table) {
        return table;
    }
}
