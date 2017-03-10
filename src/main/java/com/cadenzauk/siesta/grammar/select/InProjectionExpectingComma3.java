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
import com.cadenzauk.core.tuple.Tuple3;
import com.cadenzauk.core.tuple.Tuple4;
import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.RowMappers;
import com.cadenzauk.siesta.expression.TypedExpression;
import com.cadenzauk.siesta.expression.UnresolvedColumn;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class InProjectionExpectingComma3<T1, T2, T3> extends ExpectingWhere<Tuple3<T1,T2,T3>> {
    public InProjectionExpectingComma3(Select<Tuple3<T1,T2,T3>> select) {
        super(select);
    }

    public <T> ExpectingWhere<Tuple4<T1,T2,T3,T>> comma(TypedExpression<T> expression) {
        return comma(expression, Optional.empty());
    }

    public <T> ExpectingWhere<Tuple4<T1,T2,T3,T>> comma(TypedExpression<T> expression, String label) {
        return comma(expression, OptionalUtil.ofBlankable(label));
    }

    public <T, R> ExpectingWhere<Tuple4<T1,T2,T3,T>> comma(Function1<R,T> methodReference) {
        return comma(UnresolvedColumn.of(methodReference), Optional.empty());
    }

    public <T, R> ExpectingWhere<Tuple4<T1,T2,T3,T>> comma(FunctionOptional1<R,T> methodReference) {
        return comma(UnresolvedColumn.of(methodReference), Optional.empty());
    }

    public <T, R> ExpectingWhere<Tuple4<T1,T2,T3,T>> comma(Function1<R,T> methodReference, String label) {
        return comma(UnresolvedColumn.of(methodReference), Optional.of(label));
    }

    public <T, R> ExpectingWhere<Tuple4<T1,T2,T3,T>> comma(FunctionOptional1<R,T> methodReference, String label) {
        return comma(UnresolvedColumn.of(methodReference), Optional.of(label));
    }

    @NotNull
    private <T> ExpectingWhere<Tuple4<T1,T2,T3,T>> comma(TypedExpression<T> col, Optional<String> label) {
        Select<Tuple4<T1,T2,T3,T>> select = new Select<>(
            scope(),
            statement.from(),
            RowMappers.add4th(
                statement.rowMapper(),
                col.rowMapper(scope(), label.orElseGet(() -> col.label(scope())))),
            Projection.of(statement.projection(), Projection.of(col, label)));
        return new ExpectingWhere<>(select);
    }
}
