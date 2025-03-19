/*
 * Copyright (c) 2020, 2025 Cadenza United Kingdom Limited
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
import com.cadenzauk.core.tuple.Tuple4;
import com.cadenzauk.siesta.RowMapperFactories;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Scope;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

import java.util.stream.Stream;

import static com.cadenzauk.core.reflect.util.TypeUtil.boxedType;

public class TupleBuilder4<T1, T2, T3, T4> extends TupleBuilder implements TypedExpression<Tuple4<T1,T2,T3,T4>> {
    private final TypedExpression<T1> item1;
    private final TypedExpression<T2> item2;
    private final TypedExpression<T3> item3;
    private final TypedExpression<T4> item4;
    private final TypeToken<Tuple4<T1,T2,T3,T4>> type;

    public TupleBuilder4(TypedExpression<T1> item1,TypedExpression<T2> item2,TypedExpression<T3> item3,TypedExpression<T4> item4) {
        this.item1 = item1;
        this.item2 = item2;
        this.item3 = item3;
        this.item4 = item4;
        type = new TypeToken<Tuple4<T1,T2,T3,T4>>() {}
            .where(new TypeParameter<T1>() {}, boxedType(item1.type()))
            .where(new TypeParameter<T2>() {}, boxedType(item2.type()))
            .where(new TypeParameter<T3>() {}, boxedType(item3.type()))
            .where(new TypeParameter<T4>() {}, boxedType(item4.type()));
    }

    @Override
    public RowMapperFactory<Tuple4<T1,T2,T3,T4>> rowMapperFactory(Scope scope) {
        return RowMapperFactories.of(
            item1.rowMapperFactory(scope),
            item2.rowMapperFactory(scope),
            item3.rowMapperFactory(scope),
            item4.rowMapperFactory(scope)
        );
    }

    @Override
    public TypeToken<Tuple4<T1,T2,T3,T4>> type() {
        return type;
    }

    public <R, T5> TupleBuilder5<T1,T2,T3,T4,T5> comma(Function1<R,T5> methodRef) {
        return new TupleBuilder5<>(item1, item2, item3, item4, UnresolvedColumn.of(methodRef));
    }

    public <R, T5> TupleBuilder5<T1,T2,T3,T4,T5> comma(FunctionOptional1<R,T5> methodRef) {
        return new TupleBuilder5<>(item1, item2, item3, item4, UnresolvedColumn.of(methodRef));
    }

    public <R, T5> TupleBuilder5<T1,T2,T3,T4,T5> comma(String alias, Function1<R,T5> methodRef) {
        return new TupleBuilder5<>(item1, item2, item3, item4, UnresolvedColumn.of(alias, methodRef));
    }

    public <R, T5> TupleBuilder5<T1,T2,T3,T4,T5> comma(String alias, FunctionOptional1<R,T5> methodRef) {
        return new TupleBuilder5<>(item1, item2, item3, item4, UnresolvedColumn.of(alias, methodRef));
    }

    public <R, T5> TupleBuilder5<T1,T2,T3,T4,T5> comma(Alias<R> alias, Function1<R,T5> methodRef) {
        return new TupleBuilder5<>(item1, item2, item3, item4, ResolvedColumn.of(alias, methodRef));
    }

    public <R, T5> TupleBuilder5<T1,T2,T3,T4,T5> comma(Alias<R> alias, FunctionOptional1<R,T5> methodRef) {
        return new TupleBuilder5<>(item1, item2, item3, item4, ResolvedColumn.of(alias, methodRef));
    }

    public <T5> TupleBuilder5<T1,T2,T3,T4,T5> comma(T5 value) {
        return new TupleBuilder5<>(item1, item2, item3, item4, ValueExpression.of(value));
    }

    public <T5> TupleBuilder5<T1,T2,T3,T4,T5> comma(TypedExpression<T5> item5) {
        return new TupleBuilder5<>(item1, item2, item3, item4, item5);
    }

    @Override
    protected Stream<TypedExpression<?>> items() {
        return Stream.of(item1, item2, item3, item4);
    }
}
