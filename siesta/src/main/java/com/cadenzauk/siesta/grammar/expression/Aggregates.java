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

package com.cadenzauk.siesta.grammar.expression;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.core.util.UtilityClass;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.dialect.function.aggregate.AggregateFunctionSpecs;
import com.google.common.reflect.TypeToken;

import static com.cadenzauk.siesta.dialect.function.aggregate.AggregateFunctionSpecs.AVG;
import static com.cadenzauk.siesta.dialect.function.aggregate.AggregateFunctionSpecs.MAX;
import static com.cadenzauk.siesta.dialect.function.aggregate.AggregateFunctionSpecs.MIN;
import static com.cadenzauk.siesta.dialect.function.aggregate.AggregateFunctionSpecs.SUM;

public final class Aggregates extends UtilityClass {

    public static <T> TypedExpression<T> max(T arg) {
        return SqlFunction.of(MAX, TypedExpression.value(arg));
    }

    public static <T> TypedExpression<T> max(TypedExpression<T> arg) {
        return SqlFunction.of(MAX, arg);
    }

    public static <T> TypedExpression<T> max(Label<T> arg) {
        return SqlFunction.of(MAX, arg);
    }

    public static <T, R> TypedExpression<T> max(Function1<R,T> arg) {
        return SqlFunction.of(MAX, arg);
    }

    public static <T, R> TypedExpression<T> max(FunctionOptional1<R,T> arg) {
        return SqlFunction.of(MAX, arg);
    }

    public static <T, R> TypedExpression<T> max(String alias, Function1<R,T> arg) {
        return SqlFunction.of(MAX, alias, arg);
    }

    public static <T, R> TypedExpression<T> max(String alias, FunctionOptional1<R,T> arg) {
        return SqlFunction.of(MAX, alias, arg);
    }

    public static <T, R> TypedExpression<T> max(Alias<R> alias, Function1<R,T> arg) {
        return SqlFunction.of(MAX, alias, arg);
    }

    public static <T, R> TypedExpression<T> max(Alias<R> alias, FunctionOptional1<R,T> arg) {
        return SqlFunction.of(MAX, alias, arg);
    }

    public static <T> TypedExpression<T> min(T arg) {
        return SqlFunction.of(MIN, TypedExpression.value(arg));
    }

    public static <T> TypedExpression<T> min(TypedExpression<T> arg) {
        return SqlFunction.of(MIN, arg);
    }

    public static <T> TypedExpression<T> min(Label<T> arg) {
        return SqlFunction.of(MIN, arg);
    }

    public static <T, R> TypedExpression<T> min(Function1<R,T> arg) {
        return SqlFunction.of(MIN, arg);
    }

    public static <T, R> TypedExpression<T> min(FunctionOptional1<R,T> arg) {
        return SqlFunction.of(MIN, arg);
    }

    public static <T, R> TypedExpression<T> min(String alias, Function1<R,T> arg) {
        return SqlFunction.of(MIN, alias, arg);
    }

    public static <T, R> TypedExpression<T> min(String alias, FunctionOptional1<R,T> arg) {
        return SqlFunction.of(MIN, alias, arg);
    }

    public static <T, R> TypedExpression<T> min(Alias<R> alias, Function1<R,T> arg) {
        return SqlFunction.of(MIN, alias, arg);
    }

    public static <T, R> TypedExpression<T> min(Alias<R> alias, FunctionOptional1<R,T> arg) {
        return SqlFunction.of(MIN, alias, arg);
    }

    public static <T> TypedExpression<T> sum(T arg) {
        return SqlFunction.of(SUM, TypedExpression.value(arg));
    }

    public static <T extends Number> TypedExpression<T> sum(TypedExpression<T> arg) {
        return SqlFunction.of(SUM, arg);
    }

    public static <T extends Number> TypedExpression<T> sum(Label<T> arg) {
        return SqlFunction.of(SUM, arg);
    }

