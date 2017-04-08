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

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class Select3Test {
    @SuppressWarnings("unused")
    static class Foo {
        private int id;

        int id() {
            return id;
        }
    }

    @SuppressWarnings("unused")
    static class Bar {
        private String id;
        private int fooId;

        String id() {
            return id;
        }

        int fooId() {
            return fooId;
        }
    }

    @SuppressWarnings("unused")
    static class Baz {
        private int id;
        private String barId;

        int id() {
            return id;
        }

        String barId() {
            return barId;
        }
    }

    private final Database database = Database.newBuilder().defaultSchema("TEST").build();

    @Test
    void noWhereClause() {
        String sql = database.from(Foo.class, "f")
            .join(Bar.class, "b").on(Bar::fooId).isEqualTo(Foo::id)
            .join(Baz.class, "z").on(Baz::barId).isEqualTo(Bar::id).and(Baz::id).isEqualTo(1)
            .select(Baz::barId)
            .sql();

        assertThat(sql, is("select z.BAR_ID as z_BAR_ID " +
            "from TEST.FOO as f " +
            "join TEST.BAR as b on b.FOO_ID = f.ID " +
            "join TEST.BAZ as z on z.BAR_ID = b.ID and z.ID = ?"));
    }

    @Test
    void whereClauseWithOneCondition() {
        String sql = database.from(Foo.class, "f")
            .join(Bar.class, "b").on(Bar::fooId).isEqualTo(Foo::id)
            .join(Baz.class, "z").on(Baz::barId).isEqualTo(Bar::id).and(Baz::id).isEqualTo(1)
            .where(Baz::id).isGreaterThan(2)
            .sql();

        assertThat(sql, is("select f.ID as f_ID, " +
            "b.ID as b_ID, b.FOO_ID as b_FOO_ID, " +
            "z.ID as z_ID, z.BAR_ID as z_BAR_ID " +
            "from TEST.FOO as f " +
            "join TEST.BAR as b on b.FOO_ID = f.ID " +
            "join TEST.BAZ as z on z.BAR_ID = b.ID and z.ID = ? " +
            "where z.ID > ?"));
    }

    @Test
    void whereClauseWithTwoConditions() {
        String sql = database.from(Foo.class, "f")
            .join(Bar.class, "b").on(Bar::fooId).isEqualTo(Foo::id)
            .join(Baz.class, "z").on(Baz::barId).isEqualTo(Bar::id).and(Baz::id).isEqualTo(1)
            .where(Baz::id).isGreaterThan(2)
            .and(Bar::fooId).isNotEqualTo(4)
            .sql();

        assertThat(sql, is("select f.ID as f_ID, " +
            "b.ID as b_ID, b.FOO_ID as b_FOO_ID, " +
            "z.ID as z_ID, z.BAR_ID as z_BAR_ID " +
            "from TEST.FOO as f " +
            "join TEST.BAR as b on b.FOO_ID = f.ID " +
            "join TEST.BAZ as z on z.BAR_ID = b.ID and z.ID = ? " +
            "where z.ID > ? and b.FOO_ID <> ?"));
    }

    @Test
    void whereClauseWithOrderBy() {
        String sql = database.from(Foo.class, "f")
            .join(Bar.class, "b").on(Bar::fooId).isEqualTo(Foo::id)
            .join(Baz.class, "z").on(Baz::barId).isEqualTo(Bar::id).and(Baz::id).isEqualTo(1)
            .orderBy(Bar::fooId, Order.DESC).then("f", Foo::id, Order.ASC)
            .sql();

        assertThat(sql, is("select f.ID as f_ID, " +
            "b.ID as b_ID, b.FOO_ID as b_FOO_ID, " +
            "z.ID as z_ID, z.BAR_ID as z_BAR_ID " +
            "from TEST.FOO as f " +
            "join TEST.BAR as b on b.FOO_ID = f.ID " +
            "join TEST.BAZ as z on z.BAR_ID = b.ID and z.ID = ? " +
            "order by b.FOO_ID desc, f.ID asc"));
    }
}