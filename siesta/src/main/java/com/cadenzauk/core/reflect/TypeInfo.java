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
import com.cadenzauk.core.reflect.util.TypeUtil;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.cadenzauk.core.util.OptionalUtil.as;

public class TypeInfo {
    private final Type type;
    private final Optional<ParameterizedType> parameterizedType;
    private final Optional<GenericArrayType> genericArrayType;

    private TypeInfo(Type type) {
        this.type = type;
        parameterizedType = as(ParameterizedType.class, type);
        genericArrayType = as(GenericArrayType.class, type);
    }

    @Override
    public String toString() {
        return TypeUtil.toString(type);
    }

    public TypeInfo arrayComponentType() {
        if (!rawClass().isArray()) {
            throw new NoSuchElementException(TypeUtil.toString(type) + " is not an array type.");
        }
        return genericArrayType
            .map(GenericArrayType::getGenericComponentType)
            .map(TypeInfo::new)
            .orElseGet(() -> new TypeInfo(TypeUtil.primitiveComponentType(type)));
    }

    public TypeInfo actualTypeArgument(int index) {
        return new TypeInfo(parameterizedType
            .map(p -> typeArg(p, index))
            .orElseThrow(() -> new NoSuchElementException(TypeUtil.toString(type) + " is not a parameterized type so does not have actual type arguments.")));
    }

    public Class<?> rawClass() {
        return parameterizedType
            .map(ParameterizedType::getRawType)
            .map(Class.class::cast)
            .orElseGet(() -> genericArrayType
                .flatMap(x -> ClassUtil.forArrayOf(of(x.getGenericComponentType()).rawClass()))
                .orElseGet(() -> Class.class.cast(type)));
    }

    private static Type typeArg(ParameterizedType p, int index) {
        Type[] actualTypeArguments = p.getActualTypeArguments();
        if (index < 0 || index >= actualTypeArguments.length) {
            throw new NoSuchElementException(TypeUtil.toString(p) + " does not have type argument " + index + ".");
        }
        return actualTypeArguments[index];
    }

    public static TypeInfo of(Type t) {
        return new TypeInfo(t);
    }

    public static TypeInfo ofParameter(Method method, int parameter) {
        return new TypeInfo(method.getGenericParameterTypes()[parameter]);
    }
}
