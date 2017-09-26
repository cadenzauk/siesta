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
import com.cadenzauk.siesta.Dialect;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

public class CastBuilder<T> {
    private final TypedExpression<T> expression;

    public CastBuilder(TypedExpression<T> expression) {
        this.expression = expression;
    }

    public CastExpression<T,String> asChar(int len) {
        return new CastExpression<>(expression, DataType.STRING, d -> d.charType(len));
    }

    public CastExpression<T,Byte> asTinyInteger() {
        return new CastExpression<>(expression, DataType.BYTE, Dialect::tinyintType);
    }

    public CastExpression<T,Double> asDoublePrecision() {
        return new CastExpression<>(expression, DataType.DOUBLE, Dialect::doubleType);
    }

    public CastExpression<T,Float> asReal() {
        return new CastExpression<>(expression, DataType.FLOAT, Dialect::realType);
    }

    public CastExpression<T,LocalDate> asDate() {
        return new CastExpression<>(expression, DataType.LOCAL_DATE, Dialect::dateType);
    }

    public CastExpression<T,LocalTime> asTime() {
        return new CastExpression<>(expression, DataType.LOCAL_TIME, Dialect::timeType);
    }

    public CastExpression<T,LocalDateTime> asTimestamp() {
        return new CastExpression<>(expression, DataType.LOCAL_DATE_TIME, d -> d.timestampType(Optional.empty()));
    }

    public CastExpression<T,LocalDateTime> asTimestamp(int prec) {
        return new CastExpression<>(expression, DataType.LOCAL_DATE_TIME, d -> d.timestampType(Optional.of(prec)));
    }

    public CastExpression<T,Long> asBigInteger() {
        return new CastExpression<>(expression, DataType.LONG, Dialect::bigintType);
    }

    public CastExpression<T,Short> asSmallInteger() {
        return new CastExpression<>(expression, DataType.SHORT, Dialect::smallintType);
    }

    public CastExpression<T,Integer> asInteger() {
        return new CastExpression<>(expression, DataType.INTEGER, Dialect::integerType);
    }

    public CastExpression<T,String> asVarchar(int len) {
        return new CastExpression<>(expression, DataType.STRING, d -> d.varcharType(len));
    }
}
