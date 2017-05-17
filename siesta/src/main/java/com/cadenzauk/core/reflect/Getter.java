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

import com.cadenzauk.core.reflect.util.ClassUtil;
import com.cadenzauk.core.reflect.util.FieldUtil;
import com.cadenzauk.core.reflect.util.MethodUtil;
import com.cadenzauk.core.util.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.cadenzauk.core.lang.StringUtil.uppercaseFirst;
import static com.cadenzauk.core.reflect.util.TypeUtil.actualTypeArgument;
import static com.cadenzauk.core.reflect.util.TypeUtil.boxedType;

public final class Getter extends UtilityClass {
    private static final Function<String,String> X = Function.identity();
    private static final Function<String,String> GET_X = s -> "get" + uppercaseFirst(s);
    private static final Function<String,String> IS_X = s -> "is" + uppercaseFirst(s);

    @NotNull
    public static <T, V> Function<T,Optional<V>> forField(Class<T> targetClass, Class<V> argType, Field field) {
        return findGetter(targetClass, argType, field)
            .map(m -> fromMethod(targetClass, argType, m))
            .orElseGet(() -> fromField(targetClass, argType, field));
    }

    public static boolean isGetter(Method method, Field forField) {
        return getMethods().anyMatch(g -> StringUtils.equals(g.apply(forField.getName()), method.getName()));
    }

    @NotNull
    private static <T> Optional<Method> findGetter(Class<T> targetClass, Class<?> argType, Field field) {
        return getMethods()
            .map(f -> f.apply(field.getName()))
            .flatMap(name -> ClassUtil.declaredMethods(targetClass)
                .filter(method -> method.getName().equals(name))
                .filter(method -> method.getParameterCount() == 0)
                .filter(method -> argType.isAssignableFrom(method.getReturnType()) || method.getReturnType() == Optional.class))
            .findFirst();
    }

    @NotNull
    private static Stream<Function<String,String>> getMethods() {
        return Stream.of(GET_X, X, IS_X);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    private static <T, V> Function<T,Optional<V>> fromMethod(Class<T> targetClass, Class<V> argType, Method method) {
        if (argType.isAssignableFrom(method.getReturnType())) {
            return t -> Optional.ofNullable(boxedType(argType).cast(MethodUtil.invoke(method, t)));
        }
        if (method.getReturnType() == Optional.class && argType.isAssignableFrom(actualTypeArgument((ParameterizedType) method.getGenericReturnType(), 0))) {
            return t -> ((Optional<Object>) MethodUtil.invoke(method, t)).map(argType::cast);
        }
        throw new IllegalArgumentException(String.format("Cannot convert %s into a Function<%s,Optional<%s>>.", method, targetClass, argType));
    }

    @SuppressWarnings("unchecked")
    @NotNull
    private static <T, V> Function<T,Optional<V>> fromField(Class<T> targetClass, Class<V> argType, Field field) {
        if (argType.isAssignableFrom(field.getType())) {
            return t -> Optional.ofNullable(boxedType(argType).cast(FieldUtil.get(field, t)));
        }
        if (field.getType() == Optional.class && argType.isAssignableFrom(actualTypeArgument((ParameterizedType) field.getGenericType(), 0))) {
            return t -> ((Optional<Object>) FieldUtil.get(field, t)).map(argType::cast);
        }
        throw new IllegalArgumentException(String.format("Cannot convert %s into a Function<%s,Optional<%s>>.", field, targetClass, argType));
    }
}
