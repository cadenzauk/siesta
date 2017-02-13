/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import com.cadenzauk.core.function.MethodReference;
import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.catalog.Column;
import com.cadenzauk.siesta.catalog.Table;
import com.cadenzauk.siesta.expression.AndExpression;
import com.cadenzauk.siesta.expression.CompleteExpression;
import com.cadenzauk.siesta.expression.ResolvedColumn;
import com.cadenzauk.siesta.expression.UnresolvedColumn;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

public abstract class Select<RT> implements TypedExpression<RT> {
    protected final Scope scope;
    private final RowMapper<RT> rowMapper;
    private final Projection projection;
    private Expression whereClause;
    protected Expression onClause;

    private final List<Ordering<?,?>> orderByClauses = new ArrayList<>();

    public abstract class ClauseBuilder implements TypedExpression<RT> {
        public Optional<RT> optional(JdbcTemplate jdbcTemplate) {
            return Select.this.optional(jdbcTemplate);
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
        public RowMapper<RT> rowMapper(String label) {
            return Select.this.rowMapper(label);
        }
    }

    public class JoinOrWhereClauseBuilder extends ClauseBuilder {
        public <T> OrderByBuilder orderBy(TypedExpression<T> expression) {
            return new OrderByBuilder().then(expression);
        }

        public <T, R> OrderByBuilder orderBy(MethodReference<R,T> columnGetter) {
            return new OrderByBuilder().then(columnGetter);
        }

        public <T, R> OrderByBuilder orderBy(String alias, MethodReference<R,T> columnGetter) {
            return new OrderByBuilder().then(alias, columnGetter);
        }

        public <T, R> OrderByBuilder orderBy(Alias<R> alias, MethodReference<R,T> columnGetter) {
            return new OrderByBuilder().then(alias, columnGetter);
        }

        public <T> OrderByBuilder orderBy(TypedExpression<T> expression, Order order) {
            return new OrderByBuilder().then(expression, order);
        }

        public <T, R> OrderByBuilder orderBy(MethodReference<R,T> columnGetter, Order order) {
            return new OrderByBuilder().then(columnGetter, order);
        }

        public <T, R> OrderByBuilder orderBy(String alias, MethodReference<R,T> columnGetter, Order order) {
            return new OrderByBuilder().then(alias, columnGetter, order);
        }

        public <T, R> OrderByBuilder orderBy(Alias<R> alias, MethodReference<R,T> columnGetter, Order order) {
            return new OrderByBuilder().then(alias, columnGetter, order);
        }
    }

    public class JoinClauseBuilder extends JoinOrWhereClauseBuilder {
        public <T, R> JoinClauseBuilder and(String alias, Column<T, R> column, Condition<T> condition) {
            onClause = new AndExpression(onClause , new CompleteExpression<>(new UnresolvedColumn<>(alias, column), condition));
            return this;
        }

        public <T, R> WhereClauseBuilder where(Column<T, R> column, Condition<T> condition) {
            whereClause = new CompleteExpression<>(new UnresolvedColumn<>(column), condition);
            return new WhereClauseBuilder();
        }

        public <T> WhereClauseBuilder where(TypedExpression<T> lhs, Condition<T> rhs) {
            whereClause = new CompleteExpression<>(lhs, rhs);
            return new WhereClauseBuilder();
        }

        public <T, R> WhereClauseBuilder where(MethodReference<R,T> lhs, Condition<T> rhs) {
            whereClause = new CompleteExpression<>(UnresolvedColumn.of(lhs), rhs);
            return new WhereClauseBuilder();
        }

        public <T, R> WhereClauseBuilder where(String alias, MethodReference<R,T> lhs, Condition<T> rhs) {
            whereClause = new CompleteExpression<>(UnresolvedColumn.of(alias, lhs), rhs);
            return new WhereClauseBuilder();
        }

        public <T, R> WhereClauseBuilder where(Alias<R> alias, MethodReference<R,T> getterReference, Condition<T> condition) {
            whereClause = new CompleteExpression<>(ResolvedColumn.of(alias, getterReference), condition);
            return new WhereClauseBuilder();
        }
    }

    public class WhereClauseBuilder extends JoinOrWhereClauseBuilder {
        public <T> WhereClauseBuilder and(TypedExpression<T> lhs, Condition<T> rhs) {
            whereClause = new AndExpression(whereClause, new CompleteExpression<>(lhs, rhs));
            return this;
        }

        public <T, R> WhereClauseBuilder and(MethodReference<R,T> lhs, Condition<T> rhs) {
            whereClause = new AndExpression(whereClause, new CompleteExpression<>(UnresolvedColumn.of(lhs), rhs));
            return this;
        }

        public <T, R> WhereClauseBuilder and(String alias, MethodReference<R,T> lhs, Condition<T> rhs) {
            whereClause = new AndExpression(whereClause, new CompleteExpression<>(UnresolvedColumn.of(alias, lhs), rhs));
            return this;
        }

        public <T, R> WhereClauseBuilder and(Alias<R> alias, MethodReference<R,T> lhs, Condition<T> rhs) {
            whereClause = new AndExpression(whereClause, new CompleteExpression<>(ResolvedColumn.of(alias, lhs), rhs));
            return this;
        }
    }

    public class OrderByBuilder extends ClauseBuilder {
        public <T> OrderByBuilder then(TypedExpression<T> column) {
            orderByClauses.add(new Ordering<>(column, Order.ASCENDING));
            return this;
        }

