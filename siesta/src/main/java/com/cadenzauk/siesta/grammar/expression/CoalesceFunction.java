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
import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.grammar.LabelGenerator;
import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class CoalesceFunction<T> implements TypedExpression<T> {
    private final LabelGenerator labelGenerator = new LabelGenerator("coalesce_");
    private final List<TypedExpression<T>> terms = new ArrayList<>();

    private CoalesceFunction(TypedExpression<T> expression) {
        terms.add(expression);
    }

    @Override
    public String sql(Scope scope) {
        return String.format("coalesce(%s)", terms.stream().map(t -> t.sql(scope)).collect(joining(", ")));
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return terms.stream().flatMap(t -> t.args(scope));
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
        return label -> terms.get(0).rowMapperFactory(scope).rowMapper(Optional.of(label.orElseGet(() -> label(scope))));
    }

    @Override
    public TypeToken<T> type() {
        return terms.get(0).type();
    }

    @NotNull
    public CoalesceFunction<T> orElse(T value) {
        terms.add(ValueExpression.of(value));
        return this;
    }

    @NotNull
    public CoalesceFunction<T> orElse(TypedExpression<T> expression) {
        terms.add(expression);
        return this;
    }

    @NotNull
    public <R> CoalesceFunction<T> orElse(Function1<R,T> methodReference) {
        terms.add(UnresolvedColumn.of(methodReference));
        return this;
    }

    @NotNull
    public <R> CoalesceFunction<T> orElse(FunctionOptional1<R,T> methodReference) {
        terms.add(UnresolvedColumn.of(methodReference));
        return this;
    }

    @NotNull
    public <R> CoalesceFunction<T> orElse(String alias, Function1<R,T> methodReference) {
        terms.add(UnresolvedColumn.of(alias, methodReference));
        return this;
    }

    @NotNull
    public <R> CoalesceFunction<T> orElse(String alias, FunctionOptional1<R,T> methodReference) {
        terms.add(UnresolvedColumn.of(alias, methodReference));
        return this;
    }

    @NotNull
    public <R> CoalesceFunction<T> orElse(Alias<R> alias, Function1<R,T> methodReference) {
        terms.add(ResolvedColumn.of(alias, methodReference));
        return this;
    }

    @NotNull
    public <R> CoalesceFunction<T> orElse(Alias<R> alias, FunctionOptional1<R,T> methodReference) {
        terms.add(ResolvedColumn.of(alias, methodReference));
        return this;
    }

    @NotNull
    public static <T> CoalesceFunction<T> coalesce(TypedExpression<T> expression) {
        return new CoalesceFunction<>(expression);
    }

    @NotNull
    public static <T, R> CoalesceFunction<T> coalesce(Function1<R,T> methodReference) {
        return new CoalesceFunction<>(UnresolvedColumn.of(methodReference));
    }

    @NotNull
    public static <T, R> CoalesceFunction<T> coalesce(FunctionOptional1<R,T> methodReference) {
        return new CoalesceFunction<>(UnresolvedColumn.of(methodReference));
    }

    @NotNull
    public static <T, R> CoalesceFunction<T> coalesce(String alias, Function1<R,T> methodReference) {
        return new CoalesceFunction<>(UnresolvedColumn.of(alias, methodReference));
    }

    @NotNull
    public static <T, R> CoalesceFunction<T> coalesce(String alias, FunctionOptional1<R,T> methodReference) {
        return new CoalesceFunction<>(UnresolvedColumn.of(alias, methodReference));
    }

    @NotNull
    public static <T, R> CoalesceFunction<T> coalesce(Alias<R> alias, Function1<R,T> methodReference) {
        return new CoalesceFunction<>(ResolvedColumn.of(alias, methodReference));
    }

    @NotNull
    public static <T, R> CoalesceFunction<T> coalesce(Alias<R> alias, FunctionOptional1<R,T> methodReference) {
        return new CoalesceFunction<>(ResolvedColumn.of(alias, methodReference));
    }
}
