/*
 * Copyright (c) 2023 Cadenza United Kingdom Limited
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

import com.cadenzauk.core.sql.QualifiedName;
import com.cadenzauk.core.sql.exception.DuplicateKeyException;
import com.cadenzauk.core.sql.exception.IllegalNullException;
import com.cadenzauk.core.sql.exception.InvalidValueException;
import com.cadenzauk.core.sql.exception.LockingException;
import com.cadenzauk.core.sql.exception.NoSuchObjectException;
import com.cadenzauk.core.sql.exception.ReferentialIntegrityException;
import com.cadenzauk.core.sql.exception.SqlSyntaxException;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.SequenceInfo;
import com.cadenzauk.siesta.dialect.function.SimpleFunctionSpec;
import com.cadenzauk.siesta.dialect.function.aggregate.AggregateFunctionSpecs;
import com.cadenzauk.siesta.dialect.function.aggregate.CountDistinctFunctionSpec;
import com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs;
import com.cadenzauk.siesta.dialect.merge.MySqlMergeInfo;
import com.cadenzauk.siesta.type.DbTypeId;
import com.cadenzauk.siesta.type.DefaultBigint;
import com.cadenzauk.siesta.type.DefaultInteger;
import com.cadenzauk.siesta.type.DefaultSmallint;
import com.cadenzauk.siesta.type.DefaultTimestamp;
import com.cadenzauk.siesta.type.DefaultTinyint;
import com.cadenzauk.siesta.type.DefaultUtcTimestamp;
import com.cadenzauk.siesta.type.DefaultVarchar;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import static com.cadenzauk.siesta.dialect.function.aggregate.AggregateFunctionSpecs.COUNT_BIG;
import static com.cadenzauk.siesta.dialect.function.aggregate.AggregateFunctionSpecs.COUNT_BIG_DISTINCT;
import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.ADD_DAYS;
import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.DAY_DIFF;
import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.HOUR_DIFF;
import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.MINUTE_DIFF;
import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.SECOND_DIFF;
import static java.util.stream.Collectors.joining;

public class MySqlDialect extends AnsiDialect {
    public MySqlDialect() {
        functions()
            .register(COUNT_BIG, SimpleFunctionSpec.of("count"))
            .register(COUNT_BIG_DISTINCT, CountDistinctFunctionSpec.of("count"))
            .register(ADD_DAYS, (s, a) -> "date_add(" + a[0] + ", interval " + a[1] + " day)")
            .register(DAY_DIFF, (s, a) -> "datediff(" + a[0] + ", " + a[1] + ")")
            .register(HOUR_DIFF, (s, a) -> "timestampdiff(hour, " + a[1] + ", " + a[0] + ")")
            .register(MINUTE_DIFF, (s, a) -> "(truncate(unix_timestamp(" + a[0] + "), 0) div 60 - truncate(unix_timestamp(" + a[1] + "), 0) div 60)")
            .register(SECOND_DIFF, (s, a) -> "(truncate(unix_timestamp(" + a[0] + "), 0) - truncate(unix_timestamp(" + a[1] + "), 0))")
        ;

        types()
            .register(DbTypeId.TINYINT, new DefaultTinyint() {
                @Override
                public String castType(Database database) {
                    return "signed";
                }
            })
            .register(DbTypeId.SMALLINT, new DefaultSmallint() {
                @Override
                public String castType(Database database) {
                    return "signed";
                }
            })
            .register(DbTypeId.INTEGER, new DefaultInteger() {
                @Override
                public String castType(Database database) {
                    return "signed";
                }
            })
            .register(DbTypeId.BIGINT, new DefaultBigint() {
                @Override
                public String castType(Database database) {
                    return "signed";
                }
            })
            .register(DbTypeId.VARCHAR, new DefaultVarchar() {
                @Override
                public String castType(Database database, int arg) {
                    return "char";
                }

                @Override
                public String parameter(Database database, Optional<String> value) {
                    return "cast(? as char)";
                }

                @Override
                public String castParameter(Database database, Optional<String> value) {
                    return "cast(? as char)";
                }
            })
            .register(DbTypeId.TIMESTAMP, new DefaultTimestamp() {
                @Override
                public String castType(Database database) {
                    return "datetime";
                }

                @Override
                public String castType(Database database, int arg) {
                    return "datetime";
                }

                @Override
                public String parameter(Database database, Optional<LocalDateTime> value) {
                    return "cast(? as datetime)";
                }

                @Override
                public String castParameter(Database database, Optional<LocalDateTime> value) {
                    return "cast(? as datetime)";
                }
            })
            .register(DbTypeId.UTC_TIMESTAMP, new DefaultUtcTimestamp() {
                @Override
                public String castType(Database database) {
                    return "datetime";
                }

                @Override
                public String castType(Database database, int arg) {
                    return "datetime";
                }

                @Override
                public String parameter(Database database, Optional<ZonedDateTime> value) {
                    return "cast(? as datetime)";
                }

                @Override
                public String castParameter(Database database, Optional<ZonedDateTime> value) {
                    return "cast(? as datetime)";
                }
            })
        ;

        exceptions()
            .register("42S02", NoSuchObjectException::new)
            .register(1048, IllegalNullException::new)
            .register(1062, DuplicateKeyException::new)
            .register(1064, SqlSyntaxException::new)
            .register(1146, NoSuchObjectException::new)
            .register(1213, LockingException::new)
            .register(1451, ReferentialIntegrityException::new)
            .register(1452, ReferentialIntegrityException::new)
            .register(1264, InvalidValueException::new)
            .register(1406, InvalidValueException::new)
            .register("42.+", SqlSyntaxException::new)
            .register("22.+", InvalidValueException::new)
            .register("23502", IllegalNullException::new)
            .register("23000", ReferentialIntegrityException::new)
            .register("40P01", LockingException::new)
            ;

        setSequenceInfo(SequenceInfo.newBuilder()
            .supportsSequences(false)
            .build());
        setTempTableInfo(new MySqlTempTableInfo());
        setMergeInfo(new MySqlMergeInfo(this));
    }

    @Override
    public String qualifiedIndexName(String catalog, String schema, String name) {
        return name;
    }

    @Override
    public String concat(Stream<String> sql) {
        return "concat(" + sql.collect(joining(", ")) + ")";
    }

    @Override
    public String fetchFirst(String sql, long n) {
        return String.format("%s limit %d", sql, n);
    }

    @Override
    public QualifiedName fixQualifiedName(QualifiedName qualifiedName) {
        if (qualifiedName.schema().isPresent()) {
            return qualifiedName;
        }
        return new QualifiedName(null, qualifiedName.catalog().orElse(""), qualifiedName.name().orElse(""));
    }
}
