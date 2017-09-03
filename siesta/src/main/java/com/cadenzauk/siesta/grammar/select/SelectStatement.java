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
import com.cadenzauk.core.tuple.Tuple;
import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.From;
import com.cadenzauk.siesta.Order;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.Transaction;
import com.cadenzauk.siesta.grammar.expression.BooleanExpression;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.google.common.collect.Iterables;
import com.google.common.reflect.TypeToken;
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
    private final List<CommonTableExpression<?>> commonTableExpressions = new ArrayList<>();
    private final TypeToken<RT> rowType;
    private final From from;
    private final RowMapper<RT> rowMapper;
    private final Projection projection;
    private BooleanExpression whereClause;
    private final List<TypedExpression<?>> groupByClauses = new ArrayList<>();
    private BooleanExpression havingClause;
    private final List<Tuple2<UnionType,SelectStatement<RT>>> unions = new ArrayList<>();
    private final List<OrderingClause> orderByClauses = new ArrayList<>();
    private Optional<Long> fetchFirst = Optional.empty();

    SelectStatement(Scope scope, TypeToken<RT> rowType, From from, RowMapper<RT> rowMapper, Projection projection) {
        this.scope = scope;
        this.rowType = rowType;
        this.from = from;
        this.rowMapper = rowMapper;
        this.projection = projection;
    }

    public TypeToken<RT> rowType() {
        return rowType;
    }

    public Stream<CommonTableExpression<?>> commonTableExpressions() {
        return commonTableExpressions.stream();
    }

    void addCommonTableExpression(CommonTableExpression<?> cte) {
        if (!commonTableExpressions.contains(cte)) {
            commonTableExpressions.add(cte);
        }
    }

    void addUnion(SelectStatement<RT> next, UnionType unionType) {
        unions.add(Tuple.of(unionType, next));
    }

    String sql(Scope outerScope) {
        return "(" + sqlImpl(outerScope) + ")";
    }

    String label() {
        return null;
    }

    Stream<Object> args(Scope outerScope) {
        Scope innerScope = outerScope.plus(scope);
        return Stream.of(
            cteArgs(outerScope),
            projection.args(innerScope),
            from.args(innerScope),
            whereClauseArgs(innerScope),
            groupByClauseArgs(innerScope),
            havingClauseArgs(innerScope),
            unionsArgs(innerScope)
        ).flatMap(Function.identity());
    }

    Projection projection() {
        return projection;
    }

    void fetchFirst(long i) {
        fetchFirst = Optional.of(i);
    }

    List<RT> list(Transaction transaction) {
        Object[] args = args(scope).toArray();
        String sql = sql();
        LOG.debug(sql);
        return transaction.query(sql, args, rowMapper());
    }

    Optional<RT> optional(Transaction transaction) {
        return OptionalUtil.ofOnly(list(transaction));
    }

    Stream<RT> stream(Transaction transaction, CompositeAutoCloseable autoCloseable) {
        Object[] args = args(scope).toArray();
        String sql = sql();
        LOG.debug(sql);
        return autoCloseable.add(transaction.stream(sql, args, rowMapper()));
    }

    RT single(Transaction transaction) {
        return Iterables.getOnlyElement(list(transaction));
    }

    From from() {
        return from;
    }

    Scope scope() {
        return scope;
    }

    String sql() {
        return sqlImpl(scope.empty());
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

    void addOrderBy(int columnNumber, Order order) {
        orderByClauses.add(new OrderByColumnNumber(columnNumber, order));
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

    private String commonTableExpressionSql(Scope actualScope) {
        return commonTableExpressions.isEmpty() || !actualScope.isOutermost()
            ? ""
            : "with " + commonTableExpressions().map(cte -> cte.sql(actualScope)).collect(joining(", "));
    }

    void andHaving(BooleanExpression e) {
        havingClause = havingClause.appendAnd(e);
    }

    void orHaving(BooleanExpression e) {
        havingClause = havingClause.appendOr(e);
    }

    @NotNull
    private Stream<Object> cteArgs(Scope actualScope) {
        return actualScope.isOutermost()
            ? commonTableExpressions().flatMap(cte -> cte.args(actualScope))
            : Stream.empty();
    }

    @NotNull
    private Stream<Object> whereClauseArgs(Scope actualScope) {
        return whereClause == null
            ? Stream.empty()
            : whereClause.args(actualScope);
    }

    @NotNull
    private String whereClauseSql(Scope actualScope) {
        return whereClause == null
            ? ""
            : " where " + whereClause.sql(actualScope);
    }

    @NotNull
    private Stream<Object> groupByClauseArgs(Scope actualScope) {
        return groupByClauses
            .stream()
            .flatMap(g -> g.args(actualScope));
    }

    @NotNull
    private String groupByClauseSql(Scope actualScope) {
        return groupByClauses.isEmpty()
            ? ""
            : " group by " + groupByClauses.stream().map(ordering -> ordering.sql(actualScope)).collect(joining(", "));
    }

    @NotNull
    private Stream<Object> havingClauseArgs(Scope actualScope) {
        return havingClause == null
            ? Stream.empty()
            : havingClause.args(actualScope);
    }

    @NotNull
    private Stream<Object> unionsArgs(Scope actualScope) {
        return unions.stream().flatMap(u -> u.item2().args(actualScope));
    }

    @NotNull
    private String havingClauseSql(Scope actualScope) {
        return havingClause == null
            ? ""
            : " having " + havingClause.sql(actualScope);
    }

    @NotNull
    private String unionsSql(Scope actualScope) {
        return unions.isEmpty()
            ? ""
            : " " + unions.stream().map(t -> t.map((u, s) -> u.format(s.sqlImpl(actualScope)))).collect(joining(" "));
    }

    @NotNull
    private String orderByClauseSql(Scope actualScope) {
        return orderByClauses.isEmpty()
            ? ""
            : " order by " + orderByClauses.stream().map(ordering -> ordering.sql(actualScope)).collect(joining(", "));
    }

    private String sqlImpl(Scope outerScope) {
        Scope innerScope = outerScope.plus(scope);
        String sql = String.format("%sselect %s%s%s%s%s%s%s",
            commonTableExpressionSql(outerScope),
            projection().sql(innerScope),
            from.sql(innerScope),
            whereClauseSql(innerScope),
            groupByClauseSql(innerScope),
            havingClauseSql(innerScope),
            unionsSql(innerScope),
            orderByClauseSql(innerScope));
        return fetchFirst.map(n -> scope.dialect().fetchFirst(sql, n)).orElse(sql);
    }
}
