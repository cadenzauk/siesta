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
import java.sql.Types;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class EnumByName<T extends Enum<T>> implements DbType<T> {
    private final Class<T> enumClass;

    public EnumByName(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public T getColumnValue(Database database, ResultSet rs, String col) throws SQLException {
        String name = type(database).getColumnValue(database, rs, col);
        return isBlank(name) ? null : Enum.valueOf(enumClass, name);
    }

    @Override
    public T getColumnValue(Database database, ResultSet rs, int col) throws SQLException {
        String name = type(database).getColumnValue(database, rs, col);
        return isBlank(name) ? null : Enum.valueOf(enumClass, name);
    }

    @Override
    public String sqlType(Database database) {
        return type(database).sqlType(database, 80);
    }

    @Override
    public Object convertToDatabase(Database database, T value) {
        return type(database).convertToDatabase(database, value.name());
    }

    @Override
    public String literal(Database database, T value) {
        return type(database).literal(database, value.name());
    }

    private DbType<String> type(Database database) {
        return database.dialect().type(DbTypeId.VARCHAR);
    }

    public static <T extends Enum<T>> DbTypeId<T> id(Class<T> javaClass) {
        return new DbTypeId<>(javaClass.getCanonicalName(), Types.VARCHAR, javaClass);
    }
}
