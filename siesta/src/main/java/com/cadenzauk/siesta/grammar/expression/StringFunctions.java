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

import static com.cadenzauk.siesta.dialect.function.string.StringFunctionSpecs.INSTR;
import static com.cadenzauk.siesta.dialect.function.string.StringFunctionSpecs.LENGTH;
import static com.cadenzauk.siesta.dialect.function.string.StringFunctionSpecs.LOWER;
import static com.cadenzauk.siesta.dialect.function.string.StringFunctionSpecs.SUBSTR;
import static com.cadenzauk.siesta.dialect.function.string.StringFunctionSpecs.UPPER;

public final class StringFunctions extends UtilityClass {
    //--

    public static TypedExpression<String> upper(String arg) {
        return SqlFunction.of(UPPER, ValueExpression.of(arg));
    }

    public static TypedExpression<String> upper(TypedExpression<String> arg) {
        return SqlFunction.of(UPPER, arg);
    }

    public static TypedExpression<String> upper(Label<String> arg) {
        return SqlFunction.of(UPPER, arg);
    }

    public static <R> TypedExpression<String> upper(Function1<R,String> arg) {
        return SqlFunction.of(UPPER, arg);
    }

    public static <R> TypedExpression<String> upper(FunctionOptional1<R,String> arg) {
        return SqlFunction.of(UPPER, arg);
    }

    public static <R> TypedExpression<String> upper(String alias, Function1<R,String> arg) {
        return SqlFunction.of(UPPER, alias, arg);
    }

    public static <R> TypedExpression<String> upper(String alias, FunctionOptional1<R,String> arg) {
        return SqlFunction.of(UPPER, alias, arg);
    }

    public static <R> TypedExpression<String> upper(Alias<R> alias, Function1<R,String> arg) {
        return SqlFunction.of(UPPER, alias, arg);
    }

    public static <R> TypedExpression<String> upper(Alias<R> alias, FunctionOptional1<R,String> arg) {
        return SqlFunction.of(UPPER, alias, arg);
    }

    //--

    public static TypedExpression<String> lower(String arg) {
        return SqlFunction.of(LOWER, ValueExpression.of(arg));
    }

    public static TypedExpression<String> lower(TypedExpression<String> arg) {
        return SqlFunction.of(LOWER, arg);
    }

    public static TypedExpression<String> lower(Label<String> arg) {
        return SqlFunction.of(LOWER, arg);
    }

    public static <R> TypedExpression<String> lower(Function1<R,String> arg) {
        return SqlFunction.of(LOWER, arg);
    }

    public static <R> TypedExpression<String> lower(FunctionOptional1<R,String> arg) {
        return SqlFunction.of(LOWER, arg);
    }

    public static <R> TypedExpression<String> lower(String alias, Function1<R,String> arg) {
        return SqlFunction.of(LOWER, alias, arg);
    }

    public static <R> TypedExpression<String> lower(String alias, FunctionOptional1<R,String> arg) {
        return SqlFunction.of(LOWER, alias, arg);
    }

    public static <R> TypedExpression<String> lower(Alias<R> alias, Function1<R,String> arg) {
        return SqlFunction.of(LOWER, alias, arg);
    }

    public static <R> TypedExpression<String> lower(Alias<R> alias, FunctionOptional1<R,String> arg) {
        return SqlFunction.of(LOWER, alias, arg);
    }

    //--

    public static TypedExpression<Integer> length(String arg) {
        return SqlFunction.of(LENGTH, Integer.class, ValueExpression.of(arg));
    }

    public static TypedExpression<Integer> length(TypedExpression<String> arg) {
        return SqlFunction.of(LENGTH, Integer.class, arg);
    }

    public static TypedExpression<Integer> length(Label<String> arg) {
        return SqlFunction.of(LENGTH, Integer.class, UnresolvedColumn.of(arg));
    }

    public static <R> TypedExpression<Integer> length(Function1<R,String> arg) {
        return SqlFunction.of(LENGTH, Integer.class, arg);
    }

    public static <R> TypedExpression<Integer> length(FunctionOptional1<R,String> arg) {
        return SqlFunction.of(LENGTH, Integer.class, arg);
    }

    public static <R> TypedExpression<Integer> length(String alias, Function1<R,String> arg) {
        return SqlFunction.of(LENGTH, Integer.class, alias, arg);
    }

    public static <R> TypedExpression<Integer> length(String alias, FunctionOptional1<R,String> arg) {
        return SqlFunction.of(LENGTH, Integer.class, alias, arg);
    }

    public static <R> TypedExpression<Integer> length(Alias<R> alias, Function1<R,String> arg) {
        return SqlFunction.of(LENGTH, Integer.class, alias, arg);
    }

    public static <R> TypedExpression<Integer> length(Alias<R> alias, FunctionOptional1<R,String> arg) {
        return SqlFunction.of(LENGTH, Integer.class, alias, arg);
    }

    //--

    public static TypedExpression<String> substr(String str, int start) {
        return SqlFunction.of(SUBSTR, String.class, ValueExpression.of(str), ValueExpression.of(start));
    }

    public static TypedExpression<String> substr(TypedExpression<String> str, int start) {
        return SqlFunction.of(SUBSTR, String.class, str, ValueExpression.of(start));
    }

    public static TypedExpression<String> substr(Label<String> str, int start) {
        return SqlFunction.of(SUBSTR, String.class, UnresolvedColumn.of(str), ValueExpression.of(start));
    }

    public static <R> TypedExpression<String> substr(Function1<R,String> str, int start) {
        return SqlFunction.of(SUBSTR, String.class, UnresolvedColumn.of(str), ValueExpression.of(start));
    }

