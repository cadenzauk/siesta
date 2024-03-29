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

package com.cadenzauk.siesta.dialect.function.json;

import com.cadenzauk.siesta.dialect.function.FunctionName;
import com.cadenzauk.siesta.dialect.function.FunctionRegistry;
import com.cadenzauk.siesta.dialect.function.FunctionSpec;
import com.cadenzauk.siesta.dialect.function.SimpleFunctionSpec;

import static org.apache.commons.lang3.ArrayUtils.toArray;

public class JsonFunctionSpecs {
    public static final FunctionName JSON_OBJECT = new FunctionName("json_object");
    public static final FunctionName JSONB_OBJECT = new FunctionName("jsonb_object");
    public static final FunctionName JSON_VALUE = new FunctionName("json_value");
    public static final FunctionName JSONB_VALUE = new FunctionName("jsonb_value");
    public static final FunctionName JSON_FIELD_TEXT = new FunctionName("json_field_text");
    public static final FunctionName JSONB_FIELD_TEXT = new FunctionName("jsonb_field_text");

    public static void registerDefaults(FunctionRegistry functions) {
        functions.register(JSON_OBJECT, new JsonObjectFunctionSpec("json_object"));
        functions.register(JSONB_OBJECT, new JsonObjectFunctionSpec("json_object"));
        functions.register(JSON_VALUE, SimpleFunctionSpec.of("json_value"));
        functions.register(JSONB_VALUE, SimpleFunctionSpec.of("json_value"));
        functions.register(JSON_FIELD_TEXT, defaultJsonField(functions, JSON_VALUE));
        functions.register(JSONB_FIELD_TEXT, defaultJsonField(functions, JSONB_VALUE));
    }

    private static FunctionSpec defaultJsonField(FunctionRegistry functionRegistry, FunctionName functionName) {
        return (scope, args) -> functionRegistry.get(functionName)
            .orElse(SimpleFunctionSpec.of("json_value"))
            .sql(scope, toArray(args[0], "'$." + args[1].substring(1)));
    }
}
