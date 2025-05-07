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

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.core.util.UtilityClass;
import com.cadenzauk.siesta.Alias;

import java.math.BigDecimal;

import static com.cadenzauk.siesta.dialect.function.numeric.NumericFunctionSpecs.ABS;
import static com.cadenzauk.siesta.dialect.function.numeric.NumericFunctionSpecs.MINUS;

public final class NumericFunctions extends UtilityClass {
    //--
    public static TypedExpression<BigDecimal> abs(BigDecimal arg) {
        return SqlFunction.of(ABS, ValueExpression.of(arg));
    }

    public static TypedExpression<BigDecimal> abs(TypedExpression<BigDecimal> arg) {
        return SqlFunction.of(ABS, arg);
    }

    public static TypedExpression<BigDecimal> abs(Label<BigDecimal> arg) {
        return SqlFunction.of(ABS, arg);
    }

    public static TypedExpression<BigDecimal> abs(String alias, Label<BigDecimal> arg) {
        return SqlFunction.of(ABS, alias, arg);
    }

    public static <R> TypedExpression<BigDecimal> abs(Function1<R,BigDecimal> arg) {
        return SqlFunction.of(ABS, arg);
    }

    public static <R> TypedExpression<BigDecimal> abs(FunctionOptional1<R,BigDecimal> arg) {
        return SqlFunction.of(ABS, arg);
    }

    public static <R> TypedExpression<BigDecimal> abs(String alias, Function1<R,BigDecimal> arg) {
        return SqlFunction.of(ABS, alias, arg);
    }

    public static <R> TypedExpression<BigDecimal> abs(String alias, FunctionOptional1<R,BigDecimal> arg) {
        return SqlFunction.of(ABS, alias, arg);
    }

    public static <R> TypedExpression<BigDecimal> abs(Alias<R> alias, Function1<R,BigDecimal> arg) {
        return SqlFunction.of(ABS, alias, arg);
    }

    public static <R> TypedExpression<BigDecimal> abs(Alias<R> alias, FunctionOptional1<R,BigDecimal> arg) {
        return SqlFunction.of(ABS, alias, arg);
    }

    public static TypedExpression<BigDecimal> minus(BigDecimal arg) {
        return SqlFunction.of(MINUS, ValueExpression.of(arg));
    }

    public static TypedExpression<BigDecimal> minus(TypedExpression<BigDecimal> arg) {
        return SqlFunction.of(MINUS, arg);
    }

    public static TypedExpression<BigDecimal> minus(Label<BigDecimal> arg) {
        return SqlFunction.of(MINUS, arg);
    }

    public static TypedExpression<BigDecimal> minus(String alias, Label<BigDecimal> arg) {
        return SqlFunction.of(MINUS, alias, arg);
    }

    public static <R> TypedExpression<BigDecimal> minus(Function1<R,BigDecimal> arg) {
        return SqlFunction.of(MINUS, arg);
    }

    public static <R> TypedExpression<BigDecimal> minus(FunctionOptional1<R,BigDecimal> arg) {
        return SqlFunction.of(MINUS, arg);
    }

    public static <R> TypedExpression<BigDecimal> minus(String alias, Function1<R,BigDecimal> arg) {
        return SqlFunction.of(MINUS, alias, arg);
    }

    public static <R> TypedExpression<BigDecimal> minus(String alias, FunctionOptional1<R,BigDecimal> arg) {
        return SqlFunction.of(MINUS, alias, arg);
    }

    public static <R> TypedExpression<BigDecimal> minus(Alias<R> alias, Function1<R,BigDecimal> arg) {
        return SqlFunction.of(MINUS, alias, arg);
    }

    public static <R> TypedExpression<BigDecimal> minus(Alias<R> alias, FunctionOptional1<R,BigDecimal> arg) {
        return SqlFunction.of(MINUS, alias, arg);
    }
}
