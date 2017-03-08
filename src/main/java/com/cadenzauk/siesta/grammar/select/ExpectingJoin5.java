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

import com.cadenzauk.core.tuple.Tuple5;
import com.cadenzauk.core.tuple.Tuple6;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.JoinType;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.RowMappers;

public class ExpectingJoin5<RT1, RT2, RT3, RT4, RT5> extends InJoinExpectingAnd<ExpectingJoin5<RT1,RT2,RT3,RT4,RT5>,Tuple5<RT1,RT2,RT3,RT4,RT5>> {

    public ExpectingJoin5(Select<Tuple5<RT1,RT2,RT3,RT4,RT5>> select) {
        super(select);
    }

    public <R> InJoinExpectingOn<ExpectingJoin6<RT1,RT2,RT3,RT4,RT5,R>,Tuple6<RT1,RT2,RT3,RT4,RT5,R>> join(Alias<R> alias) {
        return join(JoinType.INNER, alias);
    }

    public <R> InJoinExpectingOn<ExpectingJoin6<RT1,RT2,RT3,RT4,RT5,R>,Tuple6<RT1,RT2,RT3,RT4,RT5,R>> join(Class<R> rowClass, String alias) {
        return join(JoinType.INNER, scope().database().table(rowClass).as(alias));
    }

    public <R> InJoinExpectingOn<ExpectingJoin6<RT1,RT2,RT3,RT4,RT5,R>,Tuple6<RT1,RT2,RT3,RT4,RT5,R>> leftJoin(Alias<R> alias) {
        return join(JoinType.LEFT_OUTER, alias);
    }

    public <R> InJoinExpectingOn<ExpectingJoin6<RT1,RT2,RT3,RT4,RT5,R>,Tuple6<RT1,RT2,RT3,RT4,RT5,R>> leftJoin(Class<R> rowClass, String alias) {
        return join(JoinType.LEFT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R> InJoinExpectingOn<ExpectingJoin6<RT1,RT2,RT3,RT4,RT5,R>,Tuple6<RT1,RT2,RT3,RT4,RT5,R>> rightJoin(Alias<R> alias) {
        return join(JoinType.RIGHT_OUTER, alias);
    }

    public <R> InJoinExpectingOn<ExpectingJoin6<RT1,RT2,RT3,RT4,RT5,R>,Tuple6<RT1,RT2,RT3,RT4,RT5,R>> rightJoin(Class<R> rowClass, String alias) {
        return join(JoinType.RIGHT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R> InJoinExpectingOn<ExpectingJoin6<RT1,RT2,RT3,RT4,RT5,R>,Tuple6<RT1,RT2,RT3,RT4,RT5,R>> fullOuterJoin(Alias<R> alias) {
        return join(JoinType.FULL_OUTER, alias);
    }

    public <R> InJoinExpectingOn<ExpectingJoin6<RT1,RT2,RT3,RT4,RT5,R>,Tuple6<RT1,RT2,RT3,RT4,RT5,R>> fullOuterJoin(Class<R> rowClass, String alias) {
        return join(JoinType.FULL_OUTER, scope().database().table(rowClass).as(alias));
    }

    private <R> InJoinExpectingOn<ExpectingJoin6<RT1,RT2,RT3,RT4,RT5,R>,Tuple6<RT1,RT2,RT3,RT4,RT5,R>> join(JoinType joinType, Alias<R> alias) {
        Select<Tuple6<RT1,RT2,RT3,RT4,RT5,R>> select = new Select<>(
            scope().plus(alias),
            statement.from().join(joinType, alias),
            RowMappers.add6th(statement.rowMapper(), alias.rowMapper()),
            Projection.of(statement.projection(), Projection.of(alias)));
        return new InJoinExpectingOn<>(select, ExpectingJoin6::new);
    }
}
