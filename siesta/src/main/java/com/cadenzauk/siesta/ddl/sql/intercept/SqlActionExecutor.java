/*
 * Copyright (c) 2018 Cadenza United Kingdom Limited
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

package com.cadenzauk.siesta.ddl.sql.intercept;

import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.ddl.action.Action;
import com.cadenzauk.siesta.ddl.action.ActionInterceptor;
import com.cadenzauk.siesta.ddl.action.Priority;
import com.cadenzauk.siesta.ddl.sql.action.SqlAction;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

public class SqlActionExecutor extends ActionInterceptor<SqlAction> {
    @Override
    public int priority() {
        return Priority.EXECUTION;
    }

    @Override
    public Stream<Action> intercept(Database database, SqlAction action) {
        String sql = action.sql(database);
        if (StringUtils.isNotBlank(sql)) {
            database.execute(sql, () -> database.getDefaultSqlExecutor().update(sql));
        }
        return Stream.of(action);
    }

    @Override
    public TypeToken<SqlAction> supportedType() {
        return TypeToken.of(SqlAction.class);
    }
}
