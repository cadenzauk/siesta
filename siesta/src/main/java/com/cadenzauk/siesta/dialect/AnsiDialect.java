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
import com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs;
import com.cadenzauk.siesta.dialect.function.FunctionName;
import com.cadenzauk.siesta.dialect.function.FunctionRegistry;
import com.cadenzauk.siesta.dialect.function.FunctionSpec;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Stream;

import static com.cadenzauk.core.lang.StringUtil.hex;
import static java.util.stream.Collectors.joining;

public class AnsiDialect implements Dialect {
    private final FunctionRegistry functions = new FunctionRegistry();

    public AnsiDialect() {
        DateFunctionSpecs.registerDefaults(functions);
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
    public FunctionSpec function(FunctionName name) {
        return functions.get(name)
            .orElseThrow(() -> new IllegalArgumentException("No function " + name + " registered with dialect " + getClass().getSimpleName()));
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
    public String binaryLiteral(byte[] bytes) {
        return String.format("X'%s'", hex(bytes));
    }

    @Override
    public String byteLiteral(byte val) {
        return String.format("cast(%d as smallint)", val);
    }

    @Override
    public String dateLiteral(LocalDate date) {
        return String.format("DATE '%s'", date.format(DateTimeFormatter.ISO_DATE));
    }

    @Override
    public String floatLiteral(float val) {
        return String.format("cast(%s as real)", val);
    }

    @Override
    public String smallIntLiteral(short val) {
        return String.format("cast(%d as smallint)", val);
    }

    @Override
    public String timestampLiteral(LocalDateTime date, ZoneId databaseTimeZone) {
        return String.format("TIMESTAMP '%s'", date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")));
    }

    @Override
    public String timestampWithTimeZoneLiteral(ZonedDateTime date, ZoneId databaseTimeZone) {
        ZonedDateTime localDateTime = date.withZoneSameInstant(databaseTimeZone);
        return String.format("TIMESTAMP '%s'", localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")));
    }

    @Override
    public String stringLiteral(String val) {
        return "'" + val.replaceAll("'", "''") + "'";
    }

    @Override
    public String dateParameter(LocalDate val) {
        return "cast(? as date)";
    }

    @Override
    public String integerParameter(int val) {
        return "?";
    }

    @Override
    public String timestampParameter(LocalDateTime val) {
        return "cast(? as timestamp)";
    }

    @Override
    public String timestampWithTimeZoneParameter(ZonedDateTime val) {
        return "cast(? as timestamp)";
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

    protected FunctionRegistry functions() {
        return functions;
    }
}
