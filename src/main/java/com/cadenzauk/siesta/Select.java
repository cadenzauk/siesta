/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.catalog.Table;
import com.cadenzauk.siesta.expression.AndExpression;
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
        public RowMapper<RT> rowMapper(Scope scope, String label) {
            return Select.this.rowMapper(scope, label);
        }
    }

    public class JoinOrWhereClauseBuilder extends ClauseBuilder {
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

    public class JoinClauseBuilder extends JoinOrWhereClauseBuilder {
        public <T, R> ExpressionBuilder<T,JoinClauseBuilder> and(Function1<R,T> lhs) {
            return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::onAnd);
        }

        public <T, R> ExpressionBuilder<T,JoinClauseBuilder> and(FunctionOptional1<R,T> lhs) {
            return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::onAnd);
        }

        public <T, R> ExpressionBuilder<T,JoinClauseBuilder> and(String alias, Function1<R,T> lhs) {
            return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::onAnd);
        }

        public <T, R> ExpressionBuilder<T,JoinClauseBuilder> and(String alias, FunctionOptional1<R,T> lhs) {
            return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::onAnd);
        }

        public <T, R> ExpressionBuilder<T,JoinClauseBuilder> and(Alias<R> alias, Function1<R,T> lhs) {
            return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), this::onAnd);
        }

        public <T, R> ExpressionBuilder<T,JoinClauseBuilder> and(Alias<R> alias, FunctionOptional1<R,T> lhs) {
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

        private JoinClauseBuilder onAnd(Expression rhs) {
            onClause = new AndExpression(onClause, rhs);
            return this;
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

    protected JoinClauseBuilder setOnClause(Expression e) {
        onClause = e;
        return new JoinClauseBuilder();
    }

    private WhereClauseBuilder setWhereClause(Expression e) {
        whereClause = e;
        return new WhereClauseBuilder();
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

    static <R> Select1<R,R> from(Database database, Alias<R> alias) {
        return new Select1<>(database, alias, alias.rowMapper(), Projection.of(alias));
    }

    static <R> Select1<R,R> from(Database database, Table<R> table) {
        return from(database, table.as(table.tableName()));
    }
}
