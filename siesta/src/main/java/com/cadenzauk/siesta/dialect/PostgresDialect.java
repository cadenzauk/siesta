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

import com.cadenzauk.core.sql.exception.ReferentialIntegrityException;
import com.cadenzauk.core.sql.exception.LockingException;
import com.cadenzauk.core.sql.exception.IllegalNullException;
import com.cadenzauk.core.sql.exception.SqlSyntaxException;
import com.cadenzauk.core.sql.exception.DuplicateKeyException;
import com.cadenzauk.core.sql.exception.InvalidValueException;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.dialect.function.ArgumentlessFunctionSpec;
import com.cadenzauk.siesta.dialect.function.FunctionSpec;
import com.cadenzauk.siesta.dialect.function.SimpleFunctionSpec;
import com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.type.DbTypeId;
import com.cadenzauk.siesta.type.DefaultTinyint;
import com.cadenzauk.siesta.type.DefaultVarbinary;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static com.cadenzauk.core.lang.StringUtil.octal;
import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.HOUR_DIFF;
import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.MINUTE_DIFF;
import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.SECOND_DIFF;
import static com.cadenzauk.siesta.dialect.function.string.StringFunctionSpecs.INSTR;

public class PostgresDialect extends AnsiDialect {
    public PostgresDialect() {
        DateFunctionSpecs.registerExtract(functions());
        DateFunctionSpecs.registerPlusNumber(functions());
        functions()
            .register(DateFunctionSpecs.CURRENT_TIMESTAMP_UTC, ArgumentlessFunctionSpec.of("localtimestamp"))
            .register(HOUR_DIFF, new FunctionSpec() {
                @Override
                public String sql(Scope scope, String[] argsSql) {
                    return String.format("date_part('day', %1$s - %2$s) * 24 + date_part('hour', %1$s - %2$s)", argsSql[0], argsSql[1]);
                }

                @Override
                public Stream<Object> args(Scope scope, TypedExpression<?>[] args) {
                    return Stream.concat(
                        Arrays.stream(args),
                        Arrays.stream(args)).flatMap(a -> a.args(scope));
                }
            })
            .register(MINUTE_DIFF, (s, argsSql) -> String.format("extract(epoch from (date_trunc('minute', %1$s) - date_trunc('minute', %2$s))) / 60", argsSql[0], argsSql[1]))
            .register(SECOND_DIFF, (s, argsSql) -> String.format("extract(epoch from (%1$s - %2$s))", argsSql[0], argsSql[1]))
            .register(INSTR, SimpleFunctionSpec.of("strpos"))
        ;

        types()
            .register(DbTypeId.TINYINT, new DefaultTinyint("smallint"))
            .register(DbTypeId.VARBINARY, new DefaultVarbinary() {
                @Override
                public String literal(Database database, byte[] value) {
                    StringBuilder builder = new StringBuilder("E'");
                    for (byte b : value) {
                        builder.append("\\\\");
                        builder.append(octal(b));
                    }
                    builder.append("'::bytea");
                    return builder.toString();
                }
            });

        exceptions()
            .register("22001", InvalidValueException::new)
            .register("22003", InvalidValueException::new)
            .register("23502", IllegalNullException::new)
            .register("23503", ReferentialIntegrityException::new)
            .register("23505", DuplicateKeyException::new)
            .register("40P01", LockingException::new)
            .register("42.+", SqlSyntaxException::new)
            .register("55P03", LockingException::new)
        ;
    }

    @Override
    public boolean supportsMultiInsert() {
        return true;
    }

    @Override
    public boolean requiresFromDual() {
        return false;
    }

    @Override
    public String fetchFirst(String sql, long n) {
        return String.format("%s offset 0 rows fetch next %d rows only", sql, n);
    }

    @Override
    public boolean supportsLockTimeout() {
        return true;
    }

    @Override
    public String setLockTimeout(long time, TimeUnit unit) {
        long millis = unit.toMillis(time);
        if (millis == 0) {
            millis = 1;
        }
        return String.format("SET LOCAL lock_timeout = '%dms'", millis);
    }

    @Override
    public String resetLockTimeout() {
        return "SET LOCAL lock_timeout = '10s'";
    }

    @Override
    public String nextFromSequence(String catalog, String schema, String sequenceName) {
        return "nextval('" + sequenceName + "')";
    }
}
