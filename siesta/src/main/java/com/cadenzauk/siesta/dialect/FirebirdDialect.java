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

import com.cadenzauk.core.sql.exception.NoSuchObjectException;
import com.cadenzauk.core.sql.exception.ReferentialIntegrityException;
import com.cadenzauk.core.sql.exception.LockingException;
import com.cadenzauk.core.sql.exception.IllegalNullException;
import com.cadenzauk.core.sql.exception.SqlSyntaxException;
import com.cadenzauk.core.sql.exception.DuplicateKeyException;
import com.cadenzauk.core.sql.exception.InvalidValueException;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.dialect.function.SimpleFunctionSpec;
import com.cadenzauk.siesta.dialect.function.aggregate.AggregateFunctionSpecs;
import com.cadenzauk.siesta.dialect.function.aggregate.CountDistinctFunctionSpec;
import com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs;
import com.cadenzauk.siesta.dialect.function.string.StringFunctionSpecs;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.type.DbTypeId;
import com.cadenzauk.siesta.type.DefaultInteger;
import com.cadenzauk.siesta.type.DefaultTimestamp;
import com.cadenzauk.siesta.type.DefaultTinyint;
import com.cadenzauk.siesta.type.DefaultUtcTimestamp;
import com.cadenzauk.siesta.type.DefaultVarbinary;
import com.cadenzauk.siesta.type.DefaultVarchar;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public class FirebirdDialect extends AnsiDialect {
    public FirebirdDialect() {
        functions()
            .register(DateFunctionSpecs::registerExtract)
            .register(DateFunctionSpecs::registerDateAdd)
            .register(AggregateFunctionSpecs.COUNT_BIG, SimpleFunctionSpec.of("count"))
            .register(AggregateFunctionSpecs.COUNT_BIG_DISTINCT, CountDistinctFunctionSpec.of("count"))
            .register(StringFunctionSpecs.INSTR, new SimpleFunctionSpec("position") {
                @Override
                public String sql(Scope scope, String[] args) {
                    return super.sql(scope, args[1], args[0]);
                }

                @Override
                public Stream<Object> args(Scope scope, TypedExpression<?>[] args) {
                    return super.args(scope, args[1], args[0]);
                }
            });

        types()
            .register(DbTypeId.BINARY, new DefaultVarbinary("char") {
                @Override
                public String sqlType(Database database, int arg1) {
                    return super.sqlType(database, arg1) + " character set octets";
                }
            })
            .register(DbTypeId.TINYINT, new DefaultTinyint("smallint"))
            .register(DbTypeId.INTEGER, new DefaultInteger() {
                @Override
                public String parameter(Database database, Integer value) {
                    return "cast(? as integer)";
                }
            })
            .register(DbTypeId.TIMESTAMP, new DefaultTimestamp() {
                @Override
                public String sqlType(Database database, int arg) {
                    return sqlType(database);
                }

                @Override
                public String sqlType(Database database, int arg1, int arg2) {
                    return sqlType(database);
                }

                @Override
                public String literal(Database database, LocalDateTime value) {
                    return String.format("TIMESTAMP '%s'", value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS")));
                }
            })
            .register(DbTypeId.UTC_TIMESTAMP, new DefaultUtcTimestamp() {
                @Override
                public String sqlType(Database database, int arg) {
                    return sqlType(database);
                }

                @Override
                public String sqlType(Database database, int arg1, int arg2) {
                    return sqlType(database);
                }

                @Override
                public String literal(Database database, ZonedDateTime value) {
                    ZonedDateTime localDateTime = value.withZoneSameInstant(database.databaseTimeZone());
                    return String.format("TIMESTAMP '%s'", localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS")));
                }
            })
            .register(DbTypeId.VARCHAR, new DefaultVarchar() {
                @Override
                public String parameter(Database database, String value) {
                    return String.format("cast(? as varchar(%d))", Integer.max(1, value.length()));
                }
            });

        exceptions()
            .register("07006", InvalidValueException::new)
            .register("22001", InvalidValueException::new)
            .register("23000", 335544347, IllegalNullException::new)
            .register("23000", 335544665, DuplicateKeyException::new)
            .register("23000", 335544466, ReferentialIntegrityException::new)
            .register("40001", 335544336, LockingException::new)
            .register("42S02", 335544580, NoSuchObjectException::new)
            .register("42.+", SqlSyntaxException::new)
        ;

        setSequenceInfo(new FirebirdSequenceInfo());
        setTempTableInfo(new FirebirdTempTableInfo());
    }

    @Override
    public String dual() {
        return "RDB$DATABASE";
    }

    @Override
    public String fetchFirst(String sql, long n) {
        return String.format("%s rows %d", sql, n);
    }

    @Override
    public String nextFromSequence(String catalog, String schema, String sequenceName) {
        return "next value for " + qualifiedSequenceName(catalog, schema, sequenceName);
    }

    @Override
    public String qualifiedSequenceName(String catalog, String schema, String name) {
        return name;
    }

    @Override
    public String qualifiedIndexName(String catalog, String schema, String name) {
        return name;
    }

    @Override
    public String qualifiedTableName(String catalog, String schema, String name) {
        return name;
    }

    @Override
    public boolean supportsMultipleValueIn() {
        return false;
    }
}
