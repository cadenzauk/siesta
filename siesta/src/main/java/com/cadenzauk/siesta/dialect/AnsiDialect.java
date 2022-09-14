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

package com.cadenzauk.siesta.dialect;

import com.cadenzauk.core.sql.exception.SqlExceptionConstructor;
import com.cadenzauk.core.sql.exception.SqlExceptionTranslator;
import com.cadenzauk.core.sql.exception.SqlStateExceptionTranslator;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Dialect;
import com.cadenzauk.siesta.IsolationLevel;
import com.cadenzauk.siesta.LockLevel;
import com.cadenzauk.siesta.MergeInfo;
import com.cadenzauk.siesta.SequenceInfo;
import com.cadenzauk.siesta.TempTableInfo;
import com.cadenzauk.siesta.dialect.function.FunctionName;
import com.cadenzauk.siesta.dialect.function.FunctionRegistry;
import com.cadenzauk.siesta.dialect.function.FunctionSpec;
import com.cadenzauk.siesta.dialect.function.aggregate.AggregateFunctionSpecs;
import com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs;
import com.cadenzauk.siesta.dialect.function.json.JsonFunctionSpecs;
import com.cadenzauk.siesta.dialect.function.string.StringFunctionSpecs;
import com.cadenzauk.siesta.type.DbType;
import com.cadenzauk.siesta.type.DbTypeId;
import com.cadenzauk.siesta.type.DbTypeRegistry;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class AnsiDialect implements Dialect {
    private final FunctionRegistry functions = new FunctionRegistry();
    private final DbTypeRegistry types = new DbTypeRegistry();
    private final SqlStateExceptionTranslator exceptionTranslator = new SqlStateExceptionTranslator();
    private SequenceInfo sequenceInfo;
    private TempTableInfo tempTableInfo;
    private MergeInfo mergeInfo;

    public AnsiDialect() {
        functions
            .register(AggregateFunctionSpecs::registerDefaults)
            .register(DateFunctionSpecs::registerDefaults)
            .register(StringFunctionSpecs::registerDefaults)
            .register(JsonFunctionSpecs::registerDefaults);

        sequenceInfo = SequenceInfo.newBuilder().build();
        tempTableInfo = TempTableInfo.newBuilder().build();
        mergeInfo = new MergeInfo(this);
    }

    @Override
    public String selectivity(double s) {
        return "";
    }

    @Override
    public String updateSql(String qualifiedTableName, Optional<String> alias, String sets, String whereClause) {
        return alias
                   .map(a -> String.format("update %s %s set %s%s",
                       qualifiedTableName,
                       a,
                       sets,
                       whereClause))
                   .orElseGet(() -> String.format("update %s set %s%s",
                       qualifiedTableName,
                       sets,
                       whereClause));
    }

    @Override
    public String deleteSql(String qualifiedTableName, Optional<String> alias, String whereClause) {
        return alias
                   .map(a -> String.format("delete from %s %s%s",
                       qualifiedTableName,
                       a,
                       whereClause))
                   .orElseGet(() -> String.format("delete from %s%s",
                       qualifiedTableName,
                       whereClause));
    }

    @Override
    public boolean requiresFromDual() {
        return true;
    }

    @Override
    public String qualifiedSequenceName(String catalog, String schema, String name) {
        return qualified(schema, name);
    }

    @Override
    public String qualifiedIndexName(String catalog, String schema, String name) {
        return qualified(schema, name);
    }

    @Override
    public String qualifiedTableName(String catalog, String schema, String name) {
        return qualified(schema, name);
    }

    private String qualified(String schema, String name) {
        return Optional.ofNullable(schema)
            .filter(StringUtils::isNotBlank)
            .map(s -> String.format("%s.%s", s, name))
            .orElse(name);
    }

    @Override
    public String dual() {
        return "DUAL";
    }

    @Override
    public void registerFunction(FunctionName functionName, FunctionSpec functionSpec) {
        functions.register(functionName, functionSpec);
    }

    @Override
    public FunctionSpec function(FunctionName name) {
        return functions.get(name)
            .orElseThrow(() -> new IllegalArgumentException("No function " + name + " registered with dialect " + getClass().getSimpleName()));
    }

    @Override
    public <T> void registerType(DbTypeId<T> dbTypeId, DbType<T> type) {
        types.register(dbTypeId, type);
    }

    @Override
    public <T> DbType<T> type(DbTypeId<T> javaClass) {
        return types.get(javaClass);
    }

    @Override
    public SqlExceptionTranslator exceptionTranslator() {
        return exceptionTranslator;
    }

    @Override
    public void exception(String sqlState, SqlExceptionConstructor constructor) {
        exceptionTranslator.register(sqlState, constructor);
    }

    @Override
    public void exception(int errorCode, SqlExceptionConstructor constructor) {
        exceptionTranslator.register(errorCode, constructor);
    }

    @Override
    public void exception(String sqlState, int errorCode, SqlExceptionConstructor constructor) {
        exceptionTranslator.register(sqlState, errorCode, constructor);
    }

    @Override
    public boolean supportsMultiInsert() {
        return false;
    }

    @Override
    public String concat(Stream<String> sql) {
        return sql.collect(joining(" || "));
    }

    @Override
    public String fetchFirst(String sql, long n) {
        return String.format("select * from (select *, row_number() over() as x_row_number from (%s)) where x_row_number <= %d", sql, n);
    }

    @Override
    public boolean supportsIsolationLevelInQuery() {
        return false;
    }

    @Override
    public String isolationLevelSql(String sql, IsolationLevel level, Optional<LockLevel> keepLocks) {
        return sql;
    }

    @Override
    public boolean supportsLockTimeout() {
        return false;
    }

    @Override
    public String setLockTimeout(long time, TimeUnit unit) {
        throw new UnsupportedOperationException(String.format("%s does not support lock timeouts.", getClass().getName()));
    }

    @Override
    public String resetLockTimeout() {
        throw new UnsupportedOperationException(String.format("%s does not support lock timeouts.", getClass().getName()));
    }

    @Override
    public boolean supportsPartitionByInOlap() {
        return true;
    }

    @Override
    public boolean supportsOrderByInOlap() {
        return true;
    }

    @Override
    public boolean supportsMultipleValueIn() {
        return true;
    }

    @Override
    public boolean supportsJsonFunctions() {
        return false;
    }

    @Override
    public boolean requiresOrderByInRowNumber() {
        return false;
    }

    @Override
    public String nextFromSequence(String catalog, String schema, String sequenceName) {
        return String.format("%s.NEXTVAL", qualifiedSequenceName(catalog, schema, sequenceName));
    }

    @Override
    public SequenceInfo sequenceInfo() {
        return sequenceInfo;
    }

    @Override
    public TempTableInfo tempTableInfo() {
        return tempTableInfo;
    }

    @Override
    public MergeInfo mergeInfo() {
        return mergeInfo;
    }

    @Override
    public String createJavaProcSql(Database database, Class<?> procClass, String methodName, String schema) {
        return "";
    }

    @Override
    public Stream<FunctionName> missingJsonFunctions() {
        return Stream.empty();
    }

    protected DbTypeRegistry types() {
        return types;
    }

    protected FunctionRegistry functions() {
        return functions;
    }

    protected SqlStateExceptionTranslator exceptions() {
        return exceptionTranslator;
    }

    protected void setSequenceInfo(SequenceInfo sequenceInfo) {
        this.sequenceInfo = sequenceInfo;
    }

    protected void setTempTableInfo(TempTableInfo tempTableInfo) {
        this.tempTableInfo = tempTableInfo;
    }

    protected void setMergeInfo(MergeInfo mergeInfo) {
        this.mergeInfo = mergeInfo;
    }
}
