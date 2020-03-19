/*
 * Copyright (c) 2020 Cadenza United Kingdom Limited
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

package com.cadenzauk.siesta.projection;

import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.ColumnSpecifier;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.ProjectionColumn;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.grammar.expression.UnresolvedColumn;
import com.cadenzauk.siesta.grammar.select.SubselectAlias;
import com.cadenzauk.siesta.model.MoneyAmount;
import com.cadenzauk.siesta.model.PartRow;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.cadenzauk.siesta.model.TestDatabase.testDatabaseBuilder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ExpressionProjectionTest {
    @Test
    void canFindEmbeddedColumnInTableAlias() {
        Database database = testDatabaseBuilder().build();
        Alias<PartRow> alias = database.table(PartRow.class).as("foo");
        ExpressionProjection<MoneyAmount> sut = new ExpressionProjection<>(false, UnresolvedColumn.of(PartRow::purchasePrice), Optional.empty());

        Optional<ProjectionColumn<MoneyAmount>> column = sut.findColumn(new Scope(database, alias), ColumnSpecifier.of(PartRow::purchasePrice));

        assertThat(column.map(ProjectionColumn::columnSql), is(Optional.of("foo.PURCHASE_PRICE_AMOUNT as foo_PURCHASE_PRICE_AMOUNT, foo.PURCHASE_PRICE_CCY as foo_PURCHASE_PRICE_CCY")));
    }

    @Test
    void canFindEmbeddedColumnInTableAliasWithLabel() {
        Database database = testDatabaseBuilder().build();
        Alias<PartRow> alias = database.table(PartRow.class).as("foo");
        ExpressionProjection<MoneyAmount> sut = new ExpressionProjection<>(false, UnresolvedColumn.of(PartRow::purchasePrice), Optional.of("PP"));

        Optional<ProjectionColumn<MoneyAmount>> column = sut.findColumn(new Scope(database, alias), ColumnSpecifier.of(PartRow::purchasePrice));

        assertThat(column.map(ProjectionColumn::columnSql), is(Optional.of("foo.PURCHASE_PRICE_AMOUNT as PP_PURCHASE_PRICE_AMOUNT, foo.PURCHASE_PRICE_CCY as PP_PURCHASE_PRICE_CCY")));
    }

    @Test
    void canFindEmbeddedColumnInSubselectAlias() {
        Database database = testDatabaseBuilder().build();
        Alias<PartRow> alias = new SubselectAlias<>(database.from(PartRow.class), "foo");
        ExpressionProjection<MoneyAmount> sut = new ExpressionProjection<>(false, UnresolvedColumn.of(PartRow::purchasePrice), Optional.empty());

        Optional<ProjectionColumn<MoneyAmount>> column = sut.findColumn(new Scope(database, alias), ColumnSpecifier.of(PartRow::purchasePrice));

        assertThat(column.map(ProjectionColumn::columnSql), is(Optional.of("foo.PART_PURCHASE_PRICE_AMOUNT as foo_PART_PURCHASE_PRICE_AMOUNT, foo.PART_PURCHASE_PRICE_CCY as foo_PART_PURCHASE_PRICE_CCY")));
    }
    @Test
    void canFindEmbeddedColumnInSubselectAliasWithLabel() {
        Database database = testDatabaseBuilder().build();
        Alias<PartRow> alias = new SubselectAlias<>(database.from(PartRow.class), "foo");
        ExpressionProjection<MoneyAmount> sut = new ExpressionProjection<>(false, UnresolvedColumn.of(PartRow::purchasePrice), Optional.of("PP"));

        Optional<ProjectionColumn<MoneyAmount>> column = sut.findColumn(new Scope(database, alias), ColumnSpecifier.of(PartRow::purchasePrice));

        assertThat(column.map(ProjectionColumn::columnSql), is(Optional.of("foo.PART_PURCHASE_PRICE_AMOUNT as PP_PART_PURCHASE_PRICE_AMOUNT, foo.PART_PURCHASE_PRICE_CCY as PP_PART_PURCHASE_PRICE_CCY")));
    }
}