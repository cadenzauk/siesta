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

package com.cadenzauk.core.sql;

import com.cadenzauk.core.lang.CompositeAutoCloseable;
import com.cadenzauk.core.tuple.Tuple;
import com.cadenzauk.core.tuple.Tuple2;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.PooledConnection;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class PooledDataSource implements DataSource, AutoCloseable {
    private final CompositeAutoCloseable autoCloseable = new CompositeAutoCloseable();
    private final ConcurrentHashMap<Tuple2<String,String>,PooledConnection> connectionMap = new ConcurrentHashMap<>();
    private final ConnectionPoolDataSource poolDataSource;
    private final PooledConnection pooledConnection;

    public PooledDataSource(ConnectionPoolDataSource poolDataSource) throws SQLException {
        this.poolDataSource = poolDataSource;
        pooledConnection = autoCloseable.add(poolDataSource.getPooledConnection(), PooledConnection::close);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return pooledConnection.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        try {
            return connectionMap.computeIfAbsent(Tuple.of(username, password), k -> openConnection(username, password)).getConnection();
        } catch (RuntimeSqlException e) {
            throw e.getCause();
        }
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return iface.cast(this);
        }
        throw new SQLException("Cannot unwrap a " + this.getClass() + " as a " + iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return poolDataSource.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        poolDataSource.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        poolDataSource.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return poolDataSource.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return poolDataSource.getParentLogger();
    }

    @Override
    public void close() {
        autoCloseable.close();
    }

    private PooledConnection openConnection(String username, String password) {
        try {
            return autoCloseable.add(poolDataSource.getPooledConnection(username, password), PooledConnection::close);
        } catch (SQLException e) {
            throw new RuntimeSqlException(e);
        }
    }
}
