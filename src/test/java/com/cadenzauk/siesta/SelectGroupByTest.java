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

import com.cadenzauk.core.MockitoTest;
import com.cadenzauk.siesta.testmodel.ManufacturerRow;
import com.cadenzauk.siesta.testmodel.TestDatabase;
import com.cadenzauk.siesta.testmodel.WidgetRow;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static com.cadenzauk.siesta.Aggregates.max;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsArrayWithSize.arrayWithSize;
import static org.mockito.Mockito.verify;

class SelectGroupByTest extends MockitoTest {
    @Mock
    private SqlExecutor sqlExecutor;

    @Captor
    private ArgumentCaptor<String> sql;

    @Captor
    private ArgumentCaptor<Object[]> args;

    @Captor
    private ArgumentCaptor<RowMapper<?>> rowMapper;

    @Test
    void groupBy1() {
        Database database = TestDatabase.testDatabase();

        database.from(WidgetRow.class, "w")
            .select(WidgetRow::manufacturerId).comma(max(WidgetRow::name))
            .groupBy(WidgetRow::manufacturerId)
            .list(sqlExecutor);

        verify(sqlExecutor).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select w.MANUFACTURER_ID as w_MANUFACTURER_ID, max(w.NAME) as max_w_NAME " +
            "from TEST.WIDGET as w " +
            "group by w.MANUFACTURER_ID"));
        assertThat(args.getValue(), arrayWithSize(0));
    }

    @Test
    void groupBy2() {
        Database database = TestDatabase.testDatabase();

        database.from(WidgetRow.class, "w")
            .select(WidgetRow::manufacturerId).comma(WidgetRow::description).comma(max(WidgetRow::name))
            .groupBy(WidgetRow::manufacturerId).comma(WidgetRow::description)
            .list(sqlExecutor);

        verify(sqlExecutor).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select w.MANUFACTURER_ID as w_MANUFACTURER_ID, w.DESCRIPTION as w_DESCRIPTION, max(w.NAME) as max_w_NAME " +
            "from TEST.WIDGET as w " +
            "group by w.MANUFACTURER_ID, w.DESCRIPTION"));
        assertThat(args.getValue(), arrayWithSize(0));
    }

    @Test
    void whereGroupBy() {
        Database database = TestDatabase.testDatabase();

        database.from(WidgetRow.class, "w")
            .select(WidgetRow::manufacturerId, "id").comma(max(WidgetRow::name), "name")
            .where(WidgetRow::description).isLike("ABC%")
            .groupBy(WidgetRow::manufacturerId)
            .list(sqlExecutor);

        verify(sqlExecutor).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select w.MANUFACTURER_ID as id, max(w.NAME) as name from TEST.WIDGET as w " +
            "where w.DESCRIPTION like ? " +
            "group by w.MANUFACTURER_ID"));
        assertThat(args.getValue(), arrayWithSize(1));
        assertThat(args.getValue()[0], is("ABC%"));
    }

    @Test
    void joinGroupBy() {
        Database database = TestDatabase.testDatabase();

        database.from(WidgetRow.class, "w")
            .leftJoin(ManufacturerRow.class, "m").on(ManufacturerRow::manufacturerId).isEqualTo(WidgetRow::manufacturerId)
            .select(WidgetRow::manufacturerId, "id").comma(ManufacturerRow::name, "manufacturer").comma(max(WidgetRow::name), "name")
            .where(WidgetRow::description).isLike("ABC%")
            .groupBy(WidgetRow::manufacturerId).comma(ManufacturerRow::name)
            .list(sqlExecutor);

        verify(sqlExecutor).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select w.MANUFACTURER_ID as id, m.NAME as manufacturer, max(w.NAME) as name " +
            "from TEST.WIDGET as w " +
            "left join TEST.MANUFACTURER as m on m.MANUFACTURER_ID = w.MANUFACTURER_ID " +
            "where w.DESCRIPTION like ? " +
            "group by w.MANUFACTURER_ID, m.NAME"));
        assertThat(args.getValue(), arrayWithSize(1));
        assertThat(args.getValue()[0], is("ABC%"));
    }

    @Test
    void whereGroupByOrderBy() {
        Database database = TestDatabase.testDatabase();

        database.from(WidgetRow.class, "w")
            .select(WidgetRow::manufacturerId, "id").comma(max(WidgetRow::name), "name")
            .where(WidgetRow::description).isLike("ABC%")
            .groupBy(WidgetRow::manufacturerId)
            .orderBy(WidgetRow::manufacturerId).then(max(WidgetRow::name))
            .list(sqlExecutor);

        verify(sqlExecutor).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select w.MANUFACTURER_ID as id, max(w.NAME) as name from TEST.WIDGET as w " +
            "where w.DESCRIPTION like ? " +
            "group by w.MANUFACTURER_ID " +
            "order by w.MANUFACTURER_ID asc, max(w.NAME) asc"));
        assertThat(args.getValue(), arrayWithSize(1));
        assertThat(args.getValue()[0], is("ABC%"));
    }

}
