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

import com.cadenzauk.core.tuple.Tuple14;
import com.cadenzauk.core.tuple.Tuple15;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.JoinType;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.Projections;
import com.cadenzauk.siesta.grammar.temp.TempTable;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

public class ExpectingJoin14<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14> extends InJoinExpectingAnd<ExpectingJoin14<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14>,Tuple14<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14>> {
    public ExpectingJoin14(SelectStatement<Tuple14<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14>> statement) {
        super(statement);
    }

    public <R15> InJoinExpectingOn<ExpectingJoin15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>, Tuple15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>> join(Alias<R15> alias) {
        return join(JoinType.INNER, alias);
    }

    public <R15> InJoinExpectingOn<ExpectingJoin15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>, Tuple15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>> join(Class<R15> rowClass, String alias) {
        return join(JoinType.INNER, scope().database().table(rowClass).as(alias));
    }

    public <R15> InJoinExpectingOn<ExpectingJoin15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>, Tuple15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>> join(TempTable<R15> tempTable, String alias) {
        return join(JoinType.INNER, tempTable.as(alias));
    }

    public <R15> InJoinExpectingOn<ExpectingJoin15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>, Tuple15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>> join(Select<R15> select, String alias) {
        return join(JoinType.INNER, new SubselectAlias<>(select, alias));
    }

    public <R15> InJoinExpectingOn<ExpectingJoin15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>, Tuple15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>> leftJoin(Alias<R15> alias) {
        return join(JoinType.LEFT_OUTER, alias);
    }

    public <R15> InJoinExpectingOn<ExpectingJoin15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>, Tuple15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>> leftJoin(Class<R15> rowClass, String alias) {
        return join(JoinType.LEFT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R15> InJoinExpectingOn<ExpectingJoin15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>, Tuple15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>> leftJoin(TempTable<R15> tempTable, String alias) {
        return join(JoinType.LEFT_OUTER, tempTable.as(alias));
    }

    public <R15> InJoinExpectingOn<ExpectingJoin15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>, Tuple15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>> leftJoin(Select<R15> select, String alias) {
        return join(JoinType.LEFT_OUTER, new SubselectAlias<>(select, alias));
    }

    public <R15> InJoinExpectingOn<ExpectingJoin15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>, Tuple15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>> rightJoin(Alias<R15> alias) {
        return join(JoinType.RIGHT_OUTER, alias);
    }

    public <R15> InJoinExpectingOn<ExpectingJoin15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>, Tuple15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>> rightJoin(Class<R15> rowClass, String alias) {
        return join(JoinType.RIGHT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R15> InJoinExpectingOn<ExpectingJoin15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>, Tuple15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>> rightJoin(TempTable<R15> tempTable, String alias) {
        return join(JoinType.RIGHT_OUTER, tempTable.as(alias));
    }

    public <R15> InJoinExpectingOn<ExpectingJoin15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>, Tuple15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>> rightJoin(Select<R15> select, String alias) {
        return join(JoinType.RIGHT_OUTER, new SubselectAlias<>(select, alias));
    }

    public <R15> InJoinExpectingOn<ExpectingJoin15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>, Tuple15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>> fullOuterJoin(Alias<R15> alias) {
        return join(JoinType.FULL_OUTER, alias);
    }

    public <R15> InJoinExpectingOn<ExpectingJoin15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>, Tuple15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>> fullOuterJoin(Class<R15> rowClass, String alias) {
        return join(JoinType.FULL_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R15> InJoinExpectingOn<ExpectingJoin15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>, Tuple15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>> fullOuterJoin(TempTable<R15> tempTable, String alias) {
        return join(JoinType.FULL_OUTER, tempTable.as(alias));
    }

    public <R15> InJoinExpectingOn<ExpectingJoin15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>, Tuple15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>> fullOuterJoin(Select<R15> select, String alias) {
        return join(JoinType.FULL_OUTER, new SubselectAlias<>(select, alias));
    }

    private <R15> InJoinExpectingOn<ExpectingJoin15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>, Tuple15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>> join(JoinType joinType, Alias<R15> alias) {
        SelectStatement<Tuple15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>> select15 = new SelectStatement<>(
            scope().plus(alias),
            new TypeToken<Tuple15<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,R15>>() {}
                .where(new TypeParameter<RT1>() {}, Tuple14.type1(type()))
                .where(new TypeParameter<RT2>() {}, Tuple14.type2(type()))
                .where(new TypeParameter<RT3>() {}, Tuple14.type3(type()))
                .where(new TypeParameter<RT4>() {}, Tuple14.type4(type()))
                .where(new TypeParameter<RT5>() {}, Tuple14.type5(type()))
                .where(new TypeParameter<RT6>() {}, Tuple14.type6(type()))
                .where(new TypeParameter<RT7>() {}, Tuple14.type7(type()))
                .where(new TypeParameter<RT8>() {}, Tuple14.type8(type()))
                .where(new TypeParameter<RT9>() {}, Tuple14.type9(type()))
                .where(new TypeParameter<RT10>() {}, Tuple14.type10(type()))
                .where(new TypeParameter<RT11>() {}, Tuple14.type11(type()))
                .where(new TypeParameter<RT12>() {}, Tuple14.type12(type()))
                .where(new TypeParameter<RT13>() {}, Tuple14.type13(type()))
                .where(new TypeParameter<RT14>() {}, Tuple14.type14(type()))
                .where(new TypeParameter<R15>() {}, alias.type()),
            statement.from().join(joinType, alias),
            Projections.of15(statement.projection(), Projection.of(alias)));
        return new InJoinExpectingOn<>(select15, ExpectingJoin15::new);
    }
}
