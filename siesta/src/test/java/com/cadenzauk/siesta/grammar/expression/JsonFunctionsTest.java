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

import com.cadenzauk.core.RandomValues;
import com.cadenzauk.siesta.model.TestRow;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

import static com.cadenzauk.core.testutil.IsUtilityClass.isUtilityClass;
import static com.cadenzauk.siesta.grammar.expression.JsonFunctions.jsonObject;
import static com.cadenzauk.siesta.grammar.expression.JsonFunctions.key;
import static com.cadenzauk.siesta.grammar.expression.TypedExpression.literal;
import static org.apache.commons.lang3.ArrayUtils.toArray;

class JsonFunctionsTest extends FunctionTest {
    @Test
    void isUtility() {
        MatcherAssert.assertThat(JsonFunctions.class, isUtilityClass());
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> parametersForFunctionTest() {
        return Stream.of(
            testCase(s -> jsonObject(key("name").value("bob")), "json_object(key ? value ?)", toArray("name", "bob")),
            testCase(s -> jsonObject(key(literal("name")).value("bob")), "json_object(key 'name' value ?)", toArray("bob")),
            testCase(s -> jsonObject(key(TestRow::stringReq).value("bob")), "json_object(key s.STRING_REQ value ?)", toArray("bob")),
            testCase(s -> jsonObject(key(TestRow::stringOpt).value("bob")), "json_object(key s.STRING_OPT value ?)", toArray("bob")),
            testCase(s -> jsonObject(key("s", TestRow::stringReq).value("bob")), "json_object(key s.STRING_REQ value ?)", toArray("bob")),
            testCase(s -> jsonObject(key("s", TestRow::stringOpt).value("bob")), "json_object(key s.STRING_OPT value ?)", toArray("bob")),
            testCase(s -> jsonObject(key(s, TestRow::stringReq).value("bob")), "json_object(key s.STRING_REQ value ?)", toArray("bob")),
            testCase(s -> jsonObject(key(s, TestRow::stringOpt).value("bob")), "json_object(key s.STRING_OPT value ?)", toArray("bob")),

            testCase(s -> jsonObject(key("name").value(literal("bob"))), "json_object(key ? value 'bob')", toArray("name")),
            testCase(s -> jsonObject(key("name").value(TestRow::stringReq)), "json_object(key ? value s.STRING_REQ)", toArray("name")),
            testCase(s -> jsonObject(key("name").value(TestRow::stringOpt)), "json_object(key ? value s.STRING_OPT)", toArray("name")),
            testCase(s -> jsonObject(key("name").value("s", TestRow::stringReq)), "json_object(key ? value s.STRING_REQ)", toArray("name")),
            testCase(s -> jsonObject(key("name").value("s", TestRow::stringOpt)), "json_object(key ? value s.STRING_OPT)", toArray("name")),
            testCase(s -> jsonObject(key("name").value(s, TestRow::stringReq)), "json_object(key ? value s.STRING_REQ)", toArray("name")),
            testCase(s -> jsonObject(key("name").value(s, TestRow::stringOpt)), "json_object(key ? value s.STRING_OPT)", toArray("name")),

            testCase(s -> jsonObject(key("name").value(TestRow::stringReq), key("title").value(TestRow::stringOpt)), "json_object(key ? value s.STRING_REQ, key ? value s.STRING_OPT)", toArray("name", "title")),
            testCase(s -> jsonObject(key(literal("name")).value(TestRow::stringReq), key(literal("title")).value(TestRow::stringOpt)), "json_object(key 'name' value s.STRING_REQ, key 'title' value s.STRING_OPT)", toArray())
        );
    }
}
