/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.core.stream;

import java.util.Optional;
import java.util.stream.Stream;

public class StreamUtil {
    public static <T> Stream<T> of(Optional<T> opt) {
        return opt.map(Stream::of).orElseGet(Stream::empty);
    }
}
