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

import com.cadenzauk.siesta.grammar.expression.BooleanExpression;

import java.util.stream.Stream;

public abstract class From {
    public abstract String sql(Scope scope);

    public abstract Stream<Object> args(Scope scope);

    public abstract void on(BooleanExpression expression);

    public abstract BooleanExpression on();

    private static class FromAlias extends From {
        private final Alias<?> alias;

        private FromAlias(Alias<?> alias) {
            this.alias = alias;
        }

        @Override
        public String sql(Scope scope) {
            return alias.inWhereClause();
        }

        @Override
        public Stream<Object> args(Scope scope) {
            return Stream.empty();
        }

        @Override
        public void on(BooleanExpression expression) {

        }

        @Override
        public BooleanExpression on() {
            return null;
        }
    }

    private static class FromJoin extends From {
        private final From lhs;
        private final JoinType join;
        private final Alias<?> next;
        private BooleanExpression onClause;

        FromJoin(From lhs, JoinType join, Alias<?> next) {
            this.lhs = lhs;
            this.join = join;
            this.next = next;
        }

        @Override
        public String sql(Scope scope) {
            return String.format("%s %s %s on %s",
                lhs.sql(scope),
                join.sql(),
                next.inWhereClause(),
                onClause.sql(scope));
        }

        @Override
        public Stream<Object> args(Scope scope) {
            return Stream.concat(lhs.args(scope), onClause.args(scope));
        }

        @Override
        public void on(BooleanExpression expression) {
            onClause = expression;
        }

        @Override
        public BooleanExpression on() {
            return onClause;
        }
    }

    public From join(JoinType join, Alias<?> next) {
        return new FromJoin(this, join, next);
    }

    public static From from(Alias<?> alias) {
        return new FromAlias(alias);
    }
}
