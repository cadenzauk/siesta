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
import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.From;
import com.cadenzauk.siesta.Order;
import com.cadenzauk.siesta.Ordering;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.SqlExecutor;
import com.cadenzauk.siesta.grammar.expression.BooleanExpression;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.google.common.collect.Iterables;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

class SelectStatement<RT> {
    private static final Logger LOG = LoggerFactory.getLogger(SelectStatement.class);

    protected final Scope scope;
    private final From from;
    private final RowMapper<RT> rowMapper;
    private final Projection projection;
    private BooleanExpression whereClause;
    private final List<TypedExpression<?>> groupByClauses = new ArrayList<>();
    private BooleanExpression havingClause;
    private final List<Ordering<?,?>> orderByClauses = new ArrayList<>();

    SelectStatement(Scope scope, From from, RowMapper<RT> rowMapper, Projection projection) {
        this.scope = scope;
        this.from = from;
        this.rowMapper = rowMapper;
        this.projection = projection;
    }

    String sql(Scope outerScope) {
        return "(" + sqlImpl(outerScope.plus(scope)) + ")";
    }

    String label() {
        return null;
    }

    Stream<Object> args(Scope scope) {
        return Stream.of(
            projection.args(scope),
            from.args(scope),
            whereClauseArgs(),
            groupByClauseArgs(),
            havingClauseArgs()
        ).flatMap(Function.identity());
    }

    Projection projection() {
        return projection;
    }

    Optional<RT> optional(SqlExecutor sqlExecutor) {
        return OptionalUtil.ofOnly(list(sqlExecutor));
    }

    Stream<RT> stream(SqlExecutor sqlExecutor, CompositeAutoCloseable autoCloseable) {
        Object[] args = args(scope).toArray();
        String sql = sql();
        LOG.debug(sql);
        return autoCloseable.add(sqlExecutor.stream(sql, args, rowMapper()));
    }

    RT single(SqlExecutor sqlExecutor) {
        return Iterables.getOnlyElement(list(sqlExecutor));
    }

    From from() {
        return from;
    }

    Scope scope() {
        return scope;
    }

    List<RT> list(SqlExecutor sqlExecutor) {
        Object[] args = args(scope).toArray();
        String sql = sql();
        LOG.debug(sql);
        return sqlExecutor.query(sql, args, rowMapper());
    }

    String sql() {
        return sqlImpl(scope);
    }

    RowMapper<RT> rowMapper() {
        return rowMapper;
    }

    <T> void addGroupBy(TypedExpression<T> expression) {
        groupByClauses.add(expression);
    }

    <T> void addOrderBy(TypedExpression<T> expression, Order order) {
        orderByClauses.add(new Ordering<>(expression, order));
    }

    InWhereExpectingAnd<RT> setWhereClause(BooleanExpression e) {
        whereClause = e;
        return new InWhereExpectingAnd<>(this);
    }

    void andWhere(BooleanExpression e) {
        whereClause = whereClause.appendAnd(e);
    }

    void orWhere(BooleanExpression e) {
        whereClause = whereClause.appendOr(e);
    }

    InHavingExpectingAnd<RT> setHavingClause(BooleanExpression e) {
        havingClause = e;
        return new InHavingExpectingAnd<>(this);
    }

    void andHaving(BooleanExpression e) {
        havingClause = havingClause.appendAnd(e);
    }

    void orHaving(BooleanExpression e) {
        havingClause = havingClause.appendOr(e);
    }

    @NotNull
    private Stream<Object> whereClauseArgs() {
        return whereClause == null
            ? Stream.empty()
            : whereClause.args(scope);
    }

    @NotNull
    private String whereClauseSql(Scope scope) {
        return whereClause == null
            ? ""
            : " where " + whereClause.sql(scope);
    }

    @NotNull
    private Stream<Object> groupByClauseArgs() {
        return groupByClauses
            .stream()
            .flatMap(g -> g.args(scope));
    }

    @NotNull
    private String groupByClauseSql(Scope scope) {
        return groupByClauses.isEmpty()
            ? ""
            : " group by " + groupByClauses.stream().map(ordering -> ordering.sql(scope)).collect(joining(", "));
    }

    @NotNull
    private Stream<Object> havingClauseArgs() {
        return havingClause == null
            ? Stream.empty()
            : havingClause.args(scope);
    }

    @NotNull
    private String havingClauseSql(Scope scope) {
        return havingClause == null
            ? ""
            : " having " + havingClause.sql(scope);
    }

    @NotNull
    private String orderByClauseSql(Scope scope) {
        return orderByClauses.isEmpty()
            ? ""
            : " order by " + orderByClauses.stream().map(ordering -> ordering.sql(scope)).collect(joining(", "));
    }

    private String sqlImpl(Scope actualScope) {
        return String.format("select %s from %s%s%s%s%s",
            projection().sql(actualScope),
            from.sql(actualScope),
            whereClauseSql(actualScope),
            groupByClauseSql(actualScope),
            havingClauseSql(actualScope),
            orderByClauseSql(actualScope));
    }
}
