/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.siesta.dialect;

import com.cadenzauk.siesta.Dialect;

public class AnsiDialect implements Dialect {
    @Override
    public String selectivity(double s) {
        return "";
    }
}
