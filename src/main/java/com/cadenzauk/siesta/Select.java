/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import com.cadenzauk.core.tuple.Tuple2;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.joining;

public abstract class Select<RT> {
    protected Expression whereClause = null;
    protected final Scope scope;
    protected final List<OrderBy<?,?>> orderByClauses = new ArrayList<>();
    protected Expression onClause;

    public abstract class ClauseBuilder {
        public Optional<RT> optional(JdbcTemplate jdbcTemplate) {
            return Select.this.optional(jdbcTemplate);
        }

        public String sql() {
            return Select.this.sql();
        }
    }

    public class JoinClauseBuilder extends ClauseBuilder {
        public <T, R> JoinClauseBuilder and(Column<T, R> column, TestSupplier<T> testSupplier) {
            onClause = new AndExpression(onClause , new ColumnTest<>(scope.findAlias(column.rowClass()), column, testSupplier.get(scope)));
            return this;
        }

        public <T, R> JoinClauseBuilder and(String alias, Column<T, R> column, TestSupplier<T> testSupplier) {
            onClause = new AndExpression(onClause , new ColumnTest<>(scope.findAlias(column.rowClass(), alias), column, testSupplier.get(scope)));
            return this;
        }

        public <T, R> WhereClauseBuilder where(Column<T, R> column, TestSupplier<T> testSupplier) {
            whereClause = new ColumnTest<>(scope.findAlias(column.rowClass()), column, testSupplier.get(scope));
            return new WhereClauseBuilder();
        }

        public <T, R> WhereClauseBuilder where(String alias, Column<T, R> column, TestSupplier<T> testSupplier) {
            whereClause = new ColumnTest<>(scope.findAlias(column.rowClass(), alias), column, testSupplier.get(scope));
            return new WhereClauseBuilder();
        }

        public <T, R> OrderByBuilder orderBy(Column<T,R> column) {
            return new OrderByBuilder().then(column);
        }

        public <T, R> OrderByBuilder orderBy(String alias, Column<T,R> column) {
            return new OrderByBuilder().then(alias, column);
        }

        public <T, R> OrderByBuilder orderBy(Column<T,R> column, Order order) {
            return new OrderByBuilder().then(column, order);
        }

        public <T, R> OrderByBuilder orderBy(String alias, Column<T,R> column, Order order) {
            return new OrderByBuilder().then(alias, column, order);
        }
    }

    public class WhereClauseBuilder extends ClauseBuilder {
        public <T, R> WhereClauseBuilder and(Column<T,R> column, TestSupplier<T> testSupplier) {
            whereClause = new AndExpression(whereClause, new ColumnTest<>(scope.findAlias(column.rowClass()), column, testSupplier.get(scope)));
            return this;
        }

        public <T, R> WhereClauseBuilder and(String alias, Column<T,R> column, TestSupplier<T> testSupplier) {
            whereClause = new AndExpression(whereClause, new ColumnTest<>(scope.findAlias(column.rowClass(), alias), column, testSupplier.get(scope)));
            return this;
        }

        public <T, R> OrderByBuilder orderBy(Column<T,R> column) {
            return new OrderByBuilder().then(column);
        }

        public <T, R> OrderByBuilder orderBy(String alias, Column<T,R> column) {
            return new OrderByBuilder().then(alias, column);
        }

        public <T, R> OrderByBuilder orderBy(Column<T,R> column, Order order) {
            return new OrderByBuilder().then(column, order);
        }

        public <T, R> OrderByBuilder orderBy(String alias, Column<T,R> column, Order order) {
            return new OrderByBuilder().then(alias, column, order);
        }
    }

    public class OrderByBuilder extends ClauseBuilder {
        public <T, R> OrderByBuilder then(Column<T,R> column) {
            orderByClauses.add(new OrderBy<>(scope.findAlias(column.rowClass()), column, Order.ASCENDING));
            return this;
        }

        public <T, R> OrderByBuilder then(String alias, Column<T,R> column) {
            orderByClauses.add(new OrderBy<>(scope.findAlias(column.rowClass(), alias), column, Order.ASCENDING));
            return this;
        }

        public <T, R> OrderByBuilder then(Column<T,R> column, Order order) {
            orderByClauses.add(new OrderBy<>(scope.findAlias(column.rowClass()), column, order));
            return this;
        }

        public <T, R> OrderByBuilder then(String alias, Column<T,R> column, Order order) {
            orderByClauses.add(new OrderBy<>(scope.findAlias(column.rowClass(), alias), column, order));
            return this;
        }
    }

    public <T, R> OrderByBuilder orderBy(Column<T,R> column) {
        return new OrderByBuilder().then(column);
    }

    public <T, R> OrderByBuilder orderBy(String alias, Column<T,R> column) {
        return new OrderByBuilder().then(alias, column);
    }

    public <T, R> OrderByBuilder orderBy(Column<T,R> column, Order order) {
        return new OrderByBuilder().then(column, order);
    }

    public <T, R> OrderByBuilder orderBy(String alias, Column<T,R> column, Order order) {
        return new OrderByBuilder().then(alias, column, order);
    }

    protected Select(Scope scope) {
        this.scope = scope;
    }

    public static <R> Select1<R> from(Alias<R> alias) {
        return new Select1<>(alias);
    }

    public static <R> Select1<R> from(Table<R,?> table) {
        return new Select1<>(table.as(table.tableName()));
    }

    public abstract Optional<RT> optional(JdbcTemplate jdbcTemplate);
    public abstract String sql();

    @NotNull
    protected Object[] onClauseArgs() {
        return whereClause == null
            ? new Object[0]
            : whereClause.args().toArray();
    }

    @NotNull
    protected String whereClauseSql() {
        return whereClause == null
            ? ""
            : " where " + whereClause.sql();
    }

    @NotNull
    protected Object[] whereClauseArgs() {
        return whereClause == null
            ? new Object[0]
            : whereClause.args().toArray();
    }

    @NotNull
    protected String orderByClauseSql() {
        return orderByClauses.isEmpty()
            ? ""
            : " order by " + orderByClauses.stream().map(OrderBy::sql).collect(joining(", "));
    }
}
