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

package com.cadenzauk.core.testutil;


import com.cadenzauk.core.lang.RuntimeInstantiationException;
import com.cadenzauk.core.reflect.Factory;
import com.cadenzauk.core.util.OptionalUtil;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static com.cadenzauk.core.testutil.FluentAssert.calling;

public class IsUtilityClass<T> extends BaseMatcher<Class<T>> {
    @SuppressWarnings("unchecked")
    @Override
    public boolean matches(Object item) {
        return OptionalUtil.as(Class.class, item)
            .filter(cls -> Modifier.isFinal(cls.getModifiers()))
            .map(cls -> {
                calling(() -> Factory.forClass(cls).get())
                    .shouldThrow(RuntimeException.class)
                    .withCause(InvocationTargetException.class)
                    .withCause(RuntimeInstantiationException.class);
                return true;
            })
            .orElse(false);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a final class that cannot be instantiated.");
    }

    public static <T> IsUtilityClass<T> isUtilityClass() {
        return new IsUtilityClass<>();
    }
}
