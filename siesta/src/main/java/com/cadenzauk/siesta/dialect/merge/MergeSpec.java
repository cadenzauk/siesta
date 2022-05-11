/*
 * Copyright (c) 2022 Cadenza United Kingdom Limited
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

package com.cadenzauk.siesta.dialect.merge;

import java.util.List;

public class MergeSpec {
    private final String targetTableName;
    private final String targetAlias;
    private final List<String> columnNames;
    private final List<String> idColumnNames;
    private final List<String> insertColumnNames;
    private final List<String> updateColumnNames;
    private final String sourceAlias;
    private final List<String> selectArgsSql;
    private final List<Object[]> selectArgs;
    private final List<String> insertArgsSql;
    private final List<Object[]> insertArgs;

    private MergeSpec(Builder builder) {
        targetTableName = builder.targetTableName;
        targetAlias = builder.targetAlias;
        columnNames = builder.columnNames;
        idColumnNames = builder.idColumnNames;
        insertColumnNames = builder.insertColumnNames;
        updateColumnNames = builder.updateColumnNames;
        sourceAlias = builder.sourceAlias;
        selectArgsSql = builder.selectArgsSql;
        selectArgs = builder.selectArgs;
        insertArgsSql = builder.insertArgsSql;
        insertArgs = builder.insertArgs;
    }

    public String targetTableName() {
        return targetTableName;
    }

    public String targetAlias() {
        return targetAlias;
    }

    public List<String> columnNames() {
        return columnNames;
    }

    public List<String> idColumnNames() {
        return idColumnNames;
    }

    public List<String> insertColumnNames() {
        return insertColumnNames;
    }

    public List<String> updateColumnNames() {
        return updateColumnNames;
    }

    public String sourceAlias() {
        return sourceAlias;
    }

    public List<String> selectArgsSql() {
        return selectArgsSql;
    }

    public List<Object[]> selectArgs() {
        return selectArgs;
    }

    public List<String> insertArgsSql() {
        return insertArgsSql;
    }

    public List<Object[]> insertArgs() {
        return insertArgs;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private String targetTableName;
        private String targetAlias;
        private List<String> columnNames;
        private List<String> idColumnNames;
        private List<String> insertColumnNames;
        private List<String> updateColumnNames;
        private String sourceAlias;
        private List<String> selectArgsSql;
        private List<Object[]> selectArgs;
        private List<String> insertArgsSql;
        private List<Object[]> insertArgs;

        private Builder() {
        }

        public Builder targetTableName(String val) {
            targetTableName = val;
            return this;
        }

        public Builder targetAlias(String val) {
            targetAlias = val;
            return this;
        }

        public Builder columnNames(List<String> val) {
            columnNames = val;
            return this;
        }

        public Builder idColumnNames(List<String> val) {
            idColumnNames = val;
            return this;
        }

        public Builder insertColumnNames(List<String> val) {
            insertColumnNames = val;
            return this;
        }

        public Builder updateColumnNames(List<String> val) {
            updateColumnNames = val;
            return this;
        }

        public Builder sourceAlias(String val) {
            sourceAlias = val;
            return this;
        }

        public Builder selectArgsSql(List<String> val) {
            selectArgsSql = val;
            return this;
        }

        public Builder selectArgs(List<Object[]> val) {
            selectArgs = val;
            return this;
        }

        public Builder insertArgsSql(List<String> val) {
            insertArgsSql = val;
            return this;
        }

        public Builder insertArgs(List<Object[]> val) {
            insertArgs = val;
            return this;
        }

        public MergeSpec build() {
            return new MergeSpec(this);
        }
    }
}
