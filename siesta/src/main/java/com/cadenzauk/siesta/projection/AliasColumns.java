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

import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.ProjectionColumn;
import com.cadenzauk.siesta.Scope;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class AliasColumns<R> implements Projection<R> {
    private final boolean distinct;
    private final Alias<R> alias;

    public AliasColumns(boolean distinct, Alias<R> alias) {
        this.distinct = distinct;
        this.alias = alias;
    }

    @Override
    public String sql(Scope outer) {
        return (distinct ? "distinct " : "") + alias.inSelectClauseSql(outer);
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return Stream.empty();
    }

    @Override
    public Stream<ProjectionColumn<?>> columns(Scope scope) {
        return alias.projectionColumns(scope);
    }

    @Override
    public <T> Optional<ProjectionColumn<T>> findColumn(Scope scope, MethodInfo<?,T> getterMethod) {
        return alias.findColumn(getterMethod);
    }

    @Override
    public RowMapperFactory<R> rowMapperFactory(Scope scope) {
        return alias.rowMapperFactory();
    }

    @Override
    public Projection<R> distinct() {
        return new AliasColumns<>(true, alias);
    }

    @Override
    public List<Projection<?>> components() {
        return ImmutableList.of(this);
    }

    @Override
    public boolean includes(MethodInfo<?,?> getter) {
        return getter.referringClass().isAssignableFrom(alias.type().getRawType());
    }
}
