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

import com.cadenzauk.siesta.dialect.AnsiDialect;
import com.cadenzauk.siesta.model.ManufacturerRow;
import com.cadenzauk.siesta.model.WidgetRow;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.cadenzauk.siesta.grammar.expression.CoalesceFunction.coalesce;
import static com.cadenzauk.siesta.grammar.expression.StringFunctions.substr;
import static com.cadenzauk.siesta.grammar.expression.TypedExpression.column;
import static com.cadenzauk.siesta.grammar.expression.TypedExpression.value;
import static com.cadenzauk.siesta.model.TestDatabase.testDatabase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SelectExpressionTest {
    @Test
    void isNull() {
        Database database = testDatabase(new AnsiDialect());

        String sql = database.from(WidgetRow.class)
            .where(WidgetRow::description).isNull()
            .sql();

        assertThat(sql, is("select WIDGET.WIDGET_ID as WIDGET_WIDGET_ID, " +
            "WIDGET.NAME as WIDGET_NAME, " +
            "WIDGET.MANUFACTURER_ID as WIDGET_MANUFACTURER_ID, " +
            "WIDGET.DESCRIPTION as WIDGET_DESCRIPTION " +
            "from SIESTA.WIDGET WIDGET " +
            "where WIDGET.DESCRIPTION is null"));
    }

    @Test
    void isNotNull() {
        Database database = testDatabase(new AnsiDialect());

        String sql = database.from(WidgetRow.class)
            .where(WidgetRow::manufacturerId).isNotNull()
            .sql();

        assertThat(sql, is("select WIDGET.WIDGET_ID as WIDGET_WIDGET_ID, " +
            "WIDGET.NAME as WIDGET_NAME, " +
            "WIDGET.MANUFACTURER_ID as WIDGET_MANUFACTURER_ID, " +
            "WIDGET.DESCRIPTION as WIDGET_DESCRIPTION " +
            "from SIESTA.WIDGET WIDGET " +
            "where WIDGET.MANUFACTURER_ID is not null"));
    }

    @Test
    void isIn() {
        Database database = testDatabase(new AnsiDialect());

        String sql = database.from(WidgetRow.class)
            .where(WidgetRow::widgetId).isIn(123L, 456L)
            .sql();

        assertThat(sql, is("select WIDGET.WIDGET_ID as WIDGET_WIDGET_ID, " +
            "WIDGET.NAME as WIDGET_NAME, " +
            "WIDGET.MANUFACTURER_ID as WIDGET_MANUFACTURER_ID, " +
            "WIDGET.DESCRIPTION as WIDGET_DESCRIPTION " +
            "from SIESTA.WIDGET WIDGET " +
            "where WIDGET.WIDGET_ID in (?, ?)"));
    }

    @Test
    void isNotIn() {
        Database database = testDatabase(new AnsiDialect());

        String sql = database.from(WidgetRow.class)
            .where(WidgetRow::widgetId).isNotIn(123L, 456L, 789L)
            .sql();

        assertThat(sql, is("select WIDGET.WIDGET_ID as WIDGET_WIDGET_ID, " +
            "WIDGET.NAME as WIDGET_NAME, " +
            "WIDGET.MANUFACTURER_ID as WIDGET_MANUFACTURER_ID, " +
            "WIDGET.DESCRIPTION as WIDGET_DESCRIPTION " +
            "from SIESTA.WIDGET WIDGET " +
            "where WIDGET.WIDGET_ID not in (?, ?, ?)"));
    }

    @Test
    void isLike() {
        Database database = testDatabase(new AnsiDialect());

        String sql = database.from(WidgetRow.class)
            .where(WidgetRow::name).isLike("abc%")
            .sql();

        assertThat(sql, is("select WIDGET.WIDGET_ID as WIDGET_WIDGET_ID, " +
            "WIDGET.NAME as WIDGET_NAME, " +
            "WIDGET.MANUFACTURER_ID as WIDGET_MANUFACTURER_ID, " +
            "WIDGET.DESCRIPTION as WIDGET_DESCRIPTION " +
            "from SIESTA.WIDGET WIDGET " +
            "where WIDGET.NAME like ?"));
    }

    @Test
    void isLikeEscape() {
        Database database = testDatabase(new AnsiDialect());

        String sql = database.from(WidgetRow.class)
            .where(WidgetRow::name).isLike("abc@%%", "@")
            .sql();

        assertThat(sql, is("select WIDGET.WIDGET_ID as WIDGET_WIDGET_ID, " +
            "WIDGET.NAME as WIDGET_NAME, " +
            "WIDGET.MANUFACTURER_ID as WIDGET_MANUFACTURER_ID, " +
            "WIDGET.DESCRIPTION as WIDGET_DESCRIPTION " +
            "from SIESTA.WIDGET WIDGET " +
            "where WIDGET.NAME like ? escape '@'"));
    }

    @Test
    void isNotLike() {
        Database database = testDatabase(new AnsiDialect());

        String sql = database.from(WidgetRow.class)
            .where(WidgetRow::name).isNotLike("abc%")
            .sql();

        assertThat(sql, is("select WIDGET.WIDGET_ID as WIDGET_WIDGET_ID, " +
            "WIDGET.NAME as WIDGET_NAME, " +
            "WIDGET.MANUFACTURER_ID as WIDGET_MANUFACTURER_ID, " +
            "WIDGET.DESCRIPTION as WIDGET_DESCRIPTION " +
            "from SIESTA.WIDGET WIDGET " +
            "where WIDGET.NAME not like ?"));
    }

    @Test
    void isNotLikeEscape() {
        Database database = testDatabase(new AnsiDialect());

        String sql = database.from(WidgetRow.class)
            .where(WidgetRow::name).isNotLike("abc+%%", "+")
            .sql();

        assertThat(sql, is("select WIDGET.WIDGET_ID as WIDGET_WIDGET_ID, " +
            "WIDGET.NAME as WIDGET_NAME, " +
            "WIDGET.MANUFACTURER_ID as WIDGET_MANUFACTURER_ID, " +
            "WIDGET.DESCRIPTION as WIDGET_DESCRIPTION " +
            "from SIESTA.WIDGET WIDGET " +
            "where WIDGET.NAME not like ? escape '+'"));
    }

    @Test
    void orExpression() {
        Database database = testDatabase(new AnsiDialect());
        Alias<ManufacturerRow> m = database.table(ManufacturerRow.class).as("m");
        String sql = database.from(m)
            .where(column(ManufacturerRow::checked).isLessThan(LocalDate.now())
                .or(ManufacturerRow::checked).isGreaterThan(LocalDate.now())
                .or(ManufacturerRow::name).isEqualTo("Fred"))
            .and(column(ManufacturerRow::name).isEqualTo("Bob")
                .or(ManufacturerRow::checked).isNull())
            .sql();

        assertThat(sql, is("select m.MANUFACTURER_ID as m_MANUFACTURER_ID, m.NAME as m_NAME, m.CHECKED as m_CHECKED " +
            "from SIESTA.MANUFACTURER m " +
            "where (m.CHECKED < ? or m.CHECKED > ? or m.NAME = ?) " +
            "and (m.NAME = ? or m.CHECKED is null)"));
    }

    @Test
    void andExpression() {
        Database database = testDatabase(new AnsiDialect());
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
            "from SIESTA.MANUFACTURER m " +
            "where m.CHECKED < ? and m.NAME = ? and (m.NAME = ? or m.CHECKED is null) and m.CHECKED > ?"));
    }

    @Test
    void andAndOrsInWhere() {
        Database database = testDatabase(new AnsiDialect());
        Alias<ManufacturerRow> m = database.table(ManufacturerRow.class).as("m");
        String sql = database.from(m)
            .where(ManufacturerRow::name).isEqualTo("Fred")
            .or(ManufacturerRow::checked).isNull()
            .and(ManufacturerRow::manufacturerId).isGreaterThan(3L)
            .sql();

        assertThat(sql, is("select m.MANUFACTURER_ID as m_MANUFACTURER_ID, m.NAME as m_NAME, m.CHECKED as m_CHECKED " +
            "from SIESTA.MANUFACTURER m " +
            "where m.NAME = ? or m.CHECKED is null and m.MANUFACTURER_ID > ?"));
    }

    @Test
    void between() {
        Database database = testDatabase(new AnsiDialect());
        Alias<ManufacturerRow> m = database.table(ManufacturerRow.class).as("m");
        String sql = database.from(m)
            .where(ManufacturerRow::name).isBetween("Fred").and("Barney")
            .or(ManufacturerRow::checked).isNull()
            .and(ManufacturerRow::manufacturerId).isGreaterThan(3L)
            .sql();

        assertThat(sql, is("select m.MANUFACTURER_ID as m_MANUFACTURER_ID, m.NAME as m_NAME, m.CHECKED as m_CHECKED " +
            "from SIESTA.MANUFACTURER m " +
            "where m.NAME between ? and ? " +
            "or m.CHECKED is null and m.MANUFACTURER_ID > ?"));
    }

    @Test
    void coalesceFunc() {
        Database database = testDatabase(new AnsiDialect());
        Alias<WidgetRow> w = database.table(WidgetRow.class).as("w");
        String sql = database.from(w)
            .select(coalesce(WidgetRow::name).orElse(WidgetRow::description).orElse("Bob"), "name")
            .where(WidgetRow::manufacturerId).isEqualTo(2L)
            .sql();

        assertThat(sql, is("select coalesce(w.NAME, w.DESCRIPTION, ?) as name " +
            "from SIESTA.WIDGET w " +
            "where w.MANUFACTURER_ID = ?"));
    }

    @Test
    void substrFunc() {
        Database database = testDatabase(new AnsiDialect());
        Alias<WidgetRow> w = database.table(WidgetRow.class).as("w");
        String sql = database.from(w)
            .select(substr(w.column(WidgetRow::name), 4), "name")
            .where(WidgetRow::manufacturerId).isEqualTo(2L)
            .sql();

        assertThat(sql, is("select substr(w.NAME, ?) as name " +
            "from SIESTA.WIDGET w " +
            "where w.MANUFACTURER_ID = ?"));
    }

    @Test
    void concat() {
        Database database = testDatabase(new AnsiDialect());
        Alias<WidgetRow> w = database.table(WidgetRow.class).as("w");
        String sql = database.from(w)
            .select(w.column(WidgetRow::name).concat(WidgetRow::description).concat(WidgetRow::manufacturerId), "name")
            .where(w.column(WidgetRow::name).concat(WidgetRow::description).isEqualTo(value("Bob").concat(w.column(WidgetRow::description))))
            .sql();

        assertThat(sql, is("select w.NAME || w.DESCRIPTION || w.MANUFACTURER_ID as name " +
            "from SIESTA.WIDGET w " +
            "where w.NAME || w.DESCRIPTION = ? || w.DESCRIPTION"));
    }
}
