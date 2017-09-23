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
import java.time.LocalTime;
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
    private final LocalTime localTimeReq;
    private final Optional<LocalTime> localTimeOpt;
    private final ZonedDateTime utcDateTimeReq;
    private final Optional<ZonedDateTime> utcDateTimeOpt;

    private TestRow(Builder builder) {
        guid = builder.guid;
        stringReq = builder.stringReq;
        stringOpt = builder.stringOpt;
        integerReq = builder.integerReq;
        integerOpt = builder.integerOpt;
        decimalReq = builder.decimalReq;
        decimalOpt = builder.decimalOpt;
        localDateReq = builder.localDateReq;
        localDateOpt = builder.localDateOpt;
        localDateTimeReq = builder.localDateTimeReq;
        localDateTimeOpt = builder.localDateTimeOpt;
        localTimeReq = builder.localTimeReq;
        localTimeOpt = builder.localTimeOpt;
        utcDateTimeReq = builder.utcDateTimeReq;
        utcDateTimeOpt = builder.utcDateTimeOpt;
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

    public LocalTime localTimeReq() {
        return localTimeReq;
    }

    public Optional<LocalTime> localTimeOpt() {
        return localTimeOpt;
    }

    public static TestRow of(LocalTime localTimeOpt) {
        return TestRow.newBuilder()
            .localTimeOpt(Optional.of(localTimeOpt))
            .build();
    }

    public static TestRow of(LocalDateTime localDateTimeOpt) {
        return TestRow.newBuilder()
            .localDateTimeOpt(Optional.of(localDateTimeOpt))
            .build();
    }

    public static TestRow of(ZonedDateTime utcDateTimeOpt) {
        return TestRow.newBuilder()
            .utcDateTimeOpt(Optional.of(utcDateTimeOpt))
            .build();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private String guid;
        private String stringReq;
        private Optional<String> stringOpt;
        private Integer integerReq;
        private Optional<Integer> integerOpt;
        private BigDecimal decimalReq;
        private Optional<BigDecimal> decimalOpt;
        private LocalDate localDateReq;
        private Optional<LocalDate> localDateOpt;
        private LocalDateTime localDateTimeReq;
        private Optional<LocalDateTime> localDateTimeOpt;
        private LocalTime localTimeReq;
        private Optional<LocalTime> localTimeOpt;
        private ZonedDateTime utcDateTimeReq;
        private Optional<ZonedDateTime> utcDateTimeOpt;

        private Builder() {
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
            this.localDateTimeOpt = Optional.empty();
            this.utcDateTimeReq = RandomValues.randomZonedDateTime(ZoneId.of("UTC"));
            this.utcDateTimeOpt = Optional.empty();
        }

        public Builder guid(String guid) {
            this.guid = guid;
            return this;
        }

        public Builder stringReq(String stringReq) {
            this.stringReq = stringReq;
            return this;
        }

        public Builder stringOpt(Optional<String> stringOpt) {
            this.stringOpt = stringOpt;
            return this;
        }

        public Builder integerReq(Integer integerReq) {
            this.integerReq = integerReq;
            return this;
        }

        public Builder integerOpt(Optional<Integer> integerOpt) {
            this.integerOpt = integerOpt;
            return this;
        }

        public Builder decimalReq(BigDecimal decimalReq) {
            this.decimalReq = decimalReq;
            return this;
        }

        public Builder decimalOpt(Optional<BigDecimal> decimalOpt) {
            this.decimalOpt = decimalOpt;
            return this;
        }

        public Builder localDateReq(LocalDate localDateReq) {
            this.localDateReq = localDateReq;
            return this;
        }

        public Builder localDateOpt(Optional<LocalDate> localDateOpt) {
            this.localDateOpt = localDateOpt;
            return this;
        }

        public Builder localDateTimeReq(LocalDateTime localDateTimeReq) {
            this.localDateTimeReq = localDateTimeReq;
            return this;
        }

        public Builder localDateTimeOpt(Optional<LocalDateTime> localDateTimeOpt) {
            this.localDateTimeOpt = localDateTimeOpt;
            return this;
        }

        public Builder localTimeReq(LocalTime localTimeReq) {
            this.localTimeReq = localTimeReq;
            return this;
        }

        public Builder localTimeOpt(Optional<LocalTime> localTimeOpt) {
            this.localTimeOpt = localTimeOpt;
            return this;
        }

        public Builder utcDateTimeReq(ZonedDateTime utcDateTimeReq) {
            this.utcDateTimeReq = utcDateTimeReq;
            return this;
        }

        public Builder utcDateTimeOpt(Optional<ZonedDateTime> utcDateTimeOpt) {
            this.utcDateTimeOpt = utcDateTimeOpt;
            return this;
        }

        public TestRow build() {
            return new TestRow(this);
        }
    }
}
