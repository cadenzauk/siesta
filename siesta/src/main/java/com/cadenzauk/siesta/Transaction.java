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

import com.cadenzauk.core.sql.RowMapper;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A DBMS transaction.
 */
public interface Transaction extends AutoCloseable {
    /**
     * Closes the connection used for the transaction.  If the transaction had not already been committed or rolled back, close first performs a rollback.
     * This allows the transaction to be safely used in a "try-with-resources":
     * <pre>
     *     try (Transaction tx = database.beginTransaction()) {
     *         database.insert(tx, someRows);
     *         database.insert(tx, otherRows);
     *         tx.commit();
     *     }
     * </pre>
     * <p>or a Kotlin {@code use}:</p>
     * <pre>
     *     database.beginTransaction().use { tx ->
     *         database.insert(tx, someRows)
     *         database.insert(tx, otherRows)
     *         tx.commit()
     *     }
     * </pre>
     * The two inserts will be performed as a single database transaction.  If the second insert throws an exception, the transaction will be closed without committing and
     * so rolled back.
     */
    @Override
    void close();

    /**
     * Commits the transaction, first executing any beforeCommit hooks.
     * @see #beforeCommit(Consumer)
     */
    void commit();

    /**
     * Installs a callback that will be called before the transaction is committed if it ever is.
     * @param hook the callback.
     */
    void beforeCommit(Consumer<Transaction> hook);

    /**
     * Rolls the transaction back and then executes any afterRollback hooks.
     * @see #afterRollback(Consumer)
     */
    void rollback();

    /**
     * Installs a callback that will be called after the transaction is rolled back, if it ever is.
     * @param hook the callback.
     */
    void afterRollback(Consumer<Transaction> hook);

    /**
     * Executes a SQL query and returns the rows as objects mapped from the result set using the given rowMapper.
     * @param sql any SQL query.
     * @param args argument values for any placeholder question marks in the SQL.
     * @param rowMapper an object that will read column values from the result set and create an object of type T for each row.
     * @return the objects created by the row mapper - one per row returned by the query.
     * @param <T> type of object to be created by the rowMapper and returned.
     */
    <T> List<T> query(String sql, Object[] args, RowMapper<T> rowMapper);

    /**
     * Executes a SQL query asynchronously and returns a future that completes with the rows as objects mapped
     * from the result set using the given rowMapper.
     * <p>The query is performed using the {@link java.util.concurrent.Executor} in the {@link SqlExecutor} that
     * was used to begin the transaction.   See {@link com.cadenzauk.siesta.jdbc.JdbcSqlExecutor#of(DataSource, Executor)}.</p>
     * @param sql any SQL query.
     * @param args argument values for any placeholder question marks in the SQL.
     * @param rowMapper an object that will read column values from the result set and create an object of type T for each row.
     * @return a future that completes with the objects created by the row mapper - one per row returned by the query.
     * @param <T> type of object to be created by the rowMapper and returned in the future.
     */
    <T> CompletableFuture<List<T>> queryAsync(String sql, Object[] args, RowMapper<T> rowMapper);

    /**
     * Executes a SQL query and returns the rows as objects mapped from the result set using the given rowMapper.
     * @param sql any SQL query.
     * @param args argument values for any placeholder question marks in the SQL.
     * @param rowMapper an object that will read column values from the result set and create an object of type T for each row.
     * @return the objects created by the row mapper - one per row returned by the query.
     * @param <T> type of object to be created by the rowMapper and returned.
     */
    <T> Stream<T> stream(String sql, Object[] args, RowMapper<T> rowMapper);

    /**
     * Performs a SQL statement that does not return a result set, e.g. DML or DDL.
     * @param sql any SQL statement that does not return a result set.
     * @param args argument values for any placeholder question marks in the SQL.
     * @return the number of rows affected by the update, or 0 if the statement does not
     * update rows. e.g. it was DDL.
     */
    int update(String sql, Object[] args);

    /**
     * Executes a SQL statement that may either return a result set or perform and update that returns the
     * number of rows affected.
     * @param sql any SQL, DML or DDL statement.
     * @param args argument values for any placeholder question marks in the SQL.
     * @return true if the first result is a result set; false if it is an update count or there are no results.
     */
    boolean execute(String sql, Object[] args);

    /**
     * Asynchronously performs a SQL statement that does not return a result set, e.g. DML or DDL.
     * <p>The update is performed using the {@link java.util.concurrent.Executor} in the {@link SqlExecutor} that
     * was used to begin the transaction.   See {@link com.cadenzauk.siesta.jdbc.JdbcSqlExecutor#of(DataSource, Executor)}.</p>
     * @param sql any SQL statement that does not return a result set.
     * @param args argument values for any placeholder question marks in the SQL.
     * @return a future that will complete with the number of rows affected by the update, or 0 if the statement does not
     * update rows. e.g. it was DDL.
     */
    CompletableFuture<Integer> updateAsync(String sql, Object[] args);
}
