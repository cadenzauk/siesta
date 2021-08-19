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

@Table(name = "SALES_AREA")
public class IncorrectSalesAreaRow extends SalesAreaRow {
    private final String nonexistantColumn;
    @Column(name = "%")
    private final String badColumnName;

    private IncorrectSalesAreaRow(Builder builder) {
        super(builder);
        nonexistantColumn = builder.nonexistantColumn;
        badColumnName = builder.badColumnName;
    }

    public String nonexistantColumn() {
        return nonexistantColumn;
    }

    public String badColumnName() {
        return badColumnName;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder extends SalesAreaRow.Builder {
        private String nonexistantColumn;
        private String badColumnName;

        private Builder() {
        }

        public IncorrectSalesAreaRow build() {
            return new IncorrectSalesAreaRow(this);
        }

        public Builder nonexistantColumn(String val) {
            nonexistantColumn = val;
            return this;
        }

        public Builder badColumnName(String val) {
            badColumnName = val;
            return this;
        }
    }
}
