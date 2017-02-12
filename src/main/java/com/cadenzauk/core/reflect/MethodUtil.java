/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.core.reflect;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.objenesis.ObjenesisHelper;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        MethodInterceptor interceptor = new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                result.set(method);
                return null;
            }
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

    @SuppressWarnings("unchecked")
    public static <T,V> Method fromReference(MethodReference<T,V> methodReference) {
        return ClassUtil.declaredMethod(methodReference.getClass(), "writeReplace")
            .map(writeReplace -> (SerializedLambda)invoke(writeReplace, methodReference))
            .map(SerializedLambda::getImplClass)
            .map(className -> className.replaceAll("/", "."))
            .map(ClassUtil::forName)
            .map(klass -> fromReference((Class<T>)klass, methodReference))
            .orElseThrow(() -> new RuntimeException("Failed to find writeReplace method in " + methodReference.getClass()));
    }
}
