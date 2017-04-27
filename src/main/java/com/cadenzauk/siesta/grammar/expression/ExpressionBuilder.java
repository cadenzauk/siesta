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
import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Condition;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.grammar.expression.condition.InCondition;
import com.cadenzauk.siesta.grammar.expression.condition.IsNullCondition;
import com.cadenzauk.siesta.grammar.expression.condition.LikeCondition;
import com.cadenzauk.siesta.grammar.expression.condition.OperatorExpressionCondition;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class ExpressionBuilder<T, N> implements TypedExpression<T> {
    private final TypedExpression<T> lhs;
    private final Function<BooleanExpression,N> onComplete;
    private Optional<Double> selectivity = Optional.empty();

    private ExpressionBuilder(TypedExpression<T> lhs, Function<BooleanExpression,N> onComplete) {
        this.lhs = lhs;
        this.onComplete = onComplete;
    }

    @Override
    public String sql(Scope scope) {
        return lhs.sql(scope);
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return lhs.args(scope);
    }

    @Override
    public Precedence precedence() {
        return lhs.precedence();
    }

    @Override
    public String label(Scope scope) {
        return lhs.label(scope);
    }

    @Override
    public RowMapper<T> rowMapper(Scope scope, String label) {
        return lhs.rowMapper(scope, label);
    }

    //---
    public N isEqualTo(T value) {
        return complete(new OperatorExpressionCondition<>("=", ValueExpression.of(value), selectivity));
    }

    public N isEqualTo(TypedExpression<T> expression) {
        return complete(new OperatorExpressionCondition<>("=", expression, selectivity));
    }

    public <R> N isEqualTo(Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("=", UnresolvedColumn.of(getter), selectivity));
    }

    public <R> N isEqualTo(FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("=", UnresolvedColumn.of(getter), selectivity));
    }

    public <R> N isEqualTo(String alias, Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("=", UnresolvedColumn.of(alias, getter), selectivity));
    }

    public <R> N isEqualTo(String alias, FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("=", UnresolvedColumn.of(alias, getter), selectivity));
    }

    public <R> N isEqualTo(Alias<R> alias, Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("=", ResolvedColumn.of(alias, getter), selectivity));
    }

    public <R> N isEqualTo(Alias<R> alias, FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("=", ResolvedColumn.of(alias, getter), selectivity));
    }

    //---
    public N isNotEqualTo(T value) {
        return complete(new OperatorExpressionCondition<>("<>", ValueExpression.of(value), selectivity));
    }

    public N isNotEqualTo(TypedExpression<T> expression) {
        return complete(new OperatorExpressionCondition<>("<>", expression, selectivity));
    }

    public <R> N isNotEqualTo(Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<>", UnresolvedColumn.of(getter), selectivity));
    }

    public <R> N isNotEqualTo(FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<>", UnresolvedColumn.of(getter), selectivity));
    }

    public <R> N isNotEqualTo(String alias, Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<>", UnresolvedColumn.of(alias, getter), selectivity));
    }

    public <R> N isNotEqualTo(String alias, FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<>", UnresolvedColumn.of(alias, getter), selectivity));
    }

    public <R> N isNotEqualTo(Alias<R> alias, Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<>", ResolvedColumn.of(alias, getter), selectivity));
    }

    public <R> N isNotEqualTo(Alias<R> alias, FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<>", ResolvedColumn.of(alias, getter), selectivity));
    }

    //---
    public N isGreaterThan(T value) {
        return complete(new OperatorExpressionCondition<>(">", ValueExpression.of(value), selectivity));
    }

    public N isGreaterThan(TypedExpression<T> expression) {
        return complete(new OperatorExpressionCondition<>(">", expression, selectivity));
    }

    public <R> N isGreaterThan(Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">", UnresolvedColumn.of(getter), selectivity));
    }

    public <R> N isGreaterThan(FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">", UnresolvedColumn.of(getter), selectivity));
    }

    public <R> N isGreaterThan(String alias, Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">", UnresolvedColumn.of(alias, getter), selectivity));
    }

    public <R> N isGreaterThan(String alias, FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">", UnresolvedColumn.of(alias, getter), selectivity));
    }

    public <R> N isGreaterThan(Alias<R> alias, Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">", ResolvedColumn.of(alias, getter), selectivity));
    }

    public <R> N isGreaterThan(Alias<R> alias, FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">", ResolvedColumn.of(alias, getter), selectivity));
    }

   //---
    public N isLessThan(T value) {
        return complete(new OperatorExpressionCondition<>("<", ValueExpression.of(value), selectivity));
    }

    public N isLessThan(TypedExpression<T> expression) {
        return complete(new OperatorExpressionCondition<>("<", expression, selectivity));
    }

    public <R> N isLessThan(Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<", UnresolvedColumn.of(getter), selectivity));
    }

    public <R> N isLessThan(FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<", UnresolvedColumn.of(getter), selectivity));
    }

    public <R> N isLessThan(String alias, Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<", UnresolvedColumn.of(alias, getter), selectivity));
    }

    public <R> N isLessThan(String alias, FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<", UnresolvedColumn.of(alias, getter), selectivity));
    }

    public <R> N isLessThan(Alias<R> alias, Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<", ResolvedColumn.of(alias, getter), selectivity));
    }

    public <R> N isLessThan(Alias<R> alias, FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<", ResolvedColumn.of(alias, getter), selectivity));
    }

    //---
    public N isGreaterThanOrEqualTo(T value) {
        return complete(new OperatorExpressionCondition<>(">=", ValueExpression.of(value), selectivity));
    }

    public N isGreaterThanOrEqualTo(TypedExpression<T> expression) {
        return complete(new OperatorExpressionCondition<>(">=", expression, selectivity));
    }

    public <R> N isGreaterThanOrEqualTo(Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">=", UnresolvedColumn.of(getter), selectivity));
    }

    public <R> N isGreaterThanOrEqualTo(FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">=", UnresolvedColumn.of(getter), selectivity));
    }

    public <R> N isGreaterThanOrEqualTo(String alias, Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">=", UnresolvedColumn.of(alias, getter), selectivity));
    }

    public <R> N isGreaterThanOrEqualTo(String alias, FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">=", UnresolvedColumn.of(alias, getter), selectivity));
    }

    public <R> N isGreaterThanOrEqualTo(Alias<R> alias, Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">=", ResolvedColumn.of(alias, getter), selectivity));
    }

    public <R> N isGreaterThanOrEqualTo(Alias<R> alias, FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>(">=", ResolvedColumn.of(alias, getter), selectivity));
    }

    //---
    public N isLessThanOrEqualTo(T value) {
        return complete(new OperatorExpressionCondition<>("<=", ValueExpression.of(value), selectivity));
    }

    public N isLessThanOrEqualTo(TypedExpression<T> expression) {
        return complete(new OperatorExpressionCondition<>("<=", expression, selectivity));
    }

    public <R> N isLessThanOrEqualTo(Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<=", UnresolvedColumn.of(getter), selectivity));
    }

    public <R> N isLessThanOrEqualTo(FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<=", UnresolvedColumn.of(getter), selectivity));
    }

    public <R> N isLessThanOrEqualTo(String alias, Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<=", UnresolvedColumn.of(alias, getter), selectivity));
    }

    public <R> N isLessThanOrEqualTo(String alias, FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<=", UnresolvedColumn.of(alias, getter), selectivity));
    }

    public <R> N isLessThanOrEqualTo(Alias<R> alias, Function1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<=", ResolvedColumn.of(alias, getter), selectivity));
    }

    public <R> N isLessThanOrEqualTo(Alias<R> alias, FunctionOptional1<R,T> getter) {
        return complete(new OperatorExpressionCondition<>("<=", ResolvedColumn.of(alias, getter), selectivity));
    }

    //--- IS [NOT] IN
    @SafeVarargs
    public final N isIn(T... values) {
        return isOpIn("in", values);
    }

    @SafeVarargs
    public final N isNotIn(T... values) {
        return isOpIn("not in", values);
    }

    private N isOpIn(String operator, T[] values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("At least one value is required for an IN expression.");
        }
        return complete(new InCondition<>(operator, values));
    }

    //--- IS [NOT] NULL
    public N isNull() {
        return complete(new IsNullCondition<>(""));
    }

    public N isNotNull() {
        return complete(new IsNullCondition<>("not "));
    }

    //--- [NOT] LIKE
    public N isLike(T value) {
        return complete(new LikeCondition<>("like", value, Optional.empty()));
    }

    public N isLike(T value, String escape) {
        return complete(new LikeCondition<>("like", value, OptionalUtil.ofBlankable(escape)));
    }

    public N isNotLike(T value) {
        return complete(new LikeCondition<>("not like", value, Optional.empty()));
    }

    public N isNotLike(T value, String escape) {
        return complete(new LikeCondition<>("not like", value, OptionalUtil.ofBlankable(escape)));
    }

    //--- BETWEEN
    public BetweenBuilder<T,N> isBetween(T value) {
        return new BetweenBuilder<>(lhs, ValueExpression.of(value), "", onComplete);
    }

    public BetweenBuilder<T,N> isBetween(TypedExpression<T> expression) {
        return new BetweenBuilder<>(lhs, expression, "", onComplete);
    }

    public <R> BetweenBuilder<T,N> isBetween(Function1<R,T> getter) {
        return new BetweenBuilder<>(lhs, UnresolvedColumn.of(getter), "", onComplete);
    }

    public <R> BetweenBuilder<T,N> isBetween(FunctionOptional1<R,T> getter) {
        return new BetweenBuilder<>(lhs, UnresolvedColumn.of(getter), "", onComplete);
    }

    public <R> BetweenBuilder<T,N> isBetween(String alias, Function1<R,T> getter) {
        return new BetweenBuilder<>(lhs, UnresolvedColumn.of(alias, getter), "", onComplete);
    }

    public <R> BetweenBuilder<T,N> isBetween(String alias, FunctionOptional1<R,T> getter) {
        return new BetweenBuilder<>(lhs, UnresolvedColumn.of(alias, getter), "", onComplete);
    }

    public <R> BetweenBuilder<T,N> isBetween(Alias<R> alias, Function1<R,T> getter) {
        return new BetweenBuilder<>(lhs, ResolvedColumn.of(alias, getter), "", onComplete);
    }

    public <R> BetweenBuilder<T,N> isBetween(Alias<R> alias, FunctionOptional1<R,T> getter) {
        return new BetweenBuilder<>(lhs, ResolvedColumn.of(alias, getter), "", onComplete);
    }

    public BetweenBuilder<T,N> notBetween(T value) {
        return new BetweenBuilder<>(lhs, ValueExpression.of(value), "not ", onComplete);
    }

    public BetweenBuilder<T,N> notBetween(TypedExpression<T> expression) {
        return new BetweenBuilder<>(lhs, expression, "not ", onComplete);
    }

    public <R> BetweenBuilder<T,N> notBetween(Function1<R,T> getter) {
        return new BetweenBuilder<>(lhs, UnresolvedColumn.of(getter), "not ", onComplete);
    }

    public <R> BetweenBuilder<T,N> notBetween(FunctionOptional1<R,T> getter) {
        return new BetweenBuilder<>(lhs, UnresolvedColumn.of(getter), "not ", onComplete);
    }

    public <R> BetweenBuilder<T,N> notBetween(String alias, Function1<R,T> getter) {
        return new BetweenBuilder<>(lhs, UnresolvedColumn.of(alias, getter), "not ", onComplete);
    }

    public <R> BetweenBuilder<T,N> notBetween(String alias, FunctionOptional1<R,T> getter) {
        return new BetweenBuilder<>(lhs, UnresolvedColumn.of(alias, getter), "not ", onComplete);
    }

    public <R> BetweenBuilder<T,N> notBetween(Alias<R> alias, Function1<R,T> getter) {
        return new BetweenBuilder<>(lhs, ResolvedColumn.of(alias, getter), "not ", onComplete);
    }

    public <R> BetweenBuilder<T,N> notBetween(Alias<R> alias, FunctionOptional1<R,T> getter) {
        return new BetweenBuilder<>(lhs, ResolvedColumn.of(alias, getter), "not ", onComplete);
    }

    //---

    public <U> ExpressionBuilder<String,N> concat(U arg) {
        return new ExpressionBuilder<>(new ConcatOperator<>(lhs, ValueExpression.of(arg)), onComplete);
    }

    public <U> ExpressionBuilder<String,N> concat(TypedExpression<U> arg) {
        return new ExpressionBuilder<>(new ConcatOperator<>(lhs, arg), onComplete);
    }

    public <R, U> ExpressionBuilder<String,N> concat(Function1<R,U> getter) {
        return new ExpressionBuilder<>(new ConcatOperator<>(lhs, UnresolvedColumn.of(getter)), onComplete);
    }

    public <R, U> ExpressionBuilder<String,N> concat(FunctionOptional1<R,U> getter) {
        return new ExpressionBuilder<>(new ConcatOperator<>(lhs, UnresolvedColumn.of(getter)), onComplete);
    }

    public <R, U> ExpressionBuilder<String,N> concat(String alias, Function1<R,U> getter) {
        return new ExpressionBuilder<>(new ConcatOperator<>(lhs, UnresolvedColumn.of(alias, getter)), onComplete);
    }

    public <R, U> ExpressionBuilder<String,N> concat(String alias, FunctionOptional1<R,U> getter) {
        return new ExpressionBuilder<>(new ConcatOperator<>(lhs, UnresolvedColumn.of(alias, getter)), onComplete);
    }

    public <R, U> ExpressionBuilder<String,N> concat(Alias<R> alias, Function1<R,U> getter) {
        return new ExpressionBuilder<>(new ConcatOperator<>(lhs, ResolvedColumn.of(alias, getter)), onComplete);
    }

    public <R, U> ExpressionBuilder<String,N> concat(Alias<R> alias, FunctionOptional1<R,U> getter) {
        return new ExpressionBuilder<>(new ConcatOperator<>(lhs, ResolvedColumn.of(alias, getter)), onComplete);
    }

    //---

    public ExpressionBuilder<T,N> selectivity(double v) {
        selectivity = Optional.of(v);
        return this;
    }

    private N complete(Condition<T> rhs) {
        return onComplete.apply(new FullExpression<>(lhs, rhs));
    }

    public static <T, N> ExpressionBuilder<T,N> of(TypedExpression<T> lhs, Function<BooleanExpression,N> onComplete) {
        return new ExpressionBuilder<>(lhs, onComplete);
    }
}
