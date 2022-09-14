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
import com.cadenzauk.core.lang.CompositeAutoCloseable;
import com.cadenzauk.core.lang.StringUtil;
import com.cadenzauk.core.sql.ConnectionUtil;
import com.cadenzauk.core.sql.DataSourceUtil;
import com.cadenzauk.core.util.UtilityClass;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Dialect;
import com.cadenzauk.siesta.json.BinaryJson;
import com.cadenzauk.siesta.json.Json;
import com.cadenzauk.siesta.json.JsonSupport;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;

import static com.cadenzauk.siesta.dialect.function.json.JsonFunctionSpecs.JSONB_OBJECT;
import static com.cadenzauk.siesta.dialect.function.json.JsonFunctionSpecs.JSONB_VALUE;
import static com.cadenzauk.siesta.dialect.function.json.JsonFunctionSpecs.JSON_OBJECT;
import static com.cadenzauk.siesta.dialect.function.json.JsonFunctionSpecs.JSON_VALUE;
import static com.cadenzauk.siesta.grammar.expression.TypedExpression.column;
import static com.cadenzauk.siesta.grammar.expression.TypedExpression.literal;

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

    //--

    public static TypedExpression<Json> jsonValue(String json, String path) {
        return SqlFunction.of(JSON_VALUE, Json.class, ValueExpression.of(Json.of(json)), literal(path));
    }

    public static TypedExpression<Json> jsonValue(TypedExpression<Json> json, String path) {
        return SqlFunction.of(JSON_VALUE, Json.class, json, literal(path));
    }

    public static <R> TypedExpression<Json> jsonValue(Function1<R,Json> json, String path) {
        return SqlFunction.of(JSON_VALUE, Json.class, UnresolvedColumn.of(json), literal(path));
    }

    public static <R> TypedExpression<Json> jsonValue(FunctionOptional1<R,Json> json, String path) {
        return SqlFunction.of(JSON_VALUE, Json.class, UnresolvedColumn.of(json), literal(path));
    }

    public static <R> TypedExpression<Json> jsonValue(String alias, Function1<R,Json> json, String path) {
        return SqlFunction.of(JSON_VALUE, Json.class, UnresolvedColumn.of(alias, json), literal(path));
    }

    public static <R> TypedExpression<Json> jsonValue(String alias, FunctionOptional1<R,Json> json, String path) {
        return SqlFunction.of(JSON_VALUE, Json.class, UnresolvedColumn.of(alias, json), literal(path));
    }

    public static <R> TypedExpression<Json> jsonValue(Alias<R> alias, Function1<R,Json> json, String path) {
        return SqlFunction.of(JSON_VALUE, Json.class, ResolvedColumn.of(alias, json), literal(path));
    }

    public static <R> TypedExpression<Json> jsonValue(Alias<R> alias, FunctionOptional1<R,Json> json, String path) {
        return SqlFunction.of(JSON_VALUE, Json.class, ResolvedColumn.of(alias, json), literal(path));
    }

    public static <R> TypedExpression<Json> jsonValue(Label<Json> json, String path) {
        return SqlFunction.of(JSON_VALUE, Json.class, UnresolvedColumn.of(json), literal(path));
    }

    public static <R> TypedExpression<Json> jsonValue(String alias, Label<Json> json, String path) {
        return SqlFunction.of(JSON_VALUE, Json.class, UnresolvedColumn.of(alias, json), literal(path));
    }

    //--

    public static TypedExpression<BinaryJson> jsonbValue(String jsonb, String path) {
        return SqlFunction.of(JSONB_VALUE, BinaryJson.class, ValueExpression.of(BinaryJson.of(jsonb)), literal(path));
    }

    public static TypedExpression<BinaryJson> jsonbValue(TypedExpression<BinaryJson> jsonb, String path) {
        return SqlFunction.of(JSONB_VALUE, BinaryJson.class, jsonb, literal(path));
    }

    public static <R> TypedExpression<BinaryJson> jsonbValue(Function1<R,BinaryJson> jsonb, String path) {
        return SqlFunction.of(JSONB_VALUE, BinaryJson.class, UnresolvedColumn.of(jsonb), literal(path));
    }

    public static <R> TypedExpression<BinaryJson> jsonbValue(FunctionOptional1<R,BinaryJson> jsonb, String path) {
        return SqlFunction.of(JSONB_VALUE, BinaryJson.class, UnresolvedColumn.of(jsonb), literal(path));
    }

    public static <R> TypedExpression<BinaryJson> jsonbValue(String alias, Function1<R,BinaryJson> jsonb, String path) {
        return SqlFunction.of(JSONB_VALUE, BinaryJson.class, UnresolvedColumn.of(alias, jsonb), literal(path));
    }

    public static <R> TypedExpression<BinaryJson> jsonbValue(String alias, FunctionOptional1<R,BinaryJson> jsonb, String path) {
        return SqlFunction.of(JSONB_VALUE, BinaryJson.class, UnresolvedColumn.of(alias, jsonb), literal(path));
    }

    public static <R> TypedExpression<BinaryJson> jsonbValue(Alias<R> alias, Function1<R,BinaryJson> jsonb, String path) {
        return SqlFunction.of(JSONB_VALUE, BinaryJson.class, ResolvedColumn.of(alias, jsonb), literal(path));
    }

    public static <R> TypedExpression<BinaryJson> jsonbValue(Alias<R> alias, FunctionOptional1<R,BinaryJson> jsonb, String path) {
        return SqlFunction.of(JSONB_VALUE, BinaryJson.class, ResolvedColumn.of(alias, jsonb), literal(path));
    }

    public static <R> TypedExpression<BinaryJson> jsonbValue(Label<BinaryJson> jsonb, String path) {
        return SqlFunction.of(JSONB_VALUE, BinaryJson.class, UnresolvedColumn.of(jsonb), literal(path));
    }

    public static <R> TypedExpression<BinaryJson> jsonbValue(String alias, Label<BinaryJson> jsonb, String path) {
        return SqlFunction.of(JSONB_VALUE, BinaryJson.class, UnresolvedColumn.of(alias, jsonb), literal(path));
    }

    //--

    public static void initialise(DataSource dataSource, Database database) {
        database.dialect().missingJsonFunctions().forEach(func -> {
            String sql = database.dialect().createJavaProcSql(database, JsonSupport.class, StringUtil.upperToCamel(func.name().toUpperCase()), func.name());
            if (StringUtils.isNotBlank(sql)) {
                try (CompositeAutoCloseable closer = new CompositeAutoCloseable()) {
                    Connection connection = closer.add(DataSourceUtil.connection(dataSource));
                    ConnectionUtil.execute(connection, sql);
                }
            }
        });
    }
}
