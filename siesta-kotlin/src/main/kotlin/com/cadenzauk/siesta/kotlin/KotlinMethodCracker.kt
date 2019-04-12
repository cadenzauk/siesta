/*
 * Copyright (c) 2019 Cadenza United Kingdom Limited
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

import com.cadenzauk.core.reflect.util.ClassUtil
import com.cadenzauk.core.reflect.util.FieldUtil
import com.cadenzauk.core.reflect.util.MethodUtil
import java.lang.reflect.Method
import java.util.Optional
import java.util.Optional.ofNullable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.jvm.javaMethod

class KotlinMethodCracker : MethodUtil.MethodCracker {
    override fun fromReference(methodReference: Any?): Optional<Method> {
        return kfunctionOf(methodReference)
            .map { it.javaMethod }
    }

    override fun referringClass(methodReference: Any?): Optional<Class<*>> {
        return kfunctionOf(methodReference)
            .flatMap { referringClass(it).map{ it.java } }
    }

    private fun kfunctionOf(methodReference: Any?): Optional<KFunction<*>> {
        return ofNullable(methodReference)
            .filter { it is KFunction<*> }
            .map { it as KFunction<*> }
            .or {
                ofNullable(methodReference)
                    .flatMap { ClassUtil.findField(it.javaClass, "function").map { field -> FieldUtil.get(field, it) } }
                    .map { it as? KFunction<*> }
            }
    }

    private fun referringClass(x: KFunction<*>): Optional<KClass<*>> {
        return Optional.ofNullable(x.instanceParameter)
            .flatMap{ ClassUtil.findField(it.javaClass, "callable").map{ field -> FieldUtil.get(field, it)}}
            .flatMap{ ClassUtil.findField(it.javaClass, "container").map{ field -> FieldUtil.get(field, it)}}
            .map{ it as KClass<*> }
    }
}

fun <T> Optional<T>.or(alt: () -> Optional<T>): Optional<T> = if (this.isPresent) this else alt()
