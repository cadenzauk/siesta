/*
 * Copyright (c) 2018 Cadenza United Kingdom Limited
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

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.lang.CompositeAutoCloseable;
import com.cadenzauk.core.sql.RuntimeSqlException;
import com.cadenzauk.core.sql.exception.ReferentialIntegrityException;
import com.cadenzauk.core.sql.exception.LockingException;
import com.cadenzauk.core.sql.exception.IllegalNullException;
import com.cadenzauk.core.sql.exception.SqlSyntaxException;
import com.cadenzauk.core.sql.exception.DuplicateKeyException;
import com.cadenzauk.core.sql.exception.InvalidValueException;
import com.cadenzauk.siesta.model.IncorrectSalesAreaRow;
import com.cadenzauk.siesta.model.NoSuchTableRow;
import com.cadenzauk.siesta.model.SalesAreaRow;
import com.cadenzauk.siesta.model.SalespersonRow;
import com.cadenzauk.siesta.model.TestDatabase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static com.cadenzauk.core.sql.testutil.RuntimeSqlExceptionMatcher.subclass;
import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static com.cadenzauk.siesta.grammar.expression.TypedExpression.literal;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public abstract class ExceptionIntegrationTest extends IntegrationTest {
    @Test
    void uniqueConstraintViolationOnInsert() {
        Database database = TestDatabase.testDatabase(dataSource);
        SalespersonRow salespersonRow = aRandomSalesperson();

        calling(() -> database.insert(salespersonRow, salespersonRow))
            .shouldThrow(RuntimeSqlException.class)
            .with(subclass(DuplicateKeyException.class))
            .withMessage(startsWith("Unique constraint violated while executing 'insert into "));
    }

    @Test
    void uniqueConstraintViolationOnInsertInTransaction() {
        Database database = TestDatabase.testDatabase(dataSource);
        SalespersonRow salespersonRow = aRandomSalesperson();

        try (Transaction transaction = database.beginTransaction()) {
            calling(() -> database.insert(transaction, salespersonRow, salespersonRow))
                .shouldThrow(RuntimeSqlException.class)
                .with(subclass(DuplicateKeyException.class))
                .withMessage(startsWith("Unique constraint violated while executing 'insert into "));
        }
    }

    @Test
    void uniqueConstraintViolationOnUpdate() {
        Database database = TestDatabase.testDatabase(dataSource);
        SalespersonRow salespersonRow1 = aRandomSalesperson();
        SalespersonRow salespersonRow2 = aRandomSalesperson();
        database.insert(salespersonRow1, salespersonRow2);

        calling(() -> database
            .update(SalespersonRow.class)
            .set(SalespersonRow::salespersonId).to(salespersonRow1.salespersonId())
            .where(SalespersonRow::salespersonId).isEqualTo(salespersonRow2.salespersonId())
            .execute())
            .shouldThrow(RuntimeSqlException.class)
            .with(subclass(DuplicateKeyException.class))
            .withMessage(startsWith("Unique constraint violated while executing 'update "));
    }

    @Test
    void foreignKeyViolationOnInsert() {
        Database database = TestDatabase.testDatabase(dataSource);
        SalesAreaRow salesArea = SalesAreaRow.newBuilder()
            .salesAreaId(newId())
            .salesAreaName("Far East")
            .salespersonId(Optional.of(newId()))
            .build();

        calling(() -> database.insert(salesArea))
            .shouldThrow(RuntimeSqlException.class)
            .with(subclass(ReferentialIntegrityException.class))
            .withMessage(startsWith("Foreign key constraint violated while executing 'insert into "));
    }

    @Test
    void foreignKeyViolationOnUpdate() {
        Database database = TestDatabase.testDatabase(dataSource);
        SalespersonRow salespersonRow = aRandomSalesperson();
        database.insert(salespersonRow);
        SalesAreaRow salesArea = SalesAreaRow.newBuilder()
            .salesAreaId(newId())
            .salesAreaName("Far East")
            .salespersonId(Optional.of(salespersonRow.salespersonId()))
            .build();
        database.insert(salesArea);

        calling(() -> database
            .update(SalesAreaRow.class)
            .set(SalesAreaRow::salespersonId).to(newId())
            .where(SalesAreaRow::salesAreaId).isEqualTo(salesArea.salesAreaId())
            .execute())
            .shouldThrow(RuntimeSqlException.class)
            .with(subclass(ReferentialIntegrityException.class))
            .withMessage(startsWith("Foreign key constraint violated while executing 'update "));
    }

    @Test
    void foreignKeyViolationOnDelete() {
        Database database = TestDatabase.testDatabase(dataSource);
        SalespersonRow salespersonRow = aRandomSalesperson();
        database.insert(salespersonRow);
        SalesAreaRow salesArea = SalesAreaRow.newBuilder()
            .salesAreaId(newId())
            .salesAreaName("Far East")
            .salespersonId(Optional.of(salespersonRow.salespersonId()))
            .build();
        database.insert(salesArea);

        calling(() -> database
            .delete(SalespersonRow.class)
            .where(SalespersonRow::salespersonId).isEqualTo(salespersonRow.salespersonId())
            .execute())
            .shouldThrow(RuntimeSqlException.class)
            .with(subclass(ReferentialIntegrityException.class))
            .withMessage(startsWith("Foreign key constraint violated while executing 'delete "));
    }

    @Test
    void nullNotAllowedOnInsert() {
        Database database = TestDatabase.testDatabase(dataSource);
        SalesAreaRow salesArea = SalesAreaRow.newBuilder()
            .salesAreaId(newId())
            .build();

        calling(() -> database.insert(salesArea))
            .shouldThrow(RuntimeSqlException.class)
            .with(subclass(IllegalNullException.class))
            .withMessage(startsWith("Null value is not allowed while executing 'insert into "));
    }

    @Test
    void nullNotAllowedOnUpdate() {
        Database database = TestDatabase.testDatabase(dataSource);
        SalesAreaRow salesArea = SalesAreaRow.newBuilder()
            .salesAreaId(newId())
            .salesAreaName("Oceania")
            .build();
        database.insert(salesArea);

        calling(() ->
            database.update(SalesAreaRow.class)
                .set(SalesAreaRow::salesAreaName).toNull()
                .where(SalesAreaRow::salesAreaId).isEqualTo(salesArea.salesAreaId())
                .execute())
            .shouldThrow(RuntimeSqlException.class)
            .with(subclass(IllegalNullException.class))
            .withMessage(startsWith("Null value is not allowed while executing 'update "));
    }

    @Test
    void stringValueTooLongOnInsert() {
        Database database = TestDatabase.testDatabase(dataSource);
        SalesAreaRow salesArea = SalesAreaRow.newBuilder()
            .salesAreaId(newId())
            .salesAreaName("Taumatawhakatangihangakoauauotamateaturipukakapikimaungahoronukupokaiwhenuakitanatahu")
            .build();
        calling(() ->
            database.insert(salesArea))
            .shouldThrow(RuntimeSqlException.class)
            .with(subclass(InvalidValueException.class))
            .withMessage(startsWith("Incorrect data value for data type while executing 'insert into "));
    }

    @Test
    void stringValueTooLongOnUpdate() {
        Database database = TestDatabase.testDatabase(dataSource);
        SalesAreaRow salesArea = SalesAreaRow.newBuilder()
            .salesAreaId(newId())
            .salesAreaName("NZ")
            .build();
        database.insert(salesArea);

        calling(() ->
            database.update(SalesAreaRow.class)
                .set(SalesAreaRow::salesAreaName).to("Taumatawhakatangihangakoauauotamateaturipukakapikimaungahoronukupokaiwhenuakitanatahu")
                .where(SalesAreaRow::salesAreaId).isEqualTo(salesArea.salesAreaId())
                .execute())
            .shouldThrow(RuntimeSqlException.class)
            .with(subclass(InvalidValueException.class))
            .withMessage(startsWith("Incorrect data value for data type while executing 'update "));
    }

    @Test
    void numericOverflowOnInsert() {
        Database database = TestDatabase.testDatabase(dataSource);
        SalesAreaRow salesArea = SalesAreaRow.newBuilder()
            .salesAreaId(newId())
            .salesAreaName("NZ")
            .salesCount(Optional.of(Integer.MAX_VALUE + 100L))
            .build();

        calling(() -> database.insert(salesArea))
            .shouldThrow(RuntimeSqlException.class)
            .with(subclass(InvalidValueException.class))
            .withMessage(startsWith("Incorrect data value for data type while executing 'insert into "));
    }

    @Test
    void numericOverflowOnUpdate() {
        Database database = TestDatabase.testDatabase(dataSource);
        SalesAreaRow salesArea = SalesAreaRow.newBuilder()
            .salesAreaId(newId())
            .salesAreaName("NZ")
            .build();
        database.insert(salesArea);

        calling(() ->
            database.update(SalesAreaRow.class)
                .set(SalesAreaRow::salesCount).to(Integer.MAX_VALUE + 1L)
                .where(SalesAreaRow::salesAreaId).isEqualTo(salesArea.salesAreaId())
                .execute())
            .shouldThrow(RuntimeSqlException.class)
            .with(subclass(InvalidValueException.class))
            .withMessage(startsWith("Incorrect data value for data type while executing 'update "));
    }

    @Test
    void lockTimeoutOnInsert() {
        assumeTrue(dialect.supportsLockTimeout(), "Database does not support lock timeouts.");
        Database database = TestDatabase.testDatabase(dataSource);
        SalesAreaRow salesArea = SalesAreaRow.newBuilder()
            .salesAreaId(newId())
            .salesAreaName("NZ")
            .build();
        try (CompositeAutoCloseable closer = new CompositeAutoCloseable()) {
            Transaction transaction1 = closer.add(database.beginTransaction());
            database.insert(transaction1, salesArea);
            Transaction transaction2 = closer.add(database.beginTransaction());
            closer.add(database.withLockTimeout(transaction2, 0, TimeUnit.MILLISECONDS));

            calling(() -> database.insert(transaction2, salesArea))
                .shouldThrow(RuntimeSqlException.class)
                .with(subclass(LockingException.class))
                .withMessage(startsWith("Locking failure while executing 'insert into"));

            transaction2.rollback();
        }
    }

    @Test
    void lockTimeoutOnUpdate() {
        assumeTrue(dialect.supportsLockTimeout(), "Database does not support lock timeouts.");
        Database database = TestDatabase.testDatabase(dataSource);
        SalesAreaRow salesArea = SalesAreaRow.newBuilder()
            .salesAreaId(newId())
            .salesAreaName("NZ")
            .build();
        database.insert(salesArea);
        try (CompositeAutoCloseable closer = new CompositeAutoCloseable()) {
            Transaction transaction1 = closer.add(database.beginTransaction());
            database.update(SalesAreaRow.class)
                .set(SalesAreaRow::salesCount).to(10L)
                .where(SalesAreaRow::salesAreaId).isEqualTo(salesArea.salesAreaId())
                .execute(transaction1);
            Transaction transaction2 = closer.add(database.beginTransaction());
            closer.add(database.withLockTimeout(transaction2, 0, TimeUnit.MILLISECONDS));

            calling(() -> database.update(SalesAreaRow.class)
                .set(SalesAreaRow::salesCount).to(1L)
                .where(SalesAreaRow::salesAreaId).isEqualTo(salesArea.salesAreaId())
                .execute(transaction2))
                .shouldThrow(RuntimeSqlException.class)
                .with(subclass(LockingException.class))
                .withMessage(startsWith("Locking failure while executing 'update "));

            transaction2.rollback();
        }
    }

    @Test
    void lockTimeoutOnDelete() {
        assumeTrue(dialect.supportsLockTimeout(), "Database does not support lock timeouts.");
        Database database = TestDatabase.testDatabase(dataSource);
        SalesAreaRow salesArea = SalesAreaRow.newBuilder()
            .salesAreaId(newId())
            .salesAreaName("NZ")
            .build();
        database.insert(salesArea);
        try (CompositeAutoCloseable closer = new CompositeAutoCloseable()) {
            Transaction transaction1 = closer.add(database.beginTransaction());
            database.update(SalesAreaRow.class)
                .set(SalesAreaRow::salesCount).to(10L)
                .where(SalesAreaRow::salesAreaId).isEqualTo(salesArea.salesAreaId())
                .execute(transaction1);
            Transaction transaction2 = closer.add(database.beginTransaction());
            closer.add(database.withLockTimeout(transaction2, 0, TimeUnit.MILLISECONDS));

            calling(() ->
                database.delete(SalesAreaRow.class)
                    .where(SalesAreaRow::salesAreaId).isEqualTo(salesArea.salesAreaId())
                    .execute(transaction2))
                .shouldThrow(RuntimeSqlException.class)
                .with(subclass(LockingException.class))
                .withMessage(startsWith("Locking failure while executing 'delete "));

            transaction2.rollback();
        }
    }

    @SuppressWarnings("ThrowableNotThrown")
    @Test
    void deadlockOnUpdate() {
        Database database = TestDatabase.testDatabase(dataSource);
        SalespersonRow salespersonRow1 = aRandomSalesperson();
        SalespersonRow salespersonRow2 = aRandomSalesperson();
        database.insert(salespersonRow1, salespersonRow2);
        CompletableFuture<Void> wait11 = new CompletableFuture<>();
        CompletableFuture<Void> wait12 = new CompletableFuture<>();
        CompletableFuture<Void> wait2 = new CompletableFuture<>();
        CompletableFuture<Void> wait3 = new CompletableFuture<>();
        CompletableFuture<Void> wait4 = new CompletableFuture<>();
        CompletableFuture<Throwable> future1 = performUpdate(database, salespersonRow1, salespersonRow2, wait11, wait2, wait3, wait4);
        CompletableFuture<Throwable> future2 = performUpdate(database, salespersonRow2, salespersonRow1, wait12, wait2, wait3, wait4);

        CompletableFuture.allOf(wait11, wait12).join();
        wait2.complete(null);
        wait3.join();
        wait4.complete(null);
        CompletableFuture.allOf(future1, future2).join();

        Throwable exception = Stream.of(future1, future2)
            .map(CompletableFuture::join)
            .filter(Objects::nonNull)
            .findFirst()
            .orElseThrow(() -> new AssertionError("No exception was thrown for deadlock."));

        assertThat(exception, instanceOf(LockingException.class));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> columnNames() {
        return Stream.of(
            Arguments.of(NoSuchTableRow.class, Function1.of(NoSuchTableRow::column)),
            Arguments.of(IncorrectSalesAreaRow.class, Function1.of(IncorrectSalesAreaRow::badColumnName)),
            Arguments.of(IncorrectSalesAreaRow.class, Function1.of(IncorrectSalesAreaRow::nonexistantColumn))
        );
    }

    @ParameterizedTest
    @MethodSource("columnNames")
    <T> void columnNameIncorrect(Class<T> table, Function1<T,String> column) {
        Database database = TestDatabase.testDatabase(dataSource);

        calling(() -> database.from(table).select(column).list())
            .shouldThrow(RuntimeSqlException.class)
            .with(subclass(SqlSyntaxException.class));
    }

    private CompletableFuture<Throwable> performUpdate(Database database, SalespersonRow salespersonRow1, SalespersonRow salespersonRow2, CompletableFuture<Void> wait1, CompletableFuture<Void> wait2, CompletableFuture<Void> wait3, CompletableFuture<Void> wait4) {
        return supplyAsync(() -> {
            try (Transaction transaction = database.beginTransaction()) {
                updateSalesperson(database, salespersonRow1, transaction);
                wait1.complete(null);
                wait2.join();
                updateSalesperson(database, salespersonRow2, transaction);
                wait3.complete(null);
                wait4.join();
                return null;
            } catch (Throwable t) {
                wait3.complete(null);
                return t;
            }
        });
    }

    private void updateSalesperson(Database database, SalespersonRow salespersonRow, Transaction transaction) {
        database.update(SalespersonRow.class)
            .set(SalespersonRow::numberOfSales).to(SalespersonRow::numberOfSales).plus(literal(1))
            .where(SalespersonRow::salespersonId).isEqualTo(literal(salespersonRow.salespersonId()))
            .execute(transaction);
    }
}
