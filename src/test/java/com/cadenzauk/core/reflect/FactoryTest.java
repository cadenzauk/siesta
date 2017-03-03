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

package com.cadenzauk.core.reflect;

import com.cadenzauk.core.lang.RuntimeInstantiationException;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class FactoryTest {
    @Test
    public void cannotInstantiate() {
        calling(() -> Factory.forClass(Factory.class).get())
            .shouldThrow(RuntimeException.class)
            .withCause(InvocationTargetException.class)
            .withCause(RuntimeInstantiationException.class);

    }
    @Test
    public void forClassOnClassWithDefaultConstructor() {
        Supplier<ClassWithMultipleConstructors> factory = Factory.forClass(ClassWithMultipleConstructors.class);

        ClassWithMultipleConstructors result = factory.get();
        assertThat(result, notNullValue());
        assertThat(result.intValue(), is(501));
    }

    @Test
    public void forClassOnClassWithNoDefaultConstructor() {
        calling(() -> Factory.forClass(NoDefaultConstructor.class))
            .shouldThrow(RuntimeException.class)
            .withMessage(is("No default constructor for class com.cadenzauk.core.reflect.FactoryTest$NoDefaultConstructor"));

    }

    @SuppressWarnings("unused")
    private static class NoDefaultConstructor {
        public NoDefaultConstructor(int i) {
        }
    }

    private static abstract class AnAbstractClass {
    }

    @SuppressWarnings("unused")
    private static class ClassWithMultipleConstructors extends AnAbstractClass {
        private final int intValue;

        private ClassWithMultipleConstructors() {
            intValue = 501;
        }

        private ClassWithMultipleConstructors(int intValue) {
            this.intValue = intValue;
        }

        protected ClassWithMultipleConstructors(String intValue) {
            this.intValue = Integer.parseInt(intValue);
        }

        public int intValue() {
            return intValue;
        }
    }
}
