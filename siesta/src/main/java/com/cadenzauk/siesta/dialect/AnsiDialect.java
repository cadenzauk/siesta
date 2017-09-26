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

import com.cadenzauk.siesta.Dialect;
import com.cadenzauk.siesta.IsolationLevel;
import com.cadenzauk.siesta.LockLevel;
import com.cadenzauk.siesta.dialect.function.FunctionName;
import com.cadenzauk.siesta.dialect.function.FunctionRegistry;
import com.cadenzauk.siesta.dialect.function.FunctionSpec;
import com.cadenzauk.siesta.dialect.function.aggregate.AggregateFunctionSpecs;
import com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs;
import com.cadenzauk.siesta.dialect.function.string.StringFunctionSpecs;
import com.cadenzauk.siesta.type.TypeAdapter;
import com.cadenzauk.siesta.type.TypeAdapterRegistry;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class AnsiDialect implements Dialect {
    private final FunctionRegistry functions = new FunctionRegistry();
    private final TypeAdapterRegistry types = new TypeAdapterRegistry();

    public AnsiDialect() {
        AggregateFunctionSpecs.registerDefaults(functions);
        DateFunctionSpecs.registerDefaults(functions);
        StringFunctionSpecs.registerDefaults(functions);
    }

    @Override
    public String selectivity(double s) {
        return "";
    }

    @Override
    public boolean requiresFromDual() {
        return true;
    }

    @Override
    public String qualifiedName(String catalog, String schema, String name) {
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
    public <T> void registerType(Class<T> javaClass, TypeAdapter<T> type) {
        types.register(javaClass, type);
    }

    @Override
    public <T> TypeAdapter<T> type(Class<T> javaClass) {
        return types.get(javaClass);
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
    public String tinyintType() {
        return "smallint";
    }

    @Override
    public String smallintType() {
        return "smallint";
    }

    @Override
    public String integerType() {
        return "integer";
    }

    @Override
    public String bigintType() {
        return "bigint";
    }

    @Override
    public String decimalType(int size, int prec) {
        return String.format("decimal(%d,%d)", size, prec);
    }

    @Override
    public String doubleType() {
        return "double precision";
    }

    @Override
    public String realType() {
        return "real";
    }

    @Override
    public String dateType() {
        return "date";
    }

    @Override
    public String timeType() {
        return "time";
    }

    @Override
    public String timestampType(Optional<Integer> prec) {
        return prec
            .map(p -> String.format("timestamp(%d)", p))
            .orElse("timestamp");
    }

    @Override
    public String varcharType(int size) {
        return String.format("varchar(%d)", size);
    }

    @Override
    public String charType(int len) {
        return String.format("char(%d)", len);
    }

    @Override
    public String nextFromSequence(String catalog, String schema, String sequenceName) {
        return String.format("%s.NEXTVAL", qualifiedName(catalog, schema, sequenceName));
    }

    protected TypeAdapterRegistry types() {
        return types;
    }

    protected FunctionRegistry functions() {
        return functions;
    }
}
