/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import static com.cadenzauk.siesta.Conditions.isEqualTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class Select1Test {
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

    @Test
    public void whereIsEqualToOneColumnWithoutAlias() {
        Catalog catalog = Catalog.newBuilder().defaultSchema("TEST").build();

        String sql = catalog.from(Row1.class)
            .where(Row1::name, isEqualTo(Row1::description))
            .sql();

        assertThat(sql, is("select ROW1.NAME as ROW1_NAME, ROW1.DESCRIPTION as ROW1_DESCRIPTION from TEST.ROW1 as ROW1 where ROW1.NAME = ROW1.DESCRIPTION"));
    }

    @Test
    public void whereIsEqualToOneColumnWithDefaultAlias() {
        Catalog catalog = Catalog.newBuilder().defaultSchema("TEST").build();

        String sql = catalog.from(Row1.class, "w")
            .where(Row1::name, isEqualTo(Row1::description))
            .sql();

        assertThat(sql, is("select w.NAME as w_NAME, w.DESCRIPTION as w_DESCRIPTION from TEST.ROW1 as w where w.NAME = w.DESCRIPTION"));
    }

    @Test
    public void whereIsEqualToOneColumnWithAlias() {
        Catalog catalog = Catalog.newBuilder().defaultSchema("TEST").build();

        String sql = catalog.from(Row1.class, "w")
            .where(Row1::name, isEqualTo("w", Row1::description))
            .sql();

        assertThat(sql, is("select w.NAME as w_NAME, w.DESCRIPTION as w_DESCRIPTION from TEST.ROW1 as w where w.NAME = w.DESCRIPTION"));
    }

    @Test
    public void whereIsEqualToOneValue() {
        Catalog catalog = Catalog.newBuilder().defaultSchema("TEST").build();

        String sql = catalog.from(Row1.class, "w")
            .where(Row1::description, isEqualTo("fred"))
            .sql();

        assertThat(sql, is("select w.NAME as w_NAME, w.DESCRIPTION as w_DESCRIPTION from TEST.ROW1 as w where w.DESCRIPTION = ?"));
    }

    @Test
    public void whereIsEqualToTwoValues() {
        Catalog catalog = Catalog.newBuilder().defaultSchema("TEST").build();

        String sql = catalog.from(Row1.class, "w")
            .where(Row1::description, isEqualTo("fred"))
            .and(Row1::name, isEqualTo("bob"))
            .sql();

        assertThat(sql, is("select w.NAME as w_NAME, w.DESCRIPTION as w_DESCRIPTION from TEST.ROW1 as w where (w.DESCRIPTION = ?) and (w.NAME = ?)"));
    }

    @Test
    public void orderByOneColumnDefaultAscending() {
        Catalog catalog = Catalog.newBuilder().defaultSchema("TEST").build();

        String sql = catalog.from(Row1.class, "q")
            .where(Row1::name, isEqualTo("joe"))
            .orderBy(Row1::description)
            .sql();

        assertThat(sql, is("select q.NAME as q_NAME, q.DESCRIPTION as q_DESCRIPTION from TEST.ROW1 as q where q.NAME = ? order by q.DESCRIPTION ascending"));
    }

    @Test
    public void orderByOneColumnAscending() {
        Catalog catalog = Catalog.newBuilder().defaultSchema("TEST").build();

        String sql = catalog.from(Row1.class, "q")
            .where(Row1::name, isEqualTo("joe"))
            .orderBy(Row1::description, Order.ASCENDING)
            .sql();

        assertThat(sql, is("select q.NAME as q_NAME, q.DESCRIPTION as q_DESCRIPTION from TEST.ROW1 as q where q.NAME = ? order by q.DESCRIPTION ascending"));
    }

    @Test
    public void orderByOneColumnDescending() {
        Catalog catalog = Catalog.newBuilder().defaultSchema("TEST").build();

        String sql = catalog.from(Row1.class, "q")
            .where(Row1::name, isEqualTo("joe"))
            .orderBy(Row1::description, Order.DESCENDING)
            .sql();

        assertThat(sql, is("select q.NAME as q_NAME, q.DESCRIPTION as q_DESCRIPTION from TEST.ROW1 as q where q.NAME = ? order by q.DESCRIPTION descending"));
    }

    @Test
    public void noWhereClauseOrderByOneColumnDefaultAscending() {
        Catalog catalog = Catalog.newBuilder().defaultSchema("TEST").build();

        String sql = catalog.from(Row1.class, "q")
            .orderBy(Row1::name)
            .sql();

        assertThat(sql, is("select q.NAME as q_NAME, q.DESCRIPTION as q_DESCRIPTION from TEST.ROW1 as q order by q.NAME ascending"));
    }

    @Test
    public void optional() throws Exception {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        Catalog catalog = Catalog.newBuilder().defaultSchema("TEST").build();

        catalog.from(Row1.class, "w")
            .optional(jdbcTemplate);

        verify(jdbcTemplate, times(1)).query(eq("select w.NAME as w_NAME, w.DESCRIPTION as w_DESCRIPTION from TEST.ROW1 as w"), ArgumentMatchers.<Object[]>any(), ArgumentMatchers.<RowMapper<Row1>>any());
    }
}