/*
 * Copyright (c) 2025 Cadenza United Kingdom Limited
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

import com.cadenzauk.core.sql.RuntimeSqlException;
import com.cadenzauk.core.sql.exception.DuplicateKeyException;
import com.cadenzauk.core.sql.exception.LockingException;
import com.cadenzauk.core.sql.exception.ReferentialIntegrityException;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.SqlExecutor;
import com.cadenzauk.siesta.Transaction;
import com.cadenzauk.siesta.dialect.H2Dialect;
import com.cadenzauk.siesta.model.WidgetRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.failedFuture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExecutableStatementClauseTest {
    @Mock
    private Transaction transaction;

    @Mock
    private SqlExecutor sqlExecutor;

    @Captor
    private ArgumentCaptor<String> sql;

    @Captor
    private ArgumentCaptor<Object[]> args;

    @Test
    void executesUsingDefaultSqlExecutorAndReturnsTheRowsUpdated() {
        Database database = Database.newBuilder()
            .defaultSchema("SIESTA")
            .defaultSqlExecutor(sqlExecutor)
            .build();
        Alias<WidgetRow> w = database.table(WidgetRow.class).as("w");
        when(sqlExecutor.update(any(), any())).thenReturn(42);

        int result = database.delete(w).where(WidgetRow::widgetId).isEqualTo(123L).execute();

        assertThat(result, is(42));
        verify(sqlExecutor).update(sql.capture(), args.capture());
        assertThat(sql.getValue(), is("delete from SIESTA.WIDGET w where w.WIDGET_ID = ?"));
        assertThat(args.getValue(), is(arrayContaining(123L)));
    }

    @Test
    void executesUsingSqlExecutorAndReturnsTheRowsUpdated() {
        Database database = Database.newBuilder()
            .defaultSchema("SIESTA")
            .build();
        Alias<WidgetRow> w = database.table(WidgetRow.class).as("w");
        when(sqlExecutor.update(any(), any())).thenReturn(42);

        int result = database.delete(w).where(WidgetRow::widgetId).isEqualTo(123L).execute(sqlExecutor);

        assertThat(result, is(42));
        verify(sqlExecutor).update(sql.capture(), args.capture());
        assertThat(sql.getValue(), is("delete from SIESTA.WIDGET w where w.WIDGET_ID = ?"));
        assertThat(args.getValue(), is(arrayContaining(123L)));
    }

    @Test
    void executesUsingTransactionAndReturnsTheRowsUpdated() {
        Database database = Database.newBuilder()
            .defaultSchema("SIESTA")
            .build();
        Alias<WidgetRow> w = database.table(WidgetRow.class).as("w");
        when(transaction.update(any(), any())).thenReturn(42);

        int result = database.delete(w).where(WidgetRow::widgetId).isEqualTo(123L).execute(transaction);

        assertThat(result, is(42));
        verify(transaction).update(sql.capture(), args.capture());
        assertThat(sql.getValue(), is("delete from SIESTA.WIDGET w where w.WIDGET_ID = ?"));
        assertThat(args.getValue(), is(arrayContaining(123L)));
    }

    @Test
    void executeAsyncUsingTransactionAndReturnsTheRowsUpdated() {
        Database database = Database.newBuilder()
            .defaultSchema("SIESTA")
            .build();
        Alias<WidgetRow> w = database.table(WidgetRow.class).as("w");
        when(transaction.updateAsync(any(), any())).thenReturn(completedFuture(42));

        CompletableFuture<Integer> result = database.delete(w).where(WidgetRow::widgetId).isEqualTo(123L).executeAsync(transaction);

        assertThat(result.join(), is(42));
        verify(transaction).updateAsync(sql.capture(), args.capture());
        assertThat(sql.getValue(), is("delete from SIESTA.WIDGET w where w.WIDGET_ID = ?"));
        assertThat(args.getValue(), is(arrayContaining(123L)));
    }

    @Test
    void exceptionWhenExecutingUsingDefaultSqlExecutorIsTransalated() {
        Database database = Database.newBuilder()
            .defaultSchema("SIESTA")
            .defaultSqlExecutor(sqlExecutor)
            .dialect(new H2Dialect())
            .build();
        when(sqlExecutor.update(any(), any())).thenThrow(new RuntimeSqlException("sql", new SQLException("deadlock", "40001")));

        calling(() -> database.delete(WidgetRow.class).where(WidgetRow::widgetId).isEqualTo(123L).execute())
            .shouldThrow(LockingException.class)
            .withMessage("Locking failure while executing 'delete from SIESTA.WIDGET where SIESTA.WIDGET.WIDGET_ID = ?'.");
    }

    @Test
    void exceptionWhenExecutingUsingSqlExecutorIsTransalated() {
        Database database = Database.newBuilder()
            .defaultSchema("SIESTA")
            .dialect(new H2Dialect())
            .build();
        when(sqlExecutor.update(any(), any())).thenThrow(new RuntimeSqlException("sql", new SQLException("fk", "23513")));

        calling(() -> database.delete(WidgetRow.class).where(WidgetRow::widgetId).isEqualTo(123L).execute(sqlExecutor))
            .shouldThrow(ReferentialIntegrityException.class)
            .withMessage("Foreign key constraint violated while executing 'delete from SIESTA.WIDGET where SIESTA.WIDGET.WIDGET_ID = ?'.");
    }

    @Test
    void exceptionWhenExecutingUsingTransactionIsTransalated() {
        Database database = Database.newBuilder()
            .defaultSchema("SIESTA")
            .dialect(new H2Dialect())
            .build();
        when(transaction.update(any(), any())).thenThrow(new RuntimeSqlException("sql", new SQLException("dup", "23505")));

        calling(() -> database.delete(WidgetRow.class).where(WidgetRow::widgetId).isEqualTo(123L).execute(transaction))
            .shouldThrow(DuplicateKeyException.class)
            .withMessage("Unique constraint violated while executing 'delete from SIESTA.WIDGET where SIESTA.WIDGET.WIDGET_ID = ?'.");
    }

    @Test
    void exceptionWhenExecutingAsyncUsingTransactionIsTransalated() {
        Database database = Database.newBuilder()
            .defaultSchema("SIESTA")
            .dialect(new H2Dialect())
            .build();
        when(transaction.updateAsync(any(), any())).thenReturn(failedFuture(new RuntimeSqlException("sql", new SQLException("dup", "23505"))));

        calling(() -> database.delete(WidgetRow.class).where(WidgetRow::widgetId).isEqualTo(123L).executeAsync(transaction).join())
            .shouldThrow(CompletionException.class)
            .withCause(DuplicateKeyException.class)
            .withMessage("Unique constraint violated while executing 'delete from SIESTA.WIDGET where SIESTA.WIDGET.WIDGET_ID = ?'.");
    }

    @Test
    void sqlReturnsTheStatementSql() {
        Database database = Database.newBuilder()
            .defaultSchema("SIESTA")
            .build();
        Alias<WidgetRow> w = database.table(WidgetRow.class).as("w");
        InWhereExpectingAnd sut = database.delete(w).where(WidgetRow::widgetId).isEqualTo(123L);

        String result = sut.sql();

        assertThat(result, is("delete from SIESTA.WIDGET w where w.WIDGET_ID = ?"));
    }

    @Test
    void argsReturnsTheStatementArgs() {
        Database database = Database.newBuilder()
            .defaultSchema("SIESTA")
            .build();
        Alias<WidgetRow> w = database.table(WidgetRow.class).as("w");
        InWhereExpectingAnd sut = database.delete(w).where(WidgetRow::widgetId).isEqualTo(123L).and(WidgetRow::description).isLike("a%");

        List<Object> result = sut.args().collect(Collectors.toList());

        assertThat(result, contains(123L, "a%"));
    }
}
