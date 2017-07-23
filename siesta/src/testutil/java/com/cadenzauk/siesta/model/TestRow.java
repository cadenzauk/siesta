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

package com.cadenzauk.siesta.model;

import com.cadenzauk.core.RandomValues;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Table(name = "TEST_TABLE", schema = "SIESTA")
public class TestRow {
    private final String guid;

    private final String stringReq;
    private final Optional<String> stringOpt;
    private final Integer integerReq;
    private final Optional<Integer> integerOpt;
    private final BigDecimal decimalReq;
    private final Optional<BigDecimal> decimalOpt;
    private final LocalDate localDateReq;
    private final Optional<LocalDate> localDateOpt;
    private final LocalDateTime localDateTimeReq;
    private final Optional<LocalDateTime> localDateTimeOpt;
    private final ZonedDateTime utcDateTimeReq;
    private final Optional<ZonedDateTime> utcDateTimeOpt;

    public TestRow(LocalDateTime localDateTimeOpt) {
        this(Optional.of(localDateTimeOpt), Optional.empty());
    }

    public TestRow(ZonedDateTime utcDateTimeOpt) {
        this(Optional.empty(), Optional.of(utcDateTimeOpt));
    }

    private TestRow(Optional<LocalDateTime> localDateTimeOpt, Optional<ZonedDateTime> utcDateTimeOpt) {
        this.guid = UUID.randomUUID().toString();
        this.stringReq = RandomStringUtils.randomAlphabetic(10, 20);
        this.stringOpt = Optional.empty();
        this.integerReq = RandomUtils.nextInt();
        this.integerOpt = Optional.empty();
        this.decimalReq = RandomValues.randomBigDecimal(4, 5);
        this.decimalOpt = Optional.empty();
        this.localDateReq = RandomValues.randomLocalDate();
        this.localDateOpt = Optional.empty();
        this.localDateTimeReq = RandomValues.randomLocalDateTime();
        this.localDateTimeOpt = localDateTimeOpt;
        this.utcDateTimeReq = RandomValues.randomZonedDateTime(ZoneId.of("UTC"));
        this.utcDateTimeOpt = utcDateTimeOpt;
    }

    public String guid() {
        return guid;
    }

    public String stringReq() {
        return stringReq;
    }

    public Optional<String> stringOpt() {
        return stringOpt;
    }

    public Integer integerReq() {
        return integerReq;
    }

    public Optional<Integer> integerOpt() {
        return integerOpt;
    }

    public BigDecimal decimalReq() {
        return decimalReq;
    }

    public Optional<BigDecimal> decimalOpt() {
        return decimalOpt;
    }

    public LocalDate localDateReq() {
        return localDateReq;
    }

    public Optional<LocalDate> localDateOpt() {
        return localDateOpt;
    }

    public LocalDateTime localDateTimeReq() {
        return localDateTimeReq;
    }

    public Optional<LocalDateTime> localDateTimeOpt() {
        return localDateTimeOpt;
    }

    public ZonedDateTime utcDateTimeReq() {
        return utcDateTimeReq;
    }

    public Optional<ZonedDateTime> utcDateTimeOpt() {
        return utcDateTimeOpt;
    }
}
