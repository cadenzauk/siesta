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

package com.cadenzauk.siesta.grammar.expression.olap;

import com.cadenzauk.core.MockitoTest;
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.DataType;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Dialect;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.Transaction;
import com.cadenzauk.siesta.dialect.AnsiDialect;
import com.cadenzauk.siesta.grammar.expression.Precedence;
import com.cadenzauk.siesta.grammar.expression.TypedExpression;
import com.cadenzauk.siesta.model.SalespersonRow;
import com.cadenzauk.siesta.type.DbTypeId;
import com.cadenzauk.siesta.type.DefaultInteger;
import com.google.common.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static com.cadenzauk.siesta.Order.DESC;
import static com.cadenzauk.siesta.grammar.expression.StringFunctions.substr;
import static com.cadenzauk.siesta.grammar.expression.StringFunctions.upper;
import static com.cadenzauk.siesta.model.TestDatabase.testDatabase;
import static org.apache.commons.lang3.ArrayUtils.toArray;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class OlapFunctionTest extends MockitoTest {
    @Mock
    private TypedExpression<?> arg1;

    @Mock
    private Scope scope;

    @Mock
    private ResultSet resultSet;

    @Mock
    private Database database;

    @Mock
    private Dialect dialect;

    @Mock
    private Transaction transaction;

    @Captor
    private ArgumentCaptor<String> sql;

    @Captor
    private ArgumentCaptor<Object[]> args;

    @Test
    void label() {
        when(scope.newLabel()).thenReturn(5L);
        OlapFunction<Integer> sut = new OlapFunction<>("foo", TypeToken.of(Integer.class), arg1);

        String result = sut.label(scope);

        assertThat(result, is("foo_5"));
    }

    @Test
    void rowMapper() throws SQLException {
        when(resultSet.getInt("fred")).thenReturn(1034);
        when(resultSet.wasNull()).thenReturn(false);
        when(scope.database()).thenReturn(database);
        when(database.dialect()).thenReturn(dialect);
        when(dialect.type(DbTypeId.INTEGER)).thenReturn(new DefaultInteger());
        when(database.getDataTypeOf(TypeToken.of(Integer.class))).thenReturn(DataType.INTEGER);
        OlapFunction<Integer> sut = new OlapFunction<>("foo", TypeToken.of(Integer.class), arg1);

        RowMapper<Integer> result = sut.rowMapper(scope, "fred");

        assertThat(result.mapRow(resultSet), is(1034));
        verifyNoMoreInteractions(resultSet);
    }

    @Test
    void type() {
        OlapFunction<Integer> sut = new OlapFunction<>("bar", TypeToken.of(Integer.class), arg1);

        assertThat(sut.type(), is(TypeToken.of(Integer.class)));
    }

    @Test
    void sql() {
        when(arg1.sql(scope)).thenReturn("arg1");
        OlapFunction<Integer> sut = new OlapFunction<>("bar", TypeToken.of(Integer.class), arg1);

        String result = sut.sql(scope);

        assertThat(result, is("bar(arg1) over ()"));
    }

    @Test
    void args() {
        when(arg1.args(scope)).thenReturn(Stream.of("foo", 123L));
        OlapFunction<Integer> sut = new OlapFunction<>("bar", TypeToken.of(Integer.class), arg1);

        Object[] result = sut.args(scope).toArray();

        assertThat(result, is(toArray("foo", 123L)));
    }

    @Test
    void precedence() {
        OlapFunction<Integer> sut = new OlapFunction<>("bar", TypeToken.of(Integer.class), arg1);

        Precedence result = sut.precedence();

        assertThat(result, is(Precedence.UNARY));
    }



    @ParameterizedTest(name = "{index}: {1}")
    @MethodSource("parametersForRowNumber")
    void addPartitionByAndParitionBy(BiFunction<InOlapExpectingPartitionBy<Integer>,Alias<SalespersonRow>,TypedExpression<Integer>> f, String expected, Object[] expectedArgs) {
        Database database = testDatabase(new AnsiDialect());
        Alias<SalespersonRow> s = database.table(SalespersonRow.class).as("s");

        database.from(s)
            .select(f.apply(Olap.rowNumber(), s))
            .list(transaction);

        verify(transaction).query(sql.capture(), args.capture(), any());
        assertThat(sql.getValue(), is("select row_number() over (" + expected + ") as row_number_1 " +
            "from SIESTA.SALESPERSON s"));
        assertThat(args.getValue(), is(expectedArgs));}

    private static Arguments testCase(BiFunction<InOlapExpectingPartitionBy<Integer>,Alias<SalespersonRow>,TypedExpression<Integer>> f, String expectedSql, Object... expectedArgs) {
        return Arguments.of(f, expectedSql, expectedArgs);
    }

    private static Stream<Arguments> parametersForRowNumber() {
        return Stream.of(
            testCase((r, s) -> r.partitionBy(upper(SalespersonRow::middleNames)), "partition by upper(s.MIDDLE_NAMES)"),
            testCase((r, s) -> r.partitionBy(SalespersonRow::salespersonId), "partition by s.SALESPERSON_ID"),
            testCase((r, s) -> r.partitionBy(SalespersonRow::middleNames), "partition by s.MIDDLE_NAMES"),
            testCase((r, s) -> r.partitionBy("s", SalespersonRow::salespersonId), "partition by s.SALESPERSON_ID"),
            testCase((r, s) -> r.partitionBy("s", SalespersonRow::middleNames), "partition by s.MIDDLE_NAMES"),
            testCase((r, s) -> r.partitionBy(s, SalespersonRow::salespersonId), "partition by s.SALESPERSON_ID"),
            testCase((r, s) -> r.partitionBy(s, SalespersonRow::middleNames), "partition by s.MIDDLE_NAMES"),

            testCase((r, s) -> r.partitionBy(SalespersonRow::salespersonId).then(upper(SalespersonRow::middleNames)), "partition by s.SALESPERSON_ID, upper(s.MIDDLE_NAMES)"),
            testCase((r, s) -> r.partitionBy(SalespersonRow::salespersonId).then(SalespersonRow::firstName), "partition by s.SALESPERSON_ID, s.FIRST_NAME"),
            testCase((r, s) -> r.partitionBy(SalespersonRow::salespersonId).then(SalespersonRow::middleNames), "partition by s.SALESPERSON_ID, s.MIDDLE_NAMES"),
            testCase((r, s) -> r.partitionBy(SalespersonRow::salespersonId).then("s", SalespersonRow::firstName), "partition by s.SALESPERSON_ID, s.FIRST_NAME"),
            testCase((r, s) -> r.partitionBy(SalespersonRow::salespersonId).then("s", SalespersonRow::middleNames), "partition by s.SALESPERSON_ID, s.MIDDLE_NAMES"),
            testCase((r, s) -> r.partitionBy(SalespersonRow::salespersonId).then(s, SalespersonRow::firstName), "partition by s.SALESPERSON_ID, s.FIRST_NAME"),
            testCase((r, s) -> r.partitionBy(SalespersonRow::salespersonId).then(s, SalespersonRow::middleNames), "partition by s.SALESPERSON_ID, s.MIDDLE_NAMES"),

            testCase((r, s) -> r.partitionBy(SalespersonRow::salespersonId).orderBy(upper(SalespersonRow::middleNames)), "partition by s.SALESPERSON_ID order by upper(s.MIDDLE_NAMES) asc"),
            testCase((r, s) -> r.partitionBy(SalespersonRow::salespersonId).orderBy(SalespersonRow::firstName), "partition by s.SALESPERSON_ID order by s.FIRST_NAME asc"),
            testCase((r, s) -> r.partitionBy(SalespersonRow::salespersonId).orderBy(SalespersonRow::middleNames), "partition by s.SALESPERSON_ID order by s.MIDDLE_NAMES asc"),
            testCase((r, s) -> r.partitionBy(SalespersonRow::salespersonId).orderBy("s", SalespersonRow::firstName), "partition by s.SALESPERSON_ID order by s.FIRST_NAME asc"),
            testCase((r, s) -> r.partitionBy(SalespersonRow::salespersonId).orderBy("s", SalespersonRow::middleNames), "partition by s.SALESPERSON_ID order by s.MIDDLE_NAMES asc"),
            testCase((r, s) -> r.partitionBy(SalespersonRow::salespersonId).orderBy(s, SalespersonRow::firstName), "partition by s.SALESPERSON_ID order by s.FIRST_NAME asc"),
            testCase((r, s) -> r.partitionBy(SalespersonRow::salespersonId).orderBy(s, SalespersonRow::middleNames), "partition by s.SALESPERSON_ID order by s.MIDDLE_NAMES asc"),

            testCase((r, s) -> r.partitionBy(SalespersonRow::salespersonId).orderBy(upper(SalespersonRow::middleNames), DESC), "partition by s.SALESPERSON_ID order by upper(s.MIDDLE_NAMES) desc"),
            testCase((r, s) -> r.partitionBy(SalespersonRow::salespersonId).orderBy(SalespersonRow::firstName, DESC), "partition by s.SALESPERSON_ID order by s.FIRST_NAME desc"),
            testCase((r, s) -> r.partitionBy(SalespersonRow::salespersonId).orderBy(SalespersonRow::middleNames, DESC), "partition by s.SALESPERSON_ID order by s.MIDDLE_NAMES desc"),
            testCase((r, s) -> r.partitionBy(SalespersonRow::salespersonId).orderBy("s", SalespersonRow::firstName, DESC), "partition by s.SALESPERSON_ID order by s.FIRST_NAME desc"),
            testCase((r, s) -> r.partitionBy(SalespersonRow::salespersonId).orderBy("s", SalespersonRow::middleNames, DESC), "partition by s.SALESPERSON_ID order by s.MIDDLE_NAMES desc"),
            testCase((r, s) -> r.partitionBy(SalespersonRow::salespersonId).orderBy(s, SalespersonRow::firstName, DESC), "partition by s.SALESPERSON_ID order by s.FIRST_NAME desc"),
            testCase((r, s) -> r.partitionBy(SalespersonRow::salespersonId).orderBy(s, SalespersonRow::middleNames, DESC), "partition by s.SALESPERSON_ID order by s.MIDDLE_NAMES desc"),

            testCase((r, s) -> r.orderBy(SalespersonRow::salespersonId).then(upper(SalespersonRow::middleNames)), "order by s.SALESPERSON_ID asc, upper(s.MIDDLE_NAMES) asc"),
            testCase((r, s) -> r.orderBy(SalespersonRow::salespersonId).then(SalespersonRow::firstName), "order by s.SALESPERSON_ID asc, s.FIRST_NAME asc"),
            testCase((r, s) -> r.orderBy(SalespersonRow::salespersonId).then(SalespersonRow::middleNames), "order by s.SALESPERSON_ID asc, s.MIDDLE_NAMES asc"),
            testCase((r, s) -> r.orderBy(SalespersonRow::salespersonId).then("s", SalespersonRow::firstName), "order by s.SALESPERSON_ID asc, s.FIRST_NAME asc"),
            testCase((r, s) -> r.orderBy(SalespersonRow::salespersonId).then("s", SalespersonRow::middleNames), "order by s.SALESPERSON_ID asc, s.MIDDLE_NAMES asc"),
            testCase((r, s) -> r.orderBy(SalespersonRow::salespersonId).then(s, SalespersonRow::firstName), "order by s.SALESPERSON_ID asc, s.FIRST_NAME asc"),
            testCase((r, s) -> r.orderBy(SalespersonRow::salespersonId).then(s, SalespersonRow::middleNames), "order by s.SALESPERSON_ID asc, s.MIDDLE_NAMES asc"),

            testCase((r, s) -> r.orderBy(SalespersonRow::salespersonId).then(upper(SalespersonRow::middleNames), DESC), "order by s.SALESPERSON_ID asc, upper(s.MIDDLE_NAMES) desc"),
            testCase((r, s) -> r.orderBy(SalespersonRow::salespersonId).then(SalespersonRow::firstName, DESC), "order by s.SALESPERSON_ID asc, s.FIRST_NAME desc"),
            testCase((r, s) -> r.orderBy(SalespersonRow::salespersonId).then(SalespersonRow::middleNames, DESC), "order by s.SALESPERSON_ID asc, s.MIDDLE_NAMES desc"),
            testCase((r, s) -> r.orderBy(SalespersonRow::salespersonId).then("s", SalespersonRow::firstName, DESC), "order by s.SALESPERSON_ID asc, s.FIRST_NAME desc"),
            testCase((r, s) -> r.orderBy(SalespersonRow::salespersonId).then("s", SalespersonRow::middleNames, DESC), "order by s.SALESPERSON_ID asc, s.MIDDLE_NAMES desc"),
            testCase((r, s) -> r.orderBy(SalespersonRow::salespersonId).then(s, SalespersonRow::firstName, DESC), "order by s.SALESPERSON_ID asc, s.FIRST_NAME desc"),
            testCase((r, s) -> r.orderBy(SalespersonRow::salespersonId).then(s, SalespersonRow::middleNames, DESC), "order by s.SALESPERSON_ID asc, s.MIDDLE_NAMES desc"),

            testCase((r, a) -> r.partitionBy(substr(SalespersonRow::middleNames, 1)), "partition by substr(s.MIDDLE_NAMES, ?)", 1)
        );
    }
}