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

import java.util.function.Function;

public class BetweenBuilder<T,N> {
    private final TypedExpression<T> lhs;
    private final TypedExpression<T> lowValue;
    private final String prefix;
    private final Function<BooleanExpression,N> onComplete;

    public BetweenBuilder(TypedExpression<T> lhs, TypedExpression<T> lowValue, String prefix, Function<BooleanExpression,N> onComplete) {
        this.lhs = lhs;
        this.lowValue = lowValue;
        this.prefix = prefix;
        this.onComplete = onComplete;
    }

    public N and(T highValue) {
        return onComplete.apply(new BetweenExpression<>(lhs, lowValue, ValueExpression.of(highValue), prefix));
    }

    public N and(TypedExpression<T> expression) {
        return onComplete.apply(new BetweenExpression<>(lhs, lowValue, expression, prefix));
    }

    public <R> N and(Function1<R,T> getter) {
        return onComplete.apply(new BetweenExpression<>(lhs, lowValue, UnresolvedColumn.of(getter), prefix));
    }

    public <R> N and(FunctionOptional1<R,T> getter) {
        return onComplete.apply(new BetweenExpression<>(lhs, lowValue, UnresolvedColumn.of(getter), prefix));
    }

    public <R> N and(String alias, Function1<R,T> getter) {
        return onComplete.apply(new BetweenExpression<>(lhs, lowValue, UnresolvedColumn.of(alias, getter), prefix));
    }

    public <R> N and(String alias, FunctionOptional1<R,T> getter) {
        return onComplete.apply(new BetweenExpression<>(lhs, lowValue, UnresolvedColumn.of(alias, getter), prefix));
    }

    public <R> N and(Alias<R> alias, Function1<R,T> getter) {
        return onComplete.apply(new BetweenExpression<>(lhs, lowValue, ResolvedColumn.of(alias, getter), prefix));
    }

    public <R> N and(Alias<R> alias, FunctionOptional1<R,T> getter) {
        return onComplete.apply(new BetweenExpression<>(lhs, lowValue, ResolvedColumn.of(alias, getter), prefix));
    }
}
