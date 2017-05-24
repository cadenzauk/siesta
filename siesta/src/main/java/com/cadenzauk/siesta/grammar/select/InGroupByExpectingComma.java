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
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.grammar.expression.ResolvedColumn;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.grammar.expression.UnresolvedColumn;

public class InGroupByExpectingComma<RT> extends ExpectingHaving<RT> {
    public InGroupByExpectingComma(SelectStatement<RT> statement) {
        super(statement);
    }

    public <T> InGroupByExpectingComma<RT> comma(TypedExpression<T> column) {
        statement.addGroupBy(column);
        return this;
    }

    public <T, R> InGroupByExpectingComma<RT> comma(Function1<R,T> column) {
        statement.addGroupBy(UnresolvedColumn.of(column));
        return this;
    }

    public <T, R> InGroupByExpectingComma<RT> comma(FunctionOptional1<R,T> column) {
        statement.addGroupBy(UnresolvedColumn.of(column));
        return this;
    }

    public <T, R> InGroupByExpectingComma<RT> comma(String alias, Function1<R,T> column) {
        statement.addGroupBy(UnresolvedColumn.of(alias, column));
        return this;
    }

    public <T, R> InGroupByExpectingComma<RT> comma(String alias, FunctionOptional1<R,T> column) {
        statement.addGroupBy(UnresolvedColumn.of(alias, column));
        return this;
    }

    public <T, R> InGroupByExpectingComma<RT> comma(Alias<R> alias, Function1<R,T> column) {
        statement.addGroupBy(ResolvedColumn.of(alias, column));
        return this;
    }

    public <T, R> InGroupByExpectingComma<RT> comma(Alias<R> alias, FunctionOptional1<R,T> column) {
        statement.addGroupBy(ResolvedColumn.of(alias, column));
        return this;
    }
}
