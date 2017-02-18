/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.siesta.projection;

import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.Scope;

import java.util.Arrays;

import static java.util.stream.Collectors.joining;

public class ProjectionList implements Projection {
    private final Projection[] p;

    public ProjectionList(Projection[] p) {
        this.p = p;
    }

    @Override
    public String sql(Scope scope) {
        return Arrays.stream(p)
            .map(x -> x.sql(scope))
            .collect(joining(", "));
    }
}
