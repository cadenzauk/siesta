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
import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.DynamicRowMapper;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.catalog.Table;
import com.cadenzauk.siesta.grammar.expression.ResolvedColumn;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.grammar.expression.UnresolvedColumn;
import com.cadenzauk.siesta.projection.DynamicProjection;

import java.util.Optional;

public abstract class ExpectingSelect<RT> extends ExpectingWhere<RT> {
    protected ExpectingSelect(SelectStatement<RT> statement) {
        super(statement);
    }

    public <T> InProjectionExpectingComma1<T> select(TypedExpression<T> expression) {
        return select(expression, Optional.empty());
    }

    public <T> InProjectionExpectingComma1<T> select(TypedExpression<T> expression, String label) {
        return select(expression, OptionalUtil.ofBlankable(label));
    }

    public <T, R> InProjectionExpectingComma1<T> select(Function1<R,T> methodReference) {
        return select(UnresolvedColumn.of(methodReference));
    }

    public <T, R> InProjectionExpectingComma1<T> select(FunctionOptional1<R, T> methodReference) {
        return select(UnresolvedColumn.of(methodReference));
    }

    public <T, R> InProjectionExpectingComma1<T> select(String alias, Function1<R,T> methodReference) {
        return select(UnresolvedColumn.of(alias, methodReference));
    }

    public <T, R> InProjectionExpectingComma1<T> select(String alias, FunctionOptional1<R,T> methodReference) {
        return select(UnresolvedColumn.of(alias, methodReference));
    }

    public <T, R> InProjectionExpectingComma1<T> select(Alias<R> alias, Function1<R,T> methodReference) {
        return select(ResolvedColumn.of(alias, methodReference));
    }

    public <T, R> InProjectionExpectingComma1<T> select(Alias<R> alias, FunctionOptional1<R,T> methodReference) {
        return select(ResolvedColumn.of(alias, methodReference));
    }

    public <T, R> InProjectionExpectingComma1<T> select(Function1<R,T> methodReference, String label) {
        return select(UnresolvedColumn.of(methodReference), label);
    }

    public <T, R> InProjectionExpectingComma1<T> select(FunctionOptional1<R,T> methodReference, String label) {
        return select(UnresolvedColumn.of(methodReference), label);
    }

    public <T, R> InProjectionExpectingComma1<T> select(String alias, Function1<R,T> methodReference, String label) {
        return select(UnresolvedColumn.of(alias, methodReference), label);
    }

    public <T, R> InProjectionExpectingComma1<T> select(String alias, FunctionOptional1<R,T> methodReference, String label) {
        return select(UnresolvedColumn.of(alias, methodReference), label);
    }

    public <T, R> InProjectionExpectingComma1<T> select(Alias<R> alias, Function1<R,T> methodReference, String label) {
        return select(ResolvedColumn.of(alias, methodReference), label);
    }

    public <T, R> InProjectionExpectingComma1<T> select(Alias<R> alias, FunctionOptional1<R,T> methodReference, String label) {
        return select(ResolvedColumn.of(alias, methodReference), label);
    }

    public <R> InProjectionExpectingComma1<R> select(Class<R> rowClass) {
        Alias<R> alias = scope().findAlias(rowClass);
        return select(alias);
    }

    public <R> InProjectionExpectingComma1<R> select(Class<R> rowClass, String aliasName) {
        Alias<R> alias = scope().findAlias(rowClass, aliasName);
        return select(alias);
    }

    public <R> InProjectionExpectingComma1<R> select(Alias<R> alias) {
        SelectStatement<R> select = new SelectStatement<>(scope(),
            alias.type(),
            statement.from(),
            alias.rowMapper(),
            Projection.of(alias));
        return new InProjectionExpectingComma1<>(select);
    }

    public <R> InSelectIntoExpectingWith<R> selectInto(Class<R> rowClass) {
        Table<R> table = database().table(rowClass);
        return selectInto(table.as(table.tableName()));
    }

    public <R> InSelectIntoExpectingWith<R> selectInto(Class<R> rowClass, String alias) {
        return selectInto(database().table(rowClass).as(alias));
    }

    public <R> InSelectIntoExpectingWith<R> selectInto(Alias<R> alias) {
        DynamicRowMapper<R> rowMapper = alias.dynamicRowMapper();
        DynamicProjection projection = new DynamicProjection();
        SelectStatement<R> select = new SelectStatement<>(scope().plus(alias),
            alias.type(),
            statement.from(),
            rowMapper,
            projection);
        return new InSelectIntoExpectingWith<>(select, rowMapper, projection);
    }

    private <T> InProjectionExpectingComma1<T> select(TypedExpression<T> column, Optional<String> label) {
        SelectStatement<T> select = new SelectStatement<>(scope(),
            column.type(),
            statement.from(),
            column.rowMapper(scope(), label),
            Projection.of(column, label));
        return new InProjectionExpectingComma1<>(select);
    }
}
