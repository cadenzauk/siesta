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

package com.cadenzauk.core.reflect.util;

import com.cadenzauk.core.junit.TestCase;
import com.cadenzauk.core.lang.RuntimeInstantiationException;
import com.cadenzauk.core.reflect.Factory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IllegalFormatCodePointException;
import java.util.Optional;
import java.util.stream.Stream;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class MethodUtilTest {
    @Test
    void cannotInstantiate() {
        calling(() -> Factory.forClass(MethodUtil.class).get())
            .shouldThrow(RuntimeException.class)
            .withCause(InvocationTargetException.class)
            .withCause(RuntimeInstantiationException.class);
    }

    @Test
    void invokeNoArgsVoid() throws NoSuchMethodException {
        TestClass mock = Mockito.mock(TestClass.class);
        Method method1 = mock.getClass().getDeclaredMethod("method1");

        MethodUtil.invoke(method1, mock);

        Mockito.verify(mock, times(1)).method1();
    }

    @Test
    void invokeThrowsException() throws NoSuchMethodException {
        TestClass mock = Mockito.mock(TestClass.class);
        doThrow(IllegalFormatCodePointException.class).when(mock).method1();
        Method method1 = mock.getClass().getDeclaredMethod("method1");

        calling(() -> MethodUtil.invoke(method1, mock))
            .shouldThrow(RuntimeException.class)
            .withCause(InvocationTargetException.class)
            .withCause(IllegalFormatCodePointException.class);

        Mockito.verify(mock, times(1)).method1();
    }

    @Test
    void invokeNoArgsWithResult() throws NoSuchMethodException {
        TestClass mock = Mockito.mock(TestClass.class);
        Optional<String> expectedResult = Optional.of("The result");
        when(mock.method2()).thenReturn(expectedResult);
        Method method2 = mock.getClass().getDeclaredMethod("method2");

        Object result = MethodUtil.invoke(method2, mock);

        Mockito.verify(mock, times(1)).method2();
        assertThat(result, is(expectedResult));
    }

    @Test
    void fromReference() {
        Method method = MethodUtil.fromReference(TestClass.class, TestClass::method2);

        assertThat(method.getName(), is("method2"));
    }

    @Test
    void fromReferenceToMethodWithoutClass() {
        Method method = MethodUtil.fromReference(TestDerivedClass::derivedMethod);

        assertThat(method.getName(), is("derivedMethod"));
        assertThat(method.getDeclaringClass().getCanonicalName(), is(TestDerivedClass.class.getCanonicalName()));
    }

    @Test
    void fromReferenceToOptionalMethodWithoutClass() {
        Method method = MethodUtil.fromReference(TestClass::method2);

        assertThat(method.getName(), is("method2"));
        assertThat(method.getDeclaringClass().getCanonicalName(), is(TestClass.class.getCanonicalName()));
    }

    @Test
    void fromReferenceToBaseOptionalMethodWithoutClass() {
        Method method = MethodUtil.fromReference(TestDerivedClass::method2);

        assertThat(method.getName(), is("method2"));
        assertThat(method.getDeclaringClass().getCanonicalName(), is(TestClass.class.getCanonicalName()));
    }

    @Test
    void fromReferenceToBaseOptionalMethodGivenDerivedClass() {
        Method method = MethodUtil.fromReference(TestDerivedClass.class, TestClass::method2);

        assertThat(method.getName(), is("method2"));
        assertThat(method.getDeclaringClass().getCanonicalName(), is(TestClass.class.getCanonicalName()));
    }

    @Test
    void annotationsPresent() {
        Method method = MethodUtil.fromReference(TestDerivedClass.class, TestClass::method2);

        Stream<Transient> annotations = MethodUtil.annotations(Transient.class, method);

        assertThat(annotations.count(), is(1L));
    }

    @Test
    void annotationsNotPresent() {
        Method method = MethodUtil.fromReference(TestDerivedClass.class, TestClass::method2);

        Stream<Column> annotations = MethodUtil.annotations(Column.class, method);

        assertThat(annotations.count(), is(0L));
    }

    @Test
    void annotationsMultiple() {
        Method method = MethodUtil.fromReference(TestDerivedClass.class, TestClass::method2);

        Stream<TestCase> annotations = MethodUtil.annotations(TestCase.class, method);

        assertThat(annotations.count(), is(3L));
    }

    @SuppressWarnings("unused")
    private static class TestClass {
        TestClass(int ignored) {
        }

        void method1() {
            System.out.println("method1");
        }

        @SuppressWarnings("SameReturnValue")
        @Transient
        @TestCase
        @TestCase
        @TestCase
        Optional<String> method2() {
            return Optional.of("Something");
        }
    }

    private static class TestDerivedClass extends TestClass {
        TestDerivedClass(int ignored) {
            super(ignored);
        }

        @SuppressWarnings("SameReturnValue")
        String derivedMethod() {
            return "";
        }
    }

}

