/*
 * Copyright (c) 2020 Cadenza United Kingdom Limited
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

import com.cadenzauk.core.reflect.util.ClassUtil;
import com.cadenzauk.core.sql.exception.DuplicateKeyException;
import com.cadenzauk.core.sql.exception.IllegalNullException;
import com.cadenzauk.core.sql.exception.InvalidValueException;
import com.cadenzauk.core.sql.exception.LockingException;
import com.cadenzauk.core.sql.exception.NoSuchObjectException;
import com.cadenzauk.core.sql.exception.ReferentialIntegrityException;
import com.cadenzauk.core.sql.exception.SqlSyntaxException;
import com.cadenzauk.siesta.DataType;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.IsolationLevel;
import com.cadenzauk.siesta.LockLevel;
import com.cadenzauk.siesta.dialect.function.FunctionName;
import com.cadenzauk.siesta.dialect.function.SimpleFunctionSpec;
import com.cadenzauk.siesta.dialect.function.aggregate.AggregateFunctionSpecs;
import com.cadenzauk.siesta.dialect.function.aggregate.CountDistinctFunctionSpec;
import com.cadenzauk.siesta.dialect.function.json.JsonFunctionSpecs;
import com.cadenzauk.siesta.dialect.function.string.StringFunctionSpecs;
import com.cadenzauk.siesta.dialect.merge.DerbyMergeInfo;
import com.cadenzauk.siesta.type.DbType;
import com.cadenzauk.siesta.type.DbTypeId;
import com.cadenzauk.siesta.type.DefaultDate;
import com.cadenzauk.siesta.type.DefaultTime;
import com.cadenzauk.siesta.type.DefaultTimestamp;
import com.cadenzauk.siesta.type.DefaultTinyint;
import com.cadenzauk.siesta.type.DefaultUtcTimestamp;
import com.cadenzauk.siesta.type.DefaultVarbinary;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.ADD_DAYS;
import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.DAY_DIFF;
import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.HOUR_DIFF;
import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.MINUTE_DIFF;
import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.SECOND_DIFF;
import static com.cadenzauk.siesta.dialect.function.json.JsonFunctionSpecs.JSONB_OBJECT;
import static com.cadenzauk.siesta.dialect.function.json.JsonFunctionSpecs.JSON_OBJECT;

public class DerbyDialect extends AnsiDialect {
    public DerbyDialect() {
        functions()
            .register(ADD_DAYS, (s, a) -> "cast({fn timestampadd(SQL_TSI_DAY, " + a[1] + ", " + a[0] + ")} as date)")
            .register(DAY_DIFF, (s, a) -> "{fn TIMESTAMPDIFF(SQL_TSI_DAY, " + a[1] + ", " + a[0] + ")}")
            .register(HOUR_DIFF, (s, a) -> "{fn TIMESTAMPDIFF(SQL_TSI_HOUR, " + a[1] + ", " + a[0] + ")}")
            .register(MINUTE_DIFF, (s, a) -> "{fn TIMESTAMPDIFF(SQL_TSI_MINUTE, timestamp(substr(varchar(" + a[1] + "), 1, 16)||':00'), timestamp(substr(varchar(" + a[0] + "), 1, 16)||':00'))}")
            .register(SECOND_DIFF, (s, a) -> "{fn TIMESTAMPDIFF(SQL_TSI_SECOND, timestamp(substr(varchar(" + a[1] + "), 1, 19)), timestamp(substr(varchar(" + a[0] + "), 1, 19)))}")
            .register(AggregateFunctionSpecs.COUNT_BIG, SimpleFunctionSpec.of("count"))
            .register(AggregateFunctionSpecs.COUNT_BIG_DISTINCT, CountDistinctFunctionSpec.of("count"))
            .register(StringFunctionSpecs.INSTR, (s, a) -> String.format("locate(%s, %s)", a[1], a[0]))
            .register(JSON_OBJECT, SimpleFunctionSpec.of("json_object"))
            .register(JSONB_OBJECT, SimpleFunctionSpec.of("json_object"));

        types()
            .register(DbTypeId.DATE, new DefaultDate() {
                @Override
                public String literal(Database database, LocalDate value) {
                    return String.format("DATE('%s')", value.format(DateTimeFormatter.ISO_DATE));
                }
            })
            .register(DbTypeId.TIME, new DefaultTime() {
                @Override
                public String literal(Database database, LocalTime value) {
                    return String.format("TIME('%s')", value.format(DateTimeFormatter.ISO_TIME));
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
                    return String.format("TIMESTAMP('%s')", value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")));
                }
            })
            .register(DbTypeId.UTC_TIMESTAMP, new DefaultUtcTimestamp() {
                @Override
                public String literal(Database database, ZonedDateTime value) {
                    ZonedDateTime localDateTime = value.withZoneSameInstant(database.databaseTimeZone());
                    return String.format("TIMESTAMP('%s')", localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")));
                }
            })
            .register(DbTypeId.TINYINT, new DefaultTinyint("smallint"))
            .register(DbTypeId.BINARY, new DefaultVarbinary("char") {
                @Override
                public String sqlType(Database database, int arg) {
                    return String.format("%s(%d) for bit data", sqlType(database), arg);
                }
            })
            .register(DbTypeId.VARBINARY, new DefaultVarbinary("varchar") {
                @Override
                public String sqlType(Database database, int arg) {
                    return String.format("%s(%d) for bit data", sqlType(database), arg);
                }
            });

        exceptions()
            .register("42X05", NoSuchObjectException::new)
            .register("07006", SqlSyntaxException::new)
            .register("42[X67]..", SqlSyntaxException::new)

            // -407 23502       Assignment of a NULL value to a NOT NULL column name is not allowed.
            .register("23502", IllegalNullException::new)

            // -530 23503       The insert or update value of the FOREIGN KEY constraint-name is not equal to any value of the parent key of the parent table.
            // -531 23001,23504 The parent key in a parent row of relationship constraint-name cannot be updated.
            // -532 23504       A parent row cannot be deleted because the relationship constraint-name restricts the deletion.
            // -543 23511       A row in a parent table cannot be deleted because the check constraint constraint-name restricts the deletion.
            // -544 23512       The check constraint constraint-name cannot be added because the table contains a row that violates the constraint.
            // -545 23513       The requested operation is not allowed because a row does not satisfy the check constraint constraint-name.
            .register("23001", ReferentialIntegrityException::new)
            .register("2350[34]", ReferentialIntegrityException::new)
            .register("2351[123]", ReferentialIntegrityException::new)

            // -803 23505       One or more values in the INSERT statement, UPDATE statement, or foreign key update caused by a DELETE statement are not valid
            //                  because the primary key, unique constraint or unique index identified by index-id constrains table table-name from having
            //                  duplicate values for the index key.
            // -603 23515       A unique index cannot be created because the table contains data that would result in duplicate index entries.
            .register("235[01]5", DuplicateKeyException::new)

            // -171 5UA0J,5UA05,5UA06,5UA07,5UA08,5UA09,2201G,2201T,2201V,10608,22003,22014,22016,22546,42815
            //                  The statement was not processed because the data type, length or value of the argument for the parameter in position n of routine
            //                  routine-name is incorrect. Parameter name: parameter-name.
            // -302 22001/22003 The value of a host variable in the EXECUTE or OPEN statement is out of range for its corresponding use.
            .register("5UA0[J56789]", InvalidValueException::new)
            .register("2201[TV46]", InvalidValueException::new)
            .register("10608", InvalidValueException::new)
            .register("2200[13]", InvalidValueException::new)
            .register("22546", InvalidValueException::new)
            .register("42815", InvalidValueException::new)

            .register("40001", LockingException::new)
            .register("57011", LockingException::new)
            .register("57033", LockingException::new)
        ;
        setSequenceInfo(new DerbySequenceInfo());
        setTempTableInfo(new DerbyTempTableInfo());
        setMergeInfo(new DerbyMergeInfo(this));
    }

    @Override
    public String dual() {
        return "SYSIBM.SYSDUMMY1";
    }

    @Override
    public String nextFromSequence(String catalog, String schema, String sequenceName) {
        return "next value for " + qualifiedSequenceName(catalog, schema, sequenceName);
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

    @Override
    public boolean supportsMultipleValueIn() {
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


    @Override
    public String createJavaProcSql(Database database, Class<?> procClass, String methodName, String functionName) {
        Method method = ClassUtil.declaredMethods(procClass)
            .filter(it -> StringUtils.equals(it.getName(), methodName))
            .limit(1)
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
        String returnType = typeString(database, method.getReturnType());
        String parameters = Arrays.stream(method.getParameters())
            .map(p -> {
                String typeString = "Object";//typeString(database, p.getType());
                return String.format("%s %s", p.getName(), typeString);
            })
            .collect(Collectors.joining(", "));
        return String.format("create function siesta.%s(%s) returns %s language java parameter style java no sql external name '%s.%s'", functionName, parameters, returnType, procClass.getCanonicalName(), methodName);
    }

    private String typeString(Database database, Class<?> parameterType) {
        DataType<?> dataType = database.getDataTypeOf(parameterType);
        DbType<?> dbType = types().get(dataType.dbTypeId());
        return dbType.sqlType(database, 32000);
    }

    @Override
    public Stream<FunctionName> missingJsonFunctions() {
        return Stream.of(
            JsonFunctionSpecs.JSON_OBJECT,
            JsonFunctionSpecs.JSONB_OBJECT,
            JsonFunctionSpecs.JSON_VALUE,
            JsonFunctionSpecs.JSONB_VALUE
        );
    }}
