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

package com.cadenzauk.siesta.catalog;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.siesta.Alias;
import com.google.common.reflect.TypeToken;

import java.util.Optional;
import java.util.stream.Stream;

public interface ColumnCollection<R> {
    TypeToken<R> rowType();

    Stream<Column<?,R>> columns();

    Stream<Column<?,?>> primitiveColumns();

    <T> Column<T,R> column(MethodInfo<R,T> methodInfo);

    <T> ColumnCollection<T> embedded(MethodInfo<R,T> methodInfo);

    RowMapperFactory<R> rowMapperFactory(Alias<?> alias, Optional<String> defaultLabel);

    default <T> Column<T,R> column(FunctionOptional1<R, T> getter) {
        return column(MethodInfo.of(getter));
    }

    default  <T> Column<T,R> column(Function1<R,T> getter) {
        return column(MethodInfo.of(getter));
    }

    default <T> ColumnCollection<T> embedded(Function1<R, T> getter) {
        return embedded(MethodInfo.of(getter));
    }

    default <T> ColumnCollection<T> embedded(FunctionOptional1<R, T> getter) {
        return embedded(MethodInfo.of(getter));
    }
}
