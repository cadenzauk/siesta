/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import org.junit.Test;

import static com.cadenzauk.siesta.Conditions.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class Select2Test {
    public static class Row1 {
        private String name;
        private String description;

        public String name() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String description() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class Row2 {
        private String name;
        private String description;

        public String name() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String description() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    @Test
    public void innerJoin() {
        Database database = Database.newBuilder().defaultSchema("TEST").build();

        String sql = database.from(Row1.class, "r1")
            .join(Row2.class, "r2").on(Row2::name, isEqualTo(Row1::name))
            .sql();

        assertThat(sql, is("select r1.NAME as r1_NAME, r1.DESCRIPTION as r1_DESCRIPTION, r2.NAME as r2_NAME, r2.DESCRIPTION as r2_DESCRIPTION from TEST.ROW1 as r1 join TEST.ROW2 as r2 on r2.NAME = r1.NAME"));
    }

    @Test
    public void leftOuterJoin() {
        Database database = Database.newBuilder().defaultSchema("TEST").build();

        String sql = database.from(Row1.class, "r1")
            .leftJoin(Row2.class, "r2").on(Row2::name, isEqualTo(Row1::name))
            .sql();

        assertThat(sql, is("select r1.NAME as r1_NAME, r1.DESCRIPTION as r1_DESCRIPTION, r2.NAME as r2_NAME, r2.DESCRIPTION as r2_DESCRIPTION from TEST.ROW1 as r1 left join TEST.ROW2 as r2 on r2.NAME = r1.NAME"));
    }

    @Test
    public void rightOuterJoin() {
        Database database = Database.newBuilder().defaultSchema("TEST").build();

        String sql = database.from(Row1.class, "r1")
            .rightJoin(Row2.class, "r2").on(Row2::name, isEqualTo(Row1::name))
            .sql();

        assertThat(sql, is("select r1.NAME as r1_NAME, r1.DESCRIPTION as r1_DESCRIPTION, r2.NAME as r2_NAME, r2.DESCRIPTION as r2_DESCRIPTION from TEST.ROW1 as r1 right join TEST.ROW2 as r2 on r2.NAME = r1.NAME"));
    }

    @Test
    public void fullOuterJoin() {
        Database database = Database.newBuilder().defaultSchema("TEST").build();

        String sql = database.from(Row1.class, "r1")
            .fullOuterJoin(Row2.class, "r2").on(Row2::name, isEqualTo(Row1::name))
            .sql();

        assertThat(sql, is("select r1.NAME as r1_NAME, r1.DESCRIPTION as r1_DESCRIPTION, r2.NAME as r2_NAME, r2.DESCRIPTION as r2_DESCRIPTION from TEST.ROW1 as r1 full outer join TEST.ROW2 as r2 on r2.NAME = r1.NAME"));
    }

    @Test
    public void innerJoinWhereOneValue() {
        Database database = Database.newBuilder().defaultSchema("TEST").build();

        String sql = database.from(Row1.class, "r1")
            .join(Row2.class, "r2").on(Row2::name, isEqualTo(Row1::name))
            .where(Row1::description, isGreaterThan(""))
            .sql();

        assertThat(sql, is("select r1.NAME as r1_NAME, r1.DESCRIPTION as r1_DESCRIPTION, r2.NAME as r2_NAME, r2.DESCRIPTION as r2_DESCRIPTION from TEST.ROW1 as r1 join TEST.ROW2 as r2 on r2.NAME = r1.NAME where r1.DESCRIPTION > ?"));
    }

    @Test
    public void innerJoinWhereOneValueAndOneColumn() {
        Database database = Database.newBuilder().defaultSchema("TEST").build();

        String sql = database.from(Row1.class, "r1")
            .join(Row2.class, "r2").on(Row2::name, isEqualTo(Row1::name))
            .where(Row1::description, isGreaterThan(""))
            .and(Row2::name, isNotEqualTo(Row1::name))
            .sql();

        assertThat(sql, is("select r1.NAME as r1_NAME, r1.DESCRIPTION as r1_DESCRIPTION, r2.NAME as r2_NAME, r2.DESCRIPTION as r2_DESCRIPTION from TEST.ROW1 as r1 join TEST.ROW2 as r2 on r2.NAME = r1.NAME where (r1.DESCRIPTION > ?) and (r2.NAME <> r1.NAME)"));
    }

}
