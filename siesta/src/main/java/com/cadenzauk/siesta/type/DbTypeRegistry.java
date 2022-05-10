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

package com.cadenzauk.siesta.type;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DbTypeRegistry {
    private final Map<DbTypeId<?>,DbType<?>> types = new ConcurrentHashMap<>();

    public DbTypeRegistry() {
        this
            .register(DbTypeId.BOOLEAN, new DefaultBoolean())
            .register(DbTypeId.DECIMAL, new DefaultDecimal())
            .register(DbTypeId.TINYINT, new DefaultTinyint())
            .register(DbTypeId.BINARY, new DefaultVarbinary("binary"))
            .register(DbTypeId.VARBINARY, new DefaultVarbinary())
            .register(DbTypeId.DOUBLE, new DefaultDouble())
            .register(DbTypeId.REAL, new DefaultReal())
            .register(DbTypeId.INTEGER, new DefaultInteger())
            .register(DbTypeId.DATE, new DefaultDate())
            .register(DbTypeId.TIMESTAMP, new DefaultTimestamp())
            .register(DbTypeId.TIME, new DefaultTime())
            .register(DbTypeId.BIGINT, new DefaultBigint())
            .register(DbTypeId.SMALLINT, new DefaultSmallint())
            .register(DbTypeId.CHAR, new DefaultVarchar("char"))
            .register(DbTypeId.VARCHAR, new DefaultVarchar())
            .register(DbTypeId.UUID, new DefaultUuid())
            .register(DbTypeId.UTC_TIMESTAMP, new DefaultUtcTimestamp())
            .register(DbTypeId.JSON, new DefaultJson())
            .register(DbTypeId.JSONB, new DefaultBinaryJson())
        ;
    }

    public <T> DbTypeRegistry register(DbTypeId<T> dbTypeId, DbType<T> type) {
        types.put(dbTypeId, type);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> DbType<T> get(DbTypeId<T> dbTypeId) {
        return Optional.ofNullable(types.get(dbTypeId))
            .map(dt -> (DbType<T>) dt)
            .orElseThrow(() -> new IllegalArgumentException("No dialect type for " + dbTypeId + " registered"));
    }
}
