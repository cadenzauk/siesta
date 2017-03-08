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

package com.cadenzauk.siesta.grammar.update;

import com.cadenzauk.siesta.Condition;
import com.cadenzauk.siesta.expression.BooleanExpression;
import com.cadenzauk.siesta.expression.Expression;
import com.cadenzauk.siesta.expression.TypedExpression;
import com.cadenzauk.siesta.expression.condition.OperatorValueCondition;

import java.util.Optional;
import java.util.function.Function;

public class SetExpressionBuilder<T,N> {
    private final TypedExpression<T> lhs;
    private final Function<Expression,N> onComplete;

    public SetExpressionBuilder(TypedExpression<T> lhs, Function<Expression,N> onComplete) {
        this.lhs = lhs;
        this.onComplete = onComplete;
    }

    public N to(T value) {
        return complete(new OperatorValueCondition<>("=", value, Optional.empty()));
    }

    public N to(Optional<T> value) {
        return complete(new OperatorValueCondition<>("=", value.orElse(null), Optional.empty()));
    }

    private N complete(Condition<T> rhs) {
        return onComplete.apply(new BooleanExpression<>(lhs, rhs));
    }

    public static <T,N> SetExpressionBuilder<T,N> of(TypedExpression<T> lhs, Function<Expression,N> onComplete) {
        return new SetExpressionBuilder<>(lhs, onComplete);
    }
}
