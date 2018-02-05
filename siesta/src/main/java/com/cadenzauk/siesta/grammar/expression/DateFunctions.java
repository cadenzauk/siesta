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
import com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;

import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.ADD_DAYS;
import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.DAY_DIFF;
import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.HOUR_DIFF;
import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.MINUTE_DIFF;
import static com.cadenzauk.siesta.dialect.function.date.DateFunctionSpecs.SECOND_DIFF;

public final class DateFunctions extends UtilityClass {
    //--
    public static TypedExpression<LocalDate> currentDate() {
        return SqlFunction.of(DateFunctionSpecs.CURRENT_DATE, LocalDate.class, new TypedExpression<?>[0]);
    }

    //--
    public static TypedExpression<LocalDateTime> currentTimestampLocal() {
        return SqlFunction.of(DateFunctionSpecs.CURRENT_TIMESTAMP, LocalDateTime.class, new TypedExpression<?>[0]);
    }

    //--
    public static TypedExpression<ZonedDateTime> currentTimestamp() {
        return SqlFunction.of(DateFunctionSpecs.CURRENT_TIMESTAMP_UTC, ZonedDateTime.class, new TypedExpression<?>[0]);
    }

    //--
    public static TypedExpression<Integer> year(LocalDate value) {
        return SqlFunction.of(DateFunctionSpecs.YEAR, Integer.class, ValueExpression.of(value));
    }

    public static TypedExpression<Integer> year(LocalDateTime value) {
        return SqlFunction.of(DateFunctionSpecs.YEAR, Integer.class, ValueExpression.of(value));
    }

    public static TypedExpression<Integer> year(ZonedDateTime value) {
        return SqlFunction.of(DateFunctionSpecs.YEAR, Integer.class, ValueExpression.of(value));
    }

    public static <T extends Temporal> TypedExpression<Integer> year(TypedExpression<T> expression) {
        return SqlFunction.of(DateFunctionSpecs.YEAR, Integer.class, expression);
    }

