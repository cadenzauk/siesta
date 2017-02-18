/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.siesta;

import com.cadenzauk.siesta.expression.Expression;

import java.util.stream.Stream;

public abstract class From {
    public abstract String sql(Scope scope);

    public abstract Stream<Object> args();

    public abstract void on(Expression expression);

    public abstract Expression on();

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
        public Stream<Object> args() {
            return Stream.empty();
        }

        @Override
        public void on(Expression expression) {

        }

        @Override
        public Expression on() {
            return null;
        }
    }

    private static class FromJoin extends From {
        private final From lhs;
        private final JoinType join;
        private final Alias<?> next;
        private Expression onClause;

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
        public Stream<Object> args() {
            return Stream.concat(lhs.args(), onClause.args());
        }

        @Override
        public void on(Expression expression) {
            onClause = expression;
        }

        @Override
        public Expression on() {
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
