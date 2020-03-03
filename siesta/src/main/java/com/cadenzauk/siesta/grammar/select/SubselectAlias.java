/*
 * Copyright (c) 2019 Cadenza United Kingdom Limited
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

import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.ProjectionColumn;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.catalog.Column;
import com.cadenzauk.siesta.catalog.ForeignKeyReference;
import com.google.common.reflect.TypeToken;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cadenzauk.core.util.OptionalUtil.or;

public class SubselectAlias<T> extends Alias<T> {
    private final Select<T> select;
    private final String aliasName;

    public SubselectAlias(Select<T> select, String aliasName) {
        this.select = select;
        this.aliasName = aliasName;
    }

    @Override
    public TypeToken<T> type() {
        return select.type();
    }

    @Override
    public Database database() {
        return select.database();
    }

    @Override
    public <T1> Optional<ProjectionColumn<T1>> findColumn(MethodInfo<?,T1> method) {
        return select.findColumn(method);
    }

    @Override
    public <P> Optional<ForeignKeyReference<T,P>> foreignKey(Alias<P> parent, Optional<String> name) {
        return Optional.empty();
    }

    @Override
    public Stream<ProjectionColumn<?>> projectionColumns(Scope scope) {
        return select.projectionColumns(scope)
            .map(c -> new ProjectionColumn<>(c.label(), inSelectClauseLabel(c.label()), label -> c.rowMapperFactory().rowMapper(label)));
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return select.args(scope);
    }

    @Override
    protected boolean isDual() {
        return false;
    }

    @Override
    public String withoutAlias() {
        return select.sql();
    }

    @Override
    public String inFromClauseSql() {
        return String.format("(%s) %s", select.sql(), aliasName);
    }

    @Override
    public <T1> String columnSql(MethodInfo<?,T1> getterMethod) {
        ProjectionColumn<T1> projectionColumn = select.findColumn(getterMethod)
            .orElseThrow(IllegalArgumentException::new);
        return inSelectClauseSql(projectionColumn.label());
    }

    @Override
    public <T1> String columnSqlWithLabel(MethodInfo<?,T1> getterMethod, Optional<String> label) {
        ProjectionColumn<T1> projectionColumn = select.findColumn(getterMethod)
            .orElseThrow(IllegalArgumentException::new);
        return inSelectClauseSql(projectionColumn.label(), label);
    }

    @Override
    public String inSelectClauseSql(Scope scope) {
        return projectionColumns(scope)
            .map(c -> String.format("%s %s", inSelectClauseSql(c.columnSql()), c.label()))
            .collect(Collectors.joining(", "));
    }

    @Override
    public String inSelectClauseSql(String columnName) {
        return String.format("%s.%s", aliasName, columnName);
    }

    @Override
    public String inSelectClauseSql(String columnName, Optional<String> label) {
        return String.format("%s.%s %s", aliasName, columnName, label.orElseGet(() -> inSelectClauseLabel(columnName)));
    }

    @Override
    protected String columnLabelPrefix() {
        return aliasName;
    }

    @Override
    public Optional<String> aliasName() {
        return Optional.of(aliasName);
    }

    @Override
    public RowMapperFactory<T> rowMapperFactory() {
        return label -> select.rowMapperFactory().rowMapper(or(label, Optional.of(aliasName + "_")));
    }

    @Override
    public <T1> RowMapperFactory<T1> rowMapperFactoryFor(MethodInfo<?,T1> getterMethod, Optional<String> defaultLabel) {
        ProjectionColumn<T1> projectionColumn = select.findColumn(getterMethod)
            .orElseThrow(IllegalArgumentException::new);
        return label -> projectionColumn.rowMapperFactory().rowMapper(or(or(label, defaultLabel), Optional.of(aliasName + "_" + projectionColumn.label())));
    }

    @Override
    public Stream<Alias<?>> as(MethodInfo<?,?> getter, Optional<String> requiredAlias) {
        if (requiredAlias.isPresent()) {
            if (requiredAlias.equals(aliasName())) {
                if (select.projectionIncludes(getter)) {
                    return Stream.of(this);
                }
                throw new IllegalArgumentException("Alias " + columnLabelPrefix() + " is an alias for " + type() + " and not " + getter.referringClass());
            } else {
                return Stream.empty();
            }
        }
        if (select.projectionIncludes(getter)) {
            return Stream.of(this);
        }
        return Stream.empty();
    }
}
