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

import co.unruly.matchers.OptionalMatchers.contains
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.Optional

open class Widget {
    fun widgetDescription(): String {
        return "Widget"
    }
}

class SpecialWidget : Widget() {
}

internal class KotlinMethodCrackerTest {
    @Test
    fun fromReference() {
        val result = KotlinMethodCracker().fromReference(Widget::widgetDescription)

        val resultClass = result.map { it.declaringClass }
        val resultName = result.map { it.name }
        assertThat(resultClass, contains(Widget::class.java as Class<*>))
        assertThat(resultName, contains("widgetDescription"))
    }

    @Test
    fun referringClass() {
        val referringClass = KotlinMethodCracker().referringClass(SpecialWidget::widgetDescription)

        assertThat(referringClass, contains(SpecialWidget::class.java as Class<*>));
    }
}