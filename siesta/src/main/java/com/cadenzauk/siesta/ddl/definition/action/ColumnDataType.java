/*
 * Copyright (c) 2018 Cadenza United Kingdom Limited
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

package com.cadenzauk.siesta.ddl.definition.action;

import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.type.DbType;
import com.cadenzauk.siesta.type.DbTypeId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Optional;

public class ColumnDataType<T> {
    private final DbTypeId<T> dataTypeId;
    private final Optional<Integer> length;
    private final Optional<Integer> scale;

    private ColumnDataType(DbTypeId<T> dataTypeId) {
        this(dataTypeId, Optional.empty(), Optional.empty());
    }

    private ColumnDataType(DbTypeId<T> dataTypeId, int length) {
        this(dataTypeId, Optional.of(length), Optional.empty());
    }

    private ColumnDataType(DbTypeId<T> dataTypeId, int length, int scale) {
        this(dataTypeId, Optional.of(length), Optional.of(scale));
    }

    private ColumnDataType(DbTypeId<T> dataTypeId, Optional<Integer> length, Optional<Integer> scale) {
        this.dataTypeId = dataTypeId;
        this.length = length;
        this.scale = scale;
    }

    public String sql(Database database) {
        DbType<T> type = database.dialect().type(dataTypeId);
        return length
            .map(len -> scale
                .map(prec -> type.sqlType(database, len, prec))
                .orElseGet(() -> type.sqlType(database, len)))
            .orElseGet(() -> type.sqlType(database));
    }

    public static ColumnDataType<Short> smallint() {
        return new ColumnDataType<>(DbTypeId.SMALLINT);
    }

    public static ColumnDataType<Integer> integer() {
        return new ColumnDataType<>(DbTypeId.INTEGER);
    }

    public static ColumnDataType<Long> bigint() {
        return new ColumnDataType<>(DbTypeId.BIGINT);
    }

    public static ColumnDataType<byte[]> binary(int length) {
        return new ColumnDataType<>(DbTypeId.BINARY, length);
    }

    public static ColumnDataType<String> varchar(int length) {
        return new ColumnDataType<>(DbTypeId.VARCHAR, length);
    }

    public static ColumnDataType<String> character(int length) {
        return new ColumnDataType<>(DbTypeId.CHAR, length);
    }

    public static ColumnDataType<BigDecimal> decimal() {
        return new ColumnDataType<>(DbTypeId.DECIMAL);
    }

    public static ColumnDataType<LocalDate> date() {
        return new ColumnDataType<>(DbTypeId.DATE);
    }

    public static ColumnDataType<ZonedDateTime> timestamp() {
        return new ColumnDataType<>(DbTypeId.UTC_TIMESTAMP);
    }

    public static ColumnDataType<LocalTime> time() {
        return new ColumnDataType<>(DbTypeId.TIME);
    }

    public static ColumnDataType<BigDecimal> decimal(int precision, int scale) {
        return new ColumnDataType<>(DbTypeId.DECIMAL, precision, scale);
    }

    public static <T> ColumnDataType<T> of(DbTypeId<T> type, int len, int prec, int scale) {
        return new ColumnDataType<>(type, type.length(len, prec), type.scale(scale));
    }
}
