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

import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.Transaction;
import com.cadenzauk.siesta.dialect.AnsiDialect;
import com.cadenzauk.siesta.grammar.select.InWhereExpectingAnd;
import com.cadenzauk.siesta.model.SalespersonRow;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static com.cadenzauk.siesta.grammar.expression.TypedExpression.literal;
import static com.cadenzauk.siesta.grammar.expression.TypedExpression.value;
import static com.cadenzauk.siesta.model.TestDatabase.testDatabase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpressionBuilderTest {
    @Mock
    private TypedExpression<String> expression1;

    @Mock
    private Scope scope;

    @Mock
    private RowMapperFactory<String> rowMapperFactory;

    @Mock
    private Transaction transaction;

    @Captor
    private ArgumentCaptor<String> sql;

    @Captor
    private ArgumentCaptor<Object[]> args;

    @Test
    void sql() {
        when(expression1.sql(scope)).thenReturn("some sql");
        ExpressionBuilder<String,BooleanExpression> sut = ExpressionBuilder.of(expression1, e -> e);

        String result = sut.sql(scope);

        assertThat(result, is("some sql"));
        verify(expression1).sql(scope);
        verifyNoMoreInteractions(expression1, scope);
    }

    @Test
    void args() {
        when(expression1.args(scope)).thenReturn(Stream.of("A", 4, BigDecimal.ONE));
        ExpressionBuilder<String,BooleanExpression> sut = ExpressionBuilder.of(expression1, e -> e);

        Object[] result = sut.args(scope).toArray();

        assertThat(result, arrayContaining("A", 4, BigDecimal.ONE));
        verify(expression1).args(scope);
        verifyNoMoreInteractions(expression1, scope);
    }

    @Test
    void precedence() {
        when(expression1.precedence()).thenReturn(Precedence.NOT);
        ExpressionBuilder<String,BooleanExpression> sut = ExpressionBuilder.of(expression1, e -> e);

        Precedence result = sut.precedence();

        assertThat(result, is(Precedence.NOT));
        verify(expression1).precedence();
        verifyNoMoreInteractions(expression1, scope);
    }

    @Test
    void label() {
        when(expression1.label(scope)).thenReturn("freddy");
        ExpressionBuilder<String,BooleanExpression> sut = ExpressionBuilder.of(expression1, e -> e);

        String result = sut.label(scope);

        assertThat(result, is("freddy"));
        verify(expression1).label(scope);
        verifyNoMoreInteractions(expression1, scope);
    }

    @Test
    void rowMapper() {
        when(expression1.rowMapperFactory(scope)).thenReturn(rowMapperFactory);
        ExpressionBuilder<String,BooleanExpression> sut = ExpressionBuilder.of(expression1, e -> e);

        RowMapperFactory<String> result = sut.rowMapperFactory(scope);

        assertThat(result, sameInstance(rowMapperFactory));
        verify(expression1).rowMapperFactory(scope);
        verifyNoMoreInteractions(expression1, scope);
    }

    private static <T> Arguments testCase(BiFunction<ExpressionBuilder<String,T>,Alias<SalespersonRow>,T> method, String expectedSql, Object... expectedArgs) {
        return arguments(method, expectedSql, expectedArgs);
    }

    private static Stream<Arguments> argsForTestExpression() {
        return Stream.of(
            testCase((e, s) -> e.isEqualTo("A"), "= ?", "A"),
            testCase((e, s) -> e.isEqualTo(value("A")), "= ?", "A"),
            testCase((e, s) -> e.isEqualTo(Label.of("SURNAME", String.class)), "= s.SURNAME"),
            testCase((e, s) -> e.isEqualTo("s", Label.of("SURNAME", String.class)), "= s.SURNAME"),
            testCase((e, s) -> e.isEqualTo(SalespersonRow::surname), "= s.SURNAME"),
            testCase((e, s) -> e.isEqualTo(SalespersonRow::middleNames), "= s.MIDDLE_NAMES"),
            testCase((e, s) -> e.isEqualTo("s", SalespersonRow::surname), "= s.SURNAME"),
            testCase((e, s) -> e.isEqualTo("s", SalespersonRow::middleNames), "= s.MIDDLE_NAMES"),
            testCase((e, s) -> e.isEqualTo(s, SalespersonRow::surname), "= s.SURNAME"),
            testCase((e, s) -> e.isEqualTo(s, SalespersonRow::middleNames), "= s.MIDDLE_NAMES"),

            testCase((e, s) -> e.isNotEqualTo("A"), "<> ?", "A"),
            testCase((e, s) -> e.isNotEqualTo(value("A")), "<> ?", "A"),
            testCase((e, s) -> e.isNotEqualTo(Label.of("SURNAME", String.class)), "<> s.SURNAME"),
            testCase((e, s) -> e.isNotEqualTo("s", Label.of("SURNAME", String.class)), "<> s.SURNAME"),
            testCase((e, s) -> e.isNotEqualTo(SalespersonRow::surname), "<> s.SURNAME"),
            testCase((e, s) -> e.isNotEqualTo(SalespersonRow::middleNames), "<> s.MIDDLE_NAMES"),
            testCase((e, s) -> e.isNotEqualTo("s", SalespersonRow::surname), "<> s.SURNAME"),
            testCase((e, s) -> e.isNotEqualTo("s", SalespersonRow::middleNames), "<> s.MIDDLE_NAMES"),
            testCase((e, s) -> e.isNotEqualTo(s, SalespersonRow::surname), "<> s.SURNAME"),
            testCase((e, s) -> e.isNotEqualTo(s, SalespersonRow::middleNames), "<> s.MIDDLE_NAMES"),

            testCase((e, s) -> e.isGreaterThan("A"), "> ?", "A"),
            testCase((e, s) -> e.isGreaterThan(value("A")), "> ?", "A"),
            testCase((e, s) -> e.isGreaterThan(Label.of("SURNAME", String.class)), "> s.SURNAME"),
            testCase((e, s) -> e.isGreaterThan("s", Label.of("SURNAME", String.class)), "> s.SURNAME"),
            testCase((e, s) -> e.isGreaterThan(SalespersonRow::surname), "> s.SURNAME"),
            testCase((e, s) -> e.isGreaterThan(SalespersonRow::middleNames), "> s.MIDDLE_NAMES"),
            testCase((e, s) -> e.isGreaterThan("s", SalespersonRow::surname), "> s.SURNAME"),
            testCase((e, s) -> e.isGreaterThan("s", SalespersonRow::middleNames), "> s.MIDDLE_NAMES"),
            testCase((e, s) -> e.isGreaterThan(s, SalespersonRow::surname), "> s.SURNAME"),
            testCase((e, s) -> e.isGreaterThan(s, SalespersonRow::middleNames), "> s.MIDDLE_NAMES"),

            testCase((e, s) -> e.isLessThan("A"), "< ?", "A"),
            testCase((e, s) -> e.isLessThan(value("A")), "< ?", "A"),
            testCase((e, s) -> e.isLessThan(Label.of("SURNAME", String.class)), "< s.SURNAME"),
            testCase((e, s) -> e.isLessThan("s", Label.of("SURNAME", String.class)), "< s.SURNAME"),
            testCase((e, s) -> e.isLessThan(SalespersonRow::surname), "< s.SURNAME"),
            testCase((e, s) -> e.isLessThan(SalespersonRow::middleNames), "< s.MIDDLE_NAMES"),
            testCase((e, s) -> e.isLessThan("s", SalespersonRow::surname), "< s.SURNAME"),
            testCase((e, s) -> e.isLessThan("s", SalespersonRow::middleNames), "< s.MIDDLE_NAMES"),
            testCase((e, s) -> e.isLessThan(s, SalespersonRow::surname), "< s.SURNAME"),
            testCase((e, s) -> e.isLessThan(s, SalespersonRow::middleNames), "< s.MIDDLE_NAMES"),

            testCase((e, s) -> e.isGreaterThanOrEqualTo("A"), ">= ?", "A"),
            testCase((e, s) -> e.isGreaterThanOrEqualTo(value("A")), ">= ?", "A"),
            testCase((e, s) -> e.isGreaterThanOrEqualTo(Label.of("SURNAME", String.class)), ">= s.SURNAME"),
            testCase((e, s) -> e.isGreaterThanOrEqualTo("s", Label.of("SURNAME", String.class)), ">= s.SURNAME"),
            testCase((e, s) -> e.isGreaterThanOrEqualTo(SalespersonRow::surname), ">= s.SURNAME"),
            testCase((e, s) -> e.isGreaterThanOrEqualTo(SalespersonRow::middleNames), ">= s.MIDDLE_NAMES"),
            testCase((e, s) -> e.isGreaterThanOrEqualTo("s", SalespersonRow::surname), ">= s.SURNAME"),
            testCase((e, s) -> e.isGreaterThanOrEqualTo("s", SalespersonRow::middleNames), ">= s.MIDDLE_NAMES"),
            testCase((e, s) -> e.isGreaterThanOrEqualTo(s, SalespersonRow::surname), ">= s.SURNAME"),
            testCase((e, s) -> e.isGreaterThanOrEqualTo(s, SalespersonRow::middleNames), ">= s.MIDDLE_NAMES"),

            testCase((e, s) -> e.isLessThanOrEqualTo("A"), "<= ?", "A"),
            testCase((e, s) -> e.isLessThanOrEqualTo(value("A")), "<= ?", "A"),
            testCase((e, s) -> e.isLessThanOrEqualTo(Label.of("SURNAME", String.class)), "<= s.SURNAME"),
            testCase((e, s) -> e.isLessThanOrEqualTo("s", Label.of("SURNAME", String.class)), "<= s.SURNAME"),
            testCase((e, s) -> e.isLessThanOrEqualTo(SalespersonRow::surname), "<= s.SURNAME"),
            testCase((e, s) -> e.isLessThanOrEqualTo(SalespersonRow::middleNames), "<= s.MIDDLE_NAMES"),
            testCase((e, s) -> e.isLessThanOrEqualTo("s", SalespersonRow::surname), "<= s.SURNAME"),
            testCase((e, s) -> e.isLessThanOrEqualTo("s", SalespersonRow::middleNames), "<= s.MIDDLE_NAMES"),
            testCase((e, s) -> e.isLessThanOrEqualTo(s, SalespersonRow::surname), "<= s.SURNAME"),
            testCase((e, s) -> e.isLessThanOrEqualTo(s, SalespersonRow::middleNames), "<= s.MIDDLE_NAMES"),

            testCase((e, s) -> e.isIn("A", "B", "C"), "in (?, ?, ?)", "A", "B", "C"),
            testCase((e, s) -> e.isNotIn("D", "E"), "not in (?, ?)", "D", "E"),
            testCase((e, s) -> e.isIn(literal("A"), value("B"), literal("C")), "in ('A', ?, 'C')", "B"),
            testCase((e, s) -> e.isNotIn(value("D"), literal("E")), "not in (?, 'E')", "D"),

            testCase((e, s) -> e.isNull(), "is null"),
            testCase((e, s) -> e.isNotNull(), "is not null"),

            testCase((e, s) -> e.isLike("A"), "like ?", "A"),
            testCase((e, s) -> e.isNotLike("A"), "not like ?", "A"),
            testCase((e, s) -> e.isLike(literal("A")), "like 'A'"),
            testCase((e, s) -> e.isNotLike(literal("A")), "not like 'A'"),

            testCase((e, s) -> e.isLike("A", "@"), "like ? escape '@'", "A"),
            testCase((e, s) -> e.isNotLike("A", "@"), "not like ? escape '@'", "A"),
            testCase((e, s) -> e.isLike(literal("A"), "@"), "like 'A' escape '@'"),
            testCase((e, s) -> e.isNotLike(literal("A"), "@"), "not like 'A' escape '@'"),

            testCase((e, s) -> e.isBetween("A").and("Z"), "between ? and ?", "A", "Z"),
            testCase((e, s) -> e.isBetween(value("A")).and("Z"), "between ? and ?", "A", "Z"),
            testCase((e, s) -> e.isBetween(SalespersonRow::surname).and("Z"), "between s.SURNAME and ?", "Z"),
            testCase((e, s) -> e.isBetween(SalespersonRow::middleNames).and("Z"), "between s.MIDDLE_NAMES and ?", "Z"),
            testCase((e, s) -> e.isBetween("s", SalespersonRow::surname).and("Z"), "between s.SURNAME and ?", "Z"),
            testCase((e, s) -> e.isBetween("s", SalespersonRow::middleNames).and("Z"), "between s.MIDDLE_NAMES and ?", "Z"),
            testCase((e, s) -> e.isBetween(s, SalespersonRow::surname).and("Z"), "between s.SURNAME and ?", "Z"),
            testCase((e, s) -> e.isBetween(s, SalespersonRow::middleNames).and("Z"), "between s.MIDDLE_NAMES and ?", "Z"),

            testCase((e, s) -> e.isNotBetween("A").and("Z"), "not between ? and ?", "A", "Z"),
            testCase((e, s) -> e.isNotBetween(value("A")).and("Z"), "not between ? and ?", "A", "Z"),
            testCase((e, s) -> e.isNotBetween(SalespersonRow::surname).and("Z"), "not between s.SURNAME and ?", "Z"),
            testCase((e, s) -> e.isNotBetween(SalespersonRow::middleNames).and("Z"), "not between s.MIDDLE_NAMES and ?", "Z"),
            testCase((e, s) -> e.isNotBetween("s", SalespersonRow::surname).and("Z"), "not between s.SURNAME and ?", "Z"),
            testCase((e, s) -> e.isNotBetween("s", SalespersonRow::middleNames).and("Z"), "not between s.MIDDLE_NAMES and ?", "Z"),
            testCase((e, s) -> e.isNotBetween(s, SalespersonRow::surname).and("Z"), "not between s.SURNAME and ?", "Z"),
            testCase((e, s) -> e.isNotBetween(s, SalespersonRow::middleNames).and("Z"), "not between s.MIDDLE_NAMES and ?", "Z"),

            testCase((e, s) -> e.concat("A").isEqualTo("AA"), "|| ? = ?", "A", "AA"),
            testCase((e, s) -> e.concat(value("A")).isEqualTo("AA"), "|| ? = ?", "A", "AA"),
            testCase((e, s) -> e.concat(SalespersonRow::surname).isEqualTo("AA"), "|| s.SURNAME = ?", "AA"),
            testCase((e, s) -> e.concat(SalespersonRow::middleNames).isEqualTo("AA"), "|| s.MIDDLE_NAMES = ?", "AA"),
            testCase((e, s) -> e.concat("s", SalespersonRow::surname).isEqualTo("AA"), "|| s.SURNAME = ?", "AA"),
            testCase((e, s) -> e.concat("s", SalespersonRow::middleNames).isEqualTo("AA"), "|| s.MIDDLE_NAMES = ?", "AA"),
            testCase((e, s) -> e.concat(s, SalespersonRow::surname).isEqualTo("AA"), "|| s.SURNAME = ?", "AA"),
            testCase((e, s) -> e.concat(s, SalespersonRow::middleNames).isEqualTo("AA"), "|| s.MIDDLE_NAMES = ?", "AA")
        );
    }

    @ParameterizedTest
    @MethodSource("argsForTestExpression")
    void testExpression(BiFunction<ExpressionBuilder<String,InWhereExpectingAnd<String>>,Alias<SalespersonRow>,InWhereExpectingAnd<String>> method, String expectedSql, Object[] expectedArgs) {
        MockitoAnnotations.initMocks(this);
        Database database = testDatabase(new AnsiDialect());
        Alias<SalespersonRow> alias = database.table(SalespersonRow.class).as("s");

        ExpressionBuilder<String,InWhereExpectingAnd<String>> select = database.from(alias)
            .select(SalespersonRow::firstName, "name")
            .where(SalespersonRow::firstName);
        method.apply(select, alias).list(transaction);

        verify(transaction).query(sql.capture(), args.capture(), any());
        assertThat(sql.getValue(), is("select s.FIRST_NAME as name from SIESTA.SALESPERSON s where s.FIRST_NAME " + expectedSql));
        assertThat(args.getValue(), is(expectedArgs));
    }

    @Test
    void isInWithNoValuesShouldThrow() {
        Database database = testDatabase(new AnsiDialect());
        ExpressionBuilder<String,InWhereExpectingAnd<SalespersonRow>> select = database.from(SalespersonRow.class)
            .where(SalespersonRow::firstName);

        //noinspection Convert2MethodRef
        calling(() -> select.isIn(new String[0]))
            .shouldThrow(IllegalArgumentException.class);
    }

    @Test
    void isNotInWithNoValuesShouldThrow() {
        Database database = testDatabase(new AnsiDialect());
        ExpressionBuilder<String,InWhereExpectingAnd<SalespersonRow>> select = database.from(SalespersonRow.class)
            .where(SalespersonRow::firstName);

        //noinspection Convert2MethodRef
        calling(() -> select.isNotIn(new String[0]))
            .shouldThrow(IllegalArgumentException.class);
    }
}
