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
import com.cadenzauk.core.tuple.Tuple8;
import com.cadenzauk.core.tuple.Tuple9;
import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.RowMappers;
import com.cadenzauk.siesta.grammar.expression.ResolvedColumn;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.grammar.expression.UnresolvedColumn;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class InProjectionExpectingComma8<T1, T2, T3, T4, T5, T6, T7, T8> extends ExpectingWhere<Tuple8<T1,T2,T3,T4,T5,T6,T7,T8>> {
    public InProjectionExpectingComma8(SelectStatement<Tuple8<T1,T2,T3,T4,T5,T6,T7,T8>> statement) {
        super(statement);
    }

    public <T> ExpectingWhere<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T>> comma(TypedExpression<T> expression) {
        return comma(expression, Optional.empty());
    }

    public <T> ExpectingWhere<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T>> comma(TypedExpression<T> expression, String label) {
        return comma(expression, OptionalUtil.ofBlankable(label));
    }

    public <T, R> ExpectingWhere<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T>> comma(Function1<R,T> methodReference) {
        return comma(UnresolvedColumn.of(methodReference), Optional.empty());
    }

    public <T, R> ExpectingWhere<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T>> comma(FunctionOptional1<R,T> methodReference) {
        return comma(UnresolvedColumn.of(methodReference), Optional.empty());
    }

    public <T, R> ExpectingWhere<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T>> comma(Function1<R,T> methodReference, String label) {
        return comma(UnresolvedColumn.of(methodReference), Optional.of(label));
    }

    public <T, R> ExpectingWhere<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T>> comma(FunctionOptional1<R,T> methodReference, String label) {
        return comma(UnresolvedColumn.of(methodReference), Optional.of(label));
    }

    public <T, R> ExpectingWhere<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T>> comma(String alias, Function1<R,T> methodReference) {
        return comma(UnresolvedColumn.of(alias, methodReference), Optional.empty());
    }

    public <T, R> ExpectingWhere<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T>> comma(String alias, FunctionOptional1<R,T> methodReference) {
        return comma(UnresolvedColumn.of(alias, methodReference), Optional.empty());
    }

    public <T, R> ExpectingWhere<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T>> comma(String alias, Function1<R,T> methodReference, String label) {
        return comma(UnresolvedColumn.of(alias, methodReference), Optional.of(label));
    }

    public <T, R> ExpectingWhere<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T>> comma(String alias, FunctionOptional1<R,T> methodReference, String label) {
        return comma(UnresolvedColumn.of(alias, methodReference), Optional.of(label));
    }

    public <T, R> ExpectingWhere<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T>> comma(Alias<R> alias, Function1<R,T> methodReference) {
        return comma(ResolvedColumn.of(alias, methodReference), Optional.empty());
    }

    public <T, R> ExpectingWhere<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T>> comma(Alias<R> alias, FunctionOptional1<R,T> methodReference) {
        return comma(ResolvedColumn.of(alias, methodReference), Optional.empty());
    }

    public <T, R> ExpectingWhere<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T>> comma(Alias<R> alias, Function1<R,T> methodReference, String label) {
        return comma(ResolvedColumn.of(alias, methodReference), Optional.of(label));
    }

    public <T, R> ExpectingWhere<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T>> comma(Alias<R> alias, FunctionOptional1<R,T> methodReference, String label) {
        return comma(ResolvedColumn.of(alias, methodReference), Optional.of(label));
    }

    @NotNull
    private <T> ExpectingWhere<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T>> comma(TypedExpression<T> col, Optional<String> label) {
        SelectStatement<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T>> select = new SelectStatement<>(
            scope(),
            statement.from(),
            RowMappers.add9th(
                statement.rowMapper(),
                col.rowMapper(scope(), label.orElseGet(() -> col.label(scope())))),
            Projection.of(statement.projection(), Projection.of(col, label)));
        return new ExpectingWhere<>(select);
    }
}
