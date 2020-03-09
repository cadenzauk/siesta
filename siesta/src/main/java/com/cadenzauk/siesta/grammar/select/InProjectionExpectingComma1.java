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

package com.cadenzauk.siesta.grammar.select;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.core.tuple.Tuple2;
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

import java.util.Optional;

import static com.cadenzauk.core.reflect.util.TypeUtil.boxedType;

public class InProjectionExpectingComma1<T1> extends ExpectingWhere<T1> {
    public InProjectionExpectingComma1(SelectStatement<T1> statement) {
        super(statement);
    }

    public <T> InProjectionExpectingComma2<T1,T> comma(TypedExpression<T> expression) {
        return comma(expression, Optional.empty());
    }

    public <T> InProjectionExpectingComma2<T1,T> comma(TypedExpression<T> expression, String label) {
        return comma(expression, OptionalUtil.ofBlankable(label));
    }

    public <T> InProjectionExpectingComma2<T1,T> comma(TypedExpression<T> expression, Label<T> label) {
        return comma(expression, OptionalUtil.ofBlankable(label.label()));
    }

    public <T, R> InProjectionExpectingComma2<T1,T> comma(Function1<R,T> methodReference) {
        return comma(UnresolvedColumn.of(methodReference), Optional.empty());
    }

    public <T, R> InProjectionExpectingComma2<T1,T> comma(Function1<R,T> methodReference, String label) {
        return comma(UnresolvedColumn.of(methodReference), OptionalUtil.ofBlankable(label));
    }

    public <T, R> InProjectionExpectingComma2<T1,T> comma(FunctionOptional1<R,T> methodReference) {
        return comma(UnresolvedColumn.of(methodReference), Optional.empty());
    }

    public <T, R> InProjectionExpectingComma2<T1,T> comma(FunctionOptional1<R,T> methodReference, String label) {
        return comma(UnresolvedColumn.of(methodReference), OptionalUtil.ofBlankable(label));
    }

    public <T, R> InProjectionExpectingComma2<T1,T> comma(String alias, Function1<R,T> methodReference) {
        return comma(UnresolvedColumn.of(alias, methodReference), Optional.empty());
    }

    public <T, R> InProjectionExpectingComma2<T1,T> comma(String alias, Function1<R,T> methodReference, String label) {
        return comma(UnresolvedColumn.of(alias, methodReference), OptionalUtil.ofBlankable(label));
    }

    public <T, R> InProjectionExpectingComma2<T1,T> comma(String alias, FunctionOptional1<R,T> methodReference) {
        return comma(UnresolvedColumn.of(alias, methodReference), Optional.empty());
    }

    public <T, R> InProjectionExpectingComma2<T1,T> comma(String alias, FunctionOptional1<R,T> methodReference, String label) {
        return comma(UnresolvedColumn.of(alias, methodReference), OptionalUtil.ofBlankable(label));
    }

    public <T, R> InProjectionExpectingComma2<T1,T> comma(Alias<R> alias, Function1<R,T> methodReference) {
        return comma(ResolvedColumn.of(alias, methodReference), Optional.empty());
    }

    public <T, R> InProjectionExpectingComma2<T1,T> comma(Alias<R> alias, Function1<R,T> methodReference, String label) {
        return comma(ResolvedColumn.of(alias, methodReference), OptionalUtil.ofBlankable(label));
    }

    public <T, R> InProjectionExpectingComma2<T1,T> comma(Alias<R> alias, FunctionOptional1<R,T> methodReference) {
        return comma(ResolvedColumn.of(alias, methodReference), Optional.empty());
    }

    public <T, R> InProjectionExpectingComma2<T1,T> comma(Alias<R> alias, FunctionOptional1<R,T> methodReference, String label) {
        return comma(ResolvedColumn.of(alias, methodReference), OptionalUtil.ofBlankable(label));
    }

    public <T> InProjectionExpectingComma2<T1,T> comma(Class<T> rowClass) {
        Alias<T> alias = scope().findAlias(rowClass);
        return comma(alias);
    }

    public <T> InProjectionExpectingComma2<T1,T> comma(Class<T> rowClass, String aliasName) {
        Alias<T> alias = scope().findAlias(rowClass, aliasName);
        return comma(alias);
    }

    public <T> InProjectionExpectingComma2<T1,T> comma(Alias<T> alias) {
        SelectStatement<Tuple2<T1,T>> select = new SelectStatement<>(
            scope(),
            new TypeToken<Tuple2<T1,T>>(getClass()) {}
                .where(new TypeParameter<T1>() {}, boxedType(type()))
                .where(new TypeParameter<T>() {}, boxedType(alias.type())),
            statement.from(),
            Projections.of2(statement.projection(), Projection.of(alias)));
        return new InProjectionExpectingComma2<>(select);
    }

    private <T> InProjectionExpectingComma2<T1,T> comma(TypedExpression<T> col, Optional<String> label) {
        SelectStatement<Tuple2<T1,T>> select = new SelectStatement<>(
            scope(),
            new TypeToken<Tuple2<T1,T>>(getClass()) {}
                .where(new TypeParameter<T1>() {}, boxedType(type()))
                .where(new TypeParameter<T>() {}, boxedType(col.type())),
            statement.from(),
            Projections.of2(statement.projection(), Projection.of(col, label)));
        return new InProjectionExpectingComma2<>(select);
    }
}
