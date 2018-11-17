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

import com.cadenzauk.core.stream.StreamUtil;
import com.cadenzauk.siesta.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class BooleanExpressionChain extends BooleanExpression {
    private Optional<BooleanExpression> first;
    private final List<Term> terms = new ArrayList<>();

    public BooleanExpressionChain() {
        this.first = Optional.empty();
    }

    public BooleanExpressionChain(BooleanExpression first) {
        this.first = Optional.of(ParenthesisedBooleanExpression.wrapIfNecessary(first));
    }

    public boolean isEmpty() {
        return !first.isPresent();
    }

    public void start(BooleanExpression expression) {
        first.ifPresent(booleanExpression -> {
            throw new IllegalStateException("Expression chain has already been started");
        });
        first = Optional.of(ParenthesisedBooleanExpression.wrapIfNecessary(expression));
    }

    @Override
    public String sql(Scope scope) {
        return sql(scope, "");
    }

    public String sql(Scope scope, String prefix) {
        return first
            .map(f -> prefix + f.sql(scope) + terms.stream().map(t -> t.sql(scope)).collect(joining()))
            .orElse("");
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return expressions().flatMap(e -> e.args(scope));
    }

    @Override
    public Precedence precedence() {
        return terms.stream()
            .map(t -> t.precedence)
            .reduce(first.map(Expression::precedence).orElse(Precedence.UNARY), Precedence::min);
    }

    @Override
    public BooleanExpression appendOr(BooleanExpression expression) {
        if (!first.isPresent()) {
            throw new IllegalStateException("Expression chain has not been started");
        }
        terms.add(Term.or(ParenthesisedBooleanExpression.wrapIfNecessary(expression)));
        return this;
    }

    @Override
    public BooleanExpression appendAnd(BooleanExpression expression) {
        if (!first.isPresent()) {
            throw new IllegalStateException("Expression chain has not been started");
        }
        terms.add(Term.and(ParenthesisedBooleanExpression.wrapIfNecessary(expression)));
        return this;
    }

    private Stream<BooleanExpression> expressions() {
        return Stream.concat(
            StreamUtil.of(first),
            terms.stream().map(Term::operand)
        );
    }

    private static class Term {
        private final Precedence precedence;
        private final String operator;
        private final BooleanExpression operand;

        private Term(Precedence precedence, String operator, BooleanExpression operand) {
            this.precedence = precedence;
            this.operator = operator;
            this.operand = operand;
        }

        private BooleanExpression operand() {
            return operand;
        }

        private String sql(Scope scope) {
            return " " + operator + " " + operand.sql(scope);
        }

        private static Term or(BooleanExpression expression) {
            return new Term(Precedence.OR, "or", expression);
        }

        private static Term and(BooleanExpression expression) {
            return new Term(Precedence.AND, "and", expression);
        }
    }
}