    public static <T extends Number, R> TypedExpression<T> sum(Function1<R,T> arg) {
        return SqlFunction.of(SUM, arg);
    }

    public static <T extends Number, R> TypedExpression<T> sum(FunctionOptional1<R,T> arg) {
        return SqlFunction.of(SUM, arg);
    }

    public static <T extends Number, R> TypedExpression<T> sum(String alias, Function1<R,T> arg) {
        return SqlFunction.of(SUM, alias, arg);
    }

    public static <T extends Number, R> TypedExpression<T> sum(String alias, FunctionOptional1<R,T> arg) {
        return SqlFunction.of(SUM, alias, arg);
    }

    public static <T extends Number, R> TypedExpression<T> sum(Alias<R> alias, Function1<R,T> arg) {
        return SqlFunction.of(SUM, alias, arg);
    }

    public static <T extends Number, R> TypedExpression<T> sum(Alias<R> alias, FunctionOptional1<R,T> arg) {
        return SqlFunction.of(SUM, alias, arg);
    }

    public static <T> TypedExpression<T> avg(T arg) {
        return SqlFunction.of(AVG, TypedExpression.value(arg));
    }

    public static <T extends Number> TypedExpression<T> avg(TypedExpression<T> arg) {
        return SqlFunction.of(AVG, arg);
    }

    public static <T extends Number> TypedExpression<T> avg(Label<T> arg) {
        return SqlFunction.of(AVG, arg);
    }

    public static <T extends Number, R> TypedExpression<T> avg(Function1<R,T> arg) {
        return SqlFunction.of(AVG, arg);
    }

    public static <T extends Number, R> TypedExpression<T> avg(FunctionOptional1<R,T> arg) {
        return SqlFunction.of(AVG, arg);
    }

    public static <T extends Number, R> TypedExpression<T> avg(String alias, Function1<R,T> arg) {
        return SqlFunction.of(AVG, alias, arg);
    }

    public static <T extends Number, R> TypedExpression<T> avg(String alias, FunctionOptional1<R,T> arg) {
        return SqlFunction.of(AVG, alias, arg);
    }

    public static <T extends Number, R> TypedExpression<T> avg(Alias<R> alias, Function1<R,T> arg) {
        return SqlFunction.of(AVG, alias, arg);
    }

    public static <T extends Number, R> TypedExpression<T> avg(Alias<R> alias, FunctionOptional1<R,T> arg) {
        return SqlFunction.of(AVG, alias, arg);
    }

    public static TypedExpression<Integer> count() {
        return new CountFunction<>(AggregateFunctionSpecs.COUNT, TypeToken.of(Integer.class));
    }

    public static <T> TypedExpression<Integer> count(TypedExpression<T> arg) {
        return new CountFunction<>(AggregateFunctionSpecs.COUNT, TypeToken.of(Integer.class), arg);
    }

    public static <T> TypedExpression<Integer> count(Label<T> arg) {
        return new CountFunction<>(AggregateFunctionSpecs.COUNT, TypeToken.of(Integer.class), UnresolvedColumn.of(arg));
    }

    public static <T, R> TypedExpression<Integer> count(Function1<R,T> arg) {
        return new CountFunction<>(AggregateFunctionSpecs.COUNT, TypeToken.of(Integer.class), UnresolvedColumn.of(arg));
    }

    public static <T, R> TypedExpression<Integer> count(FunctionOptional1<R,T> arg) {
        return new CountFunction<>(AggregateFunctionSpecs.COUNT, TypeToken.of(Integer.class), UnresolvedColumn.of(arg));
    }

    public static <T, R> TypedExpression<Integer> count(String alias, Function1<R,T> arg) {
        return new CountFunction<>(AggregateFunctionSpecs.COUNT, TypeToken.of(Integer.class), UnresolvedColumn.of(alias, arg));
    }

