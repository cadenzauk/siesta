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
import com.cadenzauk.siesta.dialect.function.SimpleFunctionSpec;
import com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs;
import com.cadenzauk.siesta.type.DefaultBinaryTypeAdapter;
import com.cadenzauk.siesta.type.DefaultByteTypeAdapter;
import com.cadenzauk.siesta.type.DefaultLocalDateTimeTypeAdapter;
import com.cadenzauk.siesta.type.DefaultLocalDateTypeAdapter;
import com.cadenzauk.siesta.type.DefaultLocalTimeTypeAdapter;
import com.cadenzauk.siesta.type.DefaultZonedDateTimeTypeAdapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.cadenzauk.core.lang.StringUtil.hex;
import static java.util.stream.Collectors.joining;

public class SqlServerDialect extends AnsiDialect {
    private static final Pattern SELECT_PATTERN = Pattern.compile("(select (distinct )?)");

    public SqlServerDialect() {
        DateFunctionSpecs.registerDatePart(functions());
        DateFunctionSpecs.registerDateAdd(functions());
        DateFunctionSpecs.registerDateDiff(functions());

        functions().register(DateFunctionSpecs.CURRENT_DATE, SimpleFunctionSpec.of("getdate"));

        types()
            .register(Byte.class, new DefaultByteTypeAdapter() {
                @Override
                public String literal(Database database, Byte value) {
                    return String.format("cast(%d as tinyint)", value & 0xff);
                }
            })
            .register(byte[].class, new DefaultBinaryTypeAdapter() {
                @Override
                public String literal(Database database, byte[] value) {
                    return String.format("0x%s", hex(value));
                }
            })
            .register(LocalDate.class, new DefaultLocalDateTypeAdapter() {
                @Override
                public String literal(Database database, LocalDate value) {
                    return String.format("cast('%s' as date)", value.format(DateTimeFormatter.ISO_DATE));
                }
            })
            .register(LocalDateTime.class, new DefaultLocalDateTimeTypeAdapter() {
                @Override
                public String literal(Database database, LocalDateTime value) {
                    return String.format("cast('%s' as datetime2(6))", value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")));
                }

                @Override
                public String parameter(Database database, LocalDateTime value) {
                    return "cast(? as datetime2)";
                }
            })
            .register(LocalTime.class, new DefaultLocalTimeTypeAdapter() {
                @Override
                public String literal(Database database, LocalTime value) {
                    return String.format("cast('%s' as time)", value.format(DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS")));
                }
            })
            .register(ZonedDateTime.class, new DefaultZonedDateTimeTypeAdapter() {
                @Override
                public String literal(Database database, ZonedDateTime value) {
                    ZonedDateTime localDateTime = value.withZoneSameInstant(database.databaseTimeZone());
                    return String.format("cast('%s' as datetime2(6))", localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")));
                }

                @Override
                public String parameter(Database database, ZonedDateTime value) {
                    return "cast(? as datetime2)";
                }
            });
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
    public String concat(Stream<String> sql) {
        return "concat(" + sql.collect(joining(", ")) + ")";
    }

    @Override
    public String fetchFirst(String sql, long n) {
        return SELECT_PATTERN.matcher(sql).replaceFirst("$1top " + n + " ");
    }

    @Override
    public String tinyintType() {
        return "tinyint";
    }

    @Override
    public String timestampType(Optional<Integer> prec) {
        return prec
            .map(p -> String.format("datetime2(%d)", p))
            .orElse("datetime2");
    }

    @Override
    public String nextFromSequence(String catalog, String schema, String sequenceName) {
        return "next value for " + qualifiedName(catalog, schema, sequenceName);
    }
}
