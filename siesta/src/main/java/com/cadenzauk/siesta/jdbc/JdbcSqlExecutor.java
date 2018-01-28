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

package com.cadenzauk.siesta.jdbc;

import com.cadenzauk.core.lang.CompositeAutoCloseable;
import com.cadenzauk.core.sql.ConnectionUtil;
import com.cadenzauk.core.sql.DataSourceUtil;
import com.cadenzauk.core.sql.PreparedStatementUtil;
import com.cadenzauk.core.sql.ResultSetSpliterator;
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.core.sql.RuntimeSqlException;
import com.cadenzauk.siesta.Dialect;
import com.cadenzauk.siesta.SqlExecutor;
import com.cadenzauk.siesta.dialect.AutoDetectDialect;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

public class JdbcSqlExecutor implements SqlExecutor {
    private final DataSource dataSource;
    private final int fetchSize;
    private final Executor executor;
    private final JdbcDataTypeRegistry registry = new JdbcDataTypeRegistry();

    private JdbcSqlExecutor(DataSource dataSource, int fetchSize, Executor executor) {
        this.dataSource = dataSource;
        this.fetchSize = fetchSize;
        this.executor = executor;
    }

    Connection connect() {
        return DataSourceUtil.connection(dataSource);
    }

    @Override
    public Dialect dialect() {
        return AutoDetectDialect.from(dataSource);
    }

    @Override
    public JdbcTransaction beginTransaction() {
        return new JdbcTransaction(this);
    }

    @Override
    public <T> List<T> query(String sql, Object[] args, RowMapper<T> rowMapper) {
        try (CompositeAutoCloseable autoCloseable = new CompositeAutoCloseable()) {
            Connection connection = autoCloseable.add(connect());
            return query(connection, sql, args, rowMapper);
        }
    }

    @Override
    public <T> Stream<T> stream(String sql, Object[] args, RowMapper<T> rowMapper) {
        CompositeAutoCloseable closeable = new CompositeAutoCloseable();
        Connection connection = closeable.add(connect());
        return stream(connection, sql, args, rowMapper, closeable);
    }

    @Override
    public int update(String sql, Object[] args) {
        try (CompositeAutoCloseable autoCloseable = new CompositeAutoCloseable()) {
            Connection connection = autoCloseable.add(connect());
            return update(connection, sql, args);
        }
    }

    <T> List<T> query(Connection connection, String sql, Object[] args, RowMapper<T> rowMapper) {
        try (CompositeAutoCloseable closeable = new CompositeAutoCloseable()) {
            return closeable.add(stream(connection, sql, args, rowMapper, closeable)).collect(toList());
        }
    }

    <T> CompletableFuture<List<T>> queryAsync(Connection connection, String sql, Object[] args, RowMapper<T> rowMapper) {
        return CompletableFuture.supplyAsync(() -> query(connection, sql, args, rowMapper));
    }

    <T> Stream<T> stream(Connection connection, String sql, Object[] args, RowMapper<T> rowMapper, CompositeAutoCloseable closeable) {
        try {
            PreparedStatement preparedStatement = prepare(connection, sql, args, closeable);
            preparedStatement.setFetchSize(fetchSize);
            ResultSet resultSet = closeable.add(preparedStatement.executeQuery());
            return StreamSupport
                .stream(new ResultSetSpliterator<>(resultSet, rowMapper, closeable::close), false)
                .onClose(closeable::close);
        } catch (RuntimeException e) {
            closeable.close();
            throw e;
        } catch (SQLException e) {
            closeable.close();
            throw new RuntimeSqlException(e);
        } catch (Exception e) {
            closeable.close();
            throw new RuntimeException(e);
        }
    }

    int update(Connection connection, String sql, Object[] args) {
        try (CompositeAutoCloseable closeable = new CompositeAutoCloseable()) {
            PreparedStatement preparedStatement = prepare(connection, sql, args, closeable);
            return PreparedStatementUtil.executeUpdate(preparedStatement);
        }
    }

    boolean execute(Connection connection, String sql, Object[] args) {
        if (args.length == 0) {
            return ConnectionUtil.execute(connection, sql);
        }
        try (CompositeAutoCloseable closeable = new CompositeAutoCloseable()) {
            PreparedStatement preparedStatement = prepare(connection, sql, args, closeable);
            return PreparedStatementUtil.execute(preparedStatement);
        }
    }

    public CompletableFuture<Integer> updateAsync(Connection connection, String sql, Object[] args) {
        return CompletableFuture.supplyAsync(() -> update(connection, sql, args), executor);
    }

    private PreparedStatement prepare(Connection connection, String sql, Object[] args, CompositeAutoCloseable closeable) {
        PreparedStatement preparedStatement = closeable.add(ConnectionUtil.prepare(connection, sql));
        IntStream.range(0, args.length).forEach(i -> registry.setParameter(preparedStatement, i + 1, args[i]));
        return preparedStatement;
    }

    public static JdbcSqlExecutor of(DataSource dataSource) {
        return new JdbcSqlExecutor(dataSource, 0, ForkJoinPool.commonPool());
    }

    public static JdbcSqlExecutor of(DataSource dataSource, int fetchSize) {
        return new JdbcSqlExecutor(dataSource, fetchSize, ForkJoinPool.commonPool());
    }

    public static JdbcSqlExecutor of(DataSource dataSource, Executor executor) {
        return new JdbcSqlExecutor(dataSource, 0, executor);
    }

    public static JdbcSqlExecutor of(DataSource dataSource, int fetchSize, Executor executor) {
        return new JdbcSqlExecutor(dataSource, fetchSize, executor);
    }
}
