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

import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Transaction;
import com.cadenzauk.siesta.catalog.Column;
import com.cadenzauk.siesta.catalog.ColumnMapping;
import com.google.common.reflect.TypeToken;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public abstract class TempTable<R> {
    private final Database database;
    private final TypeToken<R> rowType;
    private final String tableName;
    private final ColumnMapping<R,?> columnMapping;
    private final TempTableCommitAction onCommit;

    protected <B> TempTable(Builder<R,B,?> builder) {
        database = builder.database;
        rowType = TypeToken.of(builder.rowClass);
        tableName = builder.tableName;
        onCommit = builder.onCommit;
        columnMapping = new ColumnMapping<>(builder);
    }

    protected String tableName() {
        return tableName;
    }

    protected TypeToken<R> rowType() {
        return rowType;
    }

    protected Database database() {
        return database;
    }

    public TempTableCommitAction onCommit() {
        return onCommit;
    }

    public abstract String qualifiedTableName();

    public Alias<R> as(String aliasName) {
        return new TempTableAlias<>(this, OptionalUtil.ofBlankable(aliasName));
    }

    public Stream<Column<?,R>> columns() {
        return columnMapping.columns();
    }

    public Stream<TempTableColumn<R>> columnDefinitions() {
        return columnMapping
            .primitiveColumns()
            .map(x -> TempTableColumn.<R>newBuilder()
                .columnName(x.columnName())
                .columnType(x.columnType())
                .build());
    }

    public RowMapperFactory<R> rowMapperFactory(TempTableAlias<R> alias) {
        return columnMapping.rowMapperFactory(alias, Optional.empty());
    }

    public int insert(Transaction transaction, R[] rows) {
        if (database().dialect().supportsMultiInsert()) {
            return performInsert(transaction, rows);
        } else {
            return Arrays.stream(rows)
                .mapToInt(r -> performInsert(transaction, r))
                .sum();
        }
    }

    @SafeVarargs
    private final int performInsert(Transaction transaction, R... rows) {
        if (rows.length == 0) {
            return 0;
        }
        String sql = insertSql(rows);
        Object[] args = columnMapping.insertArgs(rows);
        return database.execute(sql, () -> transaction.update(sql, args));
    }

    private String insertSql(R[] rows) {
        return String.format("insert into %s (%s) values %s",
            qualifiedTableName(),
            columnMapping.columns().flatMap(Column::insertColumnSql).collect(joining(", ")),
            IntStream.range(0, rows.length)
                .mapToObj(i -> "(" + columnMapping.columns().flatMap(Column::insertArgsSql).collect(joining(", ")) + ")")
                .collect(joining(", ")));
    }

    public static abstract class Builder<R, B, S extends Builder<R,B,S>> extends ColumnMapping.Builder<R,B,S> {
        protected final Database database;
        protected final Class<R> rowClass;
        protected String tableName;
        private TempTableCommitAction onCommit = TempTableCommitAction.PRESERVE_ROWS;

        public Builder(Database database, Class<R> rowClass, TypeToken<B> builderType, Function<B,R> build) {
            super(database, TypeToken.of(rowClass), builderType, build);
            this.database = database;
            this.rowClass = rowClass;
        }

        public S tableName(String val) {
            tableName = val;
            return self();
        }

        public S onCommit(TempTableCommitAction val) {
            onCommit = val;
            return self();
        }
    }
}
