/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta.testmodel;

import com.cadenzauk.siesta.Column;
import com.cadenzauk.siesta.DataType;
import com.cadenzauk.siesta.Table;
import com.cadenzauk.siesta.RowBuilderColumn;

import static com.cadenzauk.siesta.Table.aTable;

public class ManufacturerRow {
    public static final Column<Long, ManufacturerRow> MANUFACTURER_ID = Column.aColumn("MANUFACTURER_ID", DataType.LONG, ManufacturerRow.class);
    public static final Column<String, ManufacturerRow> NAME = Column.aColumn("NAME", DataType.STRING, ManufacturerRow.class);

    public static final Table<ManufacturerRow> TABLE = aTable("TEST", "MANUFACTURER", ManufacturerRow.Builder::new, ManufacturerRow.Builder::build, ManufacturerRow.class)
        .mandatory(MANUFACTURER_ID, ManufacturerRow::manufacturerId, ManufacturerRow.Builder::manufacturerId, RowBuilderColumn.Builder::primaryKey)
        .mandatory(NAME, ManufacturerRow::name, ManufacturerRow.Builder::name)
        .build();

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
