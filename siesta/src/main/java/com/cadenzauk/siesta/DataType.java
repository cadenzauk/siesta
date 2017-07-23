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
import com.cadenzauk.core.sql.SqlDateUtil;
import com.cadenzauk.core.sql.TimestampUtil;
import com.google.common.collect.ImmutableMap;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.function.BiFunction;

public class DataType<T> {
    public static final DataType<BigDecimal> BIG_DECIMAL = new DataType<>(BigDecimal.class,
        (rs, col, db) -> rs.getBigDecimal(col),
        defaultConverter(),
        defaultLiteralFormatter(),
        defaultParameterFormatter());
    public static final DataType<Byte> BYTE = new DataType<>(Byte.class,
        (rs, col, db) -> rs.getByte(col),
        defaultConverter(),
        Dialect::byteLiteral,
        defaultParameterFormatter());
    public static final DataType<byte[]> BYTE_ARRAY = new DataType<>(byte[].class,
        (rs, col, db) -> rs.getBytes(col),
        defaultConverter(),
        Dialect::binaryLiteral,
        defaultParameterFormatter());
    public static final DataType<Double> DOUBLE = new DataType<>(Double.class,
        (rs, col, db) -> rs.getDouble(col),
        defaultConverter(),
        defaultLiteralFormatter(),
        defaultParameterFormatter());
    public static final DataType<Float> FLOAT = new DataType<>(Float.class,
        (rs, col, db) -> rs.getFloat(col),
        defaultConverter(),
        Dialect::floatLiteral,
        defaultParameterFormatter());
    public static final DataType<Integer> INTEGER = new DataType<>(Integer.class,
        (rs, col, db) -> rs.getInt(col),
        defaultConverter(),
        defaultLiteralFormatter(),
        defaultParameterFormatter());
    public static final DataType<LocalDate> LOCAL_DATE = new DataType<>(LocalDate.class,
        DataType::getLocalDate,
        DataType::localDateToDb,
        Database::dateLiteral,
        Dialect::dateParameter);
    public static final DataType<LocalDateTime> LOCAL_DATE_TIME = new DataType<>(LocalDateTime.class,
        DataType::getLocalDateTime,
        DataType::localDateTimeToDb,
        Database::timestampLiteral,
        Dialect::timestampParameter);
    public static final DataType<Long> LONG = new DataType<>(Long.class,
        (rs, col, db) -> rs.getLong(col),
        defaultConverter(),
        defaultLiteralFormatter(),
        defaultParameterFormatter());
    public static final DataType<Short> SHORT = new DataType<>(Short.class,
        (rs, col, db) -> rs.getShort(col),
        defaultConverter(),
        Dialect::smallIntLiteral,
        defaultParameterFormatter());
    public static final DataType<String> STRING = new DataType<>(String.class,
        (rs, col, db) -> rs.getString(col),
        defaultConverter(),
        Dialect::stringLiteral,
        defaultParameterFormatter());
    public static final DataType<ZonedDateTime> ZONED_DATE_TIME = new DataType<>(ZonedDateTime.class,
        DataType::getZonedDateTime,
        DataType::zonedDateTimeToDb,
        Database::timestampWithTimeZoneLiteral,
        Dialect::timestampWithTimeZoneParameter);

    private final Class<T> javaClass;
    private final ResultSetExtractor<T> resultSetExtractor;
    private final BiFunction<Database,Optional<T>,Object> converter;
    private final LiteralFormatter<T> literalFormatter;
    private final ParameterFormatter<T> parameterFormatter;

    private DataType(Class<T> javaClass, ResultSetExtractor<T> resultSetExtractor, BiFunction<Database,Optional<T>, Object> converter, BiFunction<Dialect,T,String> literalFormatter, ParameterFormatter<T> parameterFormatter) {
        this(javaClass, resultSetExtractor, converter, (Database db, T val) -> literalFormatter.apply(db.dialect(), val), parameterFormatter);
    }

    private DataType(Class<T> javaClass, ResultSetExtractor<T> resultSetExtractor, BiFunction<Database,Optional<T>,Object> converter, LiteralFormatter<T> literalFormatter, ParameterFormatter<T> parameterFormatter) {
        this.javaClass = TypeUtil.boxedType(javaClass);
        this.resultSetExtractor = resultSetExtractor;
        this.converter = converter;
        this.literalFormatter = literalFormatter;
        this.parameterFormatter = parameterFormatter;
    }

