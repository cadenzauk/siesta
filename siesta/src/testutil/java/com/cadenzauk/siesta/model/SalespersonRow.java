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

import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Optional;

@Table(name = "SALESPERSON", schema = "SIESTA")
public class SalespersonRow {
    private final long salespersonId;
    private final String firstName;
    private final Optional<String> middleNames;
    private final String surname;
    private final int numberOfSales;
    private final Optional<BigDecimal> commission;

    private SalespersonRow(Builder builder) {
        salespersonId = builder.salespersonId;
        firstName = builder.firstName;
        middleNames = builder.middleNames;
        surname = builder.surname;
        numberOfSales = builder.numberOfSales;
        commission = builder.commission;
    }

    public long salespersonId() {
        return salespersonId;
    }

    public String firstName() {
        return firstName;
    }

    public Optional<String> middleNames() {
        return middleNames;
    }

    public String surname() {
        return surname;
    }

    public int numberOfSales() {
        return numberOfSales;
    }

    public Optional<BigDecimal> commission() {
        return commission;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private long salespersonId;
        private String firstName;
        private Optional<String> middleNames = Optional.empty();
        private String surname;
        private int numberOfSales;
        private Optional<BigDecimal> commission = Optional.empty();

        private Builder() {
        }

        public Builder salespersonId(long val) {
            salespersonId = val;
            return this;
        }

        public Builder firstName(String val) {
            firstName = val;
            return this;
        }

        public Builder middleNames(Optional<String> val) {
            middleNames = val;
            return this;
        }

        public Builder surname(String val) {
            surname = val;
            return this;
        }

        public Builder numberOfSales(int val) {
            numberOfSales = val;
            return this;
        }

        public Builder commission(Optional<BigDecimal> val) {
            commission = val;
            return this;
        }

        public SalespersonRow build() {
            return new SalespersonRow(this);
        }
    }
}
