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

import com.cadenzauk.core.tuple.Tuple11;
import com.cadenzauk.core.tuple.Tuple12;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.JoinType;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.Projections;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

public class ExpectingJoin11<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11> extends InJoinExpectingAnd<ExpectingJoin11<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11>,Tuple11<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11>> {
    public ExpectingJoin11(SelectStatement<Tuple11<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11>> statement) {
        super(statement);
    }

    public <R12> InJoinExpectingOn<ExpectingJoin12<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,R12>, Tuple12<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,R12>> join(Alias<R12> alias) {
        return join(JoinType.INNER, alias);
    }

    public <R12> InJoinExpectingOn<ExpectingJoin12<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,R12>, Tuple12<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,R12>> join(Class<R12> rowClass, String alias) {
        return join(JoinType.INNER, scope().database().table(rowClass).as(alias));
    }

    public <R12> InJoinExpectingOn<ExpectingJoin12<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,R12>, Tuple12<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,R12>> join(Select<R12> select, String alias) {
        return join(JoinType.INNER, new SubselectAlias<>(select, alias));
    }

    public <R12> InJoinExpectingOn<ExpectingJoin12<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,R12>, Tuple12<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,R12>> leftJoin(Alias<R12> alias) {
        return join(JoinType.LEFT_OUTER, alias);
    }

    public <R12> InJoinExpectingOn<ExpectingJoin12<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,R12>, Tuple12<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,R12>> leftJoin(Class<R12> rowClass, String alias) {
        return join(JoinType.LEFT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R12> InJoinExpectingOn<ExpectingJoin12<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,R12>, Tuple12<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,R12>> leftJoin(Select<R12> select, String alias) {
        return join(JoinType.LEFT_OUTER, new SubselectAlias<>(select, alias));
    }

    public <R12> InJoinExpectingOn<ExpectingJoin12<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,R12>, Tuple12<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,R12>> rightJoin(Alias<R12> alias) {
        return join(JoinType.RIGHT_OUTER, alias);
    }

    public <R12> InJoinExpectingOn<ExpectingJoin12<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,R12>, Tuple12<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,R12>> rightJoin(Class<R12> rowClass, String alias) {
        return join(JoinType.RIGHT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R12> InJoinExpectingOn<ExpectingJoin12<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,R12>, Tuple12<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,R12>> rightJoin(Select<R12> select, String alias) {
        return join(JoinType.RIGHT_OUTER, new SubselectAlias<>(select, alias));
    }

    public <R12> InJoinExpectingOn<ExpectingJoin12<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,R12>, Tuple12<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,R12>> fullOuterJoin(Alias<R12> alias) {
        return join(JoinType.FULL_OUTER, alias);
    }

    public <R12> InJoinExpectingOn<ExpectingJoin12<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,R12>, Tuple12<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,R12>> fullOuterJoin(Class<R12> rowClass, String alias) {
        return join(JoinType.FULL_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R12> InJoinExpectingOn<ExpectingJoin12<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,R12>, Tuple12<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,R12>> fullOuterJoin(Select<R12> select, String alias) {
        return join(JoinType.FULL_OUTER, new SubselectAlias<>(select, alias));
    }

    private <R12> InJoinExpectingOn<ExpectingJoin12<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,R12>, Tuple12<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,R12>> join(JoinType joinType, Alias<R12> alias) {
        SelectStatement<Tuple12<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,R12>> select12 = new SelectStatement<>(
            scope().plus(alias),
            new TypeToken<Tuple12<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,RT11,R12>>() {}
                .where(new TypeParameter<RT1>() {}, Tuple11.type1(type()))
                .where(new TypeParameter<RT2>() {}, Tuple11.type2(type()))
                .where(new TypeParameter<RT3>() {}, Tuple11.type3(type()))
                .where(new TypeParameter<RT4>() {}, Tuple11.type4(type()))
                .where(new TypeParameter<RT5>() {}, Tuple11.type5(type()))
                .where(new TypeParameter<RT6>() {}, Tuple11.type6(type()))
                .where(new TypeParameter<RT7>() {}, Tuple11.type7(type()))
                .where(new TypeParameter<RT8>() {}, Tuple11.type8(type()))
                .where(new TypeParameter<RT9>() {}, Tuple11.type9(type()))
                .where(new TypeParameter<RT10>() {}, Tuple11.type10(type()))
                .where(new TypeParameter<RT11>() {}, Tuple11.type11(type()))
                .where(new TypeParameter<R12>() {}, alias.type()),
            statement.from().join(joinType, alias),
            Projections.of12(statement.projection(), Projection.of(alias)));
        return new InJoinExpectingOn<>(select12, ExpectingJoin12::new);
    }
}
