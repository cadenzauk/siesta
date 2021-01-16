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

import com.cadenzauk.core.util.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.stream.IntStream;

public final class ResultSetMetaDataUtil extends UtilityClass  {
    public static int findColumnWithLabel(ResultSetMetaData metaData, String columnLabel) {
        return IntStream.range(1, getColumnCount(metaData) + 1)
            .filter(i -> StringUtils.equalsIgnoreCase(getColumnLabel(metaData, i), columnLabel))
            .findFirst()
            .orElseThrow(() -> new RuntimeSqlException("No such column as " + columnLabel + " in result set."));
    }

    private static int getColumnCount(ResultSetMetaData metaData) {
        try {
            return metaData.getColumnCount();
        } catch (SQLException e) {
            throw new RuntimeSqlException(e);
        }
    }

    private static String getColumnLabel(ResultSetMetaData metaData, int colNo) {
        try {
            return metaData.getColumnLabel(colNo);
        } catch (SQLException e) {
            throw new RuntimeSqlException(e);
        }
    }
}
