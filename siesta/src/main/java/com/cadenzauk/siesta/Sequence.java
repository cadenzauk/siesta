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
import com.cadenzauk.siesta.grammar.expression.SequenceExpression;
import com.google.common.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sequence<T> {
    private final static Logger LOG = LoggerFactory.getLogger(Sequence.class);
    private final Database database;
    private final String catalog;
    private final String schema;
    private final String sequenceName;
    private final DataType<T> dataType;

    private Sequence(Builder<T> builder) {
        database = builder.database;
        catalog = builder.catalog;
        schema = builder.schema;
        sequenceName = builder.sequenceName;
        dataType = builder.dataType;
    }

    public SequenceExpression<T> nextVal() {
        return new SequenceExpression<>(this);
    }

    public T single() {
        return single(database.getDefaultSqlExecutor());
    }

    public TypeToken<T> type() {
        return TypeToken.of(dataType.javaClass());
    }

    public RowMapper<T> rowMapper(String label) {
        return rs -> dataType.get(rs, label, database).orElse(null);
    }

    public String name() {
        return database.dialect().qualifiedName(catalog, schema, sequenceName);
    }

    private T single(SqlExecutor sqlExecutor) {
        return database.select(nextVal(), sequenceName).single(sqlExecutor);
    }

    public String sql() {
        return database.dialect().nextFromSequence(catalog, schema, sequenceName);
    }

    public static <T> Builder<T> newBuilder() {
        return new Builder<>();
    }

    public static final class Builder<T> {
        private Database database;
        private String catalog;
        private String schema;
        private String sequenceName;
        private DataType<T> dataType;

        private Builder() {
        }

        public Builder<T> database(Database database) {
            this.database = database;
            return this;
        }

        public Builder<T> catalog(String catalog) {
            this.catalog = catalog;
            return this;
        }

        public Builder<T> schema(String schema) {
            this.schema = schema;
            return this;
        }

        public Builder<T> sequenceName(String sequenceName) {
            this.sequenceName = sequenceName;
            return this;
        }

        public Builder<T> dataType(DataType<T> dataType) {
            this.dataType = dataType;
            return this;
        }

        public Sequence<T> build() {
            return new Sequence<>(this);
        }
    }
}
