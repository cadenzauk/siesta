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

package com.cadenzauk.siesta;

import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.dialect.AnsiDialect;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.model.TestDatabase;
import com.cadenzauk.siesta.model.WidgetRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Function;

import static com.cadenzauk.siesta.grammar.expression.Aggregates.count;
import static com.cadenzauk.siesta.grammar.expression.Aggregates.countBig;
import static com.cadenzauk.siesta.grammar.expression.Aggregates.countBigDistinct;
import static com.cadenzauk.siesta.grammar.expression.Aggregates.countDistinct;
import static com.cadenzauk.siesta.grammar.expression.TypedExpression.value;
import static org.apache.commons.lang3.ArrayUtils.add;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CountTest {
    @Mock
    private Transaction transaction;

    @Captor
    private ArgumentCaptor<String> sql;

    @Captor
    private ArgumentCaptor<Object[]> args;

    @Captor
    private ArgumentCaptor<RowMapper<?>> rowMapper;

    @Test
    void countInSelectGeneratesCorrectSqlAndArgs() {
        Database database = TestDatabase.testDatabase(new AnsiDialect());

        database.from(WidgetRow.class, "w")
            .select(count(), "n")
            .where(WidgetRow::manufacturerId).isEqualTo(4002L)
            .list(transaction);

        verify(transaction).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select count(*) as n " +
            "from SIESTA.WIDGET w " +
            "where w.MANUFACTURER_ID = ?"));
        assertThat(args.getValue(), arrayWithSize(1));
        assertThat(args.getValue(), arrayContaining(args(4002L)));
    }


    @ParameterizedTest(name = "{1}")
    @MethodSource("countColumnWithExpectedSqlAndArgs")
    void countColumnInSelectGeneratesCorrectSqlAndArgs(Function<Alias<WidgetRow>,TypedExpression<Integer>> countFunc, String expectedSql, Object... expectedArgs) {
        Database database = TestDatabase.testDatabase(new AnsiDialect());
        Alias<WidgetRow> alias = database.table(WidgetRow.class).as("w");

        database.from(alias)
            .select(countFunc.apply(alias), "n")
            .where(WidgetRow::manufacturerId).isEqualTo(4002L)
            .list(transaction);

        verify(transaction).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select " + expectedSql + " as n " +
            "from SIESTA.WIDGET w " +
            "where w.MANUFACTURER_ID = ?"));
        assertThat(args.getValue(), arrayContaining(add(expectedArgs, 4002L)));
    }

    @Test
    void countBigInSelectGeneratesCorrectSqlAndArgs() {
        Database database = TestDatabase.testDatabase(new AnsiDialect());

        database.from(WidgetRow.class, "w")
            .select(countBig(), "n")
            .where(WidgetRow::manufacturerId).isEqualTo(4002L)
            .list(transaction);

        verify(transaction).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select count_big(*) as n " +
            "from SIESTA.WIDGET w " +
            "where w.MANUFACTURER_ID = ?"));
        assertThat(args.getValue(), arrayContaining(args(4002L)));
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("countBigColumnWithExpectedSqlAndArgs")
    void countBigInSelectGeneratesCorrectSqlAndArgs(Function<Alias<WidgetRow>,TypedExpression<Long>> countFunc, String expectedSql, Object... expectedArgs) {
        Database database = TestDatabase.testDatabase(new AnsiDialect());
        Alias<WidgetRow> alias = database.table(WidgetRow.class).as("w");

        database.from(alias)
            .select(countFunc.apply(alias), "n")
            .where(WidgetRow::manufacturerId).isEqualTo(4002L)
            .list(transaction);

        verify(transaction).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select " + expectedSql + " as n " +
            "from SIESTA.WIDGET w " +
            "where w.MANUFACTURER_ID = ?"));
        assertThat(args.getValue(), arrayContaining(add(expectedArgs, 4002L)));
    }

    private static <T> Function<Alias<WidgetRow>,TypedExpression<T>> testCase(Function<Alias<WidgetRow>,TypedExpression<T>> function) {
        return function;
    }

    private static Object[] args(Object... args) {
        return args;
    }

    private static Object[][] countColumnWithExpectedSqlAndArgs() {
        return new Object[][] {
            { testCase(a -> count(value(1234))), "count(?)", args(1234) },
            { testCase(a -> count(WidgetRow::name)), "count(w.NAME)", args() },
            { testCase(a -> count(WidgetRow::description)), "count(w.DESCRIPTION)", args() },
            { testCase(a -> count("w", WidgetRow::name)), "count(w.NAME)", args() },
            { testCase(a -> count("w", WidgetRow::description)), "count(w.DESCRIPTION)", args() },
            { testCase(a -> count(a, WidgetRow::name)), "count(w.NAME)", args() },
            { testCase(a -> count(a, WidgetRow::description)), "count(w.DESCRIPTION)", args() },
            { testCase(a -> countDistinct(value(2.0))), "count(distinct ?)", args(2.0) },
            { testCase(a -> countDistinct(WidgetRow::name)), "count(distinct w.NAME)", args() },
            { testCase(a -> countDistinct(WidgetRow::description)), "count(distinct w.DESCRIPTION)", args() },
            { testCase(a -> countDistinct("w", WidgetRow::name)), "count(distinct w.NAME)", args() },
            { testCase(a -> countDistinct("w", WidgetRow::description)), "count(distinct w.DESCRIPTION)", args() },
            { testCase(a -> countDistinct(a, WidgetRow::name)), "count(distinct w.NAME)", args() },
            { testCase(a -> countDistinct(a, WidgetRow::description)), "count(distinct w.DESCRIPTION)", args() },
        };
    }

    private static Object[][] countBigColumnWithExpectedSqlAndArgs() {
        return new Object[][] {
            { testCase(a -> countBig(value("ABC"))), "count_big(?)", args("ABC") },
            { testCase(a -> countBig(WidgetRow::name)), "count_big(w.NAME)", args() },
            { testCase(a -> countBig(WidgetRow::description)), "count_big(w.DESCRIPTION)", args() },
            { testCase(a -> countBig("w", WidgetRow::name)), "count_big(w.NAME)", args() },
            { testCase(a -> countBig("w", WidgetRow::description)), "count_big(w.DESCRIPTION)", args() },
            { testCase(a -> countBig(a, WidgetRow::name)), "count_big(w.NAME)", args() },
            { testCase(a -> countBig(a, WidgetRow::description)), "count_big(w.DESCRIPTION)", args() },
            { testCase(a -> countBigDistinct(value(2.0))), "count_big(distinct ?)", args(2.0) },
            { testCase(a -> countBigDistinct(WidgetRow::name)), "count_big(distinct w.NAME)", args() },
            { testCase(a -> countBigDistinct(WidgetRow::description)), "count_big(distinct w.DESCRIPTION)", args() },
            { testCase(a -> countBigDistinct("w", WidgetRow::name)), "count_big(distinct w.NAME)", args() },
            { testCase(a -> countBigDistinct("w", WidgetRow::description)), "count_big(distinct w.DESCRIPTION)", args() },
            { testCase(a -> countBigDistinct(a, WidgetRow::name)), "count_big(distinct w.NAME)", args() },
            { testCase(a -> countBigDistinct(a, WidgetRow::description)), "count_big(distinct w.DESCRIPTION)", args() },
        };
    }
}
