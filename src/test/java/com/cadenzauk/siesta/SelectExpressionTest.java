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

import com.cadenzauk.siesta.testmodel.ManufacturerRow;
import com.cadenzauk.siesta.testmodel.WidgetRow;
import org.junit.Test;

import java.time.LocalDate;

import static com.cadenzauk.siesta.grammar.expression.ExpressionBuilder.column;
import static com.cadenzauk.siesta.testmodel.TestDatabase.testDatabase;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SelectExpressionTest {
    @Test
    public void isNull() {
        Database database = testDatabase();

        String sql = database.from(WidgetRow.class)
            .where(WidgetRow::description).isNull()
            .sql();

        assertThat(sql, is("select WIDGET.WIDGET_ID as WIDGET_WIDGET_ID, " +
            "WIDGET.NAME as WIDGET_NAME, " +
            "WIDGET.MANUFACTURER_ID as WIDGET_MANUFACTURER_ID, " +
            "WIDGET.DESCRIPTION as WIDGET_DESCRIPTION " +
            "from TEST.WIDGET as WIDGET " +
            "where WIDGET.DESCRIPTION is null"));
    }

    @Test
    public void isNotNull() {
        Database database = testDatabase();

        String sql = database.from(WidgetRow.class)
            .where(WidgetRow::manufacturerId).isNotNull()
            .sql();

        assertThat(sql, is("select WIDGET.WIDGET_ID as WIDGET_WIDGET_ID, " +
            "WIDGET.NAME as WIDGET_NAME, " +
            "WIDGET.MANUFACTURER_ID as WIDGET_MANUFACTURER_ID, " +
            "WIDGET.DESCRIPTION as WIDGET_DESCRIPTION " +
            "from TEST.WIDGET as WIDGET " +
            "where WIDGET.MANUFACTURER_ID is not null"));
    }

    @Test
    public void isIn() {
        Database database = testDatabase();

        String sql = database.from(WidgetRow.class)
            .where(WidgetRow::widgetId).isIn(123L, 456L)
            .sql();

        assertThat(sql, is("select WIDGET.WIDGET_ID as WIDGET_WIDGET_ID, " +
            "WIDGET.NAME as WIDGET_NAME, " +
            "WIDGET.MANUFACTURER_ID as WIDGET_MANUFACTURER_ID, " +
            "WIDGET.DESCRIPTION as WIDGET_DESCRIPTION " +
            "from TEST.WIDGET as WIDGET " +
            "where WIDGET.WIDGET_ID in (?, ?)"));
    }

    @Test
    public void isNotIn() {
        Database database = testDatabase();

        String sql = database.from(WidgetRow.class)
            .where(WidgetRow::widgetId).isNotIn(123L, 456L, 789L)
            .sql();

        assertThat(sql, is("select WIDGET.WIDGET_ID as WIDGET_WIDGET_ID, " +
            "WIDGET.NAME as WIDGET_NAME, " +
            "WIDGET.MANUFACTURER_ID as WIDGET_MANUFACTURER_ID, " +
            "WIDGET.DESCRIPTION as WIDGET_DESCRIPTION " +
            "from TEST.WIDGET as WIDGET " +
            "where WIDGET.WIDGET_ID not in (?, ?, ?)"));
    }

    @Test
    public void isLike() {
        Database database = testDatabase();

        String sql = database.from(WidgetRow.class)
            .where(WidgetRow::name).isLike("abc%")
            .sql();

        assertThat(sql, is("select WIDGET.WIDGET_ID as WIDGET_WIDGET_ID, " +
            "WIDGET.NAME as WIDGET_NAME, " +
            "WIDGET.MANUFACTURER_ID as WIDGET_MANUFACTURER_ID, " +
            "WIDGET.DESCRIPTION as WIDGET_DESCRIPTION " +
            "from TEST.WIDGET as WIDGET " +
            "where WIDGET.NAME like ?"));
    }

    @Test
    public void isLikeEscape() {
        Database database = testDatabase();

        String sql = database.from(WidgetRow.class)
            .where(WidgetRow::name).isLike("abc@%%", "@")
            .sql();

        assertThat(sql, is("select WIDGET.WIDGET_ID as WIDGET_WIDGET_ID, " +
            "WIDGET.NAME as WIDGET_NAME, " +
            "WIDGET.MANUFACTURER_ID as WIDGET_MANUFACTURER_ID, " +
            "WIDGET.DESCRIPTION as WIDGET_DESCRIPTION " +
            "from TEST.WIDGET as WIDGET " +
            "where WIDGET.NAME like ? escape '@'"));
    }

    @Test
    public void isNotLike() {
        Database database = testDatabase();

        String sql = database.from(WidgetRow.class)
            .where(WidgetRow::name).isNotLike("abc%")
            .sql();

        assertThat(sql, is("select WIDGET.WIDGET_ID as WIDGET_WIDGET_ID, " +
            "WIDGET.NAME as WIDGET_NAME, " +
            "WIDGET.MANUFACTURER_ID as WIDGET_MANUFACTURER_ID, " +
            "WIDGET.DESCRIPTION as WIDGET_DESCRIPTION " +
            "from TEST.WIDGET as WIDGET " +
            "where WIDGET.NAME not like ?"));
    }

    @Test
    public void isNotLikeEscape() {
        Database database = testDatabase();

        String sql = database.from(WidgetRow.class)
            .where(WidgetRow::name).isNotLike("abc+%%", "+")
            .sql();

        assertThat(sql, is("select WIDGET.WIDGET_ID as WIDGET_WIDGET_ID, " +
            "WIDGET.NAME as WIDGET_NAME, " +
            "WIDGET.MANUFACTURER_ID as WIDGET_MANUFACTURER_ID, " +
            "WIDGET.DESCRIPTION as WIDGET_DESCRIPTION " +
            "from TEST.WIDGET as WIDGET " +
            "where WIDGET.NAME not like ? escape '+'"));
    }

    @Test
    public void orExpression() {
        Database database = testDatabase();
        Alias<ManufacturerRow> m = database.table(ManufacturerRow.class).as("m");
        String sql = database.from(m)
            .where(column(ManufacturerRow::checked).isLessThan(LocalDate.now())
                .or(ManufacturerRow::checked).isGreaterThan(LocalDate.now())
                .or(ManufacturerRow::name).isEqualTo("Fred"))
            .and(column(ManufacturerRow::name).isEqualTo("Bob")
                .or(ManufacturerRow::checked).isNull())
            .sql();

        assertThat(sql, is("select m.MANUFACTURER_ID as m_MANUFACTURER_ID, m.NAME as m_NAME, m.CHECKED as m_CHECKED " +
            "from TEST.MANUFACTURER as m " +
            "where (m.CHECKED < ? or m.CHECKED > ? or m.NAME = ?) " +
            "and (m.NAME = ? or m.CHECKED is null)"));
    }

    @Test
    public void andExpression() {
        Database database = testDatabase();
        Alias<ManufacturerRow> m = database.table(ManufacturerRow.class).as("m");
        String sql = database.from(m)
            .where(ManufacturerRow::checked).isLessThan(LocalDate.now())
            .and(ManufacturerRow::name).isEqualTo("Fred")
            .and(column(ManufacturerRow::name).isEqualTo("Bob")
                .or(ManufacturerRow::checked).isNull()
            )
            .and(ManufacturerRow::checked).isGreaterThan(LocalDate.now())
            .sql();

        assertThat(sql, is("select m.MANUFACTURER_ID as m_MANUFACTURER_ID, m.NAME as m_NAME, m.CHECKED as m_CHECKED " +
            "from TEST.MANUFACTURER as m " +
            "where m.CHECKED < ? and m.NAME = ? and (m.NAME = ? or m.CHECKED is null) and m.CHECKED > ?"));
    }

    @Test
    public void andAndOrsInWhere() {
        Database database = testDatabase();
        Alias<ManufacturerRow> m = database.table(ManufacturerRow.class).as("m");
        String sql = database.from(m)
            .where(ManufacturerRow::name).isEqualTo("Fred")
            .or(ManufacturerRow::checked).isNull()
            .and(ManufacturerRow::manufacturerId).isGreaterThan(3L)
            .sql();

        assertThat(sql, is("select m.MANUFACTURER_ID as m_MANUFACTURER_ID, m.NAME as m_NAME, m.CHECKED as m_CHECKED " +
            "from TEST.MANUFACTURER as m " +
            "where m.NAME = ? or m.CHECKED is null and m.MANUFACTURER_ID > ?"));
    }

    @Test
    public void between() {
        Database database = testDatabase();
        Alias<ManufacturerRow> m = database.table(ManufacturerRow.class).as("m");
        String sql = database.from(m)
            .where(ManufacturerRow::name).isBetween("Fred").and("Barney")
            .or(ManufacturerRow::checked).isNull()
            .and(ManufacturerRow::manufacturerId).isGreaterThan(3L)
            .sql();

        assertThat(sql, is("select m.MANUFACTURER_ID as m_MANUFACTURER_ID, m.NAME as m_NAME, m.CHECKED as m_CHECKED " +
            "from TEST.MANUFACTURER as m " +
            "where m.NAME between ? and ? " +
            "or m.CHECKED is null and m.MANUFACTURER_ID > ?"));
    }
}
