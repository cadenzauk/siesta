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

package com.cadenzauk.siesta.grammar;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Condition;
import com.cadenzauk.siesta.DataType;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.expression.BooleanExpression;
import com.cadenzauk.siesta.expression.Expression;
import com.cadenzauk.siesta.expression.ResolvedColumn;
import com.cadenzauk.siesta.expression.TypedExpression;
import com.cadenzauk.siesta.expression.UnresolvedColumn;
import com.cadenzauk.siesta.expression.condition.OperatorExpressionCondition;
import com.cadenzauk.siesta.expression.condition.OperatorInCondition;
import com.cadenzauk.siesta.expression.condition.OperatorIsNull;
import com.cadenzauk.siesta.expression.condition.OperatorValueCondition;

import java.util.Optional;
import java.util.function.Function;

public class ExpressionBuilder<T, N> {
    private final Database database;
    private final TypedExpression<T> lhs;
    private final Function<Expression,N> onComplete;
    private Optional<Double> selectivity = Optional.empty();

    private ExpressionBuilder(Database database, TypedExpression<T> lhs, Function<Expression,N> onComplete) {
        this.database = database;
        this.lhs = lhs;
        this.onComplete = onComplete;
    }

    //---
    public N isEqualTo(T value) {
        return complete(new OperatorValueCondition<>(database.getDataTypeOf(value), "=", value, selectivity));
    }

    public N isEqualTo(TypedExpression<T> expression) {
        return complete(new OperatorExpressionCondition<>("=", expression));
    }

    public <R> N isEqualTo(Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("=", UnresolvedColumn.of(getter)));
    }

    public <R> N isEqualTo(FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("=", UnresolvedColumn.of(getter)));
    }

    public <R> N isEqualTo(String alias, Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("=", UnresolvedColumn.of(alias, getter)));
    }

    public <R> N isEqualTo(String alias, FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("=", UnresolvedColumn.of(alias, getter)));
    }

    public <R> N isEqualTo(Alias<R> alias, Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("=", ResolvedColumn.of(alias, getter)));
    }

    public <R> N isEqualTo(Alias<R> alias, FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("=", ResolvedColumn.of(alias, getter)));
    }

    //---
    public N isNotEqualTo(T value) {
        return complete(new OperatorValueCondition<>(database.getDataTypeOf(value), "<>", value, selectivity));
    }

    public N isNotEqualTo(TypedExpression<T> expression) {
        return complete(new OperatorExpressionCondition<>("<>", expression));
    }

    public <R> N isNotEqualTo(Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<>", UnresolvedColumn.of(getter)));
    }

    public <R> N isNotEqualTo(FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<>", UnresolvedColumn.of(getter)));
    }

    public <R> N isNotEqualTo(String alias, Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<>", UnresolvedColumn.of(alias, getter)));
    }

    public <R> N isNotEqualTo(String alias, FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<>", UnresolvedColumn.of(alias, getter)));
    }

    public <R> N isNotEqualTo(Alias<R> alias, Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<>", ResolvedColumn.of(alias, getter)));
    }

    public <R> N isNotEqualTo(Alias<R> alias, FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<>", ResolvedColumn.of(alias, getter)));
    }

    //---
    public N isGreaterThan(T value) {
        return complete(new OperatorValueCondition<>(database.getDataTypeOf(value), ">", value, selectivity));
    }

    public N isGreaterThan(TypedExpression<T> expression) {
        return complete(new OperatorExpressionCondition<>(">", expression));
    }

    public <R> N isGreaterThan(Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">", UnresolvedColumn.of(getter)));
    }

    public <R> N isGreaterThan(FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">", UnresolvedColumn.of(getter)));
    }

    public <R> N isGreaterThan(String alias, Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">", UnresolvedColumn.of(alias, getter)));
    }

    public <R> N isGreaterThan(String alias, FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">", UnresolvedColumn.of(alias, getter)));
    }

    public <R> N isGreaterThan(Alias<R> alias, Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">", ResolvedColumn.of(alias, getter)));
    }

    public <R> N isGreaterThan(Alias<R> alias, FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">", ResolvedColumn.of(alias, getter)));
    }

    //---
    public N isGreaterThanOrEqualTo(T value) {
        return complete(new OperatorValueCondition<>(database.getDataTypeOf(value), ">=", value, selectivity));
    }

    public N isGreaterThanOrEqualTo(TypedExpression<T> expression) {
        return complete(new OperatorExpressionCondition<>(">=", expression));
    }

    public <R> N isGreaterThanOrEqualTo(Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">=", UnresolvedColumn.of(getter)));
    }

    public <R> N isGreaterThanOrEqualTo(FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">=", UnresolvedColumn.of(getter)));
    }

    public <R> N isGreaterThanOrEqualTo(String alias, Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">=", UnresolvedColumn.of(alias, getter)));
    }

    public <R> N isGreaterThanOrEqualTo(String alias, FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">=", UnresolvedColumn.of(alias, getter)));
    }

    public <R> N isGreaterThanOrEqualTo(Alias<R> alias, Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">=", ResolvedColumn.of(alias, getter)));
    }

    public <R> N isGreaterThanOrEqualTo(Alias<R> alias, FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">=", ResolvedColumn.of(alias, getter)));
    }

    //---
    public N isLessThanOrEqualTo(T value) {
        return complete(new OperatorValueCondition<>(database.getDataTypeOf(value), "<=", value, selectivity));
    }

    public N isLessThanOrEqualTo(TypedExpression<T> expression) {
        return complete(new OperatorExpressionCondition<>("<=", expression));
    }

    public <R> N isLessThanOrEqualTo(Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<=", UnresolvedColumn.of(getter)));
    }

    public <R> N isLessThanOrEqualTo(FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<=", UnresolvedColumn.of(getter)));
    }

    public <R> N isLessThanOrEqualTo(String alias, Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<=", UnresolvedColumn.of(alias, getter)));
    }

    public <R> N isLessThanOrEqualTo(String alias, FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<=", UnresolvedColumn.of(alias, getter)));
    }

    public <R> N isLessThanOrEqualTo(Alias<R> alias, Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<=", ResolvedColumn.of(alias, getter)));
    }

    public <R> N isLessThanOrEqualTo(Alias<R> alias, FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<=", ResolvedColumn.of(alias, getter)));
    }

    //---
    @SafeVarargs
    public final N isIn(T... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("At least one value is required for an IN expression.");
        }
        DataType<T> dataType = database.getDataTypeOf(values[0]);
        return complete(new OperatorInCondition<>(dataType, values));
    }


    //---
    public N isNull() {
        return complete(new OperatorIsNull<>());
    }

    //---
    public ExpressionBuilder<T,N> selectivity(double v) {
        selectivity = Optional.of(v);
        return this;
    }

    private N complete(Condition<T> rhs) {
        return onComplete.apply(new BooleanExpression<>(lhs, rhs));
    }

    public static <T, N> ExpressionBuilder<T,N> of(Database database, TypedExpression<T> lhs, Function<Expression,N> onComplete) {
        return new ExpressionBuilder<>(database, lhs, onComplete);
    }
}
