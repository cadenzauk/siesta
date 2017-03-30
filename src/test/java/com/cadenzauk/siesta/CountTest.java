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

import com.cadenzauk.siesta.testmodel.TestDatabase;
import com.cadenzauk.siesta.testmodel.WidgetRow;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static com.cadenzauk.siesta.Aggregates.count;
import static com.cadenzauk.siesta.Aggregates.countDistinct;
import static com.cadenzauk.siesta.Aggregates.max;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

public class CountTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private SqlExecutor sqlExecutor;

    @Captor
    private ArgumentCaptor<String> sql;

    @Captor
    private ArgumentCaptor<Object[]> args;

    @Captor
    private ArgumentCaptor<RowMapper<?>> rowMapper;

    @Test
    public void countStar() {
        Database database = TestDatabase.testDatabase();

        database.from(WidgetRow.class, "w")
            .select(count(), "n")
            .where(WidgetRow::manufacturerId).isEqualTo(4002L)
            .list(sqlExecutor);

        verify(sqlExecutor).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select count(*) as n " +
            "from TEST.WIDGET as w " +
            "where w.MANUFACTURER_ID = ?"));
        assertThat(args.getValue(), arrayWithSize(1));
        assertThat(args.getValue()[0], is(4002L));
    }
    @Test
    public void countDistinct1() {
        Database database = TestDatabase.testDatabase();

        database.from(WidgetRow.class, "w")
            .select(countDistinct(WidgetRow::name), "n")
            .where(WidgetRow::manufacturerId).isEqualTo(4002L)
            .list(sqlExecutor);

        verify(sqlExecutor).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select count(distinct w.NAME) as n " +
            "from TEST.WIDGET as w " +
            "where w.MANUFACTURER_ID = ?"));
        assertThat(args.getValue(), arrayWithSize(1));
        assertThat(args.getValue()[0], is(4002L));
    }
}
