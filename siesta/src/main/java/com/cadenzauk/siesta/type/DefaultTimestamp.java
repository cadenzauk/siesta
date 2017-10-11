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

package com.cadenzauk.siesta.type;

import com.cadenzauk.siesta.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DefaultTimestamp implements DbType<LocalDateTime> {
    @Override
    public LocalDateTime getColumnValue(Database database, ResultSet rs, String col) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(col, new GregorianCalendar(TimeZone.getDefault()));
        return rs.wasNull() ? null : timestamp.toLocalDateTime();
    }

    @Override
    public LocalDateTime getColumnValue(Database database, ResultSet rs, int col) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(col, new GregorianCalendar(TimeZone.getDefault()));
        return rs.wasNull() ? null : timestamp.toLocalDateTime();
    }

    @Override
    public String sqlType(Database database) {
        return "timestamp";
    }

    @Override
    public Object convertToDatabase(Database database, LocalDateTime value) {
        return Timestamp.valueOf(value);
    }

    @Override
    public String literal(Database database, LocalDateTime value) {
        return String.format("TIMESTAMP '%s'", value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")));
    }

    @Override
    public String parameter(Database database, LocalDateTime value) {
        return "cast(? as timestamp)";
    }
}
