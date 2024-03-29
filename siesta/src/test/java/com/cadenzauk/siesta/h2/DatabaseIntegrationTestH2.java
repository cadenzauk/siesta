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
import com.cadenzauk.core.tuple.Tuple5;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.DatabaseIntegrationTest;
import com.cadenzauk.siesta.model.ManufacturerRow;
import com.cadenzauk.siesta.model.MoneyAmount;
import com.cadenzauk.siesta.model.PartRow;
import com.cadenzauk.siesta.model.SalespersonRow;
import com.cadenzauk.siesta.model.WidgetRow;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.stream.IntStream;

import static com.cadenzauk.core.RandomValues.randomBigDecimal;
import static com.cadenzauk.core.RandomValues.randomOf;
import static com.cadenzauk.siesta.grammar.expression.Aggregates.count;
import static com.cadenzauk.siesta.grammar.expression.TypedExpression.column;
import static com.cadenzauk.siesta.model.TestDatabase.testDatabase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class DatabaseIntegrationTestH2 extends DatabaseIntegrationTest {
    @Test
    void selectSubquery() {
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

    @Test
    void embeddedThatIsAllNullsComesBackEmpty() {
        Database database = testDatabase(dataSource);
        long partId = newId();
        database.insert(PartRow.newBuilder()
            .partId(partId)
            .description("ABC")
            .widgetId(3L)
            .purchasePrice(null)
            .retailPrice(Optional.empty())
            .build());

        Tuple5<PartRow,MoneyAmount,MoneyAmount,String,String> result = database.from(PartRow.class)
            .select(PartRow.class)
            .comma(PartRow::purchasePrice, "pp")
            .comma(PartRow::retailPrice, "rp")
            .comma(column(PartRow::purchasePrice).dot(MoneyAmount::currency), "pc")
            .comma(column(PartRow::retailPrice).dot(MoneyAmount::currency), "rc")
            .where(PartRow::partId).isEqualTo(partId)
            .single();

        assertThat(result.item1().purchasePrice(), nullValue());
        assertThat(result.item1().retailPrice(), is(Optional.empty()));
        assertThat(result.item2(), nullValue());
        assertThat(result.item3(), nullValue());
        assertThat(result.item4(), nullValue());
        assertThat(result.item5(), nullValue());
    }

    @Test
    void embeddedInsertedAndReadBack() {
        Database database = testDatabase(dataSource);
        long partId = newId();
        MoneyAmount purchasePrice = new MoneyAmount(randomBigDecimal(10, 2), randomOf("USD", "GBP", "EUR"));
        MoneyAmount retailPrice = new MoneyAmount(randomBigDecimal(10, 2), randomOf("NZD", "AUD", "SAR"));
        database.insert(PartRow.newBuilder()
            .partId(partId)
            .description("ABC")
            .widgetId(3L)
            .purchasePrice(purchasePrice)
            .retailPrice(Optional.of(retailPrice))
            .build());

        Tuple5<PartRow,MoneyAmount,MoneyAmount,String,String> result = database.from(PartRow.class)
            .select(PartRow.class)
            .comma(PartRow::purchasePrice, "pp")
            .comma(PartRow::retailPrice, "rp")
            .comma(column(PartRow::purchasePrice).dot(MoneyAmount::currency), "pc")
            .comma(column(PartRow::retailPrice).dot(MoneyAmount::currency), "rc")
            .where(PartRow::partId).isEqualTo(partId)
            .single();

        assertThat(result.item1().purchasePrice(), is(purchasePrice));
        assertThat(result.item1().retailPrice(), is(Optional.of(retailPrice)));
        assertThat(result.item2(), is(purchasePrice));
        assertThat(result.item3(), is(retailPrice));
        assertThat(result.item4(), is(purchasePrice.currency()));
        assertThat(result.item5(), is(retailPrice.currency()));
    }

    @Test
    void ormUpdate() {
        Database database = testDatabase(dataSource);
        SalespersonRow salespersonRow = aRandomSalesperson();
        SalespersonRow updated = SalespersonRow.newBuilder(salespersonRow)
            .numberOfSales(salespersonRow.numberOfSales() + 10)
            .build();
        database.insert(salespersonRow);

        database.updateRow(updated);

        Integer valueInDatabase = database.from(SalespersonRow.class)
            .select(SalespersonRow::numberOfSales)
            .where(SalespersonRow::salespersonId).isEqualTo(salespersonRow.salespersonId())
            .single();

        assertThat(valueInDatabase, is(updated.numberOfSales()));
    }

    @Test
    void ormDelete() {
        Database database = testDatabase(dataSource);
        SalespersonRow salespersonRow = aRandomSalesperson();
        database.insert(salespersonRow);

        int count = countOf(database, salespersonRow);
        assertThat(count, is(1));

        database.delete(salespersonRow);

        count = countOf(database, salespersonRow);
        assertThat(count, is(0));
    }

    private Integer countOf(Database database, SalespersonRow salespersonRow) {
        return database.from(SalespersonRow.class)
            .select(count())
            .where(SalespersonRow::salespersonId).isEqualTo(salespersonRow.salespersonId())
            .single();
    }
}
