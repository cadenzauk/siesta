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
import com.cadenzauk.core.tuple.Tuple3;
import com.cadenzauk.siesta.spring.JdbcTemplateSqlExecutor;
import com.cadenzauk.siesta.test.model.ManufacturerRow;
import com.cadenzauk.siesta.test.model.WidgetRow;
import com.cadenzauk.siesta.test.model.WidgetViewRow;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static com.cadenzauk.siesta.Aggregates.count;
import static com.cadenzauk.siesta.Aggregates.countDistinct;
import static com.cadenzauk.siesta.Aggregates.max;
import static com.cadenzauk.siesta.Aggregates.min;
import static com.cadenzauk.siesta.grammar.expression.CoalesceFunction.coalesce;
import static com.cadenzauk.siesta.test.model.TestDatabase.testDatabase;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TableIntegrationTest extends IntegrationTest {
    private static final AtomicLong ids = new AtomicLong();

    @Test
    public void selectFromDatabaseOneTable() {
        Database database = testDatabase(dataSource);
        WidgetRow aWidget = WidgetRow.newBuilder()
            .widgetId(newId())
            .manufacturerId(newId())
            .name("Dodacky")
            .description(Optional.of("Thingamibob"))
            .build();
        database.insert(aWidget);

        Optional<WidgetRow> theSame = database.from(WidgetRow.class)
            .where(WidgetRow::widgetId).isEqualTo(aWidget.widgetId())
            .optional();

        assertThat(theSame, is(Optional.of(aWidget)));
    }

    @Test
    public void selectFromDatabaseTwoTables() {
        Database database = testDatabase(dataSource);
        long manufacturerId = newId();
        ManufacturerRow aManufacturer = ManufacturerRow.newBuilder()
            .manufacturerId(manufacturerId)
            .name(Optional.of("Makers"))
            .build();
        WidgetRow aWidget = WidgetRow.newBuilder()
            .widgetId(newId())
            .manufacturerId(manufacturerId)
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
            .and(w, WidgetRow::widgetId).isEqualTo(aWidget.widgetId())
            .optional()
            .map(Tuple2::item1);

        assertThat(theSame, is(Optional.of(aWidget)));
    }

    @Test
    public void update() {
        Database database = testDatabase();
        WidgetRow aWidget = WidgetRow.newBuilder()
            .widgetId(newId())
            .manufacturerId(newId())
            .name("Dodacky")
            .description(Optional.of("Thingamibob"))
            .build();
        JdbcTemplateSqlExecutor sqlExecutor = JdbcTemplateSqlExecutor.of(dataSource);
        database.insert(sqlExecutor, aWidget);

        int updated = database.update(WidgetRow.class, "w")
            .set(WidgetRow::name).to("Sprocket")
            .set(WidgetRow::description).toNull()
            .where(WidgetRow::widgetId).isEqualTo(aWidget.widgetId())
            .execute(sqlExecutor);

        Optional<WidgetRow> sprocket = database.from(WidgetRow.class)
            .where(WidgetRow::widgetId).isEqualTo(aWidget.widgetId())
            .optional(sqlExecutor);
        assertThat(sprocket.map(WidgetRow::name), is(Optional.of("Sprocket")));
        assertThat(sprocket.flatMap(WidgetRow::description), is(Optional.empty()));
        assertThat(updated, is(1));
    }

    @Test
    public void selectIntoView() {
        Database database = testDatabase(dataSource);
        long manufacturerId = newId();
        ManufacturerRow aManufacturer = ManufacturerRow.newBuilder()
            .manufacturerId(manufacturerId)
            .name(Optional.of("Acme"))
            .build();
        WidgetRow aWidget = WidgetRow.newBuilder()
            .widgetId(newId())
            .manufacturerId(manufacturerId)
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
            .where(WidgetRow::widgetId).isEqualTo(aWidget.widgetId())
            .optional();

        assertThat(gizmo.map(WidgetViewRow::widgetName), is(Optional.of("Gizmo")));
        assertThat(gizmo.flatMap(WidgetViewRow::manufacturerName), is(Optional.of("Acme")));
        assertThat(gizmo.flatMap(WidgetViewRow::description), is(Optional.of("Acme's Patent Gizmo")));
    }

    @Test
    public void groupBy() {
        Database database = testDatabase(dataSource);
        long manufacturer1 = newId();
        long manufacturer2 = newId();
        WidgetRow aWidget1 = WidgetRow.newBuilder()
            .widgetId(newId())
            .manufacturerId(manufacturer1)
            .name("Grouper 1")
            .build();
        WidgetRow aWidget2 = WidgetRow.newBuilder()
            .widgetId(newId())
            .manufacturerId(manufacturer1)
            .name("Grouper 2")
            .build();
        WidgetRow aWidget3 = WidgetRow.newBuilder()
            .widgetId(newId())
            .manufacturerId(manufacturer2)
            .name("Grouper 3")
            .build();
        database.insert(aWidget1, aWidget2, aWidget3);

        List<Tuple3<Long,String,String>> result = database.from(WidgetRow.class)
            .select(WidgetRow::manufacturerId).comma(max(WidgetRow::name)).comma(min(WidgetRow::name))
            .where(WidgetRow::widgetId).isIn(aWidget1.widgetId(), aWidget2.widgetId(), aWidget3.widgetId())
            .groupBy(WidgetRow::manufacturerId)
            .orderBy(WidgetRow::manufacturerId, Order.DESC).then(max(WidgetRow::name)).then(min(WidgetRow::name))
            .list();

        assertThat(result.get(0).item1(), is(manufacturer2));
        assertThat(result.get(0).item2(), is("Grouper 3"));
        assertThat(result.get(0).item3(), is("Grouper 3"));
        assertThat(result.get(1).item1(), is(manufacturer1));
        assertThat(result.get(1).item2(), is("Grouper 2"));
        assertThat(result.get(1).item3(), is("Grouper 1"));
    }

    @Test
    public void countAndCountDistinct() {
        Database database = testDatabase(dataSource);
        long manufacturer1 = newId();
        long manufacturer2 = newId();
        WidgetRow aWidget1 = WidgetRow.newBuilder()
            .widgetId(newId())
            .manufacturerId(manufacturer1)
            .name("Gizmo")
            .build();
        WidgetRow aWidget2 = WidgetRow.newBuilder()
            .widgetId(newId())
            .manufacturerId(manufacturer1)
            .name("Gizmo")
            .build();
        WidgetRow aWidget3 = WidgetRow.newBuilder()
            .widgetId(newId())
            .manufacturerId(manufacturer2)
            .name("Gizmo 2")
            .build();
        database.insert(aWidget1, aWidget2, aWidget3);

        List<Tuple3<Long,Integer,Integer>> result = database.from(WidgetRow.class)
            .select(WidgetRow::manufacturerId).comma(countDistinct(WidgetRow::name)).comma(count())
            .where(WidgetRow::widgetId).isIn(aWidget1.widgetId(), aWidget2.widgetId(), aWidget3.widgetId())
            .groupBy(WidgetRow::manufacturerId)
            .orderBy(WidgetRow::manufacturerId, Order.ASC)
            .list();

        assertThat(result.get(0).item1(), is(manufacturer1));
        assertThat(result.get(0).item2(), is(1));
        assertThat(result.get(0).item3(), is(2));
        assertThat(result.get(1).item1(), is(manufacturer2));
        assertThat(result.get(1).item2(), is(1));
        assertThat(result.get(1).item3(), is(1));
    }

    @Test
    public void coalesceFunc() {
        Database database = testDatabase(dataSource);
        long manufacturer1 = newId();
        long manufacturer2 = newId();
        ManufacturerRow twoParts = ManufacturerRow.newBuilder()
            .manufacturerId(manufacturer1)
            .name(Optional.of("Two Parts"))
            .build();
        ManufacturerRow noParts = ManufacturerRow.newBuilder()
            .manufacturerId(manufacturer2)
            .name(Optional.of("No Parts"))
            .build();
        WidgetRow aWidget1 = WidgetRow.newBuilder()
            .widgetId(newId())
            .manufacturerId(manufacturer1)
            .name("Name 1")
            .build();
        WidgetRow aWidget2 = WidgetRow.newBuilder()
            .widgetId(newId())
            .manufacturerId(manufacturer1)
            .name("Name 2")
            .description(Optional.of("Description 2"))
            .build();
        database.insert(noParts);
        database.insert(twoParts);
        database.insert(aWidget1);
        database.insert(aWidget2);

        List<Tuple2<String,String>> result = database.from(ManufacturerRow.class, "m")
            .leftJoin(WidgetRow.class, "w").on(WidgetRow::manufacturerId).isEqualTo(ManufacturerRow::manufacturerId)
            .select(ManufacturerRow::name)
            .comma(coalesce(WidgetRow::description).orElse(WidgetRow::name).orElse("-- no parts --"))
            .where(ManufacturerRow::manufacturerId).isIn(manufacturer1, manufacturer2)
            .orderBy(ManufacturerRow::manufacturerId).then(WidgetRow::name)
            .list();

        assertThat(result, hasSize(3));
        assertThat(result.get(0).item1(), is("Two Parts"));
        assertThat(result.get(0).item2(), is("Name 1"));
        assertThat(result.get(1).item1(), is("Two Parts"));
        assertThat(result.get(1).item2(), is("Description 2"));
        assertThat(result.get(2).item1(), is("No Parts"));
        assertThat(result.get(2).item2(), is("-- no parts --"));
    }

    @Test
    public void leftJoinOfMissingIsNull() {
        Database database = testDatabase(dataSource);
        long manufacturer1 = newId();
        long manufacturer2 = newId();
        ManufacturerRow twoParts = ManufacturerRow.newBuilder()
            .manufacturerId(manufacturer1)
            .name(Optional.of("Has a Part"))
            .build();
        ManufacturerRow noParts = ManufacturerRow.newBuilder()
            .manufacturerId(manufacturer2)
            .name(Optional.of("Has No Parts"))
            .build();
        WidgetRow aWidget1 = WidgetRow.newBuilder()
            .widgetId(newId())
            .manufacturerId(manufacturer1)
            .name("Name 1")
            .build();
        database.insert(noParts, twoParts);
        database.insert(aWidget1);

        List<Tuple2<ManufacturerRow,WidgetRow>> result = database.from(ManufacturerRow.class, "m")
            .leftJoin(WidgetRow.class, "w").on(WidgetRow::manufacturerId).isEqualTo(ManufacturerRow::manufacturerId)
            .where(ManufacturerRow::manufacturerId).isIn(manufacturer1, manufacturer2)
            .orderBy(ManufacturerRow::manufacturerId)
            .list();

        assertThat(result, hasSize(2));
        assertThat(result.get(0).item1().name(), is(Optional.of("Has a Part")));
        assertThat(result.get(0).item2().name(), is("Name 1"));
        assertThat(result.get(1).item1().name(), is(Optional.of("Has No Parts")));
        assertThat(result.get(1).item2(), nullValue());
    }

    private static long newId() {
        return ids.incrementAndGet();
    }
}