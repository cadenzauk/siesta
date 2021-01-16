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

import com.cadenzauk.core.tuple.Tuple6;
import com.cadenzauk.core.tuple.Tuple7;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.JoinType;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.Projections;
import com.cadenzauk.siesta.grammar.temp.TempTable;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

public class ExpectingJoin6<RT1, RT2, RT3, RT4, RT5, RT6> extends InJoinExpectingAnd<ExpectingJoin6<RT1,RT2,RT3,RT4,RT5,RT6>,Tuple6<RT1,RT2,RT3,RT4,RT5,RT6>> {
    public ExpectingJoin6(SelectStatement<Tuple6<RT1,RT2,RT3,RT4,RT5,RT6>> statement) {
        super(statement);
    }

    public <R7> InJoinExpectingOn<ExpectingJoin7<RT1,RT2,RT3,RT4,RT5,RT6,R7>, Tuple7<RT1,RT2,RT3,RT4,RT5,RT6,R7>> join(Alias<R7> alias) {
        return join(JoinType.INNER, alias);
    }

    public <R7> InJoinExpectingOn<ExpectingJoin7<RT1,RT2,RT3,RT4,RT5,RT6,R7>, Tuple7<RT1,RT2,RT3,RT4,RT5,RT6,R7>> join(Class<R7> rowClass, String alias) {
        return join(JoinType.INNER, scope().database().table(rowClass).as(alias));
    }

    public <R7> InJoinExpectingOn<ExpectingJoin7<RT1,RT2,RT3,RT4,RT5,RT6,R7>, Tuple7<RT1,RT2,RT3,RT4,RT5,RT6,R7>> join(TempTable<R7> tempTable, String alias) {
        return join(JoinType.INNER, tempTable.as(alias));
    }

    public <R7> InJoinExpectingOn<ExpectingJoin7<RT1,RT2,RT3,RT4,RT5,RT6,R7>, Tuple7<RT1,RT2,RT3,RT4,RT5,RT6,R7>> join(Select<R7> select, String alias) {
        return join(JoinType.INNER, new SubselectAlias<>(select, alias));
    }

    public <R7> InJoinExpectingOn<ExpectingJoin7<RT1,RT2,RT3,RT4,RT5,RT6,R7>, Tuple7<RT1,RT2,RT3,RT4,RT5,RT6,R7>> leftJoin(Alias<R7> alias) {
        return join(JoinType.LEFT_OUTER, alias);
    }

    public <R7> InJoinExpectingOn<ExpectingJoin7<RT1,RT2,RT3,RT4,RT5,RT6,R7>, Tuple7<RT1,RT2,RT3,RT4,RT5,RT6,R7>> leftJoin(Class<R7> rowClass, String alias) {
        return join(JoinType.LEFT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R7> InJoinExpectingOn<ExpectingJoin7<RT1,RT2,RT3,RT4,RT5,RT6,R7>, Tuple7<RT1,RT2,RT3,RT4,RT5,RT6,R7>> leftJoin(TempTable<R7> tempTable, String alias) {
        return join(JoinType.LEFT_OUTER, tempTable.as(alias));
    }

    public <R7> InJoinExpectingOn<ExpectingJoin7<RT1,RT2,RT3,RT4,RT5,RT6,R7>, Tuple7<RT1,RT2,RT3,RT4,RT5,RT6,R7>> leftJoin(Select<R7> select, String alias) {
        return join(JoinType.LEFT_OUTER, new SubselectAlias<>(select, alias));
    }

    public <R7> InJoinExpectingOn<ExpectingJoin7<RT1,RT2,RT3,RT4,RT5,RT6,R7>, Tuple7<RT1,RT2,RT3,RT4,RT5,RT6,R7>> rightJoin(Alias<R7> alias) {
        return join(JoinType.RIGHT_OUTER, alias);
    }

    public <R7> InJoinExpectingOn<ExpectingJoin7<RT1,RT2,RT3,RT4,RT5,RT6,R7>, Tuple7<RT1,RT2,RT3,RT4,RT5,RT6,R7>> rightJoin(Class<R7> rowClass, String alias) {
        return join(JoinType.RIGHT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R7> InJoinExpectingOn<ExpectingJoin7<RT1,RT2,RT3,RT4,RT5,RT6,R7>, Tuple7<RT1,RT2,RT3,RT4,RT5,RT6,R7>> rightJoin(TempTable<R7> tempTable, String alias) {
        return join(JoinType.RIGHT_OUTER, tempTable.as(alias));
    }

    public <R7> InJoinExpectingOn<ExpectingJoin7<RT1,RT2,RT3,RT4,RT5,RT6,R7>, Tuple7<RT1,RT2,RT3,RT4,RT5,RT6,R7>> rightJoin(Select<R7> select, String alias) {
        return join(JoinType.RIGHT_OUTER, new SubselectAlias<>(select, alias));
    }

    public <R7> InJoinExpectingOn<ExpectingJoin7<RT1,RT2,RT3,RT4,RT5,RT6,R7>, Tuple7<RT1,RT2,RT3,RT4,RT5,RT6,R7>> fullOuterJoin(Alias<R7> alias) {
        return join(JoinType.FULL_OUTER, alias);
    }

    public <R7> InJoinExpectingOn<ExpectingJoin7<RT1,RT2,RT3,RT4,RT5,RT6,R7>, Tuple7<RT1,RT2,RT3,RT4,RT5,RT6,R7>> fullOuterJoin(Class<R7> rowClass, String alias) {
        return join(JoinType.FULL_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R7> InJoinExpectingOn<ExpectingJoin7<RT1,RT2,RT3,RT4,RT5,RT6,R7>, Tuple7<RT1,RT2,RT3,RT4,RT5,RT6,R7>> fullOuterJoin(TempTable<R7> tempTable, String alias) {
        return join(JoinType.FULL_OUTER, tempTable.as(alias));
    }

    public <R7> InJoinExpectingOn<ExpectingJoin7<RT1,RT2,RT3,RT4,RT5,RT6,R7>, Tuple7<RT1,RT2,RT3,RT4,RT5,RT6,R7>> fullOuterJoin(Select<R7> select, String alias) {
        return join(JoinType.FULL_OUTER, new SubselectAlias<>(select, alias));
    }

    private <R7> InJoinExpectingOn<ExpectingJoin7<RT1,RT2,RT3,RT4,RT5,RT6,R7>, Tuple7<RT1,RT2,RT3,RT4,RT5,RT6,R7>> join(JoinType joinType, Alias<R7> alias) {
        SelectStatement<Tuple7<RT1,RT2,RT3,RT4,RT5,RT6,R7>> select7 = new SelectStatement<>(
            scope().plus(alias),
            new TypeToken<Tuple7<RT1,RT2,RT3,RT4,RT5,RT6,R7>>() {}
                .where(new TypeParameter<RT1>() {}, Tuple6.type1(type()))
                .where(new TypeParameter<RT2>() {}, Tuple6.type2(type()))
                .where(new TypeParameter<RT3>() {}, Tuple6.type3(type()))
                .where(new TypeParameter<RT4>() {}, Tuple6.type4(type()))
                .where(new TypeParameter<RT5>() {}, Tuple6.type5(type()))
                .where(new TypeParameter<RT6>() {}, Tuple6.type6(type()))
                .where(new TypeParameter<R7>() {}, alias.type()),
            statement.from().join(joinType, alias),
            Projections.of7(statement.projection(), Projection.of(alias)));
        return new InJoinExpectingOn<>(select7, ExpectingJoin7::new);
    }
}
