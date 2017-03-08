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

package com.cadenzauk.siesta.grammar.select;

import com.cadenzauk.core.tuple.Tuple;
import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.JoinType;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.RowMappers;

public class ExpectingJoin1<RT> extends ExpectingSelect<RT> {
    public ExpectingJoin1(Select<RT> select) {
        super(select);
    }

    public <R2> InJoinExpectingOn<ExpectingJoin2<RT,R2>, Tuple2<RT, R2>> join(Alias<R2> alias2) {
        return join(JoinType.INNER, alias2);
    }

    public <R2> InJoinExpectingOn<ExpectingJoin2<RT,R2>, Tuple2<RT, R2>> join(Class<R2> r2Class, String alias2) {
        return join(JoinType.INNER, scope().database().table(r2Class).as(alias2));
    }

    public <R2> InJoinExpectingOn<ExpectingJoin2<RT,R2>, Tuple2<RT, R2>> leftJoin(Alias<R2> alias2) {
        return join(JoinType.LEFT_OUTER, alias2);
    }

    public <R2> InJoinExpectingOn<ExpectingJoin2<RT,R2>, Tuple2<RT, R2>> leftJoin(Class<R2> r2Class, String alias2) {
        return join(JoinType.LEFT_OUTER, scope().database().table(r2Class).as(alias2));
    }

    public <R2> InJoinExpectingOn<ExpectingJoin2<RT,R2>, Tuple2<RT, R2>> rightJoin(Alias<R2> alias2) {
        return join(JoinType.RIGHT_OUTER, alias2);
    }

    public <R2> InJoinExpectingOn<ExpectingJoin2<RT,R2>, Tuple2<RT, R2>> rightJoin(Class<R2> r2Class, String alias2) {
        return join(JoinType.RIGHT_OUTER, scope().database().table(r2Class).as(alias2));
    }

    public <R2> InJoinExpectingOn<ExpectingJoin2<RT,R2>, Tuple2<RT, R2>>  fullOuterJoin(Alias<R2> alias2) {
        return join(JoinType.FULL_OUTER, alias2);
    }

    public <R2> InJoinExpectingOn<ExpectingJoin2<RT,R2>, Tuple2<RT, R2>> fullOuterJoin(Class<R2> r2Class, String alias2) {
        return join(JoinType.FULL_OUTER, scope().database().table(r2Class).as(alias2));
    }

    private <R2> InJoinExpectingOn<ExpectingJoin2<RT,R2>, Tuple2<RT, R2>> join(JoinType joinType, Alias<R2> alias2) {
        Select<Tuple2<RT,R2>> select2 = new Select<>(scope().plus(alias2),
            statement.from().join(joinType, alias2),
            RowMappers.of(statement.rowMapper(), alias2.rowMapper()),
            Projection.of(statement.projection(), Projection.of(alias2)));
        return new InJoinExpectingOn<>(select2, ExpectingJoin2::new);
    }
}
