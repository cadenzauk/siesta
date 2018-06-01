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

import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.dialect.AnsiDialect;
import com.cadenzauk.siesta.model.ManufacturerRow;
import com.cadenzauk.siesta.model.TestDatabase;
import com.cadenzauk.siesta.model.WidgetRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.cadenzauk.siesta.grammar.expression.Aggregates.max;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsArrayWithSize.arrayWithSize;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SelectGroupByTest {
    @Mock
    private Transaction transaction;

    @Captor
    private ArgumentCaptor<String> sql;

    @Captor
    private ArgumentCaptor<Object[]> args;

    @Captor
    private ArgumentCaptor<RowMapper<?>> rowMapper;

    @Test
    void groupBy1() {
        Database database = TestDatabase.testDatabase(new AnsiDialect());

        database.from(WidgetRow.class, "w")
            .select(WidgetRow::manufacturerId).comma(max(WidgetRow::name))
            .groupBy(WidgetRow::manufacturerId)
            .list(transaction);

        verify(transaction).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select w.MANUFACTURER_ID as w_MANUFACTURER_ID, max(w.NAME) as max_w_NAME " +
            "from SIESTA.WIDGET w " +
            "group by w.MANUFACTURER_ID"));
        assertThat(args.getValue(), arrayWithSize(0));
    }

    @Test
    void groupBy2() {
        Database database = TestDatabase.testDatabase(new AnsiDialect());

        database.from(WidgetRow.class, "w")
            .select(WidgetRow::manufacturerId).comma(WidgetRow::description).comma(max(WidgetRow::name))
            .groupBy(WidgetRow::manufacturerId).comma(WidgetRow::description)
            .list(transaction);

        verify(transaction).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select w.MANUFACTURER_ID as w_MANUFACTURER_ID, w.DESCRIPTION as w_DESCRIPTION, max(w.NAME) as max_w_NAME " +
            "from SIESTA.WIDGET w " +
            "group by w.MANUFACTURER_ID, w.DESCRIPTION"));
        assertThat(args.getValue(), arrayWithSize(0));
    }

    @Test
    void whereGroupBy() {
        Database database = TestDatabase.testDatabase(new AnsiDialect());

        database.from(WidgetRow.class, "w")
            .select(WidgetRow::manufacturerId, "id").comma(max(WidgetRow::name), "name")
            .where(WidgetRow::description).isLike("ABC%")
            .groupBy(WidgetRow::manufacturerId)
            .list(transaction);

        verify(transaction).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select w.MANUFACTURER_ID as id, max(w.NAME) as name from SIESTA.WIDGET w " +
            "where w.DESCRIPTION like ? " +
            "group by w.MANUFACTURER_ID"));
        assertThat(args.getValue(), arrayWithSize(1));
        assertThat(args.getValue()[0], is("ABC%"));
    }

    @Test
    void joinGroupBy() {
        Database database = TestDatabase.testDatabase(new AnsiDialect());

        database.from(WidgetRow.class, "w")
            .leftJoin(ManufacturerRow.class, "m").on(ManufacturerRow::manufacturerId).isEqualTo(WidgetRow::manufacturerId)
            .select(WidgetRow::manufacturerId, "id").comma(ManufacturerRow::name, "manufacturer").comma(max(WidgetRow::name), "name")
            .where(WidgetRow::description).isLike("ABC%")
            .groupBy(WidgetRow::manufacturerId).comma(ManufacturerRow::name)
            .list(transaction);

        verify(transaction).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select w.MANUFACTURER_ID as id, m.NAME as manufacturer, max(w.NAME) as name " +
            "from SIESTA.WIDGET w " +
            "left join SIESTA.MANUFACTURER m on m.MANUFACTURER_ID = w.MANUFACTURER_ID " +
            "where w.DESCRIPTION like ? " +
            "group by w.MANUFACTURER_ID, m.NAME"));
        assertThat(args.getValue(), arrayWithSize(1));
        assertThat(args.getValue()[0], is("ABC%"));
    }

    @Test
    void whereGroupByOrderBy() {
        Database database = TestDatabase.testDatabase(new AnsiDialect());

        database.from(WidgetRow.class, "w")
            .select(WidgetRow::manufacturerId, "id").comma(max(WidgetRow::name), "name")
            .where(WidgetRow::description).isLike("ABC%")
            .groupBy(WidgetRow::manufacturerId)
            .orderBy(WidgetRow::manufacturerId).then(max(WidgetRow::name))
            .list(transaction);

        verify(transaction).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select w.MANUFACTURER_ID as id, max(w.NAME) as name from SIESTA.WIDGET w " +
            "where w.DESCRIPTION like ? " +
            "group by w.MANUFACTURER_ID " +
            "order by w.MANUFACTURER_ID asc, max(w.NAME) asc"));
        assertThat(args.getValue(), arrayWithSize(1));
        assertThat(args.getValue()[0], is("ABC%"));
    }

}
