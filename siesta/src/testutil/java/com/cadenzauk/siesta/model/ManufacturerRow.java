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
import java.time.LocalDate;
import java.util.Optional;

@Table(name = "MANUFACTURER", schema = "SIESTA")
public class ManufacturerRow {
    private final long manufacturerId;
    private final Optional<String> name;
    private final Optional<LocalDate> checked;

    private ManufacturerRow(Builder builder) {
        manufacturerId = builder.manufacturerId;
        name = builder.name;
        checked = builder.checked;
    }

    public long manufacturerId() {
        return manufacturerId;
    }

    public Optional<String> name() {
        return name;
    }

    public Optional<LocalDate> checked() {
        return checked;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private long manufacturerId;
        private Optional<String> name = Optional.empty();
        private Optional<LocalDate> checked = Optional.empty();

        private Builder() {
        }

        public Builder manufacturerId(long val) {
            manufacturerId = val;
            return this;
        }

        public Builder name(Optional<String> val) {
            name = val;
            return this;
        }

        public Builder checked(Optional<LocalDate> val) {
            checked = val;
            return this;
        }

        public ManufacturerRow build() {
            return new ManufacturerRow(this);
        }
    }
}
