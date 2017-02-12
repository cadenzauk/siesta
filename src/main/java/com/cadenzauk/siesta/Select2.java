/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.siesta.catalog.Column;
import com.cadenzauk.siesta.expression.CompleteExpression;
import com.cadenzauk.siesta.expression.ResolvedColumn;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

import static java.util.stream.Collectors.joining;

public class Select2<R1, R2, RT1, RT2> extends Select<Tuple2<RT1, RT2>> {
    private final Alias<R1> alias1;
    private final Alias<R2> alias2;
    private final JoinType joinType;

    @Override
    public String sql(Scope scope) {
        return "(" + sql() + ")";
    }

    @Override
    public String label(Scope scope) {
        return null;
    }

    @Override
    public RowMapper<Tuple2<RT1, RT2>> rowMapper(String label) {
        return rowMapper();
    }

    public class JoinClauseStartBuilder {
        public <T> JoinClauseBuilder on(Column<T,R2> column2, Condition<T> condition) {
            onClause = new CompleteExpression<>(new ResolvedColumn<>(alias2, column2), condition);
            return new JoinClauseBuilder();
        }

        public <T> JoinClauseBuilder on(TypedExpression<T> lhs, Condition<T> rhs) {
            onClause = new CompleteExpression<>(lhs, rhs);
            return new JoinClauseBuilder();
        }
    }

    public Select2(JoinType joinType, Alias<R1> alias1, Alias<R2> alias2, RowMapper<RT1> rowMapper1, RowMapper<RT2> rowMapper2, Projection p1, Projection p2) {
        super(new Scope(alias1, alias2), RowMappers.of(rowMapper1, rowMapper2), Projection.of(p1, p2));
        this.joinType = joinType;
        this.alias1 = alias1;
        this.alias2 = alias2;
    }

    @Override
    public List<Tuple2<RT1, RT2>> list(JdbcTemplate jdbcTemplate) {
        Object[] args = ArrayUtils.addAll(onClauseArgs(), whereClauseArgs());
        String sql = sql();
        System.out.println(sql);
        return jdbcTemplate.query(sql, args, rowMapper());
    }

    @Override
    public String sql() {
        return String.format("select %s from %s %s %s on %s%s%s",
            projection().sql(scope),
            alias1.inWhereClause(),
            joinType.sql(),
            alias2.inWhereClause(),
            onClause.sql(scope),
            whereClauseSql(scope),
            orderByClauseSql(scope));
    }

    JoinClauseStartBuilder joinClause() {
        return new JoinClauseStartBuilder();
    }
}
