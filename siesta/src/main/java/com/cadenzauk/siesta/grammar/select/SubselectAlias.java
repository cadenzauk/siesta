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

import com.cadenzauk.core.function.ThrowingSupplier;
import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.core.util.Lazy;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.AliasColumn;
import com.cadenzauk.siesta.ColumnSpecifier;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.ProjectionColumn;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.catalog.ForeignKeyReference;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SubselectAlias<T> extends Alias<T> {
    private final Select<T> select;
    private final String aliasName;
    private final Lazy<List<ProjectionColumn<?>>> projectionColumns = new Lazy<>();

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
    public <T1> Optional<AliasColumn<T1>> findAliasColumn(Scope scope, ColumnSpecifier<T1> columnSpecifier) {
        return select.findColumn(columnSpecifier)
            .map(column -> new SubselectAliasColumn<>(scope, column));
    }

    private <T1> Optional<ProjectionColumn<T1>> findProjectionColumn(Scope scope, ColumnSpecifier<T1> columnSpecifier) {
        return projectionColumns(scope)
            .flatMap(pc -> pc.as(columnSpecifier.effectiveType()))
            .filter(pc -> columnSpecifier.specifies(scope, pc))
            .findAny();
    }

    @Override
    public <P> Optional<ForeignKeyReference<T,P>> foreignKey(Alias<P> parent, Optional<String> name) {
        return Optional.empty();
    }

    @Override
    public Stream<ProjectionColumn<?>> projectionColumns(Scope scope) {
        return projectionColumns.getOrCompute(computeProjectionColumns(scope)).stream();
    }

    private ThrowingSupplier<List<ProjectionColumn<?>>,RuntimeException> computeProjectionColumns(Scope scope) {
        return () -> select.projectionColumns(scope)
            .map(c -> ProjectionColumn.usingLabelAsColumnName(this, c, Optional.empty()))
            .collect(Collectors.toList());
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
    public <T1> String columnSql(Scope scope, ColumnSpecifier<T1> columnSpecifier) {
        ProjectionColumn<T1> projectionColumn = findProjectionColumn(scope, columnSpecifier)
            .orElseThrow(IllegalArgumentException::new);
        return inSelectClauseSql(projectionColumn.columnName());
    }

    @Override
    public <T1> String columnSqlWithLabel(Scope scope, ColumnSpecifier<T1> columnSpecifier, Optional<String> label) {
        ProjectionColumn<T1> projectionColumn = findProjectionColumn(scope, columnSpecifier)
            .orElseThrow(IllegalArgumentException::new);
        return inSelectClauseSql(projectionColumn.columnName(), label);
    }

    @Override
    public String inSelectClauseSql(Scope scope) {
        return projectionColumns(scope)
            .map(ProjectionColumn::columnSql)
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
        return select.rowMapperFactory().withPrefix(aliasName + "_");
    }

    @Override
    public Stream<Alias<?>> as(Scope scope, ColumnSpecifier<?> columnSpecifier, Optional<String> requiredAlias) {
        if (requiredAlias.isPresent()) {
            if (requiredAlias.equals(aliasName())) {
                if (select.projectionIncludes(columnSpecifier)) {
                    return Stream.of(this);
                }
                throw new IllegalArgumentException("Alias " + columnLabelPrefix() + " is an alias for " + type() + " and not " + columnSpecifier.referringClass().map(Object::toString).orElse("N/A"));
            } else {
                return Stream.empty();
            }
        }
        if (select.projectionIncludes(columnSpecifier)) {
            return Stream.of(this);
        }
        return Stream.empty();
    }

    private static class SubselectAliasColumn<T1> implements AliasColumn<T1> {
        private final Scope scope;
        private final ProjectionColumn<T1> column;

        public SubselectAliasColumn(Scope scope, ProjectionColumn<T1> column) {
            this.scope = scope;
            this.column = column;
        }

        @Override
        public TypeToken<T1> type() {
            return column.type();
        }

        @Override
        public String propertyName() {
            return column.propertyName();
        }

        @Override
        public String columnName() {
            return column.label();
        }

        @Override
        public RowMapperFactory<T1> rowMapperFactory(Alias<?> alias, Optional<String> defaultLabel) {
            return column.rowMapperFactory().withDefaultLabel(defaultLabel);
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
        public ProjectionColumn<T1> toProjection(Alias<?> alias, Optional<String> label) {
            return ProjectionColumn.usingLabelAsColumnName(alias, column, label);
        }

        @Override
        public <V> Optional<AliasColumn<V>> findColumn(TypeToken<V> type, String propertyName) {
            return column
                .components()
                .stream()
                .flatMap(c -> c.as(type))
                .filter(c -> StringUtils.equals(c.propertyName(), propertyName))
                .map(c -> new SubselectAliasColumn<>(scope, c))
                .findAny()
                .map(Function.identity());
        }
    }
}
