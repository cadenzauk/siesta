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

import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.core.tuple.Tuple;
import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.core.tuple.Tuple3;
import com.cadenzauk.core.tuple.Tuple4;
import com.cadenzauk.core.tuple.Tuple5;
import com.cadenzauk.core.tuple.Tuple6;
import com.cadenzauk.core.tuple.Tuple7;
import com.cadenzauk.core.tuple.Tuple8;
import com.cadenzauk.core.tuple.Tuple9;
import com.cadenzauk.core.tuple.Tuple10;
import com.cadenzauk.core.tuple.Tuple11;
import com.cadenzauk.core.tuple.Tuple12;
import com.cadenzauk.core.tuple.Tuple13;
import com.cadenzauk.core.tuple.Tuple14;
import com.cadenzauk.core.tuple.Tuple15;
import com.cadenzauk.core.tuple.Tuple16;
import com.cadenzauk.core.tuple.Tuple17;
import com.cadenzauk.core.tuple.Tuple18;
import com.cadenzauk.core.tuple.Tuple19;
import com.cadenzauk.core.tuple.Tuple20;

public class RowMappers {
    public static <T1, T2> RowMapper<Tuple2<T1,T2>> of(RowMapper<T1> mapper1, RowMapper<T2> mapper2) {
        return rs -> Tuple.of(mapper1.mapRow(rs), mapper2.mapRow(rs));
    }

    public static <T1, T2, T3> RowMapper<Tuple3<T1,T2,T3>> add3rd(RowMapper<Tuple2<T1,T2>> mapper2, RowMapper<T3> mapper) {
        return rs -> {
            Tuple2<T1,T2> tuple = mapper2.mapRow(rs);
            return Tuple.of(tuple.item1(), tuple.item2(), mapper.mapRow(rs));
        };
    }

    public static <T1, T2, T3, T4> RowMapper<Tuple4<T1,T2,T3,T4>> add4th(RowMapper<Tuple3<T1,T2,T3>> mapper3, RowMapper<T4> mapper) {
        return rs -> {
            Tuple3<T1,T2,T3> tuple = mapper3.mapRow(rs);
            return Tuple.of(tuple.item1(), tuple.item2(), tuple.item3(), mapper.mapRow(rs));
        };
    }

    public static <T1, T2, T3, T4, T5> RowMapper<Tuple5<T1,T2,T3,T4,T5>> add5th(RowMapper<Tuple4<T1,T2,T3,T4>> mapper4, RowMapper<T5> mapper) {
        return rs -> {
            Tuple4<T1,T2,T3,T4> tuple = mapper4.mapRow(rs);
            return Tuple.of(tuple.item1(), tuple.item2(), tuple.item3(), tuple.item4(), mapper.mapRow(rs));
        };
    }

