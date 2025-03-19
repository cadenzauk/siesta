/*
 * Copyright (c) 2020, ${year} Cadenza United Kingdom Limited
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

package com.cadenzauk.siesta.grammar.expression;

<#if n != max>
import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
</#if>
import com.cadenzauk.core.sql.RowMapperFactory;
<#if n != 1>
import com.cadenzauk.core.tuple.Tuple${n};
import com.cadenzauk.siesta.RowMapperFactories;
</#if>
<#if n != max>
import com.cadenzauk.siesta.Alias;
</#if>
import com.cadenzauk.siesta.Scope;
<#if n != 1>
import com.google.common.reflect.TypeParameter;
</#if>
import com.google.common.reflect.TypeToken;

import java.util.stream.Stream;
<#if n != 1>

import static com.cadenzauk.core.reflect.util.TypeUtil.boxedType;
</#if>

<#assign typelist_space><#if n = 1>T1<#else>T1<#list 2..n as i>, T${i}</#list></#if></#assign>
<#assign typelist><#if n = 1>T1<#else>T1<#list 2..n as i>,T${i}</#list></#if></#assign>
<#assign type><#if n = 1>T1<#else>Tuple${n}<${typelist}></#if></#assign>
public class TupleBuilder${n}<${typelist_space}> extends TupleBuilder implements TypedExpression<${type}> {
<#list 1..n as i>
    private final TypedExpression<T${i}> item${i};
</#list>
<#if n != 1>
    private final TypeToken<${type}> type;
</#if>

    public TupleBuilder${n}(<#list 1..n as i>TypedExpression<T${i}> item${i}<#if i < n>,</#if></#list>) {
<#list 1..n as i>
        this.item${i} = item${i};
</#list>
<#if n gt 1>
        type = new TypeToken<${type}>() {}
    <#list 1..n as i>
            .where(new TypeParameter<T${i}>() {}, boxedType(item${i}.type()))<#if i = n>;</#if>
    </#list>
</#if>
    }

    @Override
    public RowMapperFactory<${type}> rowMapperFactory(Scope scope) {
<#if n = 1>
        return item1.rowMapperFactory(scope);
<#else>
        return RowMapperFactories.of(
<#list 1..n as i>
            item${i}.rowMapperFactory(scope)<#if i < n>,</#if>
</#list>
        );
</#if>
    }

    @Override
    public TypeToken<${type}> type() {
<#if n = 1>
        return item1.type();
<#else>
        return type;
</#if>
    }
<#if n < max>

    public <R, T${n+1}> TupleBuilder${n+1}<${typelist},T${n+1}> comma(Function1<R,T${n+1}> methodRef) {
        return new TupleBuilder${n+1}<>(<#list 1..n as i>item${i}, </#list>UnresolvedColumn.of(methodRef));
    }

    public <R, T${n+1}> TupleBuilder${n+1}<${typelist},T${n+1}> comma(FunctionOptional1<R,T${n+1}> methodRef) {
        return new TupleBuilder${n+1}<>(<#list 1..n as i>item${i}, </#list>UnresolvedColumn.of(methodRef));
    }

    public <R, T${n+1}> TupleBuilder${n+1}<${typelist},T${n+1}> comma(String alias, Function1<R,T${n+1}> methodRef) {
        return new TupleBuilder${n+1}<>(<#list 1..n as i>item${i}, </#list>UnresolvedColumn.of(alias, methodRef));
    }

    public <R, T${n+1}> TupleBuilder${n+1}<${typelist},T${n+1}> comma(String alias, FunctionOptional1<R,T${n+1}> methodRef) {
        return new TupleBuilder${n+1}<>(<#list 1..n as i>item${i}, </#list>UnresolvedColumn.of(alias, methodRef));
    }

    public <R, T${n+1}> TupleBuilder${n+1}<${typelist},T${n+1}> comma(Alias<R> alias, Function1<R,T${n+1}> methodRef) {
        return new TupleBuilder${n+1}<>(<#list 1..n as i>item${i}, </#list>ResolvedColumn.of(alias, methodRef));
    }

    public <R, T${n+1}> TupleBuilder${n+1}<${typelist},T${n+1}> comma(Alias<R> alias, FunctionOptional1<R,T${n+1}> methodRef) {
        return new TupleBuilder${n+1}<>(<#list 1..n as i>item${i}, </#list>ResolvedColumn.of(alias, methodRef));
    }

    public <T${n+1}> TupleBuilder${n+1}<${typelist},T${n+1}> comma(T${n+1} value) {
        return new TupleBuilder${n+1}<>(<#list 1..n as i>item${i}, </#list>ValueExpression.of(value));
    }

    public <T${n+1}> TupleBuilder${n+1}<${typelist},T${n+1}> comma(TypedExpression<T${n+1}> item${n+1}) {
        return new TupleBuilder${n+1}<>(<#list 1..n as i>item${i}, </#list>item${n+1});
    }
</#if>

    @Override
    protected Stream<TypedExpression<?>> items() {
        return Stream.of(<#list 1..n as i>item${i}<#if i < n>, </#if></#list>);
    }
}
