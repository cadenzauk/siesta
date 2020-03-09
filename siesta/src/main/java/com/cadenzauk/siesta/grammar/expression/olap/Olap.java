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

package com.cadenzauk.siesta.grammar.expression.olap;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.core.util.UtilityClass;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.grammar.expression.ResolvedColumn;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.grammar.expression.UnresolvedColumn;
import com.google.common.reflect.TypeToken;

public final class Olap extends UtilityClass {
    public static InOlapExpectingPartitionBy<Integer> rowNumber() {
        return function("row_number", TypeToken.of(Integer.class));
    }

    public static <T> InOlapExpectingPartitionBy<T> sum(TypedExpression<T> argument) {
        return function("sum", argument.type(), argument);
    }

    public static <R,T> InOlapExpectingPartitionBy<T> sum(Function1<R,T> method) {
        UnresolvedColumn<T> argument = UnresolvedColumn.of(method);
        return function("sum", argument.type(), argument);
    }

    public static <R,T> InOlapExpectingPartitionBy<T> sum(FunctionOptional1<R,T> method) {
        UnresolvedColumn<T> argument = UnresolvedColumn.of(method);
        return function("sum", argument.type(), argument);
    }

    public static <R,T> InOlapExpectingPartitionBy<T> sum(String alias, Function1<R,T> method) {
        UnresolvedColumn<T> argument = UnresolvedColumn.of(alias, method);
        return function("sum", argument.type(), argument);
    }

    public static <R,T> InOlapExpectingPartitionBy<T> sum(String alias, FunctionOptional1<R,T> method) {
        UnresolvedColumn<T> argument = UnresolvedColumn.of(alias, method);
        return function("sum", argument.type(), argument);
    }

    public static <R, T> InOlapExpectingPartitionBy<T> sum(Alias<R> alias, Function1<R,T> method) {
        ResolvedColumn<T,R> argument = ResolvedColumn.of(alias, method);
        return function("sum", argument.type(), argument);
    }

    public static <R, T> InOlapExpectingPartitionBy<T> sum(Alias<R> alias, FunctionOptional1<R,T> method) {
        ResolvedColumn<T,R> argument = ResolvedColumn.of(alias, method);
        return function("sum", argument.type(), argument);
    }

    private static <T> InOlapExpectingPartitionBy<T> function(String function, TypeToken<T> type, TypedExpression<?>... arguments) {
        return new InOlapExpectingPartitionBy<>(new OlapFunction<>(function, type, arguments));
    }
}
