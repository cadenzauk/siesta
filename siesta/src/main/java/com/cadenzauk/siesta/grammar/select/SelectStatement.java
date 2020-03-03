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
import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.core.tuple.Tuple;
import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.core.util.IterableUtil;
import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.From;
import com.cadenzauk.siesta.IsolationLevel;
import com.cadenzauk.siesta.LockLevel;
import com.cadenzauk.siesta.Order;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.SqlExecutor;
import com.cadenzauk.siesta.Transaction;
import com.cadenzauk.siesta.grammar.LabelGenerator;
import com.cadenzauk.siesta.grammar.expression.BooleanExpression;
import com.cadenzauk.siesta.grammar.expression.BooleanExpressionChain;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

class SelectStatement<RT> {
    private static final Logger LOG = LoggerFactory.getLogger(SelectStatement.class);

    protected final Scope scope;
    private final LabelGenerator labelGenerator = new LabelGenerator("select_");
    private final List<CommonTableExpression<?>> commonTableExpressions = new ArrayList<>();
    private final TypeToken<RT> rowType;
    private final From from;
    private final Projection<RT> projection;
    private final BooleanExpressionChain whereClause = new BooleanExpressionChain();
    private final List<TypedExpression<?>> groupByClauses = new ArrayList<>();
    private final BooleanExpressionChain havingClause = new BooleanExpressionChain();
    private final List<Tuple2<UnionType,SelectStatement<RT>>> unions = new ArrayList<>();
    private final List<OrderingClause> orderByClauses = new ArrayList<>();
    private Optional<Long> fetchFirst = Optional.empty();
    private IsolationLevel isolationLevel = IsolationLevel.UNSPECIFIED;
    private Optional<LockLevel> keepLocks = Optional.empty();

