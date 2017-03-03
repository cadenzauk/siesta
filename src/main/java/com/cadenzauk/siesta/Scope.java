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

package com.cadenzauk.siesta;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class Scope {
    private final Optional<Scope> outer;
    private final List<Alias<?>> aliases;
    private final Database database;

    public Scope(Database database, Alias<?>... aliases) {
        this.database = database;
        this.outer = Optional.empty();
        this.aliases = ImmutableList.copyOf(aliases);
    }

    private Scope(Scope outer, List<Alias<?>> aliases) {
        this.database = outer.database;
        this.outer = Optional.of(outer);
        this.aliases = ImmutableList.copyOf(aliases);
    }

    public Database database() {
        return database;
    }

    public <R> Alias<R> findAlias(Class<R> requiredRowClass, String requiredAlias) {
        Optional<Alias<R>> found = aliases.stream().flatMap(a -> a.as(requiredRowClass, requiredAlias)).findFirst();
        return found
            .orElseGet(() -> outer.map(o -> o.findAlias(requiredRowClass, requiredAlias))
                .orElseThrow(() -> new IllegalArgumentException("No such alias as " + requiredAlias + " in scope.")));
    }

    public <R> Alias<R> findAlias(Class<R> requiredRowClass) {
        List<Alias<R>> found = aliases.stream().flatMap(a -> a.as(requiredRowClass)).collect(toList());
        if (found.isEmpty()) {
            return outer.map(o -> o.findAlias(requiredRowClass))
                .orElseThrow(() -> new IllegalArgumentException("No alias for " + requiredRowClass + " in scope."));
        }
        if (found.size() > 1) {
            throw new IllegalArgumentException("More than one alias for " + requiredRowClass + " in scope.");
        }
        return found.get(0);
    }

    public <R> Scope plus(Alias<R> alias) {
        return new Scope(this, ImmutableList.of(alias));
    }

    public Scope plus(Scope inner) {
        return new Scope(this, inner.aliases);
    }
}
