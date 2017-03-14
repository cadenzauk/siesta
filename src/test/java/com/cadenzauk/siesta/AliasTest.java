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

import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.siesta.catalog.Column;
import com.cadenzauk.siesta.catalog.Table;
import com.cadenzauk.siesta.grammar.expression.ResolvedColumn;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.testmodel.ManufacturerRow;
import com.cadenzauk.siesta.testmodel.WidgetRow;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AliasTest {
    @Mock
    private Table<WidgetRow> widgetTable;

    @Mock
    private MethodInfo<WidgetRow,Long> methodInfo;

    @Mock
    private Column<Long,WidgetRow> widgetRowId;

    @Mock
    private Column<String,WidgetRow> widgetDescription;

    @Mock
    private
    RowMapper<WidgetRow> rowMapper;

    @Test
    public void inWhereClause() throws Exception {
        when(widgetTable.qualifiedName()).thenReturn("SCHEMA.WIDGET");
        Alias<WidgetRow> sut = new Alias<>(widgetTable, "bob");

        String result = sut.inWhereClause();

        assertThat(result, is("SCHEMA.WIDGET as bob"));
    }

    @Test
    public void inSelectClauseSql() throws Exception {
        Alias<WidgetRow> sut = new Alias<>(widgetTable, "fred");

        String result = sut.inSelectClauseSql("WIDGET_ID");

        assertThat(result, is("fred.WIDGET_ID"));
    }

    @Test
    public void inSelectClauseLabel() throws Exception {
        Alias<WidgetRow> sut = new Alias<>(widgetTable, "fred");

        String result = sut.inSelectClauseLabel("WIDGET_ID");

        assertThat(result, is("fred_WIDGET_ID"));
    }

    @Test
    public void col() throws Exception {
        Alias<WidgetRow> sut = new Alias<>(widgetTable, "fred");
        Scope scope = mock(Scope.class);
        when(widgetTable.<String>column(any())).thenReturn(widgetDescription);
        when(widgetDescription.name()).thenReturn("DESCR");

        TypedExpression<Long> col = sut.col(WidgetRow::widgetId);

        assertThat(col, instanceOf(ResolvedColumn.class));
        assertThat(col.label(scope), is("fred_DESCR"));
    }

    @Test
    public void colOptional() throws Exception {
        Alias<WidgetRow> sut = new Alias<>(widgetTable, "fred");
        Scope scope = mock(Scope.class);
        when(widgetTable.<Long>column(any())).thenReturn(widgetRowId);
        when(widgetRowId.name()).thenReturn("ROW_ID");

        TypedExpression<String> col = sut.col(WidgetRow::description);

        assertThat(col, instanceOf(ResolvedColumn.class));
        assertThat(col.label(scope), is("fred_ROW_ID"));
    }

    @Test
    public void columnDelegatesToTable() throws Exception {
        Alias<WidgetRow> sut = new Alias<>(widgetTable, "joe");
        when(widgetTable.column(methodInfo)).thenReturn(widgetRowId);

        Column<Long,WidgetRow> result = sut.column(methodInfo);

        assertThat(result, sameInstance(widgetRowId));
        verify(widgetTable).column(methodInfo);
        verifyNoMoreInteractions(widgetTable);
    }

    @Test
    public void rowMapperDelegatesToTable() throws Exception {
        Alias<WidgetRow> sut = new Alias<>(widgetTable, "barney");
        when(widgetTable.rowMapper("barney_")).thenReturn(rowMapper);

        RowMapper<WidgetRow> result = sut.rowMapper();

        assertThat(result, sameInstance(rowMapper));
        verify(widgetTable).rowMapper("barney_");
        verifyNoMoreInteractions(widgetTable);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void asRightClassWithRightNameReturnsAlias() throws Exception {
        when(widgetTable.rowClass()).thenReturn(WidgetRow.class);
        Alias<WidgetRow> sut = new Alias<>(widgetTable, "wilma");

        List<Alias<WidgetRow>> result = sut.as(WidgetRow.class, "wilma").collect(toList());

        assertThat(result, containsInAnyOrder(sut));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void asDifferentClassWithWrongNameThrowsException() throws Exception {
        when(widgetTable.rowClass()).thenReturn(WidgetRow.class);
        Alias<WidgetRow> sut = new Alias<>(widgetTable, "wilma");

        calling(() -> sut.as(ManufacturerRow.class, "wilma"))
            .shouldThrow(IllegalArgumentException.class)
            .withMessage(is("Alias wilma is an alias for " +
                "class com.cadenzauk.siesta.testmodel.WidgetRow and not " +
                "class com.cadenzauk.siesta.testmodel.ManufacturerRow"));
    }

    @Test
    public void asRightClassWithDifferentNameReturnsEmpty() throws Exception {
        Alias<WidgetRow> sut = new Alias<>(widgetTable, "pebbles");

        List<Alias<WidgetRow>> result = sut.as(WidgetRow.class, "dino").collect(toList());

        assertThat(result, Matchers.empty());
    }

    @Test
    public void asDifferentClassWithDifferentNameReturnsEmpty() throws Exception {
        Alias<WidgetRow> sut = new Alias<>(widgetTable, "bambam");

        List<Alias<ManufacturerRow>> result = sut.as(ManufacturerRow.class, "burt").collect(toList());

        assertThat(result, Matchers.empty());
    }

    @Test
    public void asWithWrongClassReturnsEmpty() throws Exception {
        when(widgetTable.rowClass()).thenReturn(WidgetRow.class);
        Alias<WidgetRow> sut = new Alias<>(widgetTable, "bambam");

        List<Alias<ManufacturerRow>> result = sut.as(ManufacturerRow.class).collect(toList());

        assertThat(result, Matchers.empty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void asWithSameClassReturnsAlias() throws Exception {
        when(widgetTable.rowClass()).thenReturn(WidgetRow.class);
        Alias<WidgetRow> sut = new Alias<>(widgetTable, "wilma");

        List<Alias<WidgetRow>> result = sut.as(WidgetRow.class).collect(toList());

        assertThat(result, containsInAnyOrder(sut));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void asWithSuperClassReturnsAlias() throws Exception {
        when(widgetTable.rowClass()).thenReturn(WidgetRow.class);
        Alias<WidgetRow> sut = new Alias<>(widgetTable, "wilma");

        List<Alias<Object>> result = sut.as(Object.class).collect(toList());

        assertThat(result, containsInAnyOrder(sut));
    }
}