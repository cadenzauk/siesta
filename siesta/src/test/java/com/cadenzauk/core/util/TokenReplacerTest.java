/*
 * Copyright (c) 2023 Cadenza United Kingdom Limited
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

package com.cadenzauk.core.util;

import com.cadenzauk.core.reflect.Factory;
import com.cadenzauk.core.sql.PreparedStatementUtil;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class TokenReplacerTest {
    @ParameterizedTest
    @CsvSource({
        "'create table ${schemaName}.${tableName}', 'create table SIESTA.FRED'",
        "'create table ${schemaName}.${tableName}(etc)', 'create table SIESTA.FRED(etc)'",
        "'${create} table ${schemaName}.${tableName}(${columns})', 'create table SIESTA.FRED(id int, name varchar(20))'",
        "'', ''",
        ", ''",
    })
    void canReplaceTokens(String input, String expected) {
        TokenReplacer sut = new TokenReplacer(ImmutableMap.of("tableName", "FRED", "schemaName", "SIESTA", "columns", "id int, name varchar(20)", "create", "create"));

        String result = sut.replace(input);

        assertThat(result, is(expected));
    }

    @Test
    void throwsOnUndefinedToken() {
        TokenReplacer sut = new TokenReplacer(ImmutableMap.of("tableName", "FRED", "schemaName", "SIESTA"));
        calling(() -> sut.replace("create table ${schemaName}.${tableName}(${columns})"))
            .shouldThrow(IllegalArgumentException.class)
            .withMessage("No token 'columns' defined.  Available tokens are: schemaName, tableName.");
    }
}

