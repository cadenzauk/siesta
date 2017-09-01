/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.cadenzauk.core.tuple;

import com.cadenzauk.core.function.Function4;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.function.Function;

public class Tuple4<T1, T2, T3, T4> implements Tuple {
    private final T1 item1;
    private final T2 item2;
    private final T3 item3;
    private final T4 item4;

    public Tuple4(T1 item1, T2 item2, T3 item3, T4 item4) {
        this.item1 = item1;
        this.item2 = item2;
        this.item3 = item3;
        this.item4 = item4;
    }

    @Override
    public String toString() {
        return "(" + item1 +
            ", " + item2 +
            ", " + item3 +
            ", " + item4 +
            ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Tuple4<?,?,?,?> tuple4 = (Tuple4<?,?,?,?>) o;

        return new EqualsBuilder()
            .append(item1, tuple4.item1)
            .append(item2, tuple4.item2)
            .append(item3, tuple4.item3)
            .append(item4, tuple4.item4)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(item1)
            .append(item2)
            .append(item3)
            .append(item4)
            .toHashCode();
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

    public <T> T map(Function4<T1,T2,T3,T4,T> function) {
        return function.apply(item1, item2, item3, item4);
    }

    public <T> Tuple4<T,T2,T3,T4> map1(Function<T1,T> function) {
        return Tuple.of(
            function.apply(item1),
            item2,
            item3,
            item4
        );
    }

    public <T> Tuple4<T1,T,T3,T4> map2(Function<T2,T> function) {
        return Tuple.of(
            item1,
            function.apply(item2),
            item3,
            item4
        );
    }

    public <T> Tuple4<T1,T2,T,T4> map3(Function<T3,T> function) {
        return Tuple.of(
            item1,
            item2,
            function.apply(item3),
            item4
        );
    }

    public <T> Tuple4<T1,T2,T3,T> map4(Function<T4,T> function) {
        return Tuple.of(
            item1,
            item2,
            item3,
            function.apply(item4)
        );
    }
}
