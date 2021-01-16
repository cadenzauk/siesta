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

import com.cadenzauk.core.tuple.Tuple16;
import com.cadenzauk.core.tuple.Tuple17;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.JoinType;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.Projections;
import com.cadenzauk.siesta.grammar.temp.TempTable;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

public class ExpectingJoin16<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16> extends InJoinExpectingAnd<ExpectingJoin16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16>,Tuple16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16>> {
    public ExpectingJoin16(SelectStatement<Tuple16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16>> statement) {
        super(statement);
    }

    public <R17> InJoinExpectingOn<ExpectingJoin17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>, Tuple17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>> join(Alias<R17> alias) {
        return join(JoinType.INNER, alias);
    }

    public <R17> InJoinExpectingOn<ExpectingJoin17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>, Tuple17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>> join(Class<R17> rowClass, String alias) {
        return join(JoinType.INNER, scope().database().table(rowClass).as(alias));
    }

    public <R17> InJoinExpectingOn<ExpectingJoin17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>, Tuple17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>> join(TempTable<R17> tempTable, String alias) {
        return join(JoinType.INNER, tempTable.as(alias));
    }

    public <R17> InJoinExpectingOn<ExpectingJoin17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>, Tuple17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>> join(Select<R17> select, String alias) {
        return join(JoinType.INNER, new SubselectAlias<>(select, alias));
    }

    public <R17> InJoinExpectingOn<ExpectingJoin17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>, Tuple17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>> leftJoin(Alias<R17> alias) {
        return join(JoinType.LEFT_OUTER, alias);
    }

    public <R17> InJoinExpectingOn<ExpectingJoin17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>, Tuple17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>> leftJoin(Class<R17> rowClass, String alias) {
        return join(JoinType.LEFT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R17> InJoinExpectingOn<ExpectingJoin17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>, Tuple17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>> leftJoin(TempTable<R17> tempTable, String alias) {
        return join(JoinType.LEFT_OUTER, tempTable.as(alias));
    }

    public <R17> InJoinExpectingOn<ExpectingJoin17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>, Tuple17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>> leftJoin(Select<R17> select, String alias) {
        return join(JoinType.LEFT_OUTER, new SubselectAlias<>(select, alias));
    }

    public <R17> InJoinExpectingOn<ExpectingJoin17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>, Tuple17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>> rightJoin(Alias<R17> alias) {
        return join(JoinType.RIGHT_OUTER, alias);
    }

    public <R17> InJoinExpectingOn<ExpectingJoin17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>, Tuple17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>> rightJoin(Class<R17> rowClass, String alias) {
        return join(JoinType.RIGHT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R17> InJoinExpectingOn<ExpectingJoin17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>, Tuple17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>> rightJoin(TempTable<R17> tempTable, String alias) {
        return join(JoinType.RIGHT_OUTER, tempTable.as(alias));
    }

    public <R17> InJoinExpectingOn<ExpectingJoin17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>, Tuple17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>> rightJoin(Select<R17> select, String alias) {
        return join(JoinType.RIGHT_OUTER, new SubselectAlias<>(select, alias));
    }

    public <R17> InJoinExpectingOn<ExpectingJoin17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>, Tuple17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>> fullOuterJoin(Alias<R17> alias) {
        return join(JoinType.FULL_OUTER, alias);
    }

    public <R17> InJoinExpectingOn<ExpectingJoin17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>, Tuple17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>> fullOuterJoin(Class<R17> rowClass, String alias) {
        return join(JoinType.FULL_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R17> InJoinExpectingOn<ExpectingJoin17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>, Tuple17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>> fullOuterJoin(TempTable<R17> tempTable, String alias) {
        return join(JoinType.FULL_OUTER, tempTable.as(alias));
    }

    public <R17> InJoinExpectingOn<ExpectingJoin17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>, Tuple17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>> fullOuterJoin(Select<R17> select, String alias) {
        return join(JoinType.FULL_OUTER, new SubselectAlias<>(select, alias));
    }

    private <R17> InJoinExpectingOn<ExpectingJoin17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>, Tuple17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>> join(JoinType joinType, Alias<R17> alias) {
        SelectStatement<Tuple17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>> select17 = new SelectStatement<>(
            scope().plus(alias),
            new TypeToken<Tuple17<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,R17>>() {}
                .where(new TypeParameter<RT1>() {}, Tuple16.type1(type()))
                .where(new TypeParameter<RT2>() {}, Tuple16.type2(type()))
                .where(new TypeParameter<RT3>() {}, Tuple16.type3(type()))
                .where(new TypeParameter<RT4>() {}, Tuple16.type4(type()))
                .where(new TypeParameter<RT5>() {}, Tuple16.type5(type()))
                .where(new TypeParameter<RT6>() {}, Tuple16.type6(type()))
                .where(new TypeParameter<RT7>() {}, Tuple16.type7(type()))
                .where(new TypeParameter<RT8>() {}, Tuple16.type8(type()))
                .where(new TypeParameter<RT9>() {}, Tuple16.type9(type()))
                .where(new TypeParameter<RT10>() {}, Tuple16.type10(type()))
                .where(new TypeParameter<RT11>() {}, Tuple16.type11(type()))
                .where(new TypeParameter<RT12>() {}, Tuple16.type12(type()))
                .where(new TypeParameter<RT13>() {}, Tuple16.type13(type()))
                .where(new TypeParameter<RT14>() {}, Tuple16.type14(type()))
                .where(new TypeParameter<RT15>() {}, Tuple16.type15(type()))
                .where(new TypeParameter<RT16>() {}, Tuple16.type16(type()))
                .where(new TypeParameter<R17>() {}, alias.type()),
            statement.from().join(joinType, alias),
            Projections.of17(statement.projection(), Projection.of(alias)));
        return new InJoinExpectingOn<>(select17, ExpectingJoin17::new);
    }
}
