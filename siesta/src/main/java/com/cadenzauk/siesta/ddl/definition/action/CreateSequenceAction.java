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

package com.cadenzauk.siesta.ddl.definition.action;

import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.ddl.action.LoggableAction;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

import static com.cadenzauk.siesta.ddl.definition.action.ColumnDataType.integer;

public class CreateSequenceAction extends LoggableAction {
    private final Optional<String> catalog;
    private final Optional<String> schemaName;
    private final String sequenceName;
    private final ColumnDataType<?> dataType;
    private final Optional<Boolean> cycle;
    private final Optional<Boolean> ordered;
    private final Optional<Long> startValue;
    private final Optional<Long> minValue;
    private final Optional<Long> maxValue;
    private final Optional<Long> incrementBy;

    private CreateSequenceAction(Builder builder) {
        super(builder);
        catalog = builder.catalog;
        schemaName = builder.schemaName;
        sequenceName = builder.sequenceName
            .filter(StringUtils::isNotBlank)
            .orElseThrow(() -> new IllegalArgumentException("sequenceName is required."));
        dataType = builder.dataType;
        cycle = builder.cycle;
        ordered = builder.ordered;
        startValue = builder.startValue;
        minValue = builder.minValue;
        maxValue = builder.maxValue;
        incrementBy = builder.incrementBy;
    }

    public ColumnDataType<?> dataType() {
        return dataType;
    }

    public Optional<Boolean> cycle() {
        return cycle;
    }

    public Optional<Boolean> ordered() {
        return ordered;
    }

    public Optional<Long> startValue() {
        return startValue;
    }

    public Optional<Long> minValue() {
        return minValue;
    }

    public Optional<Long> maxValue() {
        return maxValue;
    }

    public Optional<Long> incrementBy() {
        return incrementBy;
    }

    public String qualifiedName(Database database) {
        return database.dialect().qualifiedSequenceName(catalog.orElse(""), schemaName.orElse(""), sequenceName);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @SuppressWarnings("UnusedReturnValue")
    public static final class Builder extends LoggableAction.Builder<Builder> {
        private ColumnDataType<?> dataType = integer();
        private Optional<Boolean> cycle = Optional.empty();
        private Optional<Boolean> ordered = Optional.empty();
        private Optional<Long> startValue = Optional.empty();
        private Optional<Long> minValue = Optional.empty();
        private Optional<Long> maxValue = Optional.empty();
        private Optional<Long> incrementBy = Optional.empty();
        private Optional<String> catalog = Optional.empty();
        private Optional<String> schemaName = Optional.empty();
        private Optional<String> sequenceName = Optional.empty();

        public Builder dataType(ColumnDataType<?> val) {
            dataType = val;
            return this;
        }

        public Builder catalog(String val) {
            catalog = OptionalUtil.ofBlankable(val);
            return this;
        }

        public Builder catalog(Optional<String> val) {
            catalog = val;
            return this;
        }

        public Builder schemaName(String val) {
            schemaName = OptionalUtil.ofBlankable(val);
            return this;
        }

        public Builder sequenceName(String val) {
            sequenceName = OptionalUtil.ofBlankable(val);
            return this;
        }

        public Builder sequenceName(Optional<String> val) {
            sequenceName = val;
            return this;
        }

        public Builder cycle(boolean val) {
            cycle = Optional.of(val);
            return this;
        }

        public Builder ordered(boolean val) {
            ordered = Optional.of(val);
            return this;
        }

        public Builder startValue(long val) {
            startValue = Optional.of(val);
            return this;
        }

        public Builder minValue(long val) {
            minValue = Optional.of(val);
            return this;
        }

        public Builder maxValue(long val) {
            maxValue = Optional.of(val);
            return this;
        }

        public Builder incrementBy(long val) {
            incrementBy = Optional.of(val);
            return this;
        }

        public CreateSequenceAction build() {
            return new CreateSequenceAction(this);
        }
    }
}
