/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited
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

package com.cadenzauk.siesta.grammar.dml;

import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.SqlExecutor;
import com.cadenzauk.siesta.Transaction;

public class ExecutableStatementClause {
    protected final ExecutableStatement statement;

    protected ExecutableStatementClause(ExecutableStatement statement) {
        this.statement = statement;
    }

    public int execute() {
        return execute(database().getDefaultSqlExecutor());
    }

    public int execute(SqlExecutor sqlExecutor) {
        return statement.execute(sqlExecutor);
    }

    public int execute(Transaction transaction) {
        return statement.execute(transaction);
    }

    public String sql() {
        return statement.sql();
    }

    private Database database() {
        return statement.database();
    }
}
