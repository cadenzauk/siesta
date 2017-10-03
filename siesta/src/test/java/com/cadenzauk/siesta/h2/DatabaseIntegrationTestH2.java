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

package com.cadenzauk.siesta.h2;

import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.DatabaseIntegrationTest;
import com.cadenzauk.siesta.model.ManufacturerRow;
import com.cadenzauk.siesta.model.WidgetRow;
import org.junit.Test;

import java.util.Optional;
import java.util.stream.IntStream;

import static com.cadenzauk.siesta.grammar.expression.Aggregates.count;
import static com.cadenzauk.siesta.model.TestDatabase.testDatabase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DatabaseIntegrationTestH2 extends DatabaseIntegrationTest {
    @Test
    public void selectSubquery() {
        Database database = testDatabase(dataSource);
        long manufacturerId = newId();
        ManufacturerRow manufacturer = ManufacturerRow.newBuilder()
            .manufacturerId(manufacturerId)
            .name(Optional.of("Doc Brown"))
            .build();
        WidgetRow[] widgets = IntStream.range(1, 4)
            .mapToObj(i ->
                WidgetRow.newBuilder()
                    .widgetId(newId())
                    .name("Flux capacitor " + i)
                    .manufacturerId(manufacturerId)
                    .build())
            .toArray(WidgetRow[]::new);
        database.insert(manufacturer);
        database.insert(widgets);

        Tuple2<Long,Integer> result1 = database.from(ManufacturerRow.class)
            .select(ManufacturerRow::manufacturerId)
            .comma(database.from(WidgetRow.class).select(count()).where(WidgetRow::manufacturerId).isEqualTo(ManufacturerRow::manufacturerId))
            .where(ManufacturerRow::name).isEqualTo("Doc Brown")
            .single();
        Tuple2<Long,Integer> result2 = database.from(ManufacturerRow.class)
            .select(ManufacturerRow::manufacturerId)
            .comma(database.from(WidgetRow.class).select(count()).where(WidgetRow::manufacturerId).isEqualTo(ManufacturerRow::manufacturerId), "widget_count")
            .where(ManufacturerRow::name).isEqualTo("Doc Brown")
            .single();
        Tuple2<Long,Integer> result3 = database.from(ManufacturerRow.class)
            .select(ManufacturerRow::manufacturerId)
            .comma(database.from(WidgetRow.class).select(count(), "wcount").where(WidgetRow::manufacturerId).isEqualTo(ManufacturerRow::manufacturerId), "wcount")
            .where(ManufacturerRow::name).isEqualTo("Doc Brown")
            .single();

        assertThat(result1.item1(), is(manufacturerId));
        assertThat(result1.item2(), is(3));
        assertThat(result2.item1(), is(manufacturerId));
        assertThat(result2.item2(), is(3));
        assertThat(result3.item1(), is(manufacturerId));
        assertThat(result3.item2(), is(3));
    }
}
