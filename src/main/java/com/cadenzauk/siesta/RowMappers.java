/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import com.cadenzauk.core.tuple.Tuple;
import com.cadenzauk.core.tuple.Tuple2;
import org.springframework.jdbc.core.RowMapper;

public class RowMappers {
    public static <T1, T2> RowMapper<Tuple2<T1, T2>> of(RowMapper<T1> mapper1, RowMapper<T2> mapper2) {
        return (rs, i) -> Tuple.of(mapper1.mapRow(rs, i), mapper2.mapRow(rs, i));
    }
}
