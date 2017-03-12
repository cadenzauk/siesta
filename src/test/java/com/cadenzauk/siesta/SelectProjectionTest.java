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

public class SelectProjectionTest {
    @Test
    public void projectColumns() {
        Database database = Database.newBuilder()
            .defaultSchema("PROJ")
            .table(WidgetRow.class, t -> t.builder(WidgetRow.Builder::build))
            .build();

        String sql = database.from(WidgetRow.class, "w")
            .select(WidgetRow::name, "n").comma(WidgetRow::description, "d").comma(WidgetRow::manufacturerId, "m")
            .where(WidgetRow::name).isEqualTo("Bob")
            .sql();

        assertThat(sql, is("select w.NAME as n, " +
            "w.DESCRIPTION as d, " +
            "w.MANUFACTURER_ID as m " +
            "from TEST.WIDGET as w " +
            "where w.NAME = ?"));
    }
}
