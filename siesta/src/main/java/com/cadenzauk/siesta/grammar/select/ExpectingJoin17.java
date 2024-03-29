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

import com.cadenzauk.core.tuple.Tuple17;
import com.cadenzauk.core.tuple.Tuple18;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.JoinType;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.Projections;
import com.cadenzauk.siesta.grammar.temp.TempTable;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

public class ExpectingJoin17<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17> extends InJoinExpectingAnd<ExpectingJoin17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17>,Tuple17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17>> {
    public ExpectingJoin17(SelectStatement<Tuple17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17>> statement) {
        super(statement);
    }

    public <R18> InJoinExpectingOn<ExpectingJoin18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>, Tuple18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>> join(Alias<R18> alias) {
        return join(JoinType.INNER, alias);
    }

    public <R18> InJoinExpectingOn<ExpectingJoin18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>, Tuple18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>> join(Class<R18> rowClass, String alias) {
        return join(JoinType.INNER, scope().database().table(rowClass).as(alias));
    }

    public <R18> InJoinExpectingOn<ExpectingJoin18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>, Tuple18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>> join(TempTable<R18> tempTable, String alias) {
        return join(JoinType.INNER, tempTable.as(alias));
    }

    public <R18> InJoinExpectingOn<ExpectingJoin18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>, Tuple18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>> join(Select<R18> select, String alias) {
        return join(JoinType.INNER, new SubselectAlias<>(select, alias));
    }

    public <R18> InJoinExpectingOn<ExpectingJoin18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>, Tuple18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>> leftJoin(Alias<R18> alias) {
        return join(JoinType.LEFT_OUTER, alias);
    }

    public <R18> InJoinExpectingOn<ExpectingJoin18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>, Tuple18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>> leftJoin(Class<R18> rowClass, String alias) {
        return join(JoinType.LEFT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R18> InJoinExpectingOn<ExpectingJoin18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>, Tuple18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>> leftJoin(TempTable<R18> tempTable, String alias) {
        return join(JoinType.LEFT_OUTER, tempTable.as(alias));
    }

    public <R18> InJoinExpectingOn<ExpectingJoin18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>, Tuple18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>> leftJoin(Select<R18> select, String alias) {
        return join(JoinType.LEFT_OUTER, new SubselectAlias<>(select, alias));
    }

    public <R18> InJoinExpectingOn<ExpectingJoin18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>, Tuple18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>> rightJoin(Alias<R18> alias) {
        return join(JoinType.RIGHT_OUTER, alias);
    }

    public <R18> InJoinExpectingOn<ExpectingJoin18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>, Tuple18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>> rightJoin(Class<R18> rowClass, String alias) {
        return join(JoinType.RIGHT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R18> InJoinExpectingOn<ExpectingJoin18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>, Tuple18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>> rightJoin(TempTable<R18> tempTable, String alias) {
        return join(JoinType.RIGHT_OUTER, tempTable.as(alias));
    }

    public <R18> InJoinExpectingOn<ExpectingJoin18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>, Tuple18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>> rightJoin(Select<R18> select, String alias) {
        return join(JoinType.RIGHT_OUTER, new SubselectAlias<>(select, alias));
    }

    public <R18> InJoinExpectingOn<ExpectingJoin18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>, Tuple18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>> fullOuterJoin(Alias<R18> alias) {
        return join(JoinType.FULL_OUTER, alias);
    }

    public <R18> InJoinExpectingOn<ExpectingJoin18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>, Tuple18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>> fullOuterJoin(Class<R18> rowClass, String alias) {
        return join(JoinType.FULL_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R18> InJoinExpectingOn<ExpectingJoin18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>, Tuple18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>> fullOuterJoin(TempTable<R18> tempTable, String alias) {
        return join(JoinType.FULL_OUTER, tempTable.as(alias));
    }

    public <R18> InJoinExpectingOn<ExpectingJoin18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>, Tuple18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>> fullOuterJoin(Select<R18> select, String alias) {
        return join(JoinType.FULL_OUTER, new SubselectAlias<>(select, alias));
    }

    private <R18> InJoinExpectingOn<ExpectingJoin18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>, Tuple18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>> join(JoinType joinType, Alias<R18> alias) {
        SelectStatement<Tuple18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>> select18 = new SelectStatement<>(
            scope().plus(alias),
            new TypeToken<Tuple18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,R18>>() {}
                .where(new TypeParameter<RT1>() {}, Tuple17.type1(type()))
                .where(new TypeParameter<RT2>() {}, Tuple17.type2(type()))
                .where(new TypeParameter<RT3>() {}, Tuple17.type3(type()))
                .where(new TypeParameter<RT4>() {}, Tuple17.type4(type()))
                .where(new TypeParameter<RT5>() {}, Tuple17.type5(type()))
                .where(new TypeParameter<RT6>() {}, Tuple17.type6(type()))
                .where(new TypeParameter<RT7>() {}, Tuple17.type7(type()))
                .where(new TypeParameter<RT8>() {}, Tuple17.type8(type()))
                .where(new TypeParameter<RT9>() {}, Tuple17.type9(type()))
                .where(new TypeParameter<RT10>() {}, Tuple17.type10(type()))
                .where(new TypeParameter<RT11>() {}, Tuple17.type11(type()))
                .where(new TypeParameter<RT12>() {}, Tuple17.type12(type()))
                .where(new TypeParameter<RT13>() {}, Tuple17.type13(type()))
                .where(new TypeParameter<RT14>() {}, Tuple17.type14(type()))
                .where(new TypeParameter<RT15>() {}, Tuple17.type15(type()))
                .where(new TypeParameter<RT16>() {}, Tuple17.type16(type()))
                .where(new TypeParameter<RT17>() {}, Tuple17.type17(type()))
                .where(new TypeParameter<R18>() {}, alias.type()),
            statement.from().join(joinType, alias),
            Projections.of18(statement.projection(), Projection.of(alias)));
        return new InJoinExpectingOn<>(select18, ExpectingJoin18::new);
    }
}
