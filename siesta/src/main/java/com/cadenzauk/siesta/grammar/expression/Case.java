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
import com.cadenzauk.siesta.Alias;

public class Case {
    public static CaseExpression.InFirstWhenExpectingThen when(BooleanExpression expression) {
        return new CaseExpression.InFirstWhenExpectingThen(expression);
    }

    public static <T> ExpressionBuilder<T,CaseExpression.InFirstWhenExpectingThen> when(T val) {
        return ExpressionBuilder.of(ValueExpression.of(val), CaseExpression.InFirstWhenExpectingThen::new);
    }

    public static <T> ExpressionBuilder<T,CaseExpression.InFirstWhenExpectingThen> when(TypedExpression<T> expression) {
        return ExpressionBuilder.of(expression, CaseExpression.InFirstWhenExpectingThen::new);
    }

    public static <T,R> ExpressionBuilder<R,CaseExpression.InFirstWhenExpectingThen> when(Function1<T,R> method) {
        return ExpressionBuilder.of(UnresolvedColumn.of(method), CaseExpression.InFirstWhenExpectingThen::new);
    }

    public static <T,R> ExpressionBuilder<R,CaseExpression.InFirstWhenExpectingThen> when(FunctionOptional1<T,R> method) {
        return ExpressionBuilder.of(UnresolvedColumn.of(method), CaseExpression.InFirstWhenExpectingThen::new);
    }

    public static <T,R> ExpressionBuilder<R,CaseExpression.InFirstWhenExpectingThen> when(String alias, Function1<T,R> method) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, method), CaseExpression.InFirstWhenExpectingThen::new);
    }

    public static <T,R> ExpressionBuilder<R,CaseExpression.InFirstWhenExpectingThen> when(String alias, FunctionOptional1<T,R> method) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, method), CaseExpression.InFirstWhenExpectingThen::new);
    }

    public static <T,R> ExpressionBuilder<R,CaseExpression.InFirstWhenExpectingThen> when(Alias<T> alias, Function1<T,R> method) {
        return ExpressionBuilder.of(ResolvedColumn.of(alias, method), CaseExpression.InFirstWhenExpectingThen::new);
    }

    public static <T,R> ExpressionBuilder<R,CaseExpression.InFirstWhenExpectingThen> when(Alias<T> alias, FunctionOptional1<T,R> method) {
        return ExpressionBuilder.of(ResolvedColumn.of(alias, method), CaseExpression.InFirstWhenExpectingThen::new);
    }

    public static CaseExpression.InFirstWhenExpectingThen whenever(BooleanExpression expression) {
        return new CaseExpression.InFirstWhenExpectingThen(expression);
    }

    public static <T> ExpressionBuilder<T,CaseExpression.InFirstWhenExpectingThen> whenever(T val) {
        return ExpressionBuilder.of(ValueExpression.of(val), CaseExpression.InFirstWhenExpectingThen::new);
    }

    public static <T> ExpressionBuilder<T,CaseExpression.InFirstWhenExpectingThen> whenever(TypedExpression<T> expression) {
        return ExpressionBuilder.of(expression, CaseExpression.InFirstWhenExpectingThen::new);
    }

    public static <T,R> ExpressionBuilder<R,CaseExpression.InFirstWhenExpectingThen> whenever(Function1<T,R> method) {
        return ExpressionBuilder.of(UnresolvedColumn.of(method), CaseExpression.InFirstWhenExpectingThen::new);
    }

    public static <T,R> ExpressionBuilder<R,CaseExpression.InFirstWhenExpectingThen> whenever(FunctionOptional1<T,R> method) {
        return ExpressionBuilder.of(UnresolvedColumn.of(method), CaseExpression.InFirstWhenExpectingThen::new);
    }

    public static <T,R> ExpressionBuilder<R,CaseExpression.InFirstWhenExpectingThen> whenever(String alias, Function1<T,R> method) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, method), CaseExpression.InFirstWhenExpectingThen::new);
    }

    public static <T,R> ExpressionBuilder<R,CaseExpression.InFirstWhenExpectingThen> whenever(String alias, FunctionOptional1<T,R> method) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, method), CaseExpression.InFirstWhenExpectingThen::new);
    }

    public static <T,R> ExpressionBuilder<R,CaseExpression.InFirstWhenExpectingThen> whenever(Alias<T> alias, Function1<T,R> method) {
        return ExpressionBuilder.of(ResolvedColumn.of(alias, method), CaseExpression.InFirstWhenExpectingThen::new);
    }

    public static <T,R> ExpressionBuilder<R,CaseExpression.InFirstWhenExpectingThen> whenever(Alias<T> alias, FunctionOptional1<T,R> method) {
        return ExpressionBuilder.of(ResolvedColumn.of(alias, method), CaseExpression.InFirstWhenExpectingThen::new);
    }
}
