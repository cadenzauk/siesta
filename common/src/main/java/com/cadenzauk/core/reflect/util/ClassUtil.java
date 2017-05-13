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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public final class ClassUtil extends UtilityClass {
    @SuppressWarnings("unchecked")
    public static <T> Class<T> forObject(T value) {
        Objects.requireNonNull(value);
        return (Class<T>) value.getClass();
    }

    public static Optional<Class<?>> forName(String className) {
        try {
            return Optional.of(Class.forName(className));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    public static Method getDeclaredMethod(Class<?> aClass, String name, Class<?>... parameterTypes) {
        return declaredMethod(aClass, name, parameterTypes)
            .orElseThrow(() -> new NoSuchElementException(String.format("No such method as %s(%s) in %s",
                name, Arrays.stream(parameterTypes).map(Object::toString).collect(joining(", ")), aClass)));
    }

    public static Optional<Method> declaredMethod(Class<?> aClass, String name, Class<?>... parameterTypes) {
        try {
            return Optional.of(aClass.getDeclaredMethod(name, parameterTypes));
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    public static Stream<Method> declaredMethods(Class<?> aClass) {
        return Arrays.stream(aClass.getDeclaredMethods());
    }

    public static Field getDeclaredField(Class<?> aClass, String fieldName) {
        return declaredField(aClass, fieldName)
            .orElseThrow(() -> new NoSuchElementException("No such field as " + fieldName + " in " + aClass));
    }

    public static Optional<Field> declaredField(Class<?> aClass, String fieldName) {
        try {
            return Optional.of(aClass.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            return Optional.empty();
        }
    }

    public static Optional<Field> findField(Class<?> aClass, String fieldName) {
        return superclasses(aClass)
            .map(cls -> declaredField(cls, fieldName))
            .flatMap(StreamUtil::of)
            .findFirst();
    }

    public static Optional<Class<?>> superclass(Class<?> aClass) {
        return Optional.ofNullable(aClass.getSuperclass());
    }

    public static Stream<Class<?>> superclasses(Class<?> aClass) {
        return Stream.concat(Stream.of(aClass), superclass(aClass).map(ClassUtil::superclasses).orElseGet(Stream::empty));
    }

    public static <A extends Annotation> boolean hasAnnotation(Class<?> aClass, Class<A> annotationClass) {
        return aClass.getAnnotation(annotationClass) != null;
    }

    public static <A extends Annotation, T> Optional<A> annotation(Class<T> aClass, Class<A> annotationClass) {
        return Optional.ofNullable(aClass.getAnnotation(annotationClass));
    }

    public static <T> Optional<Constructor<T>> constructor(Class<T> aClass, Class<?>... parameterTypes) {
        try {
            return Optional.of(aClass.getDeclaredConstructor(parameterTypes));
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }
}
