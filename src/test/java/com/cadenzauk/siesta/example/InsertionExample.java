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

import com.cadenzauk.core.tuple.Tuple3;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.IntegrationTest;
import com.cadenzauk.siesta.spring.JdbcTemplateSqlExecutor;
import org.junit.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class InsertionExample extends IntegrationTest {
    @Test
    public void insertOneRowAndReadItBack() {
        Database database = Database.newBuilder()
            .defaultSchema("TEST")
            .defaultSqlExecutor(JdbcTemplateSqlExecutor.of(dataSource))
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
    public void insertSomeGizmosAndReadThemBack() {
        Database database = Database.newBuilder()
            .defaultSchema("TEST")
            .defaultSqlExecutor(JdbcTemplateSqlExecutor.of(dataSource))
            .build();
        ZonedDateTime start = ZonedDateTime.now(ZoneOffset.UTC);
        database.insert(new Manufacturer(2004L, "Gizmos Inc"));
        database.insert(new Manufacturer(2005L, "Acme Inc"));
        database.insert(new Widget(1003L, "Gizmo", 2004L, Optional.empty()));
        database.insert(new Widget(1004L, "Gizmo", 2005L, Optional.of("Acme Gizmo")));
        database.insert(new Widget(1005L, "Gizmo", 2005L, Optional.of("Acme Gizmo Mk II")));
        ZonedDateTime end = ZonedDateTime.now(ZoneOffset.UTC);

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
        assertThat(makersOfGizmos.get(0).item3().isBefore(start), is(false));
        assertThat(makersOfGizmos.get(0).item3().isAfter(end), is(false));
    }
}
