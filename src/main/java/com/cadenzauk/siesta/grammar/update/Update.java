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

package com.cadenzauk.siesta.grammar.update;

import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.SqlExecutor;
import com.cadenzauk.siesta.catalog.Table;
import com.cadenzauk.siesta.grammar.expression.Assignment;
import com.cadenzauk.siesta.grammar.expression.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class Update<U> {
    private final static Logger LOG = LoggerFactory.getLogger(Update.class);
    private final Scope scope;
    private final Alias<U> alias;
    private final List<Assignment> sets = new ArrayList<>();
    private BooleanExpression whereClause;

    private Update(Database database, Alias<U> alias) {
        this.alias = alias;
        scope = new Scope(database, alias);
    }

    Database database() {
        return scope.database();
    }

    String sql() {
        return String.format("update %s as %s set %s%s",
            alias.table().qualifiedName(),
            alias.aliasName(),
            sets.stream().map(e -> e.sql(scope)).collect(joining(", ")),
            whereClauseSql());
    }

    int execute(SqlExecutor sqlExecutor) {
        Object[] args = args(scope).toArray();
        String sql = sql();
        LOG.debug(sql);
        return sqlExecutor.update(sql, args);
    }

    InSetExpectingWhere<U> addSet(Assignment expression) {
        sets.add(expression);
        return new InSetExpectingWhere<>(this);
    }

    private String whereClauseSql() {
        return whereClause == null ? "" : " where " + whereClause.sql(scope);
    }

    private Stream<Object> args(Scope scope) {
        return Stream.concat(
            sets.stream().flatMap(a -> a.args(scope)),
            whereClause == null ? Stream.empty() : whereClause.args(scope)
        );
    }

    InWhereExpectingAnd<U> setWhereClause(BooleanExpression e) {
        whereClause = e;
        return new InWhereExpectingAnd<>(this);
    }

    void andWhere(BooleanExpression newClause) {
        whereClause = whereClause.appendAnd(newClause);
    }

    void orWhere(BooleanExpression newClause) {
        whereClause = whereClause.appendOr(newClause);
    }

    private InSetExpectingWhere<U> setClause() {
        return new InSetExpectingWhere<>(this);
    }

    public static <U> InSetExpectingWhere<U> update(Database database, Table<U> table) {
        return new Update<>(database, table.as(table.tableName())).setClause();
    }
}
