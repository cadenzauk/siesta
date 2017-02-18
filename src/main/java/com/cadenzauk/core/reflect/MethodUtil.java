/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.core.reflect;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import org.objenesis.ObjenesisHelper;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class MethodUtil {
    public static Object invoke(Method method, Object target, Object... args) {
        try {
            method.setAccessible(true);
            return method.invoke(target, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T,V> Method fromReference(Class<T> c1ass, Function<T,V> methodReference) {
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
        Enhancer.registerCallbacks(proxyClass, new Callback[] { interceptor });
        @SuppressWarnings("unchecked") T proxy = (T) ObjenesisHelper.newInstance(proxyClass);

        methodReference.apply(proxy);
        return result.get();
    }

    public static <T,V> Method fromReference(Function1<T,V> methodReference) {
        return ClassUtil.declaredMethod(methodReference.getClass(), "writeReplace")
            .map(writeReplace -> (SerializedLambda)invoke(writeReplace, methodReference))
            .flatMap(lambda -> {
                Class<?> implClass = ClassUtil.forName(lambda.getImplClass().replaceAll("/", "."));
                return ClassUtil.declaredMethod(implClass, lambda.getImplMethodName());
            })
            .orElseThrow(() -> new RuntimeException("Failed to find writeReplace method in " + methodReference.getClass()));
    }

    public static <T,V> Method fromReference(FunctionOptional1<T,V> methodReference) {
        return ClassUtil.declaredMethod(methodReference.getClass(), "writeReplace")
            .map(writeReplace -> (SerializedLambda)invoke(writeReplace, methodReference))
            .flatMap(lambda -> {
                Class<?> implClass = ClassUtil.forName(lambda.getImplClass().replaceAll("/", "."));
                return ClassUtil.declaredMethod(implClass, lambda.getImplMethodName());
            })
            .orElseThrow(() -> new RuntimeException("Failed to find writeReplace method in " + methodReference.getClass()));
    }
}
