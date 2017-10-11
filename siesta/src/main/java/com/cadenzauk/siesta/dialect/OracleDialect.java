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
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.dialect.function.ArgumentlessFunctionSpec;
import com.cadenzauk.siesta.dialect.function.FunctionSpec;
import com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.type.DbTypeId;
import com.cadenzauk.siesta.type.DefaultBigint;
import com.cadenzauk.siesta.type.DefaultDate;
import com.cadenzauk.siesta.type.DefaultTime;
import com.cadenzauk.siesta.type.DefaultTinyint;
import com.cadenzauk.siesta.type.DefaultVarbinary;
import com.cadenzauk.siesta.type.DefaultVarchar;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.cadenzauk.core.lang.StringUtil.hex;
import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.HOUR_DIFF;
import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.MINUTE_DIFF;
import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.SECOND_DIFF;

public class OracleDialect extends AnsiDialect {
    public OracleDialect() {
        DateFunctionSpecs.registerExtract(functions());
        DateFunctionSpecs.registerPlusNumToDsInterval(functions());
        functions()
            .register(DateFunctionSpecs.CURRENT_TIMESTAMP, ArgumentlessFunctionSpec.of("localtimestamp"))
            .register(HOUR_DIFF, new FunctionSpec() {
                @Override
                public String sql(String[] a) {
                    return String.format("extract(day from (%1$s - %2$s)) * 24 + extract(hour from (%1$s - %2$s))", a[0], a[1]);
                }

                @Override
                public Stream<Object> args(Scope scope, TypedExpression<?>[] args) {
                    return Stream.concat(
                        Arrays.stream(args),
                        Arrays.stream(args)).flatMap(a -> a.args(scope));
                }
            })
            .register(MINUTE_DIFF, a -> String.format("round((trunc(cast(%1$s as date), 'MI') - trunc(cast(%2$s as date), 'MI')) * 1440)", a[0], a[1]))
            .register(SECOND_DIFF, a -> String.format("round((cast(%1$s as date) - cast(%2$s as date)) * 86400)", a[0], a[1]))
        ;

        types()
            .register(DbTypeId.TINYINT, new DefaultTinyint("smallint"))
            .register(DbTypeId.BIGINT, new DefaultBigint() {
                @Override
                public String sqlType(Database database) {
                    return "number(19)";
                }
            })
            .register(DbTypeId.VARBINARY, new DefaultVarbinary() {
                @Override
                public String literal(Database database, byte[] value) {
                    return String.format("hextoraw('%s')", hex(value));
                }
            })
            .register(DbTypeId.DATE, new DefaultDate() {
                @Override
                public String literal(Database database, LocalDate value) {
                    return String.format("to_date('%s', 'yyyy-mm-dd')", value.format(DateTimeFormatter.ISO_DATE));
                }
            })
            .register(DbTypeId.TIME, new DefaultTime() {
                @Override
                public String literal(Database database, LocalTime value) {
                    return String.format("INTERVAL '%s' HOUR TO SECOND", value.format(DateTimeFormatter.ISO_TIME));
                }

                @Override
                public Object convertToDatabase(Database database, LocalTime value) {
                    return "0 " + value.format(DateTimeFormatter.ISO_TIME);
                }

                @Override
                public LocalTime getColumnValue(Database database, ResultSet rs, String col) throws SQLException {
                    String string = rs.getString(col);
                    return string == null || rs.wasNull() ? null : parse(string);
                }

                @Override
                public LocalTime getColumnValue(Database database, ResultSet rs, int col) throws SQLException {
                    String string = rs.getString(col);
                    return string == null || rs.wasNull() ? null : parse(string);
                }

                @Override
                public String sqlType(Database database) {
                    return "interval day to second";
                }

                private LocalTime parse(String string) {
                    Matcher matcher = Pattern.compile("0 (\\d+):(\\d+):(\\d+)(\\.\\d+)?").matcher(string);
                    if (matcher.matches()) {
                        return LocalTime.of(
                            Integer.parseInt(matcher.group(1)),
                            Integer.parseInt(matcher.group(2)),
                            Integer.parseInt(matcher.group(3))
                        );
                    }
                    return LocalTime.parse(string);
                }
            })
            .register(DbTypeId.VARCHAR, new DefaultVarchar() {
                @Override
                public String sqlType(Database database) {
                    return "varchar2";
                }
            });
    }

    @Override
    public String fetchFirst(String sql, long n) {
        return String.format("select * from (%s) where rownum <= %d", sql, n);
    }

}
