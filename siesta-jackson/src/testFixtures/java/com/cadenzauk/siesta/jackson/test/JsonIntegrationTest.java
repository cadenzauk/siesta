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

package com.cadenzauk.siesta.jackson.test;

import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.core.util.Lazy;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Dialect;
import com.cadenzauk.siesta.IntegrationTest;
import com.cadenzauk.siesta.grammar.expression.JsonFunctions;
import com.cadenzauk.siesta.json.BinaryJson;
import com.cadenzauk.siesta.json.Json;
import com.cadenzauk.siesta.json.JsonProvider;
import com.cadenzauk.siesta.model.JsonDataRow;
import com.cadenzauk.siesta.model.SalespersonRow;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;

import java.util.Optional;

import static com.cadenzauk.siesta.grammar.expression.JsonFunctions.jsonValue;
import static com.cadenzauk.siesta.grammar.expression.JsonFunctions.jsonbValue;
import static com.cadenzauk.siesta.model.TestDatabase.testDatabase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ContextConfiguration
public abstract class JsonIntegrationTest extends IntegrationTest {
    private static final ObjectMapper objectMapper = new ObjectMapper()
        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        .registerModule(new Jdk8Module());

    @Configuration
    public static class JsonConfig extends Config {
        @Bean
        public Object initializeJsonFunctions(DataSource dataSource, Database database) {
            JsonFunctions.initialise(dataSource, database);
            return new Object();
        }
    }

    @Test
    void canExecuteJsonValue() {
        Database database = testDatabase(dataSource, dialect);

        Json result = database.select(jsonValue("{\"a\":[1,2,3]}", "$.a[1]")).single();

        assertThat(result, is(Json.of("2")));
    }

    @Test
    void canQueryBinaryJsonValue() throws JsonProcessingException {
        Database database = testDatabase(dataSource, dialect);
        SalespersonRow fred = aRandomSalesperson(s -> s.firstName("Fred").surname("Flintstone"));
        String json = objectMapper.writeValueAsString(fred);
        JsonDataRow jsonDataRow = new JsonDataRow(newId(), Json.of(json), Optional.of(BinaryJson.of(json)));
        database.insert(jsonDataRow);

        Tuple2<Json, BinaryJson> result = database
            .from(JsonDataRow.class)
            .select(jsonValue(JsonDataRow::data, "$.firstName"))
            .comma(jsonbValue(JsonDataRow::dataBinary, "$.surname"))
            .where(JsonDataRow::jsonId).isEqualTo(jsonDataRow.jsonId())
            .single();

        assertThat(result.item1().data(), is("Fred"));
        assertThat(result.item2().data(), is("Flintstone"));
    }


    private static JsonNode parse(Json json) throws JsonProcessingException {
        return objectMapper.readTree(json.data());
    }

    private static JsonNode parse(BinaryJson json) throws JsonProcessingException {
        return objectMapper.readTree(json.data());
    }

}
