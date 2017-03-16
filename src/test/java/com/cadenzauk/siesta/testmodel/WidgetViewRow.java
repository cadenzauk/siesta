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

package com.cadenzauk.siesta.testmodel;

import java.util.Optional;

public class WidgetViewRow {
    private final long widgetId;
    private final String widgetName;
    private final long manufacturerId;
    private final Optional<String> description;
    private final Optional<String> manufacturerName;

    private WidgetViewRow(Builder builder) {
        widgetId = builder.widgetId;
        widgetName = builder.widgetName;
        manufacturerId = builder.manufacturerId;
        description = builder.description;
        manufacturerName = builder.manufacturerName;
    }

    public long widgetId() {
        return widgetId;
    }

    public String widgetName() {
        return widgetName;
    }

    public long manufacturerId() {
        return manufacturerId;
    }

    public Optional<String> description() {
        return description;
    }

    public Optional<String> manufacturerName() {
        return manufacturerName;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private long widgetId;
        private String widgetName;
        private long manufacturerId;
        private Optional<String> description;
        private Optional<String> manufacturerName;

        private Builder() {
        }

        public Builder widgetId(long val) {
            widgetId = val;
            return this;
        }

        public Builder widgetName(String val) {
            widgetName = val;
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

        public Builder manufacturerName(Optional<String> val) {
            manufacturerName = val;
            return this;
        }

        public WidgetViewRow build() {
            return new WidgetViewRow(this);
        }
    }
}
