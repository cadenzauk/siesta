package com.cadenzauk.siesta.dialect.merge;

import com.cadenzauk.siesta.Dialect;
import com.cadenzauk.siesta.MergeInfo;

public class DerbyMergeInfo extends MergeInfo {
    public DerbyMergeInfo(Dialect dialect) {
        super(dialect);
    }

    @Override
    public boolean supportsUpsert() {
        return false;
    }
}
