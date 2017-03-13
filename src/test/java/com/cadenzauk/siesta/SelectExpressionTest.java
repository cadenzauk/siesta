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

import com.cadenzauk.siesta.testmodel.WidgetRow;
import org.junit.Test;

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
}
