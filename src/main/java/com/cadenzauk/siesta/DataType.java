/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class DataType<T> {
    public static DataType<Long> LONG = new DataType<>(Long.class, ResultSet::getLong);
    public static DataType<String> STRING = new DataType<>(String.class, ResultSet::getString);
    public static DataType<Integer> INTEGER = new DataType<>(Integer.class, ResultSet::getInt);

    @FunctionalInterface
    public interface ResultSetExtractor<T> {
        T get(ResultSet rs, String col) throws SQLException;
    }
    private final Class<T> javaClass;
    private final ResultSetExtractor<T> resultSetExtractor;

    public DataType(Class<T> javaClass, ResultSetExtractor<T> resultSetExtractor) {
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
}