    public static <T, R> TypedExpression<Integer> count(String alias, FunctionOptional1<R,T> arg) {
        return new CountFunction<>(AggregateFunctionSpecs.COUNT, TypeToken.of(Integer.class), UnresolvedColumn.of(alias, arg));
    }

    public static <T, R> TypedExpression<Integer> count(Alias<R> alias, Function1<R,T> arg) {
        return new CountFunction<>(AggregateFunctionSpecs.COUNT, TypeToken.of(Integer.class), ResolvedColumn.of(alias, arg));
    }

    public static <T, R> TypedExpression<Integer> count(Alias<R> alias, FunctionOptional1<R,T> arg) {
        return new CountFunction<>(AggregateFunctionSpecs.COUNT, TypeToken.of(Integer.class), ResolvedColumn.of(alias, arg));
    }

    public static TypedExpression<Long> countBig() {
        return new CountFunction<>(AggregateFunctionSpecs.COUNT_BIG, TypeToken.of(Long.class));
    }

    public static <T> TypedExpression<Long> countBig(TypedExpression<T> arg) {
        return new CountFunction<>(AggregateFunctionSpecs.COUNT_BIG, TypeToken.of(Long.class), arg);
    }

    public static <T> TypedExpression<Long> countBig(Label<T> arg) {
        return new CountFunction<>(AggregateFunctionSpecs.COUNT_BIG, TypeToken.of(Long.class), UnresolvedColumn.of(arg));
    }

    public static <T, R> TypedExpression<Long> countBig(Function1<R,T> arg) {
        return new CountFunction<>(AggregateFunctionSpecs.COUNT_BIG, TypeToken.of(Long.class), UnresolvedColumn.of(arg));
    }

    public static <T, R> TypedExpression<Long> countBig(FunctionOptional1<R,T> arg) {
        return new CountFunction<>(AggregateFunctionSpecs.COUNT_BIG, TypeToken.of(Long.class), UnresolvedColumn.of(arg));
    }

    public static <T, R> TypedExpression<Long> countBig(String alias, Function1<R,T> arg) {
        return new CountFunction<>(AggregateFunctionSpecs.COUNT_BIG, TypeToken.of(Long.class), UnresolvedColumn.of(alias, arg));
    }

    public static <T, R> TypedExpression<Long> countBig(String alias, FunctionOptional1<R,T> arg) {
        return new CountFunction<>(AggregateFunctionSpecs.COUNT_BIG, TypeToken.of(Long.class), UnresolvedColumn.of(alias, arg));
    }

    public static <T, R> TypedExpression<Long> countBig(Alias<R> alias, Function1<R,T> arg) {
        return new CountFunction<>(AggregateFunctionSpecs.COUNT_BIG, TypeToken.of(Long.class), ResolvedColumn.of(alias, arg));
    }

    public static <T, R> TypedExpression<Long> countBig(Alias<R> alias, FunctionOptional1<R,T> arg) {
        return new CountFunction<>(AggregateFunctionSpecs.COUNT_BIG, TypeToken.of(Long.class), ResolvedColumn.of(alias, arg));
    }

    public static <T> TypedExpression<Integer> countDistinct(TypedExpression<T> arg) {
        return new CountDistinctFunction<>(AggregateFunctionSpecs.COUNT_DISTINCT, TypeToken.of(Integer.class), arg);
    }

    public static <T> TypedExpression<Integer> countDistinct(Label<T> arg) {
        return new CountDistinctFunction<>(AggregateFunctionSpecs.COUNT_DISTINCT, TypeToken.of(Integer.class), UnresolvedColumn.of(arg));
    }

    public static <T, R> TypedExpression<Integer> countDistinct(Function1<R,T> arg) {
        return new CountDistinctFunction<>(AggregateFunctionSpecs.COUNT_DISTINCT, TypeToken.of(Integer.class), UnresolvedColumn.of(arg));
    }

