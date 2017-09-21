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

import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.dialect.function.ArgumentlessFunctionSpec;
import com.cadenzauk.siesta.dialect.function.FunctionSpec;
import com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Stream;

import static com.cadenzauk.core.lang.StringUtil.hex;
import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.HOUR_DIFF;

public class OracleDialect extends AnsiDialect {
    public OracleDialect() {
        DateFunctionSpecs.registerExtract(functions());
        DateFunctionSpecs.registerPlusNumToDsInterval(functions());
        functions().register(DateFunctionSpecs.CURRENT_TIMESTAMP, ArgumentlessFunctionSpec.of("localtimestamp"));
        functions().register(HOUR_DIFF, new FunctionSpec() {
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
        });
    }

    @Override
    public String dateLiteral(LocalDate date) {
        return String.format("to_date('%s', 'yyyy-mm-dd')", date.format(DateTimeFormatter.ISO_DATE));
    }

    @Override
    public String binaryLiteral(byte[] bytes) {
        return String.format("hextoraw('%s')", hex(bytes));
    }

    @Override
    public String fetchFirst(String sql, long n) {
        return String.format("select * from (%s) where rownum <= %d", sql, n);
    }

    @Override
    public String bigintType() {
        return "NUMBER(19)";
    }

    @Override
    public String varcharType(int size) {
        return String.format("varchar2(%d)", size);
    }
}
