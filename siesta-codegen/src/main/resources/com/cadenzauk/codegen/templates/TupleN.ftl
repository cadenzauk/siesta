/*
 * Copyright (c) ${year} Cadenza United Kingdom Limited
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

package com.cadenzauk.core.tuple;

<#if n &gt; 2>
import com.cadenzauk.core.function.Function${n};
</#if>
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

<#if n = 2>
import java.util.function.BiFunction;
</#if>
import java.util.function.Function;

public class Tuple${n}<<#list 1..n-1 as i>T${i}, </#list>T${n}> implements Tuple {
<#list 1..n as i>
    private final T${i} item${i};
</#list>

    public Tuple${n}(<#list 1..n-1 as i>T${i} item${i}, </#list>T${n} item${n}) {
<#list 1..n as i>
        this.item${i} = item${i};
</#list>
    }

    @Override
    public String toString() {
<#if n &gt; 3>
        return "(" + item1 +
<#list 2..n as i>
            ", " + item${i} +
</#list>
            ')';
<#else>
        return "(" + item1 + <#list 2..n as i>", " + item${i} + </#list>')';
</#if>
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Tuple${n}<?<#list 2..n as i>,?</#list>> tuple${n} = (Tuple${n}<?<#list 2..n as i>,?</#list>>) o;

        return new EqualsBuilder()
<#list 1..n as i>
            .append(item${i}, tuple${n}.item${i})
</#list>
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
<#list 1..n as i>
            .append(item${i})
</#list>
            .toHashCode();
    }
<#list 1..n as i>

    public T${i} item${i}() {
        return item${i};
    }
</#list>

    public <T> T map(<#if n = 2>BiFunction<T1, T2, T><#else>Function${n}<<#list 1..n as i>T${i},</#list>T></#if> function) {
        return function.apply(item1<#list 2..n as i>, item${i}</#list>);
    }
<#list 1..n as i>

    public <T> Tuple${n}<<#list 1..n as j><#if j=i>T<#else>T${j}</#if><#if j<n>,</#if></#list>> map${i}(Function<T${i},T> function) {
        return Tuple.of(
    <#list 1..n as j>
            <#if j=i>function.apply(item${i})<#else>item${j}</#if><#if j<n>,</#if>
    </#list>
        );
    }
</#list>
}
