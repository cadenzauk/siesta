/*
 * Copyright (c) 2025 Cadenza United Kingdom Limited
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
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.min;
import static java.util.stream.Collectors.joining;

public class OracleMergeInfo extends MergeInfo {
    public OracleMergeInfo(Dialect dialect) {
        super(dialect, true);
    }

    @Override
    protected @NotNull String selectRowsArgsSql(MergeSpec mergeSpec) {
        return mergeSpec.selectRowsArgsSql().stream()
            .map(r -> IntStream.range(0, min(mergeSpec.columnNames().size(), r.size()))
                .mapToObj(i -> String.format("%s %s", r.get(i), mergeSpec.columnNames().get(i)))
                .collect(Collectors.joining(", ", "select ", " from dual")))
            .collect(joining(" union all "));
    }
}
