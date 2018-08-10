/*
 * Copyright (c) 2018 Cadenza United Kingdom Limited
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

package com.cadenzauk.siesta.dialect;

import com.cadenzauk.core.sql.exception.DuplicateKeyException;
import com.cadenzauk.core.sql.exception.IllegalNullException;
import com.cadenzauk.core.sql.exception.InvalidValueException;
import com.cadenzauk.core.sql.exception.LockingException;
import com.cadenzauk.core.sql.exception.ReferentialIntegrityException;
import com.cadenzauk.core.sql.exception.SqlSyntaxException;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.IsolationLevel;
import com.cadenzauk.siesta.LockLevel;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs;
import com.cadenzauk.siesta.type.DbTypeId;
import com.cadenzauk.siesta.type.DefaultTimestamp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Optional;

public class HSqlDialect extends AnsiDialect {
    private static final DateTimeFormatter OFFSET_FORMATTER = new DateTimeFormatterBuilder()
        .appendOffset("+HH:MM", "+00:00")
        .toFormatter();

    public HSqlDialect() {
        DateFunctionSpecs.registerDateAdd(functions());
        functions()
            .register(DateFunctionSpecs.CURRENT_TIMESTAMP_UTC, (scope, argsSql) -> "current_timestamp at time zone interval '00:00' hour to minute")
            .register(DateFunctionSpecs.CURRENT_TIMESTAMP, (scope, argsSql) -> currentTimestamp(scope))
            .register(DateFunctionSpecs.CURRENT_DATE, (scope, argsSql) -> String.format("cast(%s as date)", currentTimestamp(scope)));

        types()
            .register(DbTypeId.TIMESTAMP, new DefaultTimestamp() {
                @Override
                public LocalDateTime getColumnValue(Database database, ResultSet rs, String col) throws SQLException {
                    Timestamp ts = rs.getTimestamp(col);
                    return rs.wasNull() ? null : ts.toLocalDateTime();
                }
            });

        exceptions()
            .register("2200[13]", InvalidValueException::new)
            .register("22018", InvalidValueException::new)

            .register("23502", IllegalNullException::new)
            .register("2350[34]", ReferentialIntegrityException::new)
            .register("23505", DuplicateKeyException::new)

            .register("40001", LockingException::new)

            .register("42.+", SqlSyntaxException::new);
    }

    @Override
    public String dual() {
        return "(VALUES(0))";
    }

    @Override
    public boolean requiresFromDual() {
        return true;
    }

    @Override
    public boolean supportsMultiInsert() {
        return true;
    }

    @Override
    public String fetchFirst(String sql, long n) {
        return String.format("%s limit %d", sql, n);
    }

    @Override
    public String isolationLevelSql(String sql, IsolationLevel level, Optional<LockLevel> keepLocks) {
        return keepLocks
            .filter(ll -> ll.ordinal() >= LockLevel.UPDATE.ordinal())
            .map(ll -> sql + " for update")
            .orElse(sql);
    }

    @Override
    public boolean supportsLockTimeout() {
        return false;
    }

    @Override
    public boolean supportsPartitionByInOlap() {
        return false;
    }

    @Override
    public boolean supportsOrderByInOlap() {
        return false;
    }

    @Override
    public String nextFromSequence(String catalog, String schema, String sequenceName) {
        return String.format("next value for %s.%s", schema, sequenceName);
    }

    private static String currentTimestamp(Scope scope) {
        String offset = ZonedDateTime.now(scope.database().databaseTimeZone()).format(OFFSET_FORMATTER);
        return String.format("current_timestamp at time zone interval '%s' hour to minute", offset);
    }
}
