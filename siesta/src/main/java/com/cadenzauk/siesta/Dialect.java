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

import com.cadenzauk.core.sql.QualifiedName;
import com.cadenzauk.core.sql.exception.SqlExceptionConstructor;
import com.cadenzauk.core.sql.exception.SqlExceptionTranslator;
import com.cadenzauk.siesta.dialect.function.FunctionName;
import com.cadenzauk.siesta.dialect.function.FunctionSpec;
import com.cadenzauk.siesta.type.DbType;
import com.cadenzauk.siesta.type.DbTypeId;

import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public interface Dialect {
    void registerFunction(FunctionName functionName, FunctionSpec functionSpec);

    FunctionSpec function(FunctionName name);

    <T> void registerType(DbTypeId<T> dbTypeId, DbType<T> type);

    <T> DbType<T> type(DbTypeId<T> type);

    SqlExceptionTranslator exceptionTranslator();

    void exception(String sqlState, SqlExceptionConstructor constructor);

    void exception(int errorCode, SqlExceptionConstructor constructor);

    void exception(String sqlState, int errorCode, SqlExceptionConstructor constructor);

    String selectivity(double s);

    String updateSql(String qualifiedTableName, Optional<String> alias, String sets, String whereClause);

    String deleteSql(String qualifiedTableName, Optional<String> alias, String whereClause);

    boolean supportsMultiInsert();

    boolean requiresFromDual();

    String dual();

    boolean supportsIsolationLevelInQuery();

    String isolationLevelSql(String sql, IsolationLevel level, Optional<LockLevel> keepLocks);

    boolean supportsLockTimeout();

    String setLockTimeout(long time, TimeUnit unit);

    String resetLockTimeout();

    boolean supportsPartitionByInOlap();

    boolean supportsOrderByInOlap();

    boolean supportsMultipleValueIn();

    boolean supportsJsonFunctions();

    boolean requiresOrderByInRowNumber();

    String qualifiedSequenceName(String catalog, String schema, String name);

    String qualifiedIndexName(String catalog, String schema, String name);

    String qualifiedTableName(String catalog, String schema, String name);

    default QualifiedName fixQualifiedName(QualifiedName qualifiedName) {
        return qualifiedName;
    }

    String concat(Stream<String> sql);

    @Deprecated
    default String fetchFirst(String sql, long n) {
        return fetchFirst(sql, n, OptionalLong.empty());
    }

    @SuppressWarnings("unused")
    default String fetchFirst(String sql, long n, OptionalLong offset) {
        return fetchFirst(sql, n);
    }

    String nextFromSequence(String catalog, String schema, String sequenceName);

    SequenceInfo sequenceInfo();

    TempTableInfo tempTableInfo();

    MergeInfo mergeInfo();

    String createJavaProcSql(Database database, Class<?> procClass, String methodName, String schema);

    Stream<FunctionName> missingJsonFunctions();

    default String orderSql(Order order) {
        return Order.orderWithNullClause(order);
    }
}
