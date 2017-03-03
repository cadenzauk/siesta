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

import com.cadenzauk.core.util.UtilityClass;
import com.cadenzauk.core.stream.StreamUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public final class ClassUtil extends UtilityClass {
    public static Optional<Class<?>> forName(String className) {
        try {
            return Optional.of(Class.forName(className));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    public static Method getDeclaredMethod(Class<?> klass, String name, Class<?>... parameterTypes) {
        return declaredMethod(klass, name, parameterTypes)
            .orElseThrow(() -> new NoSuchElementException(String.format("No such method as %s(%s) in %s",
                name, Arrays.stream(parameterTypes).map(Object::toString).collect(joining(", ")), klass)));
    }

    public static Optional<Method> declaredMethod(Class<?> klass, String name, Class<?>... parameterTypes) {
        try {
            return Optional.of(klass.getDeclaredMethod(name, parameterTypes));
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    public static Stream<Method> declaredMethods(Class<?> klass) {
        return Arrays.stream(klass.getDeclaredMethods());
    }

    public static Field getDeclaredField(Class<?> klass, String fieldName) {
        return declaredField(klass, fieldName)
            .orElseThrow(() -> new NoSuchElementException("No such field as " + fieldName + " in " + klass));
    }

    public static Optional<Field> declaredField(Class<?> klass, String fieldName) {
        try {
            return Optional.of(klass.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            return Optional.empty();
        }
    }

    public static Optional<Field> findField(Class<?> klass, String fieldName) {
        return superclasses(klass)
            .map(cls -> declaredField(cls, fieldName))
            .flatMap(StreamUtil::of)
            .findFirst();
    }

    public static Optional<Class<?>> superclass(Class<?> klass) {
        return Optional.ofNullable(klass.getSuperclass());
    }

    public static Stream<Class<?>> superclasses(Class<?> klass) {
        return Stream.concat(Stream.of(klass), superclass(klass).map(ClassUtil::superclasses).orElseGet(Stream::empty));
    }

    public static <A extends Annotation> boolean hasAnnotation(Class<?> klass, Class<A> annotationClass) {
        return klass.getAnnotation(annotationClass) != null;
    }

    public static <A extends Annotation, T> Optional<A> annotation(Class<T> klass, Class<A> annotationClass) {
        return Optional.ofNullable(klass.getAnnotation(annotationClass));
    }

    public static <T> Optional<Constructor<T>> constructor(Class<T> klass, Class<?>... parameterTypes) {
        try {
            return Optional.of(klass.getDeclaredConstructor(parameterTypes));
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }
}
