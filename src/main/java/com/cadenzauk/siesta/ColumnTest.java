/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import java.util.stream.Stream;

public class ColumnTest<T, R> implements Expression {
    private final Alias<R> alias;
    private final Column<T, R> column;
    private final Test test;

    public ColumnTest(Alias<R> alias, Column<T,R> column, Test test) {
        this.alias = alias;
        this.column = column;
        this.test = test;
    }

    @Override
    public String sql() {
        return alias.inExpression(column) + " " + test.sql();
    }

    @Override
    public Stream<Object> args() {
        return test.args();
    }
}
