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
