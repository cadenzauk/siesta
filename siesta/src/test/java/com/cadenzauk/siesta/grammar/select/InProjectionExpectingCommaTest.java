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

package com.cadenzauk.siesta.grammar.select;

import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Transaction;
import com.cadenzauk.siesta.dialect.AnsiDialect;
import com.cadenzauk.siesta.grammar.expression.ValueExpression;
import com.cadenzauk.siesta.model.SalespersonRow;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.cadenzauk.siesta.model.TestDatabase.testDatabase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;

class InProjectionExpectingCommaTest {
    @Mock
    private Transaction transaction;
    @Captor
    private ArgumentCaptor<String> sql;
    @Captor
    private ArgumentCaptor<Object[]> args;
    @Captor
    private ArgumentCaptor<RowMapper<?>> rowMapper;

    private static Arguments testCase1(BiFunction<InProjectionExpectingComma1<String>,Alias<SalespersonRow>,Select<?>> method, String expectedSql, Object... expectedArgs) {
        return Arguments.of(method, expectedSql, expectedArgs);
    }

    private static Arguments testCase2(BiFunction<InProjectionExpectingComma2<?,?>,Alias<SalespersonRow>,Select<?>> method, String expectedSql, Object... expectedArgs) {
        return testCase1((x, a) -> method.apply(x.comma(SalespersonRow::salespersonId, "id"), a), "s.SALESPERSON_ID as id, " + expectedSql, expectedArgs);
    }

    private static Arguments testCase3(BiFunction<InProjectionExpectingComma3<?,?,?>,Alias<SalespersonRow>,Select<?>> method, String expectedSql, Object... expectedArgs) {
        return testCase2((x, a) -> method.apply(x.comma(SalespersonRow::salespersonId, "id"), a), "s.SALESPERSON_ID as id, " + expectedSql, expectedArgs);
    }

    private static Arguments testCase4(BiFunction<InProjectionExpectingComma4<?,?,?,?>,Alias<SalespersonRow>,Select<?>> method, String expectedSql, Object... expectedArgs) {
        return testCase3((x, a) -> method.apply(x.comma(SalespersonRow::salespersonId, "id"), a), "s.SALESPERSON_ID as id, " + expectedSql, expectedArgs);
    }

    private static Arguments testCase5(BiFunction<InProjectionExpectingComma5<?,?,?,?,?>,Alias<SalespersonRow>,Select<?>> method, String expectedSql, Object... expectedArgs) {
        return testCase4((x, a) -> method.apply(x.comma(SalespersonRow::salespersonId, "id"), a), "s.SALESPERSON_ID as id, " + expectedSql, expectedArgs);
    }

    private static Arguments testCase6(BiFunction<InProjectionExpectingComma6<?,?,?,?,?,?>,Alias<SalespersonRow>,Select<?>> method, String expectedSql, Object... expectedArgs) {
        return testCase5((x, a) -> method.apply(x.comma(SalespersonRow::salespersonId, "id"), a), "s.SALESPERSON_ID as id, " + expectedSql, expectedArgs);
    }

    private static Arguments testCase7(BiFunction<InProjectionExpectingComma7<?,?,?,?,?,?,?>,Alias<SalespersonRow>,Select<?>> method, String expectedSql, Object... expectedArgs) {
        return testCase6((x, a) -> method.apply(x.comma(SalespersonRow::salespersonId, "id"), a), "s.SALESPERSON_ID as id, " + expectedSql, expectedArgs);
    }

    private static Arguments testCase8(BiFunction<InProjectionExpectingComma8<?,?,?,?,?,?,?,?>,Alias<SalespersonRow>,Select<?>> method, String expectedSql, Object... expectedArgs) {
        return testCase7((x, a) -> method.apply(x.comma(SalespersonRow::salespersonId, "id"), a), "s.SALESPERSON_ID as id, " + expectedSql, expectedArgs);
    }

