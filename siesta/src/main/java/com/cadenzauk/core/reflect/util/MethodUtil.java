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

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionInt;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.core.reflect.Factory;
import com.cadenzauk.core.util.Lazy;
import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.core.util.UtilityClass;

import java.lang.annotation.Annotation;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class MethodUtil extends UtilityClass {
    private static final Lazy<Optional<MethodCracker>> KOTLIN_CRACKER = new Lazy<>(MethodUtil::loadKotlinCracker);
    private static final Pattern INSTANTIATED_METHOD_PATTERN = Pattern.compile("\\(L([^;]+);.*");

    public static Object invoke(Method method, Object target, Object... args) {
        try {
            method.setAccessible(true);
            return method.invoke(target, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static <A extends Annotation> Stream<A> annotations(Class<A> annotationClass, Method method) {
        A[] annotationsByType = method.getAnnotationsByType(annotationClass);
        return Arrays.stream(annotationsByType);
    }

    public static <T, V> Method fromReference(Function1<T,V> methodReference) {
        return OptionalUtil.orGet(fromJavaFunction(methodReference), () -> fromKotlinFunction(methodReference))
            .orElseThrow(() -> new RuntimeException("Failed to find writeReplace method in " + methodReference.getClass()));
    }

    public static <T> Method fromReference(FunctionInt<T> methodReference) {
        return OptionalUtil.orGet(fromJavaFunction(methodReference), () -> fromKotlinFunction(methodReference))
            .orElseThrow(() -> new RuntimeException("Failed to find writeReplace method in " + methodReference.getClass()));
    }

    public static <T, V> Method fromReference(FunctionOptional1<T,V> methodReference) {
        return OptionalUtil.orGet(fromJavaFunction(methodReference), () -> fromKotlinFunction(methodReference))
            .orElseThrow(() -> new RuntimeException("Failed to find writeReplace method in " + methodReference.getClass()));
    }

    public static <T, V> Class<?> referringClass(Function1<T,V> methodReference) {
        return OptionalUtil.orGet(referringJavaClass(methodReference), () -> referringKotlinClass(methodReference))
            .orElseThrow(() -> new RuntimeException("Failed to find writeReplace method in " + methodReference.getClass()));
    }

    public static <T> Class<?> referringClass(FunctionInt<T> methodReference) {
        return OptionalUtil.orGet(referringJavaClass(methodReference), () -> referringKotlinClass(methodReference))
            .orElseThrow(() -> new RuntimeException("Failed to find writeReplace method in " + methodReference.getClass()));
    }

    public static <T, V> Class<?> referringClass(FunctionOptional1<T,V> methodReference) {
        return OptionalUtil.orGet(referringJavaClass(methodReference), () -> referringKotlinClass(methodReference))
            .orElseThrow(() -> new RuntimeException("Failed to find writeReplace method in " + methodReference.getClass()));
    }

    private static <T> Optional<Class<?>> referringJavaClass(Object methodReference) {
        return ClassUtil.declaredMethod(methodReference.getClass(), "writeReplace")
            .map(writeReplace -> (SerializedLambda) invoke(writeReplace, methodReference))
            .flatMap(MethodUtil::fromInstantiatedMethodType);
    }

    private static <T> Optional<Class<?>> referringKotlinClass(Object methodReference) {
        return kotlinMethodCracker()
            .flatMap(x -> x.referringClass(methodReference));
    }

    private static Optional<MethodCracker> kotlinMethodCracker() {
        return KOTLIN_CRACKER.get();
    }

    private static Optional<MethodCracker> loadKotlinCracker() {
        return ClassUtil.forName("com.cadenzauk.siesta.kotlin.KotlinMethodCracker")
            .map(Factory::forClass)
            .map(Supplier::get)
            .map(MethodCracker.class::cast);
    }

    private static Optional<Method> fromKotlinFunction(Object methodReference) {
        return kotlinMethodCracker()
            .flatMap(x -> x.fromReference(methodReference));
    }

    private static Optional<Method> fromJavaFunction(Object methodReference) {
        return ClassUtil.declaredMethod(methodReference.getClass(), "writeReplace")
            .map(writeReplace -> (SerializedLambda) invoke(writeReplace, methodReference))
            .flatMap(lambda -> ClassUtil.forName(lambda.getImplClass().replaceAll("/", "."))
                .filter(implClass -> ClassUtil.hasDeclaredMethod(implClass, lambda.getImplMethodName()))
                .map(implClass -> ClassUtil.getDeclaredMethod(implClass, lambda.getImplMethodName())));
    }

    private static Optional<Class<?>> fromInstantiatedMethodType(SerializedLambda lambda) {
        Matcher matcher = INSTANTIATED_METHOD_PATTERN.matcher(lambda.getInstantiatedMethodType());
        return matcher.matches()
            ? ClassUtil.forName(matcher.group(1).replaceAll("/", "."))
            : Optional.empty();
    }

    public interface MethodCracker {
        Optional<Method> fromReference(Object methodReference);
        Optional<Class<?>> referringClass(Object methodReference);
    }
}
