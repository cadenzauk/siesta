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

import com.cadenzauk.core.sql.TimestampUtil;
import com.cadenzauk.siesta.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DefaultUtcTimestamp implements DbType<ZonedDateTime> {
    @Override
    public ZonedDateTime getColumnValue(Database database, ResultSet rs, String col) throws SQLException {
        Timestamp timestamp = getTimestamp(rs, col, database);
        return rs.wasNull() ? null : TimestampUtil.toZonedDateTime(timestamp, database.databaseTimeZone(), ZoneId.of("UTC"));
    }

    @Override
    public ZonedDateTime getColumnValue(Database database, ResultSet rs, int col) throws SQLException {
        Timestamp timestamp = getTimestamp(rs, col, database);
        return rs.wasNull() ? null : TimestampUtil.toZonedDateTime(timestamp, database.databaseTimeZone(), ZoneId.of("UTC"));
    }

    @Override
    public String sqlType(Database database) {
        return "timestamp";
    }

    @Override
    public Object convertToDatabase(Database database, ZonedDateTime value) {
        return TimestampUtil.valueOf(database.databaseTimeZone(), value);
    }

    @Override
    public String literal(Database database, ZonedDateTime value) {
        ZonedDateTime localDateTime = value.withZoneSameInstant(database.databaseTimeZone());
        return String.format("TIMESTAMP '%s'", localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")));
    }

    @Override
    public String parameter(Database database, ZonedDateTime value) {
        return "cast(? as timestamp)";
    }

    protected static Timestamp getTimestamp(ResultSet resultSet, String columnLabel, Database db) throws SQLException {
        return resultSet.getTimestamp(columnLabel, new GregorianCalendar(TimeZone.getTimeZone(db.databaseTimeZone())));
    }

    protected static Timestamp getTimestamp(ResultSet resultSet, int columnNo, Database db) throws SQLException {
        return resultSet.getTimestamp(columnNo, new GregorianCalendar(TimeZone.getTimeZone(db.databaseTimeZone())));
    }
}
