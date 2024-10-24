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
import com.cadenzauk.core.util.Lazy;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.AliasColumn;
import com.cadenzauk.siesta.ColumnSpecifier;
import com.cadenzauk.siesta.InvalidQueryException;
import com.cadenzauk.siesta.ProjectionColumn;
import com.cadenzauk.siesta.Scope;
import com.google.common.reflect.TypeToken;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class ResolvedColumn<T, R> implements ColumnExpression<T> {
    private final Alias<R> alias;
    private final TypeToken<T> type;
    private final ColumnSpecifier<T> columnSpec;
    private final Lazy<AliasColumn<T>> resolvedColumn = new Lazy<>();

    private ResolvedColumn(Alias<R> alias, MethodInfo<R,T> getterMethod) {
        this.alias = alias;
        this.type = TypeToken.of(getterMethod.effectiveClass());
        columnSpec = ColumnSpecifier.of(getterMethod);
    }

    private ResolvedColumn(Alias<R> alias, Class<T> type, ColumnSpecifier<T> columnSpec) {
        this.alias = alias;
        this.type = TypeToken.of(type);
        this.columnSpec = columnSpec;
    }

    @Override
    public String toString() {
        return columnSpec.toString();
    }

    @Override
    public String sql(Scope scope) {
        scope.findAlias(alias.type().getRawType(), alias.aliasName().orElse(""));
        return alias.columnSql(scope, columnSpec);
    }

    @Override
    public Stream<Object> args(Scope args) {
        return Stream.empty();
    }

    @Override
    public Precedence precedence() {
        return Precedence.COLUMN;
    }

    @Override
    public String label(Scope scope) {
        String columnName = columnName(scope);
        return alias.inSelectClauseLabel(columnName);
    }

    @Override
    public RowMapperFactory<T> rowMapperFactory(Scope scope) {
        return column(scope).rowMapperFactory(alias, Optional.empty());
    }

    @Override
    public ProjectionColumn<T> toProjectionColumn(Scope scope, Optional<String> label) {
        return column(scope).toProjection(alias, label);
    }

    @Override
    public RowMapperFactory<T> rowMapperFactory(Scope scope, Optional<String> defaultLabel) {
        return column(scope).rowMapperFactory(alias, defaultLabel);
    }

    @Override
    public TypeToken<T> type() {
        return type;
    }

    @Override
    public String sqlWithLabel(Scope scope, Optional<String> label) {
        return alias.columnSqlWithLabel(scope, columnSpec, label);
    }

    @Override
    public String columnName(Scope scope) {
        return column(scope).columnName();
    }

    @Override
    public Alias<?> resolve(Scope scope) {
        return alias;
    }

    @Override
    public <V> Optional<AliasColumn<V>> findColumn(Scope scope, TypeToken<V> type, String propertyName) {
        return column(scope).findColumn(type, propertyName);
    }

    public AliasColumn<T> column(Scope scope) {
        return resolvedColumn.getOrCompute(() ->
            alias.findAliasColumn(scope, columnSpec)
                .orElseThrow(() -> new InvalidQueryException("There is no column for " + columnSpec + " in " + alias.inFromClauseSql())));
    }

    @Override
    public <X> boolean includes(ColumnSpecifier<X> columnSpecifier) {
        return Objects.equals(columnSpec, columnSpecifier);
    }

    public static <T, R> ResolvedColumn<T,R> of(Alias<R> alias, Function1<R,T> getterReference) {
        MethodInfo<R,T> method = MethodInfo.of(getterReference);
        return new ResolvedColumn<>(alias, method);
    }

    public static <T, R> ResolvedColumn<T,R> of(Alias<R> alias, FunctionOptional1<R,T> getterReference) {
        MethodInfo<R,T> method = MethodInfo.of(getterReference);
        return new ResolvedColumn<>(alias, method);
    }

    public static <R> ResolvedColumn<?,R> of(Alias<R> alias, String columnName) {
        return new ResolvedColumn<>(alias, Object.class, ColumnSpecifier.of(Object.class, columnName));
    }
}
