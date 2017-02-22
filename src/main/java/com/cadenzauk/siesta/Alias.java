/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.siesta;

import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.siesta.catalog.Column;
import com.cadenzauk.siesta.catalog.Table;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.stream.Stream;

public class Alias<R> {
    private final Table<R> table;
    private final String aliasName;

    public Alias(Table<R> table, String aliasName) {
        this.table = table;
        this.aliasName = aliasName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Alias<?> alias = (Alias<?>) o;

        return new EqualsBuilder()
            .append(table.qualifiedName(), alias.table.qualifiedName())
            .append(aliasName, alias.aliasName)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(table.qualifiedName())
            .append(aliasName)
            .toHashCode();
    }

    String inWhereClause() {
        return String.format("%s as %s", table.qualifiedName(), aliasName);
    }

    public String inSelectClauseSql(String columnName) {
        return String.format("%s.%s", aliasName, columnName);
    }

    public String inSelectClauseLabel(String columnName) {
        return String.format("%s_%s", aliasName, columnName);
    }

    public Table<R> table() {
        return table;
    }

    public String aliasName() {
        return aliasName();
    }

    public <T> Column<T,R> column(MethodInfo<R,T> methodInfo) {
        return table().column(methodInfo);
    }

    RowMapper<R> rowMapper() {
        return table.rowMapper(aliasName + "_");
    }

    @SuppressWarnings("unchecked")
    <R2> Stream<Alias<R2>> as(Class<R2> requiredRowClass, String requiredAlias) {
        if (StringUtils.equals(requiredAlias, aliasName)) {
            if (requiredRowClass.isAssignableFrom(table.rowClass())) {
                return Stream.of((Alias<R2>) this);
            }
            throw new IllegalArgumentException("Alias " + aliasName + " is an alias for " + table().rowClass() + " and not " + requiredRowClass);
        }
        return Stream.empty();
    }

    @SuppressWarnings("unchecked")
    <R2> Stream<Alias<R2>> as(Class<R2> requiredRowClass) {
        if (requiredRowClass.isAssignableFrom(table.rowClass())) {
            return Stream.of((Alias<R2>) this);
        }
        return Stream.empty();
    }
}
