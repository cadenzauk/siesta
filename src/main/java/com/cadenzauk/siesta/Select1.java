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
