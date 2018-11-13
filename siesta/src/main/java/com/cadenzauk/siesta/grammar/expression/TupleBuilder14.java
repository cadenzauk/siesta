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
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.core.tuple.Tuple14;
import com.cadenzauk.siesta.RowMappers;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Scope;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

import java.util.Optional;
import java.util.stream.Stream;

import static com.cadenzauk.core.reflect.util.TypeUtil.boxedType;

public class TupleBuilder14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> extends TupleBuilder implements TypedExpression<Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14>> {
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
    private final TypedExpression<T12> item12;
    private final TypedExpression<T13> item13;
    private final TypedExpression<T14> item14;
    private final TypeToken<Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14>> type;

    public TupleBuilder14(TypedExpression<T1> item1,TypedExpression<T2> item2,TypedExpression<T3> item3,TypedExpression<T4> item4,TypedExpression<T5> item5,TypedExpression<T6> item6,TypedExpression<T7> item7,TypedExpression<T8> item8,TypedExpression<T9> item9,TypedExpression<T10> item10,TypedExpression<T11> item11,TypedExpression<T12> item12,TypedExpression<T13> item13,TypedExpression<T14> item14) {
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
        this.item12 = item12;
        this.item13 = item13;
        this.item14 = item14;
        type = new TypeToken<Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14>>() {}
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
            .where(new TypeParameter<T11>() {}, boxedType(item11.type()))
            .where(new TypeParameter<T12>() {}, boxedType(item12.type()))
            .where(new TypeParameter<T13>() {}, boxedType(item13.type()))
            .where(new TypeParameter<T14>() {}, boxedType(item14.type()));
    }

    @Override
    public RowMapper<Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14>> rowMapper(Scope scope, Optional<String> label) {
        return RowMappers.of(
            item1.rowMapper(scope, Optional.empty()),
            item2.rowMapper(scope, Optional.empty()),
            item3.rowMapper(scope, Optional.empty()),
            item4.rowMapper(scope, Optional.empty()),
            item5.rowMapper(scope, Optional.empty()),
            item6.rowMapper(scope, Optional.empty()),
            item7.rowMapper(scope, Optional.empty()),
            item8.rowMapper(scope, Optional.empty()),
            item9.rowMapper(scope, Optional.empty()),
            item10.rowMapper(scope, Optional.empty()),
            item11.rowMapper(scope, Optional.empty()),
            item12.rowMapper(scope, Optional.empty()),
            item13.rowMapper(scope, Optional.empty()),
            item14.rowMapper(scope, Optional.empty())
        );
    }

    @Override
    public TypeToken<Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14>> type() {
        return type;
    }

    public <R, T15> TupleBuilder15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> comma(Function1<R,T15> methodRef) {
        return new TupleBuilder15<>(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11, item12, item13, item14, UnresolvedColumn.of(methodRef));
    }

    public <R, T15> TupleBuilder15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> comma(FunctionOptional1<R,T15> methodRef) {
        return new TupleBuilder15<>(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11, item12, item13, item14, UnresolvedColumn.of(methodRef));
    }

    public <R, T15> TupleBuilder15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> comma(String alias, Function1<R,T15> methodRef) {
        return new TupleBuilder15<>(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11, item12, item13, item14, UnresolvedColumn.of(alias, methodRef));
    }

    public <R, T15> TupleBuilder15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> comma(String alias, FunctionOptional1<R,T15> methodRef) {
        return new TupleBuilder15<>(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11, item12, item13, item14, UnresolvedColumn.of(alias, methodRef));
    }

    public <R, T15> TupleBuilder15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> comma(Alias<R> alias, Function1<R,T15> methodRef) {
        return new TupleBuilder15<>(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11, item12, item13, item14, ResolvedColumn.of(alias, methodRef));
    }

    public <R, T15> TupleBuilder15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> comma(Alias<R> alias, FunctionOptional1<R,T15> methodRef) {
        return new TupleBuilder15<>(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11, item12, item13, item14, ResolvedColumn.of(alias, methodRef));
    }

    @Override
    protected Stream<TypedExpression<?>> items() {
        return Stream.of(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11, item12, item13, item14);
    }
}
