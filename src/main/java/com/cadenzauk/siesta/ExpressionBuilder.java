/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.siesta;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.siesta.expression.condition.OperatorExpressionCondition;
import com.cadenzauk.siesta.expression.condition.OperatorValueCondition;
import com.cadenzauk.siesta.expression.*;

import java.util.Optional;
import java.util.function.Function;

public class ExpressionBuilder<T,N> {
    private final TypedExpression<T> lhs;
    private final Function<Expression,N> onComplete;
    private Optional<Double> selectivity = Optional.empty();

    ExpressionBuilder(TypedExpression<T> lhs, Function<Expression,N> onComplete) {
        this.lhs = lhs;
        this.onComplete = onComplete;
    }

    public N isEqualTo(T value) {
        return complete(new OperatorValueCondition<>("=", value, selectivity));
    }

    public N isEqualTo(TypedExpression<T> expression) {
        return complete(new OperatorExpressionCondition<>("=", expression));
    }

    public <R> N isEqualTo(Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("=", UnresolvedColumn.of(getter)));
    }

    public <R> N isEqualTo(FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("=", UnresolvedColumn.of(getter)));
    }

    public <R> N isEqualTo(String alias, Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("=", UnresolvedColumn.of(alias, getter)));
    }

    public <R> N isEqualTo(String alias, FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("=", UnresolvedColumn.of(alias, getter)));
    }

    public <R> N isEqualTo(Alias<R> alias, Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("=", ResolvedColumn.of(alias, getter)));
    }

    public <R> N isEqualTo(Alias<R> alias, FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("=", ResolvedColumn.of(alias, getter)));
    }

    public N isNotEqualTo(T value) {
        return complete(new OperatorValueCondition<>("<>", value, selectivity));
    }

    public N isNotEqualTo(TypedExpression<T> expression) {
        return complete(new OperatorExpressionCondition<>("<>", expression));
    }

    public <R> N isNotEqualTo(Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<>", UnresolvedColumn.of(getter)));
    }

    public <R> N isNotEqualTo(FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<>", UnresolvedColumn.of(getter)));
    }

    public <R> N isNotEqualTo(String alias, Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<>", UnresolvedColumn.of(alias, getter)));
    }

    public <R> N isNotEqualTo(String alias, FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<>", UnresolvedColumn.of(alias, getter)));
    }

    public <R> N isNotEqualTo(Alias<R> alias, Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<>", ResolvedColumn.of(alias, getter)));
    }

    public <R> N isNotEqualTo(Alias<R> alias, FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<>", ResolvedColumn.of(alias, getter)));
    }

    public N isGreaterThan(T value) {
        return complete(new OperatorValueCondition<>(">", value, selectivity));
    }

    public N isGreaterThan(TypedExpression<T> expression) {
        return complete(new OperatorExpressionCondition<>(">", expression));
    }

    public <R> N isGreaterThan(Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">", UnresolvedColumn.of(getter)));
    }

    public <R> N isGreaterThan(FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">", UnresolvedColumn.of(getter)));
    }

    public <R> N isGreaterThan(String alias, Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">", UnresolvedColumn.of(alias, getter)));
    }

    public <R> N isGreaterThan(String alias, FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">", UnresolvedColumn.of(alias, getter)));
    }

    public <R> N isGreaterThan(Alias<R> alias, Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">", ResolvedColumn.of(alias, getter)));
    }

    public <R> N isGreaterThan(Alias<R> alias, FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">", ResolvedColumn.of(alias, getter)));
    }

    public ExpressionBuilder<T,N> selectivity(double v) {
        selectivity = Optional.of(v);
        return this;
    }

    private N complete(Condition<T> rhs) {
        return onComplete.apply(new BooleanExpression<>(lhs, rhs));
    }

    public static <T,N> ExpressionBuilder<T,N> of(TypedExpression<T> lhs, Function<Expression,N> onComplete) {
        return new ExpressionBuilder<>(lhs, onComplete);
    }
}
