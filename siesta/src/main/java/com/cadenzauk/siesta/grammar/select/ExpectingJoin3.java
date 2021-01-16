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

import com.cadenzauk.core.tuple.Tuple3;
import com.cadenzauk.core.tuple.Tuple4;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.JoinType;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.Projections;
import com.cadenzauk.siesta.grammar.temp.TempTable;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

public class ExpectingJoin3<RT1, RT2, RT3> extends InJoinExpectingAnd<ExpectingJoin3<RT1,RT2,RT3>,Tuple3<RT1,RT2,RT3>> {
    public ExpectingJoin3(SelectStatement<Tuple3<RT1,RT2,RT3>> statement) {
        super(statement);
    }

    public <R4> InJoinExpectingOn<ExpectingJoin4<RT1,RT2,RT3,R4>, Tuple4<RT1,RT2,RT3,R4>> join(Alias<R4> alias) {
        return join(JoinType.INNER, alias);
    }

    public <R4> InJoinExpectingOn<ExpectingJoin4<RT1,RT2,RT3,R4>, Tuple4<RT1,RT2,RT3,R4>> join(Class<R4> rowClass, String alias) {
        return join(JoinType.INNER, scope().database().table(rowClass).as(alias));
    }

    public <R4> InJoinExpectingOn<ExpectingJoin4<RT1,RT2,RT3,R4>, Tuple4<RT1,RT2,RT3,R4>> join(TempTable<R4> tempTable, String alias) {
        return join(JoinType.INNER, tempTable.as(alias));
    }

    public <R4> InJoinExpectingOn<ExpectingJoin4<RT1,RT2,RT3,R4>, Tuple4<RT1,RT2,RT3,R4>> join(Select<R4> select, String alias) {
        return join(JoinType.INNER, new SubselectAlias<>(select, alias));
    }

    public <R4> InJoinExpectingOn<ExpectingJoin4<RT1,RT2,RT3,R4>, Tuple4<RT1,RT2,RT3,R4>> leftJoin(Alias<R4> alias) {
        return join(JoinType.LEFT_OUTER, alias);
    }

    public <R4> InJoinExpectingOn<ExpectingJoin4<RT1,RT2,RT3,R4>, Tuple4<RT1,RT2,RT3,R4>> leftJoin(Class<R4> rowClass, String alias) {
        return join(JoinType.LEFT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R4> InJoinExpectingOn<ExpectingJoin4<RT1,RT2,RT3,R4>, Tuple4<RT1,RT2,RT3,R4>> leftJoin(TempTable<R4> tempTable, String alias) {
        return join(JoinType.LEFT_OUTER, tempTable.as(alias));
    }

    public <R4> InJoinExpectingOn<ExpectingJoin4<RT1,RT2,RT3,R4>, Tuple4<RT1,RT2,RT3,R4>> leftJoin(Select<R4> select, String alias) {
        return join(JoinType.LEFT_OUTER, new SubselectAlias<>(select, alias));
    }

    public <R4> InJoinExpectingOn<ExpectingJoin4<RT1,RT2,RT3,R4>, Tuple4<RT1,RT2,RT3,R4>> rightJoin(Alias<R4> alias) {
        return join(JoinType.RIGHT_OUTER, alias);
    }

    public <R4> InJoinExpectingOn<ExpectingJoin4<RT1,RT2,RT3,R4>, Tuple4<RT1,RT2,RT3,R4>> rightJoin(Class<R4> rowClass, String alias) {
        return join(JoinType.RIGHT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R4> InJoinExpectingOn<ExpectingJoin4<RT1,RT2,RT3,R4>, Tuple4<RT1,RT2,RT3,R4>> rightJoin(TempTable<R4> tempTable, String alias) {
        return join(JoinType.RIGHT_OUTER, tempTable.as(alias));
    }

    public <R4> InJoinExpectingOn<ExpectingJoin4<RT1,RT2,RT3,R4>, Tuple4<RT1,RT2,RT3,R4>> rightJoin(Select<R4> select, String alias) {
        return join(JoinType.RIGHT_OUTER, new SubselectAlias<>(select, alias));
    }

    public <R4> InJoinExpectingOn<ExpectingJoin4<RT1,RT2,RT3,R4>, Tuple4<RT1,RT2,RT3,R4>> fullOuterJoin(Alias<R4> alias) {
        return join(JoinType.FULL_OUTER, alias);
    }

    public <R4> InJoinExpectingOn<ExpectingJoin4<RT1,RT2,RT3,R4>, Tuple4<RT1,RT2,RT3,R4>> fullOuterJoin(Class<R4> rowClass, String alias) {
        return join(JoinType.FULL_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R4> InJoinExpectingOn<ExpectingJoin4<RT1,RT2,RT3,R4>, Tuple4<RT1,RT2,RT3,R4>> fullOuterJoin(TempTable<R4> tempTable, String alias) {
        return join(JoinType.FULL_OUTER, tempTable.as(alias));
    }

    public <R4> InJoinExpectingOn<ExpectingJoin4<RT1,RT2,RT3,R4>, Tuple4<RT1,RT2,RT3,R4>> fullOuterJoin(Select<R4> select, String alias) {
        return join(JoinType.FULL_OUTER, new SubselectAlias<>(select, alias));
    }

    private <R4> InJoinExpectingOn<ExpectingJoin4<RT1,RT2,RT3,R4>, Tuple4<RT1,RT2,RT3,R4>> join(JoinType joinType, Alias<R4> alias) {
        SelectStatement<Tuple4<RT1,RT2,RT3,R4>> select4 = new SelectStatement<>(
            scope().plus(alias),
            new TypeToken<Tuple4<RT1,RT2,RT3,R4>>() {}
                .where(new TypeParameter<RT1>() {}, Tuple3.type1(type()))
                .where(new TypeParameter<RT2>() {}, Tuple3.type2(type()))
                .where(new TypeParameter<RT3>() {}, Tuple3.type3(type()))
                .where(new TypeParameter<R4>() {}, alias.type()),
            statement.from().join(joinType, alias),
            Projections.of4(statement.projection(), Projection.of(alias)));
        return new InJoinExpectingOn<>(select4, ExpectingJoin4::new);
    }
}
