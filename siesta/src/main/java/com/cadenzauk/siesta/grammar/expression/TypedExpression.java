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
import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.ProjectionColumn;
import com.cadenzauk.siesta.Scope;
import com.google.common.reflect.TypeToken;

import java.util.Optional;
import java.util.function.Function;

public interface TypedExpression<T> extends Expression {
    String label(Scope scope);

    RowMapperFactory<T> rowMapperFactory(Scope scope);

    default ProjectionColumn<T> toProjectionColumn(Scope scope, Optional<String> label) {
        return new ProjectionColumn<>(type(), sql(scope), sql(scope), sql(scope), label.orElseGet(() -> label(scope)), rowMapperFactory(scope, label));
    }

    default RowMapperFactory<T> rowMapperFactory(Scope scope, Optional<String> defaultLabel) {
        return rowMapperFactory(scope).withDefaultLabel(defaultLabel);
    }

    TypeToken<T> type();

    default String sqlWithLabel(Scope scope, Optional<String> label) {
        return sql(scope) + " as " + label.orElseGet(() -> label(scope));
    }

    default TypedExpression<T> plus(T value) {
        return plus(value(value));
    }

    default TypedExpression<T> plus(TypedExpression<T> value) {
        return new ArithmeticExpressionChain<>(this).plus(value);
    }

    default TypedExpression<T> plus(Label<T> value) {
        return plus(UnresolvedColumn.of(value));
    }

    default TypedExpression<T> plus(String alias, Label<T> value) {
        return plus(UnresolvedColumn.of(alias, value));
    }

    default <R> TypedExpression<T> plus(Function1<R,T> value) {
        return plus(UnresolvedColumn.of(value));
    }

    default <R> TypedExpression<T> plus(FunctionOptional1<R,T> value) {
        return plus(UnresolvedColumn.of(value));
    }

    default <R> TypedExpression<T> plus(String alias, Function1<R,T> value) {
        return plus(UnresolvedColumn.of(alias, value));
    }

    default <R> TypedExpression<T> plus(String alias, FunctionOptional1<R,T> value) {
        return plus(UnresolvedColumn.of(alias, value));
    }

    default <R> TypedExpression<T> plus(Alias<R> alias, Function1<R,T> value) {
        return plus(ResolvedColumn.of(alias, value));
    }

    default <R> TypedExpression<T> plus(Alias<R> alias, FunctionOptional1<R,T> value) {
        return plus(ResolvedColumn.of(alias, value));
    }

    default TypedExpression<T> minus(T value) {
        return minus(value(value));
    }

    default TypedExpression<T> minus(TypedExpression<T> value) {
        return new ArithmeticExpressionChain<>(this).minus(value);
    }

    default TypedExpression<T> minus(Label<T> value) {
        return minus(UnresolvedColumn.of(value));
    }

    default TypedExpression<T> minus(String alias, Label<T> value) {
        return minus(UnresolvedColumn.of(alias, value));
    }

    default <R> TypedExpression<T> minus(Function1<R,T> value) {
        return minus(UnresolvedColumn.of(value));
    }

    default <R> TypedExpression<T> minus(FunctionOptional1<R,T> value) {
        return minus(UnresolvedColumn.of(value));
    }

    default <R> TypedExpression<T> minus(String alias, Function1<R,T> value) {
        return minus(UnresolvedColumn.of(alias, value));
    }

    default <R> TypedExpression<T> minus(String alias, FunctionOptional1<R,T> value) {
        return minus(UnresolvedColumn.of(alias, value));
    }

    default <R> TypedExpression<T> minus(Alias<R> alias, Function1<R,T> value) {
        return minus(ResolvedColumn.of(alias, value));
    }

    default <R> TypedExpression<T> minus(Alias<R> alias, FunctionOptional1<R,T> value) {
        return minus(ResolvedColumn.of(alias, value));
    }

    default TypedExpression<T> times(T value) {
        return times(value(value));
    }

    default ArithmeticExpressionChain<T> times(TypedExpression<T> value) {
        return new ArithmeticExpressionChain<>(this).times(value);
    }

    default TypedExpression<T> times(Label<T> value) {
        return times(UnresolvedColumn.of(value));
    }

    default TypedExpression<T> times(String alias, Label<T> value) {
        return times(UnresolvedColumn.of(alias, value));
    }

    default <R> TypedExpression<T> times(Function1<R,T> value) {
        return times(UnresolvedColumn.of(value));
    }

    default <R> TypedExpression<T> times(FunctionOptional1<R,T> value) {
        return times(UnresolvedColumn.of(value));
    }

