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

import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import java.util.Optional;

@MappedSuperclass
@Table(name = "PART", schema = "SIESTA")
public class PartRow {
    @Id
    private final long partId;
    private final long widgetId;
    private final String description;
    @Embedded
    private final MoneyAmount purchasePrice;
    @Embedded
    private final Optional<MoneyAmount> retailPrice;

    PartRow(Builder builder) {
        partId = builder.partId;
        widgetId = builder.widgetId;
        description = builder.description;
        purchasePrice = builder.purchasePrice;
        retailPrice = builder.retailPrice;
    }

    public long partId() {
        return partId;
    }

    public long widgetId() {
        return widgetId;
    }

    public String description() {
        return description;
    }

    public MoneyAmount purchasePrice() {
        return purchasePrice;
    }

    public Optional<MoneyAmount> retailPrice() {
        return retailPrice;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private long partId;
        private long widgetId;
        private String description;
        private MoneyAmount purchasePrice;
        private Optional<MoneyAmount> retailPrice;

        Builder() {
        }

        public Builder partId(long val) {
            partId = val;
            return this;
        }

        public Builder widgetId(long val) {
            widgetId = val;
            return this;
        }

        public Builder description(String val) {
            description = val;
            return this;
        }

        public Builder purchasePrice(MoneyAmount val) {
            purchasePrice = val;
            return this;
        }

        public Builder retailPrice(Optional<MoneyAmount> val) {
            retailPrice = val;
            return this;
        }

        public PartRow build() {
            return new PartRow(this);
        }
    }
}
