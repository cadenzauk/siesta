/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

import static com.cadenzauk.siesta.Table.aTable;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TableBuilderTest {
    public static class TestRow {
        private long id;
        private Optional<String> name;

        public TestRow() {
        }

        public long id() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    @Test
    public void buildReflectively() {
        Table<TestRow> testRowTable = aTable("SCHEMA", "TEST", TestRow::new, TestRow.class).buildReflectively();

        String sql = Select.from(testRowTable.as("t")).sql();

        assertThat(sql, is("select t.ID as t_ID, t.NAME as t_NAME from SCHEMA.TEST as t"));
    }
}
