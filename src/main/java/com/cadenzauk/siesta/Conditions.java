/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import com.cadenzauk.core.reflect.MethodReference;
import com.cadenzauk.siesta.catalog.Column;
import com.cadenzauk.siesta.condition.OperatorColumnCondition;
import com.cadenzauk.siesta.condition.OperatorExpressionCondition;
import com.cadenzauk.siesta.condition.OperatorValueCondition;
import com.cadenzauk.siesta.expression.UnresolvedColumn;

import java.util.function.Function;

public class Conditions {
    // ---
    public static <T> Condition<T> isEqualTo(T value) {
        return new OperatorValueCondition<>("=", value);
    }

    public static <T, R> Condition<T> isEqualTo(TypedExpression<T> expression) {
        return new OperatorExpressionCondition<>("=", expression);
    }

    public static <T, R> Condition<T> isEqualTo(String alias, Column<T,R> column) {
        return new OperatorColumnCondition<>("=", alias, column);
    }

    public static <T, R> Condition<T> isEqualTo(Alias<R> alias, Column<T,R> column) {
        return new OperatorColumnCondition<>("=", alias.aliasName(), column);
    }

    public static <T, R> Condition<T> isEqualTo(MethodReference<R,T> getter) {
        return new OperatorExpressionCondition<>("=", UnresolvedColumn.of(getter));
    }

    public static <T, R> Condition<T> isEqualTo(String alias, MethodReference<R,T> getter) {
        return new OperatorExpressionCondition<>("=", UnresolvedColumn.of(alias, getter));
    }

    // ---
    public static <T> Condition<T> isNotEqualTo(T value) {
        return new OperatorValueCondition<>("<>", value);
    }

    public static <T, R> Condition<T> isNotEqualTo(TypedExpression<T> expression) {
        return new OperatorExpressionCondition<>("<>", expression);
    }

    public static <T, R> Condition<T> isNotEqualTo(String alias, Column<T,R> column) {
        return new OperatorColumnCondition<>("<>", alias, column);
    }

    // ---
    public static <T> Condition<T> isGreaterThan(T value) {
        return new OperatorValueCondition<>(">", value);
    }

    public static <T> Condition<T> isGreaterThan(TypedExpression<T> expression) {
        return new OperatorExpressionCondition<>(">", expression);
    }

    public static <T, R> Condition<T> isGreaterThan(String alias, Column<T,R> column) {
        return new OperatorColumnCondition<>(">", alias, column);
    }

    // ---
    public static <T> Condition<T> isGreaterThanOrEqualTo(T value) {
        return new OperatorValueCondition<>(">=", value);
    }

    public static <T, R> Condition<T> isGreaterThanOrEqualTo(TypedExpression<T> expression) {
        return new OperatorExpressionCondition<>(">=", expression);
    }

    public static <T, R> Condition<T> isGreaterThanOrEqualTo(String alias, Column<T,R> column) {
        return new OperatorColumnCondition<>(">=", alias, column);
    }

    // ---
    public static <T> Condition<T> isLessThan(T value) {
        return new OperatorValueCondition<>("<", value);
    }

    public static <T, R> Condition<T> isLessThan(TypedExpression<T> expression) {
        return new OperatorExpressionCondition<>("<", expression);
    }

    public static <T, R> Condition<T> isLessThan(String alias, Column<T,R> column) {
        return new OperatorColumnCondition<>("<", alias, column);
    }

    // ---
    public static <T> Condition<T> isLessThanOrEqualTo(T value) {
        return new OperatorValueCondition<>("<=", value);
    }

    public static <T, R> Condition<T> isLessThanOrEqualTo(TypedExpression<T> expression) {
        return new OperatorExpressionCondition<>("<=", expression);
    }

    public static <T, R> Condition<T> isLessThanOrEqualTo(String alias, Column<T,R> column) {
        return new OperatorColumnCondition<>("<=", alias, column);
    }
}
