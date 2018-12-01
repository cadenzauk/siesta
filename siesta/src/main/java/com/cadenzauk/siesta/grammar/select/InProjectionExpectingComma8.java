/*
* Copyright (c) 2017, 2018 Cadenza United Kingdom Limited
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

package com.cadenzauk.siesta.grammar.select;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.core.tuple.Tuple8;
import com.cadenzauk.core.tuple.Tuple9;
import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.RowMappers;
import com.cadenzauk.siesta.grammar.expression.ResolvedColumn;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.grammar.expression.UnresolvedColumn;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static com.cadenzauk.core.reflect.util.TypeUtil.boxedType;

public class InProjectionExpectingComma8<T1, T2, T3, T4, T5, T6, T7, T8> extends ExpectingWhere<Tuple8<T1,T2,T3,T4,T5,T6,T7,T8>> {
    public InProjectionExpectingComma8(SelectStatement<Tuple8<T1,T2,T3,T4,T5,T6,T7,T8>> statement) {
        super(statement);
    }

    public <T> InProjectionExpectingComma9<T1,T2,T3,T4,T5,T6,T7,T8,T> comma(TypedExpression<T> expression) {
        return comma(expression, Optional.empty());
    }

    public <T> InProjectionExpectingComma9<T1,T2,T3,T4,T5,T6,T7,T8,T> comma(TypedExpression<T> expression, String label) {
        return comma(expression, OptionalUtil.ofBlankable(label));
    }

    public <T, R> InProjectionExpectingComma9<T1,T2,T3,T4,T5,T6,T7,T8,T> comma(Function1<R,T> methodReference) {
        return comma(UnresolvedColumn.of(methodReference), Optional.empty());
    }

    public <T, R> InProjectionExpectingComma9<T1,T2,T3,T4,T5,T6,T7,T8,T> comma(Function1<R,T> methodReference, String label) {
        return comma(UnresolvedColumn.of(methodReference), OptionalUtil.ofBlankable(label));
    }

    public <T, R> InProjectionExpectingComma9<T1,T2,T3,T4,T5,T6,T7,T8,T> comma(FunctionOptional1<R,T> methodReference) {
        return comma(UnresolvedColumn.of(methodReference), Optional.empty());
    }

    public <T, R> InProjectionExpectingComma9<T1,T2,T3,T4,T5,T6,T7,T8,T> comma(FunctionOptional1<R,T> methodReference, String label) {
        return comma(UnresolvedColumn.of(methodReference), OptionalUtil.ofBlankable(label));
    }

    public <T, R> InProjectionExpectingComma9<T1,T2,T3,T4,T5,T6,T7,T8,T> comma(String alias, Function1<R,T> methodReference) {
        return comma(UnresolvedColumn.of(alias, methodReference), Optional.empty());
    }

    public <T, R> InProjectionExpectingComma9<T1,T2,T3,T4,T5,T6,T7,T8,T> comma(String alias, Function1<R,T> methodReference, String label) {
        return comma(UnresolvedColumn.of(alias, methodReference), OptionalUtil.ofBlankable(label));
    }

    public <T, R> InProjectionExpectingComma9<T1,T2,T3,T4,T5,T6,T7,T8,T> comma(String alias, FunctionOptional1<R,T> methodReference) {
        return comma(UnresolvedColumn.of(alias, methodReference), Optional.empty());
    }

    public <T, R> InProjectionExpectingComma9<T1,T2,T3,T4,T5,T6,T7,T8,T> comma(String alias, FunctionOptional1<R,T> methodReference, String label) {
        return comma(UnresolvedColumn.of(alias, methodReference), OptionalUtil.ofBlankable(label));
    }

    public <T, R> InProjectionExpectingComma9<T1,T2,T3,T4,T5,T6,T7,T8,T> comma(Alias<R> alias, Function1<R,T> methodReference) {
        return comma(ResolvedColumn.of(alias, methodReference), Optional.empty());
    }

    public <T, R> InProjectionExpectingComma9<T1,T2,T3,T4,T5,T6,T7,T8,T> comma(Alias<R> alias, Function1<R,T> methodReference, String label) {
        return comma(ResolvedColumn.of(alias, methodReference), OptionalUtil.ofBlankable(label));
    }

    public <T, R> InProjectionExpectingComma9<T1,T2,T3,T4,T5,T6,T7,T8,T> comma(Alias<R> alias, FunctionOptional1<R,T> methodReference) {
        return comma(ResolvedColumn.of(alias, methodReference), Optional.empty());
    }

    public <T, R> InProjectionExpectingComma9<T1,T2,T3,T4,T5,T6,T7,T8,T> comma(Alias<R> alias, FunctionOptional1<R,T> methodReference, String label) {
        return comma(ResolvedColumn.of(alias, methodReference), OptionalUtil.ofBlankable(label));
    }

    public <T> InProjectionExpectingComma9<T1,T2,T3,T4,T5,T6,T7,T8,T> comma(Class<T> rowClass) {
        Alias<T> alias = scope().findAlias(rowClass);
        return comma(alias);
    }

    public <T> InProjectionExpectingComma9<T1,T2,T3,T4,T5,T6,T7,T8,T> comma(Class<T> rowClass, String aliasName) {
        Alias<T> alias = scope().findAlias(rowClass, aliasName);
        return comma(alias);
    }

    public <T> InProjectionExpectingComma9<T1,T2,T3,T4,T5,T6,T7,T8,T> comma(Alias<T> alias) {
        SelectStatement<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T>> select = new SelectStatement<>(
            scope(),
            new TypeToken<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T>>() {}
                .where(new TypeParameter<T1>() {}, Tuple8.type1(type()))
                .where(new TypeParameter<T2>() {}, Tuple8.type2(type()))
                .where(new TypeParameter<T3>() {}, Tuple8.type3(type()))
                .where(new TypeParameter<T4>() {}, Tuple8.type4(type()))
                .where(new TypeParameter<T5>() {}, Tuple8.type5(type()))
                .where(new TypeParameter<T6>() {}, Tuple8.type6(type()))
                .where(new TypeParameter<T7>() {}, Tuple8.type7(type()))
                .where(new TypeParameter<T8>() {}, Tuple8.type8(type()))
                .where(new TypeParameter<T>() {}, boxedType(alias.type())),
            statement.from(),
            RowMappers.add9th(
                statement.rowMapper(),
                alias.rowMapper()),
                Projection.of(statement.projection(), Projection.of(alias)));
        return new InProjectionExpectingComma9<>(select);
    }

    @NotNull
    private <T> InProjectionExpectingComma9<T1,T2,T3,T4,T5,T6,T7,T8,T> comma(TypedExpression<T> col, Optional<String> label) {
        SelectStatement<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T>> select = new SelectStatement<>(
            scope(),
            new TypeToken<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T>>() {}
                .where(new TypeParameter<T1>() {}, Tuple8.type1(type()))
                .where(new TypeParameter<T2>() {}, Tuple8.type2(type()))
                .where(new TypeParameter<T3>() {}, Tuple8.type3(type()))
                .where(new TypeParameter<T4>() {}, Tuple8.type4(type()))
                .where(new TypeParameter<T5>() {}, Tuple8.type5(type()))
                .where(new TypeParameter<T6>() {}, Tuple8.type6(type()))
                .where(new TypeParameter<T7>() {}, Tuple8.type7(type()))
                .where(new TypeParameter<T8>() {}, Tuple8.type8(type()))
                .where(new TypeParameter<T>() {}, boxedType(col.type())),
            statement.from(),
            RowMappers.add9th(
                statement.rowMapper(),
                col.rowMapper(scope(), label)),
                Projection.of(statement.projection(), Projection.of(col, label)));
        return new InProjectionExpectingComma9<>(select);
    }
}
