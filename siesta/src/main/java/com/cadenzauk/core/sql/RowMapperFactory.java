/*
 * Copyright (c) 2019 Cadenza United Kingdom Limited
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

import java.util.Optional;

import static com.cadenzauk.core.util.OptionalUtil.or;

@FunctionalInterface
public interface RowMapperFactory<T> {
    RowMapper<T> rowMapper(String prefix, Optional<String> label);

    default RowMapper<T> rowMapper(Optional<String> label) {
        return rowMapper("", label);
    }

    default RowMapperFactory<T> withDefaultLabel(Optional<String> defaultLabel) {
        return (prefix, label) -> rowMapper(prefix, or(label, defaultLabel));
    }

    default RowMapperFactory<T> withPrefix(String prefix) {
        return (p, label) -> rowMapper(p + prefix, label);
    }
}
