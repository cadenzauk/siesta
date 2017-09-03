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
import com.cadenzauk.siesta.SqlExecutor;
import com.cadenzauk.siesta.Transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

public class JdbcSqlExecutor implements SqlExecutor {
    private final DataSource dataSource;
    private final int fetchSize;
    private final JdbcDataTypeRegistry registry = new JdbcDataTypeRegistry();

    private JdbcSqlExecutor(DataSource dataSource, int fetchSize) {
        this.dataSource = dataSource;
        this.fetchSize = fetchSize;
    }

    Connection connect() {
        return DataSourceUtil.connection(dataSource);
    }

    @Override
    public JdbcTransaction beginTransaction() {
        return new JdbcTransaction(this);
    }

    <T> List<T> query(JdbcTransaction transaction, String sql, Object[] args, RowMapper<T> rowMapper) {
        try (CompositeAutoCloseable closeable = new CompositeAutoCloseable()) {
            return closeable.add(stream(transaction, sql, args, rowMapper)).collect(toList());
        }
    }

    <T> Stream<T> stream(JdbcTransaction transaction, String sql, Object[] args, RowMapper<T> rowMapper) {
        CompositeAutoCloseable closeable = new CompositeAutoCloseable();
        try {
            PreparedStatement preparedStatement = prepare(transaction, sql, args, closeable);
            preparedStatement.setFetchSize(fetchSize);
            ResultSet resultSet = closeable.add(preparedStatement.executeQuery());
            return StreamSupport
                .stream(new ResultSetSpliterator<>(resultSet, rowMapper), false)
                .onClose(closeable::close);
        } catch (RuntimeException e) {
            closeable.close();
            throw e;
        } catch (Exception e) {
            closeable.close();
            throw new RuntimeException(e);
        }
    }

    int update(Transaction transaction, String sql, Object[] args) {
        try (CompositeAutoCloseable closeable = new CompositeAutoCloseable()) {
            PreparedStatement preparedStatement = prepare(transaction, sql, args, closeable);
            return PreparedStatementUtil.executeUpdate(preparedStatement);
        }
    }

    private PreparedStatement prepare(Transaction transaction, String sql, Object[] args, CompositeAutoCloseable closeable) {
        JdbcTransaction jdbcTransaction = (JdbcTransaction) transaction;
        Connection connection = jdbcTransaction.connection();
        PreparedStatement preparedStatement = closeable.add(ConnectionUtil.prepare(connection, sql));
        IntStream.range(0, args.length).forEach(i -> registry.setParameter(preparedStatement, i + 1, args[i]));
        return preparedStatement;
    }

    public static JdbcSqlExecutor of(DataSource dataSource) {
        return new JdbcSqlExecutor(dataSource, 0);
    }

    public static JdbcSqlExecutor of(DataSource dataSource, int fetchSize) {
        return new JdbcSqlExecutor(dataSource, fetchSize);
    }
}
