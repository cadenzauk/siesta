/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta.testmodel;

import com.cadenzauk.siesta.Column;
import com.cadenzauk.siesta.DataType;
import com.cadenzauk.siesta.Table;
import com.cadenzauk.siesta.TableColumn;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Optional;

import static com.cadenzauk.siesta.Table.aTable;

public class WidgetRow {
    public static final Column<Long,WidgetRow> WIDGET_ID = Column.aColumn("WIDGET_ID", DataType.LONG, WidgetRow.class);
    public static final Column<String,WidgetRow> NAME = Column.aColumn("NAME", DataType.STRING, WidgetRow.class);
    public static final Column<Long,WidgetRow> MANUFACTURER_ID = Column.aColumn("MANUFACTURER_ID", DataType.LONG, WidgetRow.class);
    public static final Column<String,WidgetRow> DESCRIPTION = Column.aColumn("DESCRIPTION", DataType.STRING, WidgetRow.class);

    public static final Table<WidgetRow,Builder> TABLE = aTable("TEST", "WIDGET", Builder::new, Builder::build, WidgetRow.class)
        .mandatory(WIDGET_ID, WidgetRow::widgetId, Builder::widgetId, TableColumn.Builder::primaryKey)
        .mandatory(NAME, WidgetRow::name, Builder::name)
        .mandatory(MANUFACTURER_ID, WidgetRow::manufacturerId, Builder::manufacturerId)
        .optional(DESCRIPTION, WidgetRow::description, Builder::description)
        .build();

    private final long widgetId;
    private final String name;
    private final long manufacturerId;
    private final Optional<String> description;

    private WidgetRow(Builder builder) {
        widgetId = builder.widgetId;
        name = builder.name;
        manufacturerId = builder.manufacturerId;
        description = builder.description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        WidgetRow widgetRow = (WidgetRow) o;

        return new EqualsBuilder()
            .append(widgetId, widgetRow.widgetId)
            .append(name, widgetRow.name)
            .append(description, widgetRow.description)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(widgetId)
            .append(name)
            .append(description)
            .toHashCode();
    }

    public long widgetId() {
        return widgetId;
    }

    public String name() {
        return name;
    }

    public long manufacturerId() {
        return manufacturerId;
    }

    public Optional<String> description() {
        return description;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private long widgetId;
        private String name;
        private long manufacturerId;
        private Optional<String> description;

        private Builder() {
        }

        public Builder widgetId(long val) {
            widgetId = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder manufacturerId(long val) {
            manufacturerId = val;
            return this;
        }

        public Builder description(Optional<String> val) {
            description = val;
            return this;
        }

        public WidgetRow build() {
            return new WidgetRow(this);
        }
    }
}