    SelectStatement(Scope scope, TypeToken<RT> rowType, From from, Projection<RT> projection) {
        this.scope = scope;
        this.rowType = rowType;
        this.from = from;
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

    String label(Scope outerScope) {
        return labelGenerator.label(outerScope);
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

    Projection<RT> projection() {
        return projection;
    }

    void fetchFirst(long i) {
        fetchFirst = Optional.of(i);
    }

    void withIsolation(IsolationLevel level) {
        isolationLevel = level;
    }

    void keepLocks(LockLevel level) {
        keepLocks = Optional.of(level);
    }

    List<RT> list(SqlExecutor sqlExecutor) {
        Object[] args = args(scope).toArray();
        String sql = sql();
        LOG.debug(sql);
        return scope.database().execute(sql, () -> sqlExecutor.query(sql, args, rowMapper()));
    }

    List<RT> list(Transaction transaction) {
        Object[] args = args(scope).toArray();
        String sql = sql();
        LOG.debug(sql);
        return scope.database().execute(sql, () -> transaction.query(sql, args, rowMapper()));
    }

    CompletableFuture<List<RT>> listAsync(SqlExecutor sqlExecutor) {
        try (Transaction transaction = sqlExecutor.beginTransaction()) {
            try {
                return listAsync(transaction);
            } finally {
                transaction.commit();
            }
        }
    }

    CompletableFuture<List<RT>> listAsync(Transaction transaction) {
        Object[] args = args(scope).toArray();
        String sql = sql();
        LOG.debug(sql);
        return transaction.queryAsync(sql, args, rowMapper())
            .exceptionally(e -> scope.database().translateException(sql, e));
    }

    Optional<RT> optional(SqlExecutor sqlExecutor) {
        return OptionalUtil.ofOnly(list(sqlExecutor));
    }

    Optional<RT> optional(Transaction transaction) {
        return OptionalUtil.ofOnly(list(transaction));
    }

    CompletableFuture<Optional<RT>> optionalAsync(SqlExecutor sqlExecutor) {
        return listAsync(sqlExecutor).thenApply(OptionalUtil::ofOnly);
    }

    CompletableFuture<Optional<RT>> optionalAsync(Transaction transaction) {
        return listAsync(transaction).thenApply(OptionalUtil::ofOnly);
    }

    Stream<RT> stream(SqlExecutor sqlExecutor) {
        Object[] args = args(scope).toArray();
        String sql = sql();
        LOG.debug(sql);
        return scope.database().execute(sql, () -> sqlExecutor.stream(sql, args, rowMapper()));
    }

    Stream<RT> stream(Transaction transaction) {
        Object[] args = args(scope).toArray();
        String sql = sql();
        LOG.debug(sql);
        return scope.database().execute(sql, () -> transaction.stream(sql, args, rowMapper()));
    }

    Stream<RT> stream(SqlExecutor sqlExecutor, CompositeAutoCloseable autoCloseable) {
        return autoCloseable.add(stream(sqlExecutor));
    }

    Stream<RT> stream(Transaction transaction, CompositeAutoCloseable autoCloseable) {
        return autoCloseable.add(stream(transaction));
    }

    RT single(SqlExecutor sqlExecutor) {
        return IterableUtil.single(list(sqlExecutor));
    }

    RT single(Transaction transaction) {
        return IterableUtil.single(list(transaction));
    }

    CompletableFuture<RT> singleAsync(SqlExecutor sqlExecutor) {
        return listAsync(sqlExecutor).thenApply(IterableUtil::single);
    }

    CompletableFuture<RT> singleAsync(Transaction transaction) {
        return listAsync(transaction).thenApply(IterableUtil::single);
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
        return projection.rowMapperFactory(scope).rowMapper(Optional.empty());
    }

    RowMapperFactory<RT> rowMapperFactory() {
        return projection.rowMapperFactory(scope);
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
        whereClause.start(e);
        return new InWhereExpectingAnd<>(this);
    }

    void andWhere(BooleanExpression e) {
        whereClause.appendAnd(e);
    }

    void orWhere(BooleanExpression e) {
        whereClause.appendOr(e);
    }

    InHavingExpectingAnd<RT> setHavingClause(BooleanExpression e) {
        havingClause.start(e);
        return new InHavingExpectingAnd<>(this);
    }

    private String commonTableExpressionSql(Scope actualScope) {
        return commonTableExpressions.isEmpty() || !actualScope.isOutermost()
            ? ""
            : "with " + commonTableExpressions().map(cte -> cte.sql(actualScope)).collect(joining(", "));
    }

    void andHaving(BooleanExpression e) {
        havingClause.appendAnd(e);
    }

    void orHaving(BooleanExpression e) {
        havingClause.appendOr(e);
    }

    @NotNull
    private Stream<Object> cteArgs(Scope actualScope) {
        return actualScope.isOutermost()
            ? commonTableExpressions().flatMap(cte -> cte.args(actualScope))
            : Stream.empty();
    }

    @NotNull
    private Stream<Object> whereClauseArgs(Scope actualScope) {
        return whereClause.args(actualScope);
    }

    @NotNull
    private String whereClauseSql(Scope actualScope) {
        return whereClause.sql(actualScope, " where ");
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
        return havingClause.args(actualScope);
    }

    @NotNull
    private String havingClauseSql(Scope actualScope) {
        return havingClause.sql(actualScope, " having ");
    }

    @NotNull
    private Stream<Object> unionsArgs(Scope actualScope) {
        return unions.stream().flatMap(u -> u.item2().args(actualScope));
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

    String sqlImpl(Scope outerScope) {
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
        sql = fetchFirstSql(sql);
        sql = isolationLevelSql(sql);
        return sql;
    }

    private String fetchFirstSql(String sql) {
        return fetchFirst.map(n -> scope.dialect().fetchFirst(sql, n)).orElse(sql);
    }

    private String isolationLevelSql(String sql) {
        return scope.dialect().isolationLevelSql(sql, isolationLevel, keepLocks);
    }
}
