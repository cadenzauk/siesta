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

import com.cadenzauk.core.tuple.Tuple8;
import com.cadenzauk.core.tuple.Tuple9;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.JoinType;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.Projections;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

public class ExpectingJoin8<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8> extends InJoinExpectingAnd<ExpectingJoin8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8>,Tuple8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8>> {
    public ExpectingJoin8(SelectStatement<Tuple8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8>> statement) {
        super(statement);
    }

    public <R9> InJoinExpectingOn<ExpectingJoin9<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,R9>, Tuple9<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,R9>> join(Alias<R9> alias) {
        return join(JoinType.INNER, alias);
    }

    public <R9> InJoinExpectingOn<ExpectingJoin9<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,R9>, Tuple9<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,R9>> join(Class<R9> rowClass, String alias) {
        return join(JoinType.INNER, scope().database().table(rowClass).as(alias));
    }

    public <R9> InJoinExpectingOn<ExpectingJoin9<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,R9>, Tuple9<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,R9>> join(Select<R9> select, String alias) {
        return join(JoinType.INNER, new SubselectAlias<>(select, alias));
    }

    public <R9> InJoinExpectingOn<ExpectingJoin9<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,R9>, Tuple9<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,R9>> leftJoin(Alias<R9> alias) {
        return join(JoinType.LEFT_OUTER, alias);
    }

    public <R9> InJoinExpectingOn<ExpectingJoin9<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,R9>, Tuple9<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,R9>> leftJoin(Class<R9> rowClass, String alias) {
        return join(JoinType.LEFT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R9> InJoinExpectingOn<ExpectingJoin9<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,R9>, Tuple9<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,R9>> leftJoin(Select<R9> select, String alias) {
        return join(JoinType.LEFT_OUTER, new SubselectAlias<>(select, alias));
    }

    public <R9> InJoinExpectingOn<ExpectingJoin9<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,R9>, Tuple9<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,R9>> rightJoin(Alias<R9> alias) {
        return join(JoinType.RIGHT_OUTER, alias);
    }

    public <R9> InJoinExpectingOn<ExpectingJoin9<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,R9>, Tuple9<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,R9>> rightJoin(Class<R9> rowClass, String alias) {
        return join(JoinType.RIGHT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R9> InJoinExpectingOn<ExpectingJoin9<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,R9>, Tuple9<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,R9>> rightJoin(Select<R9> select, String alias) {
        return join(JoinType.RIGHT_OUTER, new SubselectAlias<>(select, alias));
    }

    public <R9> InJoinExpectingOn<ExpectingJoin9<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,R9>, Tuple9<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,R9>> fullOuterJoin(Alias<R9> alias) {
        return join(JoinType.FULL_OUTER, alias);
    }

    public <R9> InJoinExpectingOn<ExpectingJoin9<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,R9>, Tuple9<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,R9>> fullOuterJoin(Class<R9> rowClass, String alias) {
        return join(JoinType.FULL_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R9> InJoinExpectingOn<ExpectingJoin9<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,R9>, Tuple9<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,R9>> fullOuterJoin(Select<R9> select, String alias) {
        return join(JoinType.FULL_OUTER, new SubselectAlias<>(select, alias));
    }

    private <R9> InJoinExpectingOn<ExpectingJoin9<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,R9>, Tuple9<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,R9>> join(JoinType joinType, Alias<R9> alias) {
        SelectStatement<Tuple9<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,R9>> select9 = new SelectStatement<>(
            scope().plus(alias),
            new TypeToken<Tuple9<RT1,RT2,RT3,RT4,RT5,RT6,RT7,RT8,R9>>() {}
                .where(new TypeParameter<RT1>() {}, Tuple8.type1(type()))
                .where(new TypeParameter<RT2>() {}, Tuple8.type2(type()))
                .where(new TypeParameter<RT3>() {}, Tuple8.type3(type()))
                .where(new TypeParameter<RT4>() {}, Tuple8.type4(type()))
                .where(new TypeParameter<RT5>() {}, Tuple8.type5(type()))
                .where(new TypeParameter<RT6>() {}, Tuple8.type6(type()))
                .where(new TypeParameter<RT7>() {}, Tuple8.type7(type()))
                .where(new TypeParameter<RT8>() {}, Tuple8.type8(type()))
                .where(new TypeParameter<R9>() {}, alias.type()),
            statement.from().join(joinType, alias),
            Projections.of9(statement.projection(), Projection.of(alias)));
        return new InJoinExpectingOn<>(select9, ExpectingJoin9::new);
    }
}
