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

import com.cadenzauk.core.sql.exception.DuplicateKeyException;
import com.cadenzauk.core.sql.exception.IllegalNullException;
import com.cadenzauk.core.sql.exception.InvalidValueException;
import com.cadenzauk.core.sql.exception.LockingException;
import com.cadenzauk.core.sql.exception.NoSuchObjectException;
import com.cadenzauk.core.sql.exception.ReferentialIntegrityException;
import com.cadenzauk.core.sql.exception.SqlSyntaxException;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.IsolationLevel;
import com.cadenzauk.siesta.LockLevel;
import com.cadenzauk.siesta.dialect.function.SimpleFunctionSpec;
import com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs;
import com.cadenzauk.siesta.json.BinaryJson;
import com.cadenzauk.siesta.json.Json;
import com.cadenzauk.siesta.type.DbTypeId;
import com.cadenzauk.siesta.type.DefaultBinaryJson;
import com.cadenzauk.siesta.type.DefaultJson;
import com.cadenzauk.siesta.type.DefaultTinyint;
import com.cadenzauk.siesta.type.DefaultVarbinary;
import com.cadenzauk.siesta.type.DefaultVarchar;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;

import static com.cadenzauk.core.lang.StringUtil.hex;
import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.HOUR_DIFF;
import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.MINUTE_DIFF;
import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.SECOND_DIFF;
import static com.cadenzauk.siesta.dialect.function.json.JsonFunctionSpecs.JSON_VALUE;

public class Db2Dialect extends AnsiDialect {
    public Db2Dialect() {
        functions()
            .register(DateFunctionSpecs::registerPlusUnits)
            .register(HOUR_DIFF, (s, a) -> "TIMESTAMPDIFF(8, char(trunc(" + a[0] + ", 'HH24') - trunc(" + a[1] + ", 'HH24')))")
            .register(MINUTE_DIFF, (s, a)-> "TIMESTAMPDIFF(4, char(trunc(" + a[0] + ", 'MI') - trunc(" + a[1] + ", 'MI')))")
            .register(SECOND_DIFF, (s, a) -> "TIMESTAMPDIFF(2, char(trunc(" + a[0] + ", 'SS') - trunc(" + a[1] + ", 'SS')))")
            .register(JSON_VALUE, (s, a) -> "JSON_VALUE(" + a[0] + " format json, " + a[1] + ")");
        ;

        types()
            .register(DbTypeId.TINYINT, new DefaultTinyint("smallint"))
            .register(DbTypeId.VARBINARY, new DefaultVarbinary() {
                @Override
                public String literal(Database database, byte[] value) {
                    return String.format("HEXTORAW('%s')", hex(value));
                }
            })
            .register(DbTypeId.VARCHAR, new DefaultVarchar())
            .register(DbTypeId.JSON, new DefaultJson("varchar") {
                @Override
                public String parameter(Database database, Optional<Json> value) {
                    return database.dialect().type(DbTypeId.VARCHAR).castParameter(database, value.map(Json::data));
                }
            })
            .register(DbTypeId.JSONB, new DefaultBinaryJson("varchar") {
                @Override
                public String parameter(Database database, Optional<BinaryJson> value) {
                    return database.dialect().type(DbTypeId.VARCHAR).castParameter(database, value.map(BinaryJson::data));
                }
            });

        exceptions()
            .register("42704", NoSuchObjectException::new)
            .register("07006", SqlSyntaxException::new)
            .register("42[67]..", SqlSyntaxException::new)

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

        setSequenceInfo(new Db2SequenceInfo());
        setTempTableInfo(new Db2TempTableInfo());
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
    public boolean requiresInValues() {
        return true;
    }

    @Override
    public boolean supportsLockTimeout() {
        return true;
    }

    @Override
    public String setLockTimeout(long time, TimeUnit unit) {
        return String.format("set current lock timeout %d", unit.toSeconds(time));
    }

    @Override
    public String resetLockTimeout() {
        return "set current lock timeout null";
    }

    @Override
    public boolean supportsJsonFunctions() {
        return true;
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
