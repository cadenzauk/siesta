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
import com.cadenzauk.siesta.grammar.LabelGenerator;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class SqlFunction<T> implements TypedExpression<T> {
    private final LabelGenerator labelGenerator;
    private final String name;
    private final TypedExpression<?>[] args;
    private final BiFunction<Scope,String,RowMapper<T>> rowMapperFactory;

    private SqlFunction(String name, BiFunction<Scope,String,RowMapper<T>> rowMapperFactory, TypedExpression<?>... args) {
        labelGenerator = new LabelGenerator(name + "_");
        this.name = name;
        this.args = args;
        this.rowMapperFactory = rowMapperFactory;
    }

    @Override
    public String sql(Scope scope) {
        return String.format("%s(%s)", name, Arrays.stream(args).map(a -> a.sql(scope)).collect(joining(", ")));
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
        return Arrays.stream(args).flatMap(a  -> a.args(scope));
    }

    @Override
    public Precedence precedence() {
        return Precedence.UNARY;
    }

    public static <T, U, S> SqlFunction<S> of(TypedExpression<T> arg1, TypedExpression<U> arg2, String name, Class<S> resultClass) {
        return new SqlFunction<>(name, Scope.makeMapper(resultClass), arg1, arg2);
    }

    public static <T, U, V, S> SqlFunction<S> of(TypedExpression<T> arg1, TypedExpression<U> arg2 , TypedExpression<V> arg3, String name, Class<S> resultClass) {
        return new SqlFunction<>(name, Scope.makeMapper(resultClass), arg1, arg2, arg3);
    }
}
