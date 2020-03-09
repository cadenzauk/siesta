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

package com.cadenzauk.siesta.grammar.dml;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.grammar.expression.Assignment;
import com.cadenzauk.siesta.grammar.expression.NullExpression;
import com.cadenzauk.siesta.grammar.expression.ResolvedColumn;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.grammar.expression.UnresolvedColumn;
import com.cadenzauk.siesta.grammar.expression.ValueExpression;

import java.util.Optional;
import java.util.function.Function;

public class ExpectingTo<T, N> {
    private final UnresolvedColumn<T> lhs;
    private final Function<Assignment<T>,N> onComplete;

    private ExpectingTo(UnresolvedColumn<T> lhs, Function<Assignment<T>,N> onComplete) {
        this.lhs = lhs;
        this.onComplete = onComplete;
    }

    public N to(T value) {
        return to(Optional.ofNullable(value));
    }

    public N to(Optional<T> value) {
        return value
            .map(v -> complete(ValueExpression.of(v)))
            .orElseGet(() -> complete(new NullExpression<>(lhs.type())));
    }

    public N to(TypedExpression<T> expression) {
        return complete(expression);
    }

    public <R> N to(Function1<R,T> expression) {
        return complete(UnresolvedColumn.of(expression));
    }

    public <R> N to(FunctionOptional1<R,T> expression) {
        return complete(UnresolvedColumn.of(expression));
    }

    public <R> N to(String alias, Function1<R,T> expression) {
        return complete(UnresolvedColumn.of(alias, expression));
    }

    public <R> N to(String alias, FunctionOptional1<R,T> expression) {
        return complete(UnresolvedColumn.of(alias, expression));
    }

    public <R> N to(Alias<R> alias, Function1<R,T> expression) {
        return complete(ResolvedColumn.of(alias, expression));
    }

    public <R> N to(Alias<R> alias, FunctionOptional1<R,T> expression) {
        return complete(ResolvedColumn.of(alias, expression));
    }

    public N toNull() {
        return complete(new NullExpression<>(lhs.type()));
    }

    private N complete(TypedExpression<T> rhs) {
        return onComplete.apply(new Assignment<>(lhs, rhs));
    }

    public static <T, N> ExpectingTo<T,N> of(UnresolvedColumn<T> lhs, Function<Assignment<T>,N> onComplete) {
        return new ExpectingTo<>(lhs, onComplete);
    }
}
