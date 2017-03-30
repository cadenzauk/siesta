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
import com.cadenzauk.siesta.testmodel.TestDatabase;
import com.cadenzauk.siesta.testmodel.WidgetRow;
import com.cadenzauk.siesta.testmodel.WidgetViewRow;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

public class SelectProjectionTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private SqlExecutor sqlExecutor;

    @Captor
    private ArgumentCaptor<String> sql;

    @Captor
    private ArgumentCaptor<Object[]> args;

    @Captor
    private ArgumentCaptor<RowMapper<?>> rowMapper;

    @Test
    public void projectColumns() {
        Database database = TestDatabase.testDatabase();
        Alias<WidgetRow> w = database.table(WidgetRow.class).as("w");

        database.from(w)
            .select(WidgetRow::name, "n1")
            .comma(WidgetRow::description, "d1")
            .comma(WidgetRow::manufacturerId, "m1")
            .comma("w", WidgetRow::name, "n2")
            .comma("w", WidgetRow::description, "d2")
            .comma("w", WidgetRow::manufacturerId, "m2")
            .comma(w, WidgetRow::name, "n3")
            .comma(w, WidgetRow::description, "d3")
            .comma(w, WidgetRow::manufacturerId, "m3")
            .where(WidgetRow::name).isEqualTo("Bob")
            .list(sqlExecutor);

        verify(sqlExecutor).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select " +
            "w.NAME as n1, w.DESCRIPTION as d1, w.MANUFACTURER_ID as m1, " +
            "w.NAME as n2, w.DESCRIPTION as d2, w.MANUFACTURER_ID as m2, " +
            "w.NAME as n3, w.DESCRIPTION as d3, w.MANUFACTURER_ID as m3 " +
            "from TEST.WIDGET as w " +
            "where w.NAME = ?"));
        assertThat(args.getValue(), arrayWithSize(1));
        assertThat(args.getValue()[0], is("Bob"));
    }

    @Test
    public void projectIntoObject() {
        Database database = TestDatabase.testDatabase();

        database
            .from(WidgetRow.class, "w")
            .join(ManufacturerRow.class, "m").on(ManufacturerRow::manufacturerId).isEqualTo(WidgetRow::manufacturerId)
            .select(WidgetViewRow.class, "v")
            .with(WidgetRow::widgetId).as(WidgetViewRow::widgetId)
            .with(WidgetRow::name).as(WidgetViewRow::widgetName)
            .with(WidgetRow::description).as(WidgetViewRow::description)
            .with(WidgetRow::manufacturerId).as(WidgetViewRow::manufacturerId)
            .with(ManufacturerRow::name).as(WidgetViewRow::manufacturerName)
            .list(sqlExecutor);

        verify(sqlExecutor).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select w.WIDGET_ID as v_WIDGET_ID, " +
            "w.NAME as v_WIDGET_NAME, " +
            "w.DESCRIPTION as v_DESCRIPTION, " +
            "w.MANUFACTURER_ID as v_MANUFACTURER_ID, " +
            "m.NAME as v_MANUFACTURER_NAME " +
            "from TEST.WIDGET as w " +
            "join TEST.MANUFACTURER as m on m.MANUFACTURER_ID = w.MANUFACTURER_ID"));
        assertThat(args.getValue(), arrayWithSize(0));
    }
}
