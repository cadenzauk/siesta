/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.siesta.expression.ResolvedColumn;
import com.cadenzauk.siesta.expression.UnresolvedColumn;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

public class Select2<R1, R2, RT1, RT2> extends Select<Tuple2<RT1,RT2>> {
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
    public RowMapper<Tuple2<RT1,RT2>> rowMapper(Scope scope, String label) {
        return rowMapper();
    }

    public class JoinClauseStartBuilder {
        public <T> ExpressionBuilder<T,JoinClauseBuilder> on(TypedExpression<T> lhs) {
            return ExpressionBuilder.of(lhs, Select2.this::setOnClause);
        }

        public <T, R> ExpressionBuilder<T,JoinClauseBuilder> on(Function1<R,T> lhs) {
            return ExpressionBuilder.of(UnresolvedColumn.of(lhs), Select2.this::setOnClause);
        }

        public <T, R> ExpressionBuilder<T,JoinClauseBuilder> on(FunctionOptional1<R,T> lhs) {
            return ExpressionBuilder.of(UnresolvedColumn.of(lhs), Select2.this::setOnClause);
        }

        public <T, R> ExpressionBuilder<T,JoinClauseBuilder> on(String alias, Function1<R,T> lhs) {
            return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), Select2.this::setOnClause);
        }

        public <T, R> ExpressionBuilder<T,JoinClauseBuilder> on(String alias, FunctionOptional1<R,T> lhs) {
            return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), Select2.this::setOnClause);
        }

        public <T, R> ExpressionBuilder<T,JoinClauseBuilder> on(Alias<R> alias, Function1<R,T> lhs) {
            return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), Select2.this::setOnClause);
        }

        public <T, R> ExpressionBuilder<T,JoinClauseBuilder> on(Alias<R> alias, FunctionOptional1<R,T> lhs) {
            return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), Select2.this::setOnClause);
        }
    }

    public Select2(Database database, JoinType joinType, Alias<R1> alias1, Alias<R2> alias2, RowMapper<RT1> rowMapper1, RowMapper<RT2> rowMapper2, Projection p1, Projection p2) {
        super(new Scope(database, alias1, alias2), RowMappers.of(rowMapper1, rowMapper2), Projection.of(p1, p2));
        this.joinType = joinType;
        this.alias1 = alias1;
        this.alias2 = alias2;
    }

    @Override
    public List<Tuple2<RT1,RT2>> list(JdbcTemplate jdbcTemplate) {
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
