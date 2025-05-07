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

package com.cadenzauk.siesta;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.siesta.catalog.ForeignKeyReference;
import com.cadenzauk.siesta.grammar.expression.BooleanExpression;
import com.cadenzauk.siesta.grammar.expression.ExpressionBuilder;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.google.common.reflect.TypeToken;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class Alias<R> {
    public abstract TypeToken<R> type();

    public abstract Database database();

    public abstract <T> Optional<AliasColumn<T>> findAliasColumn(Scope scope, ColumnSpecifier<T> columnSpecifier);

    public abstract <P> Optional<ForeignKeyReference<R, P>> foreignKey(Alias<P> parent, Optional<String> name);

    public abstract Stream<ProjectionColumn<?>> projectionColumns(Scope scope);

    public abstract Stream<Object> args(Scope scope);

    protected abstract boolean isDual();

    public abstract String withoutAlias();

    public abstract String inFromClauseSql();

    public abstract <T> String columnSql(Scope scope, ColumnSpecifier<T> columnSpecifier);

    public abstract <T> String columnSqlWithLabel(Scope scope, ColumnSpecifier<T> columnSpecifier, Optional<String> label);

    public abstract String inSelectClauseSql(Scope scope);

    public abstract String inSelectClauseSql(String columnName);

    public abstract String inSelectClauseSql(String columnName, Optional<String> label);

    public String inSelectClauseLabel(String columnName) {
        return String.format("%s_%s", columnLabelPrefix(), columnName);
    }

    protected abstract String columnLabelPrefix();

    public <T> ExpressionBuilder<T,BooleanExpression> column(Function1<R,T> getter) {
        return TypedExpression.column(this, getter);
    }

    public <T> ExpressionBuilder<T,BooleanExpression> column(FunctionOptional1<R,T> getter) {
        return TypedExpression.column(this, getter);
    }

    public abstract Optional<String> aliasName();

    public abstract RowMapperFactory<R> rowMapperFactory();

    public abstract Stream<Alias<?>> as(Scope scope, ColumnSpecifier<?> columnSpecifier, Optional<String> requiredAlias);

    @SuppressWarnings("unchecked")
    protected <R2> Stream<Alias<R2>> as(Class<R2> requiredRowClass, String requiredAlias) {
        if (Objects.equals(Optional.of(requiredAlias), aliasName())) {
            if (requiredRowClass.isAssignableFrom(type().getRawType())) {
                return Stream.of((Alias<R2>) this);
            }
            throw new InvalidQueryException("Alias " + columnLabelPrefix() + " is an alias for " + type() + " and not " + requiredRowClass + ".");
        }
        return Stream.empty();
    }

    @SuppressWarnings("unchecked")
    protected <R2> Stream<Alias<R2>> as(Class<R2> requiredRowClass) {
        if (requiredRowClass.isAssignableFrom(type().getRawType())) {
            return Stream.of((Alias<R2>) this);
        }
        return Stream.empty();
    }
}
