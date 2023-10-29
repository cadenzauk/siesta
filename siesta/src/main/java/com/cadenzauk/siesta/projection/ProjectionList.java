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
import com.cadenzauk.core.stream.StreamUtil;
import com.cadenzauk.siesta.ColumnSpecifier;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.ProjectionColumn;
import com.cadenzauk.siesta.Scope;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class ProjectionList<R> implements Projection<R> {
    private final boolean distinct;
    private final List<Projection<?>> components;
    private final Function<Scope,RowMapperFactory<R>> rowMapperFactory;

    public ProjectionList(boolean distinct, List<Projection<?>> components, Function<Scope,RowMapperFactory<R>> rowMapperFactory) {
        this.distinct = distinct;
        this.components = ImmutableList.copyOf(components);
        this.rowMapperFactory = rowMapperFactory;
    }

    @Override
    public String sql(Scope scope) {
        return (distinct ? "distinct " : "") + components.stream()
            .map(x -> x.sql(scope))
            .collect(joining(", "));
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return components.stream().flatMap(x -> x.args(scope));
    }

    @Override
    public Stream<ProjectionColumn<?>> columns(Scope scope) {
        return components
            .stream()
            .flatMap(c -> c.columns(scope));
    }

    @Override
    public Stream<String> resultingColumnNames(Scope scope) {
        return components.stream().flatMap(it -> it.resultingColumnNames(scope));
    }

    @Override
    public <T> Optional<ProjectionColumn<T>> findColumn(Scope scope, ColumnSpecifier<T> columnSpecifier) {
        return components
            .stream()
            .flatMap(x -> StreamUtil.of(x.findColumn(scope, columnSpecifier)))
            .findFirst();
    }

    @Override
    public RowMapperFactory<R> rowMapperFactory(Scope scope) {
        return rowMapperFactory.apply(scope);
    }

    @Override
    public Projection<R> distinct() {
        return new ProjectionList<>(true, components, rowMapperFactory);
    }

    @Override
    public List<Projection<?>> components() {
        return components;
    }

    @Override
    public boolean includes(ColumnSpecifier<?> columnSpecifier) {
        return components.stream().anyMatch(x -> x.includes(columnSpecifier));
    }
}
