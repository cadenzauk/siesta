/*
 * Copyright (c) 2018 Cadenza United Kingdom Limited
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

import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.grammar.select.Select;

import java.util.stream.Stream;

public class ExistsExpression extends BooleanExpression {
    private final boolean negated;
    private final Select<?> inner;

    private ExistsExpression(boolean negated, Select<?> inner) {
        this.negated = negated;
        this.inner = inner;
    }

    @Override
    public String sql(Scope scope) {
        return String.format("%sexists %s", negated ? "not " : "", inner.sql(scope));
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return inner.args(scope);
    }

    @Override
    public Precedence precedence() {
        return Precedence.UNARY;
    }

    public static ExistsExpression exists(Select<?> inner) {
        return new ExistsExpression(false, inner);
    }

    public static ExistsExpression notExists(Select<?> inner) {
        return new ExistsExpression(true, inner);
    }
}
