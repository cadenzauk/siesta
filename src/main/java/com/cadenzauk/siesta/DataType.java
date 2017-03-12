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

import com.cadenzauk.core.reflect.util.TypeUtil;
import com.cadenzauk.core.stream.StreamUtil;
import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.persistence.converter.LocalDateConverter;
import com.cadenzauk.persistence.converter.ZonedDateTimeConverter;
import com.google.common.collect.ImmutableMap;

import javax.persistence.AttributeConverter;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class DataType<T> {
    public static final DataType<BigDecimal> BIG_DECIMAL = new DataType<>(BigDecimal.class, ResultSet::getBigDecimal, defaultConverter());
    public static final DataType<Byte> BYTE = new DataType<>(Byte.class, ResultSet::getByte, defaultConverter());
    public static final DataType<byte[]> BYTE_ARRAY = new DataType<>(byte[].class, ResultSet::getBytes, defaultConverter());
    public static final DataType<Double> DOUBLE = new DataType<>(Double.class, ResultSet::getDouble, defaultConverter());
    public static final DataType<Float> FLOAT = new DataType<>(Float.class, ResultSet::getFloat, defaultConverter());
    public static final DataType<Integer> INTEGER = new DataType<>(Integer.class, ResultSet::getInt, defaultConverter());
    public static final DataType<Long> LONG = new DataType<>(Long.class, ResultSet::getLong, defaultConverter());
    public static final DataType<Short> SHORT = new DataType<>(Short.class, ResultSet::getShort, defaultConverter());
    public static final DataType<String> STRING = new DataType<>(String.class, ResultSet::getString, defaultConverter());
    public static final DataType<LocalDate> LOCAL_DATE = DataType.fromConverter(new LocalDateConverter());
    public static final DataType<ZonedDateTime> ZONED_DATE_TIME = DataType.fromConverter(new ZonedDateTimeConverter());

    private final Class<T> javaClass;
    private final ResultSetExtractor<T> resultSetExtractor;
    private final Function<Optional<T>,Object> converter;

    private DataType(Class<T> javaClass, ResultSetExtractor<T> resultSetExtractor, Function<Optional<T>,Object> converter) {
        this.javaClass = TypeUtil.boxedType(javaClass);
        this.resultSetExtractor = resultSetExtractor;
        this.converter = converter;
    }

    public Class<T> javaClass() {
        return javaClass;
    }

    public Object toDatabase(Optional<T> v) {
        return converter.apply(v);
    }

    public Object toDatabase(T v) {
        return converter.apply(Optional.ofNullable(v));
    }

    public Optional<T> get(ResultSet rs, String colName) {
        try {
            T value = resultSetExtractor.get(rs, colName);
            return rs.wasNull() ? Optional.empty() : Optional.of(value);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> Function<Optional<T>,Object> defaultConverter() {
        return v -> v.orElse(null);
    }

    public static <T,D,C extends AttributeConverter<T,D>> DataType<T> fromConverter(C converter) {
        ParameterizedType any = Arrays.stream(converter.getClass().getGenericInterfaces())
            .map(t -> OptionalUtil.as(ParameterizedType.class, t))
            .flatMap(StreamUtil::of)
            .filter(x -> x.getRawType() == AttributeConverter.class)
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("Unexpected error - couldn't find AttributeConverter interface."));

        @SuppressWarnings("unchecked") Class<T> entityAttributeClass = (Class<T>) any.getActualTypeArguments()[0];
        @SuppressWarnings("unchecked") Class<D> databaseClass = (Class<D>) any.getActualTypeArguments()[1];

        ResultSetExtractor<D> resultSetExtractor = ResultSetExtractor.forType(databaseClass);

        return new DataType<>(entityAttributeClass,
            (rs,n) -> converter.convertToEntityAttribute(resultSetExtractor.get(rs, n)),
            v -> v.map(converter::convertToDatabaseColumn).orElse(null));
    }

    @FunctionalInterface
    private interface ResultSetExtractor<T> {
        T get(ResultSet rs, String col) throws SQLException;

        Map<Class<?>, ResultSetExtractor<?>> extractors = ImmutableMap.<Class<?>, ResultSetExtractor<?>>builder()
            .put(BigDecimal.class, ResultSet::getBigDecimal)
            .put(Byte.class, ResultSet::getByte)
            .put(byte[].class, ResultSet::getBytes)
            .put(Date.class, ResultSet::getDate)
            .put(Double.class, ResultSet::getDouble)
            .put(Float.class, ResultSet::getFloat)
            .put(Integer.class, ResultSet::getInt)
            .put(Long.class, ResultSet::getLong)
            .put(Short.class, ResultSet::getShort)
            .put(String.class, ResultSet::getString)
            .put(Time.class, ResultSet::getTime)
            .put(Timestamp.class, ResultSet::getTimestamp)
            .build();

        @SuppressWarnings("unchecked")
        static <D> ResultSetExtractor<D> forType(Class<D> databaseClass) {
            return Optional.ofNullable((ResultSetExtractor<D>) extractors.get(databaseClass))
                .orElseThrow(() -> new IllegalArgumentException(""));
        }
    }
}
