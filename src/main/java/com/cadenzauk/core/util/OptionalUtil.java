/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.core.util;

import com.google.common.collect.Iterables;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public final class OptionalUtil extends UtilityClass {
    public static Optional<String> ofBlankable(String s) {
        return StringUtils.isBlank(s) ? Optional.empty() : Optional.of(s);
    }

    public static <T> Optional<T> ofOnly(Iterable<T> iterable) {
        return Optional.ofNullable(Iterables.getOnlyElement(iterable, null));
    }

    public static <T,U> Optional<U> as(Class<U> targetClass, Optional<T> source) {
        return source
            .filter(v -> targetClass.isAssignableFrom(v.getClass()))
            .map(targetClass::cast);
    }

    public static <T,U> Optional<U> as(Class<U> targetClass, T source) {
        return Optional.ofNullable(source)
            .filter(v -> targetClass.isAssignableFrom(v.getClass()))
            .map(targetClass::cast);
    }
}
