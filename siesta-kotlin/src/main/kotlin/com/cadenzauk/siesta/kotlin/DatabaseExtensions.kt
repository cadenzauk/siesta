package com.cadenzauk.siesta.kotlin

import com.cadenzauk.siesta.Database
import com.cadenzauk.siesta.grammar.select.ExpectingJoin1
import kotlin.reflect.KClass

fun <R : Any> Database?.from(kclass: KClass<R>): ExpectingJoin1<R> {
    return this!!.from(kclass.java)
}

fun <R : Any> Database?.from(kclass: KClass<R>, alias: String): ExpectingJoin1<R> {
    return this!!.from(kclass.java, alias)
}
