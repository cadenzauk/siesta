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

package com.cadenzauk.siesta.kotlin

<#list 2..n as i>
import com.cadenzauk.core.tuple.Tuple${i}
</#list>
<#list 1..n as i>
import com.cadenzauk.siesta.grammar.select.ExpectingJoin${i}
</#list>
import com.cadenzauk.siesta.grammar.select.InJoinExpectingOn
import kotlin.reflect.KClass
<#list 2..n as i>

<#assign typelist_space><#if i = 2>RT<#else>RT1<#list 2..(i-1) as j>, RT${j}</#list></#if></#assign>
fun <${typelist_space}, R${i} : Any> ExpectingJoin${i-1}<${typelist_space}>.join(rowClass: KClass<R${i}>, alias: String): InJoinExpectingOn<ExpectingJoin${i}<${typelist_space}, R${i}>, Tuple${i}<${typelist_space}, R${i}>> =
    join(rowClass.java, alias)

fun <${typelist_space}, R${i} : Any> ExpectingJoin${i-1}<${typelist_space}>.leftJoin(rowClass: KClass<R${i}>, alias: String): InJoinExpectingOn<ExpectingJoin${i}<${typelist_space}, R${i}>, Tuple${i}<${typelist_space}, R${i}>> =
    leftJoin(rowClass.java, alias)

fun <${typelist_space}, R${i} : Any> ExpectingJoin${i-1}<${typelist_space}>.rightJoin(rowClass: KClass<R${i}>, alias: String): InJoinExpectingOn<ExpectingJoin${i}<${typelist_space}, R${i}>, Tuple${i}<${typelist_space}, R${i}>> =
    rightJoin(rowClass.java, alias)

fun <${typelist_space}, R${i} : Any> ExpectingJoin${i-1}<${typelist_space}>.fullOuterJoin(rowClass: KClass<R${i}>, alias: String): InJoinExpectingOn<ExpectingJoin${i}<${typelist_space}, R${i}>, Tuple${i}<${typelist_space}, R${i}>> =
    fullOuterJoin(rowClass.java, alias)
</#list>
