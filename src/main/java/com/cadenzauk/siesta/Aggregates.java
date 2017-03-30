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

package com.cadenzauk.siesta;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.siesta.grammar.expression.CountDistinctFunction;
import com.cadenzauk.siesta.grammar.expression.CountFunction;
import com.cadenzauk.siesta.grammar.expression.ResolvedColumn;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.grammar.expression.UnaryFunction;
import com.cadenzauk.siesta.grammar.expression.UnresolvedColumn;

public class Aggregates {
    public static <T> TypedExpression<T> max(TypedExpression<T> arg) {
        return UnaryFunction.of(arg, "max");
    }

    public static <T, R> TypedExpression<T> max(Function1<R,T> arg) {
        return UnaryFunction.of(arg, "max");
    }

    public static <T, R> TypedExpression<T> max(FunctionOptional1<R,T> arg) {
        return UnaryFunction.of(arg, "max");
    }

    public static <T, R> TypedExpression<T> max(String alias, Function1<R,T> arg) {
        return UnaryFunction.of(alias, arg, "max");
    }

    public static <T, R> TypedExpression<T> max(String alias, FunctionOptional1<R,T> arg) {
        return UnaryFunction.of(alias, arg, "max");
    }

    public static <T, R> TypedExpression<T> max(Alias<R> alias, Function1<R,T> arg) {
        return UnaryFunction.of(alias, arg, "max");
    }

    public static <T, R> TypedExpression<T> max(Alias<R> alias, FunctionOptional1<R,T> arg) {
        return UnaryFunction.of(alias, arg, "max");
    }

    public static <T> TypedExpression<T> min(TypedExpression<T> arg) {
        return UnaryFunction.of(arg, "min");
    }

    public static <T, R> TypedExpression<T> min(Function1<R,T> arg) {
        return UnaryFunction.of(arg, "min");
    }

    public static <T, R> TypedExpression<T> min(FunctionOptional1<R,T> arg) {
        return UnaryFunction.of(arg, "min");
    }

    public static <T, R> TypedExpression<T> min(String alias, Function1<R,T> arg) {
        return UnaryFunction.of(alias, arg, "min");
    }

    public static <T, R> TypedExpression<T> min(String alias, FunctionOptional1<R,T> arg) {
        return UnaryFunction.of(alias, arg, "min");
    }

    public static <T, R> TypedExpression<T> min(Alias<R> alias, Function1<R,T> arg) {
        return UnaryFunction.of(alias, arg, "min");
    }

    public static <T, R> TypedExpression<T> min(Alias<R> alias, FunctionOptional1<R,T> arg) {
        return UnaryFunction.of(alias, arg, "min");
    }

    public static <T> TypedExpression<T> sum(TypedExpression<T> arg) {
        return UnaryFunction.of(arg, "sum");
    }

    public static <T, R> TypedExpression<T> sum(Function1<R,T> arg) {
        return UnaryFunction.of(arg, "sum");
    }

    public static <T, R> TypedExpression<T> sum(FunctionOptional1<R,T> arg) {
        return UnaryFunction.of(arg, "sum");
    }

    public static <T, R> TypedExpression<T> sum(String alias, Function1<R,T> arg) {
        return UnaryFunction.of(alias, arg, "sum");
    }

    public static <T, R> TypedExpression<T> sum(String alias, FunctionOptional1<R,T> arg) {
        return UnaryFunction.of(alias, arg, "sum");
    }

    public static <T, R> TypedExpression<T> sum(Alias<R> alias, Function1<R,T> arg) {
        return UnaryFunction.of(alias, arg, "sum");
    }

    public static <T, R> TypedExpression<T> sum(Alias<R> alias, FunctionOptional1<R,T> arg) {
        return UnaryFunction.of(alias, arg, "sum");
    }

    public static TypedExpression<Integer> count() {
        return new CountFunction();
    }

    public static <T> TypedExpression<Integer> countDistinct(TypedExpression<T> arg) {
        return new CountDistinctFunction<>(arg);
    }

    public static <T, R> TypedExpression<Integer> countDistinct(Function1<R,T> arg) {
        return new CountDistinctFunction<>(UnresolvedColumn.of(arg));
    }

    public static <T, R> TypedExpression<Integer> countDistinct(FunctionOptional1<R,T> arg) {
        return new CountDistinctFunction<>(UnresolvedColumn.of(arg));
    }

    public static <T, R> TypedExpression<Integer> countDistinct(String alias, Function1<R,T> arg) {
        return new CountDistinctFunction<>(UnresolvedColumn.of(alias, arg));
    }

    public static <T, R> TypedExpression<Integer> countDistinct(String alias, FunctionOptional1<R,T> arg) {
        return new CountDistinctFunction<>(UnresolvedColumn.of(alias, arg));
    }

    public static <T, R> TypedExpression<Integer> countDistinct(Alias<R> alias, Function1<R,T> arg) {
        return new CountDistinctFunction<>(ResolvedColumn.of(alias, arg));
    }

    public static <T, R> TypedExpression<Integer> countDistinct(Alias<R> alias, FunctionOptional1<R,T> arg) {
        return new CountDistinctFunction<>(ResolvedColumn.of(alias, arg));
    }
}
