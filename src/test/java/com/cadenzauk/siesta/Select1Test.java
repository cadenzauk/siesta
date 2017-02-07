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

import static com.cadenzauk.siesta.Table.aTable;
import static com.cadenzauk.siesta.Conditions.isEqualTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class Select1Test {
    public static class Row1 {
        public final static Column<String, Row1> NAME = Column.aColumn("NAME", DataType.STRING, Row1.class);
        public final static Column<String, Row1> DESCRIPTION = Column.aColumn("DESCRIPTION", DataType.STRING, Row1.class);

        public final static Table<Row1> TABLE = aTable("TEST", "ROW1", Row1::new, Row1.class)
            .mandatory(NAME, Row1::name, Row1::setName)
            .mandatory(DESCRIPTION, Row1::description, Row1::setDescription)
            .build();

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
        String sql = Select.from(Row1.TABLE)
                           .where(Row1.NAME, isEqualTo(Row1.DESCRIPTION))
                           .sql();

        assertThat(sql, is("select ROW1.NAME as ROW1_NAME, ROW1.DESCRIPTION as ROW1_DESCRIPTION from TEST.ROW1 as ROW1 where ROW1.NAME = ROW1.DESCRIPTION"));
    }

    @Test
    public void whereIsEqualToOneColumnWithDefaultAlias() {
        String sql = Select.from(Row1.TABLE.as("w"))
                           .where(Row1.NAME, isEqualTo(Row1.DESCRIPTION))
                           .sql();

        assertThat(sql, is("select w.NAME as w_NAME, w.DESCRIPTION as w_DESCRIPTION from TEST.ROW1 as w where w.NAME = w.DESCRIPTION"));
    }

    @Test
    public void whereIsEqualToOneColumnWithAlias() {
        String sql = Select.from(Row1.TABLE.as("w"))
                           .where(Row1.NAME, isEqualTo("w", Row1.DESCRIPTION))
                           .sql();

        assertThat(sql, is("select w.NAME as w_NAME, w.DESCRIPTION as w_DESCRIPTION from TEST.ROW1 as w where w.NAME = w.DESCRIPTION"));
    }

    @Test
    public void whereIsEqualToOneValue() {
        String sql = Select.from(Row1.TABLE.as("w"))
                           .where(Row1.DESCRIPTION, isEqualTo("fred"))
                           .sql();

        assertThat(sql, is("select w.NAME as w_NAME, w.DESCRIPTION as w_DESCRIPTION from TEST.ROW1 as w where w.DESCRIPTION = ?"));
    }

    @Test
    public void whereIsEqualToTwoValues() {
        String sql = Select.from(Row1.TABLE.as("w"))
                           .where(Row1.DESCRIPTION, isEqualTo("fred"))
                           .and(Row1.NAME, isEqualTo("bob"))
                           .sql();

        assertThat(sql, is("select w.NAME as w_NAME, w.DESCRIPTION as w_DESCRIPTION from TEST.ROW1 as w where (w.DESCRIPTION = ?) and (w.NAME = ?)"));
    }

    @Test
    public void orderByOneColumnDefaultAscending() {
        String sql = Select.from(Row1.TABLE.as("q"))
                           .where(Row1.NAME, isEqualTo("joe"))
                           .orderBy(Row1.DESCRIPTION)
                           .sql();

        assertThat(sql, is("select q.NAME as q_NAME, q.DESCRIPTION as q_DESCRIPTION from TEST.ROW1 as q where q.NAME = ? order by q.DESCRIPTION ascending"));
    }

    @Test
    public void orderByOneColumnAscending() {
        String sql = Select.from(Row1.TABLE.as("q"))
                           .where(Row1.NAME, isEqualTo("joe"))
                           .orderBy(Row1.DESCRIPTION, Order.ASCENDING)
                           .sql();

        assertThat(sql, is("select q.NAME as q_NAME, q.DESCRIPTION as q_DESCRIPTION from TEST.ROW1 as q where q.NAME = ? order by q.DESCRIPTION ascending"));
    }

    @Test
    public void orderByOneColumnDescending() {
        String sql = Select.from(Row1.TABLE.as("q"))
                           .where(Row1.NAME, isEqualTo("joe"))
                           .orderBy(Row1.DESCRIPTION, Order.DESCENDING)
                           .sql();

        assertThat(sql, is("select q.NAME as q_NAME, q.DESCRIPTION as q_DESCRIPTION from TEST.ROW1 as q where q.NAME = ? order by q.DESCRIPTION descending"));
    }

    @Test
    public void noWhereClauseOrderByOneColumnDefaultAscending() {
        String sql = Select.from(Row1.TABLE.as("q"))
                           .orderBy(Row1.NAME)
                           .sql();

        assertThat(sql, is("select q.NAME as q_NAME, q.DESCRIPTION as q_DESCRIPTION from TEST.ROW1 as q order by q.NAME ascending"));
    }

    @Test
    public void optional() throws Exception {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);

        Select.from(Row1.TABLE.as("w"))
            .optional(jdbcTemplate);

        verify(jdbcTemplate, times(1)).query(eq("select w.NAME as w_NAME, w.DESCRIPTION as w_DESCRIPTION from TEST.ROW1 as w"), ArgumentMatchers.<Object[]>any(), ArgumentMatchers.<RowMapper<Row1>>any());
    }

}