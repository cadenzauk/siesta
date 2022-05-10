package com.cadenzauk.siesta.dialect.merge;

import com.cadenzauk.siesta.Dialect;
import com.cadenzauk.siesta.MergeInfo;

public class SqlServerMergeInfo extends MergeInfo {
    public SqlServerMergeInfo(Dialect dialect) {
        super(dialect);
    }

    @Override
    public String mergeSql(MergeSpec mergeSpec) {
        return super.mergeSql(mergeSpec) + ";";
    }
}
