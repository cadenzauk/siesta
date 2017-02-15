/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
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

    private Scope(Scope outer, Alias<?>... aliases) {
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
        return new Scope(this, alias);
    }
}
