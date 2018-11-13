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

package com.cadenzauk.siesta.dialect;

import com.cadenzauk.core.sql.exception.InvalidValueException;
import com.cadenzauk.core.sql.exception.ReferentialIntegrityException;
import com.cadenzauk.core.sql.exception.LockingException;
import com.cadenzauk.core.sql.exception.IllegalNullException;
import com.cadenzauk.core.sql.exception.SqlSyntaxException;
import com.cadenzauk.core.sql.exception.DuplicateKeyException;
import com.cadenzauk.siesta.IsolationLevel;
import com.cadenzauk.siesta.LockLevel;
import com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class H2Dialect extends AnsiDialect {
    private final int defaultLockTimeout;

    public H2Dialect() {
        this(1000);
    }

    public H2Dialect(int defaultLockTimeout) {
        this.defaultLockTimeout = defaultLockTimeout;

        DateFunctionSpecs.registerDateAdd(functions());

        exceptions()
            .register("42.+", SqlSyntaxException::new)

            // DUPLICATE_KEY_1 = 23505
            .register("23505", DuplicateKeyException::new)

            // VALUE_TOO_LONG_2 = 22001
            // NUMERIC_VALUE_OUT_OF_RANGE_1 = 22003
            // INVALID_DATETIME_CONSTANT_2 = 22007
            // DATA_CONVERSION_ERROR_1 = 22018
            .register("2200[137]", InvalidValueException::new)
            .register("22018", InvalidValueException::new)

            // NULL_NOT_ALLOWED = 23502
            .register("23502", IllegalNullException::new)

            // REFERENTIAL_INTEGRITY_VIOLATED_CHILD_EXISTS_1 = 23503
            // REFERENTIAL_INTEGRITY_VIOLATED_PARENT_MISSING_1 = 23506
            // NO_DEFAULT_SET_1 = 23507
            // CHECK_CONSTRAINT_VIOLATED_1 = 23513
            .register("2350[367]", ReferentialIntegrityException::new)
            .register("23513", ReferentialIntegrityException::new)

            // LOCK_TIMEOUT_1 = 50200
            .register("HYT00", 50200, LockingException::new);
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
        return true;
    }

    @Override
    public String setLockTimeout(long time, TimeUnit unit) {
        return String.format("set lock_timeout %d", unit.toMillis(time));
    }

    @Override
    public String resetLockTimeout() {
        return "set lock_timeout " + defaultLockTimeout;
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
    public boolean supportsMultipleValueIn() {
        return false;
    }
}
