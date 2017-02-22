/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.siesta.expression;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.RowMapper;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.catalog.Column;

import java.util.stream.Stream;

public class ResolvedColumn<T,R> implements TypedExpression<T> {
    private final Alias<R> alias;
    private final Column<T,R> column;

    @SuppressWarnings("unchecked")
    private ResolvedColumn(Alias<R> alias, MethodInfo<R,T> method) {
        this.alias = alias;
        this.column = alias.column(method);
    }

    @Override
    public String sql(Scope scope) {
        return alias.inSelectClauseSql(column.name());
    }

    @Override
    public Stream<Object> args() {
        return Stream.empty();
    }

    @Override
    public String label(Scope scope) {
        return alias.inSelectClauseLabel(column.name());
    }

    @Override
    public RowMapper<T> rowMapper(Scope scope, String label) {
        return (rs, i) -> column.dataType().get(rs, label).orElse(null);
    }

    public static <T, R> ResolvedColumn<T,R> of(Alias<R> alias, Function1<R,T> getterReference) {
        MethodInfo<R,T> method = MethodInfo.of(getterReference);
        return new ResolvedColumn<>(alias, method);
    }

    public static <T, R> ResolvedColumn<T,R> of(Alias<R> alias, FunctionOptional1<R,T> getterReference) {
        MethodInfo<R,T> method = MethodInfo.of(getterReference);
        return new ResolvedColumn<>(alias, method);
    }
}
