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

package com.cadenzauk.siesta.expression.condition;

import com.cadenzauk.siesta.Condition;
import com.cadenzauk.siesta.DataType;
import com.cadenzauk.siesta.Scope;

import java.util.Optional;
import java.util.stream.Stream;

public class LikeCondition<T> implements Condition<T> {
    private final DataType<T> dataType;
    private final String operator;
    private final T value;
    private final Optional<String> escape;

    public LikeCondition(DataType<T> dataType, String operator, T value, Optional<String> escape) {
        this.dataType = dataType;
        this.operator = operator;
        this.value = value;
        this.escape = escape.map(e -> String.format(" escape '%s'", e));
    }

    @Override
    public String sql(Scope scope) {
        return operator + " ?" + escape.orElse("");
    }

    @Override
    public Stream<Object> args() {
        return Stream.of(dataType.toDatabase(value));
    }
}
