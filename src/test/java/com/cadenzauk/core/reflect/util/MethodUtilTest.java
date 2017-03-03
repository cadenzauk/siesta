/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.core.reflect.util;

import com.cadenzauk.core.lang.RuntimeInstantiationException;
import com.cadenzauk.core.reflect.Factory;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IllegalFormatCodePointException;
import java.util.Optional;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class MethodUtilTest {
    @Test
    public void cannotInstantiate() {
        calling(() -> Factory.forClass(MethodUtil.class).get())
            .shouldThrow(RuntimeException.class)
            .withCause(InvocationTargetException.class)
            .withCause(RuntimeInstantiationException.class);
    }

    @Test
    public void invokeNoArgsVoid() throws Exception {
        TestClass mock = Mockito.mock(TestClass.class);
        Method method1 = mock.getClass().getMethod("method1");

        MethodUtil.invoke(method1, mock);

        Mockito.verify(mock, times(1)).method1();
    }

    @Test
    public void invokeThrowsException() throws Exception {
        TestClass mock = Mockito.mock(TestClass.class);
        doThrow(IllegalFormatCodePointException.class).when(mock).method1();
        Method method1 = mock.getClass().getMethod("method1");

        calling(() -> MethodUtil.invoke(method1, mock))
            .shouldThrow(RuntimeException.class)
            .withCause(InvocationTargetException.class)
            .withCause(IllegalFormatCodePointException.class);

        Mockito.verify(mock, times(1)).method1();
    }

    @Test
    public void invokeNoArgsWithResult() throws Exception {
        TestClass mock = Mockito.mock(TestClass.class);
        Optional<String> expectedResult = Optional.of("The result");
        when(mock.method2()).thenReturn(expectedResult);
        Method method2 = mock.getClass().getMethod("method2");

        Object result = MethodUtil.invoke(method2, mock);

        Mockito.verify(mock, times(1)).method2();
        assertThat(result, is(expectedResult));
    }

    @Test
    public void fromReference() throws Exception {
        Method method = MethodUtil.fromReference(TestClass.class, TestClass::method2);

        assertThat(method.getName(), is("method2"));
    }

    @Test
    public void fromReferenceToMethodWithoutClass() throws Exception {
        Method method = MethodUtil.fromReference(TestDerivedClass::derivedMethod);

        assertThat(method.getName(), is("derivedMethod"));
        assertThat(method.getDeclaringClass().getCanonicalName(), is(TestDerivedClass.class.getCanonicalName()));
    }

    @Test
    public void fromReferenceToOptionalMethodWithoutClass() throws Exception {
        Method method = MethodUtil.fromReference(TestClass::method2);

        assertThat(method.getName(), is("method2"));
        assertThat(method.getDeclaringClass().getCanonicalName(), is(TestClass.class.getCanonicalName()));
    }

    @Test
    public void fromReferenceToBaseOptionalMethodWithoutClass() throws Exception {
        Method method = MethodUtil.fromReference(TestDerivedClass::method2);

        assertThat(method.getName(), is("method2"));
        assertThat(method.getDeclaringClass().getCanonicalName(), is(TestClass.class.getCanonicalName()));
    }

    @Test
    public void fromReferenceToBaseOptionalMethodGivenDerivedClass() throws Exception {
        Method method = MethodUtil.fromReference(TestDerivedClass.class, TestClass::method2);

        assertThat(method.getName(), is("method2"));
        assertThat(method.getDeclaringClass().getCanonicalName(), is(TestClass.class.getCanonicalName()));
    }

    @SuppressWarnings("unused")
    private static class TestClass {
        public TestClass(int ignored) {
        }

        void method1() {
        }

        Optional<String> method2() {
            return null;
        }
    }

    private static class TestDerivedClass extends TestClass {
        public TestDerivedClass(int ignored) {
            super(ignored);
        }

        public String derivedMethod() {
            return "";
        }
    }

}

