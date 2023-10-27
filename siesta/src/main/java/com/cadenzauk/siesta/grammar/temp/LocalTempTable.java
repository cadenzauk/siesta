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

package com.cadenzauk.siesta.grammar.temp;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.core.sql.QualifiedName;
import com.cadenzauk.core.util.TokenReplacer;
import com.cadenzauk.siesta.Database;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;

import java.util.function.Function;

import static java.util.stream.Collectors.joining;

public class LocalTempTable<R> extends TempTable<R> {
    private <B> LocalTempTable(Builder<R,B> builder) {
        super(builder);
    }

    public String qualifiedTableName() {
        String catalog = database().defaultCatalog();
        String schema = database().defaultSchema();
        String table = tableName();
        TokenReplacer tokenReplacer = new TokenReplacer(ImmutableMap.of(
            "catalogName", catalog,
            "schemaName", schema,
            "tableName", table,
            "qualifiedTableName", database().dialect().qualifiedTableName(catalog, schema, table)
        ));
        return database().dialect().tempTableInfo().tableName(tokenReplacer);
    }

    public String createSql() {
        String columnsSql = columnDefinitions()
                                .map(c -> c.sql(database()))
                                .collect(joining(", "));
        TokenReplacer tokenReplacer = new TokenReplacer(ImmutableMap.of(
            "tableName", qualifiedTableName(),
            "columnDefs", columnsSql
        ));
        switch (onCommit()) {
            case PRESERVE_ROWS:
            default:
                return database().dialect().tempTableInfo().createLocalPreserveRowsSql(tokenReplacer);
            case DELETE_ROWS:
                return database().dialect().tempTableInfo().createLocalDeleteRowsSql(tokenReplacer);
            case DROP_TABLE:
                return database().dialect().tempTableInfo().createLocalDropTableSql(tokenReplacer);
        }
    }

    public static <R> Builder<R,R> newBuilder(Database database, Class<R> rowClass) {
        return new Builder<>(database, rowClass, TypeToken.of(rowClass), Function.identity());
    }

    public static class Builder<R, B> extends TempTable.Builder<R,B,Builder<R,B>> {
        public Builder(Database database, Class<R> rowClass, TypeToken<B> builderType, Function<B,R> build) {
            super(database, rowClass, builderType, build);
        }

        public <B2> Builder<R,B2> builder(Function1<B2,R> val) {
            MethodInfo<B2,R> buildMethod = MethodInfo.of(val);
            return new Builder<>(database, rowClass, buildMethod.referringType(), val)
                       .tableName(tableName);
        }

        public LocalTempTable<R> build() {
            finish();
            return new LocalTempTable<>(this);
        }
    }
}
