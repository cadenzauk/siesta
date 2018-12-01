/*
 * Copyright (c) 2017, 2018 Cadenza United Kingdom Limited
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

import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Tuple2<T1, T2> implements Tuple, Map.Entry<T1, T2> {
    private final T1 item1;
    private final T2 item2;

    public Tuple2(T1 item1, T2 item2) {
        this.item1 = item1;
        this.item2 = item2;
    }

    @Override
    public String toString() {
        return "(" + item1 + ", " + item2 + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Tuple2<?,?> tuple2 = (Tuple2<?,?>) o;

        return new EqualsBuilder()
            .append(item1, tuple2.item1)
            .append(item2, tuple2.item2)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(item1)
            .append(item2)
            .toHashCode();
    }

    @Override
    public T1 getKey() {
        return item1;
    }

    @Override
    public T2 getValue() {
        return item2;
    }

    @Override
    public T2 setValue(T2 value) {
        throw new UnsupportedOperationException();
    }

    public T1 item1() {
        return item1;
    }

    public T2 item2() {
        return item2;
    }

    public <T> T map(BiFunction<? super T1, ? super T2, ? extends T> function) {
        return function.apply(item1, item2);
    }

    public <T> Tuple2<T,T2> map1(Function<? super T1, ? extends T> function) {
        return Tuple.of(
            function.apply(item1),
            item2
        );
    }

    public <T> Tuple2<T1,T> map2(Function<? super T2, ? extends T> function) {
        return Tuple.of(
            item1,
            function.apply(item2)
        );
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2> TypeToken<T1> type1(TypeToken<Tuple2<T1,T2>> type) {
        return (TypeToken<T1>) type.resolveType(Tuple2.class.getTypeParameters()[0]);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2> TypeToken<T2> type2(TypeToken<Tuple2<T1,T2>> type) {
        return (TypeToken<T2>) type.resolveType(Tuple2.class.getTypeParameters()[1]);
    }
}
