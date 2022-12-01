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

package com.cadenzauk.siesta.jackson;

import com.cadenzauk.siesta.json.JsonProvider;
import com.cadenzauk.siesta.json.JsonProviderException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;

public class JacksonJsonProvider implements JsonProvider {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String constructJsonObject(Map<String, Object> entries) {
        try {
            return mapper.writeValueAsString(entries);
        } catch (JsonProcessingException e) {
            throw new JsonProviderException("Unable to construct JSON object.", e);
        }
    }

    @Override
    public String evaluateJsonPath(String json, String jsonPath) {
        if (json == null) {
            return null;
        }
        if (StringUtils.isBlank(jsonPath)) {
            throw new IllegalArgumentException("Path cannot be null or empty.");
        }
        DocumentContext jsonContext = JsonPath.parse(json);
        Object result = jsonContext.read(jsonPath);
        return result == null ? null : result.toString();
    }
}
