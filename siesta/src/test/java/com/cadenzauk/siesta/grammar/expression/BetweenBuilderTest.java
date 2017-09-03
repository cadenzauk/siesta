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

package com.cadenzauk.siesta.grammar.expression;

import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Transaction;
import com.cadenzauk.siesta.dialect.AnsiDialect;
import com.cadenzauk.siesta.grammar.select.InWhereExpectingAnd;
import com.cadenzauk.siesta.model.SalespersonRow;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ObjectArrayArguments;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.function.BiFunction;
import java.util.stream.Stream;

import static com.cadenzauk.siesta.model.TestDatabase.testDatabase;
import static org.apache.commons.lang3.ArrayUtils.toArray;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;

class BetweenBuilderTest {
    @Mock
    private Transaction transaction;
    @Captor
    private ArgumentCaptor<String> sql;
    @Captor
    private ArgumentCaptor<Object[]> args;
    @Captor
    private ArgumentCaptor<RowMapper<?>> rowMapper;

    private static <T> Object[] testCase(BiFunction<BetweenBuilder<String,T>,Alias<SalespersonRow>,T> method, String expectedSql, Object... expectedArgs) {
        return toArray(method, expectedSql, expectedArgs);
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> argsForAnd() {
        return Stream.of(
            ObjectArrayArguments.create(testCase((b, s) -> b.and("Z"), "?", "Z")),
            ObjectArrayArguments.create(testCase((b, s) -> b.and(s.column(SalespersonRow::firstName)), "s.FIRST_NAME")),
            ObjectArrayArguments.create(testCase((b, s) -> b.and(SalespersonRow::firstName), "s.FIRST_NAME")),
            ObjectArrayArguments.create(testCase((b, s) -> b.and(SalespersonRow::middleNames), "s.MIDDLE_NAMES")),
            ObjectArrayArguments.create(testCase((b, s) -> b.and("s", SalespersonRow::firstName), "s.FIRST_NAME")),
            ObjectArrayArguments.create(testCase((b, s) -> b.and("s", SalespersonRow::middleNames), "s.MIDDLE_NAMES")),
            ObjectArrayArguments.create(testCase((b, s) -> b.and(s, SalespersonRow::firstName), "s.FIRST_NAME")),
            ObjectArrayArguments.create(testCase((b, s) -> b.and(s, SalespersonRow::middleNames), "s.MIDDLE_NAMES"))
        );
    }

    @ParameterizedTest
    @MethodSource(names = "argsForAnd")
    void and(BiFunction<BetweenBuilder<String,InWhereExpectingAnd<String>>,Alias<SalespersonRow>,InWhereExpectingAnd<String>> method, String expectedSql, Object[] expectedArgs) {
        MockitoAnnotations.initMocks(this);
        Database database = testDatabase(new AnsiDialect());
        Alias<SalespersonRow> alias = database.table(SalespersonRow.class).as("s");

        BetweenBuilder<String,InWhereExpectingAnd<String>> between = database.from(alias)
            .select(SalespersonRow::firstName, "name")
            .where(SalespersonRow::firstName)
            .isBetween(SalespersonRow::middleNames);
        method.apply(between, alias).list(transaction);

        verify(transaction).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select s.FIRST_NAME as name from SIESTA.SALESPERSON s where s.FIRST_NAME between s.MIDDLE_NAMES and " + expectedSql));
        assertThat(args.getValue(), is(expectedArgs));
    }
}