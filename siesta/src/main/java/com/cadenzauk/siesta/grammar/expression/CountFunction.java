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
import com.cadenzauk.core.stream.StreamUtil;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.dialect.function.FunctionName;
import com.cadenzauk.siesta.grammar.LabelGenerator;
import com.google.common.reflect.TypeToken;

import java.util.Optional;
import java.util.stream.Stream;

public class CountFunction<T> implements TypedExpression<T> {
    private final LabelGenerator labelGenerator = new LabelGenerator("count_");
    private final FunctionName name;
    private final TypeToken<T> type;
    private final Optional<TypedExpression<?>> arg;

    public CountFunction(FunctionName name, TypeToken<T> type) {
        this.name = name;
        this.type = type;
        arg = Optional.empty();
    }

    public <U> CountFunction(FunctionName name, TypeToken<T> type, TypedExpression<U> arg) {
        this.name = name;
        this.type = type;
        this.arg = Optional.of(arg);
    }

    @Override
    public String sql(Scope scope) {
        return scope.dialect().function(name).sql(scope, arg.map(a -> a.sql(scope)).orElse("*"));
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return scope.dialect().function(name).args(scope, StreamUtil.of(arg).toArray(TypedExpression[]::new));
    }

    @Override
    public Precedence precedence() {
        return Precedence.UNARY;
    }

    @Override
    public String label(Scope scope) {
        return labelGenerator.label(scope);
    }

    @Override
    public RowMapperFactory<T> rowMapperFactory(Scope scope) {
        return label -> rs -> scope.database().getDataTypeOf(type).get(rs, label.orElseGet(() -> label(scope)), scope.database()).orElse(null);
    }

    @Override
    public TypeToken<T> type() {
        return type;
    }
}
