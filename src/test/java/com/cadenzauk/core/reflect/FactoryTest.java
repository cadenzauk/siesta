/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
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