    public Class<T> javaClass() {
        return javaClass;
    }

    public Object toDatabase(Database database, Optional<T> v) {
        return converter.apply(database, v);
    }

    public Object toDatabase(Database database, T v) {
        return converter.apply(database, Optional.ofNullable(v));
    }

    public Optional<T> get(ResultSet rs, String colName, Database database) {
        try {
            T value = resultSetExtractor.get(rs, colName, database);
            return rs.wasNull() ? Optional.empty() : Optional.of(value);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String literal(Database database, T value) {
        return literalFormatter.format(database, value);
    }

    public String sqlType(Dialect dialect, T value) {
        return parameterFormatter.format(dialect, value);
    }

    private static Timestamp getTimestamp(ResultSet resultSet, String columnLabel, Database db) throws SQLException {
        return resultSet.getTimestamp(columnLabel, new GregorianCalendar(TimeZone.getTimeZone(db.databaseTimeZone())));
    }

    private static Date localDateToDb(Database db, Optional<LocalDate> value) {
        return value.map(SqlDateUtil::valueOf).orElse(null);
    }

    private static Timestamp localDateTimeToDb(Database db, Optional<LocalDateTime> value) {
        return value.map(Timestamp::valueOf).orElse(null);
    }

    private static Timestamp zonedDateTimeToDb(Database db, Optional<ZonedDateTime> value) {
        return value
            .map(v -> TimestampUtil.valueOf(db.databaseTimeZone(), v))
            .orElse(null);
    }

    private static LocalDate getLocalDate(ResultSet rs, String col, Database db) throws SQLException {
        Date date = rs.getDate(col, new GregorianCalendar(TimeZone.getDefault()));
        return SqlDateUtil.toLocalDate(date);
    }

    private static LocalDateTime getLocalDateTime(ResultSet rs, String col, Database db) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(col, new GregorianCalendar(TimeZone.getDefault()));
        return timestamp.toLocalDateTime();
    }

    private static ZonedDateTime getZonedDateTime(ResultSet rs, String col, Database db) throws SQLException {
        Timestamp timestamp = getTimestamp(rs, col, db);
        return TimestampUtil.toZonedDateTime(timestamp, db.databaseTimeZone(), ZoneId.of("UTC"));
    }

    private static <T> BiFunction<Database,Optional<T>,Object> defaultConverter() {
        return (db, v) -> v.orElse(null);
    }

    private static <T> LiteralFormatter<T> defaultLiteralFormatter() {
        return (d, v) -> v.toString();
    }

    private static <T> ParameterFormatter<T> defaultParameterFormatter() {
        return (d, v) -> "?";
    }

    @FunctionalInterface
    interface ResultSetExtractor<T> {
        T get(ResultSet rs, String col, Database database) throws SQLException;

        Map<Class<?>,ResultSetExtractor<?>> extractors = ImmutableMap.<Class<?>,ResultSetExtractor<?>>builder()
            .put(BigDecimal.class, (resultSet, columnLabel, db) -> resultSet.getBigDecimal(columnLabel))
            .put(Byte.class, (resultSet, columnLabel, db) -> resultSet.getByte(columnLabel))
            .put(byte[].class, (resultSet, columnLabel, db) -> resultSet.getBytes(columnLabel))
            .put(Date.class, (resultSet, columnLabel, db) -> resultSet.getDate(columnLabel))
            .put(Double.class, (resultSet, columnLabel, db) -> resultSet.getDouble(columnLabel))
            .put(Float.class, (resultSet, columnLabel, db) -> resultSet.getFloat(columnLabel))
            .put(Integer.class, (resultSet, columnLabel, db) -> resultSet.getInt(columnLabel))
            .put(Long.class, (resultSet, columnLabel, db) -> resultSet.getLong(columnLabel))
            .put(Short.class, (resultSet, columnLabel, db) -> resultSet.getShort(columnLabel))
            .put(String.class, (resultSet, columnLabel, db) -> resultSet.getString(columnLabel))
            .put(Time.class, (resultSet, columnLabel, db) -> resultSet.getTime(columnLabel))
            .put(Timestamp.class, DataType::getTimestamp)
            .build();
    }
}
