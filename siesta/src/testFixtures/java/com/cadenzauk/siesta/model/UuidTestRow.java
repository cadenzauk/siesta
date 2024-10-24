/*
 * Copyright (c) 2024 Cadenza United Kingdom Limited
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

import org.apache.commons.lang3.RandomStringUtils;

import javax.persistence.Table;
import java.util.Objects;
import java.util.UUID;

@Table(name = "UUID_TEST_TABLE", schema = "SIESTA")
public class UuidTestRow {
    private final UUID guid;

    private final String textValue;

    private UuidTestRow(Builder builder) {
        guid = builder.guid;
        textValue = builder.textValue;
    }

    public UUID guid() {
        return guid;
    }

    public String textValue() {
        return textValue;
    }

    @Override
    public String toString() {
        return "UuidTestRow{" +
            "guid=" + guid +
            ", textValue='" + textValue + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UuidTestRow that = (UuidTestRow) o;
        return Objects.equals(guid, that.guid) && Objects.equals(textValue, that.textValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guid, textValue);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID guid;
        private String textValue;

        private Builder() {
            this.guid = UUID.randomUUID();
            this.textValue = RandomStringUtils.randomAlphabetic(10, 20);
        }

        public Builder guid(UUID guid) {
            this.guid = guid;
            return this;
        }

        public Builder textValue(String textValue) {
            this.textValue = textValue;
            return this;
        }

        public UuidTestRow build() {
            return new UuidTestRow(this);
        }
    }
}
