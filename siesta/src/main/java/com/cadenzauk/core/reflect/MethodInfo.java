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
import com.cadenzauk.core.function.FunctionInt;
import com.cadenzauk.core.lang.StringUtil;
import com.cadenzauk.core.reflect.util.ClassUtil;
import com.cadenzauk.core.reflect.util.FieldUtil;
import com.cadenzauk.core.reflect.util.MethodUtil;
import com.cadenzauk.core.stream.StreamUtil;
import com.cadenzauk.core.util.Lazy;
import com.google.common.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

public class MethodInfo<C, R> {
    private final TypeToken<? super C> declaringType;
    private final TypeToken<C> referringType;
    private final Method method;
    private final Class<?> actualType;
    private final Class<R> effectiveType;
    private final Lazy<Optional<FieldInfo<C,?>>> field = new Lazy<>(this::findField);

    private MethodInfo(TypeToken<? super C> declaringType, TypeToken<C> referringType, Method method, Class<?> actualType, Class<R> effectiveType) {
        this.declaringType = declaringType;
        this.referringType = referringType;
        this.method = method;
        this.actualType = actualType;
        this.effectiveType = effectiveType;
    }

    public TypeToken<? super C> declaringType() {
        return declaringType;
    }

    public TypeToken<C> referringType() {
        return referringType;
    }

    @SuppressWarnings("unchecked")
    public Class<C> referringClass() {
        return (Class<C>) referringType.getRawType();
    }

    public Method method() {
        return method;
    }

    public Class<?> actualType() {
        return actualType;
    }

    public Class<R> effectiveClass() {
        return effectiveType;
    }

    public TypeToken<R> effectiveType() {
        return TypeToken.of(effectiveType);
    }

    public Optional<FieldInfo<C, ?>> field() {
        return field.get();
    }

    public String propertyName() {
        return field()
            .map(FieldInfo::name)
            .orElseGet(this::defaultPropertyName);
    }

    private String defaultPropertyName() {
        if (method.getName().startsWith("get")) {
            return StringUtil.lowercaseFirst(method.getName().substring(3));
        }
        return method.getName();
    }

    public <A extends Annotation> Optional<A> annotation(Class<A> annotationClass) {
        return Optional.ofNullable(method.getAnnotation(annotationClass));
    }

    private Optional<FieldInfo<C, ?>> findField() {
        return Getter.possibleFieldNames(method.getName())
            .map(name -> ClassUtil.findField(referringType.getRawType(), name))
            .flatMap(StreamUtil::of)
            .filter(f -> actualType.isAssignableFrom(f.getType()))
            .findFirst()
            .map(field -> FieldInfo.of(referringType, field, actualType));
    }

    public static <R, T> Optional<MethodInfo<R,T>> findGetterForField(FieldInfo<R,T> fieldInfo) {
        return Arrays.stream(fieldInfo.declaringClass().getDeclaredMethods())
            .filter(m -> m.getReturnType() == fieldInfo.fieldType())
            .filter(m -> Getter.isGetter(m, fieldInfo.field()))
            .findAny()
            .map(m -> new MethodInfo<>(
                TypeToken.of(fieldInfo.declaringClass()),
                TypeToken.of(fieldInfo.declaringClass()),
                m,
                fieldInfo.fieldType(),
                fieldInfo.effectiveClass()));
    }

    @SuppressWarnings("unchecked")
    public static <C, F> MethodInfo<C,F> of(Function1<C,F> getter) {
        Method method = MethodUtil.fromReference(getter);
        return new MethodInfo<>(
            TypeToken.of((Class<C>) method.getDeclaringClass()),
            TypeToken.of((Class<C>) MethodUtil.referringClass(getter)),
            method,
            method.getReturnType(),
            (Class<F>) method.getReturnType());
    }

    @SuppressWarnings("unchecked")
    public static <C> MethodInfo<C,Integer> of(FunctionInt<C> getter) {
        Method method = MethodUtil.fromReference(getter);
        return new MethodInfo<>(
            TypeToken.of((Class<C>) method.getDeclaringClass()),
            TypeToken.of((Class<C>) MethodUtil.referringClass(getter)),
            method,
            method.getReturnType(),
            Integer.TYPE);
    }

    @SuppressWarnings("unchecked")
    public static <C, F> MethodInfo<C,F> of(FunctionOptional1<C,F> getter) {
        Method method = MethodUtil.fromReference(getter);
        ParameterizedType genericType = (ParameterizedType) method.getGenericReturnType();
        Type argType = genericType.getActualTypeArguments()[0];
        return new MethodInfo<>(
            TypeToken.of((Class<C>) method.getDeclaringClass()),
            TypeToken.of((Class<C>) MethodUtil.referringClass(getter)),
            method,
            method.getReturnType(),
            (Class<F>) argType);
    }
}
