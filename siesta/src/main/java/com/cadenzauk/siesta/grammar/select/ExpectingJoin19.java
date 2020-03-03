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

import com.cadenzauk.core.tuple.Tuple19;
import com.cadenzauk.core.tuple.Tuple20;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.JoinType;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.Projections;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

public class ExpectingJoin19<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, RT19> extends InJoinExpectingAnd<ExpectingJoin19<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19>,Tuple19<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19>> {
    public ExpectingJoin19(SelectStatement<Tuple19<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19>> statement) {
        super(statement);
    }

    public <R20> InJoinExpectingOn<ExpectingJoin20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,R20>, Tuple20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,R20>> join(Alias<R20> alias) {
        return join(JoinType.INNER, alias);
    }

    public <R20> InJoinExpectingOn<ExpectingJoin20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,R20>, Tuple20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,R20>> join(Class<R20> rowClass, String alias) {
        return join(JoinType.INNER, scope().database().table(rowClass).as(alias));
    }

    public <R20> InJoinExpectingOn<ExpectingJoin20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,R20>, Tuple20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,R20>> join(Select<R20> select, String alias) {
        return join(JoinType.INNER, new SubselectAlias<>(select, alias));
    }

    public <R20> InJoinExpectingOn<ExpectingJoin20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,R20>, Tuple20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,R20>> leftJoin(Alias<R20> alias) {
        return join(JoinType.LEFT_OUTER, alias);
    }

    public <R20> InJoinExpectingOn<ExpectingJoin20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,R20>, Tuple20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,R20>> leftJoin(Class<R20> rowClass, String alias) {
        return join(JoinType.LEFT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R20> InJoinExpectingOn<ExpectingJoin20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,R20>, Tuple20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,R20>> leftJoin(Select<R20> select, String alias) {
        return join(JoinType.LEFT_OUTER, new SubselectAlias<>(select, alias));
    }

    public <R20> InJoinExpectingOn<ExpectingJoin20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,R20>, Tuple20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,R20>> rightJoin(Alias<R20> alias) {
        return join(JoinType.RIGHT_OUTER, alias);
    }

    public <R20> InJoinExpectingOn<ExpectingJoin20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,R20>, Tuple20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,R20>> rightJoin(Class<R20> rowClass, String alias) {
        return join(JoinType.RIGHT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R20> InJoinExpectingOn<ExpectingJoin20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,R20>, Tuple20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,R20>> rightJoin(Select<R20> select, String alias) {
        return join(JoinType.RIGHT_OUTER, new SubselectAlias<>(select, alias));
    }

    public <R20> InJoinExpectingOn<ExpectingJoin20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,R20>, Tuple20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,R20>> fullOuterJoin(Alias<R20> alias) {
        return join(JoinType.FULL_OUTER, alias);
    }

    public <R20> InJoinExpectingOn<ExpectingJoin20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,R20>, Tuple20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,R20>> fullOuterJoin(Class<R20> rowClass, String alias) {
        return join(JoinType.FULL_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R20> InJoinExpectingOn<ExpectingJoin20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,R20>, Tuple20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,R20>> fullOuterJoin(Select<R20> select, String alias) {
        return join(JoinType.FULL_OUTER, new SubselectAlias<>(select, alias));
    }

    private <R20> InJoinExpectingOn<ExpectingJoin20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,R20>, Tuple20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,R20>> join(JoinType joinType, Alias<R20> alias) {
        SelectStatement<Tuple20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,R20>> select20 = new SelectStatement<>(
            scope().plus(alias),
            new TypeToken<Tuple20<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,RT19,R20>>() {}
                .where(new TypeParameter<RT1>() {}, Tuple19.type1(type()))
                .where(new TypeParameter<RT2>() {}, Tuple19.type2(type()))
                .where(new TypeParameter<RT3>() {}, Tuple19.type3(type()))
                .where(new TypeParameter<RT4>() {}, Tuple19.type4(type()))
                .where(new TypeParameter<RT5>() {}, Tuple19.type5(type()))
                .where(new TypeParameter<RT6>() {}, Tuple19.type6(type()))
                .where(new TypeParameter<RT7>() {}, Tuple19.type7(type()))
                .where(new TypeParameter<RT8>() {}, Tuple19.type8(type()))
                .where(new TypeParameter<RT9>() {}, Tuple19.type9(type()))
                .where(new TypeParameter<RT10>() {}, Tuple19.type10(type()))
                .where(new TypeParameter<RT11>() {}, Tuple19.type11(type()))
                .where(new TypeParameter<RT12>() {}, Tuple19.type12(type()))
                .where(new TypeParameter<RT13>() {}, Tuple19.type13(type()))
                .where(new TypeParameter<RT14>() {}, Tuple19.type14(type()))
                .where(new TypeParameter<RT15>() {}, Tuple19.type15(type()))
                .where(new TypeParameter<RT16>() {}, Tuple19.type16(type()))
                .where(new TypeParameter<RT17>() {}, Tuple19.type17(type()))
                .where(new TypeParameter<RT18>() {}, Tuple19.type18(type()))
                .where(new TypeParameter<RT19>() {}, Tuple19.type19(type()))
                .where(new TypeParameter<R20>() {}, alias.type()),
            statement.from().join(joinType, alias),
            Projections.of20(statement.projection(), Projection.of(alias)));
        return new InJoinExpectingOn<>(select20, ExpectingJoin20::new);
    }
}
