/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
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
}
