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

import com.cadenzauk.core.lang.CompositeAutoCloseable;
import com.cadenzauk.core.sql.RowMapper;

import java.sql.DatabaseMetaData;
import java.util.List;
import java.util.stream.Stream;

/**
 * An object that execute SQL statements against a DBMS.
 */
public interface SqlExecutor {
    /**
     * Automatically detects a {@link Dialect} from the DBMS's metadata.
     * @return The appropriate dialect for the DBMS.
     */
    Dialect dialect();

    /**
     * Initiates a database transaction.
     * @return The new Transaction.
     */
    Transaction beginTransaction();

    /**
     *
     * @param closeable An object that will be closed by the caller when its use of the metadata is complete.  Upon closing, resources used to retrieve the metadata (the database connection) will be closed.
     * @return The DBMS metadata.
     */
    DatabaseMetaData metadata(CompositeAutoCloseable closeable);

    /**
     * Executes a query and returns the resulting rows mapped to objects of type T using the rowMapper.
     * @param sql The SQL query to be executed.
     * @param args Parameters to substitute into the query replacing the placeholder question marks.
     * @param rowMapper An object that will extract column values from the result set and construct an object of type T for each row returned.
     * @return The resulting mapped rows.
     * @param <T> The type of objects that will be construction by the rowMapper.
     */
    <T> List<T> query(String sql, Object[] args, RowMapper<T> rowMapper);

    /**
     * Executes a query and returns the resulting rows mapped to objects of type T using the rowMapper.
     * The stream that is returned must be closed by the caller when processing is complete.
     * @param sql The SQL query to be executed.
     * @param args Parameters to substitute into the query replacing the placeholder question marks.
     * @param rowMapper An object that will extract column values from the result set and construct an object of type T for each row returned.
     * @return The resulting mapped rows.
     * @param <T> The type of objects that will be construction by the rowMapper.
     */
    <T> Stream<T> stream(String sql, Object[] args, RowMapper<T> rowMapper);

    /**
     * Performs a SQL update.
     * @param sql The SQL statement to be executed.
     * @param args Parameters to substitute into the query replacing the placeholder question marks.
     * @return The number of rows updated.
     */
    int update(String sql, Object[] args);

    /**
     * Performs a SQL update with no parameters.  For executing and update with parameters use {@link #update(String, Object[])}.
     * @param sql The SQL statement to be executed.
     * @return The number of rows updated.
     */
    default int update(String sql) {
        return update(sql, new Object[0]);
    }
}
