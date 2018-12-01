/*
 * Copyright (c) 2017, 2018 Cadenza United Kingdom Limited
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

import com.cadenzauk.core.tuple.Tuple18;
import com.cadenzauk.core.tuple.Tuple19;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.JoinType;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.RowMappers;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

public class ExpectingJoin18<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18> extends InJoinExpectingAnd<ExpectingJoin18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18>,Tuple18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18>> {
    public ExpectingJoin18(SelectStatement<Tuple18<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18>> statement) {
        super(statement);
    }

    public <R19> InJoinExpectingOn<ExpectingJoin19<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,R19>, Tuple19<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,R19>> join(Alias<R19> alias) {
        return join(JoinType.INNER, alias);
    }

    public <R19> InJoinExpectingOn<ExpectingJoin19<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,R19>, Tuple19<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,R19>> join(Class<R19> rowClass, String alias) {
        return join(JoinType.INNER, scope().database().table(rowClass).as(alias));
    }

    public <R19> InJoinExpectingOn<ExpectingJoin19<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,R19>, Tuple19<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,R19>> leftJoin(Alias<R19> alias) {
        return join(JoinType.LEFT_OUTER, alias);
    }

    public <R19> InJoinExpectingOn<ExpectingJoin19<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,R19>, Tuple19<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,R19>> leftJoin(Class<R19> rowClass, String alias) {
        return join(JoinType.LEFT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R19> InJoinExpectingOn<ExpectingJoin19<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,R19>, Tuple19<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,R19>> rightJoin(Alias<R19> alias) {
        return join(JoinType.RIGHT_OUTER, alias);
    }

    public <R19> InJoinExpectingOn<ExpectingJoin19<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,R19>, Tuple19<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,R19>> rightJoin(Class<R19> rowClass, String alias) {
        return join(JoinType.RIGHT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R19> InJoinExpectingOn<ExpectingJoin19<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,R19>, Tuple19<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,R19>> fullOuterJoin(Alias<R19> alias) {
        return join(JoinType.FULL_OUTER, alias);
    }

    public <R19> InJoinExpectingOn<ExpectingJoin19<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,R19>, Tuple19<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,R19>> fullOuterJoin(Class<R19> rowClass, String alias) {
        return join(JoinType.FULL_OUTER, scope().database().table(rowClass).as(alias));
    }

    private <R19> InJoinExpectingOn<ExpectingJoin19<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,R19>, Tuple19<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,R19>> join(JoinType joinType, Alias<R19> alias) {
        SelectStatement<Tuple19<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,R19>> select19 = new SelectStatement<>(
            scope().plus(alias),
            new TypeToken<Tuple19<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,RT12,RT13,RT14,RT15,RT16,RT17,RT18,R19>>() {}
                .where(new TypeParameter<RT1>() {}, Tuple18.type1(type()))
                .where(new TypeParameter<RT2>() {}, Tuple18.type2(type()))
                .where(new TypeParameter<RT3>() {}, Tuple18.type3(type()))
                .where(new TypeParameter<RT4>() {}, Tuple18.type4(type()))
                .where(new TypeParameter<RT5>() {}, Tuple18.type5(type()))
                .where(new TypeParameter<RT6>() {}, Tuple18.type6(type()))
                .where(new TypeParameter<RT7>() {}, Tuple18.type7(type()))
                .where(new TypeParameter<RT8>() {}, Tuple18.type8(type()))
                .where(new TypeParameter<RT9>() {}, Tuple18.type9(type()))
                .where(new TypeParameter<RT10>() {}, Tuple18.type10(type()))
                .where(new TypeParameter<RT11>() {}, Tuple18.type11(type()))
                .where(new TypeParameter<RT12>() {}, Tuple18.type12(type()))
                .where(new TypeParameter<RT13>() {}, Tuple18.type13(type()))
                .where(new TypeParameter<RT14>() {}, Tuple18.type14(type()))
                .where(new TypeParameter<RT15>() {}, Tuple18.type15(type()))
                .where(new TypeParameter<RT16>() {}, Tuple18.type16(type()))
                .where(new TypeParameter<RT17>() {}, Tuple18.type17(type()))
                .where(new TypeParameter<RT18>() {}, Tuple18.type18(type()))
                .where(new TypeParameter<R19>() {}, alias.type()),
            statement.from().join(joinType, alias),
            RowMappers.add19th(statement.rowMapper(), alias.rowMapper()),
            Projection.of(statement.projection(), Projection.of(alias)));
        return new InJoinExpectingOn<>(select19, ExpectingJoin19::new);
    }
}
