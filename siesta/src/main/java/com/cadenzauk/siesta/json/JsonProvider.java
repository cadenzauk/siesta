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

package com.cadenzauk.siesta.json;

import com.cadenzauk.core.reflect.Factory;
import com.cadenzauk.core.reflect.util.ClassUtil;
import com.cadenzauk.core.util.Lazy;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;
import java.util.Map;

public interface JsonProvider {
    static JsonProvider getInstance() {
        return JsonProviderFactory.provider.get();
    }

    String evaluateJsonPath(String json, String jsonPath);

    String constructJsonObject(Map<String, Object> entries);

    class JsonProviderFactory {
        private static final Lazy<JsonProvider> provider = new Lazy<>(JsonProviderFactory::loadProvider);
        private static final String DEFAULT_PROVIDER = "com.cadenzauk.siesta.jackson.JacksonJsonProvider";

        private static JsonProvider loadProvider() {
            String className = System.getProperty("com.cadenzauk.json.provider", DEFAULT_PROVIDER);
            Class<?> jacksonClass = ClassUtil.forName(className)
                .orElseThrow(() -> new JsonProviderException(className + " was not found in your class path." +
                    (StringUtils.equals(className, DEFAULT_PROVIDER) ? "  Please add a dependency on com.cadenzauk:siesta-jackson" : "")));

            return (JsonProvider) Factory.forClass(jacksonClass).get();
        }
    }
}

