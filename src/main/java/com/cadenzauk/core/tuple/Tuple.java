/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.core.tuple;

public interface Tuple {
    static <T1, T2> Tuple2<T1,T2> of(T1 item1, T2 item2) {
        return new Tuple2<>(item1, item2);
    }

    static <T1, T2, T3> Tuple3<T1,T2,T3> of(T1 item1, T2 item2, T3 item3) {
        return new Tuple3<>(item1, item2, item3);
    }

    static <T1, T2, T3, T4> Tuple4<T1,T2,T3,T4> of(T1 item1, T2 item2, T3 item3, T4 item4) {
        return new Tuple4<>(item1, item2, item3, item4);
    }

    static <T1, T2, T3, T4, T5> Tuple5<T1,T2,T3,T4,T5> of(T1 item1, T2 item2, T3 item3, T4 item4, T5 item5) {
        return new Tuple5<>(item1, item2, item3, item4, item5);
    }
}
