/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.core.tuple;

import java.util.function.BiFunction;

public class Tuple {
    public static <T1, T2> Tuple2<T1, T2> of(T1 item1, T2 item2) {
        return new Tuple2<T1,T2>(item1, item2);
    }
}
