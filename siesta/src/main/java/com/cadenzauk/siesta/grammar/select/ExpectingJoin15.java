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

import com.cadenzauk.core.tuple.Tuple15;
import com.cadenzauk.core.tuple.Tuple16;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.JoinType;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.Projections;
import com.cadenzauk.siesta.grammar.temp.TempTable;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

public class ExpectingJoin15<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15> extends InJoinExpectingAnd<ExpectingJoin15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15>,Tuple15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15>> {
    public ExpectingJoin15(SelectStatement<Tuple15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15>> statement) {
        super(statement);
    }

    public <R16> InJoinExpectingOn<ExpectingJoin16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>, Tuple16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>> join(Alias<R16> alias) {
        return join(JoinType.INNER, alias);
    }

    public <R16> InJoinExpectingOn<ExpectingJoin16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>, Tuple16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>> join(Class<R16> rowClass, String alias) {
        return join(JoinType.INNER, scope().database().table(rowClass).as(alias));
    }

    public <R16> InJoinExpectingOn<ExpectingJoin16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>, Tuple16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>> join(TempTable<R16> tempTable, String alias) {
        return join(JoinType.INNER, tempTable.as(alias));
    }

    public <R16> InJoinExpectingOn<ExpectingJoin16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>, Tuple16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>> join(Select<R16> select, String alias) {
        return join(JoinType.INNER, new SubselectAlias<>(select, alias));
    }

    public <R16> InJoinExpectingOn<ExpectingJoin16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>, Tuple16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>> leftJoin(Alias<R16> alias) {
        return join(JoinType.LEFT_OUTER, alias);
    }

    public <R16> InJoinExpectingOn<ExpectingJoin16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>, Tuple16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>> leftJoin(Class<R16> rowClass, String alias) {
        return join(JoinType.LEFT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R16> InJoinExpectingOn<ExpectingJoin16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>, Tuple16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>> leftJoin(TempTable<R16> tempTable, String alias) {
        return join(JoinType.LEFT_OUTER, tempTable.as(alias));
    }

    public <R16> InJoinExpectingOn<ExpectingJoin16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>, Tuple16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>> leftJoin(Select<R16> select, String alias) {
        return join(JoinType.LEFT_OUTER, new SubselectAlias<>(select, alias));
    }

    public <R16> InJoinExpectingOn<ExpectingJoin16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>, Tuple16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>> rightJoin(Alias<R16> alias) {
        return join(JoinType.RIGHT_OUTER, alias);
    }

    public <R16> InJoinExpectingOn<ExpectingJoin16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>, Tuple16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>> rightJoin(Class<R16> rowClass, String alias) {
        return join(JoinType.RIGHT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R16> InJoinExpectingOn<ExpectingJoin16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>, Tuple16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>> rightJoin(TempTable<R16> tempTable, String alias) {
        return join(JoinType.RIGHT_OUTER, tempTable.as(alias));
    }

    public <R16> InJoinExpectingOn<ExpectingJoin16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>, Tuple16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>> rightJoin(Select<R16> select, String alias) {
        return join(JoinType.RIGHT_OUTER, new SubselectAlias<>(select, alias));
    }

    public <R16> InJoinExpectingOn<ExpectingJoin16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>, Tuple16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>> fullOuterJoin(Alias<R16> alias) {
        return join(JoinType.FULL_OUTER, alias);
    }

    public <R16> InJoinExpectingOn<ExpectingJoin16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>, Tuple16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>> fullOuterJoin(Class<R16> rowClass, String alias) {
        return join(JoinType.FULL_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R16> InJoinExpectingOn<ExpectingJoin16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>, Tuple16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>> fullOuterJoin(TempTable<R16> tempTable, String alias) {
        return join(JoinType.FULL_OUTER, tempTable.as(alias));
    }

    public <R16> InJoinExpectingOn<ExpectingJoin16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>, Tuple16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>> fullOuterJoin(Select<R16> select, String alias) {
        return join(JoinType.FULL_OUTER, new SubselectAlias<>(select, alias));
    }

    private <R16> InJoinExpectingOn<ExpectingJoin16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>, Tuple16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>> join(JoinType joinType, Alias<R16> alias) {
        SelectStatement<Tuple16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>> select16 = new SelectStatement<>(
            scope().plus(alias),
            new TypeToken<Tuple16<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,R16>>() {}
                .where(new TypeParameter<RT1>() {}, Tuple15.type1(type()))
                .where(new TypeParameter<RT2>() {}, Tuple15.type2(type()))
                .where(new TypeParameter<RT3>() {}, Tuple15.type3(type()))
                .where(new TypeParameter<RT4>() {}, Tuple15.type4(type()))
                .where(new TypeParameter<RT5>() {}, Tuple15.type5(type()))
                .where(new TypeParameter<RT6>() {}, Tuple15.type6(type()))
                .where(new TypeParameter<RT7>() {}, Tuple15.type7(type()))
                .where(new TypeParameter<RT8>() {}, Tuple15.type8(type()))
                .where(new TypeParameter<RT9>() {}, Tuple15.type9(type()))
                .where(new TypeParameter<RT10>() {}, Tuple15.type10(type()))
                .where(new TypeParameter<RT11>() {}, Tuple15.type11(type()))
                .where(new TypeParameter<RT12>() {}, Tuple15.type12(type()))
                .where(new TypeParameter<RT13>() {}, Tuple15.type13(type()))
                .where(new TypeParameter<RT14>() {}, Tuple15.type14(type()))
                .where(new TypeParameter<RT15>() {}, Tuple15.type15(type()))
                .where(new TypeParameter<R16>() {}, alias.type()),
            statement.from().join(joinType, alias),
            Projections.of16(statement.projection(), Projection.of(alias)));
        return new InJoinExpectingOn<>(select16, ExpectingJoin16::new);
    }
}
