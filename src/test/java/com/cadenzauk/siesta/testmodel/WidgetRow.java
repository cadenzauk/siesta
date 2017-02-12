/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta.testmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Table;
import java.util.Optional;

@Table(name = "WIDGET", schema = "TEST")
public class WidgetRow {
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
