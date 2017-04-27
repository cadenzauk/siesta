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

public final class StringFunctions extends UtilityClass {
    //--

    public static TypedExpression<String> upper(String arg) {
        return UnaryFunction.of(ValueExpression.of(arg), "upper");
    }

    public static TypedExpression<String> upper(TypedExpression<String> arg) {
        return UnaryFunction.of(arg, "upper");
    }

    public static <R> TypedExpression<String> upper(Function1<R,String> arg) {
        return UnaryFunction.of(arg, "upper");
    }

    public static <R> TypedExpression<String> upper(FunctionOptional1<R,String> arg) {
        return UnaryFunction.of(arg, "upper");
    }

    public static <R> TypedExpression<String> upper(String alias, Function1<R,String> arg) {
        return UnaryFunction.of(alias, arg, "upper");
    }

    public static <R> TypedExpression<String> upper(String alias, FunctionOptional1<R,String> arg) {
        return UnaryFunction.of(alias, arg, "upper");
    }

    public static <R> TypedExpression<String> upper(Alias<R> alias, Function1<R,String> arg) {
        return UnaryFunction.of(alias, arg, "upper");
    }

    public static <R> TypedExpression<String> upper(Alias<R> alias, FunctionOptional1<R,String> arg) {
        return UnaryFunction.of(alias, arg, "upper");
    }

    //--

    public static TypedExpression<String> lower(String arg) {
        return UnaryFunction.of(ValueExpression.of(arg), "lower");
    }

    public static TypedExpression<String> lower(TypedExpression<String> arg) {
        return UnaryFunction.of(arg, "lower");
    }

    public static <R> TypedExpression<String> lower(Function1<R,String> arg) {
        return UnaryFunction.of(arg, "lower");
    }

    public static <R> TypedExpression<String> lower(FunctionOptional1<R,String> arg) {
        return UnaryFunction.of(arg, "lower");
    }

    public static <R> TypedExpression<String> lower(String alias, Function1<R,String> arg) {
        return UnaryFunction.of(alias, arg, "lower");
    }

    public static <R> TypedExpression<String> lower(String alias, FunctionOptional1<R,String> arg) {
        return UnaryFunction.of(alias, arg, "lower");
    }

    public static <R> TypedExpression<String> lower(Alias<R> alias, Function1<R,String> arg) {
        return UnaryFunction.of(alias, arg, "lower");
    }

    public static <R> TypedExpression<String> lower(Alias<R> alias, FunctionOptional1<R,String> arg) {
        return UnaryFunction.of(alias, arg, "lower");
    }

    //--

    public static TypedExpression<Integer> length(String arg) {
        return UnaryFunction.of(ValueExpression.of(arg), "length", Integer.class);
    }

    public static TypedExpression<Integer> length(TypedExpression<String> arg) {
        return UnaryFunction.of(arg, "length", Integer.class);
    }

    public static <R> TypedExpression<Integer> length(Function1<R,String> arg) {
        return UnaryFunction.of(arg, "length", Integer.class);
    }

    public static <R> TypedExpression<Integer> length(FunctionOptional1<R,String> arg) {
        return UnaryFunction.of(arg, "length", Integer.class);
    }

    public static <R> TypedExpression<Integer> length(String alias, Function1<R,String> arg) {
        return UnaryFunction.of(alias, arg, "length", Integer.class);
    }

    public static <R> TypedExpression<Integer> length(String alias, FunctionOptional1<R,String> arg) {
        return UnaryFunction.of(alias, arg, "length", Integer.class);
    }

    public static <R> TypedExpression<Integer> length(Alias<R> alias, Function1<R,String> arg) {
        return UnaryFunction.of(alias, arg, "length", Integer.class);
    }

    public static <R> TypedExpression<Integer> length(Alias<R> alias, FunctionOptional1<R,String> arg) {
        return UnaryFunction.of(alias, arg, "length", Integer.class);
    }

    //--

