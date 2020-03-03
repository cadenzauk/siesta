/*
 * Copyright (c) 2020 Cadenza United Kingdom Limited
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
import com.google.common.reflect.TypeToken;

import java.util.stream.Stream;

public class TupleBuilder1<T1> extends TupleBuilder implements TypedExpression<T1> {
    private final TypedExpression<T1> item1;

    public TupleBuilder1(TypedExpression<T1> item1) {
        this.item1 = item1;
    }

    @Override
    public RowMapperFactory<T1> rowMapperFactory(Scope scope) {
        return item1.rowMapperFactory(scope);
    }

    @Override
    public TypeToken<T1> type() {
        return item1.type();
    }

    public <R, T2> TupleBuilder2<T1,T2> comma(Function1<R,T2> methodRef) {
        return new TupleBuilder2<>(item1, UnresolvedColumn.of(methodRef));
    }

    public <R, T2> TupleBuilder2<T1,T2> comma(FunctionOptional1<R,T2> methodRef) {
        return new TupleBuilder2<>(item1, UnresolvedColumn.of(methodRef));
    }

    public <R, T2> TupleBuilder2<T1,T2> comma(String alias, Function1<R,T2> methodRef) {
        return new TupleBuilder2<>(item1, UnresolvedColumn.of(alias, methodRef));
    }

    public <R, T2> TupleBuilder2<T1,T2> comma(String alias, FunctionOptional1<R,T2> methodRef) {
        return new TupleBuilder2<>(item1, UnresolvedColumn.of(alias, methodRef));
    }

    public <R, T2> TupleBuilder2<T1,T2> comma(Alias<R> alias, Function1<R,T2> methodRef) {
        return new TupleBuilder2<>(item1, ResolvedColumn.of(alias, methodRef));
    }

    public <R, T2> TupleBuilder2<T1,T2> comma(Alias<R> alias, FunctionOptional1<R,T2> methodRef) {
        return new TupleBuilder2<>(item1, ResolvedColumn.of(alias, methodRef));
    }

    @Override
    protected Stream<TypedExpression<?>> items() {
        return Stream.of(item1);
    }
}
