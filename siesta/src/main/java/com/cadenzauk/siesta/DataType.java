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
import com.cadenzauk.core.sql.RuntimeSqlException;
import com.cadenzauk.siesta.type.DbType;
import com.cadenzauk.siesta.type.DbTypeId;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

public class DataType<T> {
    public static final DataType<BigDecimal> BIG_DECIMAL = new DataType<>(BigDecimal.class, DbTypeId.DECIMAL);
    public static final DataType<Byte> BYTE = new DataType<>(Byte.class, DbTypeId.TINYINT);
    public static final DataType<byte[]> BYTE_ARRAY = new DataType<>(byte[].class, DbTypeId.VARBINARY);
    public static final DataType<Double> DOUBLE = new DataType<>(Double.class, DbTypeId.DOUBLE);
    public static final DataType<Float> FLOAT = new DataType<>(Float.class, DbTypeId.REAL);
    public static final DataType<Integer> INTEGER = new DataType<>(Integer.class, DbTypeId.INTEGER);
    public static final DataType<LocalDate> LOCAL_DATE = new DataType<>(LocalDate.class, DbTypeId.DATE);
    public static final DataType<LocalDateTime> LOCAL_DATE_TIME = new DataType<>(LocalDateTime.class, DbTypeId.TIMESTAMP);
    public static final DataType<LocalTime> LOCAL_TIME = new DataType<>(LocalTime.class, DbTypeId.TIME);
    public static final DataType<Long> LONG = new DataType<>(Long.class, DbTypeId.BIGINT);
    public static final DataType<Short> SHORT = new DataType<>(Short.class, DbTypeId.SMALLINT);
    public static final DataType<String> STRING = new DataType<>(String.class, DbTypeId.VARCHAR);
    public static final DataType<UUID> UUID = new DataType<>(UUID.class, DbTypeId.UUID);
    public static final DataType<ZonedDateTime> ZONED_DATE_TIME = new DataType<>(ZonedDateTime.class, DbTypeId.UTC_TIMESTAMP);

    private final Class<T> javaClass;
    private final DbTypeId<T> dbTypeId;

    DataType(Class<T> javaClass, DbTypeId<T> dbTypeId) {
        this.javaClass = TypeUtil.boxedType(javaClass);
        this.dbTypeId = dbTypeId;
    }

    public Class<T> javaClass() {
        return javaClass;
    }

    public DbTypeId<T> dbTypeId() {
        return dbTypeId;
    }

    public Object toDatabase(Database database, Optional<T> value) {
        return value
            .map(v -> dbType(database).convertToDatabase(database, v))
            .orElse(null);
    }

    public Object toDatabase(Database database, T v) {
        return toDatabase(database, Optional.ofNullable(v));
    }

    public Optional<T> get(ResultSet rs, String colName, Database database) {
        try {
            T value = dbType(database).getColumnValue(database, rs, colName);
            return value == null || rs.wasNull() ? Optional.empty() : Optional.of(value);
        } catch (SQLException e) {
            throw new RuntimeSqlException(e);
        }
    }

    public Optional<T> get(ResultSet rs, int colNo, Database database) {
        try {
            T value = dbType(database).getColumnValue(database, rs, colNo);
            return value == null || rs.wasNull() ? Optional.empty() : Optional.of(value);
        } catch (SQLException e) {
            throw new RuntimeSqlException(e);
        }
    }

    public String literal(Database database, T value) {
        return dbType(database).literal(database, value);
    }

    public String sqlType(Database database, T value) {
        return dbType(database).parameter(database, value);
    }

    private DbType<T> dbType(Database database) {
        return database.dialect().type(dbTypeId);
    }
}
