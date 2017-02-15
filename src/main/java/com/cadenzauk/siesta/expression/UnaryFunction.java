/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta.expression;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Scope;
import org.springframework.jdbc.core.RowMapper;

import java.util.stream.Stream;

public class UnaryFunction<T> implements TypedExpression<T> {
    private final String name;
    private final TypedExpression<T> arg;

    public UnaryFunction(String name, TypedExpression<T> arg) {
        this.name = name;
        this.arg = arg;
    }

    @Override
    public String sql(Scope scope) {
        return String.format("%s(%s)", name, arg.sql(scope));
    }

    @Override
    public String label(Scope scope) {
        return String.format("%s_%s", name, arg.label(scope));
    }

    @Override
    public RowMapper<T> rowMapper(Scope scope, String label) {
        return arg.rowMapper(scope, label);
    }

    @Override
    public Stream<Object> args() {
        return arg.args();
    }

    public static <T> UnaryFunction<T> of(TypedExpression<T> arg, String name) {
        return new UnaryFunction<>(name, arg);
    }

    public static <T,R> UnaryFunction<T> of(Function1<R,T> arg, String name) {
        return new UnaryFunction<>(name, UnresolvedColumn.of(arg));
    }

    public static <T,R> UnaryFunction<T> of(FunctionOptional1<R,T> arg, String name) {
        return new UnaryFunction<>(name, UnresolvedColumn.of(arg));
    }

    public static <T,R> UnaryFunction<T> of(String alias, Function1<R,T> arg, String name) {
        return new UnaryFunction<>(name, UnresolvedColumn.of(alias, arg));
    }

    public static <T,R> UnaryFunction<T> of(String alias, FunctionOptional1<R,T> arg, String name) {
        return new UnaryFunction<>(name, UnresolvedColumn.of(alias, arg));
    }

    public static <T,R> UnaryFunction<T> of(Alias<R> alias, Function1<R,T> arg, String name) {
        return new UnaryFunction<>(name, ResolvedColumn.of(alias, arg));
    }

    public static <T,R> TypedExpression<T> of(Alias<R> alias, FunctionOptional1<R,T> arg, String name) {
        return new UnaryFunction<>(name, ResolvedColumn.of(alias, arg));
    }
}
