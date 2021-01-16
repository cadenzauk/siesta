/*
 * Copyright (c) 2017, ${year} Cadenza United Kingdom Limited
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

import com.cadenzauk.core.tuple.Tuple${n};
<#if n < max>
import com.cadenzauk.core.tuple.Tuple${n+1};
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.JoinType;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.Projections;
import com.cadenzauk.siesta.grammar.temp.TempTable;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
</#if>

<#assign typelist_space><#if n = 1>RT<#else>RT1<#list 2..n as i>, RT${i}</#list></#if></#assign>
<#assign typelist><#if n = 1>RT<#else>RT1<#list 2..n as i>,RT${i}</#list></#if></#assign>
<#assign nth><#if n = 1>nd<#elseif n = 2>rd<#else>th</#if></#assign>
public class ExpectingJoin${n}<${typelist_space}> extends InJoinExpectingAnd<ExpectingJoin${n}<${typelist}>,Tuple${n}<${typelist}>> {
    public ExpectingJoin${n}(SelectStatement<Tuple${n}<${typelist}>> statement) {
        super(statement);
    }

<#if n < max>
    public <R${n+1}> InJoinExpectingOn<ExpectingJoin${n+1}<${typelist},R${n+1}>, Tuple${n+1}<${typelist},R${n+1}>> join(Alias<R${n+1}> alias) {
        return join(JoinType.INNER, alias);
    }

    public <R${n+1}> InJoinExpectingOn<ExpectingJoin${n+1}<${typelist},R${n+1}>, Tuple${n+1}<${typelist},R${n+1}>> join(Class<R${n+1}> rowClass, String alias) {
        return join(JoinType.INNER, scope().database().table(rowClass).as(alias));
    }

    public <R${n+1}> InJoinExpectingOn<ExpectingJoin${n+1}<${typelist},R${n+1}>, Tuple${n+1}<${typelist},R${n+1}>> join(TempTable<R${n+1}> tempTable, String alias) {
        return join(JoinType.INNER, tempTable.as(alias));
    }

    public <R${n+1}> InJoinExpectingOn<ExpectingJoin${n+1}<${typelist},R${n+1}>, Tuple${n+1}<${typelist},R${n+1}>> join(Select<R${n+1}> select, String alias) {
        return join(JoinType.INNER, new SubselectAlias<>(select, alias));
    }

    public <R${n+1}> InJoinExpectingOn<ExpectingJoin${n+1}<${typelist},R${n+1}>, Tuple${n+1}<${typelist},R${n+1}>> leftJoin(Alias<R${n+1}> alias) {
        return join(JoinType.LEFT_OUTER, alias);
    }

    public <R${n+1}> InJoinExpectingOn<ExpectingJoin${n+1}<${typelist},R${n+1}>, Tuple${n+1}<${typelist},R${n+1}>> leftJoin(Class<R${n+1}> rowClass, String alias) {
        return join(JoinType.LEFT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R${n+1}> InJoinExpectingOn<ExpectingJoin${n+1}<${typelist},R${n+1}>, Tuple${n+1}<${typelist},R${n+1}>> leftJoin(TempTable<R${n+1}> tempTable, String alias) {
        return join(JoinType.LEFT_OUTER, tempTable.as(alias));
    }

    public <R${n+1}> InJoinExpectingOn<ExpectingJoin${n+1}<${typelist},R${n+1}>, Tuple${n+1}<${typelist},R${n+1}>> leftJoin(Select<R${n+1}> select, String alias) {
        return join(JoinType.LEFT_OUTER, new SubselectAlias<>(select, alias));
    }

    public <R${n+1}> InJoinExpectingOn<ExpectingJoin${n+1}<${typelist},R${n+1}>, Tuple${n+1}<${typelist},R${n+1}>> rightJoin(Alias<R${n+1}> alias) {
        return join(JoinType.RIGHT_OUTER, alias);
    }

    public <R${n+1}> InJoinExpectingOn<ExpectingJoin${n+1}<${typelist},R${n+1}>, Tuple${n+1}<${typelist},R${n+1}>> rightJoin(Class<R${n+1}> rowClass, String alias) {
        return join(JoinType.RIGHT_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R${n+1}> InJoinExpectingOn<ExpectingJoin${n+1}<${typelist},R${n+1}>, Tuple${n+1}<${typelist},R${n+1}>> rightJoin(TempTable<R${n+1}> tempTable, String alias) {
        return join(JoinType.RIGHT_OUTER, tempTable.as(alias));
    }

    public <R${n+1}> InJoinExpectingOn<ExpectingJoin${n+1}<${typelist},R${n+1}>, Tuple${n+1}<${typelist},R${n+1}>> rightJoin(Select<R${n+1}> select, String alias) {
        return join(JoinType.RIGHT_OUTER, new SubselectAlias<>(select, alias));
    }

    public <R${n+1}> InJoinExpectingOn<ExpectingJoin${n+1}<${typelist},R${n+1}>, Tuple${n+1}<${typelist},R${n+1}>> fullOuterJoin(Alias<R${n+1}> alias) {
        return join(JoinType.FULL_OUTER, alias);
    }

    public <R${n+1}> InJoinExpectingOn<ExpectingJoin${n+1}<${typelist},R${n+1}>, Tuple${n+1}<${typelist},R${n+1}>> fullOuterJoin(Class<R${n+1}> rowClass, String alias) {
        return join(JoinType.FULL_OUTER, scope().database().table(rowClass).as(alias));
    }

    public <R${n+1}> InJoinExpectingOn<ExpectingJoin${n+1}<${typelist},R${n+1}>, Tuple${n+1}<${typelist},R${n+1}>> fullOuterJoin(TempTable<R${n+1}> tempTable, String alias) {
        return join(JoinType.FULL_OUTER, tempTable.as(alias));
    }

    public <R${n+1}> InJoinExpectingOn<ExpectingJoin${n+1}<${typelist},R${n+1}>, Tuple${n+1}<${typelist},R${n+1}>> fullOuterJoin(Select<R${n+1}> select, String alias) {
        return join(JoinType.FULL_OUTER, new SubselectAlias<>(select, alias));
    }

    private <R${n+1}> InJoinExpectingOn<ExpectingJoin${n+1}<${typelist},R${n+1}>, Tuple${n+1}<${typelist},R${n+1}>> join(JoinType joinType, Alias<R${n+1}> alias) {
        SelectStatement<Tuple${n+1}<${typelist},R${n+1}>> select${n+1} = new SelectStatement<>(
            scope().plus(alias),
            new TypeToken<Tuple${n+1}<${typelist},R${n+1}>>() {}
<#list 1..n as i>
                .where(new TypeParameter<RT${i}>() {}, Tuple${n}.type${i}(type()))
</#list>
                .where(new TypeParameter<R${n+1}>() {}, alias.type()),
            statement.from().join(joinType, alias),
            Projections.of${n+1}(statement.projection(), Projection.of(alias)));
        return new InJoinExpectingOn<>(select${n+1}, ExpectingJoin${n+1}::new);
    }
</#if>
}
