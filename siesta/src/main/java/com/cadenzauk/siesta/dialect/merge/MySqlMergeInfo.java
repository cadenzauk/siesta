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

package com.cadenzauk.siesta.dialect.merge;

import com.cadenzauk.siesta.Dialect;
import com.cadenzauk.siesta.MergeInfo;

import java.util.Arrays;
import java.util.stream.Collectors;

public class MySqlMergeInfo extends MergeInfo {
    public MySqlMergeInfo(Dialect dialect) {
        super(dialect, false);
    }

    @Override
    public int updatedResult() {
        return 2;
    }

    @Override
    public String mergeSql(MergeSpec mergeSpec) {
        return String.format("insert into %s(%s) values %s on duplicate key update %s",
            mergeSpec.targetTableName(),
            String.join(", ", mergeSpec.insertColumnNames()),
            mergeSpec.insertArgs().stream().map(x -> "(" + String.join(", ", mergeSpec.insertArgsSql()) + ")").collect(Collectors.joining(", ")),
            mergeSpec.updateColumnNames().stream().map(col -> String.format("%s = values(%s)", col, col)).collect(Collectors.joining(", ")));
    }

    @Override
    public Object[] mergeArgs(MergeSpec mergeSpec) {
        return mergeSpec.insertArgs().stream().flatMap(Arrays::stream).toArray();
    }
}
