/*
 * Copyright (c) 2018 Cadenza United Kingdom Limited
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
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.grammar.LabelGenerator;

import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public abstract class TupleBuilder {
    private final LabelGenerator labelGenerator = new LabelGenerator("tuple_");

    public String label(Scope scope) {
        return labelGenerator.label(scope);
    }

    public String sql(Scope scope) {
        return String.format("(%s)", items().map(item -> item.sql(scope)).collect(joining(", ")));
    }

    public Stream<Object> args(Scope scope) {
        return items().flatMap(item -> item.args(scope));
    }

    @SuppressWarnings("SameReturnValue")
    public Precedence precedence() {
        return Precedence.PARENTHESES;
    }

    protected abstract Stream<TypedExpression<?>> items();

    public static <R, T1> TupleBuilder1<T1> tuple(Function1<R,T1> methodRef) {
        return new TupleBuilder1<>(UnresolvedColumn.of(methodRef));
    }

    public static <R, T1> TupleBuilder1<T1> tuple(FunctionOptional1<R,T1> methodRef) {
        return new TupleBuilder1<>(UnresolvedColumn.of(methodRef));
    }

    public static <R, T1> TupleBuilder1<T1> tuple(String alias, Function1<R,T1> methodRef) {
        return new TupleBuilder1<>(UnresolvedColumn.of(alias, methodRef));
    }

    public static <R, T1> TupleBuilder1<T1> tuple(String alias, FunctionOptional1<R,T1> methodRef) {
        return new TupleBuilder1<>(UnresolvedColumn.of(alias, methodRef));
    }

    public static <R, T1> TupleBuilder1<T1> tuple(Alias<R> alias, Function1<R,T1> methodRef) {
        return new TupleBuilder1<>(ResolvedColumn.of(alias, methodRef));
    }

    public static <R, T1> TupleBuilder1<T1> tuple(Alias<R> alias, FunctionOptional1<R,T1> methodRef) {
        return new TupleBuilder1<>(ResolvedColumn.of(alias, methodRef));
    }
}
