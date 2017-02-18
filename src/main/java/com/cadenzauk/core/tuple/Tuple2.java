/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.core.tuple;

import java.util.function.BiFunction;

public class Tuple2<T1,T2> implements Tuple {
    private final T1 item1;
    private final T2 item2;

    public Tuple2(T1 item1, T2 item2) {
        this.item1 = item1;
        this.item2 = item2;
    }

    public T1 item1() {
        return item1;
    }

    public T2 item2() {
        return item2;
    }

    public <T> T map(BiFunction<T1, T2, T> function) {
        return function.apply(item1, item2);
    }
}
