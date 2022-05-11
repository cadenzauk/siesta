/*
 * Copyright (c) 2022 Cadenza United Kingdom Limited
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
import com.cadenzauk.siesta.json.BinaryJson;
import com.cadenzauk.siesta.json.Json;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;

import static com.cadenzauk.siesta.dialect.function.json.JsonFunctionSpecs.JSONB_OBJECT;
import static com.cadenzauk.siesta.dialect.function.json.JsonFunctionSpecs.JSON_OBJECT;
import static com.cadenzauk.siesta.grammar.expression.TypedExpression.column;

public final class JsonFunctions extends UtilityClass {
    public static TypedExpression<Json> jsonObject(JsonKeyValue<?>... keyValues) {
        return new JsonObjectFunction<>(JSON_OBJECT, TypeToken.of(Json.class), ImmutableList.copyOf(keyValues));
    }

    public static TypedExpression<BinaryJson> jsonbObject(JsonKeyValue<?>... keyValues) {
        return new JsonObjectFunction<>(JSONB_OBJECT, TypeToken.of(BinaryJson.class), ImmutableList.copyOf(keyValues));
    }

    public static JsonKeyValue.Builder key(String key) {
        return new JsonKeyValue.Builder(ValueExpression.of(key));
    }

    public static JsonKeyValue.Builder key(TypedExpression<String> key) {
        return new JsonKeyValue.Builder(key);
    }

    public static <R> JsonKeyValue.Builder key(Function1<R,String> key) {
        return new JsonKeyValue.Builder(column(key));
    }

    public static <R> JsonKeyValue.Builder key(FunctionOptional1<R, String> key) {
        return new JsonKeyValue.Builder(column(key));
    }

    public static <R> JsonKeyValue.Builder key(String alias, Function1<R,String> key) {
        return new JsonKeyValue.Builder(UnresolvedColumn.of(alias, key));
    }

    public static <R> JsonKeyValue.Builder key(String alias, FunctionOptional1<R,String> key) {
        return new JsonKeyValue.Builder(UnresolvedColumn.of(alias, key));
    }

    public static <R> JsonKeyValue.Builder key(Alias<R> alias, Function1<R,String> key) {
        return new JsonKeyValue.Builder(ResolvedColumn.of(alias, key));
    }

    public static <R> JsonKeyValue.Builder key(Alias<R> alias, FunctionOptional1<R,String> key) {
        return new JsonKeyValue.Builder(ResolvedColumn.of(alias, key));
    }

    public static JsonKeyValue.Builder key(Label<String> key) {
        return new JsonKeyValue.Builder(UnresolvedColumn.of(key));
    }

    public static JsonKeyValue.Builder key(String alias, Label<String> key) {
        return new JsonKeyValue.Builder(UnresolvedColumn.of(alias, key));
    }
}
