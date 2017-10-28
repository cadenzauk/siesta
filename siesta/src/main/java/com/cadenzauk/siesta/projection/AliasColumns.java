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

import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.catalog.Column;

import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class AliasColumns<R> implements Projection {
    private final Alias<R> alias;

    public AliasColumns(Alias<R> alias) {
        this.alias = alias;
    }

    @Override
    public String sql(Scope outer) {
        return alias
            .table()
            .columns()
            .map(c -> c.sqlWithLabel(alias, alias.inSelectClauseLabel(c.name())))
            .collect(joining(", "));
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return Stream.empty();
    }

    @Override
    public String labelList(Scope scope) {
        return alias
            .table()
            .columns()
            .map(Column::name)
            .map(alias::inSelectClauseLabel)
            .collect(joining(", "));
    }
}
