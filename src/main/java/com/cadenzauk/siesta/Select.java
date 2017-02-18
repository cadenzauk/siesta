/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.siesta;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.catalog.Table;
import com.cadenzauk.siesta.expression.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class Select<RT> implements TypedExpression<RT> {
    protected final Scope scope;
    protected final From from;
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

    public <T> ExpressionBuilder<T,WhereClauseBuilder> where(TypedExpression<T> lhs) {
        return ExpressionBuilder.of(lhs, this::setWhereClause);
    }

    public <T, R> ExpressionBuilder<T,WhereClauseBuilder> where(Function1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::setWhereClause);
    }

    public <T, R> ExpressionBuilder<T,WhereClauseBuilder> where(FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::setWhereClause);
    }

    public <T, R> ExpressionBuilder<T,WhereClauseBuilder> where(String alias, Function1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::setWhereClause);
    }

    public <T, R> ExpressionBuilder<T,WhereClauseBuilder> where(String alias, FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::setWhereClause);
    }

    public <T, R> ExpressionBuilder<T,WhereClauseBuilder> where(Alias<R> alias, Function1<R,T> lhs) {
        return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), this::setWhereClause);
    }

    public <T, R> ExpressionBuilder<T,WhereClauseBuilder> where(Alias<R> alias, FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), this::setWhereClause);
    }

    public <T> OrderByBuilder orderBy(TypedExpression<T> expression) {
        return new OrderByBuilder().then(expression);
    }

    public <T, R> OrderByBuilder orderBy(Function1<R,T> columnGetter) {
        return new OrderByBuilder().then(columnGetter);
    }

    public <T, R> OrderByBuilder orderBy(String alias, Function1<R,T> columnGetter) {
        return new OrderByBuilder().then(alias, columnGetter);
    }

    public <T, R> OrderByBuilder orderBy(Alias<R> alias, Function1<R,T> columnGetter) {
        return new OrderByBuilder().then(alias, columnGetter);
    }

    public <T> OrderByBuilder orderBy(TypedExpression<T> expression, Order order) {
        return new OrderByBuilder().then(expression, order);
    }

    public <T, R> OrderByBuilder orderBy(Function1<R,T> columnGetter, Order order) {
        return new OrderByBuilder().then(columnGetter, order);
    }

    public <T, R> OrderByBuilder orderBy(String alias, Function1<R,T> columnGetter, Order order) {
        return new OrderByBuilder().then(alias, columnGetter, order);
    }

    public <T, R> OrderByBuilder orderBy(Alias<R> alias, Function1<R,T> columnGetter, Order order) {
        return new OrderByBuilder().then(alias, columnGetter, order);
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
                column1.rowMapper(scope, column2.label(scope)),
                column2.rowMapper(scope, column2.label(scope))),
            Projection.of(Projection.of(column1), Projection.of(column2)));
    }

    public <T,R> Select<T> select(Function1<R,T> methodReference) {
        return select(UnresolvedColumn.of(methodReference));
    }

    public <T,R> Select<T> select(Alias<R> alias, Function1<R,T> methodReference) {
        return select(ResolvedColumn.of(alias, methodReference));
    }

    public Optional<RT> optional(SqlExecutor jdbcTemplate) {
        return OptionalUtil.of(list(jdbcTemplate));
    }

    public List<RT> list(SqlExecutor jdbcTemplate) {
        Object[] args = args().toArray();
        String sql = sql();
        System.out.println(sql);
        return jdbcTemplate.query(sql, args, rowMapper());
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
        return Stream.concat(from.args(),whereClauseArgs());
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

    private WhereClauseBuilder setWhereClause(Expression e) {
        whereClause = e;
        return new WhereClauseBuilder();
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

    public abstract class ClauseBuilder implements TypedExpression<RT> {
        public List<RT> list(SqlExecutor sqlExecutor) {
            return Select.this.list(sqlExecutor);
        }

        public Optional<RT> optional(SqlExecutor sqlExecutor) {
            return Select.this.optional(sqlExecutor);
        }

        public String sql() {
            return Select.this.sql();
        }

        @Override
        public String sql(Scope scope) {
            return Select.this.sql(scope);
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
        public Stream<Object> args() {
            return Select.this.args();
        }

    }

    public abstract class JoinOrWhereClauseBuilder extends ClauseBuilder {
        public <T> OrderByBuilder orderBy(TypedExpression<T> expression) {
            return new OrderByBuilder().then(expression);
        }

        public <T, R> OrderByBuilder orderBy(Function1<R,T> columnGetter) {
            return new OrderByBuilder().then(columnGetter);
        }

        public <T, R> OrderByBuilder orderBy(String alias, Function1<R,T> columnGetter) {
            return new OrderByBuilder().then(alias, columnGetter);
        }

        public <T, R> OrderByBuilder orderBy(Alias<R> alias, Function1<R,T> columnGetter) {
            return new OrderByBuilder().then(alias, columnGetter);
        }

        public <T> OrderByBuilder orderBy(TypedExpression<T> expression, Order order) {
            return new OrderByBuilder().then(expression, order);
        }

        public <T, R> OrderByBuilder orderBy(Function1<R,T> columnGetter, Order order) {
            return new OrderByBuilder().then(columnGetter, order);
        }

        public <T, R> OrderByBuilder orderBy(String alias, Function1<R,T> columnGetter, Order order) {
            return new OrderByBuilder().then(alias, columnGetter, order);
        }

        public <T, R> OrderByBuilder orderBy(Alias<R> alias, Function1<R,T> columnGetter, Order order) {
            return new OrderByBuilder().then(alias, columnGetter, order);
        }
    }

    public abstract class JoinClauseStartBuilder<S extends JoinClauseStartBuilder<S,J>, J extends JoinClauseBuilder<J>> {
        private final Function<S,J> newJoinClause;

        protected JoinClauseStartBuilder(Function<S,J> newJoinClause) {
            this.newJoinClause = newJoinClause;
        }

        public <T> ExpressionBuilder<T,J> on(TypedExpression<T> lhs) {
            return ExpressionBuilder.of(lhs, this::setOnClause);
        }

        public <T, R> ExpressionBuilder<T,J> on(Function1<R,T> lhs) {
            return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::setOnClause);
        }

        public <T, R> ExpressionBuilder<T,J> on(FunctionOptional1<R,T> lhs) {
            return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::setOnClause);
        }

        public <T, R> ExpressionBuilder<T,J> on(String alias, Function1<R,T> lhs) {
            return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::setOnClause);
        }

        public <T, R> ExpressionBuilder<T,J> on(String alias, FunctionOptional1<R,T> lhs) {
            return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::setOnClause);
        }

        public <T, R> ExpressionBuilder<T,J> on(Alias<R> alias, Function1<R,T> lhs) {
            return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), this::setOnClause);
        }

        public <T, R> ExpressionBuilder<T,J> on(Alias<R> alias, FunctionOptional1<R,T> lhs) {
            return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), this::setOnClause);
        }

        @SuppressWarnings("unchecked")
        private J setOnClause(Expression e) {
            from.on(e);
            return newJoinClause.apply((S)this);
        }
    }

    public abstract class JoinClauseBuilder<S extends JoinClauseBuilder<S>> extends JoinOrWhereClauseBuilder {
        public <T, R> ExpressionBuilder<T,S> and(Function1<R,T> lhs) {
            return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::onAnd);
        }

        public <T, R> ExpressionBuilder<T,S> and(FunctionOptional1<R,T> lhs) {
            return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::onAnd);
        }

        public <T, R> ExpressionBuilder<T,S> and(String alias, Function1<R,T> lhs) {
            return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs),this::onAnd);
        }

        public <T, R> ExpressionBuilder<T,S> and(String alias, FunctionOptional1<R,T> lhs) {
            return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::onAnd);
        }

        public <T, R> ExpressionBuilder<T,S> and(Alias<R> alias, Function1<R,T> lhs) {
            return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), this::onAnd);
        }

        public <T, R> ExpressionBuilder<T,S> and(Alias<R> alias, FunctionOptional1<R,T> lhs) {
            return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), this::onAnd);
        }

        public <T, R> ExpressionBuilder<T,WhereClauseBuilder> where(Function1<R,T> lhs) {
            return ExpressionBuilder.of(UnresolvedColumn.of(lhs), Select.this::setWhereClause);
        }

        public <T, R> ExpressionBuilder<T,WhereClauseBuilder> where(FunctionOptional1<R,T> lhs) {
            return ExpressionBuilder.of(UnresolvedColumn.of(lhs), Select.this::setWhereClause);
        }

        public <T, R> ExpressionBuilder<T,WhereClauseBuilder> where(String alias, Function1<R,T> lhs) {
            return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), Select.this::setWhereClause);
        }

        public <T, R> ExpressionBuilder<T,WhereClauseBuilder> where(String alias, FunctionOptional1<R,T> lhs) {
            return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), Select.this::setWhereClause);
        }

        public <T, R> ExpressionBuilder<T,WhereClauseBuilder> where(Alias<R> alias, Function1<R,T> lhs) {
            return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), Select.this::setWhereClause);
        }

        public <T, R> ExpressionBuilder<T,WhereClauseBuilder> where(Alias<R> alias, FunctionOptional1<R,T> lhs) {
            return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), Select.this::setWhereClause);
        }

        private S onAnd(Expression rhs) {
            from.on(new AndExpression(from.on(), rhs));
            return (S) this;
        }
    }

    public class WhereClauseBuilder extends JoinOrWhereClauseBuilder {
        public <T> ExpressionBuilder<T,WhereClauseBuilder> where(TypedExpression<T> lhs) {
            return ExpressionBuilder.of(lhs, this::andWhere);
        }

        public <T, R> ExpressionBuilder<T,WhereClauseBuilder> and(Function1<R,T> lhs) {
            return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::andWhere);
        }

        public <T, R> ExpressionBuilder<T,WhereClauseBuilder> and(FunctionOptional1<R,T> lhs) {
            return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::andWhere);
        }

        public <T, R> ExpressionBuilder<T,WhereClauseBuilder> and(String alias, Function1<R,T> lhs) {
            return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::andWhere);
        }

        public <T, R> ExpressionBuilder<T,WhereClauseBuilder> and(String alias, FunctionOptional1<R,T> lhs) {
            return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::andWhere);
        }

        public <T, R> ExpressionBuilder<T,WhereClauseBuilder> and(Alias<R> alias, Function1<R,T> lhs) {
            return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), this::andWhere);
        }

        public <T, R> ExpressionBuilder<T,WhereClauseBuilder> and(Alias<R> alias, FunctionOptional1<R,T> lhs) {
            return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), this::andWhere);
        }

        private WhereClauseBuilder andWhere(Expression newClause) {
            whereClause = new AndExpression(whereClause, newClause);
            return this;
        }
    }

    public class OrderByBuilder extends ClauseBuilder {
        public <T> OrderByBuilder then(TypedExpression<T> column) {
            orderByClauses.add(new Ordering<>(column, Order.ASCENDING));
            return this;
        }

        public <T, R> OrderByBuilder then(Function1<R,T> column) {
            orderByClauses.add(new Ordering<>(UnresolvedColumn.of(column), Order.ASCENDING));
            return this;
        }

        public <T, R> OrderByBuilder then(String alias, Function1<R,T> column) {
            orderByClauses.add(new Ordering<>(UnresolvedColumn.of(alias, column), Order.ASCENDING));
            return this;
        }

        public <T, R> OrderByBuilder then(Alias<R> alias, Function1<R,T> column) {
            orderByClauses.add(new Ordering<>(ResolvedColumn.of(alias, column), Order.ASCENDING));
            return this;
        }

        public <T> OrderByBuilder then(TypedExpression<T> column, Order order) {
            orderByClauses.add(new Ordering<>(column, order));
            return this;
        }

        public <T, R> OrderByBuilder then(Function1<R,T> column, Order order) {
            orderByClauses.add(new Ordering<>(UnresolvedColumn.of(column), order));
            return this;
        }

        public <T, R> OrderByBuilder then(String alias, Function1<R,T> column, Order order) {
            orderByClauses.add(new Ordering<>(UnresolvedColumn.of(alias, column), order));
            return this;
        }

        public <T, R> OrderByBuilder then(Alias<R> alias, Function1<R,T> column, Order order) {
            orderByClauses.add(new Ordering<>(ResolvedColumn.of(alias, column), order));
            return this;
        }
    }
}
