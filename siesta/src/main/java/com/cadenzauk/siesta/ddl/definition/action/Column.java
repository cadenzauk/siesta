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

import java.util.Optional;
import java.util.function.Function;

public class Column {
    private final String columnName;
    private final ColumnDataType<?> dataType;
    private final boolean nullable;
    private final boolean primaryKey;
    private final Optional<AddForeignKeyAction> foreignKey;

    private Column(Optional<String> catalog, Optional<String> schemaName, String tableName, Builder builder) {
        columnName = builder.columnName;
        dataType = builder.dataType;
        nullable = builder.nullable;
        primaryKey = builder.primaryKey;
        foreignKey = builder.foreignKey
            .map(fk -> AddForeignKeyAction.newBuilder()
                .logged(false)
                .catalog(catalog)
                .schemaName(schemaName)
                .tableName(tableName)
                .column(builder.columnName)
                .accept(fk)
                .build());
    }

    public ColumnDataType<?> dataType() {
        return dataType;
    }

    public boolean nullable() {
        return nullable;
    }

    public boolean primaryKey() {
        return primaryKey;
    }

    public String columnName() {
        return columnName;
    }

    public Optional<AddForeignKeyAction> foreignKey() {
        return foreignKey;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String columnName;
        private ColumnDataType<?> dataType;
        private boolean nullable = true;
        private boolean primaryKey = false;
        private Optional<Function<AddForeignKeyAction.Builder,AddForeignKeyAction.Builder>> foreignKey = Optional.empty();

        private Builder() {
        }

        public Builder columnName(String val) {
            columnName = val;
            return this;
        }

        public <T> Builder dataType(ColumnDataType<T> val) {
            dataType = val;
            return this;
        }

        private Builder notNull() {
            nullable = false;
            return this;
        }

        private Builder primaryKey() {
            primaryKey = true;
            return this;
        }

        private Builder foreignKey(Function<AddForeignKeyAction.Builder,AddForeignKeyAction.Builder> val) {
            foreignKey = Optional.of(val);
            return this;
        }

        public Column build(Optional<String> catalog, Optional<String> schemaName, String tableName) {
            return new Column(catalog, schemaName, tableName, this);
        }
    }

    public static class Constraints {
        public static Function<Builder,Builder> notNull() {
            return Builder::notNull;
        }

        public static Function<Builder,Builder> primaryKey() {
            return Builder::primaryKey;
        }

        public static ForeignKeyInit foreignKey(String constraintName) {
            return new ForeignKeyInit(constraintName);
        }
    }

    public static class ForeignKeyInit {
        private final String constraintName;

        private ForeignKeyInit(String constraintName) {
            this.constraintName = constraintName;
        }

        public ForeignKeyBuilder references(String referencedTable) {
            return new ForeignKeyBuilder(constraintName, referencedTable);
        }
    }

    public static class ForeignKeyBuilder {
        private final String constraintName;
        private final String referencedTable;

        private ForeignKeyBuilder(String constraintName, String referencedTable) {
            this.constraintName = constraintName;
            this.referencedTable = referencedTable;
        }

        public Function<Builder,Builder> column(String referencedColumn) {
            return c -> c.foreignKey(f -> f
                .constraintName(constraintName)
                .referencedTableName(referencedTable)
                .referencedColumn(referencedColumn));
        }
    }
}
