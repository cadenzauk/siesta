/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta.condition;

import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.catalog.Column;
import com.cadenzauk.siesta.Condition;
import com.cadenzauk.siesta.Scope;

import java.util.stream.Stream;

public class OperatorColumnCondition<T,R> implements Condition<T> {
    private final String operator;
    private final String alias;
    private final Column<T,R> column;

    public OperatorColumnCondition(String operator, String alias, Column<T,R> column) {
        this.operator = operator;
        this.alias = alias;
        this.column = column;
    }

    @Override
    public String sql(Scope scope) {
        Alias<R> resolvedAlias = scope.findAlias(column.rowClass(), this.alias);
        return operator + " " + column.sql(resolvedAlias);
    }

    @Override
    public Stream<Object> args() {
        return Stream.empty();
    }
}
