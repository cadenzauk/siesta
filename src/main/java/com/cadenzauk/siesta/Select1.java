/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import com.google.common.collect.Iterables;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

import static java.util.stream.Collectors.joining;

public class Select1<R1> extends Select<R1> {
    private final Alias<R1> alias;

    public Select1(Alias<R1> alias) {
        super(new Scope(alias));
        this.alias = alias;
    }

    public <R2> Select2<R1,R2>.JoinClauseStartBuilder join(Alias<R2> alias2) {
        return new Select2<>(JoinType.INNER, alias, alias2).joinClause();
    }

    public <R2> Select2<R1,R2>.JoinClauseStartBuilder leftJoin(Alias<R2> alias2) {
        return new Select2<>(JoinType.LEFT_OUTER, alias, alias2).joinClause();
    }

    public <R2> Select2<R1,R2>.JoinClauseStartBuilder rightJoin(Alias<R2> alias2) {
        return new Select2<>(JoinType.RIGHT_OUTER, alias, alias2).joinClause();
    }

    public <R2> Select2<R1,R2>.JoinClauseStartBuilder fullOuterJoin(Alias<R2> alias2) {
        return new Select2<>(JoinType.FULL_OUTER, alias, alias2).joinClause();
    }

    public <T> WhereClauseBuilder where(Column<T,R1> column, TestSupplier<T> testSupplier) {
        whereClause = new ColumnTest<>(alias, column, testSupplier.get(scope));
        return new WhereClauseBuilder();
    }

    public Optional<R1> optional(JdbcTemplate jdbcTemplate) {
        Object[] args = whereClauseArgs();
        String sql = sql();
        System.out.println(sql);
        return Optional.ofNullable(Iterables.getOnlyElement(jdbcTemplate.query(sql, args, alias.rowMapper()), null));
    }

    public String sql() {
        return String.format("select %s from %s%s%s",
            alias.table().columns().map(alias::inSelectClause).collect(joining(", ")),
            alias.inWhereClause(),
            whereClauseSql(),
            orderByClauseSql());
    }
}
