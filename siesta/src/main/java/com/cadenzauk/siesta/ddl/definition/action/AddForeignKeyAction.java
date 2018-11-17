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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class AddForeignKeyAction extends LoggableAction {
    private final String constraintName;
    private final Optional<String> catalog;
    private final Optional<String> schemaName;
    private final String tableName;
    private final String columnNames;
    private final Optional<String> referencedCatalog;
    private final Optional<String> referencedSchemaName;
    private final String referencedTableName;
    private final String referencedColumnNames;

    private AddForeignKeyAction(Builder builder) {
        super(builder);
        constraintName = builder.constraintName
            .filter(StringUtils::isNotBlank)
            .orElseThrow(() -> new IllegalArgumentException("constraintName is required"));
        catalog = builder.catalog;
        schemaName = builder.schemaName;
        tableName = builder.tableName
            .filter(StringUtils::isNotBlank)
            .orElseThrow(() -> new IllegalArgumentException("tableName is required"));
        columnNames = String.join(", ", builder.columnNames);
        referencedCatalog = OptionalUtil.or(builder.referencedCatalog, builder.catalog);
        referencedSchemaName = OptionalUtil.or(builder.referencedSchemaName, builder.schemaName);
        referencedTableName = builder.referencedTableName
            .filter(StringUtils::isNotBlank)
            .orElseThrow(() -> new IllegalArgumentException("referencedTableName is required"));
        referencedColumnNames = String.join(", ", builder.referencedColumnNames);
    }

    public String tableName() {
        return tableName;
    }

    public String qualifiedTableName(Database database) {
        return database.dialect().qualifiedTableName(catalog.orElse(""), schemaName.orElse(""), tableName);
    }

    public String constraintName() {
        return constraintName;
    }

    public String columnNames() {
        return columnNames;
    }

    public String qualifiedReferencedTableName(Database database) {
        return database.dialect().qualifiedTableName(referencedCatalog.orElse(""), referencedSchemaName.orElse(""), referencedTableName);
    }

    public String referencedColumnNames() {
        return referencedColumnNames;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder extends LoggableAction.Builder<Builder> {
        private Optional<String> catalog = Optional.empty();
        private Optional<String> schemaName = Optional.empty();
        private Optional<String> tableName = Optional.empty();
        private Optional<String> constraintName = Optional.empty();
        private final List<String> columnNames = new ArrayList<>();
        private Optional<String> referencedCatalog = Optional.empty();
        private Optional<String> referencedSchemaName = Optional.empty();
        private Optional<String> referencedTableName = Optional.empty();
        private final List<String> referencedColumnNames = new ArrayList<>();

        public Builder constraintName(String val) {
            constraintName = OptionalUtil.ofBlankable(val);
            return this;
        }

        public Builder constraintName(Optional<String> val) {
            constraintName = val.filter(StringUtils::isNotBlank);
            return this;
        }

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

        public Builder tableName(String val) {
            tableName = OptionalUtil.ofBlankable(val);
            return this;
        }

        public Builder tableName(Optional<String> val) {
            tableName = val.filter(StringUtils::isNotBlank);
            return this;
        }

        public Builder column(String val) {
            columnNames.add(val);
            return this;
        }

        public Builder referencedCatalog(String val) {
            referencedCatalog = OptionalUtil.ofBlankable(val);
            return this;
        }

        public Builder referencedCatalog(Optional<String> val) {
            referencedCatalog = val.filter(StringUtils::isNotBlank);
            return this;
        }

        public Builder referencedSchemaName(String val) {
            referencedSchemaName = OptionalUtil.ofBlankable(val);
            return this;
        }

        public Builder referencedSchemaName(Optional<String> val) {
            referencedSchemaName = val.filter(StringUtils::isNotBlank);
            return this;
        }

        public Builder referencedTableName(String val) {
            referencedTableName = OptionalUtil.ofBlankable(val);
            return this;
        }

        public Builder referencedTableName(Optional<String> val) {
            referencedTableName = val.filter(StringUtils::isNotBlank);
            return this;
        }

        public Builder referencedColumn(String val) {
            referencedColumnNames.add(val);
            return this;
        }

        public Builder accept(Function<Builder,Builder> fk) {
            return fk.apply(this);
        }

        public AddForeignKeyAction build() {
            return new AddForeignKeyAction(this);
        }
    }
}
