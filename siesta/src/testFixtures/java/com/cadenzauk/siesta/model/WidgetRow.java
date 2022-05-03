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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Optional;

@Table(name = "WIDGET", schema = "SIESTA")
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
        private Optional<String> description = Optional.empty();

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
