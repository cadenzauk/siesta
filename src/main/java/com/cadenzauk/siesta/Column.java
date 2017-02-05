/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

public class Column<T, R> {
    private final String name;
    private final DataType<T> dataType;
    private final Class<R> rowClass;

    private Column(String name, DataType<T> dataType, Class<R> rowClass) {
        this.name = name;
        this.dataType = dataType;
        this.rowClass = rowClass;
    }

    public String name() {
        return name;
    }

    public DataType<T> dataType() {
        return dataType;
    }

    public Class<R> rowClass() {
        return rowClass;
    }

    public static <T, R> Column<T, R> aColumn(String name, DataType<T> type, Class<R> rowClass) {
        return new Column<>(name, type, rowClass);
    }
}
