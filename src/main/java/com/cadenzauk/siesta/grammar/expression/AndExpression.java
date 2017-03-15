/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/and sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies and substantial portions of the Software.
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

import com.cadenzauk.siesta.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class AndExpression extends BooleanExpression {
    private final List<BooleanExpression> expressions = new ArrayList<>();

    public AndExpression(BooleanExpression lhs, BooleanExpression rhs) {
        expressions.add(lhs);
        expressions.add(rhs);
    }

    @Override
    public String sql(Scope scope) {
        return expressions.stream()
                .map(e -> sql(e, scope))
                .collect(joining(" and "));
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return expressions.stream()
            .flatMap(e -> e.args(scope));
    }

    @Override
    public Precedence precedence() {
        return Precedence.AND;
    }

    @Override
    public BooleanExpression appendOr(BooleanExpression expression) {
        return new OrExpression(this, expression);
    }

    @Override
    public BooleanExpression appendAnd(BooleanExpression expression) {
        expressions.add(expression);
        return this;
    }
}
