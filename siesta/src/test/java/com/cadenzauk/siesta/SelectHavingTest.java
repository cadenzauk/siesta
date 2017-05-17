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

import com.cadenzauk.core.MockitoTest;
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.core.tuple.Tuple3;
import com.cadenzauk.siesta.dialect.AnsiDialect;
import com.cadenzauk.siesta.grammar.select.ExpectingEndOfStatement;
import com.cadenzauk.siesta.grammar.select.ExpectingHaving;
import com.cadenzauk.siesta.model.TestDatabase;
import com.cadenzauk.siesta.model.WidgetRow;
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

import static com.cadenzauk.siesta.grammar.expression.Aggregates.count;
import static com.cadenzauk.siesta.grammar.expression.Aggregates.countDistinct;
import static com.cadenzauk.siesta.grammar.expression.Aggregates.max;
import static com.cadenzauk.siesta.grammar.expression.TypedExpression.column;
import static org.apache.commons.lang3.ArrayUtils.toArray;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;

class SelectHavingTest extends MockitoTest {
    @Mock
    private SqlExecutor sqlExecutor;

    @Captor
    private ArgumentCaptor<String> sql;

    @Captor
    private ArgumentCaptor<Object[]> args;

    @Captor
    private ArgumentCaptor<RowMapper<?>> rowMapper;

