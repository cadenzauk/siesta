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

import com.cadenzauk.core.tuple.Tuple;
import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class DynamicProjection implements Projection {
    private final boolean distinct;
    private final List<Tuple2<TypedExpression<?>,TypedExpression<?>>> columns = new ArrayList<>();

    public DynamicProjection(boolean distinct) {
        this.distinct = distinct;
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
    public String labelList(Scope scope) {
        return columns.stream()
            .map(p -> p.map((s, t) -> t.label(scope)))
            .collect(joining(", "));
    }

    @Override
    public Projection distinct() {
        DynamicProjection projection = new DynamicProjection(true);
        projection.columns.addAll(columns);
        return projection;
    }

    public <T> void add(TypedExpression<T> source, TypedExpression<T> target) {
        columns.add(Tuple.of(source, target));
    }
}
