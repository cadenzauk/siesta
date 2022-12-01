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
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class JacksonJsonProviderTest {
    @Test
    void evaluateJsonPathReturnsEvaluatesThePath() {
        JsonProvider sut = new JacksonJsonProvider();

        String result = sut.evaluateJsonPath("{\"foo\":\"abc\"}", "$.foo");

        assertThat(result, is("abc"));
    }

    @Test
    void evaluateJsonPathReturnsNullIfJsonIsNull() {
        JsonProvider sut = new JacksonJsonProvider();

        String result = sut.evaluateJsonPath(null, "$.foo");

        assertThat(result, nullValue());
    }

    @Test
    void evaluateJsonPathThrowsIfPathIsNull() {
        JsonProvider sut = new JacksonJsonProvider();

        calling(() -> sut.evaluateJsonPath("{}", null))
            .shouldThrow(IllegalArgumentException.class)
            .withMessage("Path cannot be null or empty.");
    }

    @Test
    void evaluateJsonPathThrowsIfPathIsEmpty() {
        JsonProvider sut = new JacksonJsonProvider();

        calling(() -> sut.evaluateJsonPath("{}", ""))
            .shouldThrow(IllegalArgumentException.class)
            .withMessage("Path cannot be null or empty.");
    }

    @Test
    void evaluateJsonPathThrowsIfPathIsWhitespace() {
        JsonProvider sut = new JacksonJsonProvider();

        calling(() -> sut.evaluateJsonPath("{}", " "))
            .shouldThrow(IllegalArgumentException.class)
            .withMessage("Path cannot be null or empty.");
    }
}
