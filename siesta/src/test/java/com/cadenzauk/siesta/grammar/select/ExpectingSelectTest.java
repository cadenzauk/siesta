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
import com.cadenzauk.siesta.RegularTableAlias;
import com.cadenzauk.siesta.Transaction;
import com.cadenzauk.siesta.dialect.AnsiDialect;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.model.SalespersonRow;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.BiFunction;
import java.util.stream.Stream;

import static com.cadenzauk.siesta.grammar.expression.StringFunctions.upper;
import static com.cadenzauk.siesta.model.TestDatabase.testDatabase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExpectingSelectTest {
    @Mock
    private Transaction transaction;
    @Captor
    private ArgumentCaptor<String> sql;
    @Captor
    private ArgumentCaptor<Object[]> args;
    @Captor
    private ArgumentCaptor<RowMapper<?>> rowMapper;

    private static Arguments selectTestCase(BiFunction<ExpectingSelect<SalespersonRow>,RegularTableAlias<SalespersonRow>,Select<?>> method, String expectedSql) {
        return arguments(method, expectedSql);
    }

    private static Stream<Arguments> argsForSelect() {
        return Stream.of(
            selectTestCase((s, a) -> s.select(upper(SalespersonRow::surname)), "upper(s.SURNAME) as upper_s_SURNAME"),

            selectTestCase((s, a) -> s.select(SalespersonRow::surname), "s.SURNAME as s_SURNAME"),
            selectTestCase((s, a) -> s.select(SalespersonRow::middleNames), "s.MIDDLE_NAMES as s_MIDDLE_NAMES"),
            selectTestCase((s, a) -> s.select("s", SalespersonRow::surname), "s.SURNAME as s_SURNAME"),
            selectTestCase((s, a) -> s.select("s", SalespersonRow::middleNames), "s.MIDDLE_NAMES as s_MIDDLE_NAMES"),
            selectTestCase((s, a) -> s.select(a, SalespersonRow::surname), "s.SURNAME as s_SURNAME"),
            selectTestCase((s, a) -> s.select(a, SalespersonRow::middleNames), "s.MIDDLE_NAMES as s_MIDDLE_NAMES"),

            selectTestCase((s, a) -> s.select(SalespersonRow::surname, "sname"), "s.SURNAME as sname"),
            selectTestCase((s, a) -> s.select(SalespersonRow::middleNames, "mnames"), "s.MIDDLE_NAMES as mnames"),
            selectTestCase((s, a) -> s.select("s", SalespersonRow::surname, "sname"), "s.SURNAME as sname"),
            selectTestCase((s, a) -> s.select("s", SalespersonRow::middleNames, "mnames"), "s.MIDDLE_NAMES as mnames"),
            selectTestCase((s, a) -> s.select(a, SalespersonRow::surname, "sname"), "s.SURNAME as sname"),
            selectTestCase((s, a) -> s.select(a, SalespersonRow::middleNames, "mnames"), "s.MIDDLE_NAMES as mnames"),

            selectTestCase((s, a) -> s.select(SalespersonRow.class), "s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),
            selectTestCase((s, a) -> s.select(SalespersonRow.class, "s"), "s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),
            selectTestCase(ExpectingSelect::select, "s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),

            selectTestCase((s, a) -> s.selectDistinct(), "distinct s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),

            selectTestCase((s, a) -> s.selectDistinct(upper(SalespersonRow::surname)), "distinct upper(s.SURNAME) as upper_s_SURNAME"),
            selectTestCase((s, a) -> s.selectDistinct(upper(SalespersonRow::surname), "sname"), "distinct upper(s.SURNAME) as sname"),

            selectTestCase((s, a) -> s.selectDistinct(SalespersonRow::surname), "distinct s.SURNAME as s_SURNAME"),
            selectTestCase((s, a) -> s.selectDistinct(SalespersonRow::middleNames), "distinct s.MIDDLE_NAMES as s_MIDDLE_NAMES"),
            selectTestCase((s, a) -> s.selectDistinct("s", SalespersonRow::surname), "distinct s.SURNAME as s_SURNAME"),
            selectTestCase((s, a) -> s.selectDistinct("s", SalespersonRow::middleNames), "distinct s.MIDDLE_NAMES as s_MIDDLE_NAMES"),
            selectTestCase((s, a) -> s.selectDistinct(a, SalespersonRow::surname), "distinct s.SURNAME as s_SURNAME"),
            selectTestCase((s, a) -> s.selectDistinct(a, SalespersonRow::middleNames), "distinct s.MIDDLE_NAMES as s_MIDDLE_NAMES"),

            selectTestCase((s, a) -> s.selectDistinct(SalespersonRow::surname, "sname"), "distinct s.SURNAME as sname"),
            selectTestCase((s, a) -> s.selectDistinct(SalespersonRow::middleNames, "mnames"), "distinct s.MIDDLE_NAMES as mnames"),
            selectTestCase((s, a) -> s.selectDistinct("s", SalespersonRow::surname, "sname"), "distinct s.SURNAME as sname"),
            selectTestCase((s, a) -> s.selectDistinct("s", SalespersonRow::middleNames, "mnames"), "distinct s.MIDDLE_NAMES as mnames"),
            selectTestCase((s, a) -> s.selectDistinct(a, SalespersonRow::surname, "sname"), "distinct s.SURNAME as sname"),
            selectTestCase((s, a) -> s.selectDistinct(a, SalespersonRow::middleNames, "mnames"), "distinct s.MIDDLE_NAMES as mnames"),

            selectTestCase((s, a) -> s.selectDistinct(SalespersonRow.class), "distinct s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),
            selectTestCase((s, a) -> s.selectDistinct(SalespersonRow.class, "s"), "distinct s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),
            selectTestCase(ExpectingSelect::selectDistinct, "distinct s.SALESPERSON_ID as s_SALESPERSON_ID, s.FIRST_NAME as s_FIRST_NAME, s.MIDDLE_NAMES as s_MIDDLE_NAMES, s.SURNAME as s_SURNAME, s.NUMBER_OF_SALES as s_NUMBER_OF_SALES, s.COMMISSION as s_COMMISSION"),

            selectTestCase((s, a) -> s.selectInto(SalespersonRow.class).with(TypedExpression.literal("Smith")).as(SalespersonRow::surname), "'Smith' as SALESPERSON_SURNAME"),
            selectTestCase((s, a) -> s.selectInto(SalespersonRow.class, "v").with(TypedExpression.literal("Smith")).as(SalespersonRow::surname), "'Smith' as v_SURNAME"),
            selectTestCase((s, a) -> s.selectInto(a.table().as("v")).with(TypedExpression.literal("Smith")).as(SalespersonRow::surname), "'Smith' as v_SURNAME")
        );
    }

    @ParameterizedTest
    @MethodSource("argsForSelect")
    void testSelect(BiFunction<ExpectingSelect<SalespersonRow>,Alias<SalespersonRow>,Select<?>> method, String expectedSql) {
        Database database = testDatabase(new AnsiDialect());
        Alias<SalespersonRow> alias = database.table(SalespersonRow.class).as("s");
        method.apply(database.from(alias), alias)
            .list(transaction);

        verify(transaction).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select " + expectedSql + " from SIESTA.SALESPERSON s"));
        assertThat(args.getValue(), arrayWithSize(0));
    }
}
