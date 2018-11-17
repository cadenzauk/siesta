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

package com.cadenzauk.siesta.model;

import javax.persistence.Column;
import javax.persistence.Table;
import java.time.ZonedDateTime;
import java.util.Optional;

@Table(name = "SALES_AREA")
public class SalesAreaRow {
    private final long salesAreaId;
    private final String salesAreaName;
    private final Optional<Long> salespersonId;
    private final Optional<Long> salesCount;
    @Column(insertable = false, updatable = false)
    private Optional<ZonedDateTime> insertTime;

    protected SalesAreaRow(Builder builder) {
        salesAreaId = builder.salesAreaId;
        salesAreaName = builder.salesAreaName;
        salespersonId = builder.salespersonId;
        salesCount = builder.salesCount;
    }

    public long salesAreaId() {
        return salesAreaId;
    }

    public String salesAreaName() {
        return salesAreaName;
    }

    public Optional<Long> salespersonId() {
        return salespersonId;
    }

    public Optional<Long> salesCount() {
        return salesCount;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private long salesAreaId;
        private String salesAreaName;
        private Optional<Long> salespersonId;
        private Optional<Long> salesCount;

        protected Builder() {
        }

        public Builder salesAreaId(long val) {
            salesAreaId = val;
            return this;
        }

        public Builder salesAreaName(String val) {
            salesAreaName = val;
            return this;
        }

        public Builder salespersonId(Optional<Long> val) {
            salespersonId = val;
            return this;
        }

        public Builder salesCount(Optional<Long> val) {
            salesCount = val;
            return this;
        }

        public SalesAreaRow build() {
            return new SalesAreaRow(this);
        }
    }
}
