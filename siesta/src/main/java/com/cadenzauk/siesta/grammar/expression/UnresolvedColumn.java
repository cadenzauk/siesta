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
import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.ColumnSpecifier;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.catalog.Column;
import com.google.common.reflect.TypeToken;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class UnresolvedColumn<T> implements ColumnExpression<T> {
    private final Optional<String> alias;
    private final ColumnSpecifier<T> columnSpec;

    private UnresolvedColumn(MethodInfo<?,T> getterMethod) {
        this.alias = Optional.empty();
        columnSpec = ColumnSpecifier.of(getterMethod);
    }

    private UnresolvedColumn(String alias, MethodInfo<?,T> getterMethod) {
        this.alias = Optional.of(alias);
        columnSpec = ColumnSpecifier.of(getterMethod);
    }

    private UnresolvedColumn(Class<T> effectiveClass, String name) {
        this.alias = Optional.empty();
        columnSpec = ColumnSpecifier.of(effectiveClass, name);
    }

    private UnresolvedColumn(String alias, Class<T> effectiveClass, String name) {
        this.alias = Optional.of(alias);
        columnSpec = ColumnSpecifier.of(effectiveClass, name);
    }

    @Override
    public String sql(Scope scope) {
        Alias<?> resolvedAlias = resolve(scope);
        return resolvedAlias.columnSql(scope, columnSpec);
    }

    @Override
    public String sqlWithLabel(Scope scope, Optional<String> label) {
        Alias<?> resolvedAlias = resolve(scope);
        return resolvedAlias.columnSqlWithLabel(columnSpec, label);
    }

    @Override
    public String columnName(Scope scope) {
        return columnSpec.columnName(scope);
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
        String columnName = columnSpec.columnName(scope);
        return resolve(scope).inSelectClauseLabel(columnName);
    }

    @Override
    public RowMapperFactory<T> rowMapperFactory(Scope scope) {
        Alias<?> resolvedAlias = resolve(scope);
        return resolvedAlias.rowMapperFactoryFor(columnSpec, Optional.empty());
    }

    @Override
    public RowMapperFactory<T> rowMapperFactory(Scope scope, Optional<String> defaultLabel) {
        Alias<?> resolvedAlias = resolve(scope);
        return resolvedAlias.rowMapperFactoryFor(columnSpec, defaultLabel);
    }

    @Override
    public TypeToken<T> type() {
        return TypeToken.of(columnSpec.effectiveClass());
    }

    @Override
    public Alias<?> resolve(Scope scope) {
        return scope.findAlias(columnSpec, alias);
    }

    @Override
    public <V> Optional<Column<V,T>> findColumn(Scope scope, TypeToken<V> type, String propertyName) {
        return columnSpec
            .column(scope)
            .flatMap(c -> c.findColumn(type, propertyName));
    }

    @Override
    public <X> boolean includes(ColumnSpecifier<X> columnSpecifier) {
        return Objects.equals(columnSpec, columnSpecifier);
    }

    public static <T, R> UnresolvedColumn<T> of(Function1<R,T> getter) {
        MethodInfo<R,T> method = MethodInfo.of(getter);
        return new UnresolvedColumn<>(method);
    }

    public static <T, R> UnresolvedColumn<T> of(FunctionOptional1<R,T> getter) {
        MethodInfo<R,T> method = MethodInfo.of(getter);
        return new UnresolvedColumn<>(method);
    }

    public static <T, R> UnresolvedColumn<T> of(String alias, Function1<R,T> getter) {
        MethodInfo<R,T> method = MethodInfo.of(getter);
        return new UnresolvedColumn<>(alias, method);
    }

    public static <T, R> UnresolvedColumn<T> of(String alias, FunctionOptional1<R,T> getter) {
        MethodInfo<R,T> method = MethodInfo.of(getter);
        return new UnresolvedColumn<>(alias, method);
    }

    public static <T> UnresolvedColumn<T> of(Label<T> label) {
        return new UnresolvedColumn<>(label.effectiveClass(), label.label());
    }

    public static <T> UnresolvedColumn<T> of(String alias, Label<T> label) {
        return new UnresolvedColumn<>(alias, label.effectiveClass(), label.label());
    }
}