    public static <R> TypedExpression<String> substr(FunctionOptional1<R,String> str, int start) {
        return SqlFunction.of(SUBSTR, String.class, UnresolvedColumn.of(str), ValueExpression.of(start));
    }

    public static <R> TypedExpression<String> substr(String alias, Function1<R,String> str, int start) {
        return SqlFunction.of(SUBSTR, String.class, UnresolvedColumn.of(alias, str), ValueExpression.of(start));
    }

    public static <R> TypedExpression<String> substr(String alias, FunctionOptional1<R,String> str, int start) {
        return SqlFunction.of(SUBSTR, String.class, UnresolvedColumn.of(alias, str), ValueExpression.of(start));
    }

    public static <R> TypedExpression<String> substr(Alias<R> alias, Function1<R,String> str, int start) {
        return SqlFunction.of(SUBSTR, String.class, ResolvedColumn.of(alias, str), ValueExpression.of(start));
    }

    public static <R> TypedExpression<String> substr(Alias<R> alias, FunctionOptional1<R,String> str, int start) {
        return SqlFunction.of(SUBSTR, String.class, ResolvedColumn.of(alias, str), ValueExpression.of(start));
    }

    public static TypedExpression<String> substr(TypedExpression<String> str, TypedExpression<Integer> start) {
        return SqlFunction.of(SUBSTR, String.class, str, start);
    }

    public static TypedExpression<String> substr(String str, int start, int len) {
        return SqlFunction.of(SUBSTR, String.class, ValueExpression.of(str), ValueExpression.of(start), ValueExpression.of(len));
    }

    public static TypedExpression<String> substr(TypedExpression<String> str, int start, int len) {
        return SqlFunction.of(SUBSTR, String.class, str, ValueExpression.of(start), ValueExpression.of(len));
    }

    public static TypedExpression<String> substr(Label<String> str, int start, int len) {
        return SqlFunction.of(SUBSTR, String.class, UnresolvedColumn.of(str), ValueExpression.of(start), ValueExpression.of(len));
    }

    public static <R> TypedExpression<String> substr(Function1<R,String> str, int start, int len) {
        return SqlFunction.of(SUBSTR, String.class, UnresolvedColumn.of(str), ValueExpression.of(start), ValueExpression.of(len));
    }

    public static <R> TypedExpression<String> substr(FunctionOptional1<R,String> str, int start, int len) {
        return SqlFunction.of(SUBSTR, String.class, UnresolvedColumn.of(str), ValueExpression.of(start), ValueExpression.of(len));
    }

    public static <R> TypedExpression<String> substr(String alias, Function1<R,String> str, int start, int len) {
        return SqlFunction.of(SUBSTR, String.class, UnresolvedColumn.of(alias, str), ValueExpression.of(start), ValueExpression.of(len));
    }

    public static <R> TypedExpression<String> substr(String alias, FunctionOptional1<R,String> str, int start, int len) {
        return SqlFunction.of(SUBSTR, String.class, UnresolvedColumn.of(alias, str), ValueExpression.of(start), ValueExpression.of(len));
    }

    public static <R> TypedExpression<String> substr(Alias<R> alias, Function1<R,String> str, int start, int len) {
        return SqlFunction.of(SUBSTR, String.class, ResolvedColumn.of(alias, str), ValueExpression.of(start), ValueExpression.of(len));
    }

    public static <R> TypedExpression<String> substr(Alias<R> alias, FunctionOptional1<R,String> str, int start, int len) {
        return SqlFunction.of(SUBSTR, String.class, ResolvedColumn.of(alias, str), ValueExpression.of(start), ValueExpression.of(len));
    }

    public static TypedExpression<String> substr(TypedExpression<String> str, TypedExpression<Integer> start, TypedExpression<Integer> len) {
        return SqlFunction.of(SUBSTR, String.class, str, start, len);
    }

    //--

    public static TypedExpression<Integer> instr(String str, String substr) {
        return SqlFunction.of(INSTR, Integer.class, ValueExpression.of(str), ValueExpression.of(substr));
    }

    public static TypedExpression<Integer> instr(TypedExpression<String> str, String substr) {
        return SqlFunction.of(INSTR, Integer.class, str, ValueExpression.of(substr));
    }

    public static <R> TypedExpression<Integer> instr(Function1<R,String> str, String substr) {
        return SqlFunction.of(INSTR, Integer.class, UnresolvedColumn.of(str), ValueExpression.of(substr));
    }

    public static <R> TypedExpression<Integer> instr(FunctionOptional1<R,String> str, String substr) {
        return SqlFunction.of(INSTR, Integer.class, UnresolvedColumn.of(str), ValueExpression.of(substr));
    }

    public static <R> TypedExpression<Integer> instr(String alias, Function1<R,String> str, String substr) {
        return SqlFunction.of(INSTR, Integer.class, UnresolvedColumn.of(alias, str), ValueExpression.of(substr));
    }

    public static <R> TypedExpression<Integer> instr(String alias, FunctionOptional1<R,String> str, String substr) {
        return SqlFunction.of(INSTR, Integer.class, UnresolvedColumn.of(alias, str), ValueExpression.of(substr));
    }

    public static <R> TypedExpression<Integer> instr(Alias<R> alias, Function1<R,String> str, String substr) {
        return SqlFunction.of(INSTR, Integer.class, ResolvedColumn.of(alias, str), ValueExpression.of(substr));
    }

    public static <R> TypedExpression<Integer> instr(Alias<R> alias, FunctionOptional1<R,String> str, String substr) {
        return SqlFunction.of(INSTR, Integer.class, ResolvedColumn.of(alias, str), ValueExpression.of(substr));
    }

    public static TypedExpression<Integer> instr(TypedExpression<String> str, TypedExpression<String> substr) {
        return SqlFunction.of(INSTR, Integer.class, str, substr);
    }
}
