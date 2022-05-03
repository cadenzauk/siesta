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
