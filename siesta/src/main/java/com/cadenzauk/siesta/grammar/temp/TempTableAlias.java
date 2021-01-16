/*
 * Copyright (c) 2020 Cadenza United Kingdom Limited
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

package com.cadenzauk.siesta.grammar.temp;

import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.core.util.Lazy;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.AliasColumn;
import com.cadenzauk.siesta.ColumnSpecifier;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.ProjectionColumn;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.TableAlias;
import com.cadenzauk.siesta.catalog.Column;
import com.cadenzauk.siesta.catalog.ForeignKeyReference;
import com.google.common.reflect.TypeToken;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class TempTableAlias<R> extends TableAlias<R> {
    private final TempTable<R> tempTable;
    private final Optional<String> aliasName;
    private final Lazy<List<ProjectionColumn<?>>> projectionColumns = new Lazy<>(this::computeProjectionColumns);

    public TempTableAlias(TempTable<R> tempTable, Optional<String> aliasName) {
        this.tempTable = tempTable;
        this.aliasName = aliasName;
    }

    @Override
    public TypeToken<R> type() {
        return tempTable.rowType();
    }

    @Override
    public Database database() {
        return tempTable.database();
    }

    @Override
    public <T> Optional<AliasColumn<T>> findAliasColumn(Scope scope, ColumnSpecifier<T> columnSpecifier) {
        return tempTable
            .columns()
            .flatMap(ac -> ac.as(columnSpecifier.effectiveType()))
            .filter(ac -> columnSpecifier.specifies(scope, ac))
            .findFirst();
    }

    @Override
    public <P> Optional<ForeignKeyReference<R,P>> foreignKey(Alias<P> parent, Optional<String> name) {
        return Optional.empty();
    }

    @Override
    public Stream<ProjectionColumn<?>> projectionColumns(Scope scope) {
        return projectionColumns.get().stream();
    }

    @Override
    public String qualifiedTableName() {
        return tempTable.qualifiedTableName();
    }

    private List<ProjectionColumn<?>> computeProjectionColumns() {
        return tempTable.columns()
            .map(this::projectionColumn)
            .collect(toList());
    }

    private <T> ProjectionColumn<T> projectionColumn(Column<T,R> c) {
        return c.toProjection(this, Optional.empty());
    }


    @Override
    public Stream<Object> args(Scope scope) {
        return Stream.empty();
    }

    @Override
    protected boolean isDual() {
        return false;
    }

    @Override
    public String withoutAlias() {
        return tempTable.qualifiedTableName();
    }

    @Override
    public <T> String columnSql(Scope scope, ColumnSpecifier<T> columnSpecifier) {
        return "";
    }

    @Override
    public <T> String columnSqlWithLabel(Scope scope, ColumnSpecifier<T> columnSpecifier, Optional<String> label) {
        return "";
    }

    @Override
    public String inSelectClauseSql(Scope scope) {
        return tempTable.columnDefinitions()
            .map(c -> c.sqlWithLabel(this, Optional.empty()))
            .collect(joining(", "));
    }

    @Override
    protected String columnLabelPrefix() {
        return aliasName.orElseGet(() -> tempTable.tableName() + "_");
    }

    @Override
    public Optional<String> aliasName() {
        return aliasName;
    }

    @Override
    public RowMapperFactory<R> rowMapperFactory() {
        return tempTable.rowMapperFactory(this);
    }
}
