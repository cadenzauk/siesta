/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import com.cadenzauk.core.tuple.Tuple2;
import com.google.common.collect.Iterables;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

import static java.util.stream.Collectors.joining;

public class Select2<R1, R2> extends Select<Tuple2<R1, R2>> {
    private final Alias<R1> alias1;
    private final Alias<R2> alias2;
    private final JoinType joinType;

    public class JoinClauseStartBuilder {
        public <T> JoinClauseBuilder on(Column<T,R2> column2, Column<T,R1> column1) {
            onClause = new ColumnTest<>(alias2, column2, new OperatorColumnTest<>("=", alias1, column1));
            return new JoinClauseBuilder();
        }

        public <T> JoinClauseBuilder on(Column<T,R2> column2, TestSupplier<T> testSupplier) {
            onClause = new ColumnTest<>(alias2, column2, testSupplier.get(scope));
            return new JoinClauseBuilder();
        }
    }

    public Select2(JoinType joinType, Alias<R1> alias1, Alias<R2> alias2) {
        super(new Scope(alias1, alias2));
        this.joinType = joinType;
        this.alias1 = alias1;
        this.alias2 = alias2;
    }

    public Optional<Tuple2<R1, R2>> optional(JdbcTemplate jdbcTemplate) {
        Object[] args = ArrayUtils.addAll(onClauseArgs(), whereClauseArgs());
        String sql = sql();
        System.out.println(sql);
        return Optional.ofNullable(Iterables.getOnlyElement(jdbcTemplate.query(sql, args, RowMappers.of(alias1.rowMapper(), alias2.rowMapper())), null));
    }

    public String sql() {
        return String.format("select %s, %s from %s %s %s on %s%s%s",
            alias1.table().columns().stream().map(c -> alias1.inSelectClause(c.column())).collect(joining(", ")),
            alias2.table().columns().stream().map(c -> alias2.inSelectClause(c.column())).collect(joining(", ")),
            alias1.inWhereClause(),
            joinType.sql(),
            alias2.inWhereClause(),
            onClause.sql(),
            whereClauseSql(),
            orderByClauseSql());
    }

    JoinClauseStartBuilder joinClause() {
        return new JoinClauseStartBuilder();
    }
}
