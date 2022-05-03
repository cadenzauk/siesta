package com.cadenzauk.siesta.dialect.function.json;

import com.cadenzauk.siesta.dialect.function.FunctionName;
import com.cadenzauk.siesta.dialect.function.FunctionRegistry;

public class JsonFunctionSpecs {
    public static final FunctionName JSON_OBJECT = new FunctionName("json_object");
    public static final FunctionName JSONB_OBJECT = new FunctionName("jsonb_object");

    public static void registerDefaults(FunctionRegistry functions) {
        functions.register(JSON_OBJECT, new JsonObjectFunctionSpec("json_object"));
        functions.register(JSONB_OBJECT, new JsonObjectFunctionSpec("json_object"));
    }
}
