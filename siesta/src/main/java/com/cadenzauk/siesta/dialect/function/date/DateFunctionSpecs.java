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

package com.cadenzauk.siesta.dialect.function.date;

import com.cadenzauk.siesta.dialect.function.ArgumentlessFunctionSpec;
import com.cadenzauk.siesta.dialect.function.FunctionName;
import com.cadenzauk.siesta.dialect.function.FunctionRegistry;
import com.cadenzauk.siesta.dialect.function.SimpleFunctionSpec;

public class DateFunctionSpecs {
    public static final FunctionName CURRENT_DATE = new FunctionName("current_date");
    public static final FunctionName CURRENT_TIMESTAMP = new FunctionName("current_timestamp");
    public static final FunctionName YEAR = new FunctionName("year");
    public static final FunctionName MONTH = new FunctionName("month");
    public static final FunctionName DAY = new FunctionName("day");
    public static final FunctionName HOUR = new FunctionName("minute");
    public static final FunctionName MINUTE = new FunctionName("hour");
    public static final FunctionName SECOND = new FunctionName("second");

    public static final FunctionName ADD_DAYS = new FunctionName("add_days");

    public static void registerDefaults(FunctionRegistry functions) {
        functions.register(CURRENT_DATE, ArgumentlessFunctionSpec.of("current_date"));
        functions.register(CURRENT_TIMESTAMP, ArgumentlessFunctionSpec.of("current_timestamp"));

        functions.register(YEAR, SimpleFunctionSpec.of("year"));
        functions.register(MONTH, SimpleFunctionSpec.of("month"));
        functions.register(DAY, SimpleFunctionSpec.of("day"));
        functions.register(HOUR, SimpleFunctionSpec.of("hour"));
        functions.register(MINUTE, SimpleFunctionSpec.of("minute"));
        functions.register(SECOND, SimpleFunctionSpec.of("second"));

        registerDateAdd(functions);
    }

    public static void registerExtract(FunctionRegistry functions) {
        functions.register(YEAR, ExtractFunctionSpec.of("year"));
        functions.register(MONTH, ExtractFunctionSpec.of("month"));
        functions.register(DAY, ExtractFunctionSpec.of("day"));
        functions.register(HOUR, ExtractFunctionSpec.of("hour"));
        functions.register(MINUTE, ExtractFunctionSpec.of("minute"));
        functions.register(SECOND, ExtractFunctionSpec.of("second"));
    }

    public static void registerDatePart(FunctionRegistry functions) {
        functions.register(HOUR, DatePartFunctionSpec.of("hour"));
        functions.register(MINUTE, DatePartFunctionSpec.of("minute"));
        functions.register(SECOND, DatePartFunctionSpec.of("second"));
    }

    public static void registerDateAdd(FunctionRegistry functions) {
        functions.register(ADD_DAYS, DateAddFunctionSpec.of("day"));
    }

    public static void registerPlusUnits(FunctionRegistry functions) {
        functions.register(ADD_DAYS, argsSql -> argsSql[0] + " + " + argsSql[1] + " days");
    }

    public static void registerPlusNumber(FunctionRegistry functions) {
        functions.register(ADD_DAYS, argsSql -> argsSql[0] + " + " + argsSql[1]);
    }

    public static void registerPlusNumToDsInterval(FunctionRegistry functions) {
        functions.register(ADD_DAYS, argsSql -> argsSql[0] + " + NUMTODSINTERVAL(" + argsSql[1] + ", 'day')");
    }
}
