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

import com.cadenzauk.siesta.Scope;

import java.util.stream.Stream;

public class Assignment<T> {
    private final UnresolvedColumn<T> lhs;
    private TypedExpression<T> rhs;

    public Assignment(UnresolvedColumn<T> lhs, TypedExpression<T> rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public String sql(Scope scope) {
        return lhs.columnName(scope) + " = " + rhs.sql(scope);
    }

    public Stream<Object> args(Scope scope) {
        return Stream.concat(lhs.args(scope), rhs.args(scope));
    }

    public void plus(TypedExpression<T> value) {
        rhs = rhs.plus(value);
    }

    public void minus(TypedExpression<T> value) {
        rhs = rhs.minus(value);
    }

    public void times(TypedExpression<T> value) {
        rhs = rhs.times(value);
    }

    public void dividedBy(TypedExpression<T> value) {
        rhs = rhs.dividedBy(value);
    }
}
