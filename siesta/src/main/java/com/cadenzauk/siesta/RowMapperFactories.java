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
import com.cadenzauk.core.sql.RowMapperFactory;
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

import java.util.Optional;

public class RowMapperFactories {
    public static <T1, T2> RowMapperFactory<Tuple2<T1,T2>> of(RowMapperFactory<T1> factory1, RowMapperFactory<T2> factory2) {
        return (prefix, label) ->RowMappers.of(factory1.rowMapper(prefix, Optional.empty()), factory2.rowMapper(prefix, Optional.empty()));
    }

    public static <T1, T2, T3> RowMapperFactory<Tuple3<T1,T2,T3>> of(
           RowMapperFactory<T1> factory1,
           RowMapperFactory<T2> factory2,
           RowMapperFactory<T3> factory3
    ) {
        return (prefix, label) ->RowMappers.of(
                factory1.rowMapper(prefix, Optional.empty()),
                factory2.rowMapper(prefix, Optional.empty()),
                factory3.rowMapper(prefix, Optional.empty())
            );
    }

    public static <T1, T2, T3, T4> RowMapperFactory<Tuple4<T1,T2,T3,T4>> of(
           RowMapperFactory<T1> factory1,
           RowMapperFactory<T2> factory2,
           RowMapperFactory<T3> factory3,
           RowMapperFactory<T4> factory4
    ) {
        return (prefix, label) ->RowMappers.of(
                factory1.rowMapper(prefix, Optional.empty()),
                factory2.rowMapper(prefix, Optional.empty()),
                factory3.rowMapper(prefix, Optional.empty()),
                factory4.rowMapper(prefix, Optional.empty())
            );
    }

    public static <T1, T2, T3, T4, T5> RowMapperFactory<Tuple5<T1,T2,T3,T4,T5>> of(
           RowMapperFactory<T1> factory1,
           RowMapperFactory<T2> factory2,
           RowMapperFactory<T3> factory3,
           RowMapperFactory<T4> factory4,
           RowMapperFactory<T5> factory5
    ) {
        return (prefix, label) ->RowMappers.of(
                factory1.rowMapper(prefix, Optional.empty()),
                factory2.rowMapper(prefix, Optional.empty()),
                factory3.rowMapper(prefix, Optional.empty()),
                factory4.rowMapper(prefix, Optional.empty()),
                factory5.rowMapper(prefix, Optional.empty())
            );
    }

    public static <T1, T2, T3, T4, T5, T6> RowMapperFactory<Tuple6<T1,T2,T3,T4,T5,T6>> of(
           RowMapperFactory<T1> factory1,
           RowMapperFactory<T2> factory2,
           RowMapperFactory<T3> factory3,
           RowMapperFactory<T4> factory4,
           RowMapperFactory<T5> factory5,
           RowMapperFactory<T6> factory6
    ) {
        return (prefix, label) ->RowMappers.of(
                factory1.rowMapper(prefix, Optional.empty()),
                factory2.rowMapper(prefix, Optional.empty()),
                factory3.rowMapper(prefix, Optional.empty()),
                factory4.rowMapper(prefix, Optional.empty()),
                factory5.rowMapper(prefix, Optional.empty()),
                factory6.rowMapper(prefix, Optional.empty())
            );
    }

