/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta.name;

import com.cadenzauk.core.lang.StringUtil;
import com.cadenzauk.siesta.NamingStrategy;

public class UppercaseUnderscores implements NamingStrategy {
    @Override
    public String tableName(String rowClass) {
        return StringUtil.camelToUpper(rowClass);
    }

    @Override
    public String columnName(String fieldName) {
        return StringUtil.camelToUpper(fieldName);
    }
}
