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
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;

public class JdbcTransaction implements Transaction {
    private final CompositeAutoCloseable autoCloseable = new CompositeAutoCloseable();
    private final Connection connection;
    private final JdbcSqlExecutor sqlExecutor;

    public JdbcTransaction(JdbcSqlExecutor sqlExecutor) {
        this.sqlExecutor = sqlExecutor;
        connection = autoCloseable.add(sqlExecutor.connect());
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commit() {
        ConnectionUtil.commit(connection);
    }

    @Override
    public void rollback() {
        ConnectionUtil.rollback(connection);
    }

    @Override
    public <T> List<T> query(String sql, Object[] args, RowMapper<T> rowMapper) {
        return sqlExecutor.query(connection, sql, args, rowMapper);
    }

    @Override
    public <T> Stream<T> stream(String sql, Object[] args, RowMapper<T> rowMapper) {
        return sqlExecutor.stream(connection, sql, args, rowMapper, new CompositeAutoCloseable());
    }

    @Override
    public int update(String sql, Object[] args) {
        return sqlExecutor.update(connection, sql, args);
    }

    @Override
    public void close() {
        commit();
        autoCloseable.close();
    }

    Connection connection() {
        return connection;
    }
}
