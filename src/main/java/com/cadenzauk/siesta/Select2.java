/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.siesta;

import com.cadenzauk.core.tuple.Tuple2;

public class Select2<RT1, RT2> extends Select<Tuple2<RT1,RT2>> {
    private final RowMapper<RT1> rowMapper1;
    private final RowMapper<RT2> rowMapper2;

    public Select2(Scope scope, From from, RowMapper<RT1> rowMapper1, RowMapper<RT2> rowMapper2, Projection p1, Projection p2) {
        super(scope, from, RowMappers.of(rowMapper1, rowMapper2), Projection.of(p1, p2));
        this.rowMapper1 = rowMapper1;
        this.rowMapper2 = rowMapper2;
    }

    Select2JoinClauseStartBuilder joinClause() {
        return new Select2JoinClauseStartBuilder();
    }

    private <R3> Select3<RT1,RT2,R3>.Select3JoinClauseStartBuilder join(JoinType joinType, Alias<R3> alias2) {
        return new Select3<>(scope.plus(alias2), from.join(joinType, alias2), rowMapper1, rowMapper2, alias2.rowMapper(), projection(), Projection.of(alias2)).joinClause();
    }

    public class Select2JoinClauseStartBuilder extends JoinClauseStartBuilder<Select2JoinClauseStartBuilder,Select2JoinClauseBuilder> {
        public Select2JoinClauseStartBuilder() {
            super(Select2JoinClauseStartBuilder::newJoinClause);
        }

        private Select2JoinClauseBuilder newJoinClause() {
            return new Select2JoinClauseBuilder();
        }
    }

    public class Select2JoinClauseBuilder extends JoinClauseBuilder<Select2JoinClauseBuilder> {
        public <R3> Select3<RT1,RT2,R3>.Select3JoinClauseStartBuilder join(Alias<R3> alias) {
            return Select2.this.join(JoinType.INNER, alias);
        }

        public <R3> Select3<RT1,RT2,R3>.Select3JoinClauseStartBuilder join(Class<R3> rowClass, String alias) {
            return Select2.this.join(JoinType.INNER, scope.database().table(rowClass).as(alias));
        }

        public <R3> Select3<RT1,RT2,R3>.Select3JoinClauseStartBuilder leftJoin(Alias<R3> alias) {
            return Select2.this.join(JoinType.LEFT_OUTER, alias);
        }

        public <R3> Select3<RT1,RT2,R3>.Select3JoinClauseStartBuilder leftJoin(Class<R3> rowClass, String alias) {
            return Select2.this.join(JoinType.LEFT_OUTER, scope.database().table(rowClass).as(alias));
        }

        public <R3> Select3<RT1,RT2,R3>.Select3JoinClauseStartBuilder rightJoin(Alias<R3> alias) {
            return Select2.this.join(JoinType.RIGHT_OUTER, alias);
        }

        public <R3> Select3<RT1,RT2,R3>.Select3JoinClauseStartBuilder rightJoin(Class<R3> rowClass, String alias) {
            return Select2.this.join(JoinType.RIGHT_OUTER, scope.database().table(rowClass).as(alias));
        }

        public <R3> Select3<RT1,RT2,R3>.Select3JoinClauseStartBuilder fullOuterJoin(Alias<R3> alias) {
            return Select2.this.join(JoinType.FULL_OUTER, alias);
        }

        public <R3> Select3<RT1,RT2,R3>.Select3JoinClauseStartBuilder fullOuterJoin(Class<R3> rowClass, String alias) {
            return Select2.this.join(JoinType.FULL_OUTER, scope.database().table(rowClass).as(alias));
        }
    }
}
