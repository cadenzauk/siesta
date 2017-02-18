/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.core.tuple;

import com.cadenzauk.core.function.Function9;

public class Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> implements Tuple {
    private final T1 item1;
    private final T2 item2;
    private final T3 item3;
    private final T4 item4;
    private final T5 item5;
    private final T6 item6;
    private final T7 item7;
    private final T8 item8;
    private final T9 item9;

    public Tuple9(T1 item1, T2 item2, T3 item3, T4 item4, T5 item5, T6 item6, T7 item7, T8 item8, T9 item9) {
        this.item1 = item1;
        this.item2 = item2;
        this.item3 = item3;
        this.item4 = item4;
        this.item5 = item5;
        this.item6 = item6;
        this.item7 = item7;
        this.item8 = item8;
        this.item9 = item9;
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

    public T4 item4() {
        return item4;
    }

    public T5 item5() {
        return item5;
    }

    public T6 item6() {
        return item6;
    }

    public T7 item7() {
        return item7;
    }

    public T8 item8() {
        return item8;
    }

    public T9 item9() {
        return item9;
    }

    public <T> T map(Function9<T1,T2,T3,T4,T5,T6,T7,T8,T9,T> function) {
        return function.apply(item1, item2, item3, item4, item5, item6, item7, item8, item9);
    }
}
