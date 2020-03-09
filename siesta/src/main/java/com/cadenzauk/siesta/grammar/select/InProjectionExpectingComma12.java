/*
* Copyright (c) 2017, 2020 Cadenza United Kingdom Limited
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
import com.cadenzauk.core.tuple.Tuple12;
import com.cadenzauk.core.tuple.Tuple13;
import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.Projections;
import com.cadenzauk.siesta.grammar.expression.Label;
import com.cadenzauk.siesta.grammar.expression.ResolvedColumn;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.grammar.expression.UnresolvedColumn;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static com.cadenzauk.core.reflect.util.TypeUtil.boxedType;

public class InProjectionExpectingComma12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> extends ExpectingWhere<Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12>> {
    public InProjectionExpectingComma12(SelectStatement<Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12>> statement) {
        super(statement);
    }

    public <T> InProjectionExpectingComma13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T> comma(TypedExpression<T> expression) {
        return comma(expression, Optional.empty());
    }

    public <T> InProjectionExpectingComma13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T> comma(TypedExpression<T> expression, String label) {
        return comma(expression, OptionalUtil.ofBlankable(label));
    }

    public <T> InProjectionExpectingComma13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T> comma(TypedExpression<T> expression, Label<T> label) {
        return comma(expression, OptionalUtil.ofBlankable(label.label()));
    }

    public <T, R> InProjectionExpectingComma13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T> comma(Function1<R,T> methodReference) {
        return comma(UnresolvedColumn.of(methodReference), Optional.empty());
    }

    public <T, R> InProjectionExpectingComma13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T> comma(Function1<R,T> methodReference, String label) {
        return comma(UnresolvedColumn.of(methodReference), OptionalUtil.ofBlankable(label));
    }

    public <T, R> InProjectionExpectingComma13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T> comma(Function1<R,T> methodReference, Label<T> label) {
        return comma(UnresolvedColumn.of(methodReference), OptionalUtil.ofBlankable(label.label()));
    }

    public <T, R> InProjectionExpectingComma13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T> comma(FunctionOptional1<R,T> methodReference) {
        return comma(UnresolvedColumn.of(methodReference), Optional.empty());
    }

    public <T, R> InProjectionExpectingComma13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T> comma(FunctionOptional1<R,T> methodReference, String label) {
        return comma(UnresolvedColumn.of(methodReference), OptionalUtil.ofBlankable(label));
    }

    public <T, R> InProjectionExpectingComma13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T> comma(FunctionOptional1<R,T> methodReference, Label<T> label) {
        return comma(UnresolvedColumn.of(methodReference), OptionalUtil.ofBlankable(label.label()));
    }

    public <T, R> InProjectionExpectingComma13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T> comma(String alias, Function1<R,T> methodReference) {
        return comma(UnresolvedColumn.of(alias, methodReference), Optional.empty());
    }

    public <T, R> InProjectionExpectingComma13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T> comma(String alias, Function1<R,T> methodReference, String label) {
        return comma(UnresolvedColumn.of(alias, methodReference), OptionalUtil.ofBlankable(label));
    }

    public <T, R> InProjectionExpectingComma13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T> comma(String alias, Function1<R,T> methodReference, Label<T> label) {
        return comma(UnresolvedColumn.of(alias, methodReference), OptionalUtil.ofBlankable(label.label()));
    }

    public <T, R> InProjectionExpectingComma13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T> comma(String alias, FunctionOptional1<R,T> methodReference) {
        return comma(UnresolvedColumn.of(alias, methodReference), Optional.empty());
    }

    public <T, R> InProjectionExpectingComma13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T> comma(String alias, FunctionOptional1<R,T> methodReference, String label) {
        return comma(UnresolvedColumn.of(alias, methodReference), OptionalUtil.ofBlankable(label));
    }

    public <T, R> InProjectionExpectingComma13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T> comma(String alias, FunctionOptional1<R,T> methodReference, Label<T> label) {
        return comma(UnresolvedColumn.of(alias, methodReference), OptionalUtil.ofBlankable(label.label()));
    }

    public <T, R> InProjectionExpectingComma13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T> comma(Alias<R> alias, Function1<R,T> methodReference) {
        return comma(ResolvedColumn.of(alias, methodReference), Optional.empty());
    }

    public <T, R> InProjectionExpectingComma13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T> comma(Alias<R> alias, Function1<R,T> methodReference, String label) {
        return comma(ResolvedColumn.of(alias, methodReference), OptionalUtil.ofBlankable(label));
    }

    public <T, R> InProjectionExpectingComma13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T> comma(Alias<R> alias, Function1<R,T> methodReference, Label<T> label) {
        return comma(ResolvedColumn.of(alias, methodReference), OptionalUtil.ofBlankable(label.label()));
    }

    public <T, R> InProjectionExpectingComma13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T> comma(Alias<R> alias, FunctionOptional1<R,T> methodReference) {
        return comma(ResolvedColumn.of(alias, methodReference), Optional.empty());
    }

    public <T, R> InProjectionExpectingComma13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T> comma(Alias<R> alias, FunctionOptional1<R,T> methodReference, String label) {
        return comma(ResolvedColumn.of(alias, methodReference), OptionalUtil.ofBlankable(label));
    }

    public <T, R> InProjectionExpectingComma13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T> comma(Alias<R> alias, FunctionOptional1<R,T> methodReference, Label<T> label) {
        return comma(ResolvedColumn.of(alias, methodReference), OptionalUtil.ofBlankable(label.label()));
    }

    public <T> InProjectionExpectingComma13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T> comma(Class<T> rowClass) {
        Alias<T> alias = scope().findAlias(rowClass);
        return comma(alias);
    }

    public <T> InProjectionExpectingComma13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T> comma(Class<T> rowClass, String aliasName) {
        Alias<T> alias = scope().findAlias(rowClass, aliasName);
        return comma(alias);
    }

    public <T> InProjectionExpectingComma13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T> comma(Alias<T> alias) {
        SelectStatement<Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T>> select = new SelectStatement<>(
            scope(),
            new TypeToken<Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T>>() {}
                .where(new TypeParameter<T1>() {}, Tuple12.type1(type()))
                .where(new TypeParameter<T2>() {}, Tuple12.type2(type()))
                .where(new TypeParameter<T3>() {}, Tuple12.type3(type()))
                .where(new TypeParameter<T4>() {}, Tuple12.type4(type()))
                .where(new TypeParameter<T5>() {}, Tuple12.type5(type()))
                .where(new TypeParameter<T6>() {}, Tuple12.type6(type()))
                .where(new TypeParameter<T7>() {}, Tuple12.type7(type()))
                .where(new TypeParameter<T8>() {}, Tuple12.type8(type()))
                .where(new TypeParameter<T9>() {}, Tuple12.type9(type()))
                .where(new TypeParameter<T10>() {}, Tuple12.type10(type()))
                .where(new TypeParameter<T11>() {}, Tuple12.type11(type()))
                .where(new TypeParameter<T12>() {}, Tuple12.type12(type()))
                .where(new TypeParameter<T>() {}, boxedType(alias.type())),
            statement.from(),
            Projections.of13(statement.projection(), Projection.of(alias)));
        return new InProjectionExpectingComma13<>(select);
    }

    @NotNull
    private <T> InProjectionExpectingComma13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T> comma(TypedExpression<T> col, Optional<String> label) {
        SelectStatement<Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T>> select = new SelectStatement<>(
            scope(),
            new TypeToken<Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T>>() {}
                .where(new TypeParameter<T1>() {}, Tuple12.type1(type()))
                .where(new TypeParameter<T2>() {}, Tuple12.type2(type()))
                .where(new TypeParameter<T3>() {}, Tuple12.type3(type()))
                .where(new TypeParameter<T4>() {}, Tuple12.type4(type()))
                .where(new TypeParameter<T5>() {}, Tuple12.type5(type()))
                .where(new TypeParameter<T6>() {}, Tuple12.type6(type()))
                .where(new TypeParameter<T7>() {}, Tuple12.type7(type()))
                .where(new TypeParameter<T8>() {}, Tuple12.type8(type()))
                .where(new TypeParameter<T9>() {}, Tuple12.type9(type()))
                .where(new TypeParameter<T10>() {}, Tuple12.type10(type()))
                .where(new TypeParameter<T11>() {}, Tuple12.type11(type()))
                .where(new TypeParameter<T12>() {}, Tuple12.type12(type()))
                .where(new TypeParameter<T>() {}, boxedType(col.type())),
            statement.from(),
            Projections.of13(statement.projection(), Projection.of(col, label)));
        return new InProjectionExpectingComma13<>(select);
    }
}
