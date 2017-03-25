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

import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.siesta.spring.JdbcTemplateSqlExecutor;
import com.cadenzauk.siesta.testmodel.ManufacturerRow;
import com.cadenzauk.siesta.testmodel.WidgetRow;
import com.cadenzauk.siesta.testmodel.WidgetViewRow;
import org.junit.Test;

import java.util.Optional;

import static com.cadenzauk.siesta.testmodel.TestDatabase.testDatabase;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TableIntegrationTest extends IntegrationTest {
    @Test
    public void selectFromDatabaseOneTable() {
        Database database = testDatabase(dataSource);

        WidgetRow aWidget = WidgetRow.newBuilder()
            .widgetId(1)
            .manufacturerId(2)
            .name("Dodacky")
            .description(Optional.of("Thingamibob"))
            .build();

        database.insert(aWidget);

        Optional<WidgetRow> theSame = database.from(WidgetRow.class)
            .where(WidgetRow::widgetId).isEqualTo(1L)
            .optional();

        assertThat(theSame, is(Optional.of(aWidget)));
    }

    @Test
    public void selectFromDatabaseTwoTables() {
        Database database = testDatabase(dataSource);
        ManufacturerRow aManufacturer = ManufacturerRow.newBuilder()
            .manufacturerId(2)
            .name(Optional.of("Makers"))
            .build();
        WidgetRow aWidget = WidgetRow.newBuilder()
            .widgetId(2)
            .manufacturerId(2)
            .name("Dodacky")
            .description(Optional.of("Thingamibob"))
            .build();
        database.insert(aManufacturer);
        database.insert(aWidget);

        Alias<WidgetRow> w = database.table(WidgetRow.class).as("w");
        Alias<ManufacturerRow> m = database.table(ManufacturerRow.class).as("m");
        Optional<WidgetRow> theSame = database.from(w)
            .join(m)
            .on(m, ManufacturerRow::manufacturerId).isEqualTo(w, WidgetRow::manufacturerId)
            .where(w, WidgetRow::name).isEqualTo("Dodacky")
            .and(w, WidgetRow::widgetId).isEqualTo(2L)
            .optional()
            .map(Tuple2::item1);

        assertThat(theSame, is(Optional.of(aWidget)));
    }

    @Test
    public void update() {
        Database database = testDatabase(dataSource);
        WidgetRow aWidget = WidgetRow.newBuilder()
            .widgetId(3)
            .manufacturerId(2)
            .name("Dodacky")
            .description(Optional.of("Thingamibob"))
            .build();
        database.insert(aWidget);

        database.update(WidgetRow.class)
            .set(WidgetRow::name).to("Sprocket")
            .set(WidgetRow::description).toNull()
            .where(WidgetRow::widgetId).isEqualTo(3L)
            .execute();

        Optional<WidgetRow> sprocket = database.from(WidgetRow.class)
            .where(WidgetRow::widgetId).isEqualTo(3L)
            .optional();

        assertThat(sprocket.map(WidgetRow::name), is(Optional.of("Sprocket")));
        assertThat(sprocket.flatMap(WidgetRow::description), is(Optional.empty()));
    }

    @Test
    public void selectIntoView() {
        Database database = testDatabase(dataSource);
        ManufacturerRow aManufacturer = ManufacturerRow.newBuilder()
            .manufacturerId(5)
            .name(Optional.of("Acme"))
            .build();
        WidgetRow aWidget = WidgetRow.newBuilder()
            .widgetId(4)
            .manufacturerId(5)
            .name("Gizmo")
            .description(Optional.of("Acme's Patent Gizmo"))
            .build();
        database.insert(aManufacturer);
        database.insert(aWidget);

        Optional<WidgetViewRow> gizmo = database
            .from(WidgetRow.class, "w")
            .leftJoin(ManufacturerRow.class, "m").on(ManufacturerRow::manufacturerId).isEqualTo(WidgetRow::manufacturerId)
            .select(WidgetViewRow.class, "v")
            .with(WidgetRow::widgetId).as(WidgetViewRow::widgetId)
            .with(WidgetRow::name).as(WidgetViewRow::widgetName)
            .with(WidgetRow::description).as(WidgetViewRow::description)
            .with(WidgetRow::manufacturerId).as(WidgetViewRow::manufacturerId)
            .with(ManufacturerRow::name).as(WidgetViewRow::manufacturerName)
            .where(WidgetRow::widgetId).isEqualTo(4L)
            .optional();

        assertThat(gizmo.map(WidgetViewRow::widgetName), is(Optional.of("Gizmo")));
        assertThat(gizmo.flatMap(WidgetViewRow::manufacturerName), is(Optional.of("Acme")));
        assertThat(gizmo.flatMap(WidgetViewRow::description), is(Optional.of("Acme's Patent Gizmo")));
    }
}