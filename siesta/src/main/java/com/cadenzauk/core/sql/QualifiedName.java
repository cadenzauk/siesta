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

import com.cadenzauk.core.stream.StreamUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class QualifiedName {
    private static final Pattern NAME_PATTERN = Pattern.compile("^([^.]*)(?:\\.([^.]*)(?:\\.([^.]*))?)?$", 0);
    private final Optional<String> catalog;
    private final Optional<String> schema;
    private final Optional<String> name;

    public QualifiedName(String catalog, String schema, String name) {
        this(Optional.ofNullable(catalog), Optional.ofNullable(schema), Optional.ofNullable(name));
    }

    public QualifiedName(Optional<String> catalog, Optional<String> schema, Optional<String> name) {
        this.catalog = catalog.filter(StringUtils::isNotBlank).map(String::trim);
        this.schema = schema.filter(StringUtils::isNotBlank).map(String::trim);
        this.name = name.filter(StringUtils::isNotBlank).map(String::trim);
    }

    @Override
    public String toString() {
        return Stream.of(catalog, schema, name)
            .flatMap(StreamUtil::of)
            .collect(joining("."));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        QualifiedName that = (QualifiedName) o;

        return new EqualsBuilder()
            .append(catalog, that.catalog)
            .append(schema, that.schema)
            .append(name, that.name)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(catalog)
            .append(schema)
            .append(name)
            .toHashCode();
    }

    public Optional<String> catalog() {
        return catalog;
    }

    public Optional<String> schema() {
        return schema;
    }

    public Optional<String> name() {
        return name;
    }

    public static Predicate<QualifiedName> matchesCatalogAndSchema(String catalog, String schema) {
        return qn -> matchesOrBlank(qn.catalog, catalog) && matchesOrBlank(qn.schema, schema);
    }

    public static Predicate<QualifiedName> matchesName(String name) {
        return qn -> matchesOrBlank(qn.name, name);
    }

    private static boolean matchesOrBlank(Optional<String> candidate, String value) {
        String candidateValue = candidate.orElse("");
        return StringUtils.equalsIgnoreCase(candidateValue, value) || isBlank(candidateValue) || isBlank(value);
    }

    public static QualifiedName parseString(String stringRepresentation) {
        Matcher matcher = NAME_PATTERN.matcher(stringRepresentation);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(stringRepresentation + " is not a valid QualifiedName");
        }

        int n = (int) IntStream.range(1, matcher.groupCount() + 1).mapToObj(matcher::group).filter(Objects::nonNull).count();
        String catalog = n == 3 ? matcher.group(1) : "";
        String schema = n > 1 ? matcher.group(n - 1) : "";
        String table = matcher.group(n);
        return new QualifiedName(catalog, schema, table);
    }
}
