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

package com.cadenzauk.siesta;

import com.cadenzauk.core.sql.RowMapperFactory;
import com.google.common.reflect.TypeToken;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cadenzauk.core.reflect.util.TypeUtil.boxedType;
import static com.cadenzauk.core.util.OptionalUtil.or;
import static java.util.stream.Collectors.toList;

public class ProjectionColumn<T> {
    private final TypeToken<T> type;
    private final String propertyName;
    private final String columnName;
    private final String columnSql;
    private final String label;
    private final RowMapperFactory<T> rowMapperFactory;
    private final List<ProjectionColumn<?>> components;

    public ProjectionColumn(TypeToken<T> type, String propertyName, String columnName, String columnSql, String label, RowMapperFactory<T> rowMapperFactory) {
        this(type, propertyName, columnName, columnSql, label, rowMapperFactory, Collections.emptyList());
    }

    public ProjectionColumn(TypeToken<T> type, String propertyName, String columnName, String columnSql, String label, RowMapperFactory<T> rowMapperFactory, List<ProjectionColumn<?>> components) {
        this.type = type;
        this.propertyName = propertyName;
        this.columnName = columnName;
        this.columnSql = columnSql;
        this.label = label;
        this.rowMapperFactory = rowMapperFactory;
        this.components = components;
    }

    public String propertyName() {
        return propertyName;
    }

    public String columnName() {
        return columnName;
    }

    public String columnSql() {
        return components.isEmpty()
            ? String.format("%s as %s", columnSql, label)
            : components.stream().map(ProjectionColumn::columnSql).collect(Collectors.joining(", "));
    }

    public String label() {
        return label;
    }

    public TypeToken<T> type() {
        return type;
    }

    public RowMapperFactory<T> rowMapperFactory() {
        return rowMapperFactory;
    }

    public List<ProjectionColumn<?>> components() {
        return components;
    }

    @SuppressWarnings("unchecked")
    public <T2> Stream<ProjectionColumn<T2>> as(Class<T2> type) {
        return boxedType(type).isAssignableFrom(boxedType(this.type.getRawType()))
            ? Stream.of((ProjectionColumn<T2>) this)
            : Stream.empty();
    }

    @SuppressWarnings("unchecked")
    public <T2> Stream<ProjectionColumn<T2>> as(TypeToken<T2> type) {
        return boxedType(type.getRawType()).isAssignableFrom(boxedType(this.type.getRawType()))
            ? Stream.of((ProjectionColumn<T2>) this)
            : Stream.empty();
    }

    public static <T> ProjectionColumn<T> forAliasAndLabel(Alias<?> alias, ProjectionColumn<T> input, Optional<String> label) {
        List<ProjectionColumn<?>> components = input
            .components()
            .stream()
            .map(c -> ProjectionColumn.forAliasAndLabel(alias, c, label.map(x -> alias.database().namingStrategy().embeddedName(x, c.label))))
            .collect(toList());
        String actualLabel = label.orElseGet(() -> alias.inSelectClauseLabel(input.label()));
        return new ProjectionColumn<>(input.type(), input.propertyName(), input.columnName(), alias.inSelectClauseSql(input.columnName()), actualLabel, (p, x) -> input.rowMapperFactory.rowMapper(p, or(x, Optional.of(actualLabel))), components);
    }
    public static <T> ProjectionColumn<T> usingLabelAsColumnName(Alias<?> alias, ProjectionColumn<T> input, Optional<String> label) {
        List<ProjectionColumn<?>> components = input
            .components()
            .stream()
            .map(c -> ProjectionColumn.usingLabelAsColumnName(alias, c, label.map(x -> alias.database().namingStrategy().embeddedName(x, c.label))))
            .collect(toList());
        String actualLabel = label.orElseGet(() -> alias.inSelectClauseLabel(input.label()));
        return new ProjectionColumn<>(input.type(), input.propertyName(), input.label(), alias.inSelectClauseSql(input.label()), actualLabel, (p, x) -> input.rowMapperFactory.rowMapper(p, or(x, Optional.of(actualLabel))), components);
    }
}
