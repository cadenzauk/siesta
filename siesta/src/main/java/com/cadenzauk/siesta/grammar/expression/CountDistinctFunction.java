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

import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.dialect.function.FunctionName;
import com.google.common.reflect.TypeToken;

import java.util.stream.Stream;

public class CountDistinctFunction<T> implements TypedExpression<T> {
    private final FunctionName name;
    private final TypedExpression<?> arg;
    private final TypeToken<T> type;

    public CountDistinctFunction(FunctionName name, TypeToken<T> type, TypedExpression<?> arg) {
        this.name = name;
        this.arg = arg;
        this.type = type;
    }

    @Override
    public String sql(Scope scope) {
        return scope.dialect().function(name).sql(scope, arg.sql(scope));
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return scope.dialect().function(name).args(scope, arg);
    }

    @Override
    public Precedence precedence() {
        return Precedence.UNARY;
    }

    @Override
    public String label(Scope scope) {
        return "count_" + arg.label(scope);
    }

    @Override
    public RowMapperFactory<T> rowMapperFactory(Scope scope) {
        return (prefix, label) -> rs -> scope.database().getDataTypeOf(type).get(rs, prefix + label.orElseGet(() -> label(scope)), scope.database()).orElse(null);
    }

    @Override
    public TypeToken<T> type() {
        return type;
    }
}