        public <T, R> OrderByBuilder then(MethodReference<R,T> column) {
            orderByClauses.add(new Ordering<>(UnresolvedColumn.of(column), Order.ASCENDING));
            return this;
        }

        public <T, R> OrderByBuilder then(String alias, MethodReference<R,T> column) {
            orderByClauses.add(new Ordering<>(UnresolvedColumn.of(alias, column), Order.ASCENDING));
            return this;
        }

        public <T, R> OrderByBuilder then(Alias<R> alias, MethodReference<R,T> column) {
            orderByClauses.add(new Ordering<>(ResolvedColumn.of(alias, column), Order.ASCENDING));
            return this;
        }

        public <T> OrderByBuilder then(TypedExpression<T> column, Order order) {
            orderByClauses.add(new Ordering<>(column, order));
            return this;
        }

        public <T, R> OrderByBuilder then(MethodReference<R,T> column, Order order) {
            orderByClauses.add(new Ordering<>(UnresolvedColumn.of(column), order));
            return this;
        }

        public <T, R> OrderByBuilder then(String alias, MethodReference<R,T> column, Order order) {
            orderByClauses.add(new Ordering<>(UnresolvedColumn.of(alias, column), order));
            return this;
        }

        public <T, R> OrderByBuilder then(Alias<R> alias, MethodReference<R,T> column, Order order) {
            orderByClauses.add(new Ordering<>(ResolvedColumn.of(alias, column), order));
            return this;
        }
    }

    protected Select(Scope scope, RowMapper<RT> rowMapper, Projection projection) {
        this.scope = scope;
        this.rowMapper = rowMapper;
        this.projection = projection;
    }

    protected RowMapper<RT> rowMapper() {
        return rowMapper;
    }

    protected Projection projection() {
        return projection;
    }

    public <T> WhereClauseBuilder where(TypedExpression<T> lhs, Condition<T> condition) {
        whereClause = new CompleteExpression<>(lhs, condition);
        return new WhereClauseBuilder();
    }

    public <T,R> WhereClauseBuilder where(MethodReference<R,T> lhs, Condition<T> condition) {
        whereClause = new CompleteExpression<>(UnresolvedColumn.of(lhs), condition);
        return new WhereClauseBuilder();
    }

    public <T,R> WhereClauseBuilder where(String alias, MethodReference<R,T> lhs, Condition<T> condition) {
        whereClause = new CompleteExpression<>(UnresolvedColumn.of(alias, lhs), condition);
        return new WhereClauseBuilder();
    }

    public <T,R> WhereClauseBuilder where(Alias<R> alias, MethodReference<R,T> lhs, Condition<T> condition) {
        whereClause = new CompleteExpression<>(ResolvedColumn.of(alias, lhs), condition);
        return new WhereClauseBuilder();
    }

    public <T> OrderByBuilder orderBy(TypedExpression<T> expression) {
        return new OrderByBuilder().then(expression);
    }

    public <T, R> OrderByBuilder orderBy(MethodReference<R,T> columnGetter) {
        return new OrderByBuilder().then(columnGetter);
    }

    public <T, R> OrderByBuilder orderBy(String alias, MethodReference<R,T> columnGetter) {
        return new OrderByBuilder().then(alias, columnGetter);
    }

    public <T, R> OrderByBuilder orderBy(Alias<R> alias, MethodReference<R,T> columnGetter) {
        return new OrderByBuilder().then(alias, columnGetter);
    }

    public <T> OrderByBuilder orderBy(TypedExpression<T> expression, Order order) {
        return new OrderByBuilder().then(expression, order);
    }

    public <T, R> OrderByBuilder orderBy(MethodReference<R,T> columnGetter, Order order) {
        return new OrderByBuilder().then(columnGetter, order);
    }

    public <T, R> OrderByBuilder orderBy(String alias, MethodReference<R,T> columnGetter, Order order) {
        return new OrderByBuilder().then(alias, columnGetter, order);
    }

    public <T, R> OrderByBuilder orderBy(Alias<R> alias, MethodReference<R,T> columnGetter, Order order) {
        return new OrderByBuilder().then(alias, columnGetter, order);
    }

    public abstract List<RT> list(JdbcTemplate jdbcTemplate);

    public Optional<RT> optional(JdbcTemplate jdbcTemplate) {
        return OptionalUtil.of(list(jdbcTemplate));
    }

    public abstract String sql();

    @NotNull
    protected Object[] onClauseArgs() {
        return onClause == null
            ? new Object[0]
            : onClause.args().toArray();
    }
    @NotNull
    protected String whereClauseSql(Scope scope) {
        return whereClause == null
            ? ""
            : " where " + whereClause.sql(scope);
    }

    @NotNull
    protected Object[] whereClauseArgs() {
        return whereClause == null
            ? new Object[0]
            : whereClause.args().toArray();
    }

    @NotNull
    protected String orderByClauseSql(Scope scope) {
        return orderByClauses.isEmpty()
            ? ""
            : " order by " + orderByClauses.stream().map(ordering -> ordering.sql(scope)).collect(joining(", "));
    }

    public static <R> Select1<R,R> from(Alias<R> alias) {
        return new Select1<>(alias, alias.rowMapper(), Projection.of(alias));
    }

    public static <R> Select1<R, R> from(Table<R> table) {
        return from(table.as(table.tableName()));
    }
}
