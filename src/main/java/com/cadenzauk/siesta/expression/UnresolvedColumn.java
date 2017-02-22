/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.siesta.expression;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.core.reflect.MethodUtil;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.DataType;
import com.cadenzauk.siesta.RowMapper;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.catalog.Column;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Stream;

public class UnresolvedColumn<T,R> implements TypedExpression<T> {
    private final Optional<String> alias;
    private final MethodInfo<R,T> getterMethod;

    private UnresolvedColumn(MethodInfo<R,T> getterMethod) {
        this.getterMethod = getterMethod;
        this.alias = Optional.empty();
    }

    private UnresolvedColumn(String alias, MethodInfo<R,T> getterMethod) {
        this.alias = Optional.of(alias);
        this.getterMethod = getterMethod;
    }

    @Override
    public String sql(Scope scope) {
        Column<T,R> column = scope.database().column(getterMethod);
        return resolve(scope).inSelectClauseSql(column.name());
    }

    @Override
    public Stream<Object> args() {
        return Stream.empty();
    }

    @Override
    public String label(Scope scope) {
        Column<T,R> column = scope.database().column(getterMethod);
        return resolve(scope).inSelectClauseLabel(column.name());
    }

    @SuppressWarnings("unchecked")
    @Override
    public RowMapper<T> rowMapper(Scope scope, String label) {
        Column<T,R> column = scope.database().column(getterMethod);
        return column.rowMapper(label);
    }

    @SuppressWarnings("unchecked")
    private Alias<R> resolve(Scope scope) {
        Class<R> rowClass = getterMethod.declaringClass();
        return this.alias
            .map(a -> scope.findAlias(rowClass, a))
            .orElseGet(() -> scope.findAlias(rowClass));
    }

    public static <T, R> UnresolvedColumn<T,R> of(Function1<R,T> getter) {
        MethodInfo<R,T> method = MethodInfo.of(getter);
        return new UnresolvedColumn<>(method);
    }

    public static <T, R> TypedExpression<T> of(FunctionOptional1<R,T> getter) {
        MethodInfo<R,T> method = MethodInfo.of(getter);
        return new UnresolvedColumn<>(method);
    }

    public static <T, R> UnresolvedColumn<T,R> of(String alias, Function1<R,T> getter) {
        MethodInfo<R,T> method = MethodInfo.of(getter);
        return new UnresolvedColumn<>(alias, method);
    }

    public static <T, R> TypedExpression<T> of(String alias, FunctionOptional1<R,T> getter) {
        MethodInfo<R,T> method = MethodInfo.of(getter);
        return new UnresolvedColumn<>(alias, method);
    }
}
