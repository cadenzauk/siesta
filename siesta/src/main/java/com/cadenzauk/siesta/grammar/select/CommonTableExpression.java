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

import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.AliasColumn;
import com.cadenzauk.siesta.ColumnSpecifier;
import com.cadenzauk.siesta.CteAlias;
import com.cadenzauk.siesta.ProjectionColumn;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.catalog.Table;
import com.google.common.reflect.TypeToken;

import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class CommonTableExpression<RT> {
    private final Table<RT> table;
    private final String name;
    private final SelectStatement<RT> select;

    CommonTableExpression(Table<RT> table, String name, SelectStatement<RT> select) {
        this.table = table;
        this.name = name;
        this.select = select;
    }

    public String sql(Scope scope) {
        Scope actualScope = scope.plus(select.scope());
        return String.format("%s(%s) as %s ",
            name,
            select.projection().resultingColumnNames(actualScope).collect(joining(", ")),
            select.sql(actualScope));
    }

    public Stream<Object> args(Scope scope) {
        Scope actualScope = scope.plus(select.scope());
        return select.args(actualScope);
    }

    public Alias<RT> as(String alias) {
        return new CteAlias<>(this, Optional.of(alias));
    }

    public Alias<RT> asAlias() {
        return new CteAlias<>(this, Optional.empty());
    }

    public String name() {
        return name;
    }

    public Table<RT> table() {
        return table;
    }

    public <T> Optional<AliasColumn<T>> findColumn(ColumnSpecifier<T> columnSpecifier) {
        return select.projection().findColumn(select.scope, columnSpecifier)
            .map(x -> new AliasColumn<T>() {
                @Override
                public TypeToken<T> type() {
                    return x.type();
                }

                @Override
                public String propertyName() {
                    return select.scope.database().namingStrategy().propertyName(x.label());
                }

                @Override
                public String columnName() {
                    return x.columnName();
                }

                @Override
                public RowMapperFactory<T> rowMapperFactory(Alias<?> alias, Optional<String> defaultLabel) {
                    return x.rowMapperFactory().withDefaultLabel(defaultLabel);
                }

                @Override
                public String sql() {
                    return columnName();
                }

                @Override
                public String sql(Alias<?> alias) {
                    return alias.inSelectClauseSql(columnName());
                }

                @Override
                public String sqlWithLabel(Alias<?> alias, Optional<String> label) {
                    return alias.inSelectClauseSql(columnName(), label);
                }

                @Override
                public ProjectionColumn<T> toProjection(Alias<?> alias, Optional<String> label) {
                    return null;
                }

                @Override
                public <V> Optional<AliasColumn<V>> findColumn(TypeToken<V> type, String propertyName) {
                    return Optional.empty();
                }
            });
    }

    public Stream<CommonTableExpression<?>> commonTableExpressions() {
        return Stream.concat(select.commonTableExpressions(), Stream.of(this));
    }
}
