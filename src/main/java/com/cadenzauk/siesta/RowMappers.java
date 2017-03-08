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

package com.cadenzauk.siesta;

import com.cadenzauk.core.tuple.*;

public class RowMappers {
    public static <T1, T2> RowMapper<Tuple2<T1,T2>> of(RowMapper<T1> mapper1, RowMapper<T2> mapper2) {
        return (rs, i) -> Tuple.of(mapper1.mapRow(rs, i), mapper2.mapRow(rs, i));
    }

    public static <T1, T2, T3> RowMapper<Tuple3<T1,T2,T3>> of(RowMapper<T1> mapper1, RowMapper<T2> mapper2, RowMapper<T3> mapper3) {
        return (rs, i) -> Tuple.of(mapper1.mapRow(rs, i), mapper2.mapRow(rs, i), mapper3.mapRow(rs, i));
    }

    public static <T1, T2, T3, T4> RowMapper<Tuple4<T1,T2,T3,T4>> of(RowMapper<T1> mapper1, RowMapper<T2> mapper2, RowMapper<T3> mapper3, RowMapper<T4> mapper4) {
        return (rs, i) -> Tuple.of(mapper1.mapRow(rs, i), mapper2.mapRow(rs, i), mapper3.mapRow(rs, i), mapper4.mapRow(rs, i));
    }

    public static <T1, T2, T3, T4, T5> RowMapper<Tuple5<T1,T2,T3,T4,T5>> of(RowMapper<T1> mapper1, RowMapper<T2> mapper2, RowMapper<T3> mapper3, RowMapper<T4> mapper4, RowMapper<T5> mapper5) {
        return (rs, i) -> Tuple.of(mapper1.mapRow(rs, i), mapper2.mapRow(rs, i), mapper3.mapRow(rs, i), mapper4.mapRow(rs, i), mapper5.mapRow(rs, i));
    }

    public static <T1, T2, T3> RowMapper<Tuple3<T1,T2,T3>> add3rd(RowMapper<Tuple2<T1,T2>> mapper12, RowMapper<T3> mapper3) {
        return (rs, i) -> {
            Tuple2<T1,T2> tuple12 = mapper12.mapRow(rs, i);
            return Tuple.of(tuple12.item1(), tuple12.item2(), mapper3.mapRow(rs, i));
        };
    }

    public static <T1, T2, T3, T4> RowMapper<Tuple4<T1,T2,T3,T4>> add4th(RowMapper<Tuple3<T1,T2,T3>> mapper123, RowMapper<T4> mapper4) {
        return (rs, i) -> {
            Tuple3<T1,T2,T3> tuple123 = mapper123.mapRow(rs, i);
            return Tuple.of(tuple123.item1(), tuple123.item2(), tuple123.item3(), mapper4.mapRow(rs, i));
        };
    }

}