    public static TypedExpression<String> substr(String str, int start) {
        return SqlFunction.of(ValueExpression.of(str), ValueExpression.of(start), "substr", String.class);
    }

    public static TypedExpression<String> substr(TypedExpression<String> str, int start) {
        return SqlFunction.of(str, ValueExpression.of(start), "substr", String.class);
    }

    public static <R> TypedExpression<String> substr(Function1<R,String> str, int start) {
        return SqlFunction.of(UnresolvedColumn.of(str), ValueExpression.of(start), "substr", String.class);
    }

    public static <R> TypedExpression<String> substr(FunctionOptional1<R,String> str, int start) {
        return SqlFunction.of(UnresolvedColumn.of(str), ValueExpression.of(start), "substr", String.class);
    }

    public static <R> TypedExpression<String> substr(String alias, Function1<R,String> str, int start) {
        return SqlFunction.of(UnresolvedColumn.of(alias, str), ValueExpression.of(start), "substr", String.class);
    }

    public static <R> TypedExpression<String> substr(String alias, FunctionOptional1<R,String> str, int start) {
        return SqlFunction.of(UnresolvedColumn.of(alias, str), ValueExpression.of(start), "substr", String.class);
    }

    public static <R> TypedExpression<String> substr(Alias<R> alias, Function1<R,String> str, int start) {
        return SqlFunction.of(ResolvedColumn.of(alias, str), ValueExpression.of(start), "substr", String.class);
    }

    public static <R> TypedExpression<String> substr(Alias<R> alias, FunctionOptional1<R,String> str, int start) {
        return SqlFunction.of(ResolvedColumn.of(alias, str), ValueExpression.of(start), "substr", String.class);
    }

    public static TypedExpression<String> substr(TypedExpression<String> str, TypedExpression<Integer> start) {
        return SqlFunction.of(str, start, "substr", String.class);
    }

    public static TypedExpression<String> substr(String str, int start, int len) {
        return SqlFunction.of(ValueExpression.of(str), ValueExpression.of(start), ValueExpression.of(len), "substr", String.class);
    }

    public static TypedExpression<String> substr(TypedExpression<String> str, int start, int len) {
        return SqlFunction.of(str, ValueExpression.of(start), ValueExpression.of(len), "substr", String.class);
    }

    public static <R> TypedExpression<String> substr(Function1<R,String> str, int start, int len) {
        return SqlFunction.of(UnresolvedColumn.of(str), ValueExpression.of(start), ValueExpression.of(len), "substr", String.class);
    }

    public static <R> TypedExpression<String> substr(FunctionOptional1<R,String> str, int start, int len) {
        return SqlFunction.of(UnresolvedColumn.of(str), ValueExpression.of(start), ValueExpression.of(len), "substr", String.class);
    }

    public static <R> TypedExpression<String> substr(String alias, Function1<R,String> str, int start, int len) {
        return SqlFunction.of(UnresolvedColumn.of(alias, str), ValueExpression.of(start), ValueExpression.of(len), "substr", String.class);
    }

    public static <R> TypedExpression<String> substr(String alias, FunctionOptional1<R,String> str, int start, int len) {
        return SqlFunction.of(UnresolvedColumn.of(alias, str), ValueExpression.of(start), ValueExpression.of(len), "substr", String.class);
    }

    public static <R> TypedExpression<String> substr(Alias<R> alias, Function1<R,String> str, int start, int len) {
        return SqlFunction.of(ResolvedColumn.of(alias, str), ValueExpression.of(start), ValueExpression.of(len), "substr", String.class);
    }

    public static <R> TypedExpression<String> substr(Alias<R> alias, FunctionOptional1<R,String> str, int start, int len) {
        return SqlFunction.of(ResolvedColumn.of(alias, str), ValueExpression.of(start), ValueExpression.of(len), "substr", String.class);
    }

    public static TypedExpression<String> substr(TypedExpression<String> str, TypedExpression<Integer> start, TypedExpression<Integer> len) {
        return SqlFunction.of(str, start, len, "substr", String.class);
    }
}
