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
