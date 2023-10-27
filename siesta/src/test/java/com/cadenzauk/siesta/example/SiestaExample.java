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

package com.cadenzauk.siesta.example;

import com.cadenzauk.core.lang.CompositeAutoCloseable;
import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.core.tuple.Tuple3;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.IntegrationTest;
import com.cadenzauk.siesta.jdbc.JdbcSqlExecutor;
import com.cadenzauk.siesta.model.ManufacturerId;
import com.cadenzauk.siesta.model.WidgetId;
import com.cadenzauk.siesta.model.WidgetRowWithTypeSafeId;
import com.cadenzauk.siesta.type.DbTypeId;
import org.exparity.hamcrest.date.ZonedDateTimeMatchers;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static com.cadenzauk.siesta.grammar.expression.Aggregates.countDistinct;
import static com.cadenzauk.siesta.grammar.expression.DateFunctions.currentDate;
import static com.cadenzauk.siesta.grammar.expression.DateFunctions.currentTimestamp;
import static com.cadenzauk.siesta.grammar.expression.TypedExpression.literal;
import static org.exparity.hamcrest.date.ZonedDateTimeMatchers.after;
import static org.exparity.hamcrest.date.ZonedDateTimeMatchers.before;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

class SiestaExample extends IntegrationTest {
    @Test
    void insertOneRowAndReadItBack() {
        Database database = Database.newBuilder()
            .defaultSchema("SIESTA")
            .defaultSqlExecutor(JdbcSqlExecutor.of(dataSource))
            .build();

        Widget sprocket = new Widget(1001L, "Sprocket", 4L, Optional.empty());
        database.insert(sprocket);

        Optional<Widget> widgetNumberOne = database.from(Widget.class)
            .where(Widget::widgetId).isEqualTo(1001L)
            .optional();

        List<Widget> sprockets = database.from(Widget.class)
            .where(Widget::name).isEqualTo("Sprocket")
            .list();

        assertThat(widgetNumberOne.isPresent(), is(true));
        assertThat(widgetNumberOne.map(Widget::name), is(Optional.of("Sprocket")));
        assertThat(sprockets, hasSize(1));
        assertThat(sprockets.get(0).widgetId(), is(1001L));
    }

    @Test
    void insertSomeGizmosAndReadThemBack() {
        Database database = Database.newBuilder()
            .defaultSchema("SIESTA")
            .defaultSqlExecutor(JdbcSqlExecutor.of(dataSource))
            .build();
        ZonedDateTime start = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
        database.insert(
            new Manufacturer(2004L, "Gizmos Inc"),
            new Manufacturer(2005L, "Acme Inc"));
        database.insert(
            new Widget(1003L, "Gizmo", 2004L, Optional.empty()),
            new Widget(1004L, "Gizmo", 2005L, Optional.of("Acme Gizmo")),
            new Widget(1005L, "Gizmo", 2005L, Optional.of("Acme Gizmo Mk II")));
        ZonedDateTime end = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);

        List<Tuple3<String,String,ZonedDateTime>> makersOfGizmos = database.from(Widget.class, "w")
            .join(Manufacturer.class, "m").on(Manufacturer::manufacturerId).isEqualTo(Widget::manufacturerId)
            .select(Manufacturer::name).comma(Widget::description).comma(Manufacturer::insertionTs)
            .where(Widget::name).isEqualTo("Gizmo")
            .orderBy(Widget::widgetId)
            .list();