    public static <R, T extends Temporal> TypedExpression<Integer> year(Function1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.YEAR, Integer.class, UnresolvedColumn.of(methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> year(FunctionOptional1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.YEAR, Integer.class, UnresolvedColumn.of(methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> year(String alias, Function1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.YEAR, Integer.class, UnresolvedColumn.of(alias, methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> year(String alias, FunctionOptional1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.YEAR, Integer.class, UnresolvedColumn.of(alias, methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> year(Alias<R> alias, Function1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.YEAR, Integer.class, ResolvedColumn.of(alias, methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> year(Alias<R> alias, FunctionOptional1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.YEAR, Integer.class, ResolvedColumn.of(alias, methodReference));
    }

    //--
    public static TypedExpression<Integer> month(LocalDate value) {
        return SqlFunction.of(DateFunctionSpecs.MONTH, Integer.class, ValueExpression.of(value));
    }

    public static TypedExpression<Integer> month(LocalDateTime value) {
        return SqlFunction.of(DateFunctionSpecs.MONTH, Integer.class, ValueExpression.of(value));
    }

    public static TypedExpression<Integer> month(ZonedDateTime value) {
        return SqlFunction.of(DateFunctionSpecs.MONTH, Integer.class, ValueExpression.of(value));
    }

    public static <T extends Temporal> TypedExpression<Integer> month(TypedExpression<T> expression) {
        return SqlFunction.of(DateFunctionSpecs.MONTH, Integer.class, expression);
    }

    public static <R, T extends Temporal> TypedExpression<Integer> month(Function1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.MONTH, Integer.class, UnresolvedColumn.of(methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> month(FunctionOptional1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.MONTH, Integer.class, UnresolvedColumn.of(methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> month(String alias, Function1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.MONTH, Integer.class, UnresolvedColumn.of(alias, methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> month(String alias, FunctionOptional1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.MONTH, Integer.class, UnresolvedColumn.of(alias, methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> month(Alias<R> alias, Function1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.MONTH, Integer.class, ResolvedColumn.of(alias, methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> month(Alias<R> alias, FunctionOptional1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.MONTH, Integer.class, ResolvedColumn.of(alias, methodReference));
    }

    //--
    public static TypedExpression<Integer> day(LocalDate value) {
        return SqlFunction.of(DateFunctionSpecs.DAY, Integer.class, ValueExpression.of(value));
    }

    public static TypedExpression<Integer> day(LocalDateTime value) {
        return SqlFunction.of(DateFunctionSpecs.DAY, Integer.class, ValueExpression.of(value));
    }

    public static TypedExpression<Integer> day(ZonedDateTime value) {
        return SqlFunction.of(DateFunctionSpecs.DAY, Integer.class, ValueExpression.of(value));
    }

    public static <T extends Temporal> TypedExpression<Integer> day(TypedExpression<T> expression) {
        return SqlFunction.of(DateFunctionSpecs.DAY, Integer.class, expression);
    }

    public static <R, T extends Temporal> TypedExpression<Integer> day(Function1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.DAY, Integer.class, UnresolvedColumn.of(methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> day(FunctionOptional1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.DAY, Integer.class, UnresolvedColumn.of(methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> day(String alias, Function1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.DAY, Integer.class, UnresolvedColumn.of(alias, methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> day(String alias, FunctionOptional1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.DAY, Integer.class, UnresolvedColumn.of(alias, methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> day(Alias<R> alias, Function1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.DAY, Integer.class, ResolvedColumn.of(alias, methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> day(Alias<R> alias, FunctionOptional1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.DAY, Integer.class, ResolvedColumn.of(alias, methodReference));
    }

    //--
    public static TypedExpression<Integer> hour(LocalDateTime value) {
        return SqlFunction.of(DateFunctionSpecs.HOUR, Integer.class, ValueExpression.of(value));
    }

    public static TypedExpression<Integer> hour(ZonedDateTime value) {
        return SqlFunction.of(DateFunctionSpecs.HOUR, Integer.class, ValueExpression.of(value));
    }

    public static <T extends Temporal> TypedExpression<Integer> hour(TypedExpression<T> expression) {
        return SqlFunction.of(DateFunctionSpecs.HOUR, Integer.class, expression);
    }

    public static <R, T extends Temporal> TypedExpression<Integer> hour(Function1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.HOUR, Integer.class, UnresolvedColumn.of(methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> hour(FunctionOptional1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.HOUR, Integer.class, UnresolvedColumn.of(methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> hour(String alias, Function1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.HOUR, Integer.class, UnresolvedColumn.of(alias, methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> hour(String alias, FunctionOptional1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.HOUR, Integer.class, UnresolvedColumn.of(alias, methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> hour(Alias<R> alias, Function1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.HOUR, Integer.class, ResolvedColumn.of(alias, methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> hour(Alias<R> alias, FunctionOptional1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.HOUR, Integer.class, ResolvedColumn.of(alias, methodReference));
    }

    //--
    public static TypedExpression<Integer> minute(LocalDateTime value) {
        return SqlFunction.of(DateFunctionSpecs.MINUTE, Integer.class, ValueExpression.of(value));
    }

    public static TypedExpression<Integer> minute(ZonedDateTime value) {
        return SqlFunction.of(DateFunctionSpecs.MINUTE, Integer.class, ValueExpression.of(value));
    }

    public static <T extends Temporal> TypedExpression<Integer> minute(TypedExpression<T> expression) {
        return SqlFunction.of(DateFunctionSpecs.MINUTE, Integer.class, expression);
    }

    public static <R, T extends Temporal> TypedExpression<Integer> minute(Function1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.MINUTE, Integer.class, UnresolvedColumn.of(methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> minute(FunctionOptional1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.MINUTE, Integer.class, UnresolvedColumn.of(methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> minute(String alias, Function1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.MINUTE, Integer.class, UnresolvedColumn.of(alias, methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> minute(String alias, FunctionOptional1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.MINUTE, Integer.class, UnresolvedColumn.of(alias, methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> minute(Alias<R> alias, Function1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.MINUTE, Integer.class, ResolvedColumn.of(alias, methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> minute(Alias<R> alias, FunctionOptional1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.MINUTE, Integer.class, ResolvedColumn.of(alias, methodReference));
    }

    //--
    public static TypedExpression<Integer> second(LocalDateTime value) {
        return SqlFunction.of(DateFunctionSpecs.SECOND, Integer.class, ValueExpression.of(value));
    }

    public static TypedExpression<Integer> second(ZonedDateTime value) {
        return SqlFunction.of(DateFunctionSpecs.SECOND, Integer.class, ValueExpression.of(value));
    }

    public static <T extends Temporal> TypedExpression<Integer> second(TypedExpression<T> expression) {
        return SqlFunction.of(DateFunctionSpecs.SECOND, Integer.class, expression);
    }

    public static <R, T extends Temporal> TypedExpression<Integer> second(Function1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.SECOND, Integer.class, UnresolvedColumn.of(methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> second(FunctionOptional1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.SECOND, Integer.class, UnresolvedColumn.of(methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> second(String alias, Function1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.SECOND, Integer.class, UnresolvedColumn.of(alias, methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> second(String alias, FunctionOptional1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.SECOND, Integer.class, UnresolvedColumn.of(alias, methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> second(Alias<R> alias, Function1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.SECOND, Integer.class, ResolvedColumn.of(alias, methodReference));
    }

    public static <R, T extends Temporal> TypedExpression<Integer> second(Alias<R> alias, FunctionOptional1<R,T> methodReference) {
        return SqlFunction.of(DateFunctionSpecs.SECOND, Integer.class, ResolvedColumn.of(alias, methodReference));
    }

    //--
    public static <T extends Temporal> TypedExpression<T> addDays(TypedExpression<T> date, int days) {
        return addDays(date, ValueExpression.of(days));
    }

    public static <R, T extends Temporal> TypedExpression<T> addDays(Function1<R,T> date, int days) {
        return addDays(UnresolvedColumn.of(date), ValueExpression.of(days));
    }

    public static <R, T extends Temporal> TypedExpression<T> addDays(FunctionOptional1<R,T> date, int days) {
        return addDays(UnresolvedColumn.of(date), ValueExpression.of(days));
    }

    public static <R, T extends Temporal> TypedExpression<T> addDays(String alias, Function1<R,T> date, int days) {
        return addDays(UnresolvedColumn.of(alias, date), ValueExpression.of(days));
    }

    public static <R, T extends Temporal> TypedExpression<T> addDays(String alias, FunctionOptional1<R,T> date, int days) {
        return addDays(UnresolvedColumn.of(alias, date), ValueExpression.of(days));
    }

    public static <R, T extends Temporal> TypedExpression<T> addDays(Alias<R> alias, Function1<R,T> date, int days) {
        return addDays(ResolvedColumn.of(alias, date), ValueExpression.of(days));
    }

    public static <R, T extends Temporal> TypedExpression<T> addDays(Alias<R> alias, FunctionOptional1<R,T> date, int days) {
        return addDays(ResolvedColumn.of(alias, date), ValueExpression.of(days));
    }

    public static <T extends Temporal> TypedExpression<T> addDays(TypedExpression<T> date, TypedExpression<Integer> days) {
        return SqlFunction.of(ADD_DAYS, date.type(), date, days);
    }

    //--
    public static <R, T1 extends Temporal, T2 extends Temporal> TypedExpression<Integer> dayDiff(Function1<R,T1> date1, T2 date2) {
        return dayDiff(UnresolvedColumn.of(date1), ValueExpression.of(date2));
    }

    public static <R, T1 extends Temporal, T2 extends Temporal> TypedExpression<Integer> dayDiff(FunctionOptional1<R,T1> date1, T2 date2) {
        return dayDiff(UnresolvedColumn.of(date1), ValueExpression.of(date2));
    }

    public static <R, T1 extends Temporal, T2 extends Temporal> TypedExpression<Integer> dayDiff(String alias, Function1<R,T1> date1, T2 date2) {
        return dayDiff(UnresolvedColumn.of(alias, date1), ValueExpression.of(date2));
    }

    public static <R, T1 extends Temporal, T2 extends Temporal> TypedExpression<Integer> dayDiff(String alias, FunctionOptional1<R,T1> date1, T2 date2) {
        return dayDiff(UnresolvedColumn.of(alias, date1), ValueExpression.of(date2));
    }

    public static <R, T1 extends Temporal, T2 extends Temporal> TypedExpression<Integer> dayDiff(Alias<R> alias, Function1<R,T1> date1, T2 date2) {
        return dayDiff(ResolvedColumn.of(alias, date1), ValueExpression.of(date2));
    }

    public static <R, T1 extends Temporal, T2 extends Temporal> TypedExpression<Integer> dayDiff(Alias<R> alias, FunctionOptional1<R,T1> date1, T2 date2) {
        return dayDiff(ResolvedColumn.of(alias, date1), ValueExpression.of(date2));
    }

    public static <T1 extends Temporal, T2 extends Temporal> TypedExpression<Integer> dayDiff(TypedExpression<T1> date1, TypedExpression<T2> date2) {
        return SqlFunction.of(DAY_DIFF, Integer.class, date1, date2);
    }

    //--
    public static <R, T1 extends Temporal, T2 extends Temporal> TypedExpression<Integer> hourDiff(Function1<R,T1> date1, T2 date2) {
        return hourDiff(UnresolvedColumn.of(date1), ValueExpression.of(date2));
    }

    public static <R, T1 extends Temporal, T2 extends Temporal> TypedExpression<Integer> hourDiff(FunctionOptional1<R,T1> date1, T2 date2) {
        return hourDiff(UnresolvedColumn.of(date1), ValueExpression.of(date2));
    }

    public static <R, T1 extends Temporal, T2 extends Temporal> TypedExpression<Integer> hourDiff(String alias, Function1<R,T1> date1, T2 date2) {
        return hourDiff(UnresolvedColumn.of(alias, date1), ValueExpression.of(date2));
    }

    public static <R, T1 extends Temporal, T2 extends Temporal> TypedExpression<Integer> hourDiff(String alias, FunctionOptional1<R,T1> date1, T2 date2) {
        return hourDiff(UnresolvedColumn.of(alias, date1), ValueExpression.of(date2));
    }

    public static <R, T1 extends Temporal, T2 extends Temporal> TypedExpression<Integer> hourDiff(Alias<R> alias, Function1<R,T1> date1, T2 date2) {
        return hourDiff(ResolvedColumn.of(alias, date1), ValueExpression.of(date2));
    }

    public static <R, T1 extends Temporal, T2 extends Temporal> TypedExpression<Integer> hourDiff(Alias<R> alias, FunctionOptional1<R,T1> date1, T2 date2) {
        return hourDiff(ResolvedColumn.of(alias, date1), ValueExpression.of(date2));
    }

    public static <T1 extends Temporal, T2 extends Temporal> TypedExpression<Integer> hourDiff(TypedExpression<T1> date1, TypedExpression<T2> date2) {
        return SqlFunction.of(HOUR_DIFF, Integer.class, date1, date2);
    }

    //--
    public static <R, T1 extends Temporal, T2 extends Temporal> TypedExpression<Integer> minuteDiff(Function1<R,T1> date1, T2 date2) {
        return minuteDiff(UnresolvedColumn.of(date1), ValueExpression.of(date2));
    }

    public static <R, T1 extends Temporal, T2 extends Temporal> TypedExpression<Integer> minuteDiff(FunctionOptional1<R,T1> date1, T2 date2) {
        return minuteDiff(UnresolvedColumn.of(date1), ValueExpression.of(date2));
    }

    public static <R, T1 extends Temporal, T2 extends Temporal> TypedExpression<Integer> minuteDiff(String alias, Function1<R,T1> date1, T2 date2) {
        return minuteDiff(UnresolvedColumn.of(alias, date1), ValueExpression.of(date2));
    }

    public static <R, T1 extends Temporal, T2 extends Temporal> TypedExpression<Integer> minuteDiff(String alias, FunctionOptional1<R,T1> date1, T2 date2) {
        return minuteDiff(UnresolvedColumn.of(alias, date1), ValueExpression.of(date2));
    }

    public static <R, T1 extends Temporal, T2 extends Temporal> TypedExpression<Integer> minuteDiff(Alias<R> alias, Function1<R,T1> date1, T2 date2) {
        return minuteDiff(ResolvedColumn.of(alias, date1), ValueExpression.of(date2));
    }

    public static <R, T1 extends Temporal, T2 extends Temporal> TypedExpression<Integer> minuteDiff(Alias<R> alias, FunctionOptional1<R,T1> date1, T2 date2) {
        return minuteDiff(ResolvedColumn.of(alias, date1), ValueExpression.of(date2));
    }

    public static <T1 extends Temporal, T2 extends Temporal> TypedExpression<Integer> minuteDiff(TypedExpression<T1> date1, TypedExpression<T2> date2) {
        return SqlFunction.of(MINUTE_DIFF, Integer.class, date1, date2);
    }

    //--
    public static <R, T1 extends Temporal, T2 extends Temporal> TypedExpression<Integer> secondDiff(Function1<R,T1> date1, T2 date2) {
        return secondDiff(UnresolvedColumn.of(date1), ValueExpression.of(date2));
    }

    public static <R, T1 extends Temporal, T2 extends Temporal> TypedExpression<Integer> secondDiff(FunctionOptional1<R,T1> date1, T2 date2) {
        return secondDiff(UnresolvedColumn.of(date1), ValueExpression.of(date2));
    }

    public static <R, T1 extends Temporal, T2 extends Temporal> TypedExpression<Integer> secondDiff(String alias, Function1<R,T1> date1, T2 date2) {
        return secondDiff(UnresolvedColumn.of(alias, date1), ValueExpression.of(date2));
    }

    public static <R, T1 extends Temporal, T2 extends Temporal> TypedExpression<Integer> secondDiff(String alias, FunctionOptional1<R,T1> date1, T2 date2) {
        return secondDiff(UnresolvedColumn.of(alias, date1), ValueExpression.of(date2));
    }

    public static <R, T1 extends Temporal, T2 extends Temporal> TypedExpression<Integer> secondDiff(Alias<R> alias, Function1<R,T1> date1, T2 date2) {
        return secondDiff(ResolvedColumn.of(alias, date1), ValueExpression.of(date2));
    }

    public static <R, T1 extends Temporal, T2 extends Temporal> TypedExpression<Integer> secondDiff(Alias<R> alias, FunctionOptional1<R,T1> date1, T2 date2) {
        return secondDiff(ResolvedColumn.of(alias, date1), ValueExpression.of(date2));
    }

    public static <T1 extends Temporal, T2 extends Temporal> TypedExpression<Integer> secondDiff(TypedExpression<T1> date1, TypedExpression<T2> date2) {
        return SqlFunction.of(SECOND_DIFF, Integer.class, date1, date2);
    }
}
