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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class UpdateTest {
    @Test
    public void update() {
        Database database = Database.newBuilder()
            .defaultSchema("TEST")
            .build();
        database.table(WidgetRow.class, t -> t.builder(WidgetRow.Builder.class, WidgetRow.Builder::build));

        String sql = database.update(WidgetRow.class)
            .set(WidgetRow::name).to("Fred")
            .set(WidgetRow::description).to("Bob")
            .where(WidgetRow::widgetId).isEqualTo(1L)
            .sql();

        assertThat(sql, is("update TEST.WIDGET as WIDGET set WIDGET.NAME = ?, WIDGET.DESCRIPTION = ? where WIDGET.WIDGET_ID = ?"));
    }

}