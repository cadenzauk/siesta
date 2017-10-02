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

import com.cadenzauk.siesta.IsolationLevel;
import com.cadenzauk.siesta.LockLevel;
import com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class H2Dialect extends AnsiDialect {
    public H2Dialect() {
        DateFunctionSpecs.registerDateAdd(functions());
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
        return "set lock_timeout 1000";
    }

}
