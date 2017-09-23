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
import com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs;
import com.cadenzauk.siesta.type.DefaultIntegerTypeAdapter;
import com.cadenzauk.siesta.type.DefaultLocalDateTimeTypeAdapter;
import com.cadenzauk.siesta.type.DefaultZonedDateTimeTypeAdapter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class FirebirdDialect extends AnsiDialect {
    public FirebirdDialect() {
        DateFunctionSpecs.registerExtract(functions());
        DateFunctionSpecs.registerDateAdd(functions());

        types()
            .register(Integer.class, new DefaultIntegerTypeAdapter() {
                @Override
                public String parameter(Database database, Integer value) {
                    return "cast(? as integer)";
                }
            })
            .register(LocalDateTime.class, new DefaultLocalDateTimeTypeAdapter() {
                @Override
                public String literal(Database database, LocalDateTime value) {
                    return String.format("TIMESTAMP '%s'", value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS")));
                }
            })
            .register(ZonedDateTime.class, new DefaultZonedDateTimeTypeAdapter() {
                @Override
                public String literal(Database database, ZonedDateTime value) {
                    ZonedDateTime localDateTime = value.withZoneSameInstant(database.databaseTimeZone());
                    return String.format("TIMESTAMP '%s'", localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS")));
                }
            });
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
    public String timestampType(Optional<Integer> prec) {
        return "timestamp";
    }

    @Override
    public String nextFromSequence(String catalog, String schema, String sequenceName) {
        return "next value for " + qualifiedName(catalog, schema, sequenceName);
    }

    @Override
    public String qualifiedName(String catalog, String schema, String name) {
        return name;
    }
}
