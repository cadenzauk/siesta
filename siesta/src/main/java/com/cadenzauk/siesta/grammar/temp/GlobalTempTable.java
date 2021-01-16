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
import com.cadenzauk.siesta.Database;
import com.google.common.reflect.TypeToken;

import java.util.function.Function;

public class GlobalTempTable<R> extends TempTable<R>{
    private final String catalog;
    private final String schema;

    private <B> GlobalTempTable(Builder<R,B> builder) {
        super(builder);
        catalog = builder.catalog;
        schema = builder.schema;
    }

    @Override
    public String qualifiedTableName() {
        return database().dialect().tempTableInfo().globalTempTableName(database().dialect(), catalog, schema, tableName());
    }

    public static <R> Builder<R,R> newBuilder(Database database, Class<R> rowClass) {
        return new Builder<>(database, rowClass, TypeToken.of(rowClass), Function.identity());
    }

    public static class Builder<R,B> extends TempTable.Builder<R,B,Builder<R,B>> {
        private String catalog = "";
        private String schema = "";

        public Builder(Database database, Class<R> rowClass, TypeToken<B> builderType, Function<B,R> build) {
            super(database, rowClass, builderType, build);
        }

        public Builder<R,B> catalog(String val) {
            catalog = val;
            return this;
        }

        public Builder<R,B> schema(String val) {
            schema = val;
            return this;
        }

        public <B2> Builder<R,B2> builder(Function1<B2,R> val) {
            MethodInfo<B2,R> buildMethod = MethodInfo.of(val);
            return new Builder<>(database, rowClass, buildMethod.referringType(), val)
                .tableName(tableName);
        }

        public TempTable<R> build() {
            finish();
            return new GlobalTempTable<>(this);
        }

    }
}
