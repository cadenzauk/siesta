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
import com.cadenzauk.siesta.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class OrExpression implements BooleanExpression {
    private final List<BooleanExpression> expressions = new ArrayList<>();

    private OrExpression(BooleanExpression expression) {
        expressions.add(expression);
    }

    @Override
    public String sql(Scope scope) {
        return expressions.stream()
            .map(e -> sql(e, scope))
            .collect(joining(" or "));
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return expressions.stream()
            .flatMap(e -> e.args(scope));
    }

    @Override
    public int precedence() {
        return 40;
    }

    public OrExpression or(BooleanExpression lhs) {
        return this.append(lhs);
    }

    public <T> ExpressionBuilder<T,OrExpression> or(TypedExpression<T> lhs) {
        return ExpressionBuilder.of(lhs, this::append);
    }

    public <T, R> ExpressionBuilder<T,OrExpression> or(Function1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::append);
    }

    public <T, R> ExpressionBuilder<T,OrExpression> or(FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(lhs), this::append);
    }

    public <T, R> ExpressionBuilder<T,OrExpression> or(String alias, Function1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::append);
    }

    public <T, R> ExpressionBuilder<T,OrExpression> or(String alias, FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), this::append);
    }

    public <T, R> ExpressionBuilder<T,OrExpression> or(Alias<R> alias, Function1<R,T> lhs) {
        return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), this::append);
    }

    public <T, R> ExpressionBuilder<T,OrExpression> or(Alias<R> alias, FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), this::append);
    }

    private OrExpression append(BooleanExpression expression) {
        expressions.add(expression);
        return this;
    }

    public static OrExpression either(BooleanExpression lhs) {
        return new OrExpression(lhs);
    }

    public static <T> ExpressionBuilder<T,OrExpression> either(TypedExpression<T> lhs) {
        return ExpressionBuilder.of(lhs, OrExpression::new);
    }

    public static <T, R> ExpressionBuilder<T,OrExpression> either(Function1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(lhs), OrExpression::new);
    }

    public static <T, R> ExpressionBuilder<T,OrExpression> either(FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(lhs), OrExpression::new);
    }

    public static <T, R> ExpressionBuilder<T,OrExpression> either(String alias, Function1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), OrExpression::new);
    }

    public static <T, R> ExpressionBuilder<T,OrExpression> either(String alias, FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), OrExpression::new);
    }

    public static <T, R> ExpressionBuilder<T,OrExpression> either(Alias<R> alias, Function1<R,T> lhs) {
        return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), OrExpression::new);
    }

    public static <T, R> ExpressionBuilder<T,OrExpression> either(Alias<R> alias, FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), OrExpression::new);
    }
}
