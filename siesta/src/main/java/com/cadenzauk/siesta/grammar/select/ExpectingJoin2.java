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

import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.core.tuple.Tuple3;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.JoinType;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.Projections;
import com.cadenzauk.siesta.grammar.temp.TempTable;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

public class ExpectingJoin2<RT1, RT2> extends InJoinExpectingAnd<ExpectingJoin2<RT1,RT2>,Tuple2<RT1,RT2>> {
    public ExpectingJoin2(SelectStatement<Tuple2<RT1,RT2>> statement) {
        super(statement);
    }

    public <R3> InJoinExpectingOn<ExpectingJoin3<RT1,RT2,R3>, Tuple3<RT1,RT2,R3>> join(Alias<R3> alias) {
        return join(JoinType.INNER, alias);
    }

    public <R3> InJoinExpectingOn<ExpectingJoin3<RT1,RT2,R3>, Tuple3<RT1,RT2,R3>> join(Class<R3> rowClass, String alias) {
        return join(JoinType.INNER, scope().database().table(rowClass).as(alias));
    }

    public <R3> InJoinExpectingOn<ExpectingJoin3<RT1,RT2,R3>, Tuple3<RT1,RT2,R3>> join(TempTable<R3> tempTable, String alias) {
        return join(JoinType.INNER, tempTable.as(alias));
    }

    public <R3> InJoinExpectingOn<ExpectingJoin3<RT1,RT2,R3>, Tuple3<RT1,RT2,R3>> join(Select<R3> select, String alias) {
        return join(JoinType.INNER, new SubselectAlias<>(select, alias));
    }

    public <R3> InJoinExpectingOn<ExpectingJoin3<RT1,RT2,R3>, Tuple3<RT1,RT2,R3>> leftJoin(Alias<R3> alias) {
        return join(JoinType.LEFT_OUTER, alias);
    }

    public <R3> InJoinExpectingOn<ExpectingJoin3<RT1,RT2,R3>, Tuple3<RT1,RT2,R3>> leftJoin(Class<R3> rowClass, String alias) {
        return join(JoinType.LEFT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R3> InJoinExpectingOn<ExpectingJoin3<RT1,RT2,R3>, Tuple3<RT1,RT2,R3>> leftJoin(TempTable<R3> tempTable, String alias) {
        return join(JoinType.LEFT_OUTER, tempTable.as(alias));
    }

    public <R3> InJoinExpectingOn<ExpectingJoin3<RT1,RT2,R3>, Tuple3<RT1,RT2,R3>> leftJoin(Select<R3> select, String alias) {
        return join(JoinType.LEFT_OUTER, new SubselectAlias<>(select, alias));
    }

    public <R3> InJoinExpectingOn<ExpectingJoin3<RT1,RT2,R3>, Tuple3<RT1,RT2,R3>> rightJoin(Alias<R3> alias) {
        return join(JoinType.RIGHT_OUTER, alias);
    }

    public <R3> InJoinExpectingOn<ExpectingJoin3<RT1,RT2,R3>, Tuple3<RT1,RT2,R3>> rightJoin(Class<R3> rowClass, String alias) {
        return join(JoinType.RIGHT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R3> InJoinExpectingOn<ExpectingJoin3<RT1,RT2,R3>, Tuple3<RT1,RT2,R3>> rightJoin(TempTable<R3> tempTable, String alias) {
        return join(JoinType.RIGHT_OUTER, tempTable.as(alias));
    }

    public <R3> InJoinExpectingOn<ExpectingJoin3<RT1,RT2,R3>, Tuple3<RT1,RT2,R3>> rightJoin(Select<R3> select, String alias) {
        return join(JoinType.RIGHT_OUTER, new SubselectAlias<>(select, alias));
    }

    public <R3> InJoinExpectingOn<ExpectingJoin3<RT1,RT2,R3>, Tuple3<RT1,RT2,R3>> fullOuterJoin(Alias<R3> alias) {
        return join(JoinType.FULL_OUTER, alias);
    }

    public <R3> InJoinExpectingOn<ExpectingJoin3<RT1,RT2,R3>, Tuple3<RT1,RT2,R3>> fullOuterJoin(Class<R3> rowClass, String alias) {
        return join(JoinType.FULL_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R3> InJoinExpectingOn<ExpectingJoin3<RT1,RT2,R3>, Tuple3<RT1,RT2,R3>> fullOuterJoin(TempTable<R3> tempTable, String alias) {
        return join(JoinType.FULL_OUTER, tempTable.as(alias));
    }

    public <R3> InJoinExpectingOn<ExpectingJoin3<RT1,RT2,R3>, Tuple3<RT1,RT2,R3>> fullOuterJoin(Select<R3> select, String alias) {
        return join(JoinType.FULL_OUTER, new SubselectAlias<>(select, alias));
    }

    private <R3> InJoinExpectingOn<ExpectingJoin3<RT1,RT2,R3>, Tuple3<RT1,RT2,R3>> join(JoinType joinType, Alias<R3> alias) {
        SelectStatement<Tuple3<RT1,RT2,R3>> select3 = new SelectStatement<>(
            scope().plus(alias),
            new TypeToken<Tuple3<RT1,RT2,R3>>() {}
                .where(new TypeParameter<RT1>() {}, Tuple2.type1(type()))
                .where(new TypeParameter<RT2>() {}, Tuple2.type2(type()))
                .where(new TypeParameter<R3>() {}, alias.type()),
            statement.from().join(joinType, alias),
            Projections.of3(statement.projection(), Projection.of(alias)));
        return new InJoinExpectingOn<>(select3, ExpectingJoin3::new);
    }
}
