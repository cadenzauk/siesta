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

package com.cadenzauk.core.sql;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class ForeignKeyName {
    private static final Pattern NAME_PATTERN = Pattern.compile("^(?:(.*)\\.)?([^.]+)$", 0);
    private final QualifiedName table;
    private final String name;

    public ForeignKeyName(QualifiedName table, String name) {
        this.table = Objects.requireNonNull(table);
        this.name = Objects.requireNonNull(name);
    }

    public String toString() {
        return String.format("%s.%s", table, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ForeignKeyName that = (ForeignKeyName) o;

        return new EqualsBuilder()
            .append(table, that.table)
            .append(name, that.name)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(table)
            .append(name)
            .toHashCode();
    }

    public QualifiedName table() {
        return table;
    }

    public String name() {
        return name;
    }

    public Optional<String> catalog() {
        return table.catalog();
    }

    public Optional<String> schema() {
        return table.schema();
    }

    public Optional<String> tableName() {
        return table.name();
    }

    public static ForeignKeyName parseString(String stringRepresentation) {
        Matcher matcher = NAME_PATTERN.matcher(stringRepresentation);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(stringRepresentation + " is not a valid QualifiedName");
        }
        QualifiedName table = StringUtils.isBlank(matcher.group(1))
            ? new QualifiedName("", "", "")
            : QualifiedName.parseString(matcher.group(1));
        return new ForeignKeyName(table, matcher.group(2));
    }
}
