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
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DefaultLocalTimeTypeAdapter implements TypeAdapter<LocalTime> {
    @Override
    public String literal(Database database, LocalTime value) {
        return String.format("TIME '%s'", value.format(DateTimeFormatter.ISO_TIME));
    }

    @Override
    public String parameter(Database database, LocalTime value) {
        return "cast(? as time)";
    }

    @Override
    public Object convertToDatabase(Database database, LocalTime value) {
        return Time.valueOf(value);
    }

    @Override
    public LocalTime getColumnValue(Database database, ResultSet rs, String col) throws SQLException {
        Time time = rs.getTime(col, new GregorianCalendar(TimeZone.getDefault()));
        return rs.wasNull() ? null : time.toLocalTime();
    }

    @Override
    public LocalTime getColumnValue(Database database, ResultSet rs, int col) throws SQLException {
        Time time = rs.getTime(col, new GregorianCalendar(TimeZone.getDefault()));
        return rs.wasNull() ? null : time.toLocalTime();
    }
}
