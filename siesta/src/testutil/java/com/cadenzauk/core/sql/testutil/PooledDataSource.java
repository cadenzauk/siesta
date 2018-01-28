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

package com.cadenzauk.core.sql.testutil;

import com.cadenzauk.core.lang.CompositeAutoCloseable;
import com.cadenzauk.core.sql.ConnectionUtil;
import com.cadenzauk.core.sql.RuntimeSqlException;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.PooledConnection;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Optional;
import java.util.Stack;
import java.util.logging.Logger;

public class PooledDataSource implements DataSource, AutoCloseable {
    private final CompositeAutoCloseable autoCloseable = new CompositeAutoCloseable();
    private final Object lock = new Object();
    private final Stack<PooledConnection> freeList = new Stack<>();
    private final ConnectionPoolDataSource poolDataSource;
    private final Optional<String> initString;

    public PooledDataSource(ConnectionPoolDataSource poolDataSource) {
        this.poolDataSource = poolDataSource;
        initString = Optional.empty();
    }

    public PooledDataSource(ConnectionPoolDataSource poolDataSource, String initial) {
        this.poolDataSource = poolDataSource;
        initString = Optional.ofNullable(initial).filter(StringUtils::isNotBlank);
    }

    @Override
    public Connection getConnection() {
        return init(new PoolConnection(allocateConnection(), this::release));
    }

    @Override
    public Connection getConnection(String username, String password) {
        throw new NotImplementedException("Not yet implemented.");
    }

    private Connection init(Connection connection) {
        initString.ifPresent(s -> ConnectionUtil.execute(connection, s));
        return connection;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return iface.cast(this);
        }
        throw new SQLException("Cannot unwrap a " + this.getClass() + " as a " + iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
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

    private PooledConnection allocateConnection() {
        synchronized (lock) {
            if (!freeList.empty()) {
                return freeList.pop();
            }
        }
        return newConnection();
    }

    private void release(PooledConnection pooledConnection) {
        synchronized (lock) {
            freeList.push(pooledConnection);
        }
    }

    private PooledConnection newConnection() {
        try {
            return autoCloseable.add(poolDataSource.getPooledConnection(), PooledConnection::close);
        } catch (SQLException e) {
            throw new RuntimeSqlException(e);
        }
    }
}
