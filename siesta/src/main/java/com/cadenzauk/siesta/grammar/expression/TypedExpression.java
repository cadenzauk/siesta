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
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Scope;
import com.google.common.reflect.TypeToken;

import java.util.function.Function;

public interface TypedExpression<T> extends Expression {
    String label(Scope scope);

    RowMapper<T> rowMapper(Scope scope, String label);

    TypeToken<T> type();

    static <T> ExpressionBuilder<T,BooleanExpression> column(TypedExpression<T> lhs) {
        return ExpressionBuilder.of(lhs, Function.identity());
    }

    static <T, R> ExpressionBuilder<T,BooleanExpression> column(Function1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(lhs), Function.identity());
    }

    static <T, R> ExpressionBuilder<T,BooleanExpression> column(FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(lhs), Function.identity());
    }

    static <T, R> ExpressionBuilder<T,BooleanExpression> column(String alias, Function1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), Function.identity());
    }

    static <T, R> ExpressionBuilder<T,BooleanExpression> column(String alias, FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(UnresolvedColumn.of(alias, lhs), Function.identity());
    }

    static <T, R> ExpressionBuilder<T,BooleanExpression> column(Alias<R> alias, Function1<R,T> lhs) {
        return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), Function.identity());
    }

    static <T, R> ExpressionBuilder<T,BooleanExpression> column(Alias<R> alias, FunctionOptional1<R,T> lhs) {
        return ExpressionBuilder.of(ResolvedColumn.of(alias, lhs), Function.identity());
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
