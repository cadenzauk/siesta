/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.siesta;

import java.util.List;

public interface SqlExecutor {
    <T> List<T> query(String sql, Object[] args, RowMapper<T> rowMapper);
}
