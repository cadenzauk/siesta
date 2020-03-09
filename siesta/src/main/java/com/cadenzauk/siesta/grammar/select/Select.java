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

import com.cadenzauk.core.lang.CompositeAutoCloseable;
import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.ColumnSpecifier;
import com.cadenzauk.siesta.DataType;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.From;
import com.cadenzauk.siesta.IsolationLevel;
import com.cadenzauk.siesta.LockLevel;
import com.cadenzauk.siesta.Projection;
import com.cadenzauk.siesta.ProjectionColumn;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.SqlExecutor;
import com.cadenzauk.siesta.Transaction;
import com.cadenzauk.siesta.catalog.Table;
import com.cadenzauk.siesta.grammar.expression.Precedence;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.google.common.reflect.TypeToken;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public abstract class Select<RT> implements TypedExpression<RT> {
    protected final SelectStatement<RT> statement;

    Select(SelectStatement<RT> statement) {
        this.statement = statement;
    }

    public List<RT> list() {
        return statement.list(defaultSqlExecutor());
    }

    public List<RT> list(SqlExecutor sqlExecutor) {
        return statement.list(sqlExecutor);
    }

    public List<RT> list(Transaction transaction) {
        return statement.list(transaction);
    }

    public CompletableFuture<List<RT>> listAsync() {
        return statement.listAsync(defaultSqlExecutor());
    }

    public CompletableFuture<List<RT>> listAsync(SqlExecutor sqlExecutor) {
        return statement.listAsync(sqlExecutor);
    }

    public CompletableFuture<List<RT>> listAsync(Transaction transaction) {
        return statement.listAsync(transaction);
    }

    public Optional<RT> optional() {
        return optional(defaultSqlExecutor());
    }

    public Optional<RT> optional(SqlExecutor sqlExecutor) {
        return statement.optional(sqlExecutor);
    }

    public Optional<RT> optional(Transaction transaction) {
        return statement.optional(transaction);
    }

    public CompletableFuture<Optional<RT>> optionalAsync() {
        return statement.optionalAsync(defaultSqlExecutor());
    }

    public CompletableFuture<Optional<RT>> optionalAsync(SqlExecutor sqlExecutor) {
        return statement.optionalAsync(sqlExecutor);
    }

    public CompletableFuture<Optional<RT>> optionalAsync(Transaction transaction) {
        return statement.optionalAsync(transaction);
    }

    public Stream<RT> stream() {
        return statement.stream(defaultSqlExecutor());
    }

    public Stream<RT> stream(CompositeAutoCloseable compositeAutoCloseable) {
        return statement.stream(defaultSqlExecutor(), compositeAutoCloseable);
    }

    public Stream<RT> stream(SqlExecutor sqlExecutor) {
        return statement.stream(sqlExecutor);
    }

    public Stream<RT> stream(SqlExecutor sqlExecutor, CompositeAutoCloseable compositeAutoCloseable) {
        return statement.stream(sqlExecutor, compositeAutoCloseable);
    }

    public Stream<RT> stream(Transaction transaction) {
        return statement.stream(transaction);
    }

    public Stream<RT> stream(Transaction transaction, CompositeAutoCloseable compositeAutoCloseable) {
        return statement.stream(transaction, compositeAutoCloseable);
    }

    public RT single() {
        return single(defaultSqlExecutor());
    }

    public RT single(SqlExecutor sqlExecutor) {
        return statement.single(sqlExecutor);
    }

    public RT single(Transaction transaction) {
        return statement.single(transaction);
    }

    public CompletableFuture<RT> singleAsync() {
        return statement.singleAsync(defaultSqlExecutor());
    }

    public CompletableFuture<RT> singleAsync(SqlExecutor sqlExecutor) {
        return statement.singleAsync(sqlExecutor);
    }

    public CompletableFuture<RT> singleAsync(Transaction transaction) {
        return statement.singleAsync(transaction);
    }

    public Select<RT> fetchFirst(long i) {
        statement.fetchFirst(i);
        return this;
    }

    public Select<RT> withIsolation(IsolationLevel level) {
        statement.withIsolation(level);
        return this;
    }

    public Select<RT> keepLocks(LockLevel level) {
        statement.keepLocks(level);
        return this;
    }

    @Override
    public TypeToken<RT> type() {
        return statement.rowType();
    }

    public String sql() {
        return statement.sql();
    }

    @Override
    public String sql(Scope scope) {
        return statement.sql(scope);
    }

    @Override
    public String label(Scope scope) {
        return statement.label(scope);
    }

    @Override
    public Precedence precedence() {
        return Precedence.SELECT;
    }

    @Override
    public RowMapperFactory<RT> rowMapperFactory(Scope scope) {
        final DataType<RT> dataType = scope.database().getDataTypeOf(type());
        return label -> rs -> dataType.get(rs, label.orElseGet(() -> label(scope)), scope.database()).orElse(null);
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return statement.args(scope.empty());
    }

    public Stream<ProjectionColumn<?>> projectionColumns(Scope scope) {
        return projection().columns(scope.plus(scope()));
    }

    Database database() {
        return statement.scope.database();
    }

    private Projection<RT> projection() {
        return statement.projection();
    }

    boolean projectionIncludes(ColumnSpecifier<?> columnSpecifier) {
        return statement.projection().includes(columnSpecifier);
    }

    <T> Optional<ProjectionColumn<T>> findColumn(ColumnSpecifier<T> columnSpecifier) {
        return statement.projection().findColumn(scope(), columnSpecifier);
    }

    RowMapperFactory<RT> rowMapperFactory() {
        return statement.rowMapperFactory();
    }

    protected Scope scope() {
        return statement.scope();
    }

    private SqlExecutor defaultSqlExecutor() {
        return database().getDefaultSqlExecutor();
    }

    public static <R> ExpectingJoin1<R> from(Database database, Alias<R> alias) {
        SelectStatement<R> select = new SelectStatement<>(new Scope(database, alias), alias.type(), From.from(alias), Projection.of(alias));
        return new ExpectingJoin1<>(select);
    }

    public static <R> ExpectingJoin1<R> from(Database database, CommonTableExpression<R> cte) {
        return from(database, cte, cte.asAlias());
    }

    public static <R> ExpectingJoin1<R> from(Database database, CommonTableExpression<R> cte, String aliasName) {
        return from(database, cte, cte.as(aliasName));
    }

    private static <R> ExpectingJoin1<R> from(Database database, CommonTableExpression<R> cte, Alias<R> alias) {
        SelectStatement<R> select = new SelectStatement<>(new Scope(database, alias), alias.type(), From.from(alias), Projection.of(alias));
        cte.commonTableExpressions().forEach(select::addCommonTableExpression);
        return new ExpectingJoin1<>(select);
    }

    public static <R> ExpectingJoin1<R> from(Database database, Table<R> table) {
        return from(database, table.as(table.tableName()));
    }
}
