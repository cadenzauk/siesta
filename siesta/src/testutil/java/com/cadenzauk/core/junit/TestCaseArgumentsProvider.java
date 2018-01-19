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

package com.cadenzauk.core.junit;

import com.cadenzauk.core.reflect.Factory;
import com.cadenzauk.core.reflect.TypeInfo;
import com.cadenzauk.core.reflect.util.MethodUtil;
import com.cadenzauk.core.util.ObjectStringParser;
import com.cadenzauk.core.util.StringParser;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.lang.reflect.Method;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TestCaseArgumentsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        Method testMethod = context.getRequiredTestMethod();
        StringParser<Object> stringParser = stringParser(testMethod);
        return MethodUtil.annotations(TestCase.class, testMethod).map(testCase -> arguments(testCase, testMethod, stringParser));
    }

    @SuppressWarnings("unchecked")
    private static StringParser<Object> stringParser(Method testMethod) {
        String nullValue = MethodUtil.annotations(NullValue.class, testMethod)
            .findFirst()
            .map(NullValue::value)
            .orElse("null");

        return MethodUtil.annotations(ArgumentParser.class, testMethod)
            .findFirst()
            .map(ArgumentParser::value)
            .map(Factory::forClass)
            .map(Supplier::get)
            .map(StringParser.class::cast)
            .orElseGet(() -> new ObjectStringParser(nullValue));
    }

    private Arguments arguments(TestCase testCase, Method testMethod, StringParser<Object> stringParser) {
        return Arguments.of(IntStream.range(0, testMethod.getParameterCount())
            .mapToObj(i -> argument(i, testCase.value(), testMethod, stringParser))
            .toArray());
    }

    private Object argument(int i, String[] value, Method testMethod, StringParser<Object> stringParser) {
        String stringArg = i < value.length ? value[i] : "";
        TypeInfo typeInfo = TypeInfo.ofParameter(testMethod, i);
        return stringParser.parse(stringArg, typeInfo);
    }
}
