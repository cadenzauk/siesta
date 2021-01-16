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

import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.ddl.definition.action.ColumnDataType;

import java.util.Optional;

public class TempTableColumn<R> {
    private final String columnName;
    private final ColumnDataType<?> columnType;

    private TempTableColumn(Builder<R> builder) {
        columnName = builder.columnName;
        columnType = builder.columnType;
    }

    public String sqlWithLabel(Alias<R> alias, Optional<String> label) {
        return alias.inSelectClauseSql(columnName, label);
    }

    public String columnName() {
        return columnName;
    }

    public String sql(Database database) {
        return String.format("%s %s", columnName, columnType.sql(database));
    }

    public static <R> Builder<R> newBuilder() {
        return new Builder<>();
    }

    public static final class Builder<R> {
        private String columnName;
        private ColumnDataType<?> columnType;

        private Builder() {
        }

        public Builder<R> columnName(String val) {
            columnName = val;
            return this;
        }

        public Builder<R> columnType(ColumnDataType<?> val) {
            columnType = val;
            return this;
        }

        public TempTableColumn<R> build() {
            return new TempTableColumn<>(this);
        }
    }
}