    public static <T, R> TypedExpression<Integer> countDistinct(FunctionOptional1<R,T> arg) {
        return new CountDistinctFunction<>(AggregateFunctionSpecs.COUNT_DISTINCT, TypeToken.of(Integer.class), UnresolvedColumn.of(arg));
    }

    public static <T, R> TypedExpression<Integer> countDistinct(String alias, Function1<R,T> arg) {
        return new CountDistinctFunction<>(AggregateFunctionSpecs.COUNT_DISTINCT, TypeToken.of(Integer.class), UnresolvedColumn.of(alias, arg));
    }

    public static <T, R> TypedExpression<Integer> countDistinct(String alias, FunctionOptional1<R,T> arg) {
        return new CountDistinctFunction<>(AggregateFunctionSpecs.COUNT_DISTINCT, TypeToken.of(Integer.class), UnresolvedColumn.of(alias, arg));
    }

    public static <T, R> TypedExpression<Integer> countDistinct(Alias<R> alias, Function1<R,T> arg) {
        return new CountDistinctFunction<>(AggregateFunctionSpecs.COUNT_DISTINCT, TypeToken.of(Integer.class), ResolvedColumn.of(alias, arg));
    }

    public static <T, R> TypedExpression<Integer> countDistinct(Alias<R> alias, FunctionOptional1<R,T> arg) {
        return new CountDistinctFunction<>(AggregateFunctionSpecs.COUNT_DISTINCT, TypeToken.of(Integer.class), ResolvedColumn.of(alias, arg));
    }

    public static <T> TypedExpression<Long> countBigDistinct(TypedExpression<T> arg) {
        return new CountDistinctFunction<>(AggregateFunctionSpecs.COUNT_BIG_DISTINCT, TypeToken.of(Long.class), arg);
    }

    public static <T> TypedExpression<Long> countBigDistinct(Label<T> arg) {
        return new CountDistinctFunction<>(AggregateFunctionSpecs.COUNT_BIG_DISTINCT, TypeToken.of(Long.class), UnresolvedColumn.of(arg));
    }

    public static <T, R> TypedExpression<Long> countBigDistinct(Function1<R,T> arg) {
        return new CountDistinctFunction<>(AggregateFunctionSpecs.COUNT_BIG_DISTINCT, TypeToken.of(Long.class), UnresolvedColumn.of(arg));
    }

    public static <T, R> TypedExpression<Long> countBigDistinct(FunctionOptional1<R,T> arg) {
        return new CountDistinctFunction<>(AggregateFunctionSpecs.COUNT_BIG_DISTINCT, TypeToken.of(Long.class), UnresolvedColumn.of(arg));
    }

    public static <T, R> TypedExpression<Long> countBigDistinct(String alias, Function1<R,T> arg) {
        return new CountDistinctFunction<>(AggregateFunctionSpecs.COUNT_BIG_DISTINCT, TypeToken.of(Long.class), UnresolvedColumn.of(alias, arg));
    }

    public static <T, R> TypedExpression<Long> countBigDistinct(String alias, FunctionOptional1<R,T> arg) {
        return new CountDistinctFunction<>(AggregateFunctionSpecs.COUNT_BIG_DISTINCT, TypeToken.of(Long.class), UnresolvedColumn.of(alias, arg));
    }

    public static <T, R> TypedExpression<Long> countBigDistinct(Alias<R> alias, Function1<R,T> arg) {
        return new CountDistinctFunction<>(AggregateFunctionSpecs.COUNT_BIG_DISTINCT, TypeToken.of(Long.class), ResolvedColumn.of(alias, arg));
    }

    public static <T, R> TypedExpression<Long> countBigDistinct(Alias<R> alias, FunctionOptional1<R,T> arg) {
        return new CountDistinctFunction<>(AggregateFunctionSpecs.COUNT_BIG_DISTINCT, TypeToken.of(Long.class), ResolvedColumn.of(alias, arg));
    }
}
