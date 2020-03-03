/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited
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

package com.cadenzauk.siesta.catalog;

import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.google.common.reflect.TypeToken;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Column<T, R> {
    String propertyName();

    String columnName();

    boolean identifier();

    boolean insertable();

    boolean updatable();

    int count();

    String sql();

    String sql(Alias<?> alias);

    String sqlWithLabel(Alias<?> alias, Optional<String> label);

    Stream<String> idSql(Alias<?> alias);

    Stream<Object> idArgs(Database database, R row);

    Stream<String> insertColumnSql();

    Stream<String> insertArgsSql();

    Stream<Object> insertArgs(Database database, Optional<R> row);

    Stream<String> updateSql();

    Stream<Object> updateArgs(Database database, R row);

    RowMapperFactory<T> rowMapperFactory(Alias<?> alias, Optional<String> defaultLabel);

    <U> Stream<Column<U,R>> as(TypeToken<U> requiredDataType);

    Function<R,Optional<T>> getter();

    Stream<Object> toDatabase(Database database, Optional<T> v);

    default Stream<Object> rowToDatabase(Database database, Optional<R> row) {
        return toDatabase(database, row.flatMap(r -> getter().apply(r)));
    }

    <V> Optional<Column<V,T>> findColumn(TypeToken<V> type, String propertyName);
}
