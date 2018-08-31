/*
 * Copyright (c) 2018 Cadenza United Kingdom Limited
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

package com.cadenzauk.siesta.catalog;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.core.util.Lazy;
import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.Alias;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class ForeignKeyReference<C, P> {
    private final Optional<String> name;
    private final Table<C> childTable;
    private final Class<P> parentClass;
    private final Lazy<Table<P>> parentTable = new Lazy<>(this::computeParent);
    private final List<ColumnReference<C,P,?>> columnReferences;

    private ForeignKeyReference(Builder<C,P> builder) {
        name = builder.name;
        childTable = builder.childTable;
        parentClass = builder.parentClass;
        columnReferences = builder.columnReferences
            .stream()
            .map(x -> x.foreignKey(this).build())
            .collect(Collectors.collectingAndThen(toList(), ImmutableList::copyOf));
    }

    public Optional<String> name() {
        return name;
    }

    public Table<C> childTable() {
        return childTable;
    }

    public Table<P> parentTable() {
        return parentTable.get();
    }

    @SuppressWarnings("unchecked")
    public <T> Stream<ForeignKeyReference<C,T>> toParent(Table<T> parent, Optional<String> name) {
        return parent.rowType().getRawType() == parentClass
            && nameMatches(name)
            ? Stream.of((ForeignKeyReference<C,T>) this)
            : Stream.empty();
    }

    private boolean nameMatches(Optional<String> lookingFor) {
        return lookingFor
            .map(Optional::of)
            .map(n -> Objects.equals(n, this.name))
            .orElse(true);
    }

    public String sql(Alias<C> child, Alias<P> parent, boolean reverse) {
        return columnReferences
            .stream()
            .map(c -> String.format(reverse ? "%2$s = %1$s" : "%1$s = %2$s",
                parent.inSelectClauseSql(c.parentColumn().columnName()),
                child.inSelectClauseSql(c.childColumn().columnName())))
            .collect(joining(" and "));
    }

    private Table<P> computeParent() {
        return childTable.database().table(parentClass);
    }

    public static <C, P> Builder<C,P> newBuilder() {
        return new Builder<>();
    }

    public static class Builder<C, P> {
        private Table<C> childTable;
        private Class<P> parentClass;
        private List<ColumnReference.Builder<C,P,?>> columnReferences = new ArrayList<>();
        private Optional<String> name = Optional.empty();

        private Builder() {
        }

        Builder<C,P> childTable(Table<C> val) {
            childTable = val;
            return this;
        }

        Builder<C,P> parentClass(Class<P> val) {
            parentClass = val;
            return this;
        }

        public Builder<C,P> name(String val) {
            name = OptionalUtil.ofBlankable(val);
            return this;
        }

        public <F> ReferenceBuilder<F> column(Function1<C,F> val) {
            ColumnReference.Builder<C,P,F> colRefBuilder = ColumnReference.newBuilder();
            columnReferences.add(colRefBuilder);
            return new ReferenceBuilder<>(colRefBuilder.childColumn(val));
        }

        public <F> ReferenceBuilder<F> column(FunctionOptional1<C,F> val) {
            ColumnReference.Builder<C,P,F> colRefBuilder = ColumnReference.newBuilder();
            columnReferences.add(colRefBuilder);
            return new ReferenceBuilder<>(colRefBuilder.childColumn(val));
        }

        public <F> ReferenceBuilder<F> column(Column<F,C> val) {
            ColumnReference.Builder<C,P,F> colRefBuilder = ColumnReference.newBuilder();
            columnReferences.add(colRefBuilder);
            return new ReferenceBuilder<>(colRefBuilder.childColumn(val));
        }

        public ForeignKeyReference<C,P> build() {
            return new ForeignKeyReference<>(this);
        }

        public class ReferenceBuilder<F> {
            private final ColumnReference.Builder<C,P,F> childColumn;

            public ReferenceBuilder(ColumnReference.Builder<C,P,F> childColumn) {
                this.childColumn = childColumn;
            }

            public Builder<C,P> references(Function1<P,F> parentColumn) {
                childColumn.parentColumn(parentColumn);
                return Builder.this;
            }

            public Builder<C,P> references(FunctionOptional1<P,F> parentColumn) {
                childColumn.parentColumn(parentColumn);
                return Builder.this;
            }

            void references(String parentColumn) {
                childColumn.parentColumn(parentColumn);
            }
        }
    }
}
