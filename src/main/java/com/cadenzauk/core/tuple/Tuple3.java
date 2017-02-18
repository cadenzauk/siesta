/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.core.tuple;

import com.cadenzauk.core.function.Function3;

public class Tuple3<T1, T2, T3> implements Tuple {
    private final T1 item1;
    private final T2 item2;
    private final T3 item3;

    public Tuple3(T1 item1, T2 item2, T3 item3) {
        this.item1 = item1;
        this.item2 = item2;
        this.item3 = item3;
    }

    public T1 item1() {
        return item1;
    }

    public T2 item2() {
        return item2;
    }

    public T3 item3() {
        return item3;
    }

    public <T> T map(Function3<T1, T2, T3, T> function) {
        return function.apply(item1, item2, item3);
    }
}
