/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited
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

package com.cadenzauk.siesta;

import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.core.sql.RowMapperFactory;
<#list 2..n as i>
import com.cadenzauk.core.tuple.Tuple${i};
</#list>

import java.util.Optional;

public class RowMapperFactories {
    public static <T1, T2> RowMapperFactory<Tuple2<T1,T2>> of(RowMapperFactory<T1> factory1, RowMapperFactory<T2> factory2) {
        return (prefix, label) ->RowMappers.of(factory1.rowMapper(prefix, Optional.empty()), factory2.rowMapper(prefix, Optional.empty()));
    }
   <#list 3..n as i>

    public static <T1<#list 2..i as j>, T${j}</#list>> RowMapperFactory<Tuple${i}<T1<#list 2..i as j>,T${j}</#list>>> of(
        <#list 1..i as j>
           RowMapperFactory<T${j}> factory${j}<#if j < i>,</#if>
        </#list>
    ) {
        return (prefix, label) ->RowMappers.of(
        <#list 1..i as j>
                factory${j}.rowMapper(prefix, Optional.empty())<#if j < i>,</#if>
        </#list>
            );
    }
    </#list>
    <#list 3..n as i>

    public static <T1<#list 2..i as j>, T${j}</#list>> RowMapperFactory<Tuple${i}<T1<#list 2..i as j>,T${j}</#list>>> add${i}<#if i = 3>rd<#else>th</#if>(RowMapperFactory<Tuple${i-1}<T1<#list 2..i-1 as j>,T${j}</#list>>> factory${i-1}, RowMapperFactory<T${i}> factory) {
        return (prefix, label) -> {
            RowMapper<Tuple${i-1}<T1<#list 2..i-1 as j>,T${j}</#list>>> tuple = factory${i-1}.rowMapper(prefix, Optional.empty());
            return RowMappers.add${i}<#if i = 3>rd<#else>th</#if>(tuple, factory.rowMapper(prefix, Optional.empty()));
        };
    }
    </#list>
}
