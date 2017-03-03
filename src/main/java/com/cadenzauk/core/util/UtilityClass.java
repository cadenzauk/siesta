/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.core.util;

import com.cadenzauk.core.lang.RuntimeInstantiationException;

public class UtilityClass {
    protected UtilityClass() {
        throw new RuntimeInstantiationException("No " + getClass() + " for you.");
    }
}
