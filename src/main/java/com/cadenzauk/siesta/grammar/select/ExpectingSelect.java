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
import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.core.tuple.Tuple3;
import com.cadenzauk.core.tuple.Tuple4;
import com.cadenzauk.core.tuple.Tuple5;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.RowMappers;
import com.cadenzauk.siesta.expression.ResolvedColumn;
import com.cadenzauk.siesta.expression.TypedExpression;
import com.cadenzauk.siesta.expression.UnresolvedColumn;

public class ExpectingSelect<RT> extends ExpectingWhere<RT> {
    public ExpectingSelect(SelectStatement<RT> select) {
        super(select);
    }

    public <T> ExpectingWhere<T> select(TypedExpression<T> column) {
        return new Select<>(scope(),
            statement.from(),
            column.rowMapper(scope(), column.label(scope())),
            Projection.of(column))
            .expectingWhere();
    }

    public <T1, T2> ExpectingWhere<Tuple2<T1,T2>> select(TypedExpression<T1> column1, TypedExpression<T2> column2) {
        return new Select<>(scope(),
            statement.from(),
            RowMappers.of(
                column1.rowMapper(scope(), column1.label(scope())),
                column2.rowMapper(scope(), column2.label(scope()))),
            Projection.of(Projection.of(column1), Projection.of(column2)))
            .expectingWhere();
    }

    public <T1, T2, T3> ExpectingWhere<Tuple3<T1,T2,T3>> select(TypedExpression<T1> column1, TypedExpression<T2> column2, TypedExpression<T3> column3) {
        return new Select<>(scope(),
            statement.from(),
            RowMappers.of(
                column1.rowMapper(scope(), column1.label(scope())),
                column2.rowMapper(scope(), column2.label(scope())),
                column3.rowMapper(scope(), column3.label(scope()))),
            Projection.of(Projection.of(column1), Projection.of(column2), Projection.of(column3)))
            .expectingWhere();
    }

    public <T1, T2, T3, T4> ExpectingWhere<Tuple4<T1,T2,T3,T4>> select(TypedExpression<T1> column1, TypedExpression<T2> column2, TypedExpression<T3> column3, TypedExpression<T4> column4) {
        return new Select<>(scope(),
            statement.from(),
            RowMappers.of(
                column1.rowMapper(scope(), column1.label(scope())),
                column2.rowMapper(scope(), column2.label(scope())),
                column3.rowMapper(scope(), column3.label(scope())),
                column4.rowMapper(scope(), column4.label(scope()))),
            Projection.of(Projection.of(column1), Projection.of(column2), Projection.of(column4), Projection.of(column4)))
            .expectingWhere();
    }

    public <T1, T2, T3, T4, T5> ExpectingWhere<Tuple5<T1,T2,T3,T4,T5>> select(TypedExpression<T1> column1, TypedExpression<T2> column2, TypedExpression<T3> column3, TypedExpression<T4> column4, TypedExpression<T5> column5) {
        return new Select<>(scope(),
            statement.from(),
            RowMappers.of(
                column1.rowMapper(scope(), column1.label(scope())),
                column2.rowMapper(scope(), column2.label(scope())),
                column3.rowMapper(scope(), column3.label(scope())),
                column4.rowMapper(scope(), column4.label(scope())),
                column5.rowMapper(scope(), column5.label(scope()))),
            Projection.of(Projection.of(column1), Projection.of(column2), Projection.of(column4), Projection.of(column4)))
            .expectingWhere();
    }

    public <T, R> ExpectingWhere<T> select(Function1<R,T> methodReference) {
        return select(UnresolvedColumn.of(methodReference));
    }

    public <T, R> ExpectingWhere<T> select(Alias<R> alias, Function1<R,T> methodReference) {
        return select(ResolvedColumn.of(alias, methodReference));
    }
}
