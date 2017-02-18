/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.siesta;

public enum JoinType {
    INNER("join"),
    LEFT_OUTER("left join"),
    RIGHT_OUTER("right join"),
    FULL_OUTER("full outer join");

    private final String sql;

    JoinType(String sql) {
        this.sql = sql;
    }

    public String sql() {
        return sql;
    }
}
