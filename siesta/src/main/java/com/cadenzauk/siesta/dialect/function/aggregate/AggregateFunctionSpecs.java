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

package com.cadenzauk.siesta.dialect.function.aggregate;

import com.cadenzauk.siesta.dialect.function.FunctionName;
import com.cadenzauk.siesta.dialect.function.FunctionRegistry;
import com.cadenzauk.siesta.dialect.function.SimpleFunctionSpec;

public class AggregateFunctionSpecs {
    public static final FunctionName MAX = new FunctionName("max");
    public static final FunctionName MIN = new FunctionName("min");
    public static final FunctionName SUM = new FunctionName("sum");
    public static final FunctionName AVG = new FunctionName("avg");
    public static final FunctionName COUNT = new FunctionName("count");
    public static final FunctionName COUNT_BIG = new FunctionName("count_big");
    public static final FunctionName COUNT_DISTINCT = new FunctionName("count_distinct");
    public static final FunctionName COUNT_BIG_DISTINCT = new FunctionName("count_big_distinct");

    public static void registerDefaults(FunctionRegistry functions) {
        functions.register(MAX, SimpleFunctionSpec.of("max"));
        functions.register(MIN, SimpleFunctionSpec.of("min"));
        functions.register(SUM, SimpleFunctionSpec.of("sum"));
        functions.register(AVG, SimpleFunctionSpec.of("avg"));
        functions.register(COUNT, SimpleFunctionSpec.of("count"));
        functions.register(COUNT_BIG, SimpleFunctionSpec.of("count_big"));
        functions.register(COUNT_DISTINCT, CountDistinctFunctionSpec.of("count"));
        functions.register(COUNT_BIG_DISTINCT, CountDistinctFunctionSpec.of("count_big"));
    }
}
