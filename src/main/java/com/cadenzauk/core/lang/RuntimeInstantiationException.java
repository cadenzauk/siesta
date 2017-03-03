/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.core.lang;

import org.jetbrains.annotations.NonNls;

public class RuntimeInstantiationException extends RuntimeException {
    public RuntimeInstantiationException(@NonNls String message) {
        super(message);
    }
}
