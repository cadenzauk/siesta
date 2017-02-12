/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.core.util;

import com.google.common.collect.Iterables;

import java.util.Optional;

public class OptionalUtil {
    public static <T> Optional<T> of(Iterable<T> iterable) {
        return Optional.ofNullable(Iterables.getOnlyElement(iterable, null));
    }
}
