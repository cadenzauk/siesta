/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class Scope {
    private final Alias<?>[] aliases;

    public Scope(Alias<?>... aliases) {
        this.aliases = aliases;
    }

    public <R> Alias<R> findAlias(Class<R> requiredRowClass, String requiredAlias) {
        Optional<Alias<R>> found = Arrays.stream(aliases).flatMap(a -> a.as(requiredRowClass, requiredAlias)).findFirst();
        return found.orElseThrow(() -> new IllegalArgumentException("No such alias as " + requiredAlias + " in scope."));
    }

    public <R> Alias<R> findAlias(Class<R> requiredRowClass) {
        List<Alias<R>> found = Arrays.stream(aliases).flatMap(a -> a.as(requiredRowClass)).collect(toList());
        if (found.isEmpty()) {
            throw new IllegalArgumentException("No alias for " + requiredRowClass + " in scope.");
        }
        if (found.size() > 1) {
            throw new IllegalArgumentException("More than one alias for " + requiredRowClass + " in scope.");
        }
        return found.get(0);
    }
}
