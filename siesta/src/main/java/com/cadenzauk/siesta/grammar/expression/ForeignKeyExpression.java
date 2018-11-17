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

package com.cadenzauk.siesta.grammar.expression;

import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.catalog.ForeignKeyReference;
import com.cadenzauk.siesta.grammar.InvalidForeignKeyException;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class ForeignKeyExpression<L, R> extends BooleanExpression {
    public enum Direction {CHILD_TO_PARENT, PARENT_TO_CHILD}

    private final Optional<String> name;
    private final Function<Scope,Alias<L>> lhsResolver;
    private final Function<Scope,Alias<R>> rhsResolver;
    private final Direction direction;

    public ForeignKeyExpression(Optional<String> name, Alias<L> lhs, Alias<R> rhs, Direction direction) {
        this.name = name;
        this.lhsResolver = s -> lhs;
        this.rhsResolver = s -> rhs;
        this.direction = direction;
    }

    public ForeignKeyExpression(Optional<String> name, Class<L> lhsClass, Optional<String> lhsAlias, Alias<R> rhs, Direction direction) {
        this.name = name;
        this.lhsResolver = s -> lhsAlias.map(a -> s.findAlias(lhsClass, a)).orElseGet(() -> s.findAlias(lhsClass));
        this.rhsResolver = s -> rhs;
        this.direction = direction;
    }

    private ForeignKeyExpression(Optional<String> name, Class<L> lhsClass, Optional<String> lhsAlias, Class<R> rhsClass, Optional<String> rhsAlias, Direction direction) {
        this.name = name;
        this.lhsResolver = s -> lhsAlias.map(a -> s.findAlias(lhsClass, a)).orElseGet(() -> s.findAlias(lhsClass));
        this.rhsResolver = s -> rhsAlias.map(a -> s.findAlias(rhsClass, a)).orElseGet(() -> s.findAlias(rhsClass));
        this.direction = direction;
    }

    @Override
    public String sql(Scope scope) {
        Alias<L> lhs = lhsResolver.apply(scope);
        Alias<R> rhs = rhsResolver.apply(scope);
        return direction == Direction.CHILD_TO_PARENT
            ? foreignKey(lhs, rhs).sql(lhs, rhs, false)
            : foreignKey(rhs, lhs).sql(rhs, lhs, true);
    }

    private <C,P> ForeignKeyReference<C,P> foreignKey(Alias<C> child, Alias<P> parent) {
        return child.table().foreignKey(parent.table(), name)
            .orElseThrow(() -> new InvalidForeignKeyException(child.table(), parent.table(), name));
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return Stream.empty();
    }

    @Override
    public Precedence precedence() {
        return Precedence.AND;
    }

    public static final class ForeignKeyBuilder1 {
        private final Optional<String> name;

        private ForeignKeyBuilder1(Optional<String> name) {
            this.name = name;
        }

        public <C> ForeignKeyBuilder2<C> from(Class<C> childClass) {
            return new ForeignKeyBuilder2<>(name, childClass, Optional.empty());
        }

        public <C> ForeignKeyBuilder2<C> from(Class<C> childClass, String alias) {
            return new ForeignKeyBuilder2<>(name, childClass, Optional.of(alias));
        }
    }

    public static final class ForeignKeyBuilder2<C> {
        private final Optional<String> name;
        private final Class<C> childClass;
        private final Optional<String> childAlias;

        private ForeignKeyBuilder2(Optional<String> name, Class<C> childClass, Optional<String> childAlias) {
            this.name = name;
            this.childClass = childClass;
            this.childAlias = childAlias;
        }

        public <P> BooleanExpression to(Class<P> parentClass) {
            return new ForeignKeyExpression<>(name, childClass, childAlias, parentClass, Optional.empty(), Direction.CHILD_TO_PARENT);
        }

        public <P> BooleanExpression to(Class<P> parentClass, String parentAlias) {
            return new ForeignKeyExpression<>(name, childClass, childAlias, parentClass, Optional.of(parentAlias), Direction.CHILD_TO_PARENT);
        }
    }

    public static ForeignKeyBuilder1 foreignKey() {
        return new ForeignKeyBuilder1(Optional.empty());
    }

    public static ForeignKeyBuilder1 foreignKey(String name) {
        return new ForeignKeyBuilder1(Optional.of(name));
    }
}
