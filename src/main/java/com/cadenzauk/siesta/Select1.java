/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.siesta;

public class Select1<RT> extends Select<RT> {
    public Select1(Database database, Alias<?> alias, RowMapper<RT> rowMapper, Projection projection) {
        super(new Scope(database, alias), From.from(alias), rowMapper, projection);
    }

    public <R2> Select2<RT,R2>.Select2JoinClauseStartBuilder join(Alias<R2> alias2) {
        return join(JoinType.INNER, alias2);
    }

    public <R2> Select2<RT,R2>.Select2JoinClauseStartBuilder join(Class<R2> r2Class, String alias2) {
        return join(JoinType.INNER, scope.database().table(r2Class).as(alias2));
    }

    public <R2> Select2<RT,R2>.Select2JoinClauseStartBuilder leftJoin(Alias<R2> alias2) {
        return join(JoinType.LEFT_OUTER, alias2);
    }

    public <R2> Select2<RT,R2>.Select2JoinClauseStartBuilder leftJoin(Class<R2> r2Class, String alias2) {
        return join(JoinType.LEFT_OUTER, scope.database().table(r2Class).as(alias2));
    }

    public <R2> Select2<RT,R2>.Select2JoinClauseStartBuilder rightJoin(Alias<R2> alias2) {
        return join(JoinType.RIGHT_OUTER, alias2);
    }

    public <R2> Select2<RT,R2>.Select2JoinClauseStartBuilder rightJoin(Class<R2> r2Class, String alias2) {
        return join(JoinType.RIGHT_OUTER, scope.database().table(r2Class).as(alias2));
    }

    public <R2> Select2<RT,R2>.Select2JoinClauseStartBuilder fullOuterJoin(Alias<R2> alias2) {
        return join(JoinType.FULL_OUTER, alias2);
    }

    public <R2> Select2<RT,R2>.Select2JoinClauseStartBuilder fullOuterJoin(Class<R2> r2Class, String alias2) {
        return join(JoinType.FULL_OUTER, scope.database().table(r2Class).as(alias2));
    }

    private <R2> Select2<RT,R2>.Select2JoinClauseStartBuilder join(JoinType joinType, Alias<R2> alias2) {
        return new Select2<>(scope.plus(alias2), from.join(joinType,alias2), rowMapper(), alias2.rowMapper(), projection(), Projection.of(alias2)).joinClause();
    }
 }
