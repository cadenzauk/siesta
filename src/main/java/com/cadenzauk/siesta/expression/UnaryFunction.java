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

package com.cadenzauk.siesta.expression;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.RowMapper;
import com.cadenzauk.siesta.Scope;

import java.util.stream.Stream;

public class UnaryFunction<T> implements TypedExpression<T> {
    private final String name;
    private final TypedExpression<T> arg;

    private UnaryFunction(String name, TypedExpression<T> arg) {
        this.name = name;
        this.arg = arg;
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
    public RowMapper<T> rowMapper(Scope scope, String label) {
        return arg.rowMapper(scope, label);
    }

    @Override
    public Stream<Object> args() {
        return arg.args();
    }

    public static <T> UnaryFunction<T> of(TypedExpression<T> arg, String name) {
        return new UnaryFunction<>(name, arg);
    }

    public static <T,R> UnaryFunction<T> of(Function1<R,T> arg, String name) {
        return new UnaryFunction<>(name, UnresolvedColumn.of(arg));
    }

    public static <T,R> UnaryFunction<T> of(FunctionOptional1<R,T> arg, String name) {
        return new UnaryFunction<>(name, UnresolvedColumn.of(arg));
    }

    public static <T,R> UnaryFunction<T> of(String alias, Function1<R,T> arg, String name) {
        return new UnaryFunction<>(name, UnresolvedColumn.of(alias, arg));
    }

    public static <T,R> UnaryFunction<T> of(String alias, FunctionOptional1<R,T> arg, String name) {
        return new UnaryFunction<>(name, UnresolvedColumn.of(alias, arg));
    }

    public static <T,R> UnaryFunction<T> of(Alias<R> alias, Function1<R,T> arg, String name) {
        return new UnaryFunction<>(name, ResolvedColumn.of(alias, arg));
    }

    public static <T,R> TypedExpression<T> of(Alias<R> alias, FunctionOptional1<R,T> arg, String name) {
        return new UnaryFunction<>(name, ResolvedColumn.of(alias, arg));
    }
}