    private static Arguments havingTest(BiFunction<Alias<WidgetRow>,ExpectingHaving<Tuple3<Long,String,String>>,ExpectingEndOfStatement<Tuple3<Long,String,String>>> having, String expectedSql, Object[] expectedArgs) {
        return ObjectArrayArguments.create(having, expectedSql, expectedArgs);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> parametersForHaving() {
        return Stream.of(
            havingTest((w, sel) -> sel.having(column(count()).isGreaterThan(countDistinct(WidgetRow::name))),
                "having count(*) > count(distinct w.NAME)",
                toArray()),
            havingTest((w, sel) -> sel.having(count()).isGreaterThan(countDistinct(WidgetRow::name)),
                "having count(*) > count(distinct w.NAME)",
                toArray()),
            havingTest((w, sel) -> sel.having(WidgetRow::manufacturerId).isEqualTo(1L),
                "having w.MANUFACTURER_ID = ?",
                toArray(1L)),
            havingTest((w, sel) -> sel.having("w", WidgetRow::manufacturerId).isEqualTo(2L),
                "having w.MANUFACTURER_ID = ?",
                toArray(2L)),
            havingTest((w, sel) -> sel.having(w, WidgetRow::manufacturerId).isEqualTo(3L),
                "having w.MANUFACTURER_ID = ?",
                toArray(3L)),
            havingTest((w, sel) -> sel.having(WidgetRow::description).isEqualTo("A"),
                "having w.DESCRIPTION = ?",
                toArray("A")),
            havingTest((w, sel) -> sel.having("w", WidgetRow::description).isEqualTo("B"),
                "having w.DESCRIPTION = ?",
                toArray("B")),
            havingTest((w, sel) -> sel.having(w, WidgetRow::description).isEqualTo("C"),
                "having w.DESCRIPTION = ?",
                toArray("C")),

            havingTest((w, sel) -> sel.having(WidgetRow::manufacturerId).isEqualTo(1L).and(column(WidgetRow::description).isEqualTo("(A)")),
                "having w.MANUFACTURER_ID = ? and (w.DESCRIPTION = ?)",
                toArray(1L, "(A)")),
            havingTest((w, sel) -> sel.having(WidgetRow::manufacturerId).isEqualTo(1L).and(max(WidgetRow::name)).isEqualTo("(A)"),
                "having w.MANUFACTURER_ID = ? and max(w.NAME) = ?",
                toArray(1L, "(A)")),
            havingTest((w, sel) -> sel.having(WidgetRow::manufacturerId).isEqualTo(1L).and(WidgetRow::description).isEqualTo("A"),
                "having w.MANUFACTURER_ID = ? and w.DESCRIPTION = ?",
                toArray(1L, "A")),
            havingTest((w, sel) -> sel.having("w", WidgetRow::manufacturerId).isEqualTo(2L).and("w", WidgetRow::description).isEqualTo("B"),
                "having w.MANUFACTURER_ID = ? and w.DESCRIPTION = ?",
                toArray(2L, "B")),
            havingTest((w, sel) -> sel.having(w, WidgetRow::manufacturerId).isEqualTo(3L).and(w, WidgetRow::description).isEqualTo("C"),
                "having w.MANUFACTURER_ID = ? and w.DESCRIPTION = ?",
                toArray(3L, "C")),
            havingTest((w, sel) -> sel.having(WidgetRow::description).isEqualTo("A").and(WidgetRow::manufacturerId).isEqualTo(1L),
                "having w.DESCRIPTION = ? and w.MANUFACTURER_ID = ?",
                toArray("A", 1L)),
            havingTest((w, sel) -> sel.having("w", WidgetRow::description).isEqualTo("B").and("w", WidgetRow::manufacturerId).isEqualTo(2L),
                "having w.DESCRIPTION = ? and w.MANUFACTURER_ID = ?",
                toArray("B", 2L)),
            havingTest((w, sel) -> sel.having(w, WidgetRow::description).isEqualTo("C").and(w, WidgetRow::manufacturerId).isEqualTo(3L),
                "having w.DESCRIPTION = ? and w.MANUFACTURER_ID = ?",
                toArray("C", 3L)),

            havingTest((w, sel) -> sel.having(WidgetRow::manufacturerId).isEqualTo(1L).or(column(WidgetRow::description).isEqualTo("(A)")),
                "having w.MANUFACTURER_ID = ? or (w.DESCRIPTION = ?)",
                toArray(1L, "(A)")),
            havingTest((w, sel) -> sel.having(WidgetRow::manufacturerId).isEqualTo(1L).or(max(WidgetRow::name)).isEqualTo("(A)"),
                "having w.MANUFACTURER_ID = ? or max(w.NAME) = ?",
                toArray(1L, "(A)")),
            havingTest((w, sel) -> sel.having(WidgetRow::manufacturerId).isEqualTo(1L).or(WidgetRow::description).isEqualTo("A"),
                "having w.MANUFACTURER_ID = ? or w.DESCRIPTION = ?",
                toArray(1L, "A")),
            havingTest((w, sel) -> sel.having("w", WidgetRow::manufacturerId).isEqualTo(2L).or("w", WidgetRow::description).isEqualTo("B"),
                "having w.MANUFACTURER_ID = ? or w.DESCRIPTION = ?",
                toArray(2L, "B")),
            havingTest((w, sel) -> sel.having(w, WidgetRow::manufacturerId).isEqualTo(3L).or(w, WidgetRow::description).isEqualTo("C"),
                "having w.MANUFACTURER_ID = ? or w.DESCRIPTION = ?",
                toArray(3L, "C")),
            havingTest((w, sel) -> sel.having(WidgetRow::description).isEqualTo("A").or(WidgetRow::manufacturerId).isEqualTo(1L),
                "having w.DESCRIPTION = ? or w.MANUFACTURER_ID = ?",
                toArray("A", 1L)),
            havingTest((w, sel) -> sel.having("w", WidgetRow::description).isEqualTo("B").or("w", WidgetRow::manufacturerId).isEqualTo(2L),
                "having w.DESCRIPTION = ? or w.MANUFACTURER_ID = ?",
                toArray("B", 2L)),
            havingTest((w, sel) -> sel.having(w, WidgetRow::description).isEqualTo("C").or(w, WidgetRow::manufacturerId).isEqualTo(3L),
                "having w.DESCRIPTION = ? or w.MANUFACTURER_ID = ?",
                toArray("C", 3L))
        );
    }

    @ParameterizedTest
    @MethodSource(names = "parametersForHaving")
    void having(BiFunction<Alias<WidgetRow>,ExpectingHaving<Tuple3<Long,String,String>>,ExpectingEndOfStatement<Tuple3<Long,String,String>>> having, String expectedSql, Object[] expectedArgs) {
        MockitoAnnotations.initMocks(this);
        Database database = TestDatabase.testDatabase(new AnsiDialect());
        Alias<WidgetRow> w = database.table(WidgetRow.class).as("w");
        having.apply(w, database
            .from(w)
            .select(WidgetRow::manufacturerId).comma(WidgetRow::description).comma(max(WidgetRow::name))
            .groupBy(WidgetRow::manufacturerId).comma(WidgetRow::description))
            .list(sqlExecutor);

        verify(sqlExecutor).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select w.MANUFACTURER_ID as w_MANUFACTURER_ID, w.DESCRIPTION as w_DESCRIPTION, max(w.NAME) as max_w_NAME " +
            "from SIESTA.WIDGET w " +
            "group by w.MANUFACTURER_ID, w.DESCRIPTION " +
            expectedSql));
        assertThat(args.getValue(), is(expectedArgs));
    }
}