        assertThat(makersOfGizmos, hasSize(3));
        assertThat(makersOfGizmos.get(0).item1(), is("Gizmos Inc"));
        assertThat(makersOfGizmos.get(1).item1(), is("Acme Inc"));
        assertThat(makersOfGizmos.get(2).item1(), is("Acme Inc"));
        assertThat(makersOfGizmos.get(0).item3(), not(before(start)));
        assertThat(makersOfGizmos.get(0).item3(), not(after(end)));
    }

    private static class ManufacturerSummary {
        private final String name;
        private final int numberOfPartsSupplied;

        private ManufacturerSummary(String name, int numberOfPartsSupplied) {
            this.name = name;
            this.numberOfPartsSupplied = numberOfPartsSupplied;
        }

        String name() {
            return name;
        }

        int numberOfPartsSupplied() {
            return numberOfPartsSupplied;
        }
    }

    @Test
    void selectIntoObject() {
        Database database = Database.newBuilder()
            .defaultSchema("SIESTA")
            .defaultSqlExecutor(JdbcSqlExecutor.of(dataSource))
            .build();
        database.insert(
            new Manufacturer(2006L, "Spacely Space Sprockets, Inc"),
            new Manufacturer(2007L, "Cogswell's Cogs"),
            new Manufacturer(2008L, "Orbit City Gears"));
        database.insert(
            new Widget(1006L, "Cog", 2006L, Optional.of("Spacely Sprocket")),
            new Widget(1007L, "Cog", 2007L, Optional.of("Cogswell Cog")),
            new Widget(1008L, "Cog", 2007L, Optional.of("Cogswell Sprocket")));

        List<Tuple2<String,Integer>> partCountsBySupplier = database.from(Manufacturer.class, "m")
            .leftJoin(Widget.class, "w").on(Widget::manufacturerId).isEqualTo(Manufacturer::manufacturerId)
            .select(Manufacturer::name).comma(countDistinct(Widget::widgetId))
            .where(Manufacturer::manufacturerId).isIn(2006L, 2007L, 2008L)
            .groupBy(Manufacturer::manufacturerId)
            .orderBy(Manufacturer::manufacturerId)
            .list();
        List<ManufacturerSummary> manufacturerSummaries = database.from(Manufacturer.class, "m")
            .leftJoin(Widget.class, "w").on(Widget::manufacturerId).isEqualTo(Manufacturer::manufacturerId)
            .selectInto(ManufacturerSummary.class)
            .with(Manufacturer::name).as(ManufacturerSummary::name)
            .with(countDistinct(Widget::widgetId)).as(ManufacturerSummary::numberOfPartsSupplied)
            .where(Manufacturer::manufacturerId).isIn(2006L, 2007L, 2008L)
            .groupBy(Manufacturer::manufacturerId)
            .orderBy(Manufacturer::manufacturerId)
            .list();
        List<ManufacturerSummary> nonSuppliers = database.from(Manufacturer.class, "m")
            .leftJoin(Widget.class, "w").on(Widget::manufacturerId).isEqualTo(Manufacturer::manufacturerId)
            .selectInto(ManufacturerSummary.class)
            .with(Manufacturer::name).as(ManufacturerSummary::name)
            .with(countDistinct(Widget::widgetId)).as(ManufacturerSummary::numberOfPartsSupplied)
            .where(Manufacturer::manufacturerId).isIn(2006L, 2007L, 2008L)
            .groupBy(Manufacturer::manufacturerId)
            .having(countDistinct(Widget::widgetId)).isEqualTo(0)
            .orderBy(Manufacturer::manufacturerId)
            .list();

        try (CompositeAutoCloseable autoCloseable = new CompositeAutoCloseable()) {
            database.from(Manufacturer.class)
                .select(Manufacturer::name)
                .where(Manufacturer::manufacturerId).isEqualTo(1L)
                .stream(autoCloseable)
                .forEach(System.out::println);
        }
        assertThat(partCountsBySupplier, hasSize(3));
        assertThat(partCountsBySupplier.get(0).item1(), is("Spacely Space Sprockets, Inc"));
        assertThat(partCountsBySupplier.get(1).item1(), is("Cogswell's Cogs"));
        assertThat(partCountsBySupplier.get(2).item1(), is("Orbit City Gears"));
        assertThat(partCountsBySupplier.get(0).item2(), is(1));
        assertThat(partCountsBySupplier.get(1).item2(), is(2));
        assertThat(partCountsBySupplier.get(2).item2(), is(0));

        assertThat(manufacturerSummaries, hasSize(3));
        assertThat(manufacturerSummaries.get(0).name(), is("Spacely Space Sprockets, Inc"));
        assertThat(manufacturerSummaries.get(1).name(), is("Cogswell's Cogs"));
        assertThat(manufacturerSummaries.get(2).name(), is("Orbit City Gears"));
        assertThat(manufacturerSummaries.get(0).numberOfPartsSupplied(), is(1));
        assertThat(manufacturerSummaries.get(1).numberOfPartsSupplied(), is(2));
        assertThat(manufacturerSummaries.get(2).numberOfPartsSupplied(), is(0));

        assertThat(nonSuppliers, hasSize(1));
        assertThat(nonSuppliers.get(0).name(), is("Orbit City Gears"));
    }

    @Test
    void currentDateTest() {
        Database database = Database.newBuilder()
            .defaultSchema("TEST")
            .defaultSqlExecutor(JdbcSqlExecutor.of(dataSource))
            .build();

        Tuple2<LocalDate,ZonedDateTime> today = database
            .select(currentDate()).comma(currentTimestamp())
            .single();

        System.out.println(today);
    }

    @Test
    void typeSafeIdExample() {
        Database database = Database.newBuilder()
            .defaultSqlExecutor(JdbcSqlExecutor.of(dataSource))
            .table(Widget.class, t -> t
                .column(Widget::description, "WIDGET_DESC"))
            .adapter(WidgetId.class, DbTypeId.BIGINT, WidgetId::id, WidgetId::new)
            .adapter(ManufacturerId.class, DbTypeId.BIGINT, ManufacturerId::id, ManufacturerId::new)
            .build();

        WidgetId widgetId = new WidgetId(newId());
        ManufacturerId manufacturerId = new ManufacturerId(newId());
        WidgetRowWithTypeSafeId widget = WidgetRowWithTypeSafeId.newBuilder()
            .widgetId(widgetId)
            .manufacturerId(manufacturerId)
            .name("Safety Gadget")
            .build();
        database.insert(widget);

        WidgetRowWithTypeSafeId result = database.from(WidgetRowWithTypeSafeId.class)
            .where(WidgetRowWithTypeSafeId::widgetId).isEqualTo(widgetId)
            .or(WidgetRowWithTypeSafeId::manufacturerId).isEqualTo(literal(manufacturerId))
            .single();

        assertThat(result.name(), is("Safety Gadget"));
    }
}
