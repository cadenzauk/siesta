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

package com.cadenzauk.core.reflect;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.core.reflect.util.MethodUtil;
import com.cadenzauk.core.util.UtilityClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

public class MethodInfo<C, R> {
    private final Class<C> declaringClass;
    private final Method method;
    private final Class<?> actualType;
    private final Class<R> effectiveType;

    private MethodInfo(Class<C> declaringClass, Method method, Class<?> actualType, Class<R> effectiveType) {
        this.declaringClass = declaringClass;
        this.method = method;
        this.actualType = actualType;
        this.effectiveType = effectiveType;
    }

    public Class<C> declaringClass() {
        return declaringClass;
    }

    public Method method() {
        return method;
    }

    public Class<?> actualType() {
        return actualType;
    }

    public Class<R> effectiveType() {
        return effectiveType;
    }

    public <A extends Annotation> Optional<A> annotation(Class<A> annotationClass) {
        return Optional.ofNullable(method.getAnnotation(annotationClass));
    }

    public static <R, T> Optional<MethodInfo<R,T>> findGetterForField(FieldInfo<R,T> fieldInfo) {
        return Arrays.stream(fieldInfo.declaringClass().getDeclaredMethods())
            .filter(m -> m.getReturnType() == fieldInfo.fieldType())
            .filter(m -> Getter.isGetter(m, fieldInfo.field()))
            .findAny()
            .map(m -> new MethodInfo<>(fieldInfo.declaringClass(), m, fieldInfo.fieldType(), fieldInfo.effectiveType()));
    }

    @SuppressWarnings("unchecked")
    public static <C, F> MethodInfo<C,F> of(Function1<C,F> getter) {
        Method method = MethodUtil.fromReference(getter);
        return new MethodInfo<>((Class<C>) method.getDeclaringClass(), method, method.getReturnType(), (Class<F>) method.getReturnType());
    }

    @SuppressWarnings("unchecked")
    public static <C, F> MethodInfo<C,F> of(FunctionOptional1<C,F> getter) {
        Method method = MethodUtil.fromReference(getter);
        ParameterizedType genericType = (ParameterizedType) method.getGenericReturnType();
        Type argType = genericType.getActualTypeArguments()[0];
        return new MethodInfo<>((Class<C>) method.getDeclaringClass(), method, method.getReturnType(), (Class<F>) argType);
    }
}
