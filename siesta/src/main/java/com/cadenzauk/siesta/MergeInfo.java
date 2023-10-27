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

import java.util.Arrays;
import java.util.stream.IntStream;

import static java.lang.Math.min;
import static java.util.stream.Collectors.joining;

public class MergeInfo {
    private final Dialect dialect;

    public MergeInfo(Dialect dialect) {
        this.dialect = dialect;
    }

    public boolean supportsUpsert() {
        return true;
    }

    public int insertedResult() {
        return 1;
    }

    public int updatedResult() {
        return 1;
    }

    public String mergeSql(MergeSpec mergeSpec) {
        return String.format("merge into %s %s using (select %s%s) %s on (%s) when matched then update set %s when not matched then insert(%s) values(%s)",
            mergeSpec.targetTableName(),
            mergeSpec.targetAlias(),
            IntStream.range(0, min(mergeSpec.columnNames().size(), mergeSpec.selectArgsSql().size()))
                .mapToObj(i -> String.format("%s %s", mergeSpec.selectArgsSql().get(i), mergeSpec.columnNames().get(i)))
                .collect(joining(", ")),
            (dialect.requiresFromDual() ? " from " + dialect.dual() : ""),
            mergeSpec.sourceAlias(),
            mergeSpec.idColumnNames().stream().map(col -> String.format("%s.%s = %s.%s", mergeSpec.targetAlias(), col, mergeSpec.sourceAlias(), col)).collect(joining(" and ")),
            mergeSpec.updateColumnNames().stream().map(col -> String.format("%s.%s = %s.%s", mergeSpec.targetAlias(), col, mergeSpec.sourceAlias(), col)).collect(joining(", ")),
            String.join(", ", mergeSpec.insertColumnNames()),
            mergeSpec.insertColumnNames().stream().map(col -> String.format("%s.%s", mergeSpec.sourceAlias(), col)).collect(joining(", ")));
    }

    public Object[] mergeArgs(MergeSpec mergeSpec) {
        return mergeSpec.selectArgs().stream().flatMap(Arrays::stream).toArray();
    }

}
