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

package com.cadenzauk.siesta.type;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.UUID;

public class DbTypeId<T> {
    public final static DbTypeId<Byte> TINYINT = new DbTypeId<>("tinyint", Types.TINYINT, Byte.class);
    public final static DbTypeId<Short> SMALLINT = new DbTypeId<>("smallint", Types.SMALLINT, Short.class);
    public final static DbTypeId<Integer> INTEGER = new DbTypeId<>("integer", Types.INTEGER, Integer.class);
    public final static DbTypeId<Long> BIGINT = new DbTypeId<>("bigint", Types.BIGINT, Long.class);

    public final static DbTypeId<byte[]> BINARY = new DbTypeId<>("binary", Types.BINARY, byte[].class);
    public final static DbTypeId<byte[]> VARBINARY = new DbTypeId<>("varbinary", Types.VARBINARY, byte[].class);

    public final static DbTypeId<Double> DOUBLE = new DbTypeId<>("double precision", Types.DOUBLE, Double.class);
    public final static DbTypeId<Float> REAL = new DbTypeId<>("real", Types.FLOAT, Float.class);
    public final static DbTypeId<BigDecimal> DECIMAL = new DbTypeId<>("decimal", Types.DECIMAL, BigDecimal.class);

    public final static DbTypeId<LocalDate> DATE = new DbTypeId<>("date", Types.DATE, LocalDate.class);
    public final static DbTypeId<LocalTime> TIME = new DbTypeId<>("time", Types.TIME, LocalTime.class);
    public final static DbTypeId<LocalDateTime> TIMESTAMP = new DbTypeId<>("timestamp", Types.TIMESTAMP, LocalDateTime.class);
    public final static DbTypeId<UUID> UUID = new DbTypeId<>("guid", Types.BINARY, UUID.class);
    public final static DbTypeId<ZonedDateTime> UTC_TIMESTAMP = new DbTypeId<>("utctimestamp", Types.TIMESTAMP, ZonedDateTime.class);

    public final static DbTypeId<String> CHAR = new DbTypeId<>("char", Types.CHAR, String.class);
    public final static DbTypeId<String> VARCHAR = new DbTypeId<>("varchar", Types.VARCHAR, String.class);

    private final String name;
    private final int typeCode;
    private final Class<T> javaClass;

    public DbTypeId(String name, int typeCode, Class<T> javaClass) {
        this.name = name;
        this.typeCode = typeCode;
        this.javaClass = javaClass;
    }

    public String name() {
        return name;
    }

    public int typeCode() {
        return typeCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        DbTypeId<?> dbTypeId = (DbTypeId<?>) o;

        return new EqualsBuilder()
            .append(typeCode, dbTypeId.typeCode)
            .append(name, dbTypeId.name)
            .append(javaClass, dbTypeId.javaClass)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(name)
            .append(typeCode)
            .append(javaClass)
            .toHashCode();
    }

    public static <T> DbTypeId<T> embedded(Class<T> javaClass) {
        return new DbTypeId<>(javaClass.getCanonicalName(), Types.JAVA_OBJECT, javaClass);
    }
}
