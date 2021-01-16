/*
 * Copyright (c) 2020 Cadenza United Kingdom Limited
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

import java.util.Optional;
import java.util.stream.Stream;

public abstract class TableAlias<T> extends Alias<T> {
    public abstract String qualifiedTableName();

    @Override
    public String inFromClauseSql() {
        if (isDual()) {
            return database().dialect().dual();
        }
        return aliasName()
                   .map(a -> String.format("%s %s", qualifiedTableName(), a))
                   .orElseGet(this::qualifiedTableName);
    }

    @Override
    public String inSelectClauseSql(String columnName) {
        return String.format("%s.%s", aliasName().orElseGet(this::qualifiedTableName), columnName);
    }

    @Override
    public String inSelectClauseSql(String columnName, Optional<String> label) {
        return String.format("%s.%s %s", aliasName().orElseGet(this::qualifiedTableName), columnName, label.orElseGet(() -> inSelectClauseLabel(columnName)));
    }

    @Override
    public Stream<Alias<?>> as(Scope scope, ColumnSpecifier<?> columnSpecifier, Optional<String> requiredAlias) {
        if (requiredAlias.isPresent()) {
            if (requiredAlias.equals(aliasName())) {
                if (columnSpecifier.referringClass().map(r -> r.isAssignableFrom(type().getRawType())).orElse(false)) {
                    return Stream.of(this);
                }
                throw new IllegalArgumentException("Alias " + columnLabelPrefix() + " is an alias for " + type() + " and not " + columnSpecifier.referringClass().map(Object::toString).orElse("N/A"));
            } else {
                return Stream.empty();
            }
        }
        if (columnSpecifier.referringClass().map(r -> r.isAssignableFrom(type().getRawType())).orElse(false)) {
            return Stream.of(this);
        }
        return Stream.empty();
    }
}
