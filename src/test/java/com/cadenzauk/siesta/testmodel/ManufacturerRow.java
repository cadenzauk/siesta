/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta.testmodel;

import javax.persistence.Table;

@Table(name = "MANUFACTURER", schema = "TEST")
public class ManufacturerRow {
    private final long manufacturerId;
    private final String name;

    private ManufacturerRow(Builder builder) {
        manufacturerId = builder.manufacturerId;
        name = builder.name;
    }

    public long manufacturerId() {
        return manufacturerId;
    }

    public String name() {
        return name;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private long manufacturerId;
        private String name;

        private Builder() {
        }

        public Builder manufacturerId(long val) {
            manufacturerId = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public ManufacturerRow build() {
            return new ManufacturerRow(this);
        }
    }
}
