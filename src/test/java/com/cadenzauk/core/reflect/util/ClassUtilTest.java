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

import com.cadenzauk.core.lang.RuntimeInstantiationException;
import com.cadenzauk.core.reflect.Factory;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ClassUtilTest {
    @Test
    public void cannotInstantiate() {
        calling(() -> Factory.forClass(ClassUtil.class).get())
            .shouldThrow(RuntimeException.class)
            .withCause(InvocationTargetException.class)
            .withCause(RuntimeInstantiationException.class);
    }

    @Test
    public void forObject() {
        Class<Number> numberClass = ClassUtil.forObject(BigDecimal.ONE);
        assertThat(numberClass, equalTo(BigDecimal.class));
    }

    @Test
    public void getDeclaredMethodForMethodThatExistsIsTheMethod() {
        Method method0 = ClassUtil.getDeclaredMethod(TestingTarget.class, "method");

        assertThat(method0, notNullValue());
        assertThat(method0.getName(), is("method"));
    }

    @Test
    public void getDeclaredMethodForMethodWithWrongParametersThrows() {
        calling(() -> ClassUtil.getDeclaredMethod(TestingTarget.class, "method", Integer.class))
            .shouldThrow(NoSuchElementException.class)
            .withMessage(is("No such method as method(class java.lang.Integer) in class com.cadenzauk.core.reflect.util.ClassUtilTest$TestingTarget"));
    }

    @Test
    public void declaredMethodForMethodThatExistsIsTheMethod() {
        Optional<Method> method0 = ClassUtil.declaredMethod(TestingTarget.class, "method");

        assertThat(method0.isPresent(), is(true));
        assertThat(method0.map(Method::getName), is(Optional.of("method")));
    }

    @Test
    public void declaredMethodForMethodWithWrongParametersIsEmpty() {
        Optional<Method> method0 = ClassUtil.declaredMethod(TestingTarget.class, "method", Integer.class);

        assertThat(method0, is(Optional.empty()));
    }

    @Test
    public void forNameOfClassThatExists() {
        Optional<Class<?>> result = ClassUtil.forName(TestingTarget.class.getName());

        assertThat(result, is(Optional.of(TestingTarget.class)));
    }

    @Test
    public void forNameOfNonClassIsEmpty() {
        Optional<Class<?>> result = ClassUtil.forName("com.dodgy.nothing.to.see.here.Bob");

        assertThat(result, is(Optional.empty()));
    }

    @Test
    public void getDeclaredFieldPresentIsReturned() {
        Field result = ClassUtil.getDeclaredField(TestingTarget.class, "stringField");

        assertThat(result, notNullValue());
        assertThat(result.getName(), equalTo("stringField"));
    }

    @Test
    public void getDeclaredFieldNotPresentThrows() {
        calling(() -> ClassUtil.getDeclaredField(TestingTarget.class, "nothingToSeeHere"))
            .shouldThrow(NoSuchElementException.class)
            .withMessage(is("No such field as nothingToSeeHere in class com.cadenzauk.core.reflect.util.ClassUtilTest$TestingTarget"));
    }

    @Test
    public void declaredFieldPresentIsReturned() {
        Optional<Field> result = ClassUtil.declaredField(TestingTarget.class, "stringField");

        assertThat(result.isPresent(), equalTo(true));
        assertThat(result.map(Field::getName), equalTo(Optional.of("stringField")));
    }

    @Test
    public void declaredFieldNotPresentIsEmpty() {
        Optional<Field> result = ClassUtil.declaredField(TestingTarget.class, "nothingToSeeHere");

        assertThat(result, is(Optional.empty()));
    }

    @Test
    public void findFieldInTargetFound() {
        Optional<Field> result = ClassUtil.findField(TestingTarget.class, "stringField");

        assertThat(result.isPresent(), is(true));
        assertThat(result.map(Field::getName), is(Optional.of("stringField")));
    }

    @Test
    public void findFieldInBaseFound() {
        Optional<Field> result = ClassUtil.findField(TestingTarget.class, "baseField");

        assertThat(result.isPresent(), is(true));
        assertThat(result.map(Field::getName), is(Optional.of("baseField")));
    }

    @Test
    public void findFieldNotFound() {
        Optional<Field> result = ClassUtil.findField(TestingTarget.class, "yesWeHaveNoBananas");

        assertThat(result, is(Optional.empty()));
    }

    @Test
    public void superclassOfNonObjectIsPresent() {
        Optional<Class<?>> result = ClassUtil.superclass(TestingTargetBase.class);

        assertThat(result, is(Optional.of(Object.class)));
    }

    @Test
    public void superclassOfObjectIsEmpty() {
        Optional<Class<?>> result = ClassUtil.superclass(Object.class);

        assertThat(result, is(Optional.empty()));
    }

    @Test
    public void superclassOfInterfaceIsEmpty() {
        Optional<Class<?>> result = ClassUtil.superclass(TestingInterface.class);

        assertThat(result, is(Optional.empty()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void superclasses() {
        List<Class<?>> result = ClassUtil.superclasses(TestingTarget.class).collect(toList());

        assertThat(result, contains(TestingTarget.class, TestingTargetBase.class, Object.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void superclassesOfObject() {
        List<Class<?>> result = ClassUtil.superclasses(Object.class).collect(toList());

        assertThat(result, contains(Object.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void superclassesOfInterface() {
        List<Class<?>> result = ClassUtil.superclasses(TestingBaseInterface.class).collect(toList());

        assertThat(result, contains(TestingBaseInterface.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void superclassesOfDerivedInterface() {
        List<Class<?>> result = ClassUtil.superclasses(TestingInterface.class).collect(toList());

        assertThat(result, contains(TestingInterface.class));
    }

    @Test
    public void hasAnnotationThatIsPresent() {
        boolean result = ClassUtil.hasAnnotation(TestingTarget.class, Ignore.class);

        assertThat(result, is(true));
    }

    @Test

    public void hasAnnotationThatIsNotPresent() {
        boolean result = ClassUtil.hasAnnotation(TestingTargetBase.class, Ignore.class);

        assertThat(result, is(false));
    }

    @Test
    public void annotationThatIsPresent() {
        Optional<Ignore> result = ClassUtil.annotation(TestingTarget.class, Ignore.class);

        assertThat(result.isPresent(), is(true));
    }

    @Test
    public void annotationThatIsNotPresent() {
        Optional<Ignore> result = ClassUtil.annotation(TestingTargetBase.class, Ignore.class);

        assertThat(result, is(Optional.empty()));
    }

    @Test
    public void defaultConstructorPresent() {
        Optional<Constructor<TestingTargetBase>> result = ClassUtil.constructor(TestingTargetBase.class);

        assertThat(result.isPresent(), is(true));
    }

    @Test
    public void defaultConstructorNotPresent() {
        Optional<Constructor<TestingTarget>> result = ClassUtil.constructor(TestingTarget.class);

        assertThat(result, is(Optional.empty()));
    }

    @Test
    public void constructorWithArgsPresent() {
        Optional<Constructor<TestingTarget>> result = ClassUtil.constructor(TestingTarget.class, String.class);

        assertThat(result.isPresent(), is(true));
    }


    private interface TestingBaseInterface {
    }

    private interface TestingInterface extends TestingBaseInterface {
    }

    @SuppressWarnings("unused")
    private static class TestingTargetBase {
        private Integer baseField;
    }

    @Ignore
    @SuppressWarnings("unused")
    private static class TestingTarget extends TestingTargetBase {
        private final String stringField;

        private TestingTarget(String stringField) {
            this.stringField = stringField;
        }

        public void method() {
        }
    }
}
