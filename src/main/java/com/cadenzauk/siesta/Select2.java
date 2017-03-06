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

import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.siesta.grammar.select.JoinClause;
import com.cadenzauk.siesta.grammar.select.JoinClauseStart;

public class Select2<RT1, RT2> extends Select<Tuple2<RT1,RT2>> {
    private final RowMapper<RT1> rowMapper1;
    private final RowMapper<RT2> rowMapper2;

    public Select2(Scope scope, From from, RowMapper<RT1> rowMapper1, RowMapper<RT2> rowMapper2, Projection p1, Projection p2) {
        super(scope, from, RowMappers.of(rowMapper1, rowMapper2), Projection.of(p1, p2));
        this.rowMapper1 = rowMapper1;
        this.rowMapper2 = rowMapper2;
    }

    Select2JoinClauseStart joinClause() {
        return new Select2JoinClauseStart();
    }

    private <R3> Select3<RT1, RT2, R3>.Select3JoinClauseStart join(JoinType joinType, Alias<R3> alias2) {
        return new Select3<>(scope.plus(alias2), from.join(joinType, alias2), rowMapper1, rowMapper2, alias2.rowMapper(), projection(), Projection.of(alias2)).joinClause();
    }

    public class Select2JoinClauseStart extends JoinClauseStart<Select2JoinClauseStart,Select2JoinClause,Tuple2<RT1,RT2>> {
        public Select2JoinClauseStart() {
            super(Select2.this.selectStatement, Select2JoinClauseStart::newJoinClause);
        }

        private Select2JoinClause newJoinClause() {
            return new Select2JoinClause();
        }
    }

    public class Select2JoinClause extends JoinClause<Select2JoinClause,Tuple2<RT1,RT2>> {
        public Select2JoinClause() {
            super(Select2.this.selectStatement);
        }

        public <R3> Select3<RT1,RT2,R3>.Select3JoinClauseStart join(Alias<R3> alias) {
            return Select2.this.join(JoinType.INNER, alias);
        }

        public <R3> Select3<RT1,RT2,R3>.Select3JoinClauseStart join(Class<R3> rowClass, String alias) {
            return Select2.this.join(JoinType.INNER, scope.database().table(rowClass).as(alias));
        }

        public <R3> Select3<RT1,RT2,R3>.Select3JoinClauseStart leftJoin(Alias<R3> alias) {
            return Select2.this.join(JoinType.LEFT_OUTER, alias);
        }

        public <R3> Select3<RT1,RT2,R3>.Select3JoinClauseStart leftJoin(Class<R3> rowClass, String alias) {
            return Select2.this.join(JoinType.LEFT_OUTER, scope.database().table(rowClass).as(alias));
        }

        public <R3> Select3<RT1,RT2,R3>.Select3JoinClauseStart rightJoin(Alias<R3> alias) {
            return Select2.this.join(JoinType.RIGHT_OUTER, alias);
        }

        public <R3> Select3<RT1,RT2,R3>.Select3JoinClauseStart rightJoin(Class<R3> rowClass, String alias) {
            return Select2.this.join(JoinType.RIGHT_OUTER, scope.database().table(rowClass).as(alias));
        }

        public <R3> Select3<RT1,RT2,R3>.Select3JoinClauseStart fullOuterJoin(Alias<R3> alias) {
            return Select2.this.join(JoinType.FULL_OUTER, alias);
        }

        public <R3> Select3<RT1,RT2,R3>.Select3JoinClauseStart fullOuterJoin(Class<R3> rowClass, String alias) {
            return Select2.this.join(JoinType.FULL_OUTER, scope.database().table(rowClass).as(alias));
        }
    }
}
