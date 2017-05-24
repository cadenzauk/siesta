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

import com.cadenzauk.core.tuple.Tuple3;
import com.cadenzauk.core.tuple.Tuple4;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.JoinType;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.RowMappers;

public class ExpectingJoin3<RT1, RT2, RT3> extends InJoinExpectingAnd<ExpectingJoin3<RT1,RT2,RT3>,Tuple3<RT1,RT2,RT3>> {

    public ExpectingJoin3(SelectStatement<Tuple3<RT1,RT2,RT3>> select) {
        super(select);
    }

    public <R4> InJoinExpectingOn<ExpectingJoin4<RT1, RT2, RT3,R4>,Tuple4<RT1,RT2,RT3,R4>> join(Alias<R4> alias) {
        return join(JoinType.INNER, alias);
    }

    public <R4> InJoinExpectingOn<ExpectingJoin4<RT1, RT2, RT3,R4>,Tuple4<RT1,RT2,RT3,R4>> join(Class<R4> rowClass, String alias) {
        return join(JoinType.INNER, scope().database().table(rowClass).as(alias));
    }

    public <R4> InJoinExpectingOn<ExpectingJoin4<RT1, RT2, RT3,R4>,Tuple4<RT1,RT2,RT3,R4>> leftJoin(Alias<R4> alias) {
        return join(JoinType.LEFT_OUTER, alias);
    }

    public <R4> InJoinExpectingOn<ExpectingJoin4<RT1, RT2, RT3,R4>,Tuple4<RT1,RT2,RT3,R4>> leftJoin(Class<R4> rowClass, String alias) {
        return join(JoinType.LEFT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R4> InJoinExpectingOn<ExpectingJoin4<RT1, RT2, RT3,R4>,Tuple4<RT1,RT2,RT3,R4>> rightJoin(Alias<R4> alias) {
        return join(JoinType.RIGHT_OUTER, alias);
    }

    public <R4> InJoinExpectingOn<ExpectingJoin4<RT1, RT2, RT3,R4>,Tuple4<RT1,RT2,RT3,R4>> rightJoin(Class<R4> rowClass, String alias) {
        return join(JoinType.RIGHT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R4> InJoinExpectingOn<ExpectingJoin4<RT1, RT2, RT3,R4>,Tuple4<RT1,RT2,RT3,R4>> fullOuterJoin(Alias<R4> alias) {
        return join(JoinType.FULL_OUTER, alias);
    }

    public <R4> InJoinExpectingOn<ExpectingJoin4<RT1, RT2, RT3,R4>,Tuple4<RT1,RT2,RT3,R4>> fullOuterJoin(Class<R4> rowClass, String alias) {
        return join(JoinType.FULL_OUTER, scope().database().table(rowClass).as(alias));
    }

    private <R4> InJoinExpectingOn<ExpectingJoin4<RT1, RT2, RT3,R4>,Tuple4<RT1,RT2,RT3,R4>> join(JoinType joinType, Alias<R4> alias2) {
        SelectStatement<Tuple4<RT1,RT2,RT3,R4>> select4 = new SelectStatement<>(
            scope().plus(alias2),
            statement.from().join(joinType, alias2),
            RowMappers.add4th(statement.rowMapper(), alias2.rowMapper()),
            Projection.of(statement.projection(), Projection.of(alias2)));
        return new InJoinExpectingOn<>(select4, ExpectingJoin4::new);
    }
}
