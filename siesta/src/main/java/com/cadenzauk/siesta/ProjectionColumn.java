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

package com.cadenzauk.siesta;

import com.cadenzauk.core.sql.RowMapperFactory;
import com.google.common.reflect.TypeToken;

import java.util.stream.Stream;

import static com.cadenzauk.core.reflect.util.TypeUtil.boxedType;

public class ProjectionColumn<T> {
    private final TypeToken<T> type;
    private final String columnSql;
    private final String label;
    private final RowMapperFactory<T> rowMapperFactory;

    public ProjectionColumn(TypeToken<T> type, String columnSql, String label, RowMapperFactory<T> rowMapperFactory) {
        this.type = type;
        this.columnSql = columnSql;
        this.label = label;
        this.rowMapperFactory = rowMapperFactory;
    }

    public String columnSql() {
        return columnSql;
    }

    public String label() {
        return label;
    }

    public TypeToken<T> type() {
        return type;
    }

    public RowMapperFactory<T> rowMapperFactory() {
        return rowMapperFactory;
    }

    @SuppressWarnings("unchecked")
    public <T2> Stream<ProjectionColumn<T2>> as(Class<T2> type) {
        return boxedType(type).isAssignableFrom(boxedType(this.type.getRawType()))
            ? Stream.of((ProjectionColumn<T2>)this)
            : Stream.empty();
    }
}
