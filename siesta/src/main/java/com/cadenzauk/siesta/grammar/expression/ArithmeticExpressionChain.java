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

import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.Scope;
import com.google.common.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class ArithmeticExpressionChain<T> implements TypedExpression<T> {
    private final TypedExpression<T> first;
    private final List<Term<T>> terms = new ArrayList<>();

    public ArithmeticExpressionChain(TypedExpression<T> first) {
        this.first = ParenthesisedArithmeticExpression.wrapIfNecessary(first);
    }

    @Override
    public String sql(Scope scope) {
        return first.sql(scope) + terms.stream().map(t -> t.sql(scope)).collect(joining());
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return expressions().flatMap(e -> e.args(scope));
    }

    @Override
    public Precedence precedence() {
        return terms.stream()
            .map(t -> t.precedence)
            .reduce(first.precedence(), Precedence::min);
    }

    @Override
    public String label(Scope scope) {
        return first.label(scope);
    }

    @Override
    public RowMapper<T> rowMapper(Scope scope, Optional<String> label) {
        return first.rowMapper(scope, label);
    }

    @Override
    public TypeToken<T> type() {
        return first.type();
    }

    @Override
    public TypedExpression<T> plus(TypedExpression<T> value) {
        terms.add(Term.plus(value));
        return this;
    }

    @Override
    public TypedExpression<T> minus(TypedExpression<T> value) {
        terms.add(Term.minus(value));
        return this;
    }

    @Override
    public TypedExpression<T> times(TypedExpression<T> value) {
        terms.add(Term.times(value));
        return this;
    }

    @Override
    public TypedExpression<T> dividedBy(TypedExpression<T> value) {
        terms.add(Term.dividedBy(value));
        return this;
    }

    private Stream<TypedExpression<T>> expressions() {
        return Stream.concat(
            Stream.of(first),
            terms.stream().map(Term::operand)
        );
    }

    private static class Term<T> {
        private final Precedence precedence;
        private final String operator;
        private final TypedExpression<T> operand;

        public Term(Precedence precedence, String operator, TypedExpression<T> operand) {
            this.precedence = precedence;
            this.operator = operator;
            this.operand = ParenthesisedArithmeticExpression.wrapIfNecessary(operand);
        }

        public TypedExpression<T> operand() {
            return operand;
        }

        public String sql(Scope scope) {
            return " " + operator + " " + operand.sql(scope);
        }

        public static <T> Term<T> plus(TypedExpression<T> expression) {
            return new Term<>(Precedence.PLUS_MINUS, "+", expression);
        }

        public static <T> Term<T> minus(TypedExpression<T> expression) {
            return new Term<>(Precedence.PLUS_MINUS, "-", expression);
        }

        public static <T> Term<T> times(TypedExpression<T> expression) {
            return new Term<>(Precedence.TIMES_DIVIDE, "*", expression);
        }

        public static <T> Term<T> dividedBy(TypedExpression<T> expression) {
            return new Term<>(Precedence.TIMES_DIVIDE, "/", expression);
        }
    }
}
