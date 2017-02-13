/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.core.reflect;

import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class MethodUtilTest {
    public static class TestClass {
        public TestClass(int ignored) {
        }

        void method1() {
        }
        Optional<String> method2() {
            return null;
        }
    }

    @Test
    public void invokeNoArgsVoid() throws Exception {
        TestClass mock = Mockito.mock(TestClass.class);
        Method method1 = mock.getClass().getMethod("method1");

        MethodUtil.invoke(method1, mock);

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
    public void fromReferenceWithoutClass() throws Exception {
        Method method = MethodUtil.fromReference(TestClass::method2);

        assertThat(method.getName(), is("method2"));
        assertThat(method.getDeclaringClass().getCanonicalName(), is(TestClass.class.getCanonicalName()));
    }
}