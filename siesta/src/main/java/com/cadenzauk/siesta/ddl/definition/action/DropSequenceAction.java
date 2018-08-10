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

public class DropSequenceAction extends LoggableAction {
    private final Optional<String> catalog;
    private final Optional<String> schemaName;
    private final Optional<String> sequenceName;

    private DropSequenceAction(Builder builder) {
        super(builder);
        catalog = builder.catalog;
        schemaName = builder.schemaName;
        sequenceName = builder.sequenceName;
    }

    public Optional<String> sequenceName() {
        return sequenceName;
    }

    public String qualifiedName(Database database) {
        return database.dialect().qualifiedName(catalog.orElse(""), schemaName.orElse(""), sequenceName.orElse(""));
    }

    public static Builder newBuilder() {
        return new Builder();
    }
    
    public static final class Builder extends LoggableAction.Builder<Builder> {
        private Optional<String> catalog = Optional.empty();
        private Optional<String> schemaName = Optional.empty();
        private Optional<String> sequenceName = Optional.empty();

        public Builder catalog(String val) {
            catalog = OptionalUtil.ofBlankable(val);
            return this;
        }

        public Builder catalog(Optional<String> val) {
            catalog = val.filter(StringUtils::isNotBlank);
            return this;
        }

        public Builder schemaName(String val) {
            schemaName = OptionalUtil.ofBlankable(val);
            return this;
        }

        public Builder schemaName(Optional<String> val) {
            schemaName = val.filter(StringUtils::isNotBlank);
            return this;
        }

        public Builder sequenceName(String val) {
            sequenceName = OptionalUtil.ofBlankable(val);
            return this;
        }

        public Builder sequenceName(Optional<String> val) {
            sequenceName = val.filter(StringUtils::isNotBlank);
            return this;
        }

        public DropSequenceAction build() {
            return new DropSequenceAction(this);
        }
    }
}
