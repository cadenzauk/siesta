/*
 * Copyright (c) 2017, 2020 Cadenza United Kingdom Limited
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
import com.cadenzauk.siesta.Projections;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

public class ExpectingJoin5<RT1, RT2, RT3, RT4, RT5> extends InJoinExpectingAnd<ExpectingJoin5<RT1,RT2,RT3,RT4,RT5>,Tuple5<RT1,RT2,RT3,RT4,RT5>> {
    public ExpectingJoin5(SelectStatement<Tuple5<RT1,RT2,RT3,RT4,RT5>> statement) {
        super(statement);
    }

    public <R6> InJoinExpectingOn<ExpectingJoin6<RT1,RT2,RT3,RT4,RT5,R6>, Tuple6<RT1,RT2,RT3,RT4,RT5,R6>> join(Alias<R6> alias) {
        return join(JoinType.INNER, alias);
    }

    public <R6> InJoinExpectingOn<ExpectingJoin6<RT1,RT2,RT3,RT4,RT5,R6>, Tuple6<RT1,RT2,RT3,RT4,RT5,R6>> join(Class<R6> rowClass, String alias) {
        return join(JoinType.INNER, scope().database().table(rowClass).as(alias));
    }

    public <R6> InJoinExpectingOn<ExpectingJoin6<RT1,RT2,RT3,RT4,RT5,R6>, Tuple6<RT1,RT2,RT3,RT4,RT5,R6>> join(Select<R6> select, String alias) {
        return join(JoinType.INNER, new SubselectAlias<>(select, alias));
    }

    public <R6> InJoinExpectingOn<ExpectingJoin6<RT1,RT2,RT3,RT4,RT5,R6>, Tuple6<RT1,RT2,RT3,RT4,RT5,R6>> leftJoin(Alias<R6> alias) {
        return join(JoinType.LEFT_OUTER, alias);
    }

    public <R6> InJoinExpectingOn<ExpectingJoin6<RT1,RT2,RT3,RT4,RT5,R6>, Tuple6<RT1,RT2,RT3,RT4,RT5,R6>> leftJoin(Class<R6> rowClass, String alias) {
        return join(JoinType.LEFT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R6> InJoinExpectingOn<ExpectingJoin6<RT1,RT2,RT3,RT4,RT5,R6>, Tuple6<RT1,RT2,RT3,RT4,RT5,R6>> leftJoin(Select<R6> select, String alias) {
        return join(JoinType.LEFT_OUTER, new SubselectAlias<>(select, alias));
    }

    public <R6> InJoinExpectingOn<ExpectingJoin6<RT1,RT2,RT3,RT4,RT5,R6>, Tuple6<RT1,RT2,RT3,RT4,RT5,R6>> rightJoin(Alias<R6> alias) {
        return join(JoinType.RIGHT_OUTER, alias);
    }

    public <R6> InJoinExpectingOn<ExpectingJoin6<RT1,RT2,RT3,RT4,RT5,R6>, Tuple6<RT1,RT2,RT3,RT4,RT5,R6>> rightJoin(Class<R6> rowClass, String alias) {
        return join(JoinType.RIGHT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R6> InJoinExpectingOn<ExpectingJoin6<RT1,RT2,RT3,RT4,RT5,R6>, Tuple6<RT1,RT2,RT3,RT4,RT5,R6>> rightJoin(Select<R6> select, String alias) {
        return join(JoinType.RIGHT_OUTER, new SubselectAlias<>(select, alias));
    }

    public <R6> InJoinExpectingOn<ExpectingJoin6<RT1,RT2,RT3,RT4,RT5,R6>, Tuple6<RT1,RT2,RT3,RT4,RT5,R6>> fullOuterJoin(Alias<R6> alias) {
        return join(JoinType.FULL_OUTER, alias);
    }

    public <R6> InJoinExpectingOn<ExpectingJoin6<RT1,RT2,RT3,RT4,RT5,R6>, Tuple6<RT1,RT2,RT3,RT4,RT5,R6>> fullOuterJoin(Class<R6> rowClass, String alias) {
        return join(JoinType.FULL_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R6> InJoinExpectingOn<ExpectingJoin6<RT1,RT2,RT3,RT4,RT5,R6>, Tuple6<RT1,RT2,RT3,RT4,RT5,R6>> fullOuterJoin(Select<R6> select, String alias) {
        return join(JoinType.FULL_OUTER, new SubselectAlias<>(select, alias));
    }

    private <R6> InJoinExpectingOn<ExpectingJoin6<RT1,RT2,RT3,RT4,RT5,R6>, Tuple6<RT1,RT2,RT3,RT4,RT5,R6>> join(JoinType joinType, Alias<R6> alias) {
        SelectStatement<Tuple6<RT1,RT2,RT3,RT4,RT5,R6>> select6 = new SelectStatement<>(
            scope().plus(alias),
            new TypeToken<Tuple6<RT1,RT2,RT3,RT4,RT5,R6>>() {}
                .where(new TypeParameter<RT1>() {}, Tuple5.type1(type()))
                .where(new TypeParameter<RT2>() {}, Tuple5.type2(type()))
                .where(new TypeParameter<RT3>() {}, Tuple5.type3(type()))
                .where(new TypeParameter<RT4>() {}, Tuple5.type4(type()))
                .where(new TypeParameter<RT5>() {}, Tuple5.type5(type()))
                .where(new TypeParameter<R6>() {}, alias.type()),
            statement.from().join(joinType, alias),
            Projections.of6(statement.projection(), Projection.of(alias)));
        return new InJoinExpectingOn<>(select6, ExpectingJoin6::new);
    }
}
