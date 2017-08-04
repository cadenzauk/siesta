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

package com.cadenzauk.siesta.grammar.select;

import com.cadenzauk.core.lang.CompositeAutoCloseable;
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.From;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.SqlExecutor;
import com.cadenzauk.siesta.catalog.Table;
import com.cadenzauk.siesta.grammar.expression.Precedence;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class Select<RT> implements TypedExpression<RT> {
    protected final SelectStatement<RT> statement;

    Select(SelectStatement<RT> statement) {
        this.statement = statement;
    }

    public List<RT> list() {
        return list(defaultSqlExecutor());
    }

    public List<RT> list(SqlExecutor sqlExecutor) {
        return statement.list(sqlExecutor);
    }

    public Optional<RT> optional() {
        return statement.optional(defaultSqlExecutor());
    }

    public Optional<RT> optional(SqlExecutor sqlExecutor) {
        return statement.optional(sqlExecutor);
    }

    public Stream<RT> stream(CompositeAutoCloseable compositeAutoCloseable) {
        return statement.stream(defaultSqlExecutor(), compositeAutoCloseable);
    }

    public Stream<RT> stream(SqlExecutor sqlExecutor, CompositeAutoCloseable compositeAutoCloseable) {
        return statement.stream(sqlExecutor, compositeAutoCloseable);
    }

    public RT single() {
        return single(defaultSqlExecutor());
    }

    public Select<RT> fetchFirst(long i) {
        statement.fetchFirst(i);
        return this;
    }

    private RT single(SqlExecutor sqlExecutor) {
        return statement.single(sqlExecutor);
    }

    public String sql() {
        return statement.sql();
    }

    @Override
    public String sql(Scope scope) {
        return statement.sql(scope);
    }

    @Override
    public String label(Scope scope) {
        return statement.label();
    }

    @Override
    public Precedence precedence() {
        return Precedence.SELECT;
    }

    @Override
    public RowMapper<RT> rowMapper(Scope scope, String label) {
        return statement.rowMapper();
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return statement.args(scope);
    }

    protected Scope scope() {
        return statement.scope();
    }

    protected Database database() {
        return statement.scope.database();
    }

    private SqlExecutor defaultSqlExecutor() {
        return database().getDefaultSqlExecutor();
    }

    public static <R> ExpectingJoin1<R> from(Database database, Alias<R> alias) {
        SelectStatement<R> select = new SelectStatement<>(new Scope(database, alias), From.from(alias), alias.rowMapper(), Projection.of(alias));
        return new ExpectingJoin1<>(select);
    }

    public static <R> ExpectingJoin1<R> from(Database database, Table<R> table) {
        return from(database, table.as(table.tableName()));
    }
}
