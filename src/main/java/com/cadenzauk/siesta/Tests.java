/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

public class Tests {
    // ---
    public static <T> TestSupplier<T> isEqualTo(T value) {
        return scope -> new OperatorValueTest<>("=", value);
    }

    public static <T, R> TestSupplier<T> isEqualTo(Column<T,R> column) {
        return scope -> new OperatorColumnTest<>("=", scope.findAlias(column.rowClass()), column);
    }

    public static <T, R> TestSupplier<T> isEqualTo(String alias, Column<T,R> column) {
        return scope -> new OperatorColumnTest<>("=", scope.findAlias(column.rowClass(), alias), column);
    }

    // ---
    public static <T> TestSupplier<T> isNotEqualTo(T value) {
        return scope -> new OperatorValueTest<>("<>", value);
    }

    public static <T, R> TestSupplier<T> isNotEqualTo(Column<T,R> column) {
        return scope -> new OperatorColumnTest<>("<>", scope.findAlias(column.rowClass()), column);
    }

    public static <T, R> TestSupplier<T> isNotEqualTo(String alias, Column<T,R> column) {
        return scope -> new OperatorColumnTest<>("<>", scope.findAlias(column.rowClass(), alias), column);
    }

    // ---
    public static <T> TestSupplier<T> isGreaterThan(T value) {
        return scope -> new OperatorValueTest<>(">", value);
    }

    public static <T, R> TestSupplier<T> isGreaterThan(Column<T,R> column) {
        return scope -> new OperatorColumnTest<>(">", scope.findAlias(column.rowClass()), column);
    }

    public static <T, R> TestSupplier<T> isGreaterThan(String alias, Column<T,R> column) {
        return scope -> new OperatorColumnTest<>(">", scope.findAlias(column.rowClass(), alias), column);
    }

    // ---
    public static <T> TestSupplier<T> isGreaterThanOrEqualTo(T value) {
        return scope -> new OperatorValueTest<>(">=", value);
    }

    public static <T, R> TestSupplier<T> isGreaterThanOrEqualTo(Column<T,R> column) {
        return scope -> new OperatorColumnTest<>(">=", scope.findAlias(column.rowClass()), column);
    }

    public static <T, R> TestSupplier<T> isGreaterThanOrEqualTo(String alias, Column<T,R> column) {
        return scope -> new OperatorColumnTest<>(">=", scope.findAlias(column.rowClass(), alias), column);
    }

    // ---
    public static <T> TestSupplier<T> isLessThan(T value) {
        return scope -> new OperatorValueTest<>("<", value);
    }

    public static <T, R> TestSupplier<T> isLessThan(Column<T,R> column) {
        return scope -> new OperatorColumnTest<>("<", scope.findAlias(column.rowClass()), column);
    }

    public static <T, R> TestSupplier<T> isLessThan(String alias, Column<T,R> column) {
        return scope -> new OperatorColumnTest<>("<", scope.findAlias(column.rowClass(), alias), column);
    }

    // ---
    public static <T> TestSupplier<T> isLessThanOrEqualTo(T value) {
        return scope -> new OperatorValueTest<>("<=", value);
    }

    public static <T, R> TestSupplier<T> isLessThanOrEqualTo(Column<T,R> column) {
        return scope -> new OperatorColumnTest<>("<=", scope.findAlias(column.rowClass()), column);
    }

    public static <T, R> TestSupplier<T> isLessThanOrEqualTo(String alias, Column<T,R> column) {
        return scope -> new OperatorColumnTest<>("<=", scope.findAlias(column.rowClass(), alias), column);
    }
}
