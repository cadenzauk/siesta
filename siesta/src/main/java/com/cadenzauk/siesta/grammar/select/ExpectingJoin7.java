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

import com.cadenzauk.core.tuple.Tuple7;
import com.cadenzauk.core.tuple.Tuple8;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.JoinType;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.Projections;
import com.cadenzauk.siesta.grammar.temp.TempTable;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

public class ExpectingJoin7<RT1, RT2, RT3, RT4, RT5, RT6, RT7> extends InJoinExpectingAnd<ExpectingJoin7<RT1,RT2,RT3,RT4,RT5,RT6,RT7>,Tuple7<RT1,RT2,RT3,RT4,RT5,RT6,RT7>> {
    public ExpectingJoin7(SelectStatement<Tuple7<RT1,RT2,RT3,RT4,RT5,RT6,RT7>> statement) {
        super(statement);
    }

    public <R8> InJoinExpectingOn<ExpectingJoin8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>, Tuple8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>> join(Alias<R8> alias) {
        return join(JoinType.INNER, alias);
    }

    public <R8> InJoinExpectingOn<ExpectingJoin8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>, Tuple8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>> join(Class<R8> rowClass, String alias) {
        return join(JoinType.INNER, scope().database().table(rowClass).as(alias));
    }

    public <R8> InJoinExpectingOn<ExpectingJoin8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>, Tuple8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>> join(TempTable<R8> tempTable, String alias) {
        return join(JoinType.INNER, tempTable.as(alias));
    }

    public <R8> InJoinExpectingOn<ExpectingJoin8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>, Tuple8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>> join(Select<R8> select, String alias) {
        return join(JoinType.INNER, new SubselectAlias<>(select, alias));
    }

    public <R8> InJoinExpectingOn<ExpectingJoin8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>, Tuple8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>> leftJoin(Alias<R8> alias) {
        return join(JoinType.LEFT_OUTER, alias);
    }

    public <R8> InJoinExpectingOn<ExpectingJoin8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>, Tuple8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>> leftJoin(Class<R8> rowClass, String alias) {
        return join(JoinType.LEFT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R8> InJoinExpectingOn<ExpectingJoin8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>, Tuple8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>> leftJoin(TempTable<R8> tempTable, String alias) {
        return join(JoinType.LEFT_OUTER, tempTable.as(alias));
    }

    public <R8> InJoinExpectingOn<ExpectingJoin8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>, Tuple8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>> leftJoin(Select<R8> select, String alias) {
        return join(JoinType.LEFT_OUTER, new SubselectAlias<>(select, alias));
    }

    public <R8> InJoinExpectingOn<ExpectingJoin8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>, Tuple8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>> rightJoin(Alias<R8> alias) {
        return join(JoinType.RIGHT_OUTER, alias);
    }

    public <R8> InJoinExpectingOn<ExpectingJoin8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>, Tuple8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>> rightJoin(Class<R8> rowClass, String alias) {
        return join(JoinType.RIGHT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R8> InJoinExpectingOn<ExpectingJoin8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>, Tuple8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>> rightJoin(TempTable<R8> tempTable, String alias) {
        return join(JoinType.RIGHT_OUTER, tempTable.as(alias));
    }

    public <R8> InJoinExpectingOn<ExpectingJoin8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>, Tuple8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>> rightJoin(Select<R8> select, String alias) {
        return join(JoinType.RIGHT_OUTER, new SubselectAlias<>(select, alias));
    }

    public <R8> InJoinExpectingOn<ExpectingJoin8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>, Tuple8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>> fullOuterJoin(Alias<R8> alias) {
        return join(JoinType.FULL_OUTER, alias);
    }

    public <R8> InJoinExpectingOn<ExpectingJoin8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>, Tuple8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>> fullOuterJoin(Class<R8> rowClass, String alias) {
        return join(JoinType.FULL_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R8> InJoinExpectingOn<ExpectingJoin8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>, Tuple8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>> fullOuterJoin(TempTable<R8> tempTable, String alias) {
        return join(JoinType.FULL_OUTER, tempTable.as(alias));
    }

    public <R8> InJoinExpectingOn<ExpectingJoin8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>, Tuple8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>> fullOuterJoin(Select<R8> select, String alias) {
        return join(JoinType.FULL_OUTER, new SubselectAlias<>(select, alias));
    }

    private <R8> InJoinExpectingOn<ExpectingJoin8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>, Tuple8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>> join(JoinType joinType, Alias<R8> alias) {
        SelectStatement<Tuple8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>> select8 = new SelectStatement<>(
            scope().plus(alias),
            new TypeToken<Tuple8<RT1,RT2,RT3,RT4,RT5,RT6,RT7,R8>>() {}
                .where(new TypeParameter<RT1>() {}, Tuple7.type1(type()))
                .where(new TypeParameter<RT2>() {}, Tuple7.type2(type()))
                .where(new TypeParameter<RT3>() {}, Tuple7.type3(type()))
                .where(new TypeParameter<RT4>() {}, Tuple7.type4(type()))
                .where(new TypeParameter<RT5>() {}, Tuple7.type5(type()))
                .where(new TypeParameter<RT6>() {}, Tuple7.type6(type()))
                .where(new TypeParameter<RT7>() {}, Tuple7.type7(type()))
                .where(new TypeParameter<R8>() {}, alias.type()),
            statement.from().join(joinType, alias),
            Projections.of8(statement.projection(), Projection.of(alias)));
        return new InJoinExpectingOn<>(select8, ExpectingJoin8::new);
    }
}
