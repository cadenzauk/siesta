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

public class DefaultDbType<T> implements DbType<T> {
    private final String sqlType;
    private final SqlBiFunction<ResultSet,String,T> byLabel;
    private final SqlBiFunction<ResultSet,Integer,T> byColNo;

    public DefaultDbType(String sqlType, SqlBiFunction<ResultSet,String,T> byLabel, SqlBiFunction<ResultSet,Integer,T> byColNo) {
        this.sqlType = sqlType;
        this.byLabel = byLabel;
        this.byColNo = byColNo;
    }

    @Override
    public T getColumnValue(Database database, ResultSet rs, String col) throws SQLException {
        return byLabel.apply(rs, col);
    }

    @Override
    public T getColumnValue(Database database, ResultSet rs, int col) throws SQLException {
        return byColNo.apply(rs, col);
    }

    @Override
    public String sqlType(Database database) {
        return sqlType;
    }

    @FunctionalInterface
    public interface SqlBiFunction<T1, T2, R> {
        R apply(T1 arg1, T2 arg2) throws SQLException;
    }
}
