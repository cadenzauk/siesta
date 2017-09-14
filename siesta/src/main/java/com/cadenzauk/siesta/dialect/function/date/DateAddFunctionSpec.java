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

package com.cadenzauk.siesta.dialect.function.date;

import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.dialect.function.FunctionSpec;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;

import java.util.stream.Stream;

public class DateAddFunctionSpec implements FunctionSpec {
    private String part;

    private DateAddFunctionSpec(String part) {
        this.part = part;
    }

    @Override
    public String sql(String[] argsSql) {
        return String.format("dateadd(%s, %s, %s)", part, argsSql[1], argsSql[0]);
    }

    @Override
    public Stream<Object> args(Scope scope, TypedExpression<?>[] args) {
        return Stream.of(args[1], args[0])
            .flatMap(a -> a.args(scope));
    }

    public static DateAddFunctionSpec of(String part) {
        return new DateAddFunctionSpec(part);
    }
}
