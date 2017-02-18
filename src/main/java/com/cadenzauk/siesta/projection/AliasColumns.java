/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.siesta.projection;

import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.catalog.TableColumn;

import static java.util.stream.Collectors.joining;

public class AliasColumns<R> implements Projection {
    private final Alias<R> alias;

    public AliasColumns(Alias<R> alias) {
        this.alias = alias;
    }

    @Override
    public String sql(Scope outer) {
        Scope scope = new Scope(outer.database(), alias);
        return alias
            .table()
            .columns()
            .map(TableColumn::column)
            .map(c -> String.format("%s as %s", c.sql(scope), c.label(scope)))
            .collect(joining(", "));
    }
}
