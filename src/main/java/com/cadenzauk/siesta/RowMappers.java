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

import com.cadenzauk.core.tuple.Tuple;
import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.core.tuple.Tuple3;
import com.cadenzauk.core.tuple.Tuple4;
import com.cadenzauk.core.tuple.Tuple5;
import com.cadenzauk.core.tuple.Tuple6;
import com.cadenzauk.core.tuple.Tuple7;
import com.cadenzauk.core.tuple.Tuple8;
import com.cadenzauk.core.tuple.Tuple9;

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

    public static <T1, T2, T3> RowMapper<Tuple3<T1,T2,T3>> add3rd(RowMapper<Tuple2<T1,T2>> mapper2, RowMapper<T3> mapper) {
        return (rs, i) -> {
            Tuple2<T1,T2> tuple = mapper2.mapRow(rs, i);
            return Tuple.of(tuple.item1(), tuple.item2(), mapper.mapRow(rs, i));
        };
    }

    public static <T1, T2, T3, T4> RowMapper<Tuple4<T1,T2,T3,T4>> add4th(RowMapper<Tuple3<T1,T2,T3>> mapper3, RowMapper<T4> mapper) {
        return (rs, i) -> {
            Tuple3<T1,T2,T3> tuple = mapper3.mapRow(rs, i);
            return Tuple.of(tuple.item1(), tuple.item2(), tuple.item3(), mapper.mapRow(rs, i));
        };
    }

    public static <T1, T2, T3, T4, T5> RowMapper<Tuple5<T1,T2,T3,T4,T5>> add5th(RowMapper<Tuple4<T1,T2,T3,T4>> mapper4, RowMapper<T5> mapper) {
        return (rs, i) -> {
            Tuple4<T1,T2,T3,T4> tuple = mapper4.mapRow(rs, i);
            return Tuple.of(tuple.item1(), tuple.item2(), tuple.item3(), tuple.item4(), mapper.mapRow(rs, i));
        };
    }

    public static <T1, T2, T3, T4, T5, T6> RowMapper<Tuple6<T1,T2,T3,T4,T5,T6>> add6th(RowMapper<Tuple5<T1,T2,T3,T4,T5>> mapper5, RowMapper<T6> mapper) {
        return (rs, i) -> {
            Tuple5<T1,T2,T3,T4,T5> tuple = mapper5.mapRow(rs, i);
            return Tuple.of(tuple.item1(), tuple.item2(), tuple.item3(), tuple.item4(), tuple.item5(), mapper.mapRow(rs, i));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7> RowMapper<Tuple7<T1,T2,T3,T4,T5,T6,T7>> add7th(RowMapper<Tuple6<T1,T2,T3,T4,T5,T6>> mapper6, RowMapper<T7> mapper) {
        return (rs, i) -> {
            Tuple6<T1,T2,T3,T4,T5,T6> tuple = mapper6.mapRow(rs, i);
            return Tuple.of(tuple.item1(), tuple.item2(), tuple.item3(), tuple.item4(), tuple.item5(), tuple.item6(), mapper.mapRow(rs, i));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> RowMapper<Tuple8<T1,T2,T3,T4,T5,T6,T7,T8>> add8th(RowMapper<Tuple7<T1,T2,T3,T4,T5,T6,T7>> mapper7, RowMapper<T8> mapper) {
        return (rs, i) -> {
            Tuple7<T1,T2,T3,T4,T5,T6,T7> tuple = mapper7.mapRow(rs, i);
            return Tuple.of(tuple.item1(), tuple.item2(), tuple.item3(), tuple.item4(), tuple.item5(), tuple.item6(), tuple.item7(), mapper.mapRow(rs, i));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> RowMapper<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T9>> add9th(RowMapper<Tuple8<T1,T2,T3,T4,T5,T6,T7,T8>> mapper8, RowMapper<T9> mapper) {
        return (rs, i) -> {
            Tuple8<T1,T2,T3,T4,T5,T6,T7,T8> tuple = mapper8.mapRow(rs, i);
            return Tuple.of(tuple.item1(), tuple.item2(), tuple.item3(), tuple.item4(), tuple.item5(), tuple.item6(), tuple.item7(), tuple.item8(), mapper.mapRow(rs, i));
        };
    }

}
