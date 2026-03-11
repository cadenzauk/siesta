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

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

@Table(name = "SALESPERSON", schema = "SIESTA")
public class NonUpdateableSalespersonRow {
    @Id
    private final long salespersonId;
    @Column(updatable = false)
    private final String firstName;
    @Column(updatable = false)
    private final Optional<String> middleNames;
    @Column(updatable = false)
    private final String surname;
    @Column(updatable = false)
    private final int numberOfSales;
    @Column(updatable = false)
    private final Optional<BigDecimal> commission;

    private NonUpdateableSalespersonRow(Builder builder) {
        salespersonId = builder.salespersonId;
        firstName = builder.firstName;
        middleNames = builder.middleNames;
        surname = builder.surname;
        numberOfSales = builder.numberOfSales;
        commission = builder.commission;
    }

    public NonUpdateableSalespersonRow(SalespersonRow salespersonRow) {
        salespersonId = salespersonRow.salespersonId();
        firstName = salespersonRow.firstName();
        middleNames = salespersonRow.middleNames();
        surname = salespersonRow.surname();
        numberOfSales = salespersonRow.numberOfSales();
        commission = salespersonRow.commission();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NonUpdateableSalespersonRow that = (NonUpdateableSalespersonRow) o;
        return salespersonId == that.salespersonId &&
            numberOfSales == that.numberOfSales &&
            Objects.equals(firstName, that.firstName) &&
            Objects.equals(middleNames, that.middleNames) &&
            Objects.equals(surname, that.surname) &&
            Objects.equals(commission, that.commission);
    }

    @Override
    public String toString() {
        return "NonUpdateableSalespersonRow{" +
            "salespersonId=" + salespersonId +
            ", firstName='" + firstName + '\'' +
            ", middleNames=" + middleNames +
            ", surname='" + surname + '\'' +
            ", numberOfSales=" + numberOfSales +
            ", commission=" + commission +
            '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(salespersonId, firstName, middleNames, surname, numberOfSales, commission);
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

    public static Builder newBuilder(NonUpdateableSalespersonRow copy) {
        Builder builder = new Builder();
        builder.salespersonId = copy.salespersonId();
        builder.firstName = copy.firstName();
        builder.middleNames = copy.middleNames();
        builder.surname = copy.surname();
        builder.numberOfSales = copy.numberOfSales();
        builder.commission = copy.commission();
        return builder;
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

        public NonUpdateableSalespersonRow build() {
            return new NonUpdateableSalespersonRow(this);
        }
    }
}
