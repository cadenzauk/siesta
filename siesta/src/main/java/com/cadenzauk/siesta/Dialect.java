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

import com.cadenzauk.siesta.dialect.function.FunctionName;
import com.cadenzauk.siesta.dialect.function.FunctionSpec;
import com.cadenzauk.siesta.type.DbTypeId;
import com.cadenzauk.siesta.type.DbType;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public interface Dialect {
    void registerFunction(FunctionName functionName, FunctionSpec functionSpec);

    FunctionSpec function(FunctionName name);

    <T> void registerType(DbTypeId<T> dbTypeId, DbType<T> type);

    <T> DbType<T> type(DbTypeId<T> type);

    String selectivity(double s);

    boolean supportsMultiInsert();

    boolean requiresFromDual();

    String dual();

    boolean supportsIsolationLevelInQuery();

    String isolationLevelSql(String sql, IsolationLevel level, Optional<LockLevel> keepLocks);

    boolean supportsLockTimeout();

    String setLockTimeout(long time, TimeUnit unit);

    String resetLockTimeout();

    String qualifiedName(String catalog, String schema, String name);

    String concat(Stream<String> sql);

    String fetchFirst(String sql, long n);

    String nextFromSequence(String catalog, String schema, String sequenceName);
}
