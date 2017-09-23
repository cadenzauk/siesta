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

package com.cadenzauk.siesta.jdbc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

public class JdbcDataTypeRegistry {
    private final Map<Class<?>,JdbcParameterSetter<Object>> parameterSetters = parameterSetters();

    public void setParameter(PreparedStatement ps, int parameterIndex, Object o) {
        try {
            getSetter(o).setParameter(ps, parameterIndex, o);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private JdbcParameterSetter<Object> getSetter(Object value) {
        return Optional.ofNullable(value)
            .map(Object::getClass)
            .map(parameterSetters::get)
            .orElse(PreparedStatement::setObject);
    }

    private static Map<Class<?>,JdbcParameterSetter<Object>> parameterSetters() {
        return ImmutableMap.copyOf(
            ImmutableList.of(
                entry(Timestamp.class, (ps, i, ts) -> ps.setTimestamp(i, ts, new GregorianCalendar(TimeZone.getDefault()))),
                entry(Time.class, (ps, i, ts) -> ps.setTime(i, ts, new GregorianCalendar(TimeZone.getDefault())))
            )
        );
    }

    private static <D> Map.Entry<Class<?>,JdbcParameterSetter<Object>> entry(Class<D> klass, JdbcParameterSetter<D> setter) {
        return Pair.of(klass, (ps, i, o) -> setter.setParameter(ps, i, klass.cast(o)));
    }
}
