/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta.catalog;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.siesta.*;
import org.springframework.jdbc.core.RowMapper;

public class Column<T, R> implements TypedExpression<T> {
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

    public String sql(Alias<R> alias) {
        return alias.inSelectClauseSql(this);
    }

    public String label(Alias<R> alias) {
        return alias.inSelectClauseLabel(this);
    }

    @Override
    public String sql(Scope scope) {
        return scope.findAlias(rowClass).inSelectClauseSql(this);
    }

    @Override
    public String label(Scope scope) {
        return scope.findAlias(rowClass).inSelectClauseLabel(this);
    }

    @Override
    public RowMapper<T> rowMapper(Scope scope, String label) {
        return (rs, i) -> dataType.get(rs, label).orElse(null);
    }

    public static <T, R> Column<T, R> aColumn(String name, DataType<T> type, Class<R> rowClass) {
        return new Column<>(name, type, rowClass);
    }

    public static <T, R> Column<T, R> aColumn(String name, Function1<R, T> function, Class<R> rowClass) {
        return new Column<>(name, null, rowClass);
    }

    public static <T, R> Column<T,R> of(String name, Class<R> rowClass) {
        return new Column<>(name, null, rowClass);
    }
}
