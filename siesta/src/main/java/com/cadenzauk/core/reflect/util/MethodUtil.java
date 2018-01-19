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
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.core.util.UtilityClass;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import org.objenesis.ObjenesisHelper;

import java.lang.annotation.Annotation;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Stream;

public final class MethodUtil extends UtilityClass {
    public static Object invoke(Method method, Object target, Object... args) {
        try {
            method.setAccessible(true);
            return method.invoke(target, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T, V> Method fromReference(Class<T> c1ass, Function<T,V> methodReference) {
        AtomicReference<Method> result = new AtomicReference<>();
        MethodInterceptor interceptor = (obj, method, args, proxy) -> {
            result.set(method);
            return null;
        };

        Enhancer enhancer = new Enhancer();
        enhancer.setUseCache(false);
        enhancer.setSuperclass(c1ass);
        enhancer.setCallbackType(interceptor.getClass());

        Class<?> proxyClass = enhancer.createClass();
        Enhancer.registerCallbacks(proxyClass, new Callback[]{interceptor});
        @SuppressWarnings("unchecked") T proxy = (T) ObjenesisHelper.newInstance(proxyClass);

        methodReference.apply(proxy);
        return result.get();
    }

    public static <A extends Annotation> Stream<A> annotations(Class<A> annotationClass, Method method) {
        A[] annotationsByType = method.getAnnotationsByType(annotationClass);
        return Arrays.stream(annotationsByType);
    }

    public static <T, V> Method fromReference(Function1<T,V> methodReference) {
        return ClassUtil.declaredMethod(methodReference.getClass(), "writeReplace")
            .map(writeReplace -> (SerializedLambda) invoke(writeReplace, methodReference))
            .flatMap(lambda -> ClassUtil.forName(lambda.getImplClass().replaceAll("/", "."))
                .map(implClass -> ClassUtil.getDeclaredMethod(implClass, lambda.getImplMethodName())))
            .orElseThrow(() -> new RuntimeException("Failed to find writeReplace method in " + methodReference.getClass()));
    }

    public static <T, V> Method fromReference(FunctionOptional1<T,V> methodReference) {
        return ClassUtil.declaredMethod(methodReference.getClass(), "writeReplace")
            .map(writeReplace -> (SerializedLambda) invoke(writeReplace, methodReference))
            .flatMap(lambda -> ClassUtil.forName(lambda.getImplClass().replaceAll("/", "."))
                .map(implClass -> ClassUtil.getDeclaredMethod(implClass, lambda.getImplMethodName())))
            .orElseThrow(() -> new RuntimeException("Failed to find writeReplace method in " + methodReference.getClass()));
    }

}
