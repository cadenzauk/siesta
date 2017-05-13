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
import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.catalog.Column;
import com.cadenzauk.siesta.catalog.Table;
import com.cadenzauk.siesta.grammar.expression.ResolvedColumn;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.test.model.ManufacturerRow;
import com.cadenzauk.siesta.test.model.WidgetRow;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class AliasTest extends MockitoTest {
    @Mock
    private Table<WidgetRow> widgetTable;

    @Mock
    private MethodInfo<WidgetRow,Long> methodInfo;

    @Mock
    private Column<Long,WidgetRow> widgetRowId;

    @Mock
    private Column<String,WidgetRow> widgetDescription;

    @Mock
    private RowMapper<WidgetRow> rowMapper;

    @Test
    void inWhereClause()  {
        when(widgetTable.qualifiedName()).thenReturn("SCHEMA.WIDGET");
        Alias<WidgetRow> sut = new Alias<>(widgetTable, "bob");

        String result = sut.inWhereClause();

        assertThat(result, is("SCHEMA.WIDGET as bob"));
    }

    @Test
    void inSelectClauseSql()  {
        Alias<WidgetRow> sut = new Alias<>(widgetTable, "fred");

        String result = sut.inSelectClauseSql("WIDGET_ID");

        assertThat(result, is("fred.WIDGET_ID"));
    }

    @Test
    void inSelectClauseLabel()  {
        Alias<WidgetRow> sut = new Alias<>(widgetTable, "fred");

        String result = sut.inSelectClauseLabel("WIDGET_ID");

        assertThat(result, is("fred_WIDGET_ID"));
    }

    @Test
    void col()  {
        Alias<WidgetRow> sut = new Alias<>(widgetTable, "fred");
        Scope scope = mock(Scope.class);
        when(widgetTable.<String>column(any())).thenReturn(widgetDescription);
        when(widgetDescription.name()).thenReturn("D");

        TypedExpression<Long> col = sut.column(WidgetRow::widgetId);

        assertThat(col.sql(scope), is("fred.D"));
        assertThat(col.args(scope).count(), is(0L));
        assertThat(col.label(scope), is("fred_D"));
    }

    @Test
    void colOptional()  {
        Alias<WidgetRow> sut = new Alias<>(widgetTable, "fred");
        Scope scope = mock(Scope.class);
        when(widgetTable.<Long>column(any())).thenReturn(widgetRowId);
        when(widgetRowId.name()).thenReturn("ROW_ID");

        TypedExpression<String> col = sut.column(WidgetRow::description);

        assertThat(col.sql(scope), is("fred.ROW_ID"));
        assertThat(col.args(scope).count(), is(0L));
        assertThat(col.label(scope), is("fred_ROW_ID"));
    }

    @Test
    void columnDelegatesToTable()  {
        Alias<WidgetRow> sut = new Alias<>(widgetTable, "joe");
        when(widgetTable.column(methodInfo)).thenReturn(widgetRowId);

        Column<Long,WidgetRow> result = sut.column(methodInfo);

        assertThat(result, sameInstance(widgetRowId));
        verify(widgetTable).column(methodInfo);
        verifyNoMoreInteractions(widgetTable);
    }

    @Test
    void rowMapperDelegatesToTable()  {
        Alias<WidgetRow> sut = new Alias<>(widgetTable, "barney");
        when(widgetTable.rowMapper("barney_")).thenReturn(rowMapper);

        RowMapper<WidgetRow> result = sut.rowMapper();

        assertThat(result, sameInstance(rowMapper));
        verify(widgetTable).rowMapper("barney_");
        verifyNoMoreInteractions(widgetTable);
    }

    @SuppressWarnings("unchecked")
    @Test
    void asRightClassWithRightNameReturnsAlias()  {
        when(widgetTable.rowClass()).thenReturn(WidgetRow.class);
        Alias<WidgetRow> sut = new Alias<>(widgetTable, "wilma");

        List<Alias<WidgetRow>> result = sut.as(WidgetRow.class, "wilma").collect(toList());

        assertThat(result, containsInAnyOrder(sut));
    }

    @SuppressWarnings("unchecked")
    @Test
    void asDifferentClassWithWrongNameThrowsException()  {
        when(widgetTable.rowClass()).thenReturn(WidgetRow.class);
        Alias<WidgetRow> sut = new Alias<>(widgetTable, "wilma");

        calling(() -> sut.as(ManufacturerRow.class, "wilma"))
            .shouldThrow(IllegalArgumentException.class)
            .withMessage(is("Alias wilma is an alias for " +
                "class com.cadenzauk.siesta.test.model.WidgetRow and not " +
                "class com.cadenzauk.siesta.test.model.ManufacturerRow"));
    }

    @Test
    void asRightClassWithDifferentNameReturnsEmpty()  {
        Alias<WidgetRow> sut = new Alias<>(widgetTable, "pebbles");

        List<Alias<WidgetRow>> result = sut.as(WidgetRow.class, "dino").collect(toList());

        assertThat(result, Matchers.empty());
    }

    @Test
    void asDifferentClassWithDifferentNameReturnsEmpty()  {
        Alias<WidgetRow> sut = new Alias<>(widgetTable, "dino");

        List<Alias<ManufacturerRow>> result = sut.as(ManufacturerRow.class, "burt").collect(toList());

        assertThat(result, Matchers.empty());
    }

    @Test
    void asWithWrongClassReturnsEmpty()  {
        when(widgetTable.rowClass()).thenReturn(WidgetRow.class);
        Alias<WidgetRow> sut = new Alias<>(widgetTable, "dino");

        List<Alias<ManufacturerRow>> result = sut.as(ManufacturerRow.class).collect(toList());

        assertThat(result, Matchers.empty());
    }

    @SuppressWarnings("unchecked")
    @Test
    void asWithSameClassReturnsAlias()  {
        when(widgetTable.rowClass()).thenReturn(WidgetRow.class);
        Alias<WidgetRow> sut = new Alias<>(widgetTable, "wilma");

        List<Alias<WidgetRow>> result = sut.as(WidgetRow.class).collect(toList());

        assertThat(result, containsInAnyOrder(sut));
    }

    @SuppressWarnings("unchecked")
    @Test
    void asWithSuperClassReturnsAlias()  {
        when(widgetTable.rowClass()).thenReturn(WidgetRow.class);
        Alias<WidgetRow> sut = new Alias<>(widgetTable, "wilma");

        List<Alias<Object>> result = sut.as(Object.class).collect(toList());

        assertThat(result, containsInAnyOrder(sut));
    }
}