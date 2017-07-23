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
import com.cadenzauk.core.function.Function2;
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.Dialect;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.grammar.LabelGenerator;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class DialectFunction<T> implements TypedExpression<T> {
    private final LabelGenerator labelGenerator;
    private final BiFunction<Dialect,String[],String> sqlFunction;
    private final TypedExpression<?>[] args;
    private final BiFunction<Scope,String,RowMapper<T>> rowMapperFactory;

    private DialectFunction(String name, BiFunction<Dialect,String[],String> sqlFunction, BiFunction<Scope, String, RowMapper<T>> rowMapperFactory, TypedExpression<?>... args) {
        labelGenerator = new LabelGenerator(name + "_");
        this.sqlFunction = sqlFunction;
        this.args = args;
        this.rowMapperFactory = rowMapperFactory;
    }

    @Override
    public String sql(Scope scope) {
        return sqlFunction.apply(scope.database().dialect(), Arrays.stream(args).map(a -> a.sql(scope)).toArray(String[]::new));
    }

    @Override
    public String label(Scope scope) {
        return labelGenerator.label();
    }

    @Override
    public RowMapper<T> rowMapper(Scope scope, String label) {
        return rowMapperFactory.apply(scope, label);
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return Arrays.stream(args).flatMap(a -> a.args(scope));
    }

    @Override
    public Precedence precedence() {
        return Precedence.UNARY;
    }

    public static <T> DialectFunction<T> of(String name, Function1<Dialect,String> sqlFunction, Class<T> resultClass) {
        return new DialectFunction<>(name, (d, a) -> sqlFunction.apply(d), Scope.makeMapper(resultClass));
    }

    public static <T> DialectFunction<T> of(String name, Function2<Dialect,String,String> sqlFunction, TypedExpression<?> arg, Class<T> resultClass) {
        return new DialectFunction<>(name, (d, a) -> sqlFunction.apply(d, a[0]), Scope.makeMapper(resultClass), arg);
    }
}
