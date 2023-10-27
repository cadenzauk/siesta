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
import com.cadenzauk.siesta.type.DbType;
import com.cadenzauk.siesta.type.DbTypeId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Function;

public class CastBuilder<T> {
    private final TypedExpression<T> expression;

    public CastBuilder(TypedExpression<T> expression) {
        this.expression = expression;
    }

    public ExpressionBuilder<String,BooleanExpression> asChar(int len) {
        return ExpressionBuilder.of(new CastExpression<>(expression, DataType.STRING, DbTypeId.CHAR, (d, db) -> d.castType(db, len)), Function.identity());
    }

    public ExpressionBuilder<Byte,BooleanExpression> asTinyInteger() {
        return ExpressionBuilder.of(new CastExpression<>(expression, DataType.BYTE, DbTypeId.TINYINT, DbType::castType), Function.identity());
    }

    public ExpressionBuilder<Double,BooleanExpression> asDoublePrecision() {
        return ExpressionBuilder.of(new CastExpression<>(expression, DataType.DOUBLE, DbTypeId.DOUBLE, DbType::castType), Function.identity());
    }

    public ExpressionBuilder<Float,BooleanExpression> asReal() {
        return ExpressionBuilder.of(new CastExpression<>(expression, DataType.FLOAT, DbTypeId.REAL, DbType::castType), Function.identity());
    }

    public ExpressionBuilder<LocalDate,BooleanExpression> asDate() {
        return ExpressionBuilder.of(new CastExpression<>(expression, DataType.LOCAL_DATE, DbTypeId.DATE, DbType::castType), Function.identity());
    }

    public ExpressionBuilder<LocalTime,BooleanExpression> asTime() {
        return ExpressionBuilder.of(new CastExpression<>(expression, DataType.LOCAL_TIME, DbTypeId.TIME, DbType::castType), Function.identity());
    }

    public ExpressionBuilder<LocalDateTime,BooleanExpression> asTimestamp() {
        return ExpressionBuilder.of(new CastExpression<>(expression, DataType.LOCAL_DATE_TIME, DbTypeId.TIMESTAMP, DbType::castType), Function.identity());
    }

    public ExpressionBuilder<LocalDateTime,BooleanExpression> asTimestamp(int prec) {
        return ExpressionBuilder.of(new CastExpression<>(expression, DataType.LOCAL_DATE_TIME, DbTypeId.TIMESTAMP, (d, db) -> d.castType(db, prec)), Function.identity());
    }

    public ExpressionBuilder<Long,BooleanExpression> asBigInteger() {
        return ExpressionBuilder.of(new CastExpression<>(expression, DataType.LONG, DbTypeId.BIGINT, DbType::castType), Function.identity());
    }

    public ExpressionBuilder<Short,BooleanExpression> asSmallInteger() {
        return ExpressionBuilder.of(new CastExpression<>(expression, DataType.SHORT, DbTypeId.SMALLINT, DbType::castType), Function.identity());
    }

    public ExpressionBuilder<Integer,BooleanExpression> asInteger() {
        return ExpressionBuilder.of(new CastExpression<>(expression, DataType.INTEGER, DbTypeId.INTEGER, DbType::castType), Function.identity());
    }

    public ExpressionBuilder<String,BooleanExpression> asVarchar(int len) {
        return ExpressionBuilder.of(new CastExpression<>(expression, DataType.STRING, DbTypeId.VARCHAR, (d, db) -> d.castType(db, len)), Function.identity());
    }
}
