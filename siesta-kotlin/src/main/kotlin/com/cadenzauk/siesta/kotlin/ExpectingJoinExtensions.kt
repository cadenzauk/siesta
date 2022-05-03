/*
* Copyright (c) 2017, 2022 Cadenza United Kingdom Limited
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

import com.cadenzauk.core.tuple.Tuple2
import com.cadenzauk.core.tuple.Tuple3
import com.cadenzauk.core.tuple.Tuple4
import com.cadenzauk.core.tuple.Tuple5
import com.cadenzauk.core.tuple.Tuple6
import com.cadenzauk.core.tuple.Tuple7
import com.cadenzauk.core.tuple.Tuple8
import com.cadenzauk.core.tuple.Tuple9
import com.cadenzauk.core.tuple.Tuple10
import com.cadenzauk.core.tuple.Tuple11
import com.cadenzauk.core.tuple.Tuple12
import com.cadenzauk.core.tuple.Tuple13
import com.cadenzauk.core.tuple.Tuple14
import com.cadenzauk.core.tuple.Tuple15
import com.cadenzauk.core.tuple.Tuple16
import com.cadenzauk.core.tuple.Tuple17
import com.cadenzauk.core.tuple.Tuple18
import com.cadenzauk.core.tuple.Tuple19
import com.cadenzauk.core.tuple.Tuple20
import com.cadenzauk.siesta.grammar.select.ExpectingJoin1
import com.cadenzauk.siesta.grammar.select.ExpectingJoin2
import com.cadenzauk.siesta.grammar.select.ExpectingJoin3
import com.cadenzauk.siesta.grammar.select.ExpectingJoin4
import com.cadenzauk.siesta.grammar.select.ExpectingJoin5
import com.cadenzauk.siesta.grammar.select.ExpectingJoin6
import com.cadenzauk.siesta.grammar.select.ExpectingJoin7
import com.cadenzauk.siesta.grammar.select.ExpectingJoin8
import com.cadenzauk.siesta.grammar.select.ExpectingJoin9
import com.cadenzauk.siesta.grammar.select.ExpectingJoin10
import com.cadenzauk.siesta.grammar.select.ExpectingJoin11
import com.cadenzauk.siesta.grammar.select.ExpectingJoin12
import com.cadenzauk.siesta.grammar.select.ExpectingJoin13
import com.cadenzauk.siesta.grammar.select.ExpectingJoin14
import com.cadenzauk.siesta.grammar.select.ExpectingJoin15
import com.cadenzauk.siesta.grammar.select.ExpectingJoin16
import com.cadenzauk.siesta.grammar.select.ExpectingJoin17
import com.cadenzauk.siesta.grammar.select.ExpectingJoin18
import com.cadenzauk.siesta.grammar.select.ExpectingJoin19
import com.cadenzauk.siesta.grammar.select.ExpectingJoin20
import com.cadenzauk.siesta.grammar.select.InJoinExpectingOn
import kotlin.reflect.KClass

fun <RT, R2 : Any> ExpectingJoin1<RT>.join(rowClass: KClass<R2>, alias: String): InJoinExpectingOn<ExpectingJoin2<RT, R2>, Tuple2<RT, R2>> =
    join(rowClass.java, alias)

fun <RT, R2 : Any> ExpectingJoin1<RT>.leftJoin(rowClass: KClass<R2>, alias: String): InJoinExpectingOn<ExpectingJoin2<RT, R2>, Tuple2<RT, R2>> =
    leftJoin(rowClass.java, alias)

fun <RT, R2 : Any> ExpectingJoin1<RT>.rightJoin(rowClass: KClass<R2>, alias: String): InJoinExpectingOn<ExpectingJoin2<RT, R2>, Tuple2<RT, R2>> =
    rightJoin(rowClass.java, alias)

fun <RT, R2 : Any> ExpectingJoin1<RT>.fullOuterJoin(rowClass: KClass<R2>, alias: String): InJoinExpectingOn<ExpectingJoin2<RT, R2>, Tuple2<RT, R2>> =
    fullOuterJoin(rowClass.java, alias)

fun <RT1, RT2, R3 : Any> ExpectingJoin2<RT1, RT2>.join(rowClass: KClass<R3>, alias: String): InJoinExpectingOn<ExpectingJoin3<RT1, RT2, R3>, Tuple3<RT1, RT2, R3>> =
    join(rowClass.java, alias)

fun <RT1, RT2, R3 : Any> ExpectingJoin2<RT1, RT2>.leftJoin(rowClass: KClass<R3>, alias: String): InJoinExpectingOn<ExpectingJoin3<RT1, RT2, R3>, Tuple3<RT1, RT2, R3>> =
    leftJoin(rowClass.java, alias)

fun <RT1, RT2, R3 : Any> ExpectingJoin2<RT1, RT2>.rightJoin(rowClass: KClass<R3>, alias: String): InJoinExpectingOn<ExpectingJoin3<RT1, RT2, R3>, Tuple3<RT1, RT2, R3>> =
    rightJoin(rowClass.java, alias)

fun <RT1, RT2, R3 : Any> ExpectingJoin2<RT1, RT2>.fullOuterJoin(rowClass: KClass<R3>, alias: String): InJoinExpectingOn<ExpectingJoin3<RT1, RT2, R3>, Tuple3<RT1, RT2, R3>> =
    fullOuterJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, R4 : Any> ExpectingJoin3<RT1, RT2, RT3>.join(rowClass: KClass<R4>, alias: String): InJoinExpectingOn<ExpectingJoin4<RT1, RT2, RT3, R4>, Tuple4<RT1, RT2, RT3, R4>> =
    join(rowClass.java, alias)

fun <RT1, RT2, RT3, R4 : Any> ExpectingJoin3<RT1, RT2, RT3>.leftJoin(rowClass: KClass<R4>, alias: String): InJoinExpectingOn<ExpectingJoin4<RT1, RT2, RT3, R4>, Tuple4<RT1, RT2, RT3, R4>> =
    leftJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, R4 : Any> ExpectingJoin3<RT1, RT2, RT3>.rightJoin(rowClass: KClass<R4>, alias: String): InJoinExpectingOn<ExpectingJoin4<RT1, RT2, RT3, R4>, Tuple4<RT1, RT2, RT3, R4>> =
    rightJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, R4 : Any> ExpectingJoin3<RT1, RT2, RT3>.fullOuterJoin(rowClass: KClass<R4>, alias: String): InJoinExpectingOn<ExpectingJoin4<RT1, RT2, RT3, R4>, Tuple4<RT1, RT2, RT3, R4>> =
    fullOuterJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, R5 : Any> ExpectingJoin4<RT1, RT2, RT3, RT4>.join(rowClass: KClass<R5>, alias: String): InJoinExpectingOn<ExpectingJoin5<RT1, RT2, RT3, RT4, R5>, Tuple5<RT1, RT2, RT3, RT4, R5>> =
    join(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, R5 : Any> ExpectingJoin4<RT1, RT2, RT3, RT4>.leftJoin(rowClass: KClass<R5>, alias: String): InJoinExpectingOn<ExpectingJoin5<RT1, RT2, RT3, RT4, R5>, Tuple5<RT1, RT2, RT3, RT4, R5>> =
    leftJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, R5 : Any> ExpectingJoin4<RT1, RT2, RT3, RT4>.rightJoin(rowClass: KClass<R5>, alias: String): InJoinExpectingOn<ExpectingJoin5<RT1, RT2, RT3, RT4, R5>, Tuple5<RT1, RT2, RT3, RT4, R5>> =
    rightJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, R5 : Any> ExpectingJoin4<RT1, RT2, RT3, RT4>.fullOuterJoin(rowClass: KClass<R5>, alias: String): InJoinExpectingOn<ExpectingJoin5<RT1, RT2, RT3, RT4, R5>, Tuple5<RT1, RT2, RT3, RT4, R5>> =
    fullOuterJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, R6 : Any> ExpectingJoin5<RT1, RT2, RT3, RT4, RT5>.join(rowClass: KClass<R6>, alias: String): InJoinExpectingOn<ExpectingJoin6<RT1, RT2, RT3, RT4, RT5, R6>, Tuple6<RT1, RT2, RT3, RT4, RT5, R6>> =
    join(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, R6 : Any> ExpectingJoin5<RT1, RT2, RT3, RT4, RT5>.leftJoin(rowClass: KClass<R6>, alias: String): InJoinExpectingOn<ExpectingJoin6<RT1, RT2, RT3, RT4, RT5, R6>, Tuple6<RT1, RT2, RT3, RT4, RT5, R6>> =
    leftJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, R6 : Any> ExpectingJoin5<RT1, RT2, RT3, RT4, RT5>.rightJoin(rowClass: KClass<R6>, alias: String): InJoinExpectingOn<ExpectingJoin6<RT1, RT2, RT3, RT4, RT5, R6>, Tuple6<RT1, RT2, RT3, RT4, RT5, R6>> =
    rightJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, R6 : Any> ExpectingJoin5<RT1, RT2, RT3, RT4, RT5>.fullOuterJoin(rowClass: KClass<R6>, alias: String): InJoinExpectingOn<ExpectingJoin6<RT1, RT2, RT3, RT4, RT5, R6>, Tuple6<RT1, RT2, RT3, RT4, RT5, R6>> =
    fullOuterJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, R7 : Any> ExpectingJoin6<RT1, RT2, RT3, RT4, RT5, RT6>.join(rowClass: KClass<R7>, alias: String): InJoinExpectingOn<ExpectingJoin7<RT1, RT2, RT3, RT4, RT5, RT6, R7>, Tuple7<RT1, RT2, RT3, RT4, RT5, RT6, R7>> =
    join(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, R7 : Any> ExpectingJoin6<RT1, RT2, RT3, RT4, RT5, RT6>.leftJoin(rowClass: KClass<R7>, alias: String): InJoinExpectingOn<ExpectingJoin7<RT1, RT2, RT3, RT4, RT5, RT6, R7>, Tuple7<RT1, RT2, RT3, RT4, RT5, RT6, R7>> =
    leftJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, R7 : Any> ExpectingJoin6<RT1, RT2, RT3, RT4, RT5, RT6>.rightJoin(rowClass: KClass<R7>, alias: String): InJoinExpectingOn<ExpectingJoin7<RT1, RT2, RT3, RT4, RT5, RT6, R7>, Tuple7<RT1, RT2, RT3, RT4, RT5, RT6, R7>> =
    rightJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, R7 : Any> ExpectingJoin6<RT1, RT2, RT3, RT4, RT5, RT6>.fullOuterJoin(rowClass: KClass<R7>, alias: String): InJoinExpectingOn<ExpectingJoin7<RT1, RT2, RT3, RT4, RT5, RT6, R7>, Tuple7<RT1, RT2, RT3, RT4, RT5, RT6, R7>> =
    fullOuterJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, R8 : Any> ExpectingJoin7<RT1, RT2, RT3, RT4, RT5, RT6, RT7>.join(rowClass: KClass<R8>, alias: String): InJoinExpectingOn<ExpectingJoin8<RT1, RT2, RT3, RT4, RT5, RT6, RT7, R8>, Tuple8<RT1, RT2, RT3, RT4, RT5, RT6, RT7, R8>> =
    join(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, R8 : Any> ExpectingJoin7<RT1, RT2, RT3, RT4, RT5, RT6, RT7>.leftJoin(rowClass: KClass<R8>, alias: String): InJoinExpectingOn<ExpectingJoin8<RT1, RT2, RT3, RT4, RT5, RT6, RT7, R8>, Tuple8<RT1, RT2, RT3, RT4, RT5, RT6, RT7, R8>> =
    leftJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, R8 : Any> ExpectingJoin7<RT1, RT2, RT3, RT4, RT5, RT6, RT7>.rightJoin(rowClass: KClass<R8>, alias: String): InJoinExpectingOn<ExpectingJoin8<RT1, RT2, RT3, RT4, RT5, RT6, RT7, R8>, Tuple8<RT1, RT2, RT3, RT4, RT5, RT6, RT7, R8>> =
    rightJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, R8 : Any> ExpectingJoin7<RT1, RT2, RT3, RT4, RT5, RT6, RT7>.fullOuterJoin(rowClass: KClass<R8>, alias: String): InJoinExpectingOn<ExpectingJoin8<RT1, RT2, RT3, RT4, RT5, RT6, RT7, R8>, Tuple8<RT1, RT2, RT3, RT4, RT5, RT6, RT7, R8>> =
    fullOuterJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, R9 : Any> ExpectingJoin8<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8>.join(rowClass: KClass<R9>, alias: String): InJoinExpectingOn<ExpectingJoin9<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, R9>, Tuple9<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, R9>> =
    join(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, R9 : Any> ExpectingJoin8<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8>.leftJoin(rowClass: KClass<R9>, alias: String): InJoinExpectingOn<ExpectingJoin9<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, R9>, Tuple9<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, R9>> =
    leftJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, R9 : Any> ExpectingJoin8<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8>.rightJoin(rowClass: KClass<R9>, alias: String): InJoinExpectingOn<ExpectingJoin9<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, R9>, Tuple9<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, R9>> =
    rightJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, R9 : Any> ExpectingJoin8<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8>.fullOuterJoin(rowClass: KClass<R9>, alias: String): InJoinExpectingOn<ExpectingJoin9<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, R9>, Tuple9<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, R9>> =
    fullOuterJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, R10 : Any> ExpectingJoin9<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9>.join(rowClass: KClass<R10>, alias: String): InJoinExpectingOn<ExpectingJoin10<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, R10>, Tuple10<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, R10>> =
    join(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, R10 : Any> ExpectingJoin9<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9>.leftJoin(rowClass: KClass<R10>, alias: String): InJoinExpectingOn<ExpectingJoin10<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, R10>, Tuple10<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, R10>> =
    leftJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, R10 : Any> ExpectingJoin9<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9>.rightJoin(rowClass: KClass<R10>, alias: String): InJoinExpectingOn<ExpectingJoin10<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, R10>, Tuple10<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, R10>> =
    rightJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, R10 : Any> ExpectingJoin9<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9>.fullOuterJoin(rowClass: KClass<R10>, alias: String): InJoinExpectingOn<ExpectingJoin10<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, R10>, Tuple10<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, R10>> =
    fullOuterJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, R11 : Any> ExpectingJoin10<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10>.join(rowClass: KClass<R11>, alias: String): InJoinExpectingOn<ExpectingJoin11<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, R11>, Tuple11<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, R11>> =
    join(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, R11 : Any> ExpectingJoin10<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10>.leftJoin(rowClass: KClass<R11>, alias: String): InJoinExpectingOn<ExpectingJoin11<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, R11>, Tuple11<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, R11>> =
    leftJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, R11 : Any> ExpectingJoin10<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10>.rightJoin(rowClass: KClass<R11>, alias: String): InJoinExpectingOn<ExpectingJoin11<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, R11>, Tuple11<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, R11>> =
    rightJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, R11 : Any> ExpectingJoin10<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10>.fullOuterJoin(rowClass: KClass<R11>, alias: String): InJoinExpectingOn<ExpectingJoin11<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, R11>, Tuple11<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, R11>> =
    fullOuterJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, R12 : Any> ExpectingJoin11<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11>.join(rowClass: KClass<R12>, alias: String): InJoinExpectingOn<ExpectingJoin12<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, R12>, Tuple12<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, R12>> =
    join(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, R12 : Any> ExpectingJoin11<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11>.leftJoin(rowClass: KClass<R12>, alias: String): InJoinExpectingOn<ExpectingJoin12<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, R12>, Tuple12<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, R12>> =
    leftJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, R12 : Any> ExpectingJoin11<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11>.rightJoin(rowClass: KClass<R12>, alias: String): InJoinExpectingOn<ExpectingJoin12<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, R12>, Tuple12<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, R12>> =
    rightJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, R12 : Any> ExpectingJoin11<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11>.fullOuterJoin(rowClass: KClass<R12>, alias: String): InJoinExpectingOn<ExpectingJoin12<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, R12>, Tuple12<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, R12>> =
    fullOuterJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, R13 : Any> ExpectingJoin12<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12>.join(rowClass: KClass<R13>, alias: String): InJoinExpectingOn<ExpectingJoin13<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, R13>, Tuple13<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, R13>> =
    join(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, R13 : Any> ExpectingJoin12<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12>.leftJoin(rowClass: KClass<R13>, alias: String): InJoinExpectingOn<ExpectingJoin13<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, R13>, Tuple13<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, R13>> =
    leftJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, R13 : Any> ExpectingJoin12<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12>.rightJoin(rowClass: KClass<R13>, alias: String): InJoinExpectingOn<ExpectingJoin13<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, R13>, Tuple13<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, R13>> =
    rightJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, R13 : Any> ExpectingJoin12<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12>.fullOuterJoin(rowClass: KClass<R13>, alias: String): InJoinExpectingOn<ExpectingJoin13<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, R13>, Tuple13<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, R13>> =
    fullOuterJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, R14 : Any> ExpectingJoin13<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13>.join(rowClass: KClass<R14>, alias: String): InJoinExpectingOn<ExpectingJoin14<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, R14>, Tuple14<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, R14>> =
    join(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, R14 : Any> ExpectingJoin13<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13>.leftJoin(rowClass: KClass<R14>, alias: String): InJoinExpectingOn<ExpectingJoin14<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, R14>, Tuple14<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, R14>> =
    leftJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, R14 : Any> ExpectingJoin13<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13>.rightJoin(rowClass: KClass<R14>, alias: String): InJoinExpectingOn<ExpectingJoin14<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, R14>, Tuple14<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, R14>> =
    rightJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, R14 : Any> ExpectingJoin13<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13>.fullOuterJoin(rowClass: KClass<R14>, alias: String): InJoinExpectingOn<ExpectingJoin14<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, R14>, Tuple14<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, R14>> =
    fullOuterJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, R15 : Any> ExpectingJoin14<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14>.join(rowClass: KClass<R15>, alias: String): InJoinExpectingOn<ExpectingJoin15<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, R15>, Tuple15<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, R15>> =
    join(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, R15 : Any> ExpectingJoin14<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14>.leftJoin(rowClass: KClass<R15>, alias: String): InJoinExpectingOn<ExpectingJoin15<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, R15>, Tuple15<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, R15>> =
    leftJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, R15 : Any> ExpectingJoin14<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14>.rightJoin(rowClass: KClass<R15>, alias: String): InJoinExpectingOn<ExpectingJoin15<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, R15>, Tuple15<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, R15>> =
    rightJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, R15 : Any> ExpectingJoin14<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14>.fullOuterJoin(rowClass: KClass<R15>, alias: String): InJoinExpectingOn<ExpectingJoin15<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, R15>, Tuple15<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, R15>> =
    fullOuterJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, R16 : Any> ExpectingJoin15<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15>.join(rowClass: KClass<R16>, alias: String): InJoinExpectingOn<ExpectingJoin16<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, R16>, Tuple16<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, R16>> =
    join(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, R16 : Any> ExpectingJoin15<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15>.leftJoin(rowClass: KClass<R16>, alias: String): InJoinExpectingOn<ExpectingJoin16<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, R16>, Tuple16<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, R16>> =
    leftJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, R16 : Any> ExpectingJoin15<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15>.rightJoin(rowClass: KClass<R16>, alias: String): InJoinExpectingOn<ExpectingJoin16<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, R16>, Tuple16<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, R16>> =
    rightJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, R16 : Any> ExpectingJoin15<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15>.fullOuterJoin(rowClass: KClass<R16>, alias: String): InJoinExpectingOn<ExpectingJoin16<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, R16>, Tuple16<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, R16>> =
    fullOuterJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, R17 : Any> ExpectingJoin16<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16>.join(rowClass: KClass<R17>, alias: String): InJoinExpectingOn<ExpectingJoin17<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, R17>, Tuple17<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, R17>> =
    join(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, R17 : Any> ExpectingJoin16<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16>.leftJoin(rowClass: KClass<R17>, alias: String): InJoinExpectingOn<ExpectingJoin17<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, R17>, Tuple17<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, R17>> =
    leftJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, R17 : Any> ExpectingJoin16<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16>.rightJoin(rowClass: KClass<R17>, alias: String): InJoinExpectingOn<ExpectingJoin17<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, R17>, Tuple17<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, R17>> =
    rightJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, R17 : Any> ExpectingJoin16<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16>.fullOuterJoin(rowClass: KClass<R17>, alias: String): InJoinExpectingOn<ExpectingJoin17<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, R17>, Tuple17<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, R17>> =
    fullOuterJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, R18 : Any> ExpectingJoin17<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17>.join(rowClass: KClass<R18>, alias: String): InJoinExpectingOn<ExpectingJoin18<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, R18>, Tuple18<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, R18>> =
    join(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, R18 : Any> ExpectingJoin17<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17>.leftJoin(rowClass: KClass<R18>, alias: String): InJoinExpectingOn<ExpectingJoin18<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, R18>, Tuple18<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, R18>> =
    leftJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, R18 : Any> ExpectingJoin17<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17>.rightJoin(rowClass: KClass<R18>, alias: String): InJoinExpectingOn<ExpectingJoin18<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, R18>, Tuple18<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, R18>> =
    rightJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, R18 : Any> ExpectingJoin17<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17>.fullOuterJoin(rowClass: KClass<R18>, alias: String): InJoinExpectingOn<ExpectingJoin18<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, R18>, Tuple18<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, R18>> =
    fullOuterJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, R19 : Any> ExpectingJoin18<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18>.join(rowClass: KClass<R19>, alias: String): InJoinExpectingOn<ExpectingJoin19<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, R19>, Tuple19<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, R19>> =
    join(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, R19 : Any> ExpectingJoin18<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18>.leftJoin(rowClass: KClass<R19>, alias: String): InJoinExpectingOn<ExpectingJoin19<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, R19>, Tuple19<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, R19>> =
    leftJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, R19 : Any> ExpectingJoin18<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18>.rightJoin(rowClass: KClass<R19>, alias: String): InJoinExpectingOn<ExpectingJoin19<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, R19>, Tuple19<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, R19>> =
    rightJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, R19 : Any> ExpectingJoin18<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18>.fullOuterJoin(rowClass: KClass<R19>, alias: String): InJoinExpectingOn<ExpectingJoin19<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, R19>, Tuple19<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, R19>> =
    fullOuterJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, RT19, R20 : Any> ExpectingJoin19<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, RT19>.join(rowClass: KClass<R20>, alias: String): InJoinExpectingOn<ExpectingJoin20<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, RT19, R20>, Tuple20<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, RT19, R20>> =
    join(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, RT19, R20 : Any> ExpectingJoin19<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, RT19>.leftJoin(rowClass: KClass<R20>, alias: String): InJoinExpectingOn<ExpectingJoin20<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, RT19, R20>, Tuple20<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, RT19, R20>> =
    leftJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, RT19, R20 : Any> ExpectingJoin19<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, RT19>.rightJoin(rowClass: KClass<R20>, alias: String): InJoinExpectingOn<ExpectingJoin20<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, RT19, R20>, Tuple20<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, RT19, R20>> =
    rightJoin(rowClass.java, alias)

fun <RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, RT19, R20 : Any> ExpectingJoin19<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, RT19>.fullOuterJoin(rowClass: KClass<R20>, alias: String): InJoinExpectingOn<ExpectingJoin20<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, RT19, R20>, Tuple20<RT1, RT2, RT3, RT4, RT5, RT6, RT7, RT8, RT9, RT10, RT11, RT12, RT13, RT14, RT15, RT16, RT17, RT18, RT19, R20>> =
    fullOuterJoin(rowClass.java, alias)