    private static Stream<Arguments> argsForComma() {
        return Stream.of(
            testCase1((x, a) -> x.comma(ValueExpression.of("ABC")), "\\? as value_\\d+", "ABC"),
            testCase1((x, a) -> x.comma(ValueExpression.of("ABC"), "abc"), "\\? as abc", "ABC"),
            testCase1((x, a) -> x.comma(SalespersonRow::middleNames), "s\\.MIDDLE_NAMES as s_MIDDLE_NAMES"),
            testCase1((x, a) -> x.comma(SalespersonRow::middleNames, "middle"), "s\\.MIDDLE_NAMES as middle"),
            testCase1((x, a) -> x.comma(SalespersonRow::surname), "s\\.SURNAME as s_SURNAME"),
            testCase1((x, a) -> x.comma(SalespersonRow::surname, "surname"), "s\\.SURNAME as surname"),
            testCase1((x, a) -> x.comma("s", SalespersonRow::middleNames), "s\\.MIDDLE_NAMES as s_MIDDLE_NAMES"),
            testCase1((x, a) -> x.comma("s", SalespersonRow::middleNames, "middle"), "s.MIDDLE_NAMES as middle"),
            testCase1((x, a) -> x.comma("s", SalespersonRow::surname), "s\\.SURNAME as s_SURNAME"),
            testCase1((x, a) -> x.comma("s", SalespersonRow::surname, "surname"), "s.SURNAME as surname"),
            testCase1((x, a) -> x.comma(a, SalespersonRow::middleNames), "s\\.MIDDLE_NAMES as s_MIDDLE_NAMES"),
            testCase1((x, a) -> x.comma(a, SalespersonRow::middleNames, "middle"), "s.MIDDLE_NAMES as middle"),
            testCase1((x, a) -> x.comma(a, SalespersonRow::surname), "s\\.SURNAME as s_SURNAME"),
            testCase1((x, a) -> x.comma(a, SalespersonRow::surname, "surname"), "s.SURNAME as surname"),
            testCase1((x, a) -> x.comma(SalespersonRow.class), "s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),
            testCase1((x, a) -> x.comma(SalespersonRow.class, "s"), "s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),
            testCase1(InProjectionExpectingComma1::comma, "s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),

            testCase2((x, a) -> x.comma(ValueExpression.of("ABC")), "\\? as value_\\d+", "ABC"),
            testCase2((x, a) -> x.comma(ValueExpression.of("ABC"), "abc"), "\\? as abc", "ABC"),
            testCase2((x, a) -> x.comma(SalespersonRow::middleNames), "s\\.MIDDLE_NAMES as s_MIDDLE_NAMES"),
            testCase2((x, a) -> x.comma(SalespersonRow::middleNames, "middle"), "s\\.MIDDLE_NAMES as middle"),
            testCase2((x, a) -> x.comma(SalespersonRow::surname), "s\\.SURNAME as s_SURNAME"),
            testCase2((x, a) -> x.comma(SalespersonRow::surname, "surname"), "s\\.SURNAME as surname"),
            testCase2((x, a) -> x.comma("s", SalespersonRow::middleNames), "s\\.MIDDLE_NAMES as s_MIDDLE_NAMES"),
            testCase2((x, a) -> x.comma("s", SalespersonRow::middleNames, "middle"), "s.MIDDLE_NAMES as middle"),
            testCase2((x, a) -> x.comma("s", SalespersonRow::surname), "s\\.SURNAME as s_SURNAME"),
            testCase2((x, a) -> x.comma("s", SalespersonRow::surname, "surname"), "s.SURNAME as surname"),
            testCase2((x, a) -> x.comma(a, SalespersonRow::middleNames), "s\\.MIDDLE_NAMES as s_MIDDLE_NAMES"),
            testCase2((x, a) -> x.comma(a, SalespersonRow::middleNames, "middle"), "s.MIDDLE_NAMES as middle"),
            testCase2((x, a) -> x.comma(a, SalespersonRow::surname), "s\\.SURNAME as s_SURNAME"),
            testCase2((x, a) -> x.comma(a, SalespersonRow::surname, "surname"), "s.SURNAME as surname"),
            testCase2((x, a) -> x.comma(SalespersonRow.class), "s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),
            testCase2((x, a) -> x.comma(SalespersonRow.class, "s"), "s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),
            testCase2(InProjectionExpectingComma2::comma, "s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),

            testCase3((x, a) -> x.comma(ValueExpression.of("ABC")), "\\? as value_\\d+", "ABC"),
            testCase3((x, a) -> x.comma(ValueExpression.of("ABC"), "abc"), "\\? as abc", "ABC"),
            testCase3((x, a) -> x.comma(SalespersonRow::middleNames), "s\\.MIDDLE_NAMES as s_MIDDLE_NAMES"),
            testCase3((x, a) -> x.comma(SalespersonRow::middleNames, "middle"), "s\\.MIDDLE_NAMES as middle"),
            testCase3((x, a) -> x.comma(SalespersonRow::surname), "s\\.SURNAME as s_SURNAME"),
            testCase3((x, a) -> x.comma(SalespersonRow::surname, "surname"), "s\\.SURNAME as surname"),
            testCase3((x, a) -> x.comma("s", SalespersonRow::middleNames), "s\\.MIDDLE_NAMES as s_MIDDLE_NAMES"),
            testCase3((x, a) -> x.comma("s", SalespersonRow::middleNames, "middle"), "s.MIDDLE_NAMES as middle"),
            testCase3((x, a) -> x.comma("s", SalespersonRow::surname), "s\\.SURNAME as s_SURNAME"),
            testCase3((x, a) -> x.comma("s", SalespersonRow::surname, "surname"), "s.SURNAME as surname"),
            testCase3((x, a) -> x.comma(a, SalespersonRow::middleNames), "s\\.MIDDLE_NAMES as s_MIDDLE_NAMES"),
            testCase3((x, a) -> x.comma(a, SalespersonRow::middleNames, "middle"), "s.MIDDLE_NAMES as middle"),
            testCase3((x, a) -> x.comma(a, SalespersonRow::surname), "s\\.SURNAME as s_SURNAME"),
            testCase3((x, a) -> x.comma(a, SalespersonRow::surname, "surname"), "s.SURNAME as surname"),
            testCase3((x, a) -> x.comma(SalespersonRow.class), "s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),
            testCase3((x, a) -> x.comma(SalespersonRow.class, "s"), "s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),
            testCase3(InProjectionExpectingComma3::comma, "s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),

            testCase4((x, a) -> x.comma(ValueExpression.of("ABC")), "\\? as value_\\d+", "ABC"),
            testCase4((x, a) -> x.comma(ValueExpression.of("ABC"), "abc"), "\\? as abc", "ABC"),
            testCase4((x, a) -> x.comma(SalespersonRow::middleNames), "s\\.MIDDLE_NAMES as s_MIDDLE_NAMES"),
            testCase4((x, a) -> x.comma(SalespersonRow::middleNames, "middle"), "s\\.MIDDLE_NAMES as middle"),
            testCase4((x, a) -> x.comma(SalespersonRow::surname), "s\\.SURNAME as s_SURNAME"),
            testCase4((x, a) -> x.comma(SalespersonRow::surname, "surname"), "s\\.SURNAME as surname"),
            testCase4((x, a) -> x.comma("s", SalespersonRow::middleNames), "s\\.MIDDLE_NAMES as s_MIDDLE_NAMES"),
            testCase4((x, a) -> x.comma("s", SalespersonRow::middleNames, "middle"), "s.MIDDLE_NAMES as middle"),
            testCase4((x, a) -> x.comma("s", SalespersonRow::surname), "s\\.SURNAME as s_SURNAME"),
            testCase4((x, a) -> x.comma("s", SalespersonRow::surname, "surname"), "s.SURNAME as surname"),
            testCase4((x, a) -> x.comma(a, SalespersonRow::middleNames), "s\\.MIDDLE_NAMES as s_MIDDLE_NAMES"),
            testCase4((x, a) -> x.comma(a, SalespersonRow::middleNames, "middle"), "s.MIDDLE_NAMES as middle"),
            testCase4((x, a) -> x.comma(a, SalespersonRow::surname), "s\\.SURNAME as s_SURNAME"),
            testCase4((x, a) -> x.comma(a, SalespersonRow::surname, "surname"), "s.SURNAME as surname"),
            testCase4((x, a) -> x.comma(SalespersonRow.class), "s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),
            testCase4((x, a) -> x.comma(SalespersonRow.class, "s"), "s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),
            testCase4(InProjectionExpectingComma4::comma, "s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),

            testCase5((x, a) -> x.comma(ValueExpression.of("ABC")), "\\? as value_\\d+", "ABC"),
            testCase5((x, a) -> x.comma(ValueExpression.of("ABC"), "abc"), "\\? as abc", "ABC"),
            testCase5((x, a) -> x.comma(SalespersonRow::middleNames), "s\\.MIDDLE_NAMES as s_MIDDLE_NAMES"),
            testCase5((x, a) -> x.comma(SalespersonRow::middleNames, "middle"), "s\\.MIDDLE_NAMES as middle"),
            testCase5((x, a) -> x.comma(SalespersonRow::surname), "s\\.SURNAME as s_SURNAME"),
            testCase5((x, a) -> x.comma(SalespersonRow::surname, "surname"), "s\\.SURNAME as surname"),
            testCase5((x, a) -> x.comma("s", SalespersonRow::middleNames), "s\\.MIDDLE_NAMES as s_MIDDLE_NAMES"),
            testCase5((x, a) -> x.comma("s", SalespersonRow::middleNames, "middle"), "s.MIDDLE_NAMES as middle"),
            testCase5((x, a) -> x.comma("s", SalespersonRow::surname), "s\\.SURNAME as s_SURNAME"),
            testCase5((x, a) -> x.comma("s", SalespersonRow::surname, "surname"), "s.SURNAME as surname"),
            testCase5((x, a) -> x.comma(a, SalespersonRow::middleNames), "s\\.MIDDLE_NAMES as s_MIDDLE_NAMES"),
            testCase5((x, a) -> x.comma(a, SalespersonRow::middleNames, "middle"), "s.MIDDLE_NAMES as middle"),
            testCase5((x, a) -> x.comma(a, SalespersonRow::surname), "s\\.SURNAME as s_SURNAME"),
            testCase5((x, a) -> x.comma(a, SalespersonRow::surname, "surname"), "s.SURNAME as surname"),
            testCase5((x, a) -> x.comma(SalespersonRow.class), "s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),
            testCase5((x, a) -> x.comma(SalespersonRow.class, "s"), "s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),
            testCase5(InProjectionExpectingComma5::comma, "s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),

            testCase6((x, a) -> x.comma(ValueExpression.of("ABC")), "\\? as value_\\d+", "ABC"),
            testCase6((x, a) -> x.comma(ValueExpression.of("ABC"), "abc"), "\\? as abc", "ABC"),
            testCase6((x, a) -> x.comma(SalespersonRow::middleNames), "s\\.MIDDLE_NAMES as s_MIDDLE_NAMES"),
            testCase6((x, a) -> x.comma(SalespersonRow::middleNames, "middle"), "s\\.MIDDLE_NAMES as middle"),
            testCase6((x, a) -> x.comma(SalespersonRow::surname), "s\\.SURNAME as s_SURNAME"),
            testCase6((x, a) -> x.comma(SalespersonRow::surname, "surname"), "s\\.SURNAME as surname"),
            testCase6((x, a) -> x.comma("s", SalespersonRow::middleNames), "s\\.MIDDLE_NAMES as s_MIDDLE_NAMES"),
            testCase6((x, a) -> x.comma("s", SalespersonRow::middleNames, "middle"), "s.MIDDLE_NAMES as middle"),
            testCase6((x, a) -> x.comma("s", SalespersonRow::surname), "s\\.SURNAME as s_SURNAME"),
            testCase6((x, a) -> x.comma("s", SalespersonRow::surname, "surname"), "s.SURNAME as surname"),
            testCase6((x, a) -> x.comma(a, SalespersonRow::middleNames), "s\\.MIDDLE_NAMES as s_MIDDLE_NAMES"),
            testCase6((x, a) -> x.comma(a, SalespersonRow::middleNames, "middle"), "s.MIDDLE_NAMES as middle"),
            testCase6((x, a) -> x.comma(a, SalespersonRow::surname), "s\\.SURNAME as s_SURNAME"),
            testCase6((x, a) -> x.comma(a, SalespersonRow::surname, "surname"), "s.SURNAME as surname"),
            testCase6((x, a) -> x.comma(SalespersonRow.class), "s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),
            testCase6((x, a) -> x.comma(SalespersonRow.class, "s"), "s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),
            testCase6(InProjectionExpectingComma6::comma, "s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),

            testCase7((x, a) -> x.comma(ValueExpression.of("ABC")), "\\? as value_\\d+", "ABC"),
            testCase7((x, a) -> x.comma(ValueExpression.of("ABC"), "abc"), "\\? as abc", "ABC"),
            testCase7((x, a) -> x.comma(SalespersonRow::middleNames), "s\\.MIDDLE_NAMES as s_MIDDLE_NAMES"),
            testCase7((x, a) -> x.comma(SalespersonRow::middleNames, "middle"), "s\\.MIDDLE_NAMES as middle"),
            testCase7((x, a) -> x.comma(SalespersonRow::surname), "s\\.SURNAME as s_SURNAME"),
            testCase7((x, a) -> x.comma(SalespersonRow::surname, "surname"), "s\\.SURNAME as surname"),
            testCase7((x, a) -> x.comma("s", SalespersonRow::middleNames), "s\\.MIDDLE_NAMES as s_MIDDLE_NAMES"),
            testCase7((x, a) -> x.comma("s", SalespersonRow::middleNames, "middle"), "s.MIDDLE_NAMES as middle"),
            testCase7((x, a) -> x.comma("s", SalespersonRow::surname), "s\\.SURNAME as s_SURNAME"),
            testCase7((x, a) -> x.comma("s", SalespersonRow::surname, "surname"), "s.SURNAME as surname"),
            testCase7((x, a) -> x.comma(a, SalespersonRow::middleNames), "s\\.MIDDLE_NAMES as s_MIDDLE_NAMES"),
            testCase7((x, a) -> x.comma(a, SalespersonRow::middleNames, "middle"), "s.MIDDLE_NAMES as middle"),
            testCase7((x, a) -> x.comma(a, SalespersonRow::surname), "s\\.SURNAME as s_SURNAME"),
            testCase7((x, a) -> x.comma(a, SalespersonRow::surname, "surname"), "s.SURNAME as surname"),
            testCase7((x, a) -> x.comma(SalespersonRow.class), "s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),
            testCase7((x, a) -> x.comma(SalespersonRow.class, "s"), "s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),
            testCase7(InProjectionExpectingComma7::comma, "s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),

            testCase8((x, a) -> x.comma(ValueExpression.of("ABC")), "\\? as value_\\d+", "ABC"),
            testCase8((x, a) -> x.comma(ValueExpression.of("ABC"), "abc"), "\\? as abc", "ABC"),
            testCase8((x, a) -> x.comma(SalespersonRow::middleNames), "s\\.MIDDLE_NAMES as s_MIDDLE_NAMES"),
            testCase8((x, a) -> x.comma(SalespersonRow::middleNames, "middle"), "s\\.MIDDLE_NAMES as middle"),
            testCase8((x, a) -> x.comma(SalespersonRow::surname), "s\\.SURNAME as s_SURNAME"),
            testCase8((x, a) -> x.comma(SalespersonRow::surname, "surname"), "s\\.SURNAME as surname"),
            testCase8((x, a) -> x.comma("s", SalespersonRow::middleNames), "s\\.MIDDLE_NAMES as s_MIDDLE_NAMES"),
            testCase8((x, a) -> x.comma("s", SalespersonRow::middleNames, "middle"), "s.MIDDLE_NAMES as middle"),
            testCase8((x, a) -> x.comma("s", SalespersonRow::surname), "s\\.SURNAME as s_SURNAME"),
            testCase8((x, a) -> x.comma("s", SalespersonRow::surname, "surname"), "s.SURNAME as surname"),
            testCase8((x, a) -> x.comma(a, SalespersonRow::middleNames), "s\\.MIDDLE_NAMES as s_MIDDLE_NAMES"),
            testCase8((x, a) -> x.comma(a, SalespersonRow::middleNames, "middle"), "s.MIDDLE_NAMES as middle"),
            testCase8((x, a) -> x.comma(a, SalespersonRow::surname), "s\\.SURNAME as s_SURNAME"),
            testCase8((x, a) -> x.comma(a, SalespersonRow::surname, "surname"), "s.SURNAME as surname"),
            testCase8((x, a) -> x.comma(SalespersonRow.class), "s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),
            testCase8((x, a) -> x.comma(SalespersonRow.class, "s"), "s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),
            testCase8(InProjectionExpectingComma8::comma, "s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION")
        );
    }

    @ParameterizedTest
    @MethodSource("argsForComma")
    void testComma(BiFunction<InProjectionExpectingComma1<String>,Alias<SalespersonRow>,Select<?>> method, String expectedSql, Object[] expectedArgs) {
        MockitoAnnotations.initMocks(this);
        Database database = testDatabase(new AnsiDialect());
        Alias<SalespersonRow> alias = database.table(SalespersonRow.class).as("s");

        InProjectionExpectingComma1<String> sut = database.from(alias)
            .select(SalespersonRow::firstName, "name");
        method.apply(sut, alias).list(transaction);

        verify(transaction).query(sql.capture(), args.capture(), rowMapper.capture());
        Pattern pattern = Pattern.compile("select s.FIRST_NAME as name, " + expectedSql + " from SIESTA.SALESPERSON s");
        assertThat(pattern.matcher(sql.getValue()).matches(), is(true));
        assertThat(args.getValue(), is(expectedArgs));
    }
}