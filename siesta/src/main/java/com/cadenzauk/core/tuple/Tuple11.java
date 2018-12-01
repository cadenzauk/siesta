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

import com.cadenzauk.core.function.Function11;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.function.Function;

public class Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> implements Tuple {
    private final T1 item1;
    private final T2 item2;
    private final T3 item3;
    private final T4 item4;
    private final T5 item5;
    private final T6 item6;
    private final T7 item7;
    private final T8 item8;
    private final T9 item9;
    private final T10 item10;
    private final T11 item11;

    public Tuple11(T1 item1, T2 item2, T3 item3, T4 item4, T5 item5, T6 item6, T7 item7, T8 item8, T9 item9, T10 item10, T11 item11) {
        this.item1 = item1;
        this.item2 = item2;
        this.item3 = item3;
        this.item4 = item4;
        this.item5 = item5;
        this.item6 = item6;
        this.item7 = item7;
        this.item8 = item8;
        this.item9 = item9;
        this.item10 = item10;
        this.item11 = item11;
    }

    @Override
    public String toString() {
        return "(" + item1 +
            ", " + item2 +
            ", " + item3 +
            ", " + item4 +
            ", " + item5 +
            ", " + item6 +
            ", " + item7 +
            ", " + item8 +
            ", " + item9 +
            ", " + item10 +
            ", " + item11 +
            ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Tuple11<?,?,?,?,?,?,?,?,?,?,?> tuple11 = (Tuple11<?,?,?,?,?,?,?,?,?,?,?>) o;

        return new EqualsBuilder()
            .append(item1, tuple11.item1)
            .append(item2, tuple11.item2)
            .append(item3, tuple11.item3)
            .append(item4, tuple11.item4)
            .append(item5, tuple11.item5)
            .append(item6, tuple11.item6)
            .append(item7, tuple11.item7)
            .append(item8, tuple11.item8)
            .append(item9, tuple11.item9)
            .append(item10, tuple11.item10)
            .append(item11, tuple11.item11)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(item1)
            .append(item2)
            .append(item3)
            .append(item4)
            .append(item5)
            .append(item6)
            .append(item7)
            .append(item8)
            .append(item9)
            .append(item10)
            .append(item11)
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

    public T10 item10() {
        return item10;
    }

    public T11 item11() {
        return item11;
    }

    public <T> T map(Function11<? super T1,? super T2,? super T3,? super T4,? super T5,? super T6,? super T7,? super T8,? super T9,? super T10,? super T11,? extends T> function) {
        return function.apply(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11);
    }

    public <T> Tuple11<T,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> map1(Function<? super T1, ? extends T> function) {
        return Tuple.of(
            function.apply(item1),
            item2,
            item3,
            item4,
            item5,
            item6,
            item7,
            item8,
            item9,
            item10,
            item11
        );
    }

    public <T> Tuple11<T1,T,T3,T4,T5,T6,T7,T8,T9,T10,T11> map2(Function<? super T2, ? extends T> function) {
        return Tuple.of(
            item1,
            function.apply(item2),
            item3,
            item4,
            item5,
            item6,
            item7,
            item8,
            item9,
            item10,
            item11
        );
    }

    public <T> Tuple11<T1,T2,T,T4,T5,T6,T7,T8,T9,T10,T11> map3(Function<? super T3, ? extends T> function) {
        return Tuple.of(
            item1,
            item2,
            function.apply(item3),
            item4,
            item5,
            item6,
            item7,
            item8,
            item9,
            item10,
            item11
        );
    }

    public <T> Tuple11<T1,T2,T3,T,T5,T6,T7,T8,T9,T10,T11> map4(Function<? super T4, ? extends T> function) {
        return Tuple.of(
            item1,
            item2,
            item3,
            function.apply(item4),
            item5,
            item6,
            item7,
            item8,
            item9,
            item10,
            item11
        );
    }

    public <T> Tuple11<T1,T2,T3,T4,T,T6,T7,T8,T9,T10,T11> map5(Function<? super T5, ? extends T> function) {
        return Tuple.of(
            item1,
            item2,
            item3,
            item4,
            function.apply(item5),
            item6,
            item7,
            item8,
            item9,
            item10,
            item11
        );
    }

    public <T> Tuple11<T1,T2,T3,T4,T5,T,T7,T8,T9,T10,T11> map6(Function<? super T6, ? extends T> function) {
        return Tuple.of(
            item1,
            item2,
            item3,
            item4,
            item5,
            function.apply(item6),
            item7,
            item8,
            item9,
            item10,
            item11
        );
    }

    public <T> Tuple11<T1,T2,T3,T4,T5,T6,T,T8,T9,T10,T11> map7(Function<? super T7, ? extends T> function) {
        return Tuple.of(
            item1,
            item2,
            item3,
            item4,
            item5,
            item6,
            function.apply(item7),
            item8,
            item9,
            item10,
            item11
        );
    }

    public <T> Tuple11<T1,T2,T3,T4,T5,T6,T7,T,T9,T10,T11> map8(Function<? super T8, ? extends T> function) {
        return Tuple.of(
            item1,
            item2,
            item3,
            item4,
            item5,
            item6,
            item7,
            function.apply(item8),
            item9,
            item10,
            item11
        );
    }

    public <T> Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T,T10,T11> map9(Function<? super T9, ? extends T> function) {
        return Tuple.of(
            item1,
            item2,
            item3,
            item4,
            item5,
            item6,
            item7,
            item8,
            function.apply(item9),
            item10,
            item11
        );
    }

    public <T> Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T,T11> map10(Function<? super T10, ? extends T> function) {
        return Tuple.of(
            item1,
            item2,
            item3,
            item4,
            item5,
            item6,
            item7,
            item8,
            item9,
            function.apply(item10),
            item11
        );
    }

    public <T> Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T> map11(Function<? super T11, ? extends T> function) {
        return Tuple.of(
            item1,
            item2,
            item3,
            item4,
            item5,
            item6,
            item7,
            item8,
            item9,
            item10,
            function.apply(item11)
        );
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> TypeToken<T1> type1(TypeToken<Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>> type) {
        return (TypeToken<T1>) type.resolveType(Tuple11.class.getTypeParameters()[0]);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> TypeToken<T2> type2(TypeToken<Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>> type) {
        return (TypeToken<T2>) type.resolveType(Tuple11.class.getTypeParameters()[1]);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> TypeToken<T3> type3(TypeToken<Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>> type) {
        return (TypeToken<T3>) type.resolveType(Tuple11.class.getTypeParameters()[2]);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> TypeToken<T4> type4(TypeToken<Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>> type) {
        return (TypeToken<T4>) type.resolveType(Tuple11.class.getTypeParameters()[3]);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> TypeToken<T5> type5(TypeToken<Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>> type) {
        return (TypeToken<T5>) type.resolveType(Tuple11.class.getTypeParameters()[4]);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> TypeToken<T6> type6(TypeToken<Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>> type) {
        return (TypeToken<T6>) type.resolveType(Tuple11.class.getTypeParameters()[5]);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> TypeToken<T7> type7(TypeToken<Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>> type) {
        return (TypeToken<T7>) type.resolveType(Tuple11.class.getTypeParameters()[6]);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> TypeToken<T8> type8(TypeToken<Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>> type) {
        return (TypeToken<T8>) type.resolveType(Tuple11.class.getTypeParameters()[7]);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> TypeToken<T9> type9(TypeToken<Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>> type) {
        return (TypeToken<T9>) type.resolveType(Tuple11.class.getTypeParameters()[8]);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> TypeToken<T10> type10(TypeToken<Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>> type) {
        return (TypeToken<T10>) type.resolveType(Tuple11.class.getTypeParameters()[9]);
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> TypeToken<T11> type11(TypeToken<Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>> type) {
        return (TypeToken<T11>) type.resolveType(Tuple11.class.getTypeParameters()[10]);
    }
}
