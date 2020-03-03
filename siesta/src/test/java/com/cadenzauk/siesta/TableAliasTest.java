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

import co.unruly.matchers.OptionalMatchers;
import co.unruly.matchers.StreamMatchers;
import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.siesta.catalog.Column;
import com.cadenzauk.siesta.catalog.ForeignKeyReference;
import com.cadenzauk.siesta.catalog.Table;
import com.cadenzauk.siesta.dialect.Db2Dialect;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.model.ManufacturerRow;
import com.cadenzauk.siesta.model.WidgetRow;
import com.google.common.reflect.TypeToken;
import com.google.common.testing.EqualsTester;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TableAliasTest {
    @Mock
    private Table<WidgetRow> widgetTable;

    @Mock
    private Table<WidgetRow> widgetTable2;

    @Mock
    private Table<ManufacturerRow> manufacturerTable;

    @Mock
    private Table<Dual> dual;

    @Mock
    private MethodInfo<WidgetRow,Long> methodInfo;

    @Mock
    private MethodInfo<ManufacturerRow,Long> manufacturerMethodInfo;

    @Mock
    private MethodInfo<Object,Long> objectMethodInfo;

    @Mock
    private Column<Long,WidgetRow> widgetRowId;

    @Mock
    private Column<String,WidgetRow> widgetDescription;

    @Mock
    private RowMapperFactory<WidgetRow> rowMapperFactory;

    @Mock
    private RowMapperFactory<Long> rowMapperFactoryForColumn;

    @Mock
    private Database database;

    @Mock
    private DynamicRowMapperFactory<WidgetRow> dynamicRowMapperFactory;

    @Mock
    private Column<String,WidgetRow> column;

    @Mock
    private Column<?,WidgetRow> column2;

    @Mock
    private Scope scope;

    @Mock
    private ForeignKeyReference<WidgetRow,ManufacturerRow> foreignKey;

    @Test
    void equalsAndHashCode() {
        when(widgetTable.qualifiedName()).thenReturn("WIDGET");
        when(widgetTable2.qualifiedName()).thenReturn("WIDGET");
        new EqualsTester()
            .addEqualityGroup(TableAlias.of(widgetTable), TableAlias.of(widgetTable2))
            .addEqualityGroup(TableAlias.of(widgetTable, "a"), TableAlias.of(widgetTable2, "a"))
            .testEquals();
    }

    @Test
    void database() {
        when(widgetTable.database()).thenReturn(database);
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "bob");

        Database result = sut.database();

        assertThat(result, sameInstance(database));
    }

    @Test
    void argsIsEmpty() {
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "w");

        Stream<Object> result = sut.args(scope);

        assertThat(result, StreamMatchers.empty());
    }

    @Test
    void isDualIsTrueForDual() {
        TableAlias<Dual> sut = TableAlias.of(dual, "d");
        when(dual.rowType()).thenReturn(TypeToken.of(Dual.class));

        boolean result = sut.isDual();

        assertThat(result, is(true));
    }

    @Test
    void isDualIsFalseWhenNotDual() {
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "w");
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));

        boolean result = sut.isDual();

        assertThat(result, is(false));
    }

    @Test
    void withoutAlias() {
        when(widgetTable.qualifiedName()).thenReturn("SCHEMA.WIDGET");
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "bob");

        String result = sut.withoutAlias();

        assertThat(result, is("SCHEMA.WIDGET"));
    }

    @Test
    void inFromClauseSql()  {
        when(widgetTable.qualifiedName()).thenReturn("SCHEMA.WIDGET");
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "bob");

        String result = sut.inFromClauseSql();

        assertThat(result, is("SCHEMA.WIDGET bob"));
    }

    @Test
    void inFromClauseSqlOfDual()  {
        when(dual.rowType()).thenReturn(TypeToken.of(Dual.class));
        when(dual.database()).thenReturn(database);
        when(database.dialect()).thenReturn(new Db2Dialect());
        Alias<Dual> sut = TableAlias.of(dual);

        String result = sut.inFromClauseSql();

        assertThat(result, is("SYSIBM.SYSDUMMY1"));
    }

    @Test
    void inSelectClauseSql()  {
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "fred");

        String result = sut.inSelectClauseSql("WIDGET_ID");

        assertThat(result, is("fred.WIDGET_ID"));
    }

    @Test
    void inSelectClauseLabel()  {
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "fred");

        String result = sut.inSelectClauseLabel("WIDGET_ID");

        assertThat(result, is("fred_WIDGET_ID"));
    }

    @Test
    void columnSqlDelegatesToColumn() {
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "barney");
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        when(widgetTable.column(MethodInfo.of(WidgetRow::name))).thenReturn(column);
        when(column.sql(sut)).thenReturn("colname");

        String result = sut.columnSql(MethodInfo.of(WidgetRow::name));

        assertThat(result, is("colname"));
    }

    @Test
    void columnSqlWithLabelDelegatesToColumn() {
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "barney");
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        when(widgetTable.column(MethodInfo.of(WidgetRow::name))).thenReturn(column);
        when(column.sqlWithLabel(sut, Optional.of("fred"))).thenReturn("colname as foo");

        String result = sut.columnSqlWithLabel(MethodInfo.of(WidgetRow::name), Optional.of("fred"));

        assertThat(result, is("colname as foo"));
    }

    @Test
    void inSelectClauseSqlDelegatesToColumns() {
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "barney");
        when(widgetTable.columns()).thenReturn(Stream.of(column, column2));
        when(column.sqlWithLabel(sut, Optional.empty())).thenReturn("colname as foo");
        when(column2.sqlWithLabel(sut, Optional.empty())).thenReturn("colname2 as bar");

        String result = sut.inSelectClauseSql(scope);

        assertThat(result, is("colname as foo, colname2 as bar"));
    }

    @Test
    void inSelectClauseSqlDelegatesToColumn() {
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "barney");

        String result = sut.inSelectClauseSql("SomeCol", Optional.of("foo"));

        assertThat(result, is("barney.SomeCol foo"));
    }

    @Test
    void col()  {
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "fred");
        Scope scope = mock(Scope.class);
        when(scope.database()).thenReturn(database);
        when(database.columnNameFor(Mockito.<MethodInfo<WidgetRow,String>>any())).thenReturn("ROW_ID");
        when(widgetTable.column(Mockito.<MethodInfo<WidgetRow,Long>>any())).thenReturn(widgetRowId);
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        when(widgetRowId.sql(sut)).thenReturn("fred.ROW_ID");

        TypedExpression<Long> col = sut.column(WidgetRow::widgetId);

        assertThat(col.sql(scope), is("fred.ROW_ID"));
        assertThat(col.args(scope).count(), is(0L));
        assertThat(col.label(scope), is("fred_ROW_ID"));
    }

    @Test
    void colOptional()  {
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "fred");
        Scope scope = mock(Scope.class);
        when(scope.database()).thenReturn(database);
        when(database.columnNameFor(Mockito.<MethodInfo<WidgetRow,String>>any())).thenReturn("DESCRIPTION");
        when(widgetTable.column(Mockito.<MethodInfo<WidgetRow,String>>any())).thenReturn(widgetDescription);
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        when(widgetDescription.sql(sut)).thenReturn("fred.DESCRIPTION");

        TypedExpression<String> col = sut.column(WidgetRow::description);

        assertThat(col.sql(scope), is("fred.DESCRIPTION"));
        assertThat(col.args(scope).count(), is(0L));
        assertThat(col.label(scope), is("fred_DESCRIPTION"));
    }

    @Test
    void findColumnOfCorrectTypeReturnsColumn() {
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        when(methodInfo.asReferring(TypeToken.of(WidgetRow.class))).thenReturn(Optional.of(methodInfo));
        when(widgetTable.column(methodInfo)).thenReturn(widgetRowId);
        when(widgetRowId.columnName()).thenReturn("bob");
        Alias<?> sut = TableAlias.of(widgetTable, "joe");

        Optional<ProjectionColumn<Long>> result = sut.findColumn(methodInfo);

        assertThat(result.map(ProjectionColumn::columnSql), is(Optional.of("bob")));
        assertThat(result.map(ProjectionColumn::label), is(Optional.of("joe_bob")));
    }

    @Test
    void findColumnOfIncorrectTypeReturnsEmpty() {
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        when(methodInfo.asReferring(TypeToken.of(WidgetRow.class))).thenReturn(Optional.empty());
        Alias<?> sut = TableAlias.of(widgetTable, "joe");

        Optional<ProjectionColumn<Long>> result = sut.findColumn(methodInfo);

        assertThat(result, OptionalMatchers.empty());
    }

    @Test
    void foreignKeyDelegatesToTable() {
        when(widgetTable.foreignKey(manufacturerTable, Optional.empty())).thenReturn(Optional.of(foreignKey));
        Alias<?> sut = TableAlias.of(widgetTable, "joe");

        Optional<? extends ForeignKeyReference<?,?>> result = sut.foreignKey(TableAlias.of(manufacturerTable, "joe"), Optional.empty());

        verify(widgetTable, times(1)).foreignKey(manufacturerTable, Optional.empty());
        assertThat(result, is(Optional.of(foreignKey)));
    }

    @Test
    void projectionColumnsHaveTheRightColumnSql() {
        Alias<?> sut = TableAlias.of(widgetTable, "band");
        when(widgetTable.columns()).thenReturn(Stream.of(column, column2));
        when(column.columnName()).thenReturn("axl");
        when(column2.columnName()).thenReturn("slash");

        Stream<ProjectionColumn<?>> result = sut.projectionColumns(scope);

        assertThat(result.map(ProjectionColumn::columnSql), StreamMatchers.contains("axl", "slash"));
    }

    @Test
    void projectionColumnsHaveTheRightLabels() {
        Alias<?> sut = TableAlias.of(widgetTable, "band");
        when(widgetTable.columns()).thenReturn(Stream.of(column, column2));
        when(column.columnName()).thenReturn("duff");
        when(column2.columnName()).thenReturn("izzy");

        Stream<ProjectionColumn<?>> result = sut.projectionColumns(scope);

        assertThat(result.map(ProjectionColumn::label), StreamMatchers.contains("band_duff", "band_izzy"));
    }

    @Test
    void rowMapperFactoryDelegatesToColumn()  {
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "barney");
        when(widgetTable.rowMapperFactory(sut, Optional.empty())).thenReturn(rowMapperFactory);

        RowMapperFactory<WidgetRow> result = sut.rowMapperFactory();

        assertThat(result, sameInstance(rowMapperFactory));
        verify(widgetTable).rowMapperFactory(sut, Optional.empty());
        verifyNoMoreInteractions(widgetTable);
    }

    @Test
    void rowMapperFactoryForDelegatesToColumn()  {
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        when(methodInfo.asReferring(TypeToken.of(WidgetRow.class))).thenReturn(Optional.of(methodInfo));
        when(widgetTable.column(methodInfo)).thenReturn(widgetRowId);
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "barney");
        when(widgetRowId.rowMapperFactory(sut, Optional.of("foo"))).thenReturn(rowMapperFactoryForColumn);

        RowMapperFactory<Long> result = sut.rowMapperFactoryFor(methodInfo, Optional.of("foo"));

        assertThat(result, sameInstance(rowMapperFactoryForColumn));
        verify(widgetRowId).rowMapperFactory(sut, Optional.of("foo"));
        verifyNoMoreInteractions(widgetRowId);
    }

    @SuppressWarnings("unchecked")
    @Test
    void asRightClassWithRightNameReturnsAlias()  {
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "wilma");

        List<Alias<WidgetRow>> result = sut.as(WidgetRow.class, "wilma").collect(toList());

        assertThat(result, containsInAnyOrder(sut));
    }

    @Test
    void asDifferentClassWithSameNameThrowsException()  {
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "wilma");

        calling(() -> sut.as(ManufacturerRow.class, "wilma"))
            .shouldThrow(IllegalArgumentException.class)
            .withMessage(is("Alias wilma is an alias for " +
                "com.cadenzauk.siesta.model.WidgetRow and not " +
                "class com.cadenzauk.siesta.model.ManufacturerRow"));
    }

    @Test
    void asRightClassWithDifferentNameReturnsEmpty()  {
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "pebbles");

        List<Alias<WidgetRow>> result = sut.as(WidgetRow.class, "dino").collect(toList());

        assertThat(result, Matchers.empty());
    }

    @Test
    void asDifferentClassWithDifferentNameReturnsEmpty()  {
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "dino");

        List<Alias<ManufacturerRow>> result = sut.as(ManufacturerRow.class, "burt").collect(toList());

        assertThat(result, Matchers.empty());
    }

    @Test
    void asWithWrongClassReturnsEmpty()  {
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "dino");

        List<Alias<ManufacturerRow>> result = sut.as(ManufacturerRow.class).collect(toList());

        assertThat(result, Matchers.empty());
    }

    @SuppressWarnings("unchecked")
    @Test
    void asWithSameClassReturnsAlias()  {
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "wilma");

        List<Alias<WidgetRow>> result = sut.as(WidgetRow.class).collect(toList());

        assertThat(result, containsInAnyOrder(sut));
    }

    @SuppressWarnings("unchecked")
    @Test
    void asWithSuperClassReturnsAlias()  {
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "wilma");

        List<Alias<Object>> result = sut.as(Object.class).collect(toList());

        assertThat(result, containsInAnyOrder(sut));
    }

    @Test
    void asMethodInfoFromSameClassWithRightNameReturnsAlias()  {
        when(methodInfo.referringClass()).thenReturn(WidgetRow.class);
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "wilma");

        List<Alias<?>> result = sut.as(methodInfo, Optional.of("wilma")).collect(toList());

        assertThat(result, containsInAnyOrder(sut));
    }

    @Test
    void asMethodInfoForDifferentClassWithWrongNameThrowsException()  {
        when(manufacturerMethodInfo.referringClass()).thenReturn(ManufacturerRow.class);
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "wilma");

        calling(() -> sut.as(manufacturerMethodInfo, Optional.of("wilma")))
            .shouldThrow(IllegalArgumentException.class)
            .withMessage(is("Alias wilma is an alias for " +
                "com.cadenzauk.siesta.model.WidgetRow and not " +
                "class com.cadenzauk.siesta.model.ManufacturerRow"));
    }

    @Test
    void asMethodInfoForRightClassWithDifferentNameReturnsEmpty()  {
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "pebbles");

        List<Alias<?>> result = sut.as(methodInfo, Optional.of("dino")).collect(toList());

        assertThat(result, Matchers.empty());
    }

    @Test
    void asMethodInfoForDifferentClassWithDifferentNameReturnsEmpty()  {
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "dino");

        List<Alias<?>> result = sut.as(manufacturerMethodInfo, Optional.of("burt")).collect(toList());

        assertThat(result, Matchers.empty());
    }

    @Test
    void asMethodInfoWithWrongClassReturnsEmpty()  {
        when(manufacturerMethodInfo.referringClass()).thenReturn(ManufacturerRow.class);
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "dino");

        List<Alias<?>> result = sut.as(manufacturerMethodInfo, Optional.empty()).collect(toList());

        assertThat(result, Matchers.empty());
    }

    @Test
    void asMethodInfoWithSameClassReturnsAlias()  {
        when(methodInfo.referringClass()).thenReturn(WidgetRow.class);
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "wilma");

        List<Alias<?>> result = sut.as(methodInfo, Optional.empty()).collect(toList());

        assertThat(result, containsInAnyOrder(sut));
    }

    @Test
    void asMethodInfoWithSuperClassReturnsAlias()  {
        when(objectMethodInfo.referringClass()).thenReturn(Object.class);
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        Alias<WidgetRow> sut = TableAlias.of(widgetTable, "wilma");

        List<Alias<?>> result = sut.as(objectMethodInfo, Optional.empty()).collect(toList());

        assertThat(result, containsInAnyOrder(sut));
    }

    @Test
    void dynamicRowMapperFactory() {
        TableAlias<WidgetRow> sut = TableAlias.of(widgetTable, "pebbles");
        when(widgetTable.dynamicRowMapperFactory(sut)).thenReturn(dynamicRowMapperFactory);

        DynamicRowMapperFactory<WidgetRow> result = sut.dynamicRowMapperFactory();

        assertThat(result, sameInstance(dynamicRowMapperFactory));
    }
}