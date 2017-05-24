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
import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.catalog.Column;
import com.cadenzauk.siesta.catalog.Table;
import com.cadenzauk.siesta.grammar.expression.BooleanExpression;
import com.cadenzauk.siesta.grammar.expression.ExpressionBuilder;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.stream.Stream;

public class Alias<R> {
    private final Table<R> table;
    private final String aliasName;

    public Alias(Table<R> table, String aliasName) {
        this.table = table;
        this.aliasName = aliasName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Alias<?> alias = (Alias<?>) o;

        return new EqualsBuilder()
            .append(table.qualifiedName(), alias.table.qualifiedName())
            .append(aliasName, alias.aliasName)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(table.qualifiedName())
            .append(aliasName)
            .toHashCode();
    }

    public boolean isDual() {
        return table.rowClass() == Dual.class;
    }

    String inWhereClause() {
        if (isDual()) {
            return table.database().dialect().dual();
        }
        return String.format("%s %s", table.qualifiedName(), aliasName);
    }

    public String inSelectClauseSql(String columnName) {
        return String.format("%s.%s", aliasName, columnName);
    }

    public String inSelectClauseLabel(String columnName) {
        return String.format("%s_%s", aliasName, columnName);
    }

    public <T> ExpressionBuilder<T,BooleanExpression> column(Function1<R,T> getter) {
        return TypedExpression.column(this, getter);
    }

    public <T> ExpressionBuilder<T,BooleanExpression> column(FunctionOptional1<R,T> getter) {
        return TypedExpression.column(this, getter);
    }

    public Table<R> table() {
        return table;
    }

    public String aliasName() {
        return aliasName;
    }

    public <T> Column<T,R> column(MethodInfo<R,T> methodInfo) {
        return table().column(methodInfo);
    }

    public RowMapper<R> rowMapper() {
        return table.rowMapper(aliasName + "_");
    }

    public DynamicRowMapper<R> dynamicRowMapper() {
        return table.dynamicRowMapper(aliasName + "_");
    }

    @SuppressWarnings("unchecked")
    <R2> Stream<Alias<R2>> as(Class<R2> requiredRowClass, String requiredAlias) {
        if (StringUtils.equals(requiredAlias, aliasName)) {
            if (requiredRowClass.isAssignableFrom(table.rowClass())) {
                return Stream.of((Alias<R2>) this);
            }
            throw new IllegalArgumentException("Alias " + aliasName + " is an alias for " + table().rowClass() + " and not " + requiredRowClass);
        }
        return Stream.empty();
    }

    @SuppressWarnings("unchecked")
    <R2> Stream<Alias<R2>> as(Class<R2> requiredRowClass) {
        if (requiredRowClass.isAssignableFrom(table.rowClass())) {
            return Stream.of((Alias<R2>) this);
        }
        return Stream.empty();
    }
}