    public static <T1, T2, T3, T4, T5, T6, T7> RowMapperFactory<Tuple7<T1,T2,T3,T4,T5,T6,T7>> of(
           RowMapperFactory<T1> factory1,
           RowMapperFactory<T2> factory2,
           RowMapperFactory<T3> factory3,
           RowMapperFactory<T4> factory4,
           RowMapperFactory<T5> factory5,
           RowMapperFactory<T6> factory6,
           RowMapperFactory<T7> factory7
    ) {
        return (prefix, label) ->RowMappers.of(
                factory1.rowMapper(prefix, Optional.empty()),
                factory2.rowMapper(prefix, Optional.empty()),
                factory3.rowMapper(prefix, Optional.empty()),
                factory4.rowMapper(prefix, Optional.empty()),
                factory5.rowMapper(prefix, Optional.empty()),
                factory6.rowMapper(prefix, Optional.empty()),
                factory7.rowMapper(prefix, Optional.empty())
            );
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> RowMapperFactory<Tuple8<T1,T2,T3,T4,T5,T6,T7,T8>> of(
           RowMapperFactory<T1> factory1,
           RowMapperFactory<T2> factory2,
           RowMapperFactory<T3> factory3,
           RowMapperFactory<T4> factory4,
           RowMapperFactory<T5> factory5,
           RowMapperFactory<T6> factory6,
           RowMapperFactory<T7> factory7,
           RowMapperFactory<T8> factory8
    ) {
        return (prefix, label) ->RowMappers.of(
                factory1.rowMapper(prefix, Optional.empty()),
                factory2.rowMapper(prefix, Optional.empty()),
                factory3.rowMapper(prefix, Optional.empty()),
                factory4.rowMapper(prefix, Optional.empty()),
                factory5.rowMapper(prefix, Optional.empty()),
                factory6.rowMapper(prefix, Optional.empty()),
                factory7.rowMapper(prefix, Optional.empty()),
                factory8.rowMapper(prefix, Optional.empty())
            );
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> RowMapperFactory<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T9>> of(
           RowMapperFactory<T1> factory1,
           RowMapperFactory<T2> factory2,
           RowMapperFactory<T3> factory3,
           RowMapperFactory<T4> factory4,
           RowMapperFactory<T5> factory5,
           RowMapperFactory<T6> factory6,
           RowMapperFactory<T7> factory7,
           RowMapperFactory<T8> factory8,
           RowMapperFactory<T9> factory9
    ) {
        return (prefix, label) ->RowMappers.of(
                factory1.rowMapper(prefix, Optional.empty()),
                factory2.rowMapper(prefix, Optional.empty()),
                factory3.rowMapper(prefix, Optional.empty()),
                factory4.rowMapper(prefix, Optional.empty()),
                factory5.rowMapper(prefix, Optional.empty()),
                factory6.rowMapper(prefix, Optional.empty()),
                factory7.rowMapper(prefix, Optional.empty()),
                factory8.rowMapper(prefix, Optional.empty()),
                factory9.rowMapper(prefix, Optional.empty())
            );
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> RowMapperFactory<Tuple10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10>> of(
           RowMapperFactory<T1> factory1,
           RowMapperFactory<T2> factory2,
           RowMapperFactory<T3> factory3,
           RowMapperFactory<T4> factory4,
           RowMapperFactory<T5> factory5,
           RowMapperFactory<T6> factory6,
           RowMapperFactory<T7> factory7,
           RowMapperFactory<T8> factory8,
           RowMapperFactory<T9> factory9,
           RowMapperFactory<T10> factory10
    ) {
        return (prefix, label) ->RowMappers.of(
                factory1.rowMapper(prefix, Optional.empty()),
                factory2.rowMapper(prefix, Optional.empty()),
                factory3.rowMapper(prefix, Optional.empty()),
                factory4.rowMapper(prefix, Optional.empty()),
                factory5.rowMapper(prefix, Optional.empty()),
                factory6.rowMapper(prefix, Optional.empty()),
                factory7.rowMapper(prefix, Optional.empty()),
                factory8.rowMapper(prefix, Optional.empty()),
                factory9.rowMapper(prefix, Optional.empty()),
                factory10.rowMapper(prefix, Optional.empty())
            );
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> RowMapperFactory<Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>> of(
           RowMapperFactory<T1> factory1,
           RowMapperFactory<T2> factory2,
           RowMapperFactory<T3> factory3,
           RowMapperFactory<T4> factory4,
           RowMapperFactory<T5> factory5,
           RowMapperFactory<T6> factory6,
           RowMapperFactory<T7> factory7,
           RowMapperFactory<T8> factory8,
           RowMapperFactory<T9> factory9,
           RowMapperFactory<T10> factory10,
           RowMapperFactory<T11> factory11
    ) {
        return (prefix, label) ->RowMappers.of(
                factory1.rowMapper(prefix, Optional.empty()),
                factory2.rowMapper(prefix, Optional.empty()),
                factory3.rowMapper(prefix, Optional.empty()),
                factory4.rowMapper(prefix, Optional.empty()),
                factory5.rowMapper(prefix, Optional.empty()),
                factory6.rowMapper(prefix, Optional.empty()),
                factory7.rowMapper(prefix, Optional.empty()),
                factory8.rowMapper(prefix, Optional.empty()),
                factory9.rowMapper(prefix, Optional.empty()),
                factory10.rowMapper(prefix, Optional.empty()),
                factory11.rowMapper(prefix, Optional.empty())
            );
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> RowMapperFactory<Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12>> of(
           RowMapperFactory<T1> factory1,
           RowMapperFactory<T2> factory2,
           RowMapperFactory<T3> factory3,
           RowMapperFactory<T4> factory4,
           RowMapperFactory<T5> factory5,
           RowMapperFactory<T6> factory6,
           RowMapperFactory<T7> factory7,
           RowMapperFactory<T8> factory8,
           RowMapperFactory<T9> factory9,
           RowMapperFactory<T10> factory10,
           RowMapperFactory<T11> factory11,
           RowMapperFactory<T12> factory12
    ) {
        return (prefix, label) ->RowMappers.of(
                factory1.rowMapper(prefix, Optional.empty()),
                factory2.rowMapper(prefix, Optional.empty()),
                factory3.rowMapper(prefix, Optional.empty()),
                factory4.rowMapper(prefix, Optional.empty()),
                factory5.rowMapper(prefix, Optional.empty()),
                factory6.rowMapper(prefix, Optional.empty()),
                factory7.rowMapper(prefix, Optional.empty()),
                factory8.rowMapper(prefix, Optional.empty()),
                factory9.rowMapper(prefix, Optional.empty()),
                factory10.rowMapper(prefix, Optional.empty()),
                factory11.rowMapper(prefix, Optional.empty()),
                factory12.rowMapper(prefix, Optional.empty())
            );
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> RowMapperFactory<Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13>> of(
           RowMapperFactory<T1> factory1,
           RowMapperFactory<T2> factory2,
           RowMapperFactory<T3> factory3,
           RowMapperFactory<T4> factory4,
           RowMapperFactory<T5> factory5,
           RowMapperFactory<T6> factory6,
           RowMapperFactory<T7> factory7,
           RowMapperFactory<T8> factory8,
           RowMapperFactory<T9> factory9,
           RowMapperFactory<T10> factory10,
           RowMapperFactory<T11> factory11,
           RowMapperFactory<T12> factory12,
           RowMapperFactory<T13> factory13
    ) {
        return (prefix, label) ->RowMappers.of(
                factory1.rowMapper(prefix, Optional.empty()),
                factory2.rowMapper(prefix, Optional.empty()),
                factory3.rowMapper(prefix, Optional.empty()),
                factory4.rowMapper(prefix, Optional.empty()),
                factory5.rowMapper(prefix, Optional.empty()),
                factory6.rowMapper(prefix, Optional.empty()),
                factory7.rowMapper(prefix, Optional.empty()),
                factory8.rowMapper(prefix, Optional.empty()),
                factory9.rowMapper(prefix, Optional.empty()),
                factory10.rowMapper(prefix, Optional.empty()),
                factory11.rowMapper(prefix, Optional.empty()),
                factory12.rowMapper(prefix, Optional.empty()),
                factory13.rowMapper(prefix, Optional.empty())
            );
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> RowMapperFactory<Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14>> of(
           RowMapperFactory<T1> factory1,
           RowMapperFactory<T2> factory2,
           RowMapperFactory<T3> factory3,
           RowMapperFactory<T4> factory4,
           RowMapperFactory<T5> factory5,
           RowMapperFactory<T6> factory6,
           RowMapperFactory<T7> factory7,
           RowMapperFactory<T8> factory8,
           RowMapperFactory<T9> factory9,
           RowMapperFactory<T10> factory10,
           RowMapperFactory<T11> factory11,
           RowMapperFactory<T12> factory12,
           RowMapperFactory<T13> factory13,
           RowMapperFactory<T14> factory14
    ) {
        return (prefix, label) ->RowMappers.of(
                factory1.rowMapper(prefix, Optional.empty()),
                factory2.rowMapper(prefix, Optional.empty()),
                factory3.rowMapper(prefix, Optional.empty()),
                factory4.rowMapper(prefix, Optional.empty()),
                factory5.rowMapper(prefix, Optional.empty()),
                factory6.rowMapper(prefix, Optional.empty()),
                factory7.rowMapper(prefix, Optional.empty()),
                factory8.rowMapper(prefix, Optional.empty()),
                factory9.rowMapper(prefix, Optional.empty()),
                factory10.rowMapper(prefix, Optional.empty()),
                factory11.rowMapper(prefix, Optional.empty()),
                factory12.rowMapper(prefix, Optional.empty()),
                factory13.rowMapper(prefix, Optional.empty()),
                factory14.rowMapper(prefix, Optional.empty())
            );
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> RowMapperFactory<Tuple15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15>> of(
           RowMapperFactory<T1> factory1,
           RowMapperFactory<T2> factory2,
           RowMapperFactory<T3> factory3,
           RowMapperFactory<T4> factory4,
           RowMapperFactory<T5> factory5,
           RowMapperFactory<T6> factory6,
           RowMapperFactory<T7> factory7,
           RowMapperFactory<T8> factory8,
           RowMapperFactory<T9> factory9,
           RowMapperFactory<T10> factory10,
           RowMapperFactory<T11> factory11,
           RowMapperFactory<T12> factory12,
           RowMapperFactory<T13> factory13,
           RowMapperFactory<T14> factory14,
           RowMapperFactory<T15> factory15
    ) {
        return (prefix, label) ->RowMappers.of(
                factory1.rowMapper(prefix, Optional.empty()),
                factory2.rowMapper(prefix, Optional.empty()),
                factory3.rowMapper(prefix, Optional.empty()),
                factory4.rowMapper(prefix, Optional.empty()),
                factory5.rowMapper(prefix, Optional.empty()),
                factory6.rowMapper(prefix, Optional.empty()),
                factory7.rowMapper(prefix, Optional.empty()),
                factory8.rowMapper(prefix, Optional.empty()),
                factory9.rowMapper(prefix, Optional.empty()),
                factory10.rowMapper(prefix, Optional.empty()),
                factory11.rowMapper(prefix, Optional.empty()),
                factory12.rowMapper(prefix, Optional.empty()),
                factory13.rowMapper(prefix, Optional.empty()),
                factory14.rowMapper(prefix, Optional.empty()),
                factory15.rowMapper(prefix, Optional.empty())
            );
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> RowMapperFactory<Tuple16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16>> of(
           RowMapperFactory<T1> factory1,
           RowMapperFactory<T2> factory2,
           RowMapperFactory<T3> factory3,
           RowMapperFactory<T4> factory4,
           RowMapperFactory<T5> factory5,
           RowMapperFactory<T6> factory6,
           RowMapperFactory<T7> factory7,
           RowMapperFactory<T8> factory8,
           RowMapperFactory<T9> factory9,
           RowMapperFactory<T10> factory10,
           RowMapperFactory<T11> factory11,
           RowMapperFactory<T12> factory12,
           RowMapperFactory<T13> factory13,
           RowMapperFactory<T14> factory14,
           RowMapperFactory<T15> factory15,
           RowMapperFactory<T16> factory16
    ) {
        return (prefix, label) ->RowMappers.of(
                factory1.rowMapper(prefix, Optional.empty()),
                factory2.rowMapper(prefix, Optional.empty()),
                factory3.rowMapper(prefix, Optional.empty()),
                factory4.rowMapper(prefix, Optional.empty()),
                factory5.rowMapper(prefix, Optional.empty()),
                factory6.rowMapper(prefix, Optional.empty()),
                factory7.rowMapper(prefix, Optional.empty()),
                factory8.rowMapper(prefix, Optional.empty()),
                factory9.rowMapper(prefix, Optional.empty()),
                factory10.rowMapper(prefix, Optional.empty()),
                factory11.rowMapper(prefix, Optional.empty()),
                factory12.rowMapper(prefix, Optional.empty()),
                factory13.rowMapper(prefix, Optional.empty()),
                factory14.rowMapper(prefix, Optional.empty()),
                factory15.rowMapper(prefix, Optional.empty()),
                factory16.rowMapper(prefix, Optional.empty())
            );
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> RowMapperFactory<Tuple17<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17>> of(
           RowMapperFactory<T1> factory1,
           RowMapperFactory<T2> factory2,
           RowMapperFactory<T3> factory3,
           RowMapperFactory<T4> factory4,
           RowMapperFactory<T5> factory5,
           RowMapperFactory<T6> factory6,
           RowMapperFactory<T7> factory7,
           RowMapperFactory<T8> factory8,
           RowMapperFactory<T9> factory9,
           RowMapperFactory<T10> factory10,
           RowMapperFactory<T11> factory11,
           RowMapperFactory<T12> factory12,
           RowMapperFactory<T13> factory13,
           RowMapperFactory<T14> factory14,
           RowMapperFactory<T15> factory15,
           RowMapperFactory<T16> factory16,
           RowMapperFactory<T17> factory17
    ) {
        return (prefix, label) ->RowMappers.of(
                factory1.rowMapper(prefix, Optional.empty()),
                factory2.rowMapper(prefix, Optional.empty()),
                factory3.rowMapper(prefix, Optional.empty()),
                factory4.rowMapper(prefix, Optional.empty()),
                factory5.rowMapper(prefix, Optional.empty()),
                factory6.rowMapper(prefix, Optional.empty()),
                factory7.rowMapper(prefix, Optional.empty()),
                factory8.rowMapper(prefix, Optional.empty()),
                factory9.rowMapper(prefix, Optional.empty()),
                factory10.rowMapper(prefix, Optional.empty()),
                factory11.rowMapper(prefix, Optional.empty()),
                factory12.rowMapper(prefix, Optional.empty()),
                factory13.rowMapper(prefix, Optional.empty()),
                factory14.rowMapper(prefix, Optional.empty()),
                factory15.rowMapper(prefix, Optional.empty()),
                factory16.rowMapper(prefix, Optional.empty()),
                factory17.rowMapper(prefix, Optional.empty())
            );
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> RowMapperFactory<Tuple18<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18>> of(
           RowMapperFactory<T1> factory1,
           RowMapperFactory<T2> factory2,
           RowMapperFactory<T3> factory3,
           RowMapperFactory<T4> factory4,
           RowMapperFactory<T5> factory5,
           RowMapperFactory<T6> factory6,
           RowMapperFactory<T7> factory7,
           RowMapperFactory<T8> factory8,
           RowMapperFactory<T9> factory9,
           RowMapperFactory<T10> factory10,
           RowMapperFactory<T11> factory11,
           RowMapperFactory<T12> factory12,
           RowMapperFactory<T13> factory13,
           RowMapperFactory<T14> factory14,
           RowMapperFactory<T15> factory15,
           RowMapperFactory<T16> factory16,
           RowMapperFactory<T17> factory17,
           RowMapperFactory<T18> factory18
    ) {
        return (prefix, label) ->RowMappers.of(
                factory1.rowMapper(prefix, Optional.empty()),
                factory2.rowMapper(prefix, Optional.empty()),
                factory3.rowMapper(prefix, Optional.empty()),
                factory4.rowMapper(prefix, Optional.empty()),
                factory5.rowMapper(prefix, Optional.empty()),
                factory6.rowMapper(prefix, Optional.empty()),
                factory7.rowMapper(prefix, Optional.empty()),
                factory8.rowMapper(prefix, Optional.empty()),
                factory9.rowMapper(prefix, Optional.empty()),
                factory10.rowMapper(prefix, Optional.empty()),
                factory11.rowMapper(prefix, Optional.empty()),
                factory12.rowMapper(prefix, Optional.empty()),
                factory13.rowMapper(prefix, Optional.empty()),
                factory14.rowMapper(prefix, Optional.empty()),
                factory15.rowMapper(prefix, Optional.empty()),
                factory16.rowMapper(prefix, Optional.empty()),
                factory17.rowMapper(prefix, Optional.empty()),
                factory18.rowMapper(prefix, Optional.empty())
            );
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> RowMapperFactory<Tuple19<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19>> of(
           RowMapperFactory<T1> factory1,
           RowMapperFactory<T2> factory2,
           RowMapperFactory<T3> factory3,
           RowMapperFactory<T4> factory4,
           RowMapperFactory<T5> factory5,
           RowMapperFactory<T6> factory6,
           RowMapperFactory<T7> factory7,
           RowMapperFactory<T8> factory8,
           RowMapperFactory<T9> factory9,
           RowMapperFactory<T10> factory10,
           RowMapperFactory<T11> factory11,
           RowMapperFactory<T12> factory12,
           RowMapperFactory<T13> factory13,
           RowMapperFactory<T14> factory14,
           RowMapperFactory<T15> factory15,
           RowMapperFactory<T16> factory16,
           RowMapperFactory<T17> factory17,
           RowMapperFactory<T18> factory18,
           RowMapperFactory<T19> factory19
    ) {
        return (prefix, label) ->RowMappers.of(
                factory1.rowMapper(prefix, Optional.empty()),
                factory2.rowMapper(prefix, Optional.empty()),
                factory3.rowMapper(prefix, Optional.empty()),
                factory4.rowMapper(prefix, Optional.empty()),
                factory5.rowMapper(prefix, Optional.empty()),
                factory6.rowMapper(prefix, Optional.empty()),
                factory7.rowMapper(prefix, Optional.empty()),
                factory8.rowMapper(prefix, Optional.empty()),
                factory9.rowMapper(prefix, Optional.empty()),
                factory10.rowMapper(prefix, Optional.empty()),
                factory11.rowMapper(prefix, Optional.empty()),
                factory12.rowMapper(prefix, Optional.empty()),
                factory13.rowMapper(prefix, Optional.empty()),
                factory14.rowMapper(prefix, Optional.empty()),
                factory15.rowMapper(prefix, Optional.empty()),
                factory16.rowMapper(prefix, Optional.empty()),
                factory17.rowMapper(prefix, Optional.empty()),
                factory18.rowMapper(prefix, Optional.empty()),
                factory19.rowMapper(prefix, Optional.empty())
            );
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> RowMapperFactory<Tuple20<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20>> of(
           RowMapperFactory<T1> factory1,
           RowMapperFactory<T2> factory2,
           RowMapperFactory<T3> factory3,
           RowMapperFactory<T4> factory4,
           RowMapperFactory<T5> factory5,
           RowMapperFactory<T6> factory6,
           RowMapperFactory<T7> factory7,
           RowMapperFactory<T8> factory8,
           RowMapperFactory<T9> factory9,
           RowMapperFactory<T10> factory10,
           RowMapperFactory<T11> factory11,
           RowMapperFactory<T12> factory12,
           RowMapperFactory<T13> factory13,
           RowMapperFactory<T14> factory14,
           RowMapperFactory<T15> factory15,
           RowMapperFactory<T16> factory16,
           RowMapperFactory<T17> factory17,
           RowMapperFactory<T18> factory18,
           RowMapperFactory<T19> factory19,
           RowMapperFactory<T20> factory20
    ) {
        return (prefix, label) ->RowMappers.of(
                factory1.rowMapper(prefix, Optional.empty()),
                factory2.rowMapper(prefix, Optional.empty()),
                factory3.rowMapper(prefix, Optional.empty()),
                factory4.rowMapper(prefix, Optional.empty()),
                factory5.rowMapper(prefix, Optional.empty()),
                factory6.rowMapper(prefix, Optional.empty()),
                factory7.rowMapper(prefix, Optional.empty()),
                factory8.rowMapper(prefix, Optional.empty()),
                factory9.rowMapper(prefix, Optional.empty()),
                factory10.rowMapper(prefix, Optional.empty()),
                factory11.rowMapper(prefix, Optional.empty()),
                factory12.rowMapper(prefix, Optional.empty()),
                factory13.rowMapper(prefix, Optional.empty()),
                factory14.rowMapper(prefix, Optional.empty()),
                factory15.rowMapper(prefix, Optional.empty()),
                factory16.rowMapper(prefix, Optional.empty()),
                factory17.rowMapper(prefix, Optional.empty()),
                factory18.rowMapper(prefix, Optional.empty()),
                factory19.rowMapper(prefix, Optional.empty()),
                factory20.rowMapper(prefix, Optional.empty())
            );
    }

    public static <T1, T2, T3> RowMapperFactory<Tuple3<T1,T2,T3>> add3rd(RowMapperFactory<Tuple2<T1,T2>> factory2, RowMapperFactory<T3> factory) {
        return (prefix, label) -> {
            RowMapper<Tuple2<T1,T2>> tuple = factory2.rowMapper(prefix, Optional.empty());
            return RowMappers.add3rd(tuple, factory.rowMapper(prefix, Optional.empty()));
        };
    }

    public static <T1, T2, T3, T4> RowMapperFactory<Tuple4<T1,T2,T3,T4>> add4th(RowMapperFactory<Tuple3<T1,T2,T3>> factory3, RowMapperFactory<T4> factory) {
        return (prefix, label) -> {
            RowMapper<Tuple3<T1,T2,T3>> tuple = factory3.rowMapper(prefix, Optional.empty());
            return RowMappers.add4th(tuple, factory.rowMapper(prefix, Optional.empty()));
        };
    }

    public static <T1, T2, T3, T4, T5> RowMapperFactory<Tuple5<T1,T2,T3,T4,T5>> add5th(RowMapperFactory<Tuple4<T1,T2,T3,T4>> factory4, RowMapperFactory<T5> factory) {
        return (prefix, label) -> {
            RowMapper<Tuple4<T1,T2,T3,T4>> tuple = factory4.rowMapper(prefix, Optional.empty());
            return RowMappers.add5th(tuple, factory.rowMapper(prefix, Optional.empty()));
        };
    }

    public static <T1, T2, T3, T4, T5, T6> RowMapperFactory<Tuple6<T1,T2,T3,T4,T5,T6>> add6th(RowMapperFactory<Tuple5<T1,T2,T3,T4,T5>> factory5, RowMapperFactory<T6> factory) {
        return (prefix, label) -> {
            RowMapper<Tuple5<T1,T2,T3,T4,T5>> tuple = factory5.rowMapper(prefix, Optional.empty());
            return RowMappers.add6th(tuple, factory.rowMapper(prefix, Optional.empty()));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7> RowMapperFactory<Tuple7<T1,T2,T3,T4,T5,T6,T7>> add7th(RowMapperFactory<Tuple6<T1,T2,T3,T4,T5,T6>> factory6, RowMapperFactory<T7> factory) {
        return (prefix, label) -> {
            RowMapper<Tuple6<T1,T2,T3,T4,T5,T6>> tuple = factory6.rowMapper(prefix, Optional.empty());
            return RowMappers.add7th(tuple, factory.rowMapper(prefix, Optional.empty()));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> RowMapperFactory<Tuple8<T1,T2,T3,T4,T5,T6,T7,T8>> add8th(RowMapperFactory<Tuple7<T1,T2,T3,T4,T5,T6,T7>> factory7, RowMapperFactory<T8> factory) {
        return (prefix, label) -> {
            RowMapper<Tuple7<T1,T2,T3,T4,T5,T6,T7>> tuple = factory7.rowMapper(prefix, Optional.empty());
            return RowMappers.add8th(tuple, factory.rowMapper(prefix, Optional.empty()));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> RowMapperFactory<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T9>> add9th(RowMapperFactory<Tuple8<T1,T2,T3,T4,T5,T6,T7,T8>> factory8, RowMapperFactory<T9> factory) {
        return (prefix, label) -> {
            RowMapper<Tuple8<T1,T2,T3,T4,T5,T6,T7,T8>> tuple = factory8.rowMapper(prefix, Optional.empty());
            return RowMappers.add9th(tuple, factory.rowMapper(prefix, Optional.empty()));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> RowMapperFactory<Tuple10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10>> add10th(RowMapperFactory<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T9>> factory9, RowMapperFactory<T10> factory) {
        return (prefix, label) -> {
            RowMapper<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T9>> tuple = factory9.rowMapper(prefix, Optional.empty());
            return RowMappers.add10th(tuple, factory.rowMapper(prefix, Optional.empty()));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> RowMapperFactory<Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>> add11th(RowMapperFactory<Tuple10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10>> factory10, RowMapperFactory<T11> factory) {
        return (prefix, label) -> {
            RowMapper<Tuple10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10>> tuple = factory10.rowMapper(prefix, Optional.empty());
            return RowMappers.add11th(tuple, factory.rowMapper(prefix, Optional.empty()));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> RowMapperFactory<Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12>> add12th(RowMapperFactory<Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>> factory11, RowMapperFactory<T12> factory) {
        return (prefix, label) -> {
            RowMapper<Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>> tuple = factory11.rowMapper(prefix, Optional.empty());
            return RowMappers.add12th(tuple, factory.rowMapper(prefix, Optional.empty()));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> RowMapperFactory<Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13>> add13th(RowMapperFactory<Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12>> factory12, RowMapperFactory<T13> factory) {
        return (prefix, label) -> {
            RowMapper<Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12>> tuple = factory12.rowMapper(prefix, Optional.empty());
            return RowMappers.add13th(tuple, factory.rowMapper(prefix, Optional.empty()));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> RowMapperFactory<Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14>> add14th(RowMapperFactory<Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13>> factory13, RowMapperFactory<T14> factory) {
        return (prefix, label) -> {
            RowMapper<Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13>> tuple = factory13.rowMapper(prefix, Optional.empty());
            return RowMappers.add14th(tuple, factory.rowMapper(prefix, Optional.empty()));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> RowMapperFactory<Tuple15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15>> add15th(RowMapperFactory<Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14>> factory14, RowMapperFactory<T15> factory) {
        return (prefix, label) -> {
            RowMapper<Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14>> tuple = factory14.rowMapper(prefix, Optional.empty());
            return RowMappers.add15th(tuple, factory.rowMapper(prefix, Optional.empty()));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> RowMapperFactory<Tuple16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16>> add16th(RowMapperFactory<Tuple15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15>> factory15, RowMapperFactory<T16> factory) {
        return (prefix, label) -> {
            RowMapper<Tuple15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15>> tuple = factory15.rowMapper(prefix, Optional.empty());
            return RowMappers.add16th(tuple, factory.rowMapper(prefix, Optional.empty()));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> RowMapperFactory<Tuple17<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17>> add17th(RowMapperFactory<Tuple16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16>> factory16, RowMapperFactory<T17> factory) {
        return (prefix, label) -> {
            RowMapper<Tuple16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16>> tuple = factory16.rowMapper(prefix, Optional.empty());
            return RowMappers.add17th(tuple, factory.rowMapper(prefix, Optional.empty()));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> RowMapperFactory<Tuple18<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18>> add18th(RowMapperFactory<Tuple17<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17>> factory17, RowMapperFactory<T18> factory) {
        return (prefix, label) -> {
            RowMapper<Tuple17<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17>> tuple = factory17.rowMapper(prefix, Optional.empty());
            return RowMappers.add18th(tuple, factory.rowMapper(prefix, Optional.empty()));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19> RowMapperFactory<Tuple19<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19>> add19th(RowMapperFactory<Tuple18<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18>> factory18, RowMapperFactory<T19> factory) {
        return (prefix, label) -> {
            RowMapper<Tuple18<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18>> tuple = factory18.rowMapper(prefix, Optional.empty());
            return RowMappers.add19th(tuple, factory.rowMapper(prefix, Optional.empty()));
        };
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20> RowMapperFactory<Tuple20<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19,T20>> add20th(RowMapperFactory<Tuple19<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19>> factory19, RowMapperFactory<T20> factory) {
        return (prefix, label) -> {
            RowMapper<Tuple19<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16,T17,T18,T19>> tuple = factory19.rowMapper(prefix, Optional.empty());
            return RowMappers.add20th(tuple, factory.rowMapper(prefix, Optional.empty()));
        };
    }
}
