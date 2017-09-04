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
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.SqlExecutor;
import com.cadenzauk.siesta.Transaction;
import com.cadenzauk.siesta.grammar.expression.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

public abstract class ExecutableStatement {
    private final static Logger LOG = LoggerFactory.getLogger(ExecutableStatement.class);
    private final Scope scope;
    private BooleanExpression whereClause;

    protected ExecutableStatement(Scope scope) {
        this.scope = scope;
    }

    int execute(SqlExecutor sqlExecutor) {
        Object[] args = args(scope).toArray();
        String sql = sql(scope);
        LOG.debug(sql);
        return sqlExecutor.update(sql, args);
    }

    int execute(Transaction transaction) {
        Object[] args = args(scope).toArray();
        String sql = sql(scope);
        LOG.debug(sql);
        return transaction.update(sql, args);
    }

    Database database() {
        return scope.database();
    }

    String sql() {
        return sql(scope);
    }

    protected String whereClauseSql(Scope scope) {
        return whereClause == null ? "" : " where " + whereClause.sql(scope);
    }

    protected Stream<Object> whereClauseArgs(Scope scope) {
        return whereClause == null ? Stream.empty() : whereClause.args(scope);
    }

    InWhereExpectingAnd setWhereClause(BooleanExpression e) {
        whereClause = e;
        return new InWhereExpectingAnd(this);
    }

    void andWhere(BooleanExpression newClause) {
        whereClause = whereClause.appendAnd(newClause);
    }

    void orWhere(BooleanExpression newClause) {
        whereClause = whereClause.appendOr(newClause);
    }

    protected abstract String sql(Scope scope);

    protected abstract Stream<Object> args(Scope scope);
}
