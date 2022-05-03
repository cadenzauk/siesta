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

import com.cadenzauk.siesta.json.BinaryJson;
import com.cadenzauk.siesta.json.Json;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

public class DbTypeId<T> {
    public static final DbTypeId<Byte> TINYINT = new DbTypeId<>("tinyint", Types.TINYINT, Byte.class);
    public static final DbTypeId<Short> SMALLINT = new DbTypeId<>("smallint", Types.SMALLINT, Short.class);
    public static final DbTypeId<Integer> INTEGER = new DbTypeId<>("integer", Types.INTEGER, Integer.class);
    public static final DbTypeId<Long> BIGINT = new DbTypeId<>("bigint", Types.BIGINT, Long.class);

    public static final DbTypeId<byte[]> BINARY = new DbTypeId<byte[]>("binary", Types.BINARY, byte[].class) {
        @Override
        public Optional<Integer> length(int len, int prec) {
            return Optional.of(len);
        }
    };
    public static final DbTypeId<byte[]> VARBINARY = new DbTypeId<byte[]>("varbinary", Types.VARBINARY, byte[].class) {
        @Override
        public Optional<Integer> length(int len, int prec) {
            return Optional.of(len);
        }
    };

    public static final DbTypeId<Double> DOUBLE = new DbTypeId<>("double precision", Types.DOUBLE, Double.class);
    public static final DbTypeId<Float> REAL = new DbTypeId<>("real", Types.FLOAT, Float.class);
    public static final DbTypeId<BigDecimal> DECIMAL = new DbTypeId<BigDecimal>("decimal", Types.DECIMAL, BigDecimal.class) {
        @Override
        public Optional<Integer> length(int len, int prec) {
            return Optional.of(prec > 0 ? prec : 18);
        }

        @Override
        public Optional<Integer> scale(int scale) {
            return Optional.of(scale);
        }
    };

    public static final DbTypeId<LocalDate> DATE = new DbTypeId<>("date", Types.DATE, LocalDate.class);
    public static final DbTypeId<LocalTime> TIME = new DbTypeId<>("time", Types.TIME, LocalTime.class);
    public static final DbTypeId<LocalDateTime> TIMESTAMP = new DbTypeId<>("timestamp", Types.TIMESTAMP, LocalDateTime.class);
    public static final DbTypeId<UUID> UUID = new DbTypeId<UUID>("guid", Types.BINARY, UUID.class) {
        @Override
        public Optional<Integer> length(int len, int prec) {
            return Optional.of(len);
        }
    };
    public static final DbTypeId<ZonedDateTime> UTC_TIMESTAMP = new DbTypeId<>("utctimestamp", Types.TIMESTAMP, ZonedDateTime.class);

    public static final DbTypeId<String> CHAR = new DbTypeId<String>("char", Types.CHAR, String.class) {
        @Override
        public Optional<Integer> length(int len, int prec) {
            return Optional.of(len);
        }
    };
    public static final DbTypeId<String> VARCHAR = new DbTypeId<String>("varchar", Types.VARCHAR, String.class) {
        @Override
        public Optional<Integer> length(int len, int prec) {
            return Optional.of(len);
        }
    };

    public static final DbTypeId<Json> JSON = new DbTypeId<Json>("json", Types.JAVA_OBJECT, Json.class) {
        @Override
        public Optional<Integer> length(int len, int prec) {
            return Optional.of(len);
        }
    };

    public static final DbTypeId<BinaryJson> JSONB = new DbTypeId<BinaryJson>("json", Types.JAVA_OBJECT, BinaryJson.class) {
        @Override
        public Optional<Integer> length(int len, int prec) {
            return Optional.of(len);
        }
    };

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

    public Optional<Integer> length(int len, int prec) {
        return Optional.empty();
    }

    public Optional<Integer> scale(int scale) {
        return Optional.empty();
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

    public static <T> DbTypeId<T> of(Class<T> javaClass) {
        return new DbTypeId<>(javaClass.getCanonicalName(), Types.JAVA_OBJECT, javaClass);
    }
}
