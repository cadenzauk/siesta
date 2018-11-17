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
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CreateIndexAction extends LoggableAction {
    private final boolean unique;
    private final Optional<String> catalog;
    private final Optional<String> schemaName;
    private final String tableName;
    private final String indexName;
    private final List<String> columnNames;

    private CreateIndexAction(Builder builder) {
        super(builder);
        unique = builder.unique;
        catalog = builder.catalog;
        schemaName = builder.schemaName;
        tableName = builder.tableName
            .filter(StringUtils::isNotBlank)
            .orElseThrow(() -> new IllegalArgumentException("tableName is required."));
        indexName = builder.indexName
            .filter(StringUtils::isNotBlank)
            .orElseThrow(() -> new IllegalArgumentException("indexName is required."));
        columnNames = ImmutableList.copyOf(builder.columnNames);
    }

    public boolean unique() {
        return unique;
    }

    public String qualifiedIndexName(Database database) {
        return database.dialect().qualifiedIndexName(catalog.orElse(""), schemaName.orElse(""), indexName);
    }

    public String qualifiedTableName(Database database) {
        return database.dialect().qualifiedTableName(catalog.orElse(""), schemaName.orElse(""), tableName);
    }

    public List<String> columnNames() {
        return columnNames;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @SuppressWarnings("UnusedReturnValue")
    public static final class Builder extends LoggableAction.Builder<Builder> {
        private boolean unique;
        private Optional<String> catalog = Optional.empty();
        private Optional<String> schemaName = Optional.empty();
        private Optional<String> tableName = Optional.empty();
        private Optional<String> indexName = Optional.empty();
        private final List<String> columnNames = new ArrayList<>();

        private Builder() {
        }

        public Builder unique() {
            unique = true;
            return this;
        }

        public Builder unique(boolean val) {
            unique = val;
            return this;
        }

        public Builder catalog(String val) {
            catalog = OptionalUtil.ofBlankable(val);
            return this;
        }

        public Builder schemaName(String val) {
            schemaName = OptionalUtil.ofBlankable(val);
            return this;
        }

        public Builder tableName(String val) {
            tableName = OptionalUtil.ofBlankable(val);
            return this;
        }

        public Builder indexName(String val) {
            indexName = OptionalUtil.ofBlankable(val);
            return this;
        }

        public Builder column(String val) {
            columnNames.add(val);
            return this;
        }

        public Builder columns(String... val) {
            columnNames.addAll(Arrays.asList(val));
            return this;
        }

        public CreateIndexAction build() {
            return new CreateIndexAction(this);
        }
    }
}
