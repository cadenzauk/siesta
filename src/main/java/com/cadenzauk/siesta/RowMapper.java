/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.siesta;

import java.sql.ResultSet;

public interface RowMapper<T> {
    T mapRow(ResultSet rs, int rowNum);
}
