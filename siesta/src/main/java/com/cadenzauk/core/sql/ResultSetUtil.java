/*
 * Copyright (c) 2018 Cadenza United Kingdom Limited
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

import com.cadenzauk.core.function.ThrowingSupplier;
import com.cadenzauk.core.lang.CompositeAutoCloseable;
import com.cadenzauk.core.util.UtilityClass;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class ResultSetUtil extends UtilityClass  {
    public static String getString(ResultSet rs, String columnLabel) {
        try {
            return rs.getString(columnLabel);
        } catch (SQLException e) {
            throw new RuntimeSqlException(e);
        }
    }

    public static void close(ResultSet rs) {
        try {
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeSqlException(e);
        }
    }

    public static <T> Stream<T> stream(ResultSet rs, RowMapper<T> rowMapper) {
        CompositeAutoCloseable closer = new CompositeAutoCloseable();
        closer.add(rs);
        return StreamSupport.stream(new ResultSetSpliterator<T>(rs, rowMapper, closer::close), false).onClose(closer::close);
    }

    public static <T> Stream<T> stream(ThrowingSupplier<ResultSet, ? extends SQLException> rs, RowMapper<T> rowMapper) {
        try {
            return stream(rs.get(), rowMapper);
        } catch (SQLException e) {
            throw new RuntimeSqlException(e);
        }
    }
}
