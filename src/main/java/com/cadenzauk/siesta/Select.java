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

package com.cadenzauk.siesta;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.core.tuple.Tuple3;
import com.cadenzauk.core.tuple.Tuple4;
import com.cadenzauk.core.tuple.Tuple5;
import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.catalog.Table;
import com.cadenzauk.siesta.expression.AndExpression;
import com.cadenzauk.siesta.expression.Expression;
import com.cadenzauk.siesta.expression.ResolvedColumn;
import com.cadenzauk.siesta.expression.TypedExpression;
import com.cadenzauk.siesta.expression.UnresolvedColumn;
import com.cadenzauk.siesta.grammar.ExpressionBuilder;
import com.cadenzauk.siesta.grammar.select.OrderBy;
import com.cadenzauk.siesta.grammar.select.SelectStatement;
import com.cadenzauk.siesta.grammar.select.WhereClause;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class Select<RT> implements TypedExpression<RT> {
    protected final Scope scope;
    protected final From from;
    protected final SelectStatement<RT> selectStatement = new Statement();
    private final RowMapper<RT> rowMapper;
    private final Projection projection;
    private final List<Ordering<?,?>> orderByClauses = new ArrayList<>();
    private Expression whereClause;

    protected Select(Scope scope, From from, RowMapper<RT> rowMapper, Projection projection) {
        this.scope = scope;
        this.from = from;
        this.rowMapper = rowMapper;
        this.projection = projection;
    }

    public <T> ExpressionBuilder<T,WhereClause<RT>> where(TypedExpression<T> lhs) {
        return ExpressionBuilder.of(lhs, this::setWhereClause);
    }

    public <T, R> ExpressionBuilder<T,WhereClause<RT>> where(Function1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::setWhereClause);
    }

    public <T, R> ExpressionBuilder<T,WhereClause<RT>> where(FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::setWhereClause);
    }

    public <T, R> ExpressionBuilder<T,WhereClause<RT>> where(String alias, Function1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::setWhereClause);
    }

    public <T, R> ExpressionBuilder<T,WhereClause<RT>> where(String alias, FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::setWhereClause);
    }

    public <T, R> ExpressionBuilder<T,WhereClause<RT>> where(Alias<R> alias, Function1<R,T> lhs) {
        return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), this::setWhereClause);
    }

    public <T, R> ExpressionBuilder<T,WhereClause<RT>> where(Alias<R> alias, FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), this::setWhereClause);
    }

    public <T> OrderBy<RT> orderBy(TypedExpression<T> expression) {
        return new OrderBy<>(selectStatement).then(expression);
    }

    public <T, R> OrderBy<RT> orderBy(Function1<R,T> columnGetter) {
        return new OrderBy<>(selectStatement).then(columnGetter);
    }

    public <T, R> OrderBy<RT> orderBy(FunctionOptional1<R,T> columnGetter) {
        return new OrderBy<>(selectStatement).then(columnGetter);
    }

    public <T, R> OrderBy<RT> orderBy(String alias, Function1<R,T> columnGetter) {
        return new OrderBy<>(selectStatement).then(alias, columnGetter);
    }

    public <T, R> OrderBy<RT> orderBy(String alias, FunctionOptional1<R,T> columnGetter) {
        return new OrderBy<>(selectStatement).then(alias, columnGetter);
    }

    public <T, R> OrderBy<RT> orderBy(Alias<R> alias, Function1<R,T> columnGetter) {
        return new OrderBy<>(selectStatement).then(alias, columnGetter);
    }

    public <T, R> OrderBy<RT> orderBy(Alias<R> alias, FunctionOptional1<R,T> columnGetter) {
        return new OrderBy<>(selectStatement).then(alias, columnGetter);
    }

    public <T> OrderBy<RT> orderBy(TypedExpression<T> expression, Order order) {
        return new OrderBy<>(selectStatement).then(expression, order);
    }

    public <T, R> OrderBy<RT> orderBy(Function1<R,T> columnGetter, Order order) {
        return new OrderBy<>(selectStatement).then(columnGetter, order);
    }

    public <T, R> OrderBy<RT> orderBy(FunctionOptional1<R,T> columnGetter, Order order) {
        return new OrderBy<>(selectStatement).then(columnGetter, order);
    }

    public <T, R> OrderBy<RT> orderBy(String alias, Function1<R,T> columnGetter, Order order) {
        return new OrderBy<>(selectStatement).then(alias, columnGetter, order);
    }

    public <T, R> OrderBy<RT> orderBy(String alias, FunctionOptional1<R,T> columnGetter, Order order) {
        return new OrderBy<>(selectStatement).then(alias, columnGetter, order);
    }

    public <T, R> OrderBy<RT> orderBy(Alias<R> alias, Function1<R,T> columnGetter, Order order) {
        return new OrderBy<>(selectStatement).then(alias, columnGetter, order);
    }

    public <T, R> OrderBy<RT> orderBy(Alias<R> alias, FunctionOptional1<R,T> columnGetter, Order order) {
        return new OrderBy<>(selectStatement).then(alias, columnGetter, order);
    }

    public <T> Select<T> select(TypedExpression<T> column) {
        return new Select<>(scope,
            from,
            column.rowMapper(scope, column.label(scope)),
            Projection.of(column));
    }

    public <T1, T2> Select<Tuple2<T1,T2>> select(TypedExpression<T1> column1, TypedExpression<T2> column2) {
        return new Select<>(scope,
            from,
            RowMappers.of(
                column1.rowMapper(scope, column1.label(scope)),
                column2.rowMapper(scope, column2.label(scope))),
            Projection.of(Projection.of(column1), Projection.of(column2)));
    }

    public <T1, T2, T3> Select<Tuple3<T1,T2,T3>> select(TypedExpression<T1> column1, TypedExpression<T2> column2, TypedExpression<T3> column3) {
        return new Select<>(scope,
            from,
            RowMappers.of(
                column1.rowMapper(scope, column1.label(scope)),
                column2.rowMapper(scope, column2.label(scope)),
                column3.rowMapper(scope, column3.label(scope))),
            Projection.of(Projection.of(column1), Projection.of(column2), Projection.of(column3)));
    }

    public <T1, T2, T3, T4> Select<Tuple4<T1,T2,T3,T4>> select(TypedExpression<T1> column1, TypedExpression<T2> column2, TypedExpression<T3> column3, TypedExpression<T4> column4) {
        return new Select<>(scope,
            from,
            RowMappers.of(
                column1.rowMapper(scope, column1.label(scope)),
                column2.rowMapper(scope, column2.label(scope)),
                column3.rowMapper(scope, column3.label(scope)),
                column4.rowMapper(scope, column4.label(scope))),
            Projection.of(Projection.of(column1), Projection.of(column2), Projection.of(column4), Projection.of(column4)));
    }

    public <T1, T2, T3, T4, T5> Select<Tuple5<T1,T2,T3,T4,T5>> select(TypedExpression<T1> column1, TypedExpression<T2> column2, TypedExpression<T3> column3, TypedExpression<T4> column4, TypedExpression<T5> column5) {
        return new Select<>(scope,
            from,
            RowMappers.of(
                column1.rowMapper(scope, column1.label(scope)),
                column2.rowMapper(scope, column2.label(scope)),
                column3.rowMapper(scope, column3.label(scope)),
                column4.rowMapper(scope, column4.label(scope)),
                column5.rowMapper(scope, column5.label(scope))),
            Projection.of(Projection.of(column1), Projection.of(column2), Projection.of(column4), Projection.of(column4)));
    }

    public <T, R> Select<T> select(Function1<R,T> methodReference) {
        return select(UnresolvedColumn.of(methodReference));
    }

    public <T, R> Select<T> select(Alias<R> alias, Function1<R,T> methodReference) {
        return select(ResolvedColumn.of(alias, methodReference));
    }

    public Optional<RT> optional(SqlExecutor sqlExecutor) {
        return OptionalUtil.ofOnly(list(sqlExecutor));
    }

    public List<RT> list(SqlExecutor sqlExecutor) {
        Object[] args = args().toArray();
        String sql = sql();
        System.out.println(sql);
        return sqlExecutor.query(sql, args, rowMapper());
    }

    public String sql() {
        return sqlImpl(scope);
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

    protected RowMapper<RT> rowMapper() {
        return rowMapper;
    }

    protected Projection projection() {
        return projection;
    }

    private <T> void addOrderBy(TypedExpression<T> expression, Order order) {
        orderByClauses.add(new Ordering<>(expression, order));
    }

    private WhereClause<RT> setWhereClause(Expression e) {
        whereClause = e;
        return new WhereClause<>(selectStatement);
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

    static <R> Select1<R> from(Database database, Alias<R> alias) {
        return new Select1<>(database, alias, alias.rowMapper(), Projection.of(alias));
    }

    static <R> Select1<R> from(Database database, Table<R> table) {
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
        public WhereClause<RT> setWhereClause(Expression e) {
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
