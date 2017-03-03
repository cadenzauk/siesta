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

package com.cadenzauk.siesta;

import com.cadenzauk.core.reflect.MethodInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class DataType<T> {
    @FunctionalInterface
    public interface ResultSetExtractor<T> {
        T get(ResultSet rs, String col) throws SQLException;
    }

    public static final DataType<Long> LONG = new DataType<>(Long.class, ResultSet::getLong);

    public static final DataType<String> STRING = new DataType<>(String.class, ResultSet::getString);
    public static final DataType<Integer> INTEGER = new DataType<>(Integer.class, ResultSet::getInt);
    private final Class<T> javaClass;

    private final ResultSetExtractor<T> resultSetExtractor;

    private DataType(Class<T> javaClass, ResultSetExtractor<T> resultSetExtractor) {
        this.javaClass = javaClass;
        this.resultSetExtractor = resultSetExtractor;
    }

    public Optional<T> get(ResultSet rs, String colName) {
        try {
            T value = resultSetExtractor.get(rs, colName);
            return rs.wasNull() ? Optional.empty() : Optional.of(value);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> DataType<T> of(MethodInfo<?,T> getterMethod) {
        Class<T> fieldType = getterMethod.effectiveType();
        return DataType.of(fieldType)
            .orElseThrow(() -> new RuntimeException("Unable to determine the type of " + getterMethod));
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<DataType<T>> of(Class<T> fieldType) {
        if (fieldType == Long.class || fieldType == Long.TYPE) {
            return Optional.of((DataType<T>) DataType.LONG);
        }
        if (fieldType == Integer.class || fieldType == Integer.TYPE) {
            return Optional.of((DataType<T>) DataType.INTEGER);
        }
        if (fieldType == String.class) {
            return Optional.of((DataType<T>) DataType.STRING);
        }
        return Optional.empty();
    }
}
