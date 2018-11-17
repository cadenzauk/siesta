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
import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.core.util.Lazy;

import java.util.function.Function;

public class ColumnReference<C, P, F> {
    private final ForeignKeyReference<C,P> foreignKey;
    private final Function<ForeignKeyReference<C,P>,Column<F,C>> childColumnGetter;
    private final Function<ForeignKeyReference<C,P>,Column<F,P>> parentColumnGetter;
    private final Lazy<Column<F,C>> childColumn = new Lazy<>(this::computeChildColumn);
    private final Lazy<Column<F,P>> parentColumn = new Lazy<>(this::computeParentColumn);

    private ColumnReference(Builder<C,P,F> builder) {
        foreignKey = builder.foreignKey;
        childColumnGetter = builder.childColumnGetter;
        parentColumnGetter = builder.parentColumnGetter;
    }

    public Column<F,C> childColumn() {
        return childColumn.get();
    }

    public Column<F,P> parentColumn() {
        return parentColumn.get();
    }

    private Column<F,C> computeChildColumn() {
        return childColumnGetter.apply(foreignKey);
    }

    private Column<F,P> computeParentColumn() {
        return parentColumnGetter.apply(foreignKey);
    }

    public static <C, P, F> Builder<C,P,F> newBuilder() {
        return new Builder<>();
    }

    @SuppressWarnings("UnusedReturnValue")
    public static final class Builder<C, P, F> {
        private ForeignKeyReference<C,P> foreignKey;
        private Function<ForeignKeyReference<C,P>,Column<F,C>> childColumnGetter;
        private Function<ForeignKeyReference<C,P>,Column<F,P>> parentColumnGetter;
        private Class<F> fieldClass;

        private Builder() {
        }

        public Builder<C, P, F> foreignKey(ForeignKeyReference<C,P> val) {
            foreignKey = val;
            return this;
        }

        public Builder<C, P, F> childColumn(Function1<C,F> val) {
            MethodInfo<C,F> methodInfo = MethodInfo.of(val);
            fieldClass = methodInfo.effectiveClass();
            childColumnGetter = getChildColumn(methodInfo);
            return this;
        }

        public Builder<C, P, F> childColumn(FunctionOptional1<C,F> val) {
            MethodInfo<C,F> methodInfo = MethodInfo.of(val);
            fieldClass = methodInfo.effectiveClass();
            childColumnGetter = getChildColumn(methodInfo);
            return this;
        }

        public Builder<C, P, F> childColumn(Column<F,C> val) {
            childColumnGetter = fk -> val;
            return this;
        }

        public Builder<C, P, F> parentColumn(Function1<P,F> val) {
            parentColumnGetter = getParentColumn(MethodInfo.of(val));
            return this;
        }

        public Builder<C, P, F> parentColumn(FunctionOptional1<P,F> val) {
            parentColumnGetter = getParentColumn(MethodInfo.of(val));
            return this;
        }

        public Builder<C, P, F> parentColumn(String val) {
            parentColumnGetter = fk -> fk.parentTable().findColumn(fieldClass, val).orElseThrow(IllegalArgumentException::new);
            return this;
        }

        public ColumnReference<C,P,F> build() {
            return new ColumnReference<>(this);
        }

        private Function<ForeignKeyReference<C,P>,Column<F,C>> getChildColumn(MethodInfo<C,F> methodInfo) {
            return fk -> fk.childTable().column(methodInfo);
        }

        private Function<ForeignKeyReference<C,P>,Column<F,P>> getParentColumn(MethodInfo<P,F> methodInfo) {
            return fk -> fk.parentTable().column(methodInfo);
        }
    }
}
