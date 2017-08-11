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

package com.cadenzauk.siesta.grammar.expression;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.catalog.Column;
import com.google.common.reflect.TypeToken;

import java.util.Optional;
import java.util.stream.Stream;

public class UnresolvedColumn<T,R> implements TypedExpression<T> {
    private final Optional<String> alias;
    private final MethodInfo<R,T> getterMethod;

    private UnresolvedColumn(MethodInfo<R,T> getterMethod) {
        this.getterMethod = getterMethod;
        this.alias = Optional.empty();
    }

    private UnresolvedColumn(String alias, MethodInfo<R,T> getterMethod) {
        this.alias = Optional.of(alias);
        this.getterMethod = getterMethod;
    }

    @Override
    public String sql(Scope scope) {
        return resolve(scope).inSelectClauseSql(columnName(scope));
    }

    public String columnName(Scope scope) {
        Column<T,R> column = scope.database().column(getterMethod);
        return column.name();
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return Stream.empty();
    }

    @Override
    public Precedence precedence() {
        return Precedence.COLUMN;
    }

    @Override
    public String label(Scope scope) {
        Column<T,R> column = scope.database().column(getterMethod);
        return resolve(scope).inSelectClauseLabel(column.name());
    }

    @SuppressWarnings("unchecked")
    @Override
    public RowMapper<T> rowMapper(Scope scope, String label) {
        Column<T,R> column = scope.database().column(getterMethod);
        return column.rowMapper(scope.database(), label);
    }

    @Override
    public TypeToken<T> type() {
        return TypeToken.of(getterMethod.effectiveType());
    }

    @SuppressWarnings("unchecked")
    private Alias<R> resolve(Scope scope) {
        Class<R> rowClass = getterMethod.declaringClass();
        return this.alias
            .map(a -> scope.findAlias(rowClass, a))
            .orElseGet(() -> scope.findAlias(rowClass));
    }

    public static <T, R> UnresolvedColumn<T,R> of(Function1<R,T> getter) {
        MethodInfo<R,T> method = MethodInfo.of(getter);
        return new UnresolvedColumn<>(method);
    }

    public static <T, R> UnresolvedColumn<T,R> of(FunctionOptional1<R,T> getter) {
        MethodInfo<R,T> method = MethodInfo.of(getter);
        return new UnresolvedColumn<>(method);
    }

    public static <T, R> UnresolvedColumn<T,R> of(String alias, Function1<R,T> getter) {
        MethodInfo<R,T> method = MethodInfo.of(getter);
        return new UnresolvedColumn<>(alias, method);
    }

    public static <T, R> TypedExpression<T> of(String alias, FunctionOptional1<R,T> getter) {
        MethodInfo<R,T> method = MethodInfo.of(getter);
        return new UnresolvedColumn<>(alias, method);
    }
}
