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
package com.cadenzauk.siesta;

import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.siesta.projection.ProjectionList;
<#list 2..n as i>
import com.cadenzauk.core.tuple.Tuple${i};
</#list>
import com.google.common.collect.ImmutableList;

import java.util.function.Function;

public interface Projections {
    static <R1, R2> Projection<Tuple2<R1,R2>> of2(Projection<R1> lhs, Projection<R2> rhs) {
        Function<Scope,RowMapperFactory<Tuple2<R1,R2>>> rowMapperFactory = scope -> RowMapperFactories.of(lhs.rowMapperFactory(scope), rhs.rowMapperFactory(scope));
        return new ProjectionList<>(false, ImmutableList.of(lhs, rhs), rowMapperFactory);
    }
<#list 3..n as i>

    static <R1<#list 2..i as j>, R${j}</#list>> Projection<Tuple${i}<R1<#list 2..i as j>,R${j}</#list>>> of${i}(Projection<Tuple${i-1}<R1<#list 2..i-1 as j>,R${j}</#list>>> lhs, Projection<R${i}> rhs) {
        Function<Scope,RowMapperFactory<Tuple${i}<R1<#list 2..i as j>,R${j}</#list>>>> rowMapperFactory = scope -> RowMapperFactories.add${i}<#if i = 3>rd<#else>th</#if>(lhs.rowMapperFactory(scope), rhs.rowMapperFactory(scope));
        return new ProjectionList<>(
            false,
            ImmutableList.<Projection<?>>builder()
                .addAll(lhs.components())
                .add(rhs)
                .build(),
            rowMapperFactory);
    }
</#list>
}
