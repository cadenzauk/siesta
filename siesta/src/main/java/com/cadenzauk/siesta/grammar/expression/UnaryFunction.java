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
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Scope;

import java.util.function.BiFunction;
import java.util.stream.Stream;

public class UnaryFunction<T, R> implements TypedExpression<R> {
    private final String name;
    private final TypedExpression<T> arg;
    private final BiFunction<Scope,String,RowMapper<R>> rowMapperFactory;

    private UnaryFunction(String name, TypedExpression<T> arg, BiFunction<Scope,String,RowMapper<R>> rowMapperFactory) {
        this.name = name;
        this.arg = arg;
        this.rowMapperFactory = rowMapperFactory;
    }

    @Override
    public String sql(Scope scope) {
        return String.format("%s(%s)", name, arg.sql(scope));
    }

    @Override
    public String label(Scope scope) {
        return String.format("%s_%s", name, arg.label(scope));
    }

    @Override
    public RowMapper<R> rowMapper(Scope scope, String label) {
        return rowMapperFactory.apply(scope, label);
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return arg.args(scope);
    }

    @Override
    public Precedence precedence() {
        return Precedence.UNARY;
    }

    public static <T> UnaryFunction<T,T> of(TypedExpression<T> arg, String name) {
        return new UnaryFunction<>(name, arg, arg::rowMapper);
    }

    public static <T, R> UnaryFunction<T,T> of(Function1<R,T> arg, String name) {
        return of(UnresolvedColumn.of(arg), name);
    }

    public static <T, R> UnaryFunction<T,T> of(FunctionOptional1<R,T> arg, String name) {
        return of(UnresolvedColumn.of(arg), name);
    }

    public static <T, R> UnaryFunction<T,T> of(String alias, Function1<R,T> arg, String name) {
        return of(UnresolvedColumn.of(alias, arg), name);
    }

    public static <T, R> UnaryFunction<T,T> of(String alias, FunctionOptional1<R,T> arg, String name) {
        return of(UnresolvedColumn.of(alias, arg), name);
    }

    public static <T, R> UnaryFunction<T,T> of(Alias<R> alias, Function1<R,T> arg, String name) {
        return of(ResolvedColumn.of(alias, arg), name);
    }

    public static <T, R> UnaryFunction<T,T> of(Alias<R> alias, FunctionOptional1<R,T> arg, String name) {
        return of(ResolvedColumn.of(alias, arg), name);
    }

    public static <T, S> UnaryFunction<T,S> of(TypedExpression<T> arg, String name, Class<S> resultClass) {
        return new UnaryFunction<>(name, arg, Scope.makeMapper(resultClass));
    }

    public static <T, R, S> UnaryFunction<T,S> of(Function1<R,T> arg, String name, Class<S> resultClass) {
        return of(UnresolvedColumn.of(arg), name, resultClass);
    }

    public static <T, R, S> UnaryFunction<T,S> of(FunctionOptional1<R,T> arg, String name, Class<S> resultClass) {
        return of(UnresolvedColumn.of(arg), name, resultClass);
    }

    public static <T, R, S> UnaryFunction<T,S> of(String alias, Function1<R,T> arg, String name, Class<S> resultClass) {
        return of(UnresolvedColumn.of(alias, arg), name, resultClass);
    }

    public static <T, R, S> UnaryFunction<T,S> of(String alias, FunctionOptional1<R,T> arg, String name, Class<S> resultClass) {
        return of(UnresolvedColumn.of(alias, arg), name, resultClass);
    }

    public static <T, R, S> UnaryFunction<T,S> of(Alias<R> alias, Function1<R,T> arg, String name, Class<S> resultClass) {
        return of(ResolvedColumn.of(alias, arg), name, resultClass);
    }

    public static <T, R, S> UnaryFunction<T,S> of(Alias<R> alias, FunctionOptional1<R,T> arg, String name, Class<S> resultClass) {
        return of(ResolvedColumn.of(alias, arg), name, resultClass);
    }

}