    default <R> TypedExpression<T> times(String alias, Function1<R,T> value) {
        return times(UnresolvedColumn.of(alias, value));
    }

    default <R> TypedExpression<T> times(String alias, FunctionOptional1<R,T> value) {
        return times(UnresolvedColumn.of(alias, value));
    }

    default <R> TypedExpression<T> times(Alias<R> alias, Function1<R,T> value) {
        return times(ResolvedColumn.of(alias, value));
    }

    default <R> TypedExpression<T> times(Alias<R> alias, FunctionOptional1<R,T> value) {
        return times(ResolvedColumn.of(alias, value));
    }

    default TypedExpression<T> dividedBy(T value) {
        return dividedBy(value(value));
    }

    default TypedExpression<T> dividedBy(TypedExpression<T> value) {
        return new ArithmeticExpressionChain<>(this).dividedBy(value);
    }

    default TypedExpression<T> dividedBy(Label<T> value) {
        return dividedBy(UnresolvedColumn.of(value));
    }

    default TypedExpression<T> dividedBy(String alias, Label<T> value) {
        return dividedBy(UnresolvedColumn.of(alias, value));
    }

    default <R> TypedExpression<T> dividedBy(Function1<R,T> value) {
        return dividedBy(UnresolvedColumn.of(value));
    }

    default <R> TypedExpression<T> dividedBy(FunctionOptional1<R,T> value) {
        return dividedBy(UnresolvedColumn.of(value));
    }

    default <R> TypedExpression<T> dividedBy(String alias, Function1<R,T> value) {
        return dividedBy(UnresolvedColumn.of(alias, value));
    }

    default <R> TypedExpression<T> dividedBy(String alias, FunctionOptional1<R,T> value) {
        return dividedBy(UnresolvedColumn.of(alias, value));
    }

    default <R> TypedExpression<T> dividedBy(Alias<R> alias, Function1<R,T> value) {
        return dividedBy(ResolvedColumn.of(alias, value));
    }

    default <R> TypedExpression<T> dividedBy(Alias<R> alias, FunctionOptional1<R,T> value) {
        return dividedBy(ResolvedColumn.of(alias, value));
    }

    static <T, R> ColumnExpressionBuilder<T,R,BooleanExpression> column(Function1<R,T> lhs) {
        return ColumnExpressionBuilder.of(UnresolvedColumn.of(lhs), Function.identity());
    }

    static <T, R> ColumnExpressionBuilder<T,R,BooleanExpression> column(FunctionOptional1<R,T> lhs) {
        return ColumnExpressionBuilder.of(UnresolvedColumn.of(lhs), Function.identity());
    }

    static <T, R> ColumnExpressionBuilder<T,R,BooleanExpression> column(String alias, Function1<R,T> lhs) {
        return ColumnExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), Function.identity());
    }

    static <T, R> ColumnExpressionBuilder<T,R,BooleanExpression> column(String alias, FunctionOptional1<R,T> lhs) {
        return ColumnExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), Function.identity());
    }

    static <T, R> ExpressionBuilder<T,BooleanExpression> column(Alias<R> alias, Function1<R,T> lhs) {
        return ColumnExpressionBuilder.of(ResolvedColumn.of(alias, lhs), Function.identity());
    }

    static <T, R> ExpressionBuilder<T,BooleanExpression> column(Alias<R> alias, FunctionOptional1<R,T> lhs) {
        return ColumnExpressionBuilder.of(ResolvedColumn.of(alias, lhs), Function.identity());
    }

    static <T> ExpressionBuilder<T,BooleanExpression> column(Label<T> lhs) {
        return ColumnExpressionBuilder.of(UnresolvedColumn.of(lhs), Function.identity());
    }

    static <T> ExpressionBuilder<T,BooleanExpression> column(String alias, Label<T> lhs) {
        return ColumnExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), Function.identity());
    }

    static <T> ExpressionBuilder<T,BooleanExpression> value(T value) {
        return ExpressionBuilder.of(ValueExpression.of(value), Function.identity());
    }

    static <T> ExpressionBuilder<T,BooleanExpression> literal(T value) {
        return ExpressionBuilder.of(LiteralExpression.of(value), Function.identity());
    }

    static <T> CastBuilder<T> cast(T value) {
        return new CastBuilder<>(ValueExpression.of(value));
    }

    static <T> CastBuilder<T> cast(TypedExpression<T> value) {
        return new CastBuilder<>(value);
    }
}
