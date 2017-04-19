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

package com.cadenzauk.siesta.grammar.dml;

import com.cadenzauk.core.MockitoTest;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.SqlExecutor;
import com.cadenzauk.siesta.test.model.WidgetRow;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static com.cadenzauk.siesta.grammar.expression.ExpressionBuilder.column;
import static org.apache.commons.lang3.ArrayUtils.toArray;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;

class DeleteTest extends MockitoTest {
    @Mock
    private SqlExecutor sqlExecutor;

    @Captor
    private ArgumentCaptor<String> sql;

    @Captor
    private ArgumentCaptor<Object[]> args;

    @Test
    void update() {
        Database database = Database.newBuilder()
            .defaultSchema("TEST")
            .build();

        database.delete(WidgetRow.class)
            .where(WidgetRow::widgetId).isEqualTo(2L)
            .and(column(WidgetRow::description).isBetween("C").and(WidgetRow::name)
                .or(column(WidgetRow::description).isNull()))
            .execute(sqlExecutor);

        verify(sqlExecutor).update(sql.capture(), args.capture());
        assertThat(sql.getValue(), is("delete from TEST.WIDGET as WIDGET " +
            "where WIDGET.WIDGET_ID = ? " +
            "and (" +
            "WIDGET.DESCRIPTION between ? and WIDGET.NAME " +
            "or WIDGET.DESCRIPTION is null" +
            ")"));
        assertThat(args.getValue(), is(toArray(2L, "C")));
    }
}
