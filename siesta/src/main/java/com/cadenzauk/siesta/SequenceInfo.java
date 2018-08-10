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

package com.cadenzauk.siesta;

public class SequenceInfo {
    private final String listSql;
    private final boolean supportsStartValue;

    protected SequenceInfo(Builder builder) {
        listSql = builder.listSql;
        supportsStartValue = builder.supportsStartValue;
    }

    public String listSql() {
        return listSql;
    }

    public boolean supportsStartValue() {
        return supportsStartValue;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private String listSql = "SELECT SEQUENCE_CATALOG, SEQUENCE_SCHEMA, SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES";
        private boolean supportsStartValue = true;

        private Builder() {
        }

        public Builder listSql(String val) {
            listSql = val;
            return this;
        }

        public Builder supportsStartValue(boolean val) {
            supportsStartValue = val;
            return this;
        }

        public SequenceInfo build() {
            return new SequenceInfo(this);
        }
    }
}
