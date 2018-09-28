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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ResultSetSpliterator<T> extends Spliterators.AbstractSpliterator<T> {
    private final ResultSet resultSet;
    private final RowMapper<T> rowMapper;
    private final Runnable atEnd;
    private final AtomicBoolean exhausted = new AtomicBoolean(false);

    public ResultSetSpliterator(ResultSet resultSet, RowMapper<T> rowMapper) {
        this(resultSet, rowMapper, () -> {});
    }

    public ResultSetSpliterator(ResultSet resultSet, RowMapper<T> rowMapper, Runnable atEnd) {
        super(Long.MAX_VALUE, 0);
        this.resultSet = resultSet;
        this.rowMapper = rowMapper;
        this.atEnd = atEnd;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        try {
            if (exhausted.get() || !resultSet.next()) {
                if (!exhausted.getAndSet(true)) {
                    atEnd.run();
                }
                return false;
            }
            action.accept(rowMapper.mapRow(resultSet));
            return true;
        } catch (SQLException e) {
            throw new RuntimeSqlException(e);
        }
    }
}
