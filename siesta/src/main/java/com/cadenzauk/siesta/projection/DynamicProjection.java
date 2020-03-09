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

package com.cadenzauk.siesta.projection;

import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.core.tuple.Tuple;
import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.siesta.ColumnSpecifier;
import com.cadenzauk.siesta.DynamicRowMapperFactory;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.ProjectionColumn;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.TableAlias;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class DynamicProjection<R> implements Projection<R> {
    private final boolean distinct;
    private final TableAlias<R> alias;
    private final List<Tuple2<TypedExpression<?>,TypedExpression<?>>> columns = new ArrayList<>();

    public DynamicProjection(boolean distinct, TableAlias<R> alias) {
        this.distinct = distinct;
        this.alias = alias;
    }

    @Override
    public String sql(Scope scope) {
        return (distinct ? "distinct " : "") + columns.stream()
            .map(p -> p.map((s, t) -> s.sql(scope) + " as " + t.label(scope)))
            .collect(joining(", "));
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return columns.stream().flatMap(p -> p.item1().args(scope));
    }

    @Override
    public Stream<ProjectionColumn<?>> columns(Scope scope) {
        return columns.stream().map(p -> p.map((s, t) -> projectionColumn(scope, s, t)));
    }

    private <T> ProjectionColumn<T> projectionColumn(Scope scope, TypedExpression<?> s, TypedExpression<T> t) {
        return new ProjectionColumn<T>(t.type(), s.sql(scope), t.label(scope), t.rowMapperFactory(scope));
    }

    @Override
    public <T> Optional<ProjectionColumn<T>> findColumn(Scope scope, ColumnSpecifier<T> getterMethod) {
        return Optional.empty();
    }

    @Override
    public RowMapperFactory<R> rowMapperFactory(Scope scope) {
        DynamicRowMapperFactory<R> rowMapperFactory = alias.dynamicRowMapperFactory();
        columns
            .stream()
            .map(p -> p.map((s, t) -> t.label(scope)))
            .forEach(rowMapperFactory::add);
        return rowMapperFactory;
    }

    @Override
    public Projection<R> distinct() {
        DynamicProjection<R> projection = new DynamicProjection<>(true, alias);
        projection.columns.addAll(columns);
        return projection;
    }

    @Override
    public List<Projection<?>> components() {
        return ImmutableList.of(this);
    }

    @Override
    public boolean includes(ColumnSpecifier<?> getter) {
        return components()
            .stream()
            .anyMatch(c -> c.includes(getter));
    }

    public <T> void add(TypedExpression<T> source, TypedExpression<T> target) {
        columns.add(Tuple.of(source, target));
    }
}
