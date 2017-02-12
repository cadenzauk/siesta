/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import com.cadenzauk.core.lang.StringUtil;
import com.cadenzauk.core.reflect.MethodUtil;
import com.cadenzauk.siesta.catalog.Column;
import com.cadenzauk.siesta.catalog.Table;
import com.cadenzauk.siesta.expression.ResolvedColumn;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.cadenzauk.siesta.catalog.Column.aColumn;

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

    public String aliasName() {
        return aliasName;
    }

    String inWhereClause() {
        return String.format("%s as %s", table.qualifiedName(), aliasName);
    }

    public String inSelectClauseSql(Column<?, R> column) {
        return String.format("%s.%s", aliasName, column.name());
    }

    public String inSelectClauseLabel(Column<?, R> column) {
        return String.format("%s_%s", aliasName, column.name());
    }

    public String inExpression(Column<?, R> column) {
        return String.format("%s.%s", aliasName, column.name());
    }

    public Table<R> table() {
        return table;
    }

    public static <T,R> ResolvedColumn<T,R> column(Alias<R> alias, Function<R,T> getter) {
        Class<R> rowClass = alias.table.rowClass();
        Method method = MethodUtil.fromReference(rowClass, getter);
        return new ResolvedColumn<T, R>(alias, aColumn(StringUtil.camelToUpper(method.getName()), getter,rowClass));
    }

    RowMapper<R> rowMapper() {
        return table.rowMapper(aliasName + "_");
    }

    <T> RowMapper<Optional<T>> rowMapper(Column<T, R> column) {
        return table.rowMapper(aliasName + "_", column);
    }

    @SuppressWarnings("unchecked")
    <R2> Stream<Alias<R2>> as(Class<R2> requiredRowClass, String requiredAlias) {
        if (StringUtils.equals(requiredAlias, aliasName)) {
            if (requiredRowClass == table.rowClass()) {
                return Stream.of((Alias<R2>) this);
            }
            throw new IllegalArgumentException("Alias " + aliasName + " is an alias for " + table().rowClass() + " and not " + requiredRowClass);
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
