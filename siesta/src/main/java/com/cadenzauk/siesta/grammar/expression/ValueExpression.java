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

import com.cadenzauk.siesta.DataType;
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.grammar.LabelGenerator;

import java.util.Objects;
import java.util.stream.Stream;

public class ValueExpression<T> implements TypedExpression<T> {
    private final LabelGenerator labelGenerator = new LabelGenerator("value_");
    private final T value;

    private ValueExpression(T value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    @Override
    public String sql(Scope scope) {
        return "?";
    }

    @Override
    public Stream<Object> args(Scope scope) {
        return Stream.of(scope.database().getDataTypeOf(value).toDatabase(value));
    }

    @Override
    public Precedence precedence() {
        return Precedence.COLUMN;
    }

    @Override
    public String label(Scope scope) {
        return labelGenerator.label();
    }

    @Override
    public RowMapper<T> rowMapper(Scope scope, String label) {
        DataType<T> dataType = scope.database().getDataTypeOf(value);
        return rs -> dataType.get(rs, label).orElse(null);
    }

    public static <T> ValueExpression<T> of(T value) {
        return new ValueExpression<>(value);
    }
}
