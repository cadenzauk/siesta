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
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.jvm.javaMethod

class KotlinMethodCracker : MethodUtil.MethodCracker {
    override fun fromReference(methodReference: Any?): Optional<Method> {
        return crack(methodReference, this::getMethod)
    }

    override fun referringClass(methodReference: Any?): Optional<Class<*>> {
        return crack(methodReference, this::getReferringClass)
    }

    private fun <T> crack(methodReference: Any?, target: (KFunction<*>?) -> T?): Optional<T> {
        if (methodReference == null) {
            return Optional.empty()
        }

        val fromKFunction: T? = target(methodReference as? KFunction<*>)
        if (fromKFunction != null) {
            return Optional.of(fromKFunction)
        }

        val fromGetter = crackProperty(methodReference, target)
        if (fromGetter.isPresent) {
            return fromGetter
        }

        val underlyingFunction = ClassUtil.findField(methodReference.javaClass, "function")
            .map { FieldUtil.get(it, methodReference) }
        val fromUnderlyingFunction: Optional<T> = underlyingFunction
            .map { it as? KFunction<*> }
            .map { target(it) }
        if (fromUnderlyingFunction.isPresent) {
            return fromUnderlyingFunction
        }

        return underlyingFunction.flatMap{ crackProperty(it, target) }
    }

    private fun <T> crackProperty(methodReference: Any?, target: (KFunction<*>?) -> T?): Optional<T> {
        if (methodReference == null) {
            return Optional.empty()
        }
        methodReference.toString()
        return ClassUtil.findField(methodReference.javaClass, "reflected")
            .map { FieldUtil.get(it, methodReference) }
            .map { it as? KProperty<*> }
            .map { it?.getter }
            .map { target(it) }
    }

    private fun getMethod(x: KFunction<*>?): Method? {
        return x?.javaMethod
    }

    private fun getReferringClass(x: KFunction<*>?): Class<*>? {
        return (x?.instanceParameter?.type?.classifier as? KClass<*>)?.java
    }
}
