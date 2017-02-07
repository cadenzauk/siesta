/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import org.junit.Test;

import static com.cadenzauk.siesta.Table.aTable;
import static com.cadenzauk.siesta.Conditions.isEqualTo;
import static com.cadenzauk.siesta.Conditions.isGreaterThan;
import static com.cadenzauk.siesta.Conditions.isNotEqualTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class Select2Test {
    private static class Row1 {
        private final static Column<String, Row1> NAME = Column.aColumn("NAME", DataType.STRING, Row1.class);
        private final static Column<String, Row1> DESCRIPTION = Column.aColumn("DESCRIPTION", DataType.STRING, Row1.class);

        private final static Table<Row1> TABLE = aTable("TEST", "ROW1", Row1::new, Row1.class)
            .mandatory(NAME, Row1::name, Row1::setName)
            .mandatory(DESCRIPTION, Row1::description, Row1::setDescription)
            .build();

        private String name;
        private String description;

        private String name() {
            return name;
        }

        private void setName(String name) {
            this.name = name;
        }

        private String description() {
            return description;
        }

        private void setDescription(String description) {
            this.description = description;
        }
    }

    private static class Row2 {
        private final static Column<String, Row2> NAME = Column.aColumn("NAME", DataType.STRING, Row2.class);
        private final static Column<String, Row2> DESCRIPTION = Column.aColumn("DESCRIPTION", DataType.STRING, Row2.class);

        private final static Table<Row2> TABLE = aTable("TEST", "ROW2", Row2::new, Row2.class)
            .mandatory(NAME, Row2::name, Row2::setName)
            .mandatory(DESCRIPTION, Row2::description, Row2::setDescription)
            .build();

        private String name;
        private String description;

        private String name() {
            return name;
        }

        private void setName(String name) {
            this.name = name;
        }

        private String description() {
            return description;
        }

        private void setDescription(String description) {
            this.description = description;
        }
    }

    @Test
    public void innerJoin() {
        String sql = Select.from(Row1.TABLE.as("r1"))
            .join(Row2.TABLE.as("r2")).on(Row2.NAME, isEqualTo(Row1.NAME))
            .sql();

        assertThat(sql, is("select r1.NAME as r1_NAME, r1.DESCRIPTION as r1_DESCRIPTION, r2.NAME as r2_NAME, r2.DESCRIPTION as r2_DESCRIPTION from TEST.ROW1 as r1 join TEST.ROW2 as r2 on r2.NAME = r1.NAME"));
    }

    @Test
    public void leftOuterJoin() {
        String sql = Select.from(Row1.TABLE.as("r1"))
            .leftJoin(Row2.TABLE.as("r2")).on(Row2.NAME, isEqualTo(Row1.NAME))
            .sql();

        assertThat(sql, is("select r1.NAME as r1_NAME, r1.DESCRIPTION as r1_DESCRIPTION, r2.NAME as r2_NAME, r2.DESCRIPTION as r2_DESCRIPTION from TEST.ROW1 as r1 left join TEST.ROW2 as r2 on r2.NAME = r1.NAME"));
    }

    @Test
    public void rightOuterJoin() {
        String sql = Select.from(Row1.TABLE.as("r1"))
            .rightJoin(Row2.TABLE.as("r2")).on(Row2.NAME, isEqualTo(Row1.NAME))
            .sql();

        assertThat(sql, is("select r1.NAME as r1_NAME, r1.DESCRIPTION as r1_DESCRIPTION, r2.NAME as r2_NAME, r2.DESCRIPTION as r2_DESCRIPTION from TEST.ROW1 as r1 right join TEST.ROW2 as r2 on r2.NAME = r1.NAME"));
    }

    @Test
    public void fullOuterJoin() {
        String sql = Select.from(Row1.TABLE.as("r1"))
            .fullOuterJoin(Row2.TABLE.as("r2")).on(Row2.NAME, isEqualTo(Row1.NAME))
            .sql();

        assertThat(sql, is("select r1.NAME as r1_NAME, r1.DESCRIPTION as r1_DESCRIPTION, r2.NAME as r2_NAME, r2.DESCRIPTION as r2_DESCRIPTION from TEST.ROW1 as r1 full outer join TEST.ROW2 as r2 on r2.NAME = r1.NAME"));
    }

    @Test
    public void innerJoinWhereOneValue() {
        String sql = Select.from(Row1.TABLE.as("r1"))
            .join(Row2.TABLE.as("r2")).on(Row2.NAME, isEqualTo(Row1.NAME))
            .where(Row1.DESCRIPTION, isGreaterThan(""))
            .sql();

        assertThat(sql, is("select r1.NAME as r1_NAME, r1.DESCRIPTION as r1_DESCRIPTION, r2.NAME as r2_NAME, r2.DESCRIPTION as r2_DESCRIPTION from TEST.ROW1 as r1 join TEST.ROW2 as r2 on r2.NAME = r1.NAME where r1.DESCRIPTION > ?"));
    }

    @Test
    public void innerJoinWhereOneValueAndOneColumn() {
        String sql = Select.from(Row1.TABLE.as("r1"))
            .join(Row2.TABLE.as("r2")).on(Row2.NAME, isEqualTo(Row1.NAME))
            .where(Row1.DESCRIPTION, isGreaterThan(""))
            .and(Row2.NAME, isNotEqualTo(Row1.NAME))
            .sql();

        assertThat(sql, is("select r1.NAME as r1_NAME, r1.DESCRIPTION as r1_DESCRIPTION, r2.NAME as r2_NAME, r2.DESCRIPTION as r2_DESCRIPTION from TEST.ROW1 as r1 join TEST.ROW2 as r2 on r2.NAME = r1.NAME where (r1.DESCRIPTION > ?) and (r2.NAME <> r1.NAME)"));
    }

}
