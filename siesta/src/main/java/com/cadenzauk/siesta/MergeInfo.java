/*
 * Copyright (c) 2022 Cadenza United Kingdom Limited
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

package com.cadenzauk.siesta;

import com.cadenzauk.siesta.dialect.merge.MergeSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.IntStream;

import static java.lang.Math.min;
import static java.util.stream.Collectors.joining;

public class MergeInfo {
    private final Dialect dialect;
    private final boolean supportsMultiRowUpsert;

    public MergeInfo(Dialect dialect) {
        this(dialect, true);
    }

    public MergeInfo(Dialect dialect, boolean supportsMultiRowUpsert) {
        this.dialect = dialect;
        this.supportsMultiRowUpsert = supportsMultiRowUpsert;
    }

    public boolean supportsUpsert() {
        return true;
    }

    public boolean supportsMultiRowUpsert() {
        return supportsMultiRowUpsert;
    }

    public int insertedResult() {
        return 1;
    }

    public int updatedResult() {
        return 1;
    }

    public int insertedAndUpdatedResult() {
        return insertedResult() + updatedResult();
    }

    private String selectSql(MergeSpec mergeSpec) {
        if (mergeSpec.selectArgsSql() != null) {
            return selectArgsSql(mergeSpec);
        } else {
            return selectRowsArgsSql(mergeSpec);
        }
    }

    protected @NotNull String selectArgsSql(MergeSpec mergeSpec) {
        return IntStream.range(0, min(mergeSpec.columnNames().size(), mergeSpec.selectArgsSql().size()))
            .mapToObj(i -> String.format("%s %s", mergeSpec.selectArgsSql().get(i), mergeSpec.columnNames().get(i)))
            .collect(joining(", ", "select ", "")) +
            (dialect.requiresFromDual() ? " from " + dialect.dual() : "");
    }

    protected  @NotNull String selectRowsArgsSql(MergeSpec mergeSpec) {
        String columnsSql = String.join(", ", mergeSpec.columnNames());
        return mergeSpec.selectRowsArgsSql().stream()
            .map(r -> r.stream().collect(joining(", ", "(", ")")))
            .collect(joining(", ", "select * from (values ", ") as x(" + columnsSql + ")"));
    }

    public String mergeSql(MergeSpec mergeSpec) {
        String selectSql = selectSql(mergeSpec);
        return String.format("merge into %s %s using (%s) %s on (%s) when matched then update set %s when not matched then insert(%s) values(%s)",
            mergeSpec.targetTableName(),
            mergeSpec.targetAlias(),
            selectSql,
            mergeSpec.sourceAlias(),
            mergeSpec.idColumnNames().stream().map(col -> String.format("%s.%s = %s.%s", mergeSpec.targetAlias(), col, mergeSpec.sourceAlias(), col)).collect(joining(" and ")),
            mergeSpec.updateColumnNames().stream().map(col -> String.format("%s = %s.%s", col, mergeSpec.sourceAlias(), col)).collect(joining(", ")),
            String.join(", ", mergeSpec.insertColumnNames()),
            mergeSpec.insertColumnNames().stream().map(col -> String.format("%s.%s", mergeSpec.sourceAlias(), col)).collect(joining(", ")));
    }

    public Object[] mergeArgs(MergeSpec mergeSpec) {
        return mergeSpec.selectArgs().stream().flatMap(Arrays::stream).toArray();
    }

}
