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

package com.cadenzauk.siesta.grammar.select;

import co.unruly.matchers.OptionalMatchers;
import co.unruly.matchers.StreamMatchers;
import com.cadenzauk.core.junit.TestCase;
import com.cadenzauk.core.junit.TestCaseArgumentsProvider;
import com.cadenzauk.core.lang.CompositeAutoCloseable;
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.core.sql.RuntimeSqlException;
import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.core.tuple.Tuple3;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.IsolationLevel;
import com.cadenzauk.siesta.LockLevel;
import com.cadenzauk.siesta.Order;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.SqlExecutor;
import com.cadenzauk.siesta.Transaction;
import com.cadenzauk.siesta.dialect.AnsiDialect;
import com.cadenzauk.siesta.dialect.Db2Dialect;
import com.cadenzauk.siesta.grammar.expression.Precedence;
import com.cadenzauk.siesta.model.SalesAreaRow;
import com.cadenzauk.siesta.model.SalespersonRow;
import com.cadenzauk.siesta.model.TestDatabase;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Stream;

import static co.unruly.matchers.OptionalMatchers.empty;
import static com.cadenzauk.core.function.FunctionUtil.supplier;
import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static com.cadenzauk.siesta.grammar.expression.TypedExpression.literal;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SelectTest {
    @Mock
    private SqlExecutor defaultSqlExecutor;

    @Mock
    private SqlExecutor sqlExecutor;

    @Mock
    private Transaction transaction;

    @Mock
    private CompletableFuture<List<Long>> future;

    @Mock
    private RuntimeSqlException exception;

    @Mock
    private CompositeAutoCloseable compositeCloseable;

    @Mock
    private Stream<Long> stream;

    @Captor
    private ArgumentCaptor<String> sqlCaptor;

    @Captor
    private ArgumentCaptor<Object[]> argsCaptor;

    @Captor
    private ArgumentCaptor<RowMapper<Long>> rowMapperCaptor;

    @Test
    void listWithoutArgsUsesTheDefaultSqlExecutor() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId).where(SalespersonRow::firstName).isEqualTo("James");

        sut.list();

        verify(defaultSqlExecutor).query(sqlCaptor.capture(), argsCaptor.capture(), rowMapperCaptor.capture());
        assertThat(sqlCaptor.getValue(), is("select p.SALESPERSON_ID as p_SALESPERSON_ID from SIESTA.SALESPERSON p where p.FIRST_NAME = ?"));
        assertThat(argsCaptor.getValue(), arrayContaining("James"));
        assertThat(rowMapperCaptor.getValue(), isLongMapperFor("p_SALESPERSON_ID"));
    }

    @Test
    void listWithoutArgsThrowsIfNoDefault() {
        Select<SalespersonRow> sut = databaseWithNoDefaultSqlExecutor().from(SalespersonRow.class);

        calling(supplier(sut::list))
            .shouldThrow(IllegalStateException.class)
            .withMessage("Default SQL executor has not been set.");
    }

    @Test
    void listWithoutArgsTranslatesExceptions() {
        Database database = database();
        Select<SalespersonRow> sut = database.from(SalespersonRow.class);
        when(defaultSqlExecutor.query(any(), any(), anyRowMapper())).thenThrow(exception);

        calling(supplier(sut::list))
            .shouldThrow(RuntimeSqlException.class);

        verify(database).translateException(any(), eq(exception));
    }

    @Test
    void listWithSqlExecutorUsesThatRatherThanTheDefault() {
        Select<Long> sut = database().from(SalespersonRow.class, "q").select(SalespersonRow::salespersonId).where(SalespersonRow::surname).isEqualTo("Kirk");

        sut.list(sqlExecutor);

        verify(sqlExecutor).query(sqlCaptor.capture(), argsCaptor.capture(), rowMapperCaptor.capture());
        verifyNoInteractions(defaultSqlExecutor);
        assertThat(sqlCaptor.getValue(), is("select q.SALESPERSON_ID as q_SALESPERSON_ID from SIESTA.SALESPERSON q where q.SURNAME = ?"));
        assertThat(argsCaptor.getValue(), arrayContaining("Kirk"));
        assertThat(rowMapperCaptor.getValue(), isLongMapperFor("q_SALESPERSON_ID"));
    }

    @Test
    void listWithSqlExecutorTranslatesExceptions() {
        Database database = database();
        Select<SalespersonRow> sut = database.from(SalespersonRow.class);
        when(sqlExecutor.query(any(), any(), anyRowMapper())).thenThrow(exception);

        calling(() -> sut.list(sqlExecutor))
            .shouldThrow(RuntimeSqlException.class);

        verify(database).translateException(any(), eq(exception));
    }

    @Test
    void listWithTransactionUsesThatRatherThanDefaultSqlExecutor() {
        Select<Long> sut = database().from(SalespersonRow.class, "r").select(SalespersonRow::salespersonId).where(SalespersonRow::middleNames).isEqualTo("Tiberius");

        sut.list(transaction);

        verify(transaction).query(sqlCaptor.capture(), argsCaptor.capture(), rowMapperCaptor.capture());
        verifyNoMoreInteractions(defaultSqlExecutor, transaction);
        assertThat(sqlCaptor.getValue(), is("select r.SALESPERSON_ID as r_SALESPERSON_ID from SIESTA.SALESPERSON r where r.MIDDLE_NAMES = ?"));
        assertThat(argsCaptor.getValue(), arrayContaining("Tiberius"));
        assertThat(rowMapperCaptor.getValue(), isLongMapperFor("r_SALESPERSON_ID"));
    }

    @Test
    void listWithTransactionTranslatesExceptions() {
        Database database = database();
        Select<SalespersonRow> sut = database.from(SalespersonRow.class);
        when(transaction.query(any(), any(), anyRowMapper())).thenThrow(exception);

        calling(() -> sut.list(transaction))
            .shouldThrow(RuntimeSqlException.class);

        verify(database).translateException(any(), eq(exception));
    }

    @Test
    void listAsyncWithoutArgsUsesTransactionFromTheDefaultSqlExecutor() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId).where(SalespersonRow::firstName).isEqualTo("James");
        when(defaultSqlExecutor.beginTransaction()).thenReturn(transaction);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(future);

        sut.listAsync();

        verify(transaction).queryAsync(sqlCaptor.capture(), argsCaptor.capture(), rowMapperCaptor.capture());
        assertThat(sqlCaptor.getValue(), is("select p.SALESPERSON_ID as p_SALESPERSON_ID from SIESTA.SALESPERSON p where p.FIRST_NAME = ?"));
        assertThat(argsCaptor.getValue(), arrayContaining("James"));
        assertThat(rowMapperCaptor.getValue(), isLongMapperFor("p_SALESPERSON_ID"));
    }

    @Test
    void listAsyncWithoutArgsThrowsIfNoDefault() {
        Select<SalespersonRow> sut = databaseWithNoDefaultSqlExecutor().from(SalespersonRow.class);

        calling(supplier(sut::listAsync))
            .shouldThrow(IllegalStateException.class)
            .withMessage("Default SQL executor has not been set.");
    }

    @Test
    void listAsyncWithoutArgsTranslatesExceptions() {
        Database database = database();
        Select<SalespersonRow> sut = database.from(SalespersonRow.class);
        when(defaultSqlExecutor.beginTransaction()).thenReturn(transaction);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(failedFuture(exception));

        calling(() -> sut.listAsync().join())
            .shouldThrow(CompletionException.class)
            .withCause(RuntimeSqlException.class);

        verify(database).translateException(any(), eq(exception));
    }

    @Test
    void listAsyncWithSqlExecutorUsesTransactionFromThatExecutor() {
        Select<Long> sut = database().from(SalespersonRow.class, "q").select(SalespersonRow::salespersonId).where(SalespersonRow::surname).isEqualTo("Kirk");
        when(sqlExecutor.beginTransaction()).thenReturn(transaction);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(future);

        sut.listAsync(sqlExecutor);

        verify(transaction).queryAsync(sqlCaptor.capture(), argsCaptor.capture(), rowMapperCaptor.capture());
        verifyNoInteractions(defaultSqlExecutor);
        assertThat(sqlCaptor.getValue(), is("select q.SALESPERSON_ID as q_SALESPERSON_ID from SIESTA.SALESPERSON q where q.SURNAME = ?"));
        assertThat(argsCaptor.getValue(), arrayContaining("Kirk"));
        assertThat(rowMapperCaptor.getValue(), isLongMapperFor("q_SALESPERSON_ID"));
    }

    @Test
    void listAsyncWithSqlExecutorTranslatesExceptions() {
        Database database = database();
        Select<SalespersonRow> sut = database.from(SalespersonRow.class);
        when(sqlExecutor.beginTransaction()).thenReturn(transaction);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(failedFuture(exception));

        calling(() -> sut.listAsync(sqlExecutor).join())
            .shouldThrow(CompletionException.class)
            .withCause(RuntimeSqlException.class);

        verify(database).translateException(any(), eq(exception));
    }

    @Test
    void listAsyncWithTransactionUsesThatAndNotTheDefaultSqlExecutor() {
        Select<Long> sut = database().from(SalespersonRow.class, "r").select(SalespersonRow::salespersonId).where(SalespersonRow::middleNames).isEqualTo("Tiberius");
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(future);

        sut.listAsync(transaction);

        verify(transaction).queryAsync(sqlCaptor.capture(), argsCaptor.capture(), rowMapperCaptor.capture());
        verifyNoMoreInteractions(defaultSqlExecutor, transaction);
        assertThat(sqlCaptor.getValue(), is("select r.SALESPERSON_ID as r_SALESPERSON_ID from SIESTA.SALESPERSON r where r.MIDDLE_NAMES = ?"));
        assertThat(argsCaptor.getValue(), arrayContaining("Tiberius"));
        assertThat(rowMapperCaptor.getValue(), isLongMapperFor("r_SALESPERSON_ID"));
    }

    @Test
    void listAsyncWithTransactionTranslatesExceptions() {
        Database database = database();
        Select<SalespersonRow> sut = database.from(SalespersonRow.class);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(failedFuture(exception));

        calling(() -> sut.listAsync(transaction).join())
            .shouldThrow(CompletionException.class)
            .withCause(RuntimeSqlException.class);

        verify(database).translateException(any(), eq(exception));
    }

    @Test
    void optionalWithoutArgsUsesTheDefaultSqlExecutor() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId).where(SalespersonRow::firstName).isEqualTo("James");

        sut.optional();

        verify(defaultSqlExecutor).query(sqlCaptor.capture(), argsCaptor.capture(), rowMapperCaptor.capture());
        assertThat(sqlCaptor.getValue(), is("select p.SALESPERSON_ID as p_SALESPERSON_ID from SIESTA.SALESPERSON p where p.FIRST_NAME = ?"));
        assertThat(argsCaptor.getValue(), arrayContaining("James"));
        assertThat(rowMapperCaptor.getValue(), isLongMapperFor("p_SALESPERSON_ID"));
    }

    @Test
    void optionalWithoutArgsTranslatesExceptions() {
        Database database = database();
        Select<SalespersonRow> sut = database.from(SalespersonRow.class);
        when(sqlExecutor.query(any(), any(), anyRowMapper())).thenThrow(exception);

        calling(() -> sut.optional(sqlExecutor))
            .shouldThrow(RuntimeSqlException.class);

        verify(database).translateException(any(), eq(exception));
    }

    @Test
    void optionalWithoutArgsThrowsIfNoDefault() {
        Select<SalespersonRow> sut = databaseWithNoDefaultSqlExecutor().from(SalespersonRow.class);

        calling(supplier(sut::optional))
            .shouldThrow(IllegalStateException.class)
            .withMessage("Default SQL executor has not been set.");
    }

    @Test
    void optionalWithoutArgsEmptyIfNoResults() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(defaultSqlExecutor.query(any(), any(), anyRowMapper())).thenReturn(ImmutableList.of());

        Optional<Long> result = sut.optional();

        assertThat(result, empty());
    }

    @Test
    void optionalWithoutArgsReturnsSingleResult() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(defaultSqlExecutor.query(any(), any(), anyRowMapper())).thenReturn(ImmutableList.of(3L));

        Optional<Long> result = sut.optional();

        assertThat(result, OptionalMatchers.contains(3L));
    }

    @Test
    void optionalWithoutArgsThrowsIfIfMoreThanOneResult() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(defaultSqlExecutor.query(any(), any(), anyRowMapper())).thenReturn(ImmutableList.of(1L, 2L));

        calling(supplier(sut::optional))
            .shouldThrow(IllegalArgumentException.class)
            .withMessage("expected one element but was: <1, 2>");
    }

    @Test
    void optionalWithSqlExecutorUsesThatRatherThanTheDefault() {
        Select<Long> sut = database().from(SalespersonRow.class, "q").select(SalespersonRow::salespersonId).where(SalespersonRow::surname).isEqualTo("Kirk");

        sut.optional(sqlExecutor);

        verify(sqlExecutor).query(sqlCaptor.capture(), argsCaptor.capture(), rowMapperCaptor.capture());
        verifyNoInteractions(defaultSqlExecutor);
        assertThat(sqlCaptor.getValue(), is("select q.SALESPERSON_ID as q_SALESPERSON_ID from SIESTA.SALESPERSON q where q.SURNAME = ?"));
        assertThat(argsCaptor.getValue(), arrayContaining("Kirk"));
        assertThat(rowMapperCaptor.getValue(), isLongMapperFor("q_SALESPERSON_ID"));
    }

    @Test
    void optionalWithSqlExecutorTranslatesExceptions() {
        Database database = database();
        Select<SalespersonRow> sut = database.from(SalespersonRow.class);
        when(sqlExecutor.query(any(), any(), anyRowMapper())).thenThrow(exception);

        calling(() -> sut.optional(sqlExecutor))
            .shouldThrow(RuntimeSqlException.class);

        verify(database).translateException(any(), eq(exception));
    }

    @Test
    void optionalWithSqlExecutorEmptyIfNoResults() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(sqlExecutor.query(any(), any(), anyRowMapper())).thenReturn(ImmutableList.of());

        Optional<Long> result = sut.optional(sqlExecutor);

        assertThat(result, empty());
    }

    @Test
    void optionalWithSqlExecutorReturnsSingleResult() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(sqlExecutor.query(any(), any(), anyRowMapper())).thenReturn(ImmutableList.of(3L));

        Optional<Long> result = sut.optional(sqlExecutor);

        assertThat(result, OptionalMatchers.contains(3L));
    }

    @Test
    void optionalWithSqlExecutorThrowsIfIfMoreThanOneResult() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(sqlExecutor.query(any(), any(), anyRowMapper())).thenReturn(ImmutableList.of(1L, 2L));

        calling(() -> sut.optional(sqlExecutor))
            .shouldThrow(IllegalArgumentException.class)
            .withMessage("expected one element but was: <1, 2>");
    }

    @Test
    void optionalWithTransactionUsesThatRatherThanDefaultSqlExecutor() {
        Select<Long> sut = database().from(SalespersonRow.class, "r").select(SalespersonRow::salespersonId).where(SalespersonRow::middleNames).isEqualTo("Tiberius");

        sut.optional(transaction);

        verify(transaction).query(sqlCaptor.capture(), argsCaptor.capture(), rowMapperCaptor.capture());
        verifyNoMoreInteractions(defaultSqlExecutor, transaction);
        assertThat(sqlCaptor.getValue(), is("select r.SALESPERSON_ID as r_SALESPERSON_ID from SIESTA.SALESPERSON r where r.MIDDLE_NAMES = ?"));
        assertThat(argsCaptor.getValue(), arrayContaining("Tiberius"));
        assertThat(rowMapperCaptor.getValue(), isLongMapperFor("r_SALESPERSON_ID"));
    }

    @Test
    void optionalWithTransactionTranslatesExceptions() {
        Database database = database();
        Select<SalespersonRow> sut = database.from(SalespersonRow.class);
        when(transaction.query(any(), any(), anyRowMapper())).thenThrow(exception);

        calling(() -> sut.optional(transaction))
            .shouldThrow(RuntimeSqlException.class);

        verify(database).translateException(any(), eq(exception));
    }

    @Test
    void optionalWithTransactionEmptyIfNoResults() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(transaction.query(any(), any(), anyRowMapper())).thenReturn(ImmutableList.of());

        Optional<Long> result = sut.optional(transaction);

        assertThat(result, empty());
    }

    @Test
    void optionalWithTransactionReturnsSingleResult() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(transaction.query(any(), any(), anyRowMapper())).thenReturn(ImmutableList.of(3L));

        Optional<Long> result = sut.optional(transaction);

        assertThat(result, OptionalMatchers.contains(3L));
    }

    @Test
    void optionalWithTransactionThrowsIfIfMoreThanOneResult() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(transaction.query(any(), any(), anyRowMapper())).thenReturn(ImmutableList.of(1L, 2L));

        calling(() -> sut.optional(transaction))
            .shouldThrow(IllegalArgumentException.class)
            .withMessage("expected one element but was: <1, 2>");
    }

    @Test
    void optionalAsyncWithoutArgsUsesTransactionFromTheDefaultSqlExecutor() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId).where(SalespersonRow::firstName).isEqualTo("James");
        when(defaultSqlExecutor.beginTransaction()).thenReturn(transaction);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(completedFuture(ImmutableList.of()));

        sut.optionalAsync();

        verify(transaction).queryAsync(sqlCaptor.capture(), argsCaptor.capture(), rowMapperCaptor.capture());
        assertThat(sqlCaptor.getValue(), is("select p.SALESPERSON_ID as p_SALESPERSON_ID from SIESTA.SALESPERSON p where p.FIRST_NAME = ?"));
        assertThat(argsCaptor.getValue(), arrayContaining("James"));
        assertThat(rowMapperCaptor.getValue(), isLongMapperFor("p_SALESPERSON_ID"));
    }

    @Test
    void optionalAsyncWithoutArgsThrowsIfNoDefault() {
        Select<SalespersonRow> sut = databaseWithNoDefaultSqlExecutor().from(SalespersonRow.class);

        calling(supplier(sut::optionalAsync))
            .shouldThrow(IllegalStateException.class)
            .withMessage("Default SQL executor has not been set.");
    }

    @Test
    void optionalAsyncWithoutArgsTranslatesExceptions() {
        Database database = database();
        Select<SalespersonRow> sut = database.from(SalespersonRow.class);
        when(defaultSqlExecutor.beginTransaction()).thenReturn(transaction);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(failedFuture(exception));

        calling(() -> sut.optionalAsync().join())
            .shouldThrow(CompletionException.class)
            .withCause(RuntimeSqlException.class);

        verify(database).translateException(any(), eq(exception));
    }

    @Test
    void optionalAsyncWithoutArgsEmptyIfNoResults() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(defaultSqlExecutor.beginTransaction()).thenReturn(transaction);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(completedFuture(ImmutableList.of()));

        Optional<Long> result = sut.optionalAsync().join();

        assertThat(result, empty());
    }

    @Test
    void optionalAsyncWithoutArgsReturnsSingleResult() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(defaultSqlExecutor.beginTransaction()).thenReturn(transaction);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(completedFuture(ImmutableList.of(3L)));

        Optional<Long> result = sut.optionalAsync().join();

        assertThat(result, OptionalMatchers.contains(3L));
    }

    @Test
    void optionalAsyncWithoutArgsThrowsIfIfMoreThanOneResult() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(defaultSqlExecutor.beginTransaction()).thenReturn(transaction);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(completedFuture(ImmutableList.of(1L, 2L)));

        calling(() -> sut.optionalAsync().join())
            .shouldThrow(CompletionException.class)
            .withCause(IllegalArgumentException.class)
            .withMessage("expected one element but was: <1, 2>");
    }

    @Test
    void optionalAsyncWithSqlExecutorUsesTransactionFromThatExecutor() {
        Select<Long> sut = database().from(SalespersonRow.class, "q").select(SalespersonRow::salespersonId).where(SalespersonRow::surname).isEqualTo("Kirk");
        when(sqlExecutor.beginTransaction()).thenReturn(transaction);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(completedFuture(ImmutableList.of()));

        sut.optionalAsync(sqlExecutor);

        verify(transaction).queryAsync(sqlCaptor.capture(), argsCaptor.capture(), rowMapperCaptor.capture());
        verifyNoInteractions(defaultSqlExecutor);
        assertThat(sqlCaptor.getValue(), is("select q.SALESPERSON_ID as q_SALESPERSON_ID from SIESTA.SALESPERSON q where q.SURNAME = ?"));
        assertThat(argsCaptor.getValue(), arrayContaining("Kirk"));
        assertThat(rowMapperCaptor.getValue(), isLongMapperFor("q_SALESPERSON_ID"));
    }

    @Test
    void optionalAsyncWithSqlExecutorTranslatesExceptions() {
        Database database = database();
        Select<SalespersonRow> sut = database.from(SalespersonRow.class);
        when(sqlExecutor.beginTransaction()).thenReturn(transaction);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(failedFuture(exception));

        calling(() -> sut.optionalAsync(sqlExecutor).join())
            .shouldThrow(CompletionException.class)
            .withCause(RuntimeSqlException.class);

        verify(database).translateException(any(), eq(exception));
    }

    @Test
    void optionalAsyncWithSqlExecutorEmptyIfNoResults() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(sqlExecutor.beginTransaction()).thenReturn(transaction);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(completedFuture(ImmutableList.of()));

        Optional<Long> result = sut.optionalAsync(sqlExecutor).join();

        assertThat(result, empty());
    }

    @Test
    void optionalAsyncWithSqlExecutorReturnsSingleResult() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(sqlExecutor.beginTransaction()).thenReturn(transaction);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(completedFuture(ImmutableList.of(3L)));

        Optional<Long> result = sut.optionalAsync(sqlExecutor).join();

        assertThat(result, OptionalMatchers.contains(3L));
    }

    @Test
    void optionalAsyncWithSqlExecutorThrowsIfIfMoreThanOneResult() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(sqlExecutor.beginTransaction()).thenReturn(transaction);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(completedFuture(ImmutableList.of(1L, 2L)));

        calling(() -> sut.optionalAsync(sqlExecutor).join())
            .shouldThrow(CompletionException.class)
            .withCause(IllegalArgumentException.class)
            .withMessage("expected one element but was: <1, 2>");
    }

    @Test
    void optionalAsyncWithTransactionUsesThatAndNotTheDefaultSqlExecutor() {
        Select<Long> sut = database().from(SalespersonRow.class, "r").select(SalespersonRow::salespersonId).where(SalespersonRow::middleNames).isEqualTo("Tiberius");
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(completedFuture(ImmutableList.of()));

        sut.optionalAsync(transaction);

        verify(transaction).queryAsync(sqlCaptor.capture(), argsCaptor.capture(), rowMapperCaptor.capture());
        verifyNoMoreInteractions(defaultSqlExecutor, transaction);
        assertThat(sqlCaptor.getValue(), is("select r.SALESPERSON_ID as r_SALESPERSON_ID from SIESTA.SALESPERSON r where r.MIDDLE_NAMES = ?"));
        assertThat(argsCaptor.getValue(), arrayContaining("Tiberius"));
        assertThat(rowMapperCaptor.getValue(), isLongMapperFor("r_SALESPERSON_ID"));
    }

    @Test
    void optionalAsyncWithTransactionTranslatesExceptions() {
        Database database = database();
        Select<SalespersonRow> sut = database.from(SalespersonRow.class);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(failedFuture(exception));

        calling(() -> sut.optionalAsync(transaction).join())
            .shouldThrow(CompletionException.class)
            .withCause(RuntimeSqlException.class);

        verify(database).translateException(any(), eq(exception));
    }

    @Test
    void optionalAsyncWithTransactionEmptyIfNoResults() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(completedFuture(ImmutableList.of()));

        Optional<Long> result = sut.optionalAsync(transaction).join();

        assertThat(result, empty());
    }

    @Test
    void optionalAsyncWithTransactionReturnsSingleResult() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(completedFuture(ImmutableList.of(3L)));

        Optional<Long> result = sut.optionalAsync(transaction).join();

        assertThat(result, OptionalMatchers.contains(3L));
    }

    @Test
    void optionalAsyncWithTransactionThrowsIfIfMoreThanOneResult() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(completedFuture(ImmutableList.of(1L, 2L)));

        calling(() -> sut.optionalAsync(transaction).join())
            .shouldThrow(CompletionException.class)
            .withCause(IllegalArgumentException.class)
            .withMessage("expected one element but was: <1, 2>");
    }

    @Test
    void streamWithoutArgsUsesTheDefaultExecutor() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId).where(SalespersonRow::firstName).isEqualTo("James");

        sut.stream();

        verify(defaultSqlExecutor).stream(sqlCaptor.capture(), argsCaptor.capture(), rowMapperCaptor.capture());
        assertThat(sqlCaptor.getValue(), is("select p.SALESPERSON_ID as p_SALESPERSON_ID from SIESTA.SALESPERSON p where p.FIRST_NAME = ?"));
        assertThat(argsCaptor.getValue(), arrayContaining("James"));
        assertThat(rowMapperCaptor.getValue(), isLongMapperFor("p_SALESPERSON_ID"));
    }

    @Test
    void streamWithCompositeCloseableUsesTheDefaultExecutor() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId).where(SalespersonRow::firstName).isEqualTo("James");

        sut.stream(compositeCloseable);

        verify(defaultSqlExecutor).stream(sqlCaptor.capture(), argsCaptor.capture(), rowMapperCaptor.capture());
        assertThat(sqlCaptor.getValue(), is("select p.SALESPERSON_ID as p_SALESPERSON_ID from SIESTA.SALESPERSON p where p.FIRST_NAME = ?"));
        assertThat(argsCaptor.getValue(), arrayContaining("James"));
        assertThat(rowMapperCaptor.getValue(), isLongMapperFor("p_SALESPERSON_ID"));
    }

    @Test
    void streamWithCompositeCloseableAddsStreamToCloseable() {
        Select<Long> sut = database().from(SalespersonRow.class, "r").select(SalespersonRow::salespersonId).where(SalespersonRow::middleNames).isEqualTo("Tiberius");
        when(defaultSqlExecutor.stream(any(), any(), anyRowMapper())).thenReturn(stream);

        sut.stream(compositeCloseable);

        verify(compositeCloseable).add(stream);
    }

    @Test
    void streamWithoutArgsThrowsIfNoDefault() {
        Select<SalespersonRow> sut = databaseWithNoDefaultSqlExecutor().from(SalespersonRow.class);

        calling(supplier(sut::stream))
            .shouldThrow(IllegalStateException.class)
            .withMessage("Default SQL executor has not been set.");
    }

    @Test
    void streamWithCompositeCloseableThrowsIfNoDefault() {
        Select<SalespersonRow> sut = databaseWithNoDefaultSqlExecutor().from(SalespersonRow.class);

        calling(() -> sut.stream(compositeCloseable))
            .shouldThrow(IllegalStateException.class)
            .withMessage("Default SQL executor has not been set.");
    }

    @Test
    void streamWithoutArgsTranslatesExceptions() {
        Database database = database();
        Select<SalespersonRow> sut = database.from(SalespersonRow.class);
        when(defaultSqlExecutor.stream(any(), any(), anyRowMapper())).thenThrow(exception);

        calling(supplier(sut::stream))
            .shouldThrow(RuntimeSqlException.class);

        verify(database).translateException(any(), eq(exception));
    }

    @Test
    void streamWithCompositeCloseableTranslatesExceptions() {
        Database database = database();
        Select<SalespersonRow> sut = database.from(SalespersonRow.class);
        when(defaultSqlExecutor.stream(any(), any(), anyRowMapper())).thenThrow(exception);

        calling(() -> sut.stream(compositeCloseable))
            .shouldThrow(RuntimeSqlException.class);

        verify(database).translateException(any(), eq(exception));
    }

    @Test
    void streamWithSqlExecutorUsesThatRatherThanTheDefault() {
        Select<Long> sut = database().from(SalespersonRow.class, "q").select(SalespersonRow::salespersonId).where(SalespersonRow::surname).isEqualTo("Kirk");

        sut.stream(sqlExecutor);

        verify(sqlExecutor).stream(sqlCaptor.capture(), argsCaptor.capture(), rowMapperCaptor.capture());
        verifyNoInteractions(defaultSqlExecutor);
        assertThat(sqlCaptor.getValue(), is("select q.SALESPERSON_ID as q_SALESPERSON_ID from SIESTA.SALESPERSON q where q.SURNAME = ?"));
        assertThat(argsCaptor.getValue(), arrayContaining("Kirk"));
        assertThat(rowMapperCaptor.getValue(), isLongMapperFor("q_SALESPERSON_ID"));
    }

    @Test
    void streamWithSqlExecutorAndCompositeCloseableUsesTheSqlExecutorRatherThanTheDefault() {
        Select<Long> sut = database().from(SalespersonRow.class, "q").select(SalespersonRow::salespersonId).where(SalespersonRow::surname).isEqualTo("Kirk");

        sut.stream(sqlExecutor, compositeCloseable);

        verify(sqlExecutor).stream(sqlCaptor.capture(), argsCaptor.capture(), rowMapperCaptor.capture());
        verifyNoInteractions(defaultSqlExecutor);
        assertThat(sqlCaptor.getValue(), is("select q.SALESPERSON_ID as q_SALESPERSON_ID from SIESTA.SALESPERSON q where q.SURNAME = ?"));
        assertThat(argsCaptor.getValue(), arrayContaining("Kirk"));
        assertThat(rowMapperCaptor.getValue(), isLongMapperFor("q_SALESPERSON_ID"));
    }

    @Test
    void streamWithSqlExecutorAndCompositeCloseableAddsStreamToCloseable() {
        Select<Long> sut = database().from(SalespersonRow.class, "r").select(SalespersonRow::salespersonId).where(SalespersonRow::middleNames).isEqualTo("Tiberius");
        when(sqlExecutor.stream(any(), any(), anyRowMapper())).thenReturn(stream);

        sut.stream(sqlExecutor, compositeCloseable);

        verify(compositeCloseable).add(stream);
    }

    @Test
    void streamWithSqlExecutorTranslatesExceptions() {
        Database database = database();
        Select<SalespersonRow> sut = database.from(SalespersonRow.class);
        when(sqlExecutor.stream(any(), any(), anyRowMapper())).thenThrow(exception);

        calling(() -> sut.stream(sqlExecutor))
            .shouldThrow(RuntimeSqlException.class);

        verify(database).translateException(any(), eq(exception));
    }

    @Test
    void streamWithSqlExecutorAndCompositeCloseableTranslatesExceptions() {
        Database database = database();
        Select<SalespersonRow> sut = database.from(SalespersonRow.class);
        when(sqlExecutor.stream(any(), any(), anyRowMapper())).thenThrow(exception);

        calling(() -> sut.stream(sqlExecutor, compositeCloseable))
            .shouldThrow(RuntimeSqlException.class);

        verify(database).translateException(any(), eq(exception));
    }

    @Test
    void streamWithTransactionUsesThatRatherThanDefaultSqlExecutor() {
        Select<Long> sut = database().from(SalespersonRow.class, "r").select(SalespersonRow::salespersonId).where(SalespersonRow::middleNames).isEqualTo("Tiberius");

        sut.stream(transaction);

        verify(transaction).stream(sqlCaptor.capture(), argsCaptor.capture(), rowMapperCaptor.capture());
        verifyNoMoreInteractions(defaultSqlExecutor, transaction);
        assertThat(sqlCaptor.getValue(), is("select r.SALESPERSON_ID as r_SALESPERSON_ID from SIESTA.SALESPERSON r where r.MIDDLE_NAMES = ?"));
        assertThat(argsCaptor.getValue(), arrayContaining("Tiberius"));
        assertThat(rowMapperCaptor.getValue(), isLongMapperFor("r_SALESPERSON_ID"));
    }

    @Test
    void streamWithTransactionAndCompositeCloseableUsesTheTransactionRatherThanDefaultSqlExecutor() {
        Select<Long> sut = database().from(SalespersonRow.class, "r").select(SalespersonRow::salespersonId).where(SalespersonRow::middleNames).isEqualTo("Tiberius");

        sut.stream(transaction, compositeCloseable);

        verify(transaction).stream(sqlCaptor.capture(), argsCaptor.capture(), rowMapperCaptor.capture());
        verifyNoMoreInteractions(defaultSqlExecutor, transaction);
        assertThat(sqlCaptor.getValue(), is("select r.SALESPERSON_ID as r_SALESPERSON_ID from SIESTA.SALESPERSON r where r.MIDDLE_NAMES = ?"));
        assertThat(argsCaptor.getValue(), arrayContaining("Tiberius"));
        assertThat(rowMapperCaptor.getValue(), isLongMapperFor("r_SALESPERSON_ID"));
    }

    @Test
    void streamWithTransactionAndCompositeCloseableAddsStreamToCloseable() {
        Select<Long> sut = database().from(SalespersonRow.class, "r").select(SalespersonRow::salespersonId).where(SalespersonRow::middleNames).isEqualTo("Tiberius");
        when(transaction.stream(any(), any(), anyRowMapper())).thenReturn(stream);

        sut.stream(transaction, compositeCloseable);

        verify(compositeCloseable).add(stream);
    }

    @Test
    void streamWithTransactionTranslatesExceptions() {
        Database database = database();
        Select<SalespersonRow> sut = database.from(SalespersonRow.class);
        when(transaction.stream(any(), any(), anyRowMapper())).thenThrow(exception);

        calling(() -> sut.stream(transaction))
            .shouldThrow(RuntimeSqlException.class);

        verify(database).translateException(any(), eq(exception));
    }

    @Test
    void streamWithTransactionAndCompositeCloseableTranslatesExceptions() {
        Database database = database();
        Select<SalespersonRow> sut = database.from(SalespersonRow.class);
        when(transaction.stream(any(), any(), anyRowMapper())).thenThrow(exception);

        calling(() -> sut.stream(transaction, compositeCloseable))
            .shouldThrow(RuntimeSqlException.class);

        verify(database).translateException(any(), eq(exception));
    }

    @Test
    void singleWithoutArgsUsesTheDefaultSqlExecutor() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId).where(SalespersonRow::firstName).isEqualTo("James");
        when(defaultSqlExecutor.query(any(), any(), anyRowMapper())).thenReturn(ImmutableList.of(1L));

        sut.single();

        verify(defaultSqlExecutor).query(sqlCaptor.capture(), argsCaptor.capture(), rowMapperCaptor.capture());
        assertThat(sqlCaptor.getValue(), is("select p.SALESPERSON_ID as p_SALESPERSON_ID from SIESTA.SALESPERSON p where p.FIRST_NAME = ?"));
        assertThat(argsCaptor.getValue(), arrayContaining("James"));
        assertThat(rowMapperCaptor.getValue(), isLongMapperFor("p_SALESPERSON_ID"));
    }

    @Test
    void singleWithoutArgsThrowsIfNoDefault() {
        Select<SalespersonRow> sut = databaseWithNoDefaultSqlExecutor().from(SalespersonRow.class);

        calling(supplier(sut::single))
            .shouldThrow(IllegalStateException.class)
            .withMessage("Default SQL executor has not been set.");
    }

    @Test
    void singleWithoutArgsThrowsIfNoResults() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(defaultSqlExecutor.query(any(), any(), anyRowMapper())).thenReturn(ImmutableList.of());

        calling(supplier(sut::single))
            .shouldThrow(NoSuchElementException.class)
            .withMessage("Expected a single element but was empty.");
    }

    @Test
    void singleWithoutArgsReturnsSingleResult() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(defaultSqlExecutor.query(any(), any(), anyRowMapper())).thenReturn(ImmutableList.of(3L));

        Long result = sut.single();

        assertThat(result, is(3L));
    }

    @Test
    void singleWithoutArgsThrowsIfIfMoreThanOneResult() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(defaultSqlExecutor.query(any(), any(), anyRowMapper())).thenReturn(ImmutableList.of(1L, 2L));

        calling(supplier(sut::single))
            .shouldThrow(IllegalArgumentException.class)
            .withMessage("Expected a single element but was <1, 2>.");
    }

    @Test
    void singleWithSqlExecutorUsesThatRatherThanTheDefault() {
        Select<Long> sut = database().from(SalespersonRow.class, "q").select(SalespersonRow::salespersonId).where(SalespersonRow::surname).isEqualTo("Kirk");
        when(sqlExecutor.query(any(), any(), anyRowMapper())).thenReturn(ImmutableList.of(3L));

        sut.single(sqlExecutor);

        verify(sqlExecutor).query(sqlCaptor.capture(), argsCaptor.capture(), rowMapperCaptor.capture());
        verifyNoInteractions(defaultSqlExecutor);
        assertThat(sqlCaptor.getValue(), is("select q.SALESPERSON_ID as q_SALESPERSON_ID from SIESTA.SALESPERSON q where q.SURNAME = ?"));
        assertThat(argsCaptor.getValue(), arrayContaining("Kirk"));
        assertThat(rowMapperCaptor.getValue(), isLongMapperFor("q_SALESPERSON_ID"));
    }

    @Test
    void singleWithSqlExecutorThrowsIfNoResults() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(sqlExecutor.query(any(), any(), anyRowMapper())).thenReturn(ImmutableList.of());

        calling(() -> sut.single(sqlExecutor))
            .shouldThrow(NoSuchElementException.class)
            .withMessage("Expected a single element but was empty.");
    }

    @Test
    void singleWithSqlExecutorReturnsSingleResult() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(sqlExecutor.query(any(), any(), anyRowMapper())).thenReturn(ImmutableList.of(3L));

        Long result = sut.single(sqlExecutor);

        assertThat(result, is(3L));
    }

    @Test
    void singleWithSqlExecutorReturnsSingleResultThrowsIfIfMoreThanOneResult() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(sqlExecutor.query(any(), any(), anyRowMapper())).thenReturn(ImmutableList.of(1L, 2L));

        calling(() -> sut.single(sqlExecutor))
            .shouldThrow(IllegalArgumentException.class)
            .withMessage("Expected a single element but was <1, 2>.");
    }

    @Test
    void singleWithTransactionUsesThatRatherThanDefaultSqlExecutor() {
        Select<Long> sut = database().from(SalespersonRow.class, "r").select(SalespersonRow::salespersonId).where(SalespersonRow::middleNames).isEqualTo("Tiberius");
        when(transaction.query(any(), any(), anyRowMapper())).thenReturn(ImmutableList.of(1L));

        sut.single(transaction);

        verify(transaction).query(sqlCaptor.capture(), argsCaptor.capture(), rowMapperCaptor.capture());
        verifyNoMoreInteractions(defaultSqlExecutor, transaction);
        assertThat(sqlCaptor.getValue(), is("select r.SALESPERSON_ID as r_SALESPERSON_ID from SIESTA.SALESPERSON r where r.MIDDLE_NAMES = ?"));
        assertThat(argsCaptor.getValue(), arrayContaining("Tiberius"));
        assertThat(rowMapperCaptor.getValue(), isLongMapperFor("r_SALESPERSON_ID"));
    }

    @Test
    void singleWithTransactionThrowsIfNoResults() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(transaction.query(any(), any(), anyRowMapper())).thenReturn(ImmutableList.of());

        calling(() -> sut.single(transaction))
            .shouldThrow(NoSuchElementException.class)
            .withMessage("Expected a single element but was empty.");
    }

    @Test
    void singleWithTransactionReturnsSingleResult() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(transaction.query(any(), any(), anyRowMapper())).thenReturn(ImmutableList.of(3L));

        Long result = sut.single(transaction);

        assertThat(result, is(3L));
    }

    @Test
    void singleWithTransactionReturnsSingleResultThrowsIfIfMoreThanOneResult() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(transaction.query(any(), any(), anyRowMapper())).thenReturn(ImmutableList.of(1L, 2L));

        calling(() -> sut.single(transaction))
            .shouldThrow(IllegalArgumentException.class)
            .withMessage("Expected a single element but was <1, 2>.");
    }

    @Test
    void singleAsyncWithoutArgsUsesTransactionFromTheDefaultSqlExecutor() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId).where(SalespersonRow::firstName).isEqualTo("James");
        when(defaultSqlExecutor.beginTransaction()).thenReturn(transaction);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(completedFuture(ImmutableList.of()));

        sut.singleAsync();

        verify(transaction).queryAsync(sqlCaptor.capture(), argsCaptor.capture(), rowMapperCaptor.capture());
        assertThat(sqlCaptor.getValue(), is("select p.SALESPERSON_ID as p_SALESPERSON_ID from SIESTA.SALESPERSON p where p.FIRST_NAME = ?"));
        assertThat(argsCaptor.getValue(), arrayContaining("James"));
        assertThat(rowMapperCaptor.getValue(), isLongMapperFor("p_SALESPERSON_ID"));
    }

    @Test
    void singleAsyncWithoutArgsThrowsIfNoDefault() {
        Select<SalespersonRow> sut = databaseWithNoDefaultSqlExecutor().from(SalespersonRow.class);

        calling(supplier(sut::singleAsync))
            .shouldThrow(IllegalStateException.class)
            .withMessage("Default SQL executor has not been set.");
    }

    @Test
    void singleAsyncWithoutArgsTranslatesExceptions() {
        Database database = database();
        Select<SalespersonRow> sut = database.from(SalespersonRow.class);
        when(defaultSqlExecutor.beginTransaction()).thenReturn(transaction);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(failedFuture(exception));

        calling(() -> sut.singleAsync().join())
            .shouldThrow(CompletionException.class)
            .withCause(RuntimeSqlException.class);

        verify(database).translateException(any(), eq(exception));
    }

    @Test
    void singleAsyncWithoutArgsThrowsIfNoResults() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(defaultSqlExecutor.beginTransaction()).thenReturn(transaction);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(completedFuture(ImmutableList.of()));

        calling(() -> sut.singleAsync().join())
            .shouldThrow(CompletionException.class)
            .withCause(NoSuchElementException.class)
            .withMessage("Expected a single element but was empty.");
    }

    @Test
    void singleAsyncWithoutArgsReturnsSingleResult() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(defaultSqlExecutor.beginTransaction()).thenReturn(transaction);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(completedFuture(ImmutableList.of(3L)));

        Long result = sut.singleAsync().join();

        assertThat(result, is(3L));
    }

    @Test
    void singleAsyncWithoutArgsThrowsIfIfMoreThanOneResult() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(defaultSqlExecutor.beginTransaction()).thenReturn(transaction);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(completedFuture(ImmutableList.of(1L, 2L)));

        calling(() -> sut.singleAsync().join())
            .shouldThrow(CompletionException.class)
            .withCause(IllegalArgumentException.class)
            .withMessage("Expected a single element but was <1, 2>.");
    }

    @Test
    void singleAsyncWithSqlExecutorUsesTransactionFromThatExecutor() {
        Select<Long> sut = database().from(SalespersonRow.class, "q").select(SalespersonRow::salespersonId).where(SalespersonRow::surname).isEqualTo("Kirk");
        when(sqlExecutor.beginTransaction()).thenReturn(transaction);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(completedFuture(ImmutableList.of()));

        sut.singleAsync(sqlExecutor);

        verify(transaction).queryAsync(sqlCaptor.capture(), argsCaptor.capture(), rowMapperCaptor.capture());
        verifyNoInteractions(defaultSqlExecutor);
        assertThat(sqlCaptor.getValue(), is("select q.SALESPERSON_ID as q_SALESPERSON_ID from SIESTA.SALESPERSON q where q.SURNAME = ?"));
        assertThat(argsCaptor.getValue(), arrayContaining("Kirk"));
        assertThat(rowMapperCaptor.getValue(), isLongMapperFor("q_SALESPERSON_ID"));
    }

    @Test
    void singleAsyncWithSqlExecutorTranslatesExceptions() {
        Database database = database();
        Select<SalespersonRow> sut = database.from(SalespersonRow.class);
        when(sqlExecutor.beginTransaction()).thenReturn(transaction);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(failedFuture(exception));

        calling(() -> sut.singleAsync(sqlExecutor).join())
            .shouldThrow(CompletionException.class)
            .withCause(RuntimeSqlException.class);

        verify(database).translateException(any(), eq(exception));
    }

    @Test
    void singleAsyncWithSqlExecutorThrowsIfNoResults() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(sqlExecutor.beginTransaction()).thenReturn(transaction);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(completedFuture(ImmutableList.of()));

        calling(() -> sut.singleAsync(sqlExecutor).join())
            .shouldThrow(CompletionException.class)
            .withCause(NoSuchElementException.class)
            .withMessage("Expected a single element but was empty.");
    }

    @Test
    void singleAsyncWithSqlExecutorReturnsSingleResult() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(sqlExecutor.beginTransaction()).thenReturn(transaction);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(completedFuture(ImmutableList.of(3L)));

        Long result = sut.singleAsync(sqlExecutor).join();

        assertThat(result, is(3L));
    }

    @Test
    void singleAsyncWithSqlExecutorThrowsIfIfMoreThanOneResult() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(sqlExecutor.beginTransaction()).thenReturn(transaction);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(completedFuture(ImmutableList.of(1L, 2L)));

        calling(() -> sut.singleAsync(sqlExecutor).join())
            .shouldThrow(CompletionException.class)
            .withCause(IllegalArgumentException.class)
            .withMessage("Expected a single element but was <1, 2>.");
    }

    @Test
    void singleAsyncWithTransactionUsesThatAndNotTheDefaultSqlExecutor() {
        Select<Long> sut = database().from(SalespersonRow.class, "r").select(SalespersonRow::salespersonId).where(SalespersonRow::middleNames).isEqualTo("Tiberius");
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(completedFuture(ImmutableList.of()));

        sut.singleAsync(transaction);

        verify(transaction).queryAsync(sqlCaptor.capture(), argsCaptor.capture(), rowMapperCaptor.capture());
        verifyNoMoreInteractions(defaultSqlExecutor, transaction);
        assertThat(sqlCaptor.getValue(), is("select r.SALESPERSON_ID as r_SALESPERSON_ID from SIESTA.SALESPERSON r where r.MIDDLE_NAMES = ?"));
        assertThat(argsCaptor.getValue(), arrayContaining("Tiberius"));
        assertThat(rowMapperCaptor.getValue(), isLongMapperFor("r_SALESPERSON_ID"));
    }

    @Test
    void singleAsyncWithTransactionTranslatesExceptions() {
        Database database = database();
        Select<SalespersonRow> sut = database.from(SalespersonRow.class);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(failedFuture(exception));

        calling(() -> sut.singleAsync(transaction).join())
            .shouldThrow(CompletionException.class)
            .withCause(RuntimeSqlException.class);

        verify(database).translateException(any(), eq(exception));
    }

    @Test
    void singleAsyncWithTransactionThrows() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(completedFuture(ImmutableList.of()));

        calling(() -> sut.singleAsync(transaction).join())
            .shouldThrow(CompletionException.class)
            .withCause(NoSuchElementException.class)
            .withMessage("Expected a single element but was empty.");
    }

    @Test
    void singleAsyncWithTransactionReturnsSingleResult() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(completedFuture(ImmutableList.of(3L)));

        Long result = sut.singleAsync(transaction).join();

        assertThat(result, is(3L));
    }

    @Test
    void singleAsyncWithTransactionThrowsIfIfMoreThanOneResult() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        when(transaction.queryAsync(any(), any(), anyRowMapper())).thenReturn(completedFuture(ImmutableList.of(1L, 2L)));

        calling(() -> sut.singleAsync(transaction).join())
            .shouldThrow(CompletionException.class)
            .withCause(IllegalArgumentException.class)
            .withMessage("Expected a single element but was <1, 2>.");
    }

    @Test
    void fetchFirstModifiesSql() {
        Select<Long> sut = db2Database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);
        long rows = nextLong(100, 200);

        sut.fetchFirst(rows);

        assertThat(sut.sql(), is("select p.SALESPERSON_ID as p_SALESPERSON_ID from SIESTA.SALESPERSON p fetch first " + rows + " rows only"));
    }

    @Test
    void pageProducesCorrectOffsetAndOrder() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);

        sut.page(3, 15, Ordering.of("SALESPERSON_ID", Order.DESC));

        assertThat(sut.sql(), is("select p.SALESPERSON_ID as p_SALESPERSON_ID from SIESTA.SALESPERSON p order by p.SALESPERSON_ID desc offset 30 rows fetch next 15 rows only"));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"UNSPECIFIED", ""})
    @TestCase({"UNCOMMITTED_READ", " with ur"})
    @TestCase({"READ_COMMITTED", " with cs"})
    @TestCase({"REPEATABLE_READ", " with rs"})
    @TestCase({"SERIALIZABLE", " with rr"})
    void withIsolationModifiesSql(IsolationLevel isolation, String isolationSql) {
        Select<Long> sut = db2Database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);

        sut.withIsolation(isolation);

        assertThat(sut.sql(), is("select p.SALESPERSON_ID as p_SALESPERSON_ID from SIESTA.SALESPERSON p" + isolationSql));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"SHARE", " for read only with rs use and keep SHARE locks"})
    @TestCase({"UPDATE", " for read only with rs use and keep UPDATE locks"})
    @TestCase({"EXCLUSIVE", " for read only with rs use and keep EXCLUSIVE locks"})
    void keepLocksModifiesTheSql(LockLevel level, String lockSql) {
        Select<Long> sut = db2Database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);

        sut.keepLocks(level);

        assertThat(sut.sql(), is("select p.SALESPERSON_ID as p_SALESPERSON_ID from SIESTA.SALESPERSON p" + lockSql));
    }

    @Test
    void typeReturnsTypeOfFColumnForSingleColumnSelect() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);

        TypeToken<Long> result = sut.type();

        assertThat(result, is(TypeToken.of(long.class)));
    }

    @Test
    void typeReturnsTypeOfRowForFullRowSelect() {
        Select<SalespersonRow> sut = database().from(SalespersonRow.class, "p");

        TypeToken<SalespersonRow> result = sut.type();

        assertThat(result, is(TypeToken.of(SalespersonRow.class)));
    }

    @Test
    void typeReturnsTypeOfTupleForMultipleColumnSelect() {
        Select<Tuple3<String,BigDecimal,Long>> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::firstName).comma(SalespersonRow::commission).comma(SalespersonRow::salespersonId);

        TypeToken<Tuple3<String,BigDecimal,Long>> result = sut.type();

        assertThat(result, is(new TypeToken<Tuple3<String,BigDecimal,Long>>() {}));
    }

    @Test
    void typeReturnsTypeOfTupleForJoin() {
        Select<Tuple2<SalespersonRow,SalesAreaRow>> sut = database().from(SalespersonRow.class, "p").join(SalesAreaRow.class, "a").on(SalesAreaRow::salespersonId).isEqualTo(SalespersonRow::salespersonId);

        TypeToken<Tuple2<SalespersonRow,SalesAreaRow>> result = sut.type();

        assertThat(result, is(new TypeToken<Tuple2<SalespersonRow,SalesAreaRow>>() {}));
    }

    @Test
    void typeReturnsTypeOfTupleForMultipleTableJoin() {
        Select<Tuple3<SalespersonRow,SalesAreaRow,SalesAreaRow>> sut = database().from(SalespersonRow.class, "p")
            .join(SalesAreaRow.class, "a").on(SalesAreaRow::salespersonId).isEqualTo(SalespersonRow::salespersonId)
            .join(SalesAreaRow.class, "a2").on(SalesAreaRow::salespersonId).isEqualTo(SalespersonRow::salespersonId);

        TypeToken<Tuple3<SalespersonRow,SalesAreaRow,SalesAreaRow>> result = sut.type();

        assertThat(result, is(new TypeToken<Tuple3<SalespersonRow,SalesAreaRow,SalesAreaRow>>() {}));
    }

    @Test
    void sqlReturnsSqlWithoutParentheses() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);

        String result = sut.sql();

        assertThat(result, is("select p.SALESPERSON_ID as p_SALESPERSON_ID from SIESTA.SALESPERSON p"));
    }

    @Test
    void sqlWithScopeIsParenthesized() {
        Database database = database();
        Select<Long> sut = database.from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);

        String result = sut.sql(new Scope(database));

        assertThat(result, is("(select p.SALESPERSON_ID as p_SALESPERSON_ID from SIESTA.SALESPERSON p)"));
    }

    @Test
    void labelIsSelect() {
        Database database = database();
        Select<Long> sut = database.from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);

        String result = sut.label(new Scope(database));

        assertThat(result, is("select_1"));
    }

    @Test
    void precedenceIsSelect() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);

        Precedence result = sut.precedence();

        assertThat(result, is(Precedence.SELECT));
    }

    @Test
    void rowMapperWithLabelCreatesMapperForThatColumn() {
        Database database = database();
        Select<Long> sut = database.from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);

        RowMapper<Long> result = sut.rowMapperFactory(new Scope(database), Optional.of("FOO")).rowMapper(Optional.empty());

        assertThat(result, isLongMapperFor("FOO"));
    }

    @Test
    void rowMapperWithoutLabelCreatesMapperForDefaultLabel() {
        Database database = database();
        Select<Long> sut = database.from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);

        RowMapper<Long> result = sut.rowMapperFactory(new Scope(database), Optional.empty()).rowMapper(Optional.empty());

        assertThat(result, isLongMapperFor("select_1"));
    }

    @Test
    void argsReturnsStatementArgs() {
        Database database = database();
        Select<Long> sut = database.from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId).where(SalespersonRow::firstName).isEqualTo("Joe");

        Stream<Object> result = sut.args(new Scope(database));

        assertThat(result, StreamMatchers.contains("Joe"));
    }

    @Test
    void scopeAlwaysReturnsTheSameResult() {
        Select<Long> sut = database().from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);

        Scope result1 = sut.scope();
        Scope result2 = sut.scope();

        assertThat(result1, sameInstance(result2));
    }

    @Test
    void scopeCanResolveAlias() {
        Database database = database();
        Select<Long> sut = database.from(SalespersonRow.class, "p").select(SalespersonRow::salespersonId);

        Scope result = sut.scope();

        assertThat(result.findAlias(SalespersonRow.class), is(database.table(SalespersonRow.class).as("p")));
    }

    @Test
    void databaseReturnsTheDatabase() {
        Database database = database();
        Select<SalespersonRow> sut = database.from(SalespersonRow.class);

        Database result = sut.database();

        assertThat(result, sameInstance(database));
    }

    @Test
    void fromOnTableProducesSelect() {
        Database database = database();

        Select<Integer> result = Select.from(database, database.table(SalespersonRow.class)).select(literal(1));

        assertThat(result.sql(), is("select 1 as literal_1 from SIESTA.SALESPERSON SALESPERSON"));
    }

    @Test
    void fromOnAliasProducesSelect() {
        Database database = database();

        Select<Integer> result = Select.from(database, database.table(SalespersonRow.class).as("p")).select(literal(1));

        assertThat(result.sql(), is("select 1 as literal_1 from SIESTA.SALESPERSON p"));
    }

    @Test
    void fromOnCteProducesSelect() {
        Database database = database();
        CommonTableExpression<Long> commonTableExpression = database.with("cte").as(database.select(literal(1L), "col"));

        Select<Long> result = Select.from(database, commonTableExpression);

        assertThat(result.sql(), is("with cte(col) as (select 1 as col from DUAL) select cte.VALUE as cte_VALUE from cte"));
    }

    @Test
    void fromOnCteWithAliasProducesSelect() {
        Database database = database();
        CommonTableExpression<Long> commonTableExpression = database.with("cte").as(database.select(literal(1L), "col"));

        Select<Long> result = Select.from(database, commonTableExpression, "bob");

        assertThat(result.sql(), is("with cte(col) as (select 1 as col from DUAL) select bob.VALUE as bob_VALUE from cte bob"));
    }

    private Database database() {
        return Mockito.spy(TestDatabase.testDatabaseBuilder()
            .dialect(new AnsiDialect())
            .defaultSqlExecutor(defaultSqlExecutor)
            .build());
    }

    private Database db2Database() {
        return Mockito.spy(TestDatabase.testDatabaseBuilder()
            .dialect(new Db2Dialect())
            .defaultSqlExecutor(defaultSqlExecutor)
            .build());
    }

    private Database databaseWithNoDefaultSqlExecutor() {
        return TestDatabase.testDatabaseBuilder()
            .dialect(new AnsiDialect())
            .build();
    }

    private RowMapper<Long> anyRowMapper() {
        return any();
    }

    private static <U> CompletableFuture<U> failedFuture(Throwable ex) {
        CompletableFuture<U> result = new CompletableFuture<>();
        result.completeExceptionally(ex);
        return result;
    }

    private Matcher<RowMapper<Long>> isLongMapperFor(String column) {
        return new TypeSafeMatcher<RowMapper<Long>>() {
            @Override
            protected boolean matchesSafely(RowMapper<Long> item) {
                try {
                    ResultSet rs = mock(ResultSet.class);
                    long randomLong = nextLong();
                    when(rs.getLong(column)).thenReturn(randomLong);
                    Long result = item.mapRow(rs);
                    verify(rs).getLong(column);
                    assertThat(result, is(randomLong));
                    return true;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a RowMapper that maps column " + column + " to a long.");
            }
        };
    }
}
