/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.cadenzauk.siesta;

import com.cadenzauk.core.tuple.Tuple3;

public class Select3<RT1, RT2, RT3> extends Select<Tuple3<RT1,RT2,RT3>> {
    private final RowMapper<RT1> rowMapper1;
    private final RowMapper<RT2> rowMapper2;
    private final RowMapper<RT3> rowMapper3;

    public Select3(Scope scope, From from, RowMapper<RT1> rowMapper1, RowMapper<RT2> rowMapper2, RowMapper<RT3> rowMapper3, Projection p1, Projection p2) {
        super(scope, from, RowMappers.of(rowMapper1, rowMapper2, rowMapper3), Projection.of(p1, p2));
        this.rowMapper1 = rowMapper1;
        this.rowMapper2 = rowMapper2;
        this.rowMapper3 = rowMapper3;
    }

    Select3JoinClauseStartBuilder joinClause() {
        return new Select3JoinClauseStartBuilder();
    }

    private <R4> Select4<RT1,RT2,RT3,R4>.Select4JoinClauseStartBuilder join(JoinType joinType, Alias<R4> alias2) {
        return new Select4<>(scope.plus(alias2), from.join(joinType, alias2), rowMapper1, rowMapper2, rowMapper3, alias2.rowMapper(), projection(), Projection.of(alias2)).joinClause();
    }

    public class Select3JoinClauseStartBuilder extends JoinClauseStartBuilder<Select3JoinClauseStartBuilder,Select3JoinClauseBuilder> {
        Select3JoinClauseStartBuilder() {
            super(Select3JoinClauseStartBuilder::newJoinClause);
        }

        private Select3JoinClauseBuilder newJoinClause() {
            return new Select3JoinClauseBuilder();
        }
    }

    public class Select3JoinClauseBuilder extends JoinClauseBuilder<Select3JoinClauseBuilder> {
        public <R4> Select4<RT1,RT2,RT3,R4>.Select4JoinClauseStartBuilder join(Alias<R4> alias) {
            return Select3.this.join(JoinType.INNER, alias);
        }

        public <R4> Select4<RT1,RT2,RT3,R4>.Select4JoinClauseStartBuilder join(Class<R4> rowClass, String alias) {
            return Select3.this.join(JoinType.INNER, scope.database().table(rowClass).as(alias));
        }

        public <R4> Select4<RT1,RT2,RT3,R4>.Select4JoinClauseStartBuilder leftJoin(Alias<R4> alias) {
            return Select3.this.join(JoinType.LEFT_OUTER, alias);
        }

        public <R4> Select4<RT1,RT2,RT3,R4>.Select4JoinClauseStartBuilder leftJoin(Class<R4> rowClass, String alias) {
            return Select3.this.join(JoinType.LEFT_OUTER, scope.database().table(rowClass).as(alias));
        }

        public <R4> Select4<RT1,RT2,RT3,R4>.Select4JoinClauseStartBuilder rightJoin(Alias<R4> alias) {
            return Select3.this.join(JoinType.RIGHT_OUTER, alias);
        }

        public <R4> Select4<RT1,RT2,RT3,R4>.Select4JoinClauseStartBuilder rightJoin(Class<R4> rowClass, String alias) {
            return Select3.this.join(JoinType.RIGHT_OUTER, scope.database().table(rowClass).as(alias));
        }

        public <R4> Select4<RT1,RT2,RT3,R4>.Select4JoinClauseStartBuilder fullOuterJoin(Alias<R4> alias) {
            return Select3.this.join(JoinType.FULL_OUTER, alias);
        }

        public <R4> Select4<RT1,RT2,RT3,R4>.Select4JoinClauseStartBuilder fullOuterJoin(Class<R4> rowClass, String alias) {
            return Select3.this.join(JoinType.FULL_OUTER, scope.database().table(rowClass).as(alias));
        }
    }
}
