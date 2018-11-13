/*
 * Copyright (c) 2018 Cadenza United Kingdom Limited
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

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.Function2;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.core.tuple.Tuple;
import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.core.tuple.Tuple3;
import com.cadenzauk.core.tuple.Tuple4;
import com.cadenzauk.core.tuple.Tuple5;
import com.cadenzauk.core.tuple.Tuple6;
import com.cadenzauk.core.tuple.Tuple7;
import com.cadenzauk.core.tuple.Tuple8;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.model.SalespersonRow;
import com.cadenzauk.siesta.model.TestDatabase;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static co.unruly.matchers.StreamMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TupleBuilderTest {
    private static Database database = TestDatabase.testDatabaseBuilder().build();

    @Mock
    private Scope scope;

    @Mock
    private ResultSet rs;

    @Test
    void label() {
        long labelNum = RandomUtils.nextLong(1, 400);
        when(scope.newLabel()).thenReturn(labelNum);
        TupleBuilder1<Long> sut = TupleBuilder.tuple(SalespersonRow::salespersonId);

        String result = sut.label(scope);

        assertThat(result, is("tuple_" + labelNum));
    }

    @ParameterizedTest(name = "{index}: {1}")
    @MethodSource("argsForSql")
    void sql(TupleBuilder sut, String expectedSql) {
        String result = sut.sql(new Scope(database, alias()));

        assertThat(result, is(expectedSql));
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("argsForArgs")
    void args(TupleBuilder sut, Stream<Object> expectedResult) {
        Stream<Object> result = sut.args(scope);

        assertThat(result, equalTo(expectedResult));
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("argsForPrecedence")
    void precedence(TupleBuilder sut, Precedence expectedResult) {
        Precedence result = sut.precedence();

        assertThat(result, is(expectedResult));
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("argsForType")
    <T extends TupleBuilder & TypedExpression<?>> void type(T sut, TypeToken<?> expectedType) {
        TypeToken<?> result = sut.type();

        assertThat(result, is(expectedType));
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("argsForRowMapper")
    @MockitoSettings(strictness = Strictness.LENIENT)
    <T extends TupleBuilder & TypedExpression<?>> void rowMapper(T sut, Object expectedRow) throws SQLException {
        when(rs.getString("a_FIRST_NAME")).thenReturn("Fred");
        when(rs.getBigDecimal("a_COMMISSION")).thenReturn(new BigDecimal("1234"));
        when(rs.getLong("a_SALESPERSON_ID")).thenReturn(5678L);

        RowMapper<?> result = sut.rowMapper(new Scope(database, alias()), Optional.empty());
        Object row = result.mapRow(rs);

        assertThat(row, is(expectedRow));
    }

    private static TupleBuilder1<String> aTuple1() {
        return TupleBuilder.tuple(SalespersonRow::firstName);
    }

    private static TupleBuilder2<String,BigDecimal> aTuple2() {
        return aTuple1().comma(SalespersonRow::commission);
    }

    private static TupleBuilder3<String,BigDecimal,Long> aTuple3() {
        return aTuple2().comma(SalespersonRow::salespersonId);
    }

    private static TupleBuilder4<String,BigDecimal,Long,Long> aTuple4() {
        return aTuple3().comma(SalespersonRow::salespersonId);
    }

    private static TupleBuilder5<String,BigDecimal,Long,Long,Long> aTuple5() {
        return aTuple4().comma(SalespersonRow::salespersonId);
    }

    private static TupleBuilder6<String,BigDecimal,Long,Long,Long,Long> aTuple6() {
        return aTuple5().comma(SalespersonRow::salespersonId);
    }

    private static TupleBuilder7<String,BigDecimal,Long,Long,Long,Long,Long> aTuple7() {
        return aTuple6().comma(SalespersonRow::salespersonId);
    }

    private static TupleBuilder8<String,BigDecimal,Long,Long,Long,Long,Long,Long> aTuple8() {
        return aTuple7().comma(SalespersonRow::salespersonId);
    }

    private static <T extends TupleBuilder> Stream<T> build(
        Function<Function1<SalespersonRow,Long>,T> f1,
        Function2<String,Function1<SalespersonRow,Long>,T> f2,
        Function2<Alias<SalespersonRow>,Function1<SalespersonRow,Long>,T> f3) {
        return Stream.of(
            f1.apply(SalespersonRow::salespersonId),
            f2.apply("a", SalespersonRow::salespersonId),
            f3.apply(alias(), SalespersonRow::salespersonId)
        );
    }

    private static <T extends TupleBuilder> Stream<T> buildOpt(
        Function<FunctionOptional1<SalespersonRow,String>,T> f1,
        Function2<String,FunctionOptional1<SalespersonRow,String>,T> f2,
        Function2<Alias<SalespersonRow>,FunctionOptional1<SalespersonRow,String>,T> f3) {
        return Stream.of(
            f1.apply(SalespersonRow::middleNames),
            f2.apply("a", SalespersonRow::middleNames),
            f3.apply(alias(), SalespersonRow::middleNames)
        );
    }

    private static Stream<TupleBuilder1<?>> tuples1() {
        return build(TupleBuilder::tuple, TupleBuilder::tuple, TupleBuilder::tuple);
    }

    private static Stream<TupleBuilder1<?>> tuples1Opt() {
        return buildOpt(TupleBuilder::tuple, TupleBuilder::tuple, TupleBuilder::tuple);
    }

    private static Stream<TupleBuilder2<?,?>> tuples2(TupleBuilder1<?> tuple) {
        return build(tuple::comma, tuple::comma, tuple::comma);
    }

    private static Stream<TupleBuilder2<?,?>> tuples2Opt(TupleBuilder1<?> tuple) {
        return buildOpt(tuple::comma, tuple::comma, tuple::comma);
    }

    private static Stream<TupleBuilder3<?,?,?>> tuples3(TupleBuilder2<?,?> tuple) {
        return build(tuple::comma, tuple::comma, tuple::comma);
    }

    private static Stream<TupleBuilder3<?,?,?>> tuples3Opt(TupleBuilder2<?,?> tuple) {
        return buildOpt(tuple::comma, tuple::comma, tuple::comma);
    }

    private static Stream<TupleBuilder4<?,?,?,?>> tuples4(TupleBuilder3<?,?,?> tuple) {
        return build(tuple::comma, tuple::comma, tuple::comma);
    }

    private static Stream<TupleBuilder4<?,?,?,?>> tuples4Opt(TupleBuilder3<?,?,?> tuple) {
        return buildOpt(tuple::comma, tuple::comma, tuple::comma);
    }

    private static Stream<Arguments> argsForSql() {
        return Stream.of(
            tuples1().map(t -> Arguments.of(t, "(a.SALESPERSON_ID)")),
            tuples1Opt().map(t -> Arguments.of(t, "(a.MIDDLE_NAMES)")),
            tuples2(aTuple1()).map(t -> Arguments.of(t, "(a.FIRST_NAME, a.SALESPERSON_ID)")),
            tuples2Opt(aTuple1()).map(t -> Arguments.of(t, "(a.FIRST_NAME, a.MIDDLE_NAMES)")),
            tuples3(aTuple2()).map(t -> Arguments.of(t, "(a.FIRST_NAME, a.COMMISSION, a.SALESPERSON_ID)")),
            tuples3Opt(aTuple2()).map(t -> Arguments.of(t, "(a.FIRST_NAME, a.COMMISSION, a.MIDDLE_NAMES)")),
            tuples4(aTuple3()).map(t -> Arguments.of(t, "(a.FIRST_NAME, a.COMMISSION, a.SALESPERSON_ID, a.SALESPERSON_ID)")),
            tuples4Opt(aTuple3()).map(t -> Arguments.of(t, "(a.FIRST_NAME, a.COMMISSION, a.SALESPERSON_ID, a.MIDDLE_NAMES)"))
        ).flatMap(Function.identity());
    }

    private static Stream<Arguments> argsForArgs() {
        return Stream.of(
            tuples1().map(t -> Arguments.of(t, Stream.empty())),
            tuples1Opt().map(t -> Arguments.of(t, Stream.empty())),
            tuples2(aTuple1()).map(t -> Arguments.of(t, Stream.empty())),
            tuples2Opt(aTuple1()).map(t -> Arguments.of(t, Stream.empty())),
            tuples3(aTuple2()).map(t -> Arguments.of(t, Stream.empty())),
            tuples3Opt(aTuple2()).map(t -> Arguments.of(t, Stream.empty())),
            tuples4(aTuple3()).map(t -> Arguments.of(t, Stream.empty())),
            tuples4Opt(aTuple3()).map(t -> Arguments.of(t, Stream.empty()))
        ).flatMap(Function.identity());
    }

    private static Stream<Arguments> argsForPrecedence() {
        return Stream.of(
            Arguments.of(aTuple1(), Precedence.PARENTHESES),
            Arguments.of(aTuple2(), Precedence.PARENTHESES),
            Arguments.of(aTuple3(), Precedence.PARENTHESES),
            Arguments.of(aTuple4(), Precedence.PARENTHESES),
            Arguments.of(aTuple5(), Precedence.PARENTHESES),
            Arguments.of(aTuple6(), Precedence.PARENTHESES),
            Arguments.of(aTuple7(), Precedence.PARENTHESES),
            Arguments.of(aTuple8(), Precedence.PARENTHESES)
        );
    }

    private static Stream<Arguments> argsForType() {
        return Stream.of(
            Arguments.of(aTuple1(), TypeToken.of(String.class)),
            Arguments.of(aTuple2(), new TypeToken<Tuple2<String,BigDecimal>>() {}),
            Arguments.of(aTuple3(), new TypeToken<Tuple3<String,BigDecimal,Long>>() {}),
            Arguments.of(aTuple4(), new TypeToken<Tuple4<String,BigDecimal,Long,Long>>() {}),
            Arguments.of(aTuple5(), new TypeToken<Tuple5<String,BigDecimal,Long,Long,Long>>() {}),
            Arguments.of(aTuple6(), new TypeToken<Tuple6<String,BigDecimal,Long,Long,Long,Long>>() {}),
            Arguments.of(aTuple7(), new TypeToken<Tuple7<String,BigDecimal,Long,Long,Long,Long,Long>>() {}),
            Arguments.of(aTuple8(), new TypeToken<Tuple8<String,BigDecimal,Long,Long,Long,Long,Long,Long>>() {})
        );
    }

    private static Stream<Arguments> argsForRowMapper() {
        return Stream.of(
            Arguments.of(aTuple1(), "Fred"),
            Arguments.of(aTuple2(), Tuple.of("Fred", new BigDecimal("1234"))),
            Arguments.of(aTuple3(), Tuple.of("Fred", new BigDecimal("1234"), 5678L))
        );
    }

    private static Alias<SalespersonRow> alias() {
        return database.table(SalespersonRow.class).as("a");
    }
}