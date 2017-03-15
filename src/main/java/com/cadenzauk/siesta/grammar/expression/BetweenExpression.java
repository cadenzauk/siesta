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

import com.cadenzauk.siesta.DataType;
import com.cadenzauk.siesta.Scope;

import java.util.stream.Stream;

public class BetweenExpression<T> extends BooleanExpression {
    private final TypedExpression<T> lhs;
    private final T lowValue;
    private final T highValue;
    private final String prefix;

    public BetweenExpression(TypedExpression<T> lhs, T lowValue, T highValue, String prefix) {
        this.lhs = lhs;
        this.lowValue = lowValue;
        this.highValue = highValue;
        this.prefix = prefix;
    }

    @Override
    public String sql(Scope scope) {
        return lhs.sql(scope) + " " + prefix + "between ? and ?";
    }

    @Override
    public Stream<Object> args(Scope scope) {
        DataType<T> dataType = scope.database().getDataTypeOf(lowValue);
        return Stream.concat(
            lhs.args(scope),
            Stream.of(
                dataType.toDatabase(lowValue),
                dataType.toDatabase(highValue)));
    }

    @Override
    public Precedence precedence() {
        return Precedence.BETWEEN;
    }

    @Override
    public BooleanExpression appendOr(BooleanExpression expression) {
        return new OrExpression(this, expression);
    }

    @Override
    public BooleanExpression appendAnd(BooleanExpression expression) {
        return new AndExpression(this, expression);
    }
}