    public static <T1, T2, T3, T4, T5, T6> RowMapper<Tuple6<T1,T2,T3,T4,T5,T6>> add6th(RowMapper<Tuple5<T1,T2,T3,T4,T5>> mapper5, RowMapper<T6> mapper) {
        return rs -> {
            Tuple5<T1,T2,T3,T4,T5> tuple = mapper5.mapRow(rs);
            return Tuple.of(tuple.item1(), tuple.item2(), tuple.item3(), tuple.item4(), tuple.item5(), mapper.mapRow(rs));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7> RowMapper<Tuple7<T1,T2,T3,T4,T5,T6,T7>> add7th(RowMapper<Tuple6<T1,T2,T3,T4,T5,T6>> mapper6, RowMapper<T7> mapper) {
        return rs -> {
            Tuple6<T1,T2,T3,T4,T5,T6> tuple = mapper6.mapRow(rs);
            return Tuple.of(tuple.item1(), tuple.item2(), tuple.item3(), tuple.item4(), tuple.item5(), tuple.item6(), mapper.mapRow(rs));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> RowMapper<Tuple8<T1,T2,T3,T4,T5,T6,T7,T8>> add8th(RowMapper<Tuple7<T1,T2,T3,T4,T5,T6,T7>> mapper7, RowMapper<T8> mapper) {
        return rs -> {
            Tuple7<T1,T2,T3,T4,T5,T6,T7> tuple = mapper7.mapRow(rs);
            return Tuple.of(tuple.item1(), tuple.item2(), tuple.item3(), tuple.item4(), tuple.item5(), tuple.item6(), tuple.item7(), mapper.mapRow(rs));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> RowMapper<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T9>> add9th(RowMapper<Tuple8<T1,T2,T3,T4,T5,T6,T7,T8>> mapper8, RowMapper<T9> mapper) {
        return rs -> {
            Tuple8<T1,T2,T3,T4,T5,T6,T7,T8> tuple = mapper8.mapRow(rs);
            return Tuple.of(tuple.item1(), tuple.item2(), tuple.item3(), tuple.item4(), tuple.item5(), tuple.item6(), tuple.item7(), tuple.item8(), mapper.mapRow(rs));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> RowMapper<Tuple10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10>> add10th(RowMapper<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T9>> mapper9, RowMapper<T10> mapper) {
        return rs -> {
            Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T9> tuple = mapper9.mapRow(rs);
            return Tuple.of(tuple.item1(), tuple.item2(), tuple.item3(), tuple.item4(), tuple.item5(), tuple.item6(), tuple.item7(), tuple.item8(), tuple.item9(), mapper.mapRow(rs));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> RowMapper<Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>> add11th(RowMapper<Tuple10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10>> mapper10, RowMapper<T11> mapper) {
        return rs -> {
            Tuple10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> tuple = mapper10.mapRow(rs);
            return Tuple.of(tuple.item1(), tuple.item2(), tuple.item3(), tuple.item4(), tuple.item5(), tuple.item6(), tuple.item7(), tuple.item8(), tuple.item9(), tuple.item10(), mapper.mapRow(rs));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> RowMapper<Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12>> add12th(RowMapper<Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>> mapper11, RowMapper<T12> mapper) {
        return rs -> {
            Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> tuple = mapper11.mapRow(rs);
            return Tuple.of(tuple.item1(), tuple.item2(), tuple.item3(), tuple.item4(), tuple.item5(), tuple.item6(), tuple.item7(), tuple.item8(), tuple.item9(), tuple.item10(), tuple.item11(), mapper.mapRow(rs));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> RowMapper<Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13>> add13th(RowMapper<Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12>> mapper12, RowMapper<T13> mapper) {
        return rs -> {
            Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> tuple = mapper12.mapRow(rs);
            return Tuple.of(tuple.item1(), tuple.item2(), tuple.item3(), tuple.item4(), tuple.item5(), tuple.item6(), tuple.item7(), tuple.item8(), tuple.item9(), tuple.item10(), tuple.item11(), tuple.item12(), mapper.mapRow(rs));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> RowMapper<Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14>> add14th(RowMapper<Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13>> mapper13, RowMapper<T14> mapper) {
        return rs -> {
            Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> tuple = mapper13.mapRow(rs);
            return Tuple.of(tuple.item1(), tuple.item2(), tuple.item3(), tuple.item4(), tuple.item5(), tuple.item6(), tuple.item7(), tuple.item8(), tuple.item9(), tuple.item10(), tuple.item11(), tuple.item12(), tuple.item13(), mapper.mapRow(rs));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> RowMapper<Tuple15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15>> add15th(RowMapper<Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14>> mapper14, RowMapper<T15> mapper) {
        return rs -> {
            Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> tuple = mapper14.mapRow(rs);
            return Tuple.of(tuple.item1(), tuple.item2(), tuple.item3(), tuple.item4(), tuple.item5(), tuple.item6(), tuple.item7(), tuple.item8(), tuple.item9(), tuple.item10(), tuple.item11(), tuple.item12(), tuple.item13(), tuple.item14(), mapper.mapRow(rs));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> RowMapper<Tuple16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16>> add16th(RowMapper<Tuple15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15>> mapper15, RowMapper<T16> mapper) {
        return rs -> {
            Tuple15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> tuple = mapper15.mapRow(rs);
            return Tuple.of(tuple.item1(), tuple.item2(), tuple.item3(), tuple.item4(), tuple.item5(), tuple.item6(), tuple.item7(), tuple.item8(), tuple.item9(), tuple.item10(), tuple.item11(), tuple.item12(), tuple.item13(), tuple.item14(), tuple.item15(), mapper.mapRow(rs));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> RowMapper<Tuple17<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17>> add17th(RowMapper<Tuple16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16>> mapper16, RowMapper<T17> mapper) {
        return rs -> {
            Tuple16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> tuple = mapper16.mapRow(rs);
            return Tuple.of(tuple.item1(), tuple.item2(), tuple.item3(), tuple.item4(), tuple.item5(), tuple.item6(), tuple.item7(), tuple.item8(), tuple.item9(), tuple.item10(), tuple.item11(), tuple.item12(), tuple.item13(), tuple.item14(), tuple.item15(), tuple.item16(), mapper.mapRow(rs));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> RowMapper<Tuple18<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18>> add18th(RowMapper<Tuple17<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17>> mapper17, RowMapper<T18> mapper) {
        return rs -> {
            Tuple17<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17> tuple = mapper17.mapRow(rs);
            return Tuple.of(tuple.item1(), tuple.item2(), tuple.item3(), tuple.item4(), tuple.item5(), tuple.item6(), tuple.item7(), tuple.item8(), tuple.item9(), tuple.item10(), tuple.item11(), tuple.item12(), tuple.item13(), tuple.item14(), tuple.item15(), tuple.item16(), tuple.item17(), mapper.mapRow(rs));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> RowMapper<Tuple19<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19>> add19th(RowMapper<Tuple18<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18>> mapper18, RowMapper<T19> mapper) {
        return rs -> {
            Tuple18<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18> tuple = mapper18.mapRow(rs);
            return Tuple.of(tuple.item1(), tuple.item2(), tuple.item3(), tuple.item4(), tuple.item5(), tuple.item6(), tuple.item7(), tuple.item8(), tuple.item9(), tuple.item10(), tuple.item11(), tuple.item12(), tuple.item13(), tuple.item14(), tuple.item15(), tuple.item16(), tuple.item17(), tuple.item18(), mapper.mapRow(rs));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> RowMapper<Tuple20<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20>> add20th(RowMapper<Tuple19<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19>> mapper19, RowMapper<T20> mapper) {
        return rs -> {
            Tuple19<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19> tuple = mapper19.mapRow(rs);
            return Tuple.of(tuple.item1(), tuple.item2(), tuple.item3(), tuple.item4(), tuple.item5(), tuple.item6(), tuple.item7(), tuple.item8(), tuple.item9(), tuple.item10(), tuple.item11(), tuple.item12(), tuple.item13(), tuple.item14(), tuple.item15(), tuple.item16(), tuple.item17(), tuple.item18(), tuple.item19(), mapper.mapRow(rs));
        };
    }
}
