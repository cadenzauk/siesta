/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.siesta;

public enum Order {
    ASCENDING,
    DESCENDING;

    public String sql() {
        return name().toLowerCase();
    }
}
