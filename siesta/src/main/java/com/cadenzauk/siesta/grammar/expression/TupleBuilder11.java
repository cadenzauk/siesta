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
import com.cadenzauk.core.tuple.Tuple11;
import com.cadenzauk.siesta.RowMapperFactories;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Scope;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

import java.util.stream.Stream;

import static com.cadenzauk.core.reflect.util.TypeUtil.boxedType;

public class TupleBuilder11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> extends TupleBuilder implements TypedExpression<Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>> {
    private final TypedExpression<T1> item1;
    private final TypedExpression<T2> item2;
    private final TypedExpression<T3> item3;
    private final TypedExpression<T4> item4;
    private final TypedExpression<T5> item5;
    private final TypedExpression<T6> item6;
    private final TypedExpression<T7> item7;
    private final TypedExpression<T8> item8;
    private final TypedExpression<T9> item9;
    private final TypedExpression<T10> item10;
    private final TypedExpression<T11> item11;
    private final TypeToken<Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>> type;

    public TupleBuilder11(TypedExpression<T1> item1,TypedExpression<T2> item2,TypedExpression<T3> item3,TypedExpression<T4> item4,TypedExpression<T5> item5,TypedExpression<T6> item6,TypedExpression<T7> item7,TypedExpression<T8> item8,TypedExpression<T9> item9,TypedExpression<T10> item10,TypedExpression<T11> item11) {
        this.item1 = item1;
        this.item2 = item2;
        this.item3 = item3;
        this.item4 = item4;
        this.item5 = item5;
        this.item6 = item6;
        this.item7 = item7;
        this.item8 = item8;
        this.item9 = item9;
        this.item10 = item10;
        this.item11 = item11;
        type = new TypeToken<Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>>() {}
            .where(new TypeParameter<T1>() {}, boxedType(item1.type()))
            .where(new TypeParameter<T2>() {}, boxedType(item2.type()))
            .where(new TypeParameter<T3>() {}, boxedType(item3.type()))
            .where(new TypeParameter<T4>() {}, boxedType(item4.type()))
            .where(new TypeParameter<T5>() {}, boxedType(item5.type()))
            .where(new TypeParameter<T6>() {}, boxedType(item6.type()))
            .where(new TypeParameter<T7>() {}, boxedType(item7.type()))
            .where(new TypeParameter<T8>() {}, boxedType(item8.type()))
            .where(new TypeParameter<T9>() {}, boxedType(item9.type()))
            .where(new TypeParameter<T10>() {}, boxedType(item10.type()))
            .where(new TypeParameter<T11>() {}, boxedType(item11.type()));
    }

    @Override
    public RowMapperFactory<Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>> rowMapperFactory(Scope scope) {
        return RowMapperFactories.of(
            item1.rowMapperFactory(scope),
            item2.rowMapperFactory(scope),
            item3.rowMapperFactory(scope),
            item4.rowMapperFactory(scope),
            item5.rowMapperFactory(scope),
            item6.rowMapperFactory(scope),
            item7.rowMapperFactory(scope),
            item8.rowMapperFactory(scope),
            item9.rowMapperFactory(scope),
            item10.rowMapperFactory(scope),
            item11.rowMapperFactory(scope)
        );
    }

    @Override
    public TypeToken<Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>> type() {
        return type;
    }

    public <R, T12> TupleBuilder12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> comma(Function1<R,T12> methodRef) {
        return new TupleBuilder12<>(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11, UnresolvedColumn.of(methodRef));
    }

    public <R, T12> TupleBuilder12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> comma(FunctionOptional1<R,T12> methodRef) {
        return new TupleBuilder12<>(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11, UnresolvedColumn.of(methodRef));
    }

    public <R, T12> TupleBuilder12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> comma(String alias, Function1<R,T12> methodRef) {
        return new TupleBuilder12<>(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11, UnresolvedColumn.of(alias, methodRef));
    }

    public <R, T12> TupleBuilder12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> comma(String alias, FunctionOptional1<R,T12> methodRef) {
        return new TupleBuilder12<>(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11, UnresolvedColumn.of(alias, methodRef));
    }

    public <R, T12> TupleBuilder12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> comma(Alias<R> alias, Function1<R,T12> methodRef) {
        return new TupleBuilder12<>(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11, ResolvedColumn.of(alias, methodRef));
    }

    public <R, T12> TupleBuilder12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> comma(Alias<R> alias, FunctionOptional1<R,T12> methodRef) {
        return new TupleBuilder12<>(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11, ResolvedColumn.of(alias, methodRef));
    }

    public <T12> TupleBuilder12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> comma(T12 value) {
        return new TupleBuilder12<>(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11, ValueExpression.of(value));
    }

    public <T12> TupleBuilder12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> comma(TypedExpression<T12> item12) {
        return new TupleBuilder12<>(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11, item12);
    }

    @Override
    protected Stream<TypedExpression<?>> items() {
        return Stream.of(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11);
    }
}
