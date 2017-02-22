/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.siesta.projection;

import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.catalog.Column;

import static java.util.stream.Collectors.joining;

public class AliasColumns<R> implements Projection {
    private final Alias<R> alias;

    public AliasColumns(Alias<R> alias) {
        this.alias = alias;
    }

    @Override
    public String sql(Scope outer) {
        return alias
            .table()
            .columns()
            .map(Column::name)
            .map(c -> String.format("%s as %s", alias.inSelectClauseSql(c), alias.inSelectClauseLabel(c)))
            .collect(joining(", "));
    }
}
