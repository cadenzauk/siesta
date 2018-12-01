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

import com.cadenzauk.core.tuple.Tuple10;
import com.cadenzauk.core.tuple.Tuple11;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.JoinType;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.RowMappers;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

public class ExpectingJoin10<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10> extends InJoinExpectingAnd<ExpectingJoin10<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10>,Tuple10<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10>> {
    public ExpectingJoin10(SelectStatement<Tuple10<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10>> statement) {
        super(statement);
    }

    public <R11> InJoinExpectingOn<ExpectingJoin11<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,R11>, Tuple11<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,R11>> join(Alias<R11> alias) {
        return join(JoinType.INNER, alias);
    }

    public <R11> InJoinExpectingOn<ExpectingJoin11<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,R11>, Tuple11<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,R11>> join(Class<R11> rowClass, String alias) {
        return join(JoinType.INNER, scope().database().table(rowClass).as(alias));
    }

    public <R11> InJoinExpectingOn<ExpectingJoin11<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,R11>, Tuple11<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,R11>> leftJoin(Alias<R11> alias) {
        return join(JoinType.LEFT_OUTER, alias);
    }

    public <R11> InJoinExpectingOn<ExpectingJoin11<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,R11>, Tuple11<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,R11>> leftJoin(Class<R11> rowClass, String alias) {
        return join(JoinType.LEFT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R11> InJoinExpectingOn<ExpectingJoin11<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,R11>, Tuple11<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,R11>> rightJoin(Alias<R11> alias) {
        return join(JoinType.RIGHT_OUTER, alias);
    }

    public <R11> InJoinExpectingOn<ExpectingJoin11<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,R11>, Tuple11<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,R11>> rightJoin(Class<R11> rowClass, String alias) {
        return join(JoinType.RIGHT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R11> InJoinExpectingOn<ExpectingJoin11<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,R11>, Tuple11<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,R11>> fullOuterJoin(Alias<R11> alias) {
        return join(JoinType.FULL_OUTER, alias);
    }

    public <R11> InJoinExpectingOn<ExpectingJoin11<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,R11>, Tuple11<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,R11>> fullOuterJoin(Class<R11> rowClass, String alias) {
        return join(JoinType.FULL_OUTER, scope().database().table(rowClass).as(alias));
    }

    private <R11> InJoinExpectingOn<ExpectingJoin11<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,R11>, Tuple11<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,R11>> join(JoinType joinType, Alias<R11> alias) {
        SelectStatement<Tuple11<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,R11>> select11 = new SelectStatement<>(
            scope().plus(alias),
            new TypeToken<Tuple11<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,RT9,RT10,R11>>() {}
                .where(new TypeParameter<RT1>() {}, Tuple10.type1(type()))
                .where(new TypeParameter<RT2>() {}, Tuple10.type2(type()))
                .where(new TypeParameter<RT3>() {}, Tuple10.type3(type()))
                .where(new TypeParameter<RT4>() {}, Tuple10.type4(type()))
                .where(new TypeParameter<RT5>() {}, Tuple10.type5(type()))
                .where(new TypeParameter<RT6>() {}, Tuple10.type6(type()))
                .where(new TypeParameter<RT7>() {}, Tuple10.type7(type()))
                .where(new TypeParameter<RT8>() {}, Tuple10.type8(type()))
                .where(new TypeParameter<RT9>() {}, Tuple10.type9(type()))
                .where(new TypeParameter<RT10>() {}, Tuple10.type10(type()))
                .where(new TypeParameter<R11>() {}, alias.type()),
            statement.from().join(joinType, alias),
            RowMappers.add11th(statement.rowMapper(), alias.rowMapper()),
            Projection.of(statement.projection(), Projection.of(alias)));
        return new InJoinExpectingOn<>(select11, ExpectingJoin11::new);
    }
}
