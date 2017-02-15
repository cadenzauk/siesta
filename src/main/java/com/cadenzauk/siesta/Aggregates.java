/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.siesta.expression.TypedExpression;
import com.cadenzauk.siesta.expression.UnaryFunction;

public class Aggregates {
    public static <T> TypedExpression<T> max(TypedExpression<T> arg) {
        return UnaryFunction.of(arg, "max");
    }

    public static <T,R> TypedExpression<T> max(Function1<R,T> arg) {
        return UnaryFunction.of(arg, "max");
    }

    public static <T,R> TypedExpression<T> max(FunctionOptional1<R,T> arg) {
        return UnaryFunction.of(arg, "max");
    }

    public static <T,R> TypedExpression<T> max(String alias, Function1<R,T> arg) {
        return UnaryFunction.of(alias, arg, "max");
    }

    public static <T,R> TypedExpression<T> max(String alias, FunctionOptional1<R,T> arg) {
        return UnaryFunction.of(alias, arg, "max");
    }

    public static <T,R> TypedExpression<T> max(Alias<R> alias, Function1<R,T> arg) {
        return UnaryFunction.of(alias, arg, "max");
    }

    public static <T,R> TypedExpression<T> max(Alias<R> alias, FunctionOptional1<R,T> arg) {
        return UnaryFunction.of(alias, arg, "max");
    }

    public static <T> TypedExpression<T> min(TypedExpression<T> arg) {
        return UnaryFunction.of(arg, "min");
    }

    public static <T,R> TypedExpression<T> min(Function1<R,T> arg) {
        return UnaryFunction.of(arg, "min");
    }

    public static <T,R> TypedExpression<T> min(FunctionOptional1<R,T> arg) {
        return UnaryFunction.of(arg, "min");
    }

    public static <T,R> TypedExpression<T> min(String alias, Function1<R,T> arg) {
        return UnaryFunction.of(alias, arg, "min");
    }

    public static <T,R> TypedExpression<T> min(String alias, FunctionOptional1<R,T> arg) {
        return UnaryFunction.of(alias, arg, "min");
    }

    public static <T,R> TypedExpression<T> min(Alias<R> alias, Function1<R,T> arg) {
        return UnaryFunction.of(alias, arg, "min");
    }

    public static <T,R> TypedExpression<T> min(Alias<R> alias, FunctionOptional1<R,T> arg) {
        return UnaryFunction.of(alias, arg, "min");
    }

    public static <T> TypedExpression<T> sum(TypedExpression<T> arg) {
        return UnaryFunction.of(arg, "sum");
    }

    public static <T,R> TypedExpression<T> sum(Function1<R,T> arg) {
        return UnaryFunction.of(arg, "sum");
    }

    public static <T,R> TypedExpression<T> sum(FunctionOptional1<R,T> arg) {
        return UnaryFunction.of(arg, "sum");
    }

    public static <T,R> TypedExpression<T> sum(String alias, Function1<R,T> arg) {
        return UnaryFunction.of(alias, arg, "sum");
    }

    public static <T,R> TypedExpression<T> sum(String alias, FunctionOptional1<R,T> arg) {
        return UnaryFunction.of(alias, arg, "sum");
    }

    public static <T,R> TypedExpression<T> sum(Alias<R> alias, Function1<R,T> arg) {
        return UnaryFunction.of(alias, arg, "sum");
    }

    public static <T,R> TypedExpression<T> sum(Alias<R> alias, FunctionOptional1<R,T> arg) {
        return UnaryFunction.of(alias, arg, "sum");
    }
}
