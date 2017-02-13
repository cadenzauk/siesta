/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import com.cadenzauk.core.reflect.MethodReference;
import com.cadenzauk.siesta.catalog.Column;
import com.cadenzauk.siesta.expression.CompleteExpression;
import com.cadenzauk.siesta.expression.ResolvedColumn;
import com.cadenzauk.siesta.expression.UnresolvedColumn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.function.Function;

import static com.cadenzauk.siesta.Alias.column;

public class Select1<R1, RT> extends Select<RT> {
    private final Alias<R1> alias;

    public Select1(Alias<R1> alias, RowMapper<RT> rowMapper, Projection projection) {
        super(new Scope(alias), rowMapper, projection);
        this.alias = alias;
    }

    public <T> Select1<R1, T> select(TypedExpression<T> column) {
        return new Select1<>(alias, column.rowMapper(column.label(scope)), Projection.of(column));
    }

    public <T> Select1<R1, T> select(MethodReference<R1,T> methodReference) {
        return select(UnresolvedColumn.of(methodReference));
    }

    public <R2> Select2<R1, R2, RT, R2>.JoinClauseStartBuilder join(Alias<R2> alias2) {
        return join(JoinType.INNER, alias2);
    }

    public <R2> Select2<R1, R2, RT, R2>.JoinClauseStartBuilder join(Class<R2> r2Class, String alias2) {
        return join(JoinType.INNER, alias.table().catalog().table(r2Class).as(alias2));
    }

    public <R2> Select2<R1, R2, RT, R2>.JoinClauseStartBuilder leftJoin(Alias<R2> alias2) {
        return join(JoinType.LEFT_OUTER, alias2);
    }

    public <R2> Select2<R1, R2, RT, R2>.JoinClauseStartBuilder leftJoin(Class<R2> r2Class, String alias2) {
        return join(JoinType.LEFT_OUTER, alias.table().catalog().table(r2Class).as(alias2));
    }

    public <R2> Select2<R1, R2, RT, R2>.JoinClauseStartBuilder rightJoin(Alias<R2> alias2) {
        return join(JoinType.RIGHT_OUTER, alias2);
    }

    public <R2> Select2<R1, R2, RT, R2>.JoinClauseStartBuilder rightJoin(Class<R2> r2Class, String alias2) {
        return join(JoinType.RIGHT_OUTER, alias.table().catalog().table(r2Class).as(alias2));
    }

    public <R2> Select2<R1, R2, RT, R2>.JoinClauseStartBuilder fullOuterJoin(Alias<R2> alias2) {
        return join(JoinType.FULL_OUTER, alias2);
    }

    public <R2> Select2<R1, R2, RT, R2>.JoinClauseStartBuilder fullOuterJoin(Class<R2> r2Class, String alias2) {
        return join(JoinType.FULL_OUTER, alias.table().catalog().table(r2Class).as(alias2));
    }

    private <R2> Select2<R1, R2, RT, R2>.JoinClauseStartBuilder join(JoinType joinType, Alias<R2> alias2) {
        return new Select2<>(joinType, alias, alias2, rowMapper(), alias2.rowMapper(), projection(), Projection.of(alias2)).joinClause();
    }

    public <T> WhereClauseBuilder where(Column<T, R1> column, Condition<T> condition) {
        whereClause = new CompleteExpression<>(new ResolvedColumn<>(alias, column), condition);
        return new WhereClauseBuilder();
    }

    public <T> WhereClauseBuilder where(TypedExpression<T> lhs, Condition<T> condition) {
        whereClause = new CompleteExpression<>(lhs, condition);
        return new WhereClauseBuilder();
    }

    public <T> WhereClauseBuilder where(MethodReference<R1,T> lhs, Condition<T> condition) {
        whereClause = new CompleteExpression<>(column(alias, lhs), condition);
        return new WhereClauseBuilder();
    }

    public List<RT> list(JdbcTemplate jdbcTemplate) {
        Object[] args = whereClauseArgs();
        String sql = sql();
        System.out.println(sql);
        return jdbcTemplate.query(sql, args, rowMapper());
    }

    public String sql() {
        return sqlImpl(scope);
    }

    @Override
    public String sql(Scope scope) {
        return "(" + sqlImpl(scope.plus(alias)) + ")";
    }

    private String sqlImpl(Scope actualScope) {
        return String.format("select %s from %s%s%s",
            projection().sql(actualScope),
            alias.inWhereClause(),
            whereClauseSql(actualScope),
            orderByClauseSql(actualScope));
    }

    @Override
    public String label(Scope scope) {
        return null;
    }

    @Override
    public RowMapper<RT> rowMapper(String label) {
        return rowMapper();
    }
}
