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

import com.cadenzauk.core.junit.TestCase;
import com.cadenzauk.core.junit.TestCaseArgumentsProvider;
import com.cadenzauk.core.lang.CompositeAutoCloseable;
import com.cadenzauk.core.lang.UncheckedAutoCloseable;
import com.cadenzauk.core.sql.RuntimeSqlException;
import com.cadenzauk.core.sql.exception.NoSuchObjectException;
import com.cadenzauk.core.tuple.Tuple3;
import com.cadenzauk.siesta.grammar.temp.LocalTempTable;
import com.cadenzauk.siesta.grammar.temp.TempTable;
import com.cadenzauk.siesta.grammar.temp.TempTableCommitAction;
import com.cadenzauk.siesta.model.SaleRow;
import com.cadenzauk.siesta.model.SalesAreaRow;
import com.cadenzauk.siesta.model.SalespersonRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.function.Function;

import static com.cadenzauk.core.sql.testutil.RuntimeSqlExceptionMatcher.subclass;
import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static com.cadenzauk.siesta.grammar.expression.Aggregates.count;
import static com.cadenzauk.siesta.model.TestDatabase.testDatabase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public abstract class TempTableIntegrationTest extends IntegrationTest {
    @Test
    void canInsertRowsIntoGlobalTempTable() {
        try (Scenario<SalespersonRow> scenario = givenGlobalTempTable(SalespersonRow.class, "tmp_salesperson")) {
            scenario.database.insert(scenario.tran, scenario.sut, aRandomSalesperson(), aRandomSalesperson(), aRandomSalesperson());

            assertThat(scenario.database.from(scenario.sut.as("t")).select(count()).single(scenario.tran), is(3));
        }
    }

    @Test
    void canInsertFromSelectIntoGlobalTempTable() {
        try (Scenario<SalespersonRow> scenario = givenGlobalTempTable(SalespersonRow.class, "tmp_salesperson")) {
            Database database = scenario.database;
            SalespersonRow salesperson1 = aRandomSalesperson();
            SalespersonRow salesperson2 = aRandomSalesperson();
            SalespersonRow salesperson3 = aRandomSalesperson();
            database.insert(aRandomSalesperson(), salesperson1, salesperson2, salesperson3);

            database.insert(
                scenario.tran,
                scenario.sut,
                database
                    .from(SalespersonRow.class)
                    .where(SalespersonRow::salespersonId).isIn(salesperson1.salespersonId(), salesperson3.salespersonId()));

            assertThat(database.from(scenario.sut.as("t")).select(count()).single(scenario.tran), is(2));
        }
    }

    @Test
    void rowsInGlobalTempTableAreNotVisibleToOtherConnections() {
        try (Scenario<SalespersonRow> scenario = givenGlobalTempTable(SalespersonRow.class, "tmp_salesperson")
                                                     .containing(aRandomSalesperson(), aRandomSalesperson())) {
            assertThat(scenario.database.from(scenario.sut, "x")
                           .select(count())
                           .withIsolation(IsolationLevel.UNCOMMITTED_READ)
                           .single(),
                is(0));
        }
    }

    @Test
    void rowsInGlobalTempTableDeletedAfterCommit() {
        try (Scenario<SalespersonRow> scenario = givenGlobalTempTable(SalespersonRow.class, "tmp_salesperson")
                                                     .containing(aRandomSalesperson(), aRandomSalesperson())) {
            scenario.tran.commit();

            assertThat(scenario.database.from(scenario.sut, "x")
                           .select(count())
                           .single(scenario.tran),
                is(0));
        }
    }

    @Test
    void rowsInGlobalTempTableRolledBack() {
        try (Scenario<SalespersonRow> scenario = givenGlobalTempTable(SalespersonRow.class, "tmp_salesperson")
                                                     .containing(aRandomSalesperson(), aRandomSalesperson())) {
            scenario.tran.rollback();

            assertThat(scenario.database.from(scenario.sut, "x")
                           .select(count())
                           .single(scenario.tran),
                is(0));
        }
    }

    @Test
    void canInsertRowsIntoLocalTempTable() {
        try (Scenario<SalespersonRow> scenario = givenLocalTempTable(SalespersonRow.class, "tmp1")) {
            scenario.database.insert(scenario.tran, scenario.sut, aRandomSalesperson(), aRandomSalesperson(), aRandomSalesperson());

            assertThat(scenario.database.from(scenario.sut.as("t")).select(count()).single(scenario.tran), is(3));
        }
    }

    @Test
    void canInsertFromSelectIntoLocalTempTable() {
        try (Scenario<SalespersonRow> scenario = givenLocalTempTable(SalespersonRow.class, "tmp2")) {
            Database database = scenario.database;
            SalespersonRow salesperson1 = aRandomSalesperson();
            SalespersonRow salesperson2 = aRandomSalesperson();
            database.insert(aRandomSalesperson(), salesperson1, salesperson2, aRandomSalesperson());

            database.insert(
                scenario.tran,
                scenario.sut,
                database
                    .from(SalespersonRow.class)
                    .where(SalespersonRow::salespersonId).isIn(salesperson1.salespersonId(), salesperson2.salespersonId()));

            assertThat(database.from(scenario.sut.as("t")).select(count()).single(scenario.tran), is(2));
        }
    }

    @Test
    void canSelectFromLocalTempTable() {
        SalespersonRow salespersonRow = aRandomSalesperson();
        try (Scenario<SalespersonRow> scenario = givenLocalTempTable(SalespersonRow.class, "tmp3")
                                                     .containing(aRandomSalesperson(), salespersonRow, aRandomSalesperson())) {

            SalespersonRow result = scenario.database.from(scenario.sut, "t").where(SalespersonRow::salespersonId).isEqualTo(salespersonRow.salespersonId()).single(scenario.tran);

            assertThat(result, is(salespersonRow));
        }
    }

    @Test
    void canSelectFromSubselectIncludingTempTable() {
        SalespersonRow salespersonRow = aRandomSalesperson();
        try (Scenario<SalespersonRow> scenario = givenLocalTempTable(SalespersonRow.class, "tmp4")
                                                     .containing(aRandomSalesperson(), salespersonRow, aRandomSalesperson())) {
            Database database = scenario.database;

            SalespersonRow result = database
                                        .from(database.from(scenario.sut, "x"), "s")
                                        .where(SalespersonRow::salespersonId).isEqualTo(salespersonRow.salespersonId())
                                        .single(scenario.tran);

            assertThat(result, is(salespersonRow));
        }
    }

    @Test
    void canJoinToLocalTempTable() {
        SaleRow sale = aRandomSale();
        SalespersonRow salesperson = aRandomSalesperson(s -> s.salespersonId(sale.salespersonId()));
        try (Scenario<SaleRow> scenario = givenLocalTempTable(SaleRow.class, "tmp_part")
                                              .containing(aRandomSale(), aRandomSale(), sale)) {
            Database database = scenario.database;
            database.insert(salesperson);

            Tuple3<SalespersonRow,SalesAreaRow,SaleRow> result = database
                                                                     .from(SalespersonRow.class)
                                                                     .leftJoin(SalesAreaRow.class, "sa")
                                                                     .on(SalesAreaRow::salespersonId).isEqualTo(SalespersonRow::salespersonId)
                                                                     .join(scenario.sut, "x")
                                                                     .on(SaleRow::salespersonId).isEqualTo(SalespersonRow::salespersonId)
                                                                     .where(SalespersonRow::salespersonId).isEqualTo(salesperson.salespersonId())
                                                                     .single(scenario.tran);
            assertThat(result.item1(), is(salesperson));
            assertThat(result.item3(), is(sale));
        }
    }

    @Test
    void localTempTableIsNotVisibleToOtherConnections() {
        try (Scenario<SalespersonRow> scenario = givenLocalTempTable(SalespersonRow.class, "tmp5")
                                                     .containing(aRandomSalesperson(), aRandomSalesperson())) {
            calling(() -> scenario.database.from(scenario.sut, "x").list())
                .shouldThrow(RuntimeSqlException.class)
                .with(subclass(NoSuchObjectException.class));
        }
    }

    @Test
    void localTempPreservesRowsAfterTransactionCommitsByDefault() {
        try (Scenario<SalespersonRow> scenario = givenLocalTempTable(SalespersonRow.class, "tmp6")
                                                     .containing(aRandomSalesperson(), aRandomSalesperson())) {
            assertThat(scenario.database.from(scenario.sut.as("t")).select(count()).single(scenario.tran), is(2));

            scenario.tran.commit();

            assertThat(scenario.database.from(scenario.sut.as("t")).select(count()).single(scenario.tran), is(2));
        }
    }

    @Test
    void localTempPreservesRowsAfterTransactionCommitsIfRequested() {
        try (Scenario<SalespersonRow> scenario = givenLocalTempTable(SalespersonRow.class, "tmp7", t -> t.onCommit(TempTableCommitAction.PRESERVE_ROWS))
                                                     .containing(aRandomSalesperson(), aRandomSalesperson())) {
            assertThat(scenario.database.from(scenario.sut.as("t")).select(count()).single(scenario.tran), is(2));

            scenario.tran.commit();

            assertThat(scenario.database.from(scenario.sut.as("t")).select(count()).single(scenario.tran), is(2));
        }
    }

    @Test
    void localTempDeletesRowsAfterTransactionCommitsIfRequested() {
        try (Scenario<SalespersonRow> scenario = givenLocalTempTable(SalespersonRow.class, "tmp8", t -> t.onCommit(TempTableCommitAction.DELETE_ROWS))
                                                     .containing(aRandomSalesperson(), aRandomSalesperson())) {
            assertThat(scenario.database.from(scenario.sut.as("t")).select(count()).single(scenario.tran), is(2));

            scenario.tran.commit();

            assertThat(scenario.database.from(scenario.sut.as("t")).select(count()).single(scenario.tran), is(0));
        }
    }

    @Test
    void localTempDroppedAfterTransactionCommitsIfRequested() {
        try (Scenario<SalespersonRow> scenario = givenLocalTempTable(SalespersonRow.class, "tmp9", t -> t.onCommit(TempTableCommitAction.DROP_TABLE))
                                                     .containing(aRandomSalesperson(), aRandomSalesperson())) {
            assertThat(scenario.database.from(scenario.sut.as("t")).select(count()).single(scenario.tran), is(2));

            scenario.tran.commit();

            calling(() -> scenario.database.from(scenario.sut, "x").list(scenario.tran))
                .shouldThrow(RuntimeSqlException.class)
                .with(subclass(NoSuchObjectException.class));
        }
    }

    @ParameterizedTest
    @EnumSource(TempTableCommitAction.class)
    void localTempDroppedAfterTransactionRollsback(TempTableCommitAction commitAction) {
        try (Scenario<SalespersonRow> scenario = givenLocalTempTable(SalespersonRow.class, "tmp10", t -> t.onCommit(commitAction))
                                                     .containing(aRandomSalesperson(), aRandomSalesperson())) {
            assertThat(scenario.database.from(scenario.sut.as("t")).select(count()).single(scenario.tran), is(2));

            scenario.tran.rollback();

            calling(() -> scenario.database.from(scenario.sut.as("t")).select(count()).single(scenario.tran))
                .shouldThrow(RuntimeSqlException.class)
                .with(subclass(NoSuchObjectException.class));
        }
    }

    @Test
    void canUpdateTemporaryTable() {
        SalespersonRow salesperson = aRandomSalesperson(v -> v.firstName("Bruce"));
        try (Scenario<SalespersonRow> scenario = givenLocalTempTable(SalespersonRow.class, "tmp11")
                                                     .containing(salesperson, aRandomSalesperson())) {
            scenario.database.update(scenario.sut, "t")
                .set(SalespersonRow::firstName).to("Wayne")
                .where(SalespersonRow::salespersonId).isEqualTo(salesperson.salespersonId())
                .execute(scenario.tran);

            assertThat(
                scenario.database.from(scenario.sut.as("t"))
                    .select(SalespersonRow::firstName)
                    .where(SalespersonRow::salespersonId).isEqualTo(salesperson.salespersonId())
                    .single(scenario.tran),
                is("Wayne"));

            scenario.tran.commit();
        }
    }

    @Test
    void canDeleteFromLocalTemporaryTable() {
        SalespersonRow salesperson = aRandomSalesperson(v -> v.firstName("Bruce"));
        try (Scenario<SalespersonRow> scenario = givenLocalTempTable(SalespersonRow.class, "tmp12")
                                                     .containing(salesperson, aRandomSalesperson())) {
            scenario.database.delete(scenario.sut)
                .where(SalespersonRow::salespersonId).isEqualTo(salesperson.salespersonId())
                .execute(scenario.tran);

            assertThat(
                scenario.database.from(scenario.sut, "t")
                    .select(count())
                    .where(SalespersonRow::salespersonId).isEqualTo(salesperson.salespersonId())
                    .single(scenario.tran),
                is(0));
        }
    }

    private <T> Scenario<T> givenGlobalTempTable(Class<T> rowClass, String name) {
        assumeTrue(dialect.tempTableInfo().supportsGlobal(), "Database does not support global temporary tables.");
        Database database = testDatabase(dataSource, dialect);
        return new Scenario<>(database, rowClass, name);
    }

    private <T> Scenario<T> givenLocalTempTable(Class<T> rowClass, String name) {
        assumeTrue(dialect.tempTableInfo().supportsLocal(), "Database does not support local temporary tables.");
        Database database = testDatabase(dataSource, dialect);
        return new Scenario<>(database, rowClass, name, Function.identity());
    }

    private <T> Scenario<T> givenLocalTempTable(Class<T> rowClass, String name, Function<LocalTempTable.Builder<T,T>,LocalTempTable.Builder<T,T>> init) {
        assumeTrue(dialect.tempTableInfo().supportsLocal(), "Database does not support local temporary tables.");
        Database database = testDatabase(dataSource, dialect);
        return new Scenario<>(database, rowClass, name, init);
    }

    private static class Scenario<T> implements UncheckedAutoCloseable {
        private final CompositeAutoCloseable closer = new CompositeAutoCloseable();
        private final Database database;
        private final Transaction tran;
        private final TempTable<T> sut;

        public Scenario(Database database, Class<T> rowClass, String name) {
            tran = closer.add(database.getDefaultSqlExecutor().beginTransaction());
            this.database = database;
            sut = database.globalTemporaryTable(tran, rowClass, t -> t.tableName(name));
        }

        public Scenario(Database database, Class<T> rowClass, String name, Function<LocalTempTable.Builder<T,T>,LocalTempTable.Builder<T,T>> init) {
            tran = closer.add(database.getDefaultSqlExecutor().beginTransaction());
            this.database = database;
            sut = database.createTemporaryTable(tran, rowClass, t -> init.apply(t.tableName(name)));
        }

        @SafeVarargs
        private final Scenario<T> containing(T... content) {
            database.insert(tran, sut, content);
            return this;
        }

        @Override
        public void close() {
            closer.close();
        }
    }
}
