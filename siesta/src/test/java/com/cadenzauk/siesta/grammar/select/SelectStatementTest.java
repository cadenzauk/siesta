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

package com.cadenzauk.siesta.grammar.select;

import com.cadenzauk.core.MockitoTest;
import com.cadenzauk.core.junit.TestCase;
import com.cadenzauk.core.junit.TestCaseArgumentsProvider;
import com.cadenzauk.core.lang.CompositeAutoCloseable;
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.From;
import com.cadenzauk.siesta.IsolationLevel;
import com.cadenzauk.siesta.LockLevel;
import com.cadenzauk.siesta.Order;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.SqlExecutor;
import com.cadenzauk.siesta.Transaction;
import com.cadenzauk.siesta.dialect.Db2Dialect;
import com.cadenzauk.siesta.grammar.expression.BooleanExpression;
import com.cadenzauk.siesta.grammar.expression.Precedence;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.model.WidgetRow;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.stream.Stream;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static com.cadenzauk.core.util.OptionalUtil.with;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SelectStatementTest extends MockitoTest {
    @Mock
    private From from;

    @Mock
    private RowMapper<Integer> rowMapper;

    @Mock
    private Projection projection;

    @Mock
    private CommonTableExpression<?> cte;

    @Mock
    private TypedExpression<? extends Object> typedExpression1;

    @Mock
    private TypedExpression<? extends Object> typedExpression2;

    @Mock
    private SelectStatement<Integer> union;

    @Mock
    private SqlExecutor sqlExecutor;

    @Mock
    private Transaction transaction;

    @Mock
    private BooleanExpression booleanExpression1;

    @Mock
    private BooleanExpression booleanExpression2;

    @Mock
    private CompositeAutoCloseable autoCloseable;

    @Test
    void rowType() {
        TypeToken<Integer> typeToken = TypeToken.of(Integer.class);
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), typeToken, from, rowMapper, projection);

        TypeToken<Integer> result = sut.rowType();

        assertThat(result, is(typeToken));
    }

    @Test
    void commonTableExpressions() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);

        sut.addCommonTableExpression(cte);
        Stream<CommonTableExpression<?>> result = sut.commonTableExpressions();

        assertThat(result.collect(toList()), contains(cte));
    }

    @Test
    void commonTableExpressionsSql() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("*");
        when(from.sql(any())).thenReturn(" from foo");
        when(cte.sql(any())).thenReturn("(cte query)");

        sut.addCommonTableExpression(cte);

        String result = sut.sql(createScope());

        assertThat(result, is("(with (cte query)select * from foo)"));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"UNION", "union"})
    @TestCase({"UNION_ALL", "union all"})
    void addUnion(UnionType unionType, String unionSql) {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("*");
        when(from.sql(any())).thenReturn(" from foo");
        when(union.sqlImpl(any())).thenReturn("select * from bar");

        sut.addUnion(union, unionType);

        String sql = sut.sql();
        assertThat(sql, is("select * from foo " + unionSql + " select * from bar"));
    }

    @Test
    void label() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);

        String result = sut.label(sut.scope());

        assertThat(result, is("select_1"));
    }

    @Test
    void args() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.args(any())).thenReturn(Stream.of("ABC"));
        when(from.args(any())).thenReturn(Stream.of(1, 2.4));

        Stream<Object> result = sut.args(sut.scope());

        assertThat(result.toArray(), arrayContaining("ABC", 1, 2.4));
    }

    @Test
    void cteArgs() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.args(any())).thenReturn(Stream.of("ABC"));
        when(from.args(any())).thenReturn(Stream.of(1, 2.4));
        when(cte.args(any())).thenReturn(Stream.of("Foo", "Bar"));
        sut.addCommonTableExpression(cte);

        Stream<Object> result = sut.args(sut.scope());

        assertThat(result.toArray(), arrayContaining("Foo", "Bar", "ABC", 1, 2.4));
    }

    @Test
    void cteArgsInnerScope() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.args(any())).thenReturn(Stream.of("ABC"));
        when(from.args(any())).thenReturn(Stream.of(1, 2.4));
        sut.addCommonTableExpression(cte);

        Stream<Object> result = sut.args(sut.scope().plus(sut.scope.database().table(WidgetRow.class).as("bob")));

        assertThat(result.toArray(), arrayContaining("ABC", 1, 2.4));
        verify(cte, never()).args(any());
    }

    @Test
    void projection() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);

        Projection result = sut.projection();

        assertThat(result, sameInstance(projection));
    }

    @Test
    void fetchFirst() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("*");
        when(from.sql(any())).thenReturn(" from bob");

        sut.fetchFirst(4L);

        assertThat(sut.sql(), is("select * from bob fetch first 4 rows only"));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"UNSPECIFIED", ""})
    @TestCase({"UNCOMMITTED_READ", " with ur"})
    @TestCase({"READ_COMMITTED", " with cs"})
    @TestCase({"REPEATABLE_READ", " with rs"})
    @TestCase({"SERIALIZABLE", " with rr"})
    void withIsolation(IsolationLevel isolation, String isolationSql) {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);

        sut.withIsolation(isolation);

        when(projection.sql(any())).thenReturn("col1");
        when(from.sql(any())).thenReturn(" from tab1");
        assertThat(sut.sql(), is("select col1 from tab1" + isolationSql));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"SHARE", " for read only with rs use and keep SHARE locks"})
    @TestCase({"UPDATE", " for read only with rs use and keep UPDATE locks"})
    @TestCase({"EXCLUSIVE", " for read only with rs use and keep EXCLUSIVE locks"})
    void keepLocks(LockLevel level, String lockSql) {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);

        sut.keepLocks(level);

        when(projection.sql(any())).thenReturn("col1");
        when(from.sql(any())).thenReturn(" from tab1");
        assertThat(sut.sql(), is("select col1 from tab1" + lockSql));
    }

    @Test
    void listSqlExecutor() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("columnlist");
        when(from.sql(any())).thenReturn(" from table");
        when(sqlExecutor.query(eq("select columnlist from table"), any(), eq(rowMapper))).thenReturn(ImmutableList.of(1, 2, 3));

        List<Integer> list = sut.list(sqlExecutor);

        assertThat(list, contains(1, 2, 3));
    }

    @Test
    void listTransaction() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("columnlist");
        when(from.sql(any())).thenReturn(" from table");
        when(transaction.query(eq("select columnlist from table"), any(), eq(rowMapper))).thenReturn(ImmutableList.of(1, 2, 3));

        List<Integer> list = sut.list(transaction);

        assertThat(list, contains(1, 2, 3));
    }

    @Test
    void listAsyncSqlExecutor() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("columnlist");
        when(from.sql(any())).thenReturn(" from table");
        when(sqlExecutor.beginTransaction()).thenReturn(transaction);
        when(transaction.queryAsync(eq("select columnlist from table"), any(), eq(rowMapper))).thenReturn(completedFuture(ImmutableList.of(1, 2, 3)));

        List<Integer> list = sut.listAsync(sqlExecutor).join();

        assertThat(list, contains(1, 2, 3));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({""})
    @TestCase({"1"})
    @TestCase({"1,2"})
    void listAsyncTransaction(List<Integer> expected) {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("columnlist");
        when(from.sql(any())).thenReturn(" from table");
        when(transaction.queryAsync(eq("select columnlist from table"), any(), eq(rowMapper))).thenReturn(completedFuture(expected));

        List<Integer> list = sut.listAsync(transaction).join();

        assertThat(list, is(expected));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase("")
    @TestCase("1")
    @TestCase({"1,2", "java.lang.IllegalArgumentException", "expected one element but was: <1, 2>"})
    void optionalSqlExecutor(List<Integer> result, Optional<Class<Exception>> expectedException, String exceptionMessage) {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("columnlist");
        when(from.sql(any())).thenReturn(" from table");
        when(sqlExecutor.query(eq("select columnlist from table"), any(), eq(rowMapper))).thenReturn(result);

        with(expectedException)
            .ifPresent(e ->
                calling(() -> sut.optional(sqlExecutor))
                    .shouldThrow(e)
                    .withMessage(exceptionMessage))
            .otherwise(() -> {
                Optional<Integer> optional = sut.optional(sqlExecutor);

                assertThat(optional, is(result.stream().findFirst()));
            });
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase("")
    @TestCase("1")
    @TestCase({"1,2", "java.lang.IllegalArgumentException", "expected one element but was: <1, 2>"})
    void optionalTransaction(List<Integer> result, Optional<Class<Exception>> expectedException, String exceptionMessage) {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("columnlist");
        when(from.sql(any())).thenReturn(" from table");
        when(transaction.query(eq("select columnlist from table"), any(), eq(rowMapper))).thenReturn(result);

        with(expectedException)
            .ifPresent(e ->
                calling(() -> sut.optional(transaction))
                    .shouldThrow(e)
                    .withMessage(exceptionMessage))
            .otherwise(() -> {
                Optional<Integer> optional = sut.optional(transaction);

                assertThat(optional, is(result.stream().findFirst()));
            });
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase("")
    @TestCase("1")
    @TestCase({"1,2", "java.lang.IllegalArgumentException", "expected one element but was: <1, 2>"})
    void optionalAsyncSqlExecutor(List<Integer> result, Optional<Class<Exception>> expectedException, String exceptionMessage) {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("columnlist");
        when(from.sql(any())).thenReturn(" from table");
        when(sqlExecutor.beginTransaction()).thenReturn(transaction);
        when(transaction.queryAsync(eq("select columnlist from table"), any(), eq(rowMapper))).thenReturn(completedFuture(result));

        with(expectedException)
            .ifPresent(e ->
                calling(() -> sut.optionalAsync(sqlExecutor).join())
                    .shouldThrow(CompletionException.class)
                    .withCause(e)
                    .withMessage(exceptionMessage))
            .otherwise(() -> {
                Optional<Integer> optional = sut.optionalAsync(sqlExecutor).join();

                assertThat(optional, is(result.stream().findFirst()));
            });
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({""})
    @TestCase({"1"})
    @TestCase({"1,2", "java.lang.IllegalArgumentException", "expected one element but was: <1, 2>"})
    void optionalAsyncTransaction(List<Integer> result, Optional<Class<Exception>> expectedException, String exceptionMessage) {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("columnlist");
        when(from.sql(any())).thenReturn(" from table");
        when(transaction.queryAsync(eq("select columnlist from table"), any(), eq(rowMapper))).thenReturn(completedFuture(result));

        with(expectedException)
            .ifPresent(e ->
                calling(() -> sut.optionalAsync(transaction).join())
                    .shouldThrow(CompletionException.class)
                    .withCause(e)
                    .withMessage(exceptionMessage))
            .otherwise(() -> {
                Optional<Integer> optional = sut.optionalAsync(transaction).join();

                assertThat(optional, is(result.stream().findFirst()));
            });
    }

    @Test
    void streamSqlExecutor() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("columnlist");
        when(from.sql(any())).thenReturn(" from table");
        when(sqlExecutor.stream(eq("select columnlist from table"), any(), eq(rowMapper))).thenReturn(Stream.of(1, 2, 3));

        Stream<Integer> stream = sut.stream(sqlExecutor);

        assertThat(stream.collect(toList()), contains(1, 2, 3));
    }

    @Test
    void streamTransaction() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("columnlist");
        when(from.sql(any())).thenReturn(" from table");
        when(transaction.stream(eq("select columnlist from table"), any(), eq(rowMapper))).thenReturn(Stream.of(1, 2, 3));

        Stream<Integer> stream = sut.stream(transaction);

        assertThat(stream.collect(toList()), contains(1, 2, 3));
    }

    @Test
    void streamSqlExecutorAndAutoCloseable() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("columnlist");
        when(from.sql(any())).thenReturn(" from table");
        Stream<Integer> stream = Stream.of(1, 2, 3);
        when(autoCloseable.add(stream)).thenReturn(stream);
        when(sqlExecutor.stream(eq("select columnlist from table"), any(), eq(rowMapper))).thenReturn(stream);

        Stream<Integer> result = sut.stream(sqlExecutor, autoCloseable);

        assertThat(result.collect(toList()), contains(1, 2, 3));
        verify(autoCloseable).add(stream);
    }

    @Test
    void streamTransactionAndAutoCloseable() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("columnlist");
        when(from.sql(any())).thenReturn(" from table");
        Stream<Integer> stream = Stream.of(1, 2, 3);
        when(autoCloseable.add(stream)).thenReturn(stream);
        when(transaction.stream(eq("select columnlist from table"), any(), eq(rowMapper))).thenReturn(stream);

        Stream<Integer> result = sut.stream(transaction, autoCloseable);

        assertThat(result.collect(toList()), contains(1, 2, 3));
        verify(autoCloseable).add(stream);
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"", "java.util.NoSuchElementException", "Expected a single element but was empty."})
    @TestCase("1")
    @TestCase({"1,2", "java.lang.IllegalArgumentException", "Expected a single element but was <1, 2>."})
    @TestCase({"1,2,3,4,5", "java.lang.IllegalArgumentException", "Expected a single element but was <1, 2, 3, ...>."})
    void singleSqlExecutor(List<Integer> result, Optional<Class<Exception>> expectedException, String exceptionMessage) {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("columnlist");
        when(from.sql(any())).thenReturn(" from table");
        when(sqlExecutor.query(eq("select columnlist from table"), any(), eq(rowMapper))).thenReturn(result);

        with(expectedException)
            .ifPresent(e ->
                calling(() -> sut.single(sqlExecutor))
                    .shouldThrow(e)
                    .withMessage(exceptionMessage))
            .otherwise(() -> {
                Integer single = sut.single(sqlExecutor);

                assertThat(single, is(result.get(0)));
            });
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"", "java.util.NoSuchElementException", "Expected a single element but was empty."})
    @TestCase("1")
    @TestCase({"1,2", "java.lang.IllegalArgumentException", "Expected a single element but was <1, 2>."})
    @TestCase({"1,2,3,4,5", "java.lang.IllegalArgumentException", "Expected a single element but was <1, 2, 3, ...>."})
    void singleTransaction(List<Integer> result, Optional<Class<Exception>> expectedException, String exceptionMessage) {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("columnlist");
        when(from.sql(any())).thenReturn(" from table");
        when(transaction.query(eq("select columnlist from table"), any(), eq(rowMapper))).thenReturn(result);

        with(expectedException)
            .ifPresent(e ->
                calling(() -> sut.single(transaction))
                    .shouldThrow(e)
                    .withMessage(exceptionMessage))
            .otherwise(() -> {
                Integer single = sut.single(transaction);

                assertThat(single, is(result.get(0)));
            });
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"", "java.util.NoSuchElementException", "Expected a single element but was empty."})
    @TestCase("1")
    @TestCase({"1,2", "java.lang.IllegalArgumentException", "Expected a single element but was <1, 2>."})
    @TestCase({"1,2,3,4,5", "java.lang.IllegalArgumentException", "Expected a single element but was <1, 2, 3, ...>."})
    void singleAsyncSqlExecutor(List<Integer> result, Optional<Class<Exception>> expectedException, String exceptionMessage) {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("columnlist");
        when(from.sql(any())).thenReturn(" from table");
        when(sqlExecutor.beginTransaction()).thenReturn(transaction);
        when(transaction.queryAsync(eq("select columnlist from table"), any(), eq(rowMapper))).thenReturn(completedFuture(result));

        with(expectedException)
            .ifPresent(e ->
                calling(() -> sut.singleAsync(sqlExecutor).join())
                    .shouldThrow(CompletionException.class)
                    .withCause(e)
                    .withMessage(exceptionMessage))
            .otherwise(() -> {
                Integer single = sut.singleAsync(sqlExecutor).join();

                assertThat(single, is(result.get(0)));
            });
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"", "java.util.NoSuchElementException", "Expected a single element but was empty."})
    @TestCase("1")
    @TestCase({"1,2", "java.lang.IllegalArgumentException", "Expected a single element but was <1, 2>."})
    @TestCase({"1,2,3,4,5", "java.lang.IllegalArgumentException", "Expected a single element but was <1, 2, 3, ...>."})
    void singleAsyncTransaction(List<Integer> result, Optional<Class<Exception>> expectedException, String exceptionMessage) {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("columnlist");
        when(from.sql(any())).thenReturn(" from table");
        when(transaction.queryAsync(eq("select columnlist from table"), any(), eq(rowMapper))).thenReturn(completedFuture(result));

        with(expectedException)
            .ifPresent(e ->
                calling(() -> sut.singleAsync(transaction).join())
                    .shouldThrow(CompletionException.class)
                    .withCause(e)
                    .withMessage(exceptionMessage))
            .otherwise(() -> {
                Integer single = sut.singleAsync(transaction).join();

                assertThat(single, is(result.get(0)));
            });
    }

    @Test
    void from() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);

        From result = sut.from();

        assertThat(result, sameInstance(from));
    }

    @Test
    void scope() {
        Scope scope = createScope();
        SelectStatement<Integer> sut = new SelectStatement<>(scope, TypeToken.of(Integer.class), from, rowMapper, projection);

        Scope result = sut.scope();

        assertThat(result, sameInstance(scope));
    }

    @Test
    void sql() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("*");
        when(from.sql(any())).thenReturn(" from foo");
        when(booleanExpression1.sql(any())).thenReturn("1 = 2");
        when(booleanExpression1.precedence()).thenReturn(Precedence.COMPARISON);
        sut.setWhereClause(booleanExpression1);
        sut.addGroupBy(TypedExpression.literal("ABC"));
        sut.addOrderBy(1, Order.DESC);

        String result = sut.sql();

        assertThat(result, is("select * from foo where 1 = 2 group by 'ABC' order by 1 desc"));
    }

    @Test
    void sqlWithOuterScope() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("*");
        when(from.sql(any())).thenReturn(" from foo");
        when(booleanExpression1.sql(any())).thenReturn("1 = 2");
        when(booleanExpression1.precedence()).thenReturn(Precedence.COMPARISON);
        sut.setWhereClause(booleanExpression1);
        sut.addGroupBy(TypedExpression.literal("ABC"));
        sut.addOrderBy(1, Order.DESC);

        String result = sut.sql(createScope());

        assertThat(result, is("(select * from foo where 1 = 2 group by 'ABC' order by 1 desc)"));
    }

    @Test
    void rowMapper() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);

        RowMapper<Integer> result = sut.rowMapper();

        assertThat(result, sameInstance(rowMapper));
    }

    @Test
    void addGroupBy1() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("*");
        when(from.sql(any())).thenReturn(" from foo");
        when(typedExpression1.sql(any())).thenReturn("COL1");

        sut.addGroupBy(typedExpression1);

        assertThat(sut.sql(), is("select * from foo group by COL1"));
    }

    @Test
    void addGroupBy2() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("*");
        when(from.sql(any())).thenReturn(" from foo");
        when(typedExpression1.sql(any())).thenReturn("COL1");
        when(typedExpression2.sql(any())).thenReturn("COL2");

        sut.addGroupBy(typedExpression1);
        sut.addGroupBy(typedExpression2);

        assertThat(sut.sql(), is("select * from foo group by COL1, COL2"));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"ASC", "select * from foo order by COL1 asc"})
    @TestCase({"DESC", "select * from foo order by COL1 desc"})
    void addOrderByExpression(Order order, String expectedSql) {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("*");
        when(from.sql(any())).thenReturn(" from foo");
        when(typedExpression1.sql(any())).thenReturn("COL1");

        sut.addOrderBy(typedExpression1, order);

        assertThat(sut.sql(), is(expectedSql));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"ASC", "ASC", "select * from foo order by COL1 asc, COL2 asc"})
    @TestCase({"ASC", "DESC", "select * from foo order by COL1 asc, COL2 desc"})
    @TestCase({"DESC", "ASC", "select * from foo order by COL1 desc, COL2 asc"})
    @TestCase({"DESC", "DESC", "select * from foo order by COL1 desc, COL2 desc"})
    void addOrderByExpressions(Order order1, Order order2, String expectedSql) {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("*");
        when(from.sql(any())).thenReturn(" from foo");
        when(typedExpression1.sql(any())).thenReturn("COL1");
        when(typedExpression2.sql(any())).thenReturn("COL2");

        sut.addOrderBy(typedExpression1, order1);
        sut.addOrderBy(typedExpression2, order2);

        assertThat(sut.sql(), is(expectedSql));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"1", "ASC", "select * from foo order by 1 asc"})
    @TestCase({"2", "DESC", "select * from foo order by 2 desc"})
    void addOrderByInt(int col1, Order order1, String expectedSql) {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("*");
        when(from.sql(any())).thenReturn(" from foo");

        sut.addOrderBy(col1, order1);

        assertThat(sut.sql(), is(expectedSql));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @TestCase({"1", "ASC", "ASC", "select * from foo order by 1 asc, COL2 asc"})
    @TestCase({"2", "ASC", "DESC", "select * from foo order by 2 asc, COL2 desc"})
    @TestCase({"3", "DESC", "ASC", "select * from foo order by 3 desc, COL2 asc"})
    void addOrderByIntAndCol(int col1, Order order1, Order order2, String expectedSql) {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("*");
        when(from.sql(any())).thenReturn(" from foo");
        when(typedExpression2.sql(any())).thenReturn("COL2");

        sut.addOrderBy(col1, order1);
        sut.addOrderBy(typedExpression2, order2);

        assertThat(sut.sql(), is(expectedSql));
    }

    @Test
    void setWhereClause() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("*");
        when(from.sql(any())).thenReturn(" from foo");
        when(booleanExpression1.sql(any())).thenReturn("a = b");
        when(booleanExpression1.precedence()).thenReturn(Precedence.COMPARISON);

        sut.setWhereClause(booleanExpression1);

        assertThat(sut.sql(), is("select * from foo where a = b"));
    }

    @Test
    void andWhere() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("*");
        when(from.sql(any())).thenReturn(" from foo");
        when(booleanExpression1.sql(any())).thenReturn("a = b");
        when(booleanExpression1.precedence()).thenReturn(Precedence.COMPARISON);
        when(booleanExpression2.sql(any())).thenReturn("c = d");
        when(booleanExpression2.precedence()).thenReturn(Precedence.COMPARISON);
        sut.setWhereClause(booleanExpression1);

        sut.andWhere(booleanExpression2);

        assertThat(sut.sql(), is("select * from foo where a = b and c = d"));
    }

    @Test
    void orWhere() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("*");
        when(from.sql(any())).thenReturn(" from foo");
        when(booleanExpression1.sql(any())).thenReturn("a = b");
        when(booleanExpression1.precedence()).thenReturn(Precedence.COMPARISON);
        when(booleanExpression2.sql(any())).thenReturn("c = d");
        when(booleanExpression2.precedence()).thenReturn(Precedence.COMPARISON);
        sut.setWhereClause(booleanExpression1);

        sut.orWhere(booleanExpression2);

        assertThat(sut.sql(), is("select * from foo where a = b or c = d"));
    }

    @Test
    void orWhereAndWhere() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("*");
        when(from.sql(any())).thenReturn(" from foo");
        when(booleanExpression1.sql(any())).thenReturn("a = b");
        when(booleanExpression1.precedence()).thenReturn(Precedence.COMPARISON);
        when(booleanExpression2.sql(any())).thenReturn("c = d");
        when(booleanExpression2.precedence()).thenReturn(Precedence.COMPARISON);
        sut.setWhereClause(booleanExpression1);

        sut.orWhere(booleanExpression2);
        sut.andWhere(booleanExpression1);

        assertThat(sut.sql(), is("select * from foo where a = b or c = d and a = b"));
    }

    @Test
    void setHavingClause() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("*");
        when(from.sql(any())).thenReturn(" from foo");
        when(booleanExpression1.sql(any())).thenReturn("a = b");
        when(booleanExpression1.precedence()).thenReturn(Precedence.COMPARISON);

        sut.setHavingClause(booleanExpression1);

        assertThat(sut.sql(), is("select * from foo having a = b"));
    }

    @Test
    void andHaving() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("*");
        when(from.sql(any())).thenReturn(" from foo");
        when(booleanExpression1.sql(any())).thenReturn("a = b");
        when(booleanExpression1.precedence()).thenReturn(Precedence.COMPARISON);
        when(booleanExpression2.sql(any())).thenReturn("c = d");
        when(booleanExpression2.precedence()).thenReturn(Precedence.COMPARISON);
        sut.setHavingClause(booleanExpression1);

        sut.andHaving(booleanExpression2);

        assertThat(sut.sql(), is("select * from foo having a = b and c = d"));
    }

    @Test
    void orHaving() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("*");
        when(from.sql(any())).thenReturn(" from foo");
        when(booleanExpression1.sql(any())).thenReturn("a = b");
        when(booleanExpression1.precedence()).thenReturn(Precedence.COMPARISON);
        when(booleanExpression2.sql(any())).thenReturn("c = d");
        when(booleanExpression2.precedence()).thenReturn(Precedence.COMPARISON);
        sut.setHavingClause(booleanExpression1);

        sut.orHaving(booleanExpression2);

        assertThat(sut.sql(), is("select * from foo having a = b or c = d"));
    }

    @Test
    void andHavingOrHaving() {
        SelectStatement<Integer> sut = new SelectStatement<>(createScope(), TypeToken.of(Integer.class), from, rowMapper, projection);
        when(projection.sql(any())).thenReturn("*");
        when(from.sql(any())).thenReturn(" from foo");
        when(booleanExpression1.sql(any())).thenReturn("a = b");
        when(booleanExpression1.precedence()).thenReturn(Precedence.COMPARISON);
        when(booleanExpression2.sql(any())).thenReturn("c = d");
        when(booleanExpression2.precedence()).thenReturn(Precedence.COMPARISON);
        sut.setHavingClause(booleanExpression1);

        sut.andHaving(booleanExpression2);
        sut.orHaving(booleanExpression1);

        assertThat(sut.sql(), is("select * from foo having a = b and c = d or a = b"));
    }

    private Scope createScope() {
        Database database = Database.newBuilder().dialect(new Db2Dialect()).build();
        return new Scope(database);
    }
}