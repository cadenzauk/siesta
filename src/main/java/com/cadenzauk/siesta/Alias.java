/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import java.util.stream.Stream;

public class Alias<R> {
    private final Table<R> table;
    private final String alias;

    public Alias(Table<R> table, String alias) {
        this.table = table;
        this.alias = alias;
    }

    String inWhereClause() {
        return String.format("%s as %s", table.qualifiedName(), alias);
    }

    String inSelectClause(TableColumn<?,R> column) {
        return String.format("%1$s.%2$s as %1$s_%2$s", alias, column.name());
    }

    String inExpression(Column<?,R> column) {
        return String.format("%s.%s", alias, column.name());
    }

    Table<R> table() {
        return table;
    }

    RowMapper<R> rowMapper() {
        return table.rowMapper(alias + "_");
    }

    @SuppressWarnings("unchecked")
    <R2> Stream<Alias<R2>> as(Class<R2> requiredRowClass, String requiredAlias) {
        if (StringUtils.equals(requiredAlias, alias)) {
            if (requiredRowClass == table.rowClass()) {
                return Stream.of((Alias<R2>) this);
            }
            throw new IllegalArgumentException("Alias " + alias + " is an alias for " + table().rowClass() + " and not " + requiredRowClass);
        }
        return Stream.empty();
    }

    @SuppressWarnings("unchecked")
    <R2> Stream<Alias<R2>> as(Class<R2> requiredRowClass) {
        if (requiredRowClass == table.rowClass()) {
            return Stream.of((Alias<R2>) this);
        }
        return Stream.empty();
    }
}
