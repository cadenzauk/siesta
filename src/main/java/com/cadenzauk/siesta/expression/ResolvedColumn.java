/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta.expression;

import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.catalog.Column;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.TypedExpression;
import org.springframework.jdbc.core.RowMapper;

public class ResolvedColumn<T,R> implements TypedExpression<T> {
    private final Alias<R> alias;
    private final Column<T,R> column;

    public ResolvedColumn(Alias<R> alias, Column<T, R> column) {
        this.alias = alias;
        this.column = column;
    }

    @Override
    public String sql(Scope scope) {
        return alias.inSelectClauseSql(column);
    }

    @Override
    public String label(Scope scope) {
        return alias.inSelectClauseLabel(column);
    }

    @Override
    public RowMapper<T> rowMapper(String label) {
        return (rs, i) -> column.dataType().get(rs, label).orElse(null);
    }
}
