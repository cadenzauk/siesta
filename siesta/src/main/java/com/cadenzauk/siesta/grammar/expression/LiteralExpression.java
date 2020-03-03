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

package com.cadenzauk.siesta.grammar.expression;

import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.grammar.LabelGenerator;
import com.google.common.reflect.TypeToken;

import java.util.Objects;
import java.util.stream.Stream;

public class LiteralExpression<T> implements TypedExpression<T> {
    private final LabelGenerator labelGenerator = new LabelGenerator("literal_");
    private final T value;

    private LiteralExpression(T value) {
        this.value = value;
    }

    @Override
    public String sql(Scope scope) {
        Database database = scope.database();
        return database.getDataTypeOf(value).literal(database, value);
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return Stream.empty();
    }

    @Override
    public Precedence precedence() {
        return Precedence.UNARY;
    }

    @Override
    public String label(Scope scope) {
        return labelGenerator.label(scope);
    }

    @Override
    public RowMapperFactory<T> rowMapperFactory(Scope scope) {
        return label -> rs -> scope.database().getDataTypeOf(value).get(rs, label.orElseGet(() -> label(scope)), scope.database()).orElse(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TypeToken<T> type() {
        return TypeToken.of((Class<T>) value.getClass());
    }

    @Override
    public String toString() {
        return value.toString();
    }

    public static <T> LiteralExpression<T> of(T value) {
        Objects.requireNonNull(value);
        return new LiteralExpression<>(value);
    }
}
