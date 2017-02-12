/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta.expression;

import com.cadenzauk.core.lang.StringUtil;
import com.cadenzauk.core.reflect.MethodReference;
import com.cadenzauk.core.reflect.MethodUtil;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.TypedExpression;
import com.cadenzauk.siesta.catalog.Column;
import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;

import static com.cadenzauk.siesta.catalog.Column.aColumn;

public class UnresolvedColumn<T,R> implements TypedExpression<T> {
    private final Optional<String> alias;
    private final Column<T,R> column;

    public UnresolvedColumn(Column<T, R> column) {
        this.alias = Optional.empty();
        this.column = column;
    }

    public UnresolvedColumn(String alias, Column<T, R> column) {
        this.alias = Optional.of(alias);
        this.column = column;
    }

    public Column<T, R> column() {
        return column;
    }

    @Override
    public String sql(Scope scope) {
        return resolve(scope).inSelectClauseSql(column);
    }

    @Override
    public String label(Scope scope) {
        return resolve(scope).inSelectClauseLabel(column);
    }

    @Override
    public RowMapper<T> rowMapper(String label) {
        return column.rowMapper(label);
    }

    private Alias<R> resolve(Scope scope) {
        return alias
            .map(a -> scope.findAlias(column.rowClass(), a))
            .orElseGet(() -> scope.findAlias(column.rowClass()));
    }

    public static <T, R> UnresolvedColumn<T,R> of(MethodReference<R,T> getter) {
        Method method = MethodUtil.fromReference(getter);
        return new UnresolvedColumn<>(aColumn(StringUtil.camelToUpper(method.getName()), getter, (Class<R>) method.getDeclaringClass()));
    }

    public static <T, R> UnresolvedColumn<T,R> of(String alias, MethodReference<R,T> getter) {
        Method method = MethodUtil.fromReference(getter);
        return new UnresolvedColumn<>(alias, aColumn(StringUtil.camelToUpper(method.getName()), getter, (Class<R>) method.getDeclaringClass()));
    }
}
