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

package com.cadenzauk.siesta.grammar.dml;

import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.TableAlias;
import com.cadenzauk.siesta.catalog.Table;
import com.cadenzauk.siesta.grammar.expression.Assignment;
import com.cadenzauk.siesta.grammar.expression.ParenthesisedArithmeticExpression;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.google.common.collect.Iterables;
import com.google.common.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class Update<U> extends ExecutableStatement {
    private final TableAlias<U> alias;
    private final List<Assignment<?>> sets = new ArrayList<>();

    private Update(Database database, TableAlias<U> alias) {
        super(new Scope(database, alias));
        this.alias = alias;
    }

    protected String sql(Scope scope) {
        return String.format("update %s%s set %s%s",
            alias.table().qualifiedName(),
            alias.aliasName().map(a -> " " + a).orElse(""),
            sets.stream().map(e -> e.sql(scope)).collect(joining(", ")),
            whereClauseSql(scope));
    }

    protected Stream<Object> args(Scope scope) {
        return Stream.concat(
            sets.stream().flatMap(a -> a.args(scope)),
            whereClauseArgs(scope)
        );
    }

    <T> SetExpressionBuilder<U,T> addSet(Assignment<T> expression) {
        sets.add(expression);
        return new SetExpressionBuilder<>(this);
    }

    <T> void plus(TypedExpression<T> value) {
        apply(Assignment::plus, value);
    }

    <T> void minus(TypedExpression<T> value) {
        apply(Assignment::minus, value);
    }

    <T> void times(TypedExpression<T> value) {
        apply(Assignment::times, value);
    }

    <T> void dividedBy(TypedExpression<T> value) {
        apply(Assignment::dividedBy, value);
    }

    private <T> void apply(BiConsumer<Assignment<T>,TypedExpression<T>> fun, TypedExpression<T> value) {
        OptionalUtil.as(new TypeToken<Assignment<T>>() {}, Iterables.getLast(sets))
            .ifPresent(v -> fun.accept(v, ParenthesisedArithmeticExpression.wrapIfNecessary(value)));
    }

    private InSetExpectingWhere<U> setClause() {
        return new InSetExpectingWhere<>(this);
    }

    public static <U> InSetExpectingWhere<U> update(Database database, Table<U> table) {
        return update(database, TableAlias.of(table));
    }

    public static <U> InSetExpectingWhere<U> update(Database database, Alias<U> alias) {
        TableAlias<U> tableAlias = OptionalUtil.as(new TypeToken<TableAlias<U>>() {}, alias)
            .orElseThrow(() -> new IllegalArgumentException("Can only use table aliases in UPDATE."));
        return new Update<>(database, tableAlias).setClause();
    }
}
