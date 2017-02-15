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

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class Select1Test {
    public static class Row1 {
        private String name;
        private String description;

        public String name() {
            return name;
        }

        public String description() {
            return description;
        }
    }

    public static class Row2 {
        private String name;
        private String description;
        private Optional<String> comment;

        public String name() {
            return name;
        }

        public String description() {
            return description;
        }

        public Optional<String> comment() {
            return comment;
        }
    }

    @Test
    public void whereIsEqualToOneColumnWithoutAlias() {
        Database database = Database.newBuilder().defaultSchema("TEST").build();

        String sql = database.from(Row1.class)
            .where(Row1::name).isEqualTo(Row1::description)
            .sql();

        assertThat(sql, is("select ROW1.NAME as ROW1_NAME, ROW1.DESCRIPTION as ROW1_DESCRIPTION from TEST.ROW1 as ROW1 where ROW1.NAME = ROW1.DESCRIPTION"));
    }

    @Test
    public void whereIsEqualToOneColumnWithDefaultAlias() {
        Database database = Database.newBuilder().defaultSchema("TEST").build();

        String sql = database.from(Row1.class, "w")
            .where(Row1::name).isEqualTo(Row1::description)
            .sql();

        assertThat(sql, is("select w.NAME as w_NAME, w.DESCRIPTION as w_DESCRIPTION from TEST.ROW1 as w where w.NAME = w.DESCRIPTION"));
    }

    @Test
    public void whereIsEqualToOneColumnWithAlias() {
        Database database = Database.newBuilder().defaultSchema("TEST").build();

        String sql = database.from(Row1.class, "w")
            .where(Row1::name).isEqualTo("w", Row1::description)
            .sql();

        assertThat(sql, is("select w.NAME as w_NAME, w.DESCRIPTION as w_DESCRIPTION from TEST.ROW1 as w where w.NAME = w.DESCRIPTION"));
    }

    @Test
    public void whereIsEqualToOneValue() {
        Database database = Database.newBuilder().defaultSchema("TEST").build();

        String sql = database.from(Row1.class, "w")
            .where(Row1::description).isEqualTo("fred")
            .sql();

        assertThat(sql, is("select w.NAME as w_NAME, w.DESCRIPTION as w_DESCRIPTION from TEST.ROW1 as w where w.DESCRIPTION = ?"));
    }

    @Test
    public void whereIsEqualToTwoValues() {
        Database database = Database.newBuilder().defaultSchema("TEST").build();

        String sql = database.from(Row1.class, "w")
            .where(Row1::description).isEqualTo("fred")
            .and(Row1::name).isEqualTo("bob")
            .sql();

        assertThat(sql, is("select w.NAME as w_NAME, w.DESCRIPTION as w_DESCRIPTION from TEST.ROW1 as w where (w.DESCRIPTION = ?) and (w.NAME = ?)"));
    }

    @Test
    public void orderByOneColumnDefaultAscending() {
        Database database = Database.newBuilder().defaultSchema("TEST").build();

        String sql = database.from(Row1.class, "q")
            .where(Row1::name).isEqualTo("joe")
            .orderBy(Row1::description)
            .sql();

        assertThat(sql, is("select q.NAME as q_NAME, q.DESCRIPTION as q_DESCRIPTION from TEST.ROW1 as q where q.NAME = ? order by q.DESCRIPTION ascending"));
    }

    @Test
    public void orderByOneColumnAscending() {
        Database database = Database.newBuilder().defaultSchema("TEST").build();

        String sql = database.from(Row1.class, "q")
            .where(Row1::name).isEqualTo("joe")
            .orderBy(Row1::description, Order.ASCENDING)
            .sql();

        assertThat(sql, is("select q.NAME as q_NAME, q.DESCRIPTION as q_DESCRIPTION from TEST.ROW1 as q where q.NAME = ? order by q.DESCRIPTION ascending"));
    }

    @Test
    public void orderByOneColumnDescending() {
        Database database = Database.newBuilder().defaultSchema("TEST").build();

        String sql = database.from(Row1.class, "q")
            .where(Row1::name).isEqualTo("joe")
            .orderBy(Row1::description, Order.DESCENDING)
            .sql();

        assertThat(sql, is("select q.NAME as q_NAME, q.DESCRIPTION as q_DESCRIPTION from TEST.ROW1 as q where q.NAME = ? order by q.DESCRIPTION descending"));
    }

    @Test
    public void optionalColumnInCondition() {
        Database database = Database.newBuilder().defaultSchema("TEST").build();

        String sql = database.from(Row2.class, "q")
            .where(Row2::name).isEqualTo(Row2::comment)
            .orderBy(Row2::description, Order.DESCENDING)
            .sql();

        assertThat(sql, is("select q.NAME as q_NAME, q.DESCRIPTION as q_DESCRIPTION, q.COMMENT as q_COMMENT from TEST.ROW2 as q where q.NAME = q.COMMENT order by q.DESCRIPTION descending"));
    }

    @Test
    public void optionalColumnInWhere() {
        Database database = Database.newBuilder().defaultSchema("TEST").build();

        String sql = database.from(Row2.class, "q")
            .where(Row2::comment).isEqualTo(Row2::name)
            .orderBy(Row2::description, Order.ASCENDING)
            .sql();

        assertThat(sql, is("select q.NAME as q_NAME, q.DESCRIPTION as q_DESCRIPTION, q.COMMENT as q_COMMENT from TEST.ROW2 as q where q.COMMENT = q.NAME order by q.DESCRIPTION ascending"));
    }

    @Test
    public void noWhereClauseOrderByOneColumnDefaultAscending() {
        Database database = Database.newBuilder().defaultSchema("TEST").build();

        String sql = database.from(Row1.class, "q")
            .orderBy(Row1::name)
            .sql();

        assertThat(sql, is("select q.NAME as q_NAME, q.DESCRIPTION as q_DESCRIPTION from TEST.ROW1 as q order by q.NAME ascending"));
    }

    @Test
    public void optional() throws Exception {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        Database database = Database.newBuilder().defaultSchema("TEST").build();

        database.from(Row1.class, "w")
            .optional(jdbcTemplate);

        verify(jdbcTemplate, times(1)).query(eq("select w.NAME as w_NAME, w.DESCRIPTION as w_DESCRIPTION from TEST.ROW1 as w"), any(Object[].class), ArgumentMatchers.<RowMapper<Row1>>any());
    }
}
