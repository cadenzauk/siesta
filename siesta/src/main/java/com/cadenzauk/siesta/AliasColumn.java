/*
 * Copyright (c) 2020 Cadenza United Kingdom Limited
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

import com.cadenzauk.core.reflect.util.TypeUtil;
import com.cadenzauk.core.sql.RowMapperFactory;
import com.google.common.reflect.TypeToken;

import java.util.Optional;
import java.util.stream.Stream;

public interface AliasColumn<T> {
    TypeToken<T> type();

    String propertyName();

    String columnName();

    RowMapperFactory<T> rowMapperFactory(Alias<?> alias, Optional<String> defaultLabel);

    String sql();

    String sql(Alias<?> alias);

    String sqlWithLabel(Alias<?> alias, Optional<String> label);

    ProjectionColumn<T> toProjection(Alias<?> alias, Optional<String> label);

    <V> Optional<AliasColumn<V>> findColumn(TypeToken<V> type, String propertyName);

    @SuppressWarnings("unchecked")
    default <U> Stream<AliasColumn<U>> as(TypeToken<U> requiredDataType) {
        Class<? super U> requiredBoxed = TypeUtil.boxedType(requiredDataType.getRawType());
        Class<? super T> boxedDatatype = TypeUtil.boxedType(type().getRawType());
        return requiredBoxed.isAssignableFrom(boxedDatatype)
            ? Stream.of((AliasColumn<U>) this)
            : Stream.empty();
    }
}
