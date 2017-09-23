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

import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.IsolationLevel;
import com.cadenzauk.siesta.LockLevel;
import com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs;
import com.cadenzauk.siesta.type.DefaultBinaryTypeAdapter;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static com.cadenzauk.core.lang.StringUtil.hex;
import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.HOUR_DIFF;

public class Db2Dialect extends AnsiDialect {
    public Db2Dialect() {
        DateFunctionSpecs.registerPlusUnits(functions());
        functions().register(HOUR_DIFF, a -> "TIMESTAMPDIFF(8, char(" + a[0] + " - " + a[1] + "))");

        types()
            .register(byte[].class, new DefaultBinaryTypeAdapter() {

                @Override
                public String literal(Database database, byte[] value) {
                    return String.format("HEXTORAW('%s')", hex(value));
                }
            });
    }

    @Override
    public String selectivity(double s) {
        return String.format(" selectivity %f", s);
    }

    @Override
    public String dual() {
        return "SYSIBM.SYSDUMMY1";
    }

    @Override
    public boolean supportsMultiInsert() {
        return true;
    }

    @Override
    public String fetchFirst(String sql, long n) {
        return String.format("%s fetch first %d rows only", sql, n);
    }

    @Override
    public boolean supportsIsolationLevelInQuery() {
        return true;
    }

    @Override
    public String isolationLevelSql(String sql, IsolationLevel level, Optional<LockLevel> keepLocks) {
        return keepLocks
            .map(kl -> isolationLevelSqlWithLocks(sql, level, kl))
            .orElseGet(() -> isolationLevelWithNoLocks(sql, level));
    }

    private String isolationLevelSqlWithLocks(String sql, IsolationLevel level, LockLevel keepLocks) {
        return String.format("%s for read only with %s use and keep %s locks",
            sql,
            level.ordinal() <= IsolationLevel.REPEATABLE_READ.ordinal() ? "rs" : "rr",
            keepLocks);
    }

    @Nullable
    private String isolationLevelWithNoLocks(String sql, IsolationLevel level) {
        switch (level) {
            case UNSPECIFIED:
                return sql;
            case UNCOMMITTED_READ:
                return sql + " with ur";
            case READ_COMMITTED:
                return sql + " with cs";
            case REPEATABLE_READ:
                return sql + " with rs";
            case SERIALIZABLE:
                return sql + " with rr";
        }
        return sql;
    }
}
