/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.siesta;

import com.cadenzauk.core.tuple.Tuple4;

public class Select4<RT1, RT2, RT3,RT4> extends Select<Tuple4<RT1,RT2,RT3,RT4>> {
    public Select4(Scope scope, From from, RowMapper<RT1> rowMapper1, RowMapper<RT2> rowMapper2, RowMapper<RT3> rowMapper3, RowMapper<RT4> rowMapper4, Projection p1, Projection p2) {
        super(scope, from, RowMappers.of(rowMapper1, rowMapper2, rowMapper3, rowMapper4), Projection.of(p1, p2));
    }

    Select4JoinClauseStartBuilder joinClause() {
        return new Select4JoinClauseStartBuilder();
    }

    public class Select4JoinClauseStartBuilder extends JoinClauseStartBuilder<Select4JoinClauseStartBuilder,Select4JoinClauseBuilder> {
        Select4JoinClauseStartBuilder() {
            super(Select4JoinClauseStartBuilder::newJoinClause);
        }

        private Select4JoinClauseBuilder newJoinClause() {
            return new Select4JoinClauseBuilder();
        }
    }

    public class Select4JoinClauseBuilder extends JoinClauseBuilder<Select4JoinClauseBuilder> {
    }
}
