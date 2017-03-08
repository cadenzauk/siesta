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

import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.From;
import com.cadenzauk.siesta.Order;
import com.cadenzauk.siesta.Ordering;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.RowMapper;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.SqlExecutor;
import com.cadenzauk.siesta.catalog.Table;
import com.cadenzauk.siesta.expression.AndExpression;
import com.cadenzauk.siesta.expression.Expression;
import com.cadenzauk.siesta.expression.TypedExpression;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class Select<RT> implements TypedExpression<RT> {
    protected final Scope scope;
    protected final From from;
    protected final SelectStatement<RT> statement = new Statement();
    private final RowMapper<RT> rowMapper;
    private final Projection projection;
    private final List<Ordering<?,?>> orderByClauses = new ArrayList<>();
    private Expression whereClause;

    Select(Scope scope, From from, RowMapper<RT> rowMapper, Projection projection) {
        this.scope = scope;
        this.from = from;
        this.rowMapper = rowMapper;
        this.projection = projection;
    }

    @Override
    public String sql(Scope outerScope) {
        return "(" + sqlImpl(outerScope.plus(scope)) + ")";
    }

    @Override
    public String label(Scope scope) {
        return null;
    }

    @Override
    public Stream<Object> args() {
        return Stream.concat(from.args(), whereClauseArgs());
    }

    @Override
    public RowMapper<RT> rowMapper(Scope scope, String label) {
        return rowMapper();
    }

    ExpectingWhere<RT> expectingWhere() {
        return new ExpectingWhere<>(statement);
    }

    protected Projection projection() {
        return projection;
    }

    private Optional<RT> optional(SqlExecutor sqlExecutor) {
        return OptionalUtil.ofOnly(list(sqlExecutor));
    }

    private List<RT> list(SqlExecutor sqlExecutor) {
        Object[] args = args().toArray();
        String sql = sql();
        System.out.println(sql);
        return sqlExecutor.query(sql, args, rowMapper());
    }

    private String sql() {
        return sqlImpl(scope);
    }

    private RowMapper<RT> rowMapper() {
        return rowMapper;
    }

    private <T> void addOrderBy(TypedExpression<T> expression, Order order) {
        orderByClauses.add(new Ordering<>(expression, order));
    }

    private InWhereExpectingAnd<RT> setWhereClause(Expression e) {
        whereClause = e;
        return new InWhereExpectingAnd<>(statement);
    }

    private void andWhere(Expression e) {
        whereClause = new AndExpression(whereClause, e);
    }

    @NotNull
    private Stream<Object> whereClauseArgs() {
        return whereClause == null
            ? Stream.empty()
            : whereClause.args();
    }

    @NotNull
    private String whereClauseSql(Scope scope) {
        return whereClause == null
            ? ""
            : " where " + whereClause.sql(scope);
    }

    @NotNull
    private String orderByClauseSql(Scope scope) {
        return orderByClauses.isEmpty()
            ? ""
            : " order by " + orderByClauses.stream().map(ordering -> ordering.sql(scope)).collect(joining(", "));
    }

    private String sqlImpl(Scope actualScope) {
        return String.format("select %s from %s%s%s",
            projection().sql(actualScope),
            from.sql(actualScope),
            whereClauseSql(actualScope),
            orderByClauseSql(actualScope));
    }

    public static <R> ExpectingJoin1<R> from(Database database, Alias<R> alias) {
        Select<R> select = new Select<>(new Scope(database, alias), From.from(alias), alias.rowMapper(), Projection.of(alias));
        return new ExpectingJoin1<>(select.statement);
    }

    public static <R> ExpectingJoin1<R> from(Database database, Table<R> table) {
        return from(database, table.as(table.tableName()));
    }

    private class Statement implements SelectStatement<RT> {
        @Override
        public List<RT> list(SqlExecutor sqlExecutor) {
            return Select.this.list(sqlExecutor);
        }

        @Override
        public Optional<RT> optional(SqlExecutor sqlExecutor) {
            return Select.this.optional(sqlExecutor);
        }

        @Override
        public String sql() {
            return Select.this.sql();
        }

        @Override
        public InWhereExpectingAnd<RT> setWhereClause(Expression e) {
            return Select.this.setWhereClause(e);
        }

        @Override
        public From from() {
            return Select.this.from;
        }

        @Override
        public <T> void addOrderBy(TypedExpression<T> column, Order ascending) {
            Select.this.addOrderBy(column, ascending);
        }

        @Override
        public Scope scope() {
            return Select.this.scope;
        }

        @Override
        public Projection projection() {
            return Select.this.projection();
        }

        @Override
        public RowMapper<RT> rowMapper() {
            return Select.this.rowMapper();
        }

        @Override
        public void andWhere(Expression newClause) {
            Select.this.andWhere(newClause);
        }

        @Override
        public String label(Scope scope) {
            return Select.this.label(scope);
        }

        @Override
        public RowMapper<RT> rowMapper(Scope scope, String label) {
            return Select.this.rowMapper(scope, label);
        }

        @Override
        public String sql(Scope scope) {
            return Select.this.sql(scope);
        }

        @Override
        public Stream<Object> args() {
            return Select.this.args();
        }
    }
}
