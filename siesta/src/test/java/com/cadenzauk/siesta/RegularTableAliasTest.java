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
import com.cadenzauk.siesta.grammar.expression.ColumnExpressionBuilder;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.model.ManufacturerRow;
import com.cadenzauk.siesta.model.WidgetRow;
import com.google.common.reflect.TypeToken;
import com.google.common.testing.EqualsTester;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegularTableAliasTest {
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
    private ColumnSpecifier<Long> columnSpecifier;

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
    private Column<String,WidgetRow> column2;

    @Mock
    private ProjectionColumn<String> projectionColumn;

    @Mock
    private ProjectionColumn<String> projectionColumn2;

    @Mock
    private Scope scope;

    @Mock
    private ForeignKeyReference<WidgetRow,ManufacturerRow> foreignKey;

    @Test
    void equalsAndHashCode() {
        when(widgetTable.qualifiedName()).thenReturn("WIDGET");
        when(widgetTable2.qualifiedName()).thenReturn("WIDGET");
        new EqualsTester()
            .addEqualityGroup(RegularTableAlias.of(widgetTable), RegularTableAlias.of(widgetTable2))
            .addEqualityGroup(RegularTableAlias.of(widgetTable, "a"), RegularTableAlias.of(widgetTable2, "a"))
            .testEquals();
    }

    @Test
    void database() {
        when(widgetTable.database()).thenReturn(database);
        Alias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "bob");

        Database result = sut.database();

        assertThat(result, sameInstance(database));
    }

    @Test
    void argsIsEmpty() {
        Alias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "w");

        Stream<Object> result = sut.args(scope);

        assertThat(result, StreamMatchers.empty());
    }

    @Test
    void isDualIsTrueForDual() {
        RegularTableAlias<Dual> sut = RegularTableAlias.of(dual, "d");
        when(dual.rowType()).thenReturn(TypeToken.of(Dual.class));

        boolean result = sut.isDual();

        assertThat(result, is(true));
    }

    @Test
    void isDualIsFalseWhenNotDual() {
        Alias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "w");
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));

        boolean result = sut.isDual();

        assertThat(result, is(false));
    }

    @Test
    void withoutAlias() {
        when(widgetTable.qualifiedName()).thenReturn("SCHEMA.WIDGET");
        Alias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "bob");

        String result = sut.withoutAlias();

        assertThat(result, is("SCHEMA.WIDGET"));
    }

    @Test
    void inFromClauseSql()  {
        when(widgetTable.qualifiedName()).thenReturn("SCHEMA.WIDGET");
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        Alias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "bob");

        String result = sut.inFromClauseSql();

        assertThat(result, is("SCHEMA.WIDGET bob"));
    }

    @Test
    void inFromClauseSqlOfDual()  {
        when(dual.rowType()).thenReturn(TypeToken.of(Dual.class));
        when(dual.database()).thenReturn(database);
        when(database.dialect()).thenReturn(new Db2Dialect());
        Alias<Dual> sut = RegularTableAlias.of(dual);

        String result = sut.inFromClauseSql();

        assertThat(result, is("SYSIBM.SYSDUMMY1"));
    }

    @Test
    void inSelectClauseSql()  {
        Alias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "fred");

        String result = sut.inSelectClauseSql("WIDGET_ID");

        assertThat(result, is("fred.WIDGET_ID"));
    }

    @Test
    void inSelectClauseLabel()  {
        Alias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "fred");

        String result = sut.inSelectClauseLabel("WIDGET_ID");

        assertThat(result, is("fred_WIDGET_ID"));
    }

    @Test
    void columnSqlDelegatesToColumn() {
        Alias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "barney");
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        when(widgetTable.column(MethodInfo.of(WidgetRow::name))).thenReturn(column);
        when(column.sql(sut)).thenReturn("colname");

        String result = sut.columnSql(scope, ColumnSpecifier.of(WidgetRow::name));

        assertThat(result, is("colname"));
    }

    @Test
    void columnSqlWithLabelDelegatesToColumn() {
        Alias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "barney");
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        when(widgetTable.column(MethodInfo.of(WidgetRow::name))).thenReturn(column);
        when(column.sqlWithLabel(sut, Optional.of("fred"))).thenReturn("colname as foo");

        String result = sut.columnSqlWithLabel(scope, ColumnSpecifier.of(WidgetRow::name), Optional.of("fred"));

        assertThat(result, is("colname as foo"));
    }

    @Test
    void inSelectClauseSqlDelegatesToColumns() {
        Alias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "barney");
        when(widgetTable.columns()).thenReturn(Stream.of(column, column2));
        when(column.sqlWithLabel(sut, Optional.empty())).thenReturn("colname as foo");
        when(column2.sqlWithLabel(sut, Optional.empty())).thenReturn("colname2 as bar");

        String result = sut.inSelectClauseSql(scope);

        assertThat(result, is("colname as foo, colname2 as bar"));
    }

    @Test
    void inSelectClauseSqlDelegatesToColumn() {
        Alias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "barney");

        String result = sut.inSelectClauseSql("SomeCol", Optional.of("foo"));

        assertThat(result, is("barney.SomeCol foo"));
    }

    @Test
    void col()  {
        Alias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "fred");

        TypedExpression<Long> result = sut.column(WidgetRow::widgetId);

        assertThat(result, instanceOf(ColumnExpressionBuilder.class));
        assertThat(result.toString(), is("widgetId"));
    }

    @Test
    void colOptional()  {
        Alias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "fred");

        TypedExpression<String> result = sut.column(WidgetRow::description);

        assertThat(result, instanceOf(ColumnExpressionBuilder.class));
        assertThat(result.toString(), is("description"));
    }

    @Test
    void findColumnOfCorrectTypeReturnsColumn() {
        when(columnSpecifier.effectiveType()).thenReturn(TypeToken.of(Long.class));
        when(columnSpecifier.specifies(any(), any(AliasColumn.class), any())).thenReturn(true);
        when(widgetTable.columns()).thenReturn(Stream.of(widgetRowId));
        when(widgetRowId.columnName()).thenReturn("bob");
        when(widgetRowId.as(TypeToken.of(Long.class))).thenReturn(Stream.of(widgetRowId));
        Alias<?> sut = RegularTableAlias.of(widgetTable, "joe");

        Optional<AliasColumn<Long>> result = sut.findAliasColumn(scope, columnSpecifier);

        assertThat(result.map(AliasColumn::columnName), is(Optional.of("bob")));
    }

    @Test
    void findColumnOfIncorrectTypeReturnsEmpty() {
        when(columnSpecifier.effectiveType()).thenReturn(TypeToken.of(Long.class));
        when(widgetTable.columns()).thenReturn(Stream.of(column));
        when(column.as(TypeToken.of(Long.class))).thenReturn(Stream.empty());
        Alias<?> sut = RegularTableAlias.of(widgetTable, "joe");

        Optional<AliasColumn<Long>> result = sut.findAliasColumn(scope, columnSpecifier);

        assertThat(result, OptionalMatchers.empty());
    }

    @Test
    void foreignKeyDelegatesToTable() {
        when(widgetTable.foreignKey(manufacturerTable, Optional.empty())).thenReturn(Optional.of(foreignKey));
        Alias<?> sut = RegularTableAlias.of(widgetTable, "joe");

        Optional<? extends ForeignKeyReference<?,?>> result = sut.foreignKey(RegularTableAlias.of(manufacturerTable, "joe"), Optional.empty());

        verify(widgetTable, times(1)).foreignKey(manufacturerTable, Optional.empty());
        assertThat(result, is(Optional.of(foreignKey)));
    }

    @Test
    void projectionColumnsAreGeneratedByColumns() {
        when(widgetTable.columns()).thenReturn(Stream.of(column, column2));
        Alias<?> sut = RegularTableAlias.of(widgetTable, "band");
        when(column.toProjection(sut, Optional.empty())).thenReturn(projectionColumn);
        when(column2.toProjection(sut, Optional.empty())).thenReturn(projectionColumn2);

        Stream<ProjectionColumn<?>> result = sut.projectionColumns(scope);

        assertThat(result, StreamMatchers.contains(projectionColumn, projectionColumn2));
    }

    @Test
    void rowMapperFactoryDelegatesToColumn()  {
        Alias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "barney");
        when(widgetTable.rowMapperFactory(sut, Optional.empty())).thenReturn(rowMapperFactory);

        RowMapperFactory<WidgetRow> result = sut.rowMapperFactory();

        assertThat(result, sameInstance(rowMapperFactory));
        verify(widgetTable).rowMapperFactory(sut, Optional.empty());
        verifyNoMoreInteractions(widgetTable);
    }

    @SuppressWarnings("unchecked")
    @Test
    void asRightClassWithRightNameReturnsAlias()  {
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        Alias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "wilma");

        List<Alias<WidgetRow>> result = sut.as(WidgetRow.class, "wilma").collect(toList());

        assertThat(result, containsInAnyOrder(sut));
    }

    @Test
    void asDifferentClassWithSameNameThrowsException()  {
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        Alias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "wilma");

        calling(() -> sut.as(ManufacturerRow.class, "wilma"))
            .shouldThrow(InvalidQueryException.class)
            .withMessage(is("Alias wilma is an alias for " +
                "com.cadenzauk.siesta.model.WidgetRow and not " +
                "class com.cadenzauk.siesta.model.ManufacturerRow."));
    }

    @Test
    void asRightClassWithDifferentNameReturnsEmpty()  {
        Alias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "pebbles");

        List<Alias<WidgetRow>> result = sut.as(WidgetRow.class, "dino").collect(toList());

        assertThat(result, Matchers.empty());
    }

    @Test
    void asDifferentClassWithDifferentNameReturnsEmpty()  {
        Alias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "dino");

        List<Alias<ManufacturerRow>> result = sut.as(ManufacturerRow.class, "burt").collect(toList());

        assertThat(result, Matchers.empty());
    }

    @Test
    void asWithWrongClassReturnsEmpty()  {
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        Alias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "dino");

        List<Alias<ManufacturerRow>> result = sut.as(ManufacturerRow.class).collect(toList());

        assertThat(result, Matchers.empty());
    }

    @SuppressWarnings("unchecked")
    @Test
    void asWithSameClassReturnsAlias()  {
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        Alias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "wilma");

        List<Alias<WidgetRow>> result = sut.as(WidgetRow.class).collect(toList());

        assertThat(result, containsInAnyOrder(sut));
    }

    @SuppressWarnings("unchecked")
    @Test
    void asWithSuperClassReturnsAlias()  {
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        Alias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "wilma");

        List<Alias<Object>> result = sut.as(Object.class).collect(toList());

        assertThat(result, containsInAnyOrder(sut));
    }

    @Test
    void asMethodInfoFromSameClassWithRightNameReturnsAlias()  {
        when(columnSpecifier.referringClass()).thenReturn(Optional.of(WidgetRow.class));
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        Alias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "wilma");

        List<Alias<?>> result = sut.as(scope, columnSpecifier, Optional.of("wilma")).collect(toList());

        assertThat(result, containsInAnyOrder(sut));
    }

    @Test
    void asMethodInfoForDifferentClassWithWrongNameThrowsException()  {
        when(columnSpecifier.referringClass()).thenReturn(Optional.of(ManufacturerRow.class));
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        Alias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "wilma");

        calling(() -> sut.as(scope, columnSpecifier, Optional.of("wilma")))
            .shouldThrow(InvalidQueryException.class)
            .withMessage(is("Alias wilma is an alias for " +
                "com.cadenzauk.siesta.model.WidgetRow and not " +
                "class com.cadenzauk.siesta.model.ManufacturerRow."));
    }

    @Test
    void asMethodInfoForRightClassWithDifferentNameReturnsEmpty()  {
        Alias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "pebbles");

        List<Alias<?>> result = sut.as(scope, columnSpecifier, Optional.of("dino")).collect(toList());

        assertThat(result, Matchers.empty());
    }

    @Test
    void asMethodInfoForDifferentClassWithDifferentNameReturnsEmpty()  {
        Alias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "dino");

        List<Alias<?>> result = sut.as(scope, columnSpecifier, Optional.of("burt")).collect(toList());

        assertThat(result, Matchers.empty());
    }

    @Test
    void asMethodInfoWithWrongClassReturnsEmpty()  {
        when(columnSpecifier.referringClass()).thenReturn(Optional.of(ManufacturerRow.class));
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        Alias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "dino");

        List<Alias<?>> result = sut.as(scope, columnSpecifier, Optional.empty()).collect(toList());

        assertThat(result, Matchers.empty());
    }

    @Test
    void asMethodInfoWithSameClassReturnsAlias()  {
        when(columnSpecifier.referringClass()).thenReturn(Optional.of(WidgetRow.class));
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        Alias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "wilma");

        List<Alias<?>> result = sut.as(scope, columnSpecifier, Optional.empty()).collect(toList());

        assertThat(result, containsInAnyOrder(sut));
    }

    @Test
    void asMethodInfoWithSuperClassReturnsAlias()  {
        when(columnSpecifier.referringClass()).thenReturn(Optional.of(Object.class));
        when(widgetTable.rowType()).thenReturn(TypeToken.of(WidgetRow.class));
        Alias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "wilma");

        List<Alias<?>> result = sut.as(scope, columnSpecifier, Optional.empty()).collect(toList());

        assertThat(result, containsInAnyOrder(sut));
    }

    @Test
    void dynamicRowMapperFactory() {
        RegularTableAlias<WidgetRow> sut = RegularTableAlias.of(widgetTable, "pebbles");
        when(widgetTable.dynamicRowMapperFactory(sut)).thenReturn(dynamicRowMapperFactory);

        DynamicRowMapperFactory<WidgetRow> result = sut.dynamicRowMapperFactory();

        assertThat(result, sameInstance(dynamicRowMapperFactory));
    }
}
