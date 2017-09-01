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
import com.cadenzauk.core.tuple.Tuple;
<#list 2..n as i>
import com.cadenzauk.core.tuple.Tuple${i};
</#list>

public class RowMappers {
    public static <T1, T2> RowMapper<Tuple2<T1,T2>> of(RowMapper<T1> mapper1, RowMapper<T2> mapper2) {
        return rs -> Tuple.of(mapper1.mapRow(rs), mapper2.mapRow(rs));
    }
   <#list 3..n as i>

    public static <T1<#list 2..i as j>, T${j}</#list>> RowMapper<Tuple${i}<T1<#list 2..i as j>,T${j}</#list>>> add${i}<#if i = 3>rd<#else>th</#if>(RowMapper<Tuple${i-1}<T1<#list 2..i-1 as j>,T${j}</#list>>> mapper${i-1}, RowMapper<T${i}> mapper) {
        return rs -> {
            Tuple${i-1}<T1<#list 2..i-1 as j>,T${j}</#list>> tuple = mapper${i-1}.mapRow(rs);
            return Tuple.of(tuple.item1()<#list 2..i-1 as j>, tuple.item${j}()</#list>, mapper.mapRow(rs));
        };
    }
    </#list>
}
