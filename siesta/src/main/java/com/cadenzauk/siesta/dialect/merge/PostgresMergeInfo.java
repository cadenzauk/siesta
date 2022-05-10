package com.cadenzauk.siesta.dialect.merge;

import com.cadenzauk.siesta.Dialect;
import com.cadenzauk.siesta.MergeInfo;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PostgresMergeInfo extends MergeInfo {
    public PostgresMergeInfo(Dialect dialect) {
        super(dialect);
    }

    @Override
    public String mergeSql(MergeSpec mergeSpec) {
        return String.format("insert into %s as %s(%s) values %s on conflict (%s) do update set %s",
            mergeSpec.targetTableName(),
            mergeSpec.targetAlias(),
            String.join(", ", mergeSpec.insertColumnNames()),
            mergeSpec.insertArgs().stream().map(x -> "(" + String.join(", ", mergeSpec.insertArgsSql()) + ")").collect(Collectors.joining(", ")),
            String.join(", ", mergeSpec.idColumnNames()),
            mergeSpec.updateColumnNames().stream().map(col -> String.format("%s = EXCLUDED.%s", col, col)).collect(Collectors.joining(", ")));
    }

    @Override
    public Object[] mergeArgs(MergeSpec mergeSpec) {
        return mergeSpec.insertArgs().stream().flatMap(Arrays::stream).toArray();
    }
}
