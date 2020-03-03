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

import com.cadenzauk.core.tuple.Tuple4;
import com.cadenzauk.core.tuple.Tuple5;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.JoinType;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.Projections;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

public class ExpectingJoin4<RT1, RT2, RT3, RT4> extends InJoinExpectingAnd<ExpectingJoin4<RT1,RT2,RT3,RT4>,Tuple4<RT1,RT2,RT3,RT4>> {
    public ExpectingJoin4(SelectStatement<Tuple4<RT1,RT2,RT3,RT4>> statement) {
        super(statement);
    }

    public <R5> InJoinExpectingOn<ExpectingJoin5<RT1,RT2,RT3,RT4,R5>, Tuple5<RT1,RT2,RT3,RT4,R5>> join(Alias<R5> alias) {
        return join(JoinType.INNER, alias);
    }

    public <R5> InJoinExpectingOn<ExpectingJoin5<RT1,RT2,RT3,RT4,R5>, Tuple5<RT1,RT2,RT3,RT4,R5>> join(Class<R5> rowClass, String alias) {
        return join(JoinType.INNER, scope().database().table(rowClass).as(alias));
    }

    public <R5> InJoinExpectingOn<ExpectingJoin5<RT1,RT2,RT3,RT4,R5>, Tuple5<RT1,RT2,RT3,RT4,R5>> join(Select<R5> select, String alias) {
        return join(JoinType.INNER, new SubselectAlias<>(select, alias));
    }

    public <R5> InJoinExpectingOn<ExpectingJoin5<RT1,RT2,RT3,RT4,R5>, Tuple5<RT1,RT2,RT3,RT4,R5>> leftJoin(Alias<R5> alias) {
        return join(JoinType.LEFT_OUTER, alias);
    }

    public <R5> InJoinExpectingOn<ExpectingJoin5<RT1,RT2,RT3,RT4,R5>, Tuple5<RT1,RT2,RT3,RT4,R5>> leftJoin(Class<R5> rowClass, String alias) {
        return join(JoinType.LEFT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R5> InJoinExpectingOn<ExpectingJoin5<RT1,RT2,RT3,RT4,R5>, Tuple5<RT1,RT2,RT3,RT4,R5>> leftJoin(Select<R5> select, String alias) {
        return join(JoinType.LEFT_OUTER, new SubselectAlias<>(select, alias));
    }

    public <R5> InJoinExpectingOn<ExpectingJoin5<RT1,RT2,RT3,RT4,R5>, Tuple5<RT1,RT2,RT3,RT4,R5>> rightJoin(Alias<R5> alias) {
        return join(JoinType.RIGHT_OUTER, alias);
    }

    public <R5> InJoinExpectingOn<ExpectingJoin5<RT1,RT2,RT3,RT4,R5>, Tuple5<RT1,RT2,RT3,RT4,R5>> rightJoin(Class<R5> rowClass, String alias) {
        return join(JoinType.RIGHT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R5> InJoinExpectingOn<ExpectingJoin5<RT1,RT2,RT3,RT4,R5>, Tuple5<RT1,RT2,RT3,RT4,R5>> rightJoin(Select<R5> select, String alias) {
        return join(JoinType.RIGHT_OUTER, new SubselectAlias<>(select, alias));
    }

    public <R5> InJoinExpectingOn<ExpectingJoin5<RT1,RT2,RT3,RT4,R5>, Tuple5<RT1,RT2,RT3,RT4,R5>> fullOuterJoin(Alias<R5> alias) {
        return join(JoinType.FULL_OUTER, alias);
    }

    public <R5> InJoinExpectingOn<ExpectingJoin5<RT1,RT2,RT3,RT4,R5>, Tuple5<RT1,RT2,RT3,RT4,R5>> fullOuterJoin(Class<R5> rowClass, String alias) {
        return join(JoinType.FULL_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R5> InJoinExpectingOn<ExpectingJoin5<RT1,RT2,RT3,RT4,R5>, Tuple5<RT1,RT2,RT3,RT4,R5>> fullOuterJoin(Select<R5> select, String alias) {
        return join(JoinType.FULL_OUTER, new SubselectAlias<>(select, alias));
    }

    private <R5> InJoinExpectingOn<ExpectingJoin5<RT1,RT2,RT3,RT4,R5>, Tuple5<RT1,RT2,RT3,RT4,R5>> join(JoinType joinType, Alias<R5> alias) {
        SelectStatement<Tuple5<RT1,RT2,RT3,RT4,R5>> select5 = new SelectStatement<>(
            scope().plus(alias),
            new TypeToken<Tuple5<RT1,RT2,RT3,RT4,R5>>() {}
                .where(new TypeParameter<RT1>() {}, Tuple4.type1(type()))
                .where(new TypeParameter<RT2>() {}, Tuple4.type2(type()))
                .where(new TypeParameter<RT3>() {}, Tuple4.type3(type()))
                .where(new TypeParameter<RT4>() {}, Tuple4.type4(type()))
                .where(new TypeParameter<R5>() {}, alias.type()),
            statement.from().join(joinType, alias),
            Projections.of5(statement.projection(), Projection.of(alias)));
        return new InJoinExpectingOn<>(select5, ExpectingJoin5::new);
    }
}
