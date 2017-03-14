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
import com.cadenzauk.siesta.grammar.expression.AndExpression;
import com.cadenzauk.siesta.grammar.expression.BooleanExpression;
import com.cadenzauk.siesta.grammar.expression.Assignment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class Update<U> {
    private final UpdateStatement<U> statement = new Statement();
    private final Scope scope;
    private final Alias<U> alias;
    private final List<Assignment> sets = new ArrayList<>();
    private BooleanExpression whereClause;

    private Update(Database database, Alias<U> alias) {
        this.alias = alias;
        scope = new Scope(database, alias);
    }

    private String sql() {
        return String.format("update %s as %s set %s%s",
            alias.table().qualifiedName(),
            alias.aliasName(),
            sets.stream().map(e -> e.sql(scope)).collect(joining(", ")),
            whereClauseSql());
    }

    private int execute(SqlExecutor sqlExecutor) {
        Object[] args = args(scope).toArray();
        String sql = sql();
        System.out.println(sql);
        return sqlExecutor.update(sql, args);
    }

    private SetClause<U> addSet(Assignment expression) {
        sets.add(expression);
        return new SetClause<>(statement);
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

    private WhereClause<U> setWhereClause(BooleanExpression e) {
        whereClause = e;
        return new WhereClause<>(statement);
    }

    private void andWhere(BooleanExpression newClause) {
        whereClause = new AndExpression(whereClause, newClause);
    }

    private SetClause<U> setClause() {
        return new SetClause<>(statement);
    }

    public static <U> SetClause<U> update(Database database, Table<U> table) {
        return new Update<>(database, table.as(table.tableName())).setClause();
    }

    private class Statement implements UpdateStatement<U> {
        @Override
        public int execute(SqlExecutor sqlExecutor) {
            return Update.this.execute(sqlExecutor);
        }

        @Override
        public WhereClause<U> setWhereClause(BooleanExpression e) {
            return Update.this.setWhereClause(e);
        }

        @Override
        public void andWhere(BooleanExpression newClause) {
            Update.this.andWhere(newClause);
        }

        @Override
        public SetClause<U> addSet(Assignment assignment) {
            return Update.this.addSet(assignment);
        }

        @Override
        public Database database() {
            return Update.this.scope.database();
        }

        @Override
        public String sql() {
            return Update.this.sql();
        }
    }
}
