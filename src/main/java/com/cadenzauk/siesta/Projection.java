/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import com.cadenzauk.siesta.projection.AliasColumns;
import com.cadenzauk.siesta.projection.ExpressionProjection;
import com.cadenzauk.siesta.projection.ProjectionList;

public interface Projection {
    String sql(Scope scope);

    static <T> Projection of(TypedExpression<T> column) {
        return new ExpressionProjection<>(column);
    }

    static <R1> Projection of(Alias<R1> alias) {
        return new AliasColumns<>(alias);
    }

    static Projection of(Projection... p) {
        return new ProjectionList(p);
    }
}
