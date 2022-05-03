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
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AddForeignKeyAction extends LoggableAction {
    private final String constraintName;
    private final Optional<Function<Database,String>> catalog;
    private final Optional<Function<Database,String>> schemaName;
    private final Function<Database,String> tableName;
    private final List<Function<Database,String>> columnNames;
    private final Optional<Function<Database,String>> referencedCatalog;
    private final Optional<Function<Database,String>> referencedSchemaName;
    private final Function<Database,String> referencedTableName;
    private final List<Function<Database,String>> referencedColumnNames;

    private AddForeignKeyAction(Builder builder) {
        super(builder);
        constraintName = builder.constraintName
            .filter(StringUtils::isNotBlank)
            .orElseThrow(() -> new IllegalArgumentException("constraintName is required"));
        catalog = builder.catalog;
        schemaName = builder.schemaName;
        tableName = builder.tableName
            .orElseThrow(() -> new IllegalArgumentException("tableName is required"));
        columnNames = builder.columnNames;
        referencedCatalog = OptionalUtil.or(builder.referencedCatalog, builder.catalog);
        referencedSchemaName = OptionalUtil.or(builder.referencedSchemaName, builder.schemaName);
        referencedTableName = builder.referencedTableName
            .orElseThrow(() -> new IllegalArgumentException("referencedTableName is required"));
        referencedColumnNames = ImmutableList.copyOf(builder.referencedColumnNames);
    }

    public String tableName(Database database) {
        return tableName.apply(database);
    }

    public String qualifiedTableName(Database database) {
        return database.dialect().qualifiedTableName(catalog.map(c -> c.apply(database)).orElse(""), schemaName.map(s -> s.apply(database)).orElse(""), tableName.apply(database));
    }

    public String constraintName() {
        return constraintName;
    }

    public String columnNames(Database database) {
        return columnNames.stream().map(c -> c.apply(database)).collect(Collectors.joining(", "));
    }

    public String qualifiedReferencedTableName(Database database) {
        return database.dialect().qualifiedTableName(
            referencedCatalog.map(cat -> cat.apply(database)).orElse(""),
            referencedSchemaName.map(schema -> schema.apply(database)).orElse(""),
            referencedTableName.apply(database));
    }

    public String referencedColumnNames(Database database) {
        return referencedColumnNames.stream().map(c -> c.apply(database)).collect(Collectors.joining(", "));
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder extends LoggableAction.Builder<Builder> {
        private Optional<Function<Database,String>> catalog = Optional.empty();
        private Optional<Function<Database,String>> schemaName = Optional.empty();
        private Optional<Function<Database,String>> tableName = Optional.empty();
        private Optional<String> constraintName = Optional.empty();
        private final List<Function<Database,String>> columnNames = new ArrayList<>();
        private Optional<Function<Database,String>> referencedCatalog = Optional.empty();
        private Optional<Function<Database,String>> referencedSchemaName = Optional.empty();
        private Optional<Function<Database,String>> referencedTableName = Optional.empty();
        private final List<Function<Database,String>> referencedColumnNames = new ArrayList<>();

        public Builder constraintName(String val) {
            constraintName = OptionalUtil.ofBlankable(val);
            return this;
        }

        public Builder constraintName(Optional<String> val) {
            constraintName = val.filter(StringUtils::isNotBlank);
            return this;
        }

        public Builder catalog(String val) {
            catalog = OptionalUtil.ofBlankable(val).map(name -> d -> name);
            return this;
        }

        public Builder catalog(Optional<Function<Database, String>> val) {
            catalog = val;
            return this;
        }

        public Builder schemaName(String val) {
            schemaName = OptionalUtil.ofBlankable(val).map(name -> d -> name);;
            return this;
        }

        public Builder schemaName(Optional<Function<Database, String>> val) {
            schemaName = val;
            return this;
        }

        public Builder tableName(String val) {
            tableName = OptionalUtil.ofBlankable(val).map(name -> d -> name);
            return this;
        }

        public Builder tableName(Optional<String> val) {
            tableName = val.filter(StringUtils::isNotBlank).map(name -> d -> name);;
            return this;
        }

        public Builder tableName(Function<Database, String> val) {
            tableName = Optional.of(val);
            return this;
        }

        public Builder column(Function<Database,String> val) {
            columnNames.add(val);
            return this;
        }

        public Builder referencedCatalog(String val) {
            referencedCatalog = OptionalUtil.ofBlankable(val).map(n -> d -> n);
            return this;
        }

        public Builder referencedCatalog(Optional<Function<Database,String>> val) {
            referencedCatalog = val;
            return this;
        }

        public Builder referencedSchemaName(String val) {
            referencedSchemaName = OptionalUtil.ofBlankable(val).map(n -> d -> n);
            return this;
        }

        public Builder referencedSchemaName(Optional<Function<Database,String>> val) {
            referencedSchemaName = val;
            return this;
        }

        public Builder referencedTableName(String val) {
            referencedTableName = OptionalUtil.ofBlankable(val).map(n -> d -> n);
            return this;
        }

        public Builder referencedTableName(Optional<Function<Database,String>> val) {
            referencedTableName = val;
            return this;
        }

        public Builder referencedColumn(String val) {
            referencedColumnNames.add(d -> val);
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
