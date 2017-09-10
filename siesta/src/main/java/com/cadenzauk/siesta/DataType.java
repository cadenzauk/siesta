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

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.TimeZone;
import java.util.function.BiFunction;

public class DataType<T> {
    public static final DataType<BigDecimal> BIG_DECIMAL = new DataType<>(BigDecimal.class,
        ResultSetExtractor.of(ResultSet::getBigDecimal, ResultSet::getBigDecimal),
        defaultConverter(),
        defaultLiteralFormatter(),
        defaultParameterFormatter());
    public static final DataType<Byte> BYTE = new DataType<>(Byte.class,
        ResultSetExtractor.of(ResultSet::getByte, ResultSet::getByte),
        defaultConverter(),
        Dialect::byteLiteral,
        defaultParameterFormatter());
    public static final DataType<byte[]> BYTE_ARRAY = new DataType<>(byte[].class,
        ResultSetExtractor.of(ResultSet::getBytes, ResultSet::getBytes),
        defaultConverter(),
        Dialect::binaryLiteral,
        defaultParameterFormatter());
    public static final DataType<Double> DOUBLE = new DataType<>(Double.class,
        ResultSetExtractor.of(ResultSet::getDouble, ResultSet::getDouble),
        defaultConverter(),
        defaultLiteralFormatter(),
        defaultParameterFormatter());
    public static final DataType<Float> FLOAT = new DataType<>(Float.class,
        ResultSetExtractor.of(ResultSet::getFloat, ResultSet::getFloat),
        defaultConverter(),
        Dialect::floatLiteral,
        defaultParameterFormatter());
    public static final DataType<Integer> INTEGER = new DataType<>(Integer.class,
        ResultSetExtractor.of(ResultSet::getInt, ResultSet::getInt),
        defaultConverter(),
        defaultLiteralFormatter(),
        Dialect::integerParameter);
    public static final DataType<LocalDate> LOCAL_DATE = new DataType<>(LocalDate.class,
        ResultSetExtractor.of(DataType::getLocalDate, DataType::getLocalDate),
        DataType::localDateToDb,
        Database::dateLiteral,
        Dialect::dateParameter);
    public static final DataType<LocalDateTime> LOCAL_DATE_TIME = new DataType<>(LocalDateTime.class,
        ResultSetExtractor.of(DataType::getLocalDateTime, DataType::getLocalDateTime),
        DataType::localDateTimeToDb,
        Database::timestampLiteral,
        Dialect::timestampParameter);
    public static final DataType<Long> LONG = new DataType<>(Long.class,
        ResultSetExtractor.of(ResultSet::getLong, ResultSet::getLong),
        defaultConverter(),
        defaultLiteralFormatter(),
        defaultParameterFormatter());
    public static final DataType<Short> SHORT = new DataType<>(Short.class,
        ResultSetExtractor.of(ResultSet::getShort, ResultSet::getShort),
        defaultConverter(),
        Dialect::smallIntLiteral,
        defaultParameterFormatter());
    public static final DataType<String> STRING = new DataType<>(String.class,
        ResultSetExtractor.of(ResultSet::getString, ResultSet::getString),
        defaultConverter(),
        Dialect::stringLiteral,
        defaultParameterFormatter());
    public static final DataType<ZonedDateTime> ZONED_DATE_TIME = new DataType<>(ZonedDateTime.class,
        DataType.getZonedDateTimeExtractor(),
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

    public Optional<T> get(ResultSet rs, int colNo, Database database) {
        try {
            T value = resultSetExtractor.get(rs, colNo, database);
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

    private static LocalDate getLocalDate(ResultSet rs, String col) throws SQLException {
        Date date = rs.getDate(col, new GregorianCalendar(TimeZone.getDefault()));
        return SqlDateUtil.toLocalDate(date);
    }

    private static LocalDate getLocalDate(ResultSet rs, int col) throws SQLException {
        Date date = rs.getDate(col, new GregorianCalendar(TimeZone.getDefault()));
        return SqlDateUtil.toLocalDate(date);
    }

    private static LocalDateTime getLocalDateTime(ResultSet rs, String col) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(col, new GregorianCalendar(TimeZone.getDefault()));
        return timestamp.toLocalDateTime();
    }

    private static LocalDateTime getLocalDateTime(ResultSet rs, int col) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(col, new GregorianCalendar(TimeZone.getDefault()));
        return timestamp.toLocalDateTime();
    }

    private static Timestamp getTimestamp(ResultSet resultSet, String columnLabel, Database db) throws SQLException {
        return resultSet.getTimestamp(columnLabel, new GregorianCalendar(TimeZone.getTimeZone(db.databaseTimeZone())));
    }

    private static Timestamp getTimestamp(ResultSet resultSet, int columnNo, Database db) throws SQLException {
        return resultSet.getTimestamp(columnNo, new GregorianCalendar(TimeZone.getTimeZone(db.databaseTimeZone())));
    }

    private static ResultSetExtractor<ZonedDateTime> getZonedDateTimeExtractor() {
        return new ResultSetExtractor<ZonedDateTime>() {
            @Override
            public ZonedDateTime get(ResultSet rs, String col, Database db) throws SQLException {
                Timestamp timestamp = getTimestamp(rs, col, db);
                return TimestampUtil.toZonedDateTime(timestamp, db.databaseTimeZone(), ZoneId.of("UTC"));
            }

            @Override
            public ZonedDateTime get(ResultSet rs, int col, Database db) throws SQLException {
                Timestamp timestamp = getTimestamp(rs, col, db);
                return TimestampUtil.toZonedDateTime(timestamp, db.databaseTimeZone(), ZoneId.of("UTC"));
            }
        };
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
    interface SqlBiFunction<T1,T2,R> {
        R apply(T1 arg1, T2 arg2) throws SQLException;
    }

    interface ResultSetExtractor<T> {
        T get(ResultSet rs, String col, Database database) throws SQLException;
        T get(ResultSet rs, int col, Database database) throws SQLException;

        static <T> ResultSetExtractor<T> of(SqlBiFunction<ResultSet,String,T> byLabel, SqlBiFunction<ResultSet,Integer,T> byColNo) {
            return new ResultSetExtractor<T>() {
                @Override
                public T get(ResultSet rs, String col, Database database) throws SQLException {
                    return byLabel.apply(rs, col);
                }

                @Override
                public T get(ResultSet rs, int col, Database database) throws SQLException {
                    return byColNo.apply(rs, col);
                }
            };
        }
    }
}
