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

import com.cadenzauk.core.MockitoTest;
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.SqlExecutor;
import com.cadenzauk.siesta.dialect.AnsiDialect;
import com.cadenzauk.siesta.model.WidgetRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ObjectArrayArguments;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.cadenzauk.siesta.grammar.expression.TypedExpression.literal;
import static com.cadenzauk.siesta.grammar.expression.TypedExpression.value;
import static com.cadenzauk.siesta.model.TestDatabase.testDatabase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class CaseExpressionTest extends MockitoTest {
    @Mock
    private Scope scope;

    @Mock
    private BooleanExpression condition;

    @Mock
    private TypedExpression<String> expression1;

    @Mock
    private TypedExpression<String> expression2;

    @Mock
    private RowMapper<String> rowMapper;

    @Mock
    private SqlExecutor sqlExecutor;

    @Captor
    private ArgumentCaptor<String> sql;

    @Captor
    private ArgumentCaptor<Object[]> args;

    @Test
    void sql() {
        CaseExpression<String> sut = new CaseExpression<>(condition, expression1);
        when(condition.sql(scope)).thenReturn("CONDITION");
        when(expression1.sql(scope)).thenReturn("EXPRESSION");

        String sql = sut.sql(scope);

        assertThat(sql, is("case when CONDITION then EXPRESSION end"));
        verifyNoMoreInteractions(condition, expression1, scope);
    }

    @Test
    void args() {
        CaseExpression<String> sut = new CaseExpression<>(condition, expression1);
        when(condition.args(scope)).thenReturn(Stream.of(1, "2"));
        when(expression1.args(scope)).thenReturn(Stream.of(3.0, BigDecimal.TEN));

        Object[] args = sut.args(scope).toArray();

        assertThat(args, arrayContaining(1, "2", 3.0, BigDecimal.TEN));
        verifyNoMoreInteractions(condition, expression1, scope);
    }

    @Test
    void precedence() {
        CaseExpression<String> sut = new CaseExpression<>(condition, expression1);

        Precedence precedence = sut.precedence();

        assertThat(precedence, is(Precedence.UNARY));
        verifyNoMoreInteractions(condition, expression1, scope);
    }

    @Test
    void label() {
        CaseExpression<String> sut1 = new CaseExpression<>(condition, expression1);
        CaseExpression<String> sut2 = new CaseExpression<>(condition, expression2);

        String label1 = sut1.label(scope);
        String label2 = sut1.label(scope);
        String label3 = sut2.label(scope);

        assertThat(label1, is(label2));
        assertThat(label1, not(is(label3)));
        assertThat(label1, startsWith("case_"));
        assertThat(label3, startsWith("case_"));
        verifyNoMoreInteractions(condition, expression1, expression2, scope);
    }

    @Test
    void rowMapper() {
        CaseExpression<String> sut = new CaseExpression<>(condition, expression1);
        when(expression1.rowMapper(scope, "test_case")).thenReturn(rowMapper);

        RowMapper<String> result = sut.rowMapper(scope, "test_case");

        assertThat(result, sameInstance(rowMapper));
        verifyNoMoreInteractions(condition, expression1, scope);
    }

    private static Arguments testCase(String expected, Function<Alias<WidgetRow>,CaseExpression<String>> f, Object... args) {
        return ObjectArrayArguments.create(expected, f, args);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> parametersForInitialWhen() {
        return Stream.of(
            testCase("when ? = ? then 'BURT'", w -> ExpressionBuilder.when(2).isEqualTo(3).then("BURT"), 2, 3),
            testCase("when 2 = ? then 'BURT'", w -> ExpressionBuilder.when(literal(2)).isEqualTo(3).then("BURT"), 3),
            testCase("when w.NAME = ? then 'BURT'", w -> ExpressionBuilder.when(WidgetRow::name).isEqualTo("FRED").then("BURT"), "FRED"),
            testCase("when w.DESCRIPTION = ? then 'BURT'", w -> ExpressionBuilder.when(WidgetRow::description).isEqualTo("FRED").then("BURT"), "FRED"),
            testCase("when w.NAME = ? then 'BURT'", w -> ExpressionBuilder.when("w", WidgetRow::name).isEqualTo("FRED").then("BURT"), "FRED"),
            testCase("when w.DESCRIPTION = ? then 'BURT'", w -> ExpressionBuilder.when("w", WidgetRow::description).isEqualTo("FRED").then("BURT"), "FRED"),
            testCase("when w.NAME = ? then 'BURT'", w -> ExpressionBuilder.when(w, WidgetRow::name).isEqualTo("FRED").then("BURT"), "FRED"),
            testCase("when w.DESCRIPTION = ? then 'BURT'", w -> ExpressionBuilder.when(w, WidgetRow::description).isEqualTo("FRED").then("BURT"), "FRED"),

            testCase("when ? = ? then 'BAZ'", w -> ExpressionBuilder.when(2).isEqualTo(3).then("BAZ"), 2, 3),
            testCase("when ? = ? then ?", w -> ExpressionBuilder.when(2).isEqualTo(3).then(value("BAZ")), 2, 3, "BAZ"),
            testCase("when ? = ? then w.NAME", w -> ExpressionBuilder.when(2).isEqualTo(3).then(WidgetRow::name), 2, 3),
            testCase("when ? = ? then w.DESCRIPTION", w -> ExpressionBuilder.when(2).isEqualTo(3).then(WidgetRow::description), 2, 3),
            testCase("when ? = ? then w.NAME", w -> ExpressionBuilder.when(2).isEqualTo(3).then("w", WidgetRow::name), 2, 3),
            testCase("when ? = ? then w.DESCRIPTION", w -> ExpressionBuilder.when(2).isEqualTo(3).then("w", WidgetRow::description), 2, 3),
            testCase("when ? = ? then w.NAME", w -> ExpressionBuilder.when(2).isEqualTo(3).then(w, WidgetRow::name), 2, 3),
            testCase("when ? = ? then w.DESCRIPTION", w -> ExpressionBuilder.when(2).isEqualTo(3).then(w, WidgetRow::description), 2, 3)
        );
    }

    @ParameterizedTest
    @MethodSource(names = "parametersForInitialWhen")
    void initialWhen(String expected, Function<Alias<WidgetRow>,CaseExpression<String>> f, Object[] expectedArgs) {
        Database database = testDatabase(new AnsiDialect());
        Alias<WidgetRow> w = database.table(WidgetRow.class).as("w");

        database.from(w)
            .select(f.apply(w).orElse(literal("DEFAULT")), "name")
            .list(sqlExecutor);

        verify(sqlExecutor).query(sql.capture(), args.capture(), any());
        assertThat(sql.getValue(), is("select case " + expected + " else 'DEFAULT' end as name " +
            "from SIESTA.WIDGET w"));
        assertThat(args.getValue(), is(expectedArgs));
    }

    private static Arguments testCase(String expected, BiFunction<CaseExpression<String>,Alias<WidgetRow>,TypedExpression<String>> f, Object... args) {
        return ObjectArrayArguments.create(expected, f, args);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> parametersForSubsequentClauses() {
        return Stream.of(
            testCase("else 'FRED'", (c, w) -> c.orElse("FRED")),
            testCase("else ?", (c, w) -> c.orElse(value("JOE")), "JOE"),
            testCase("else w.NAME", (c, w) -> c.orElse(WidgetRow::name)),
            testCase("else w.DESCRIPTION", (c, w) -> c.orElse(WidgetRow::description)),
            testCase("else w.NAME", (c, w) -> c.orElse("w", WidgetRow::name)),
            testCase("else w.DESCRIPTION", (c, w) -> c.orElse("w", WidgetRow::description)),
            testCase("else w.NAME", (c, w) -> c.orElse(w, WidgetRow::name)),
            testCase("else w.DESCRIPTION", (c, w) -> c.orElse(w, WidgetRow::description)),

            testCase("when ? = ? then 'BOB'", (c, w) -> c.when("BRUCE").isEqualTo("BRUCE").then(literal("BOB")), "BRUCE", "BRUCE"),
            testCase("when 'BRUCE' = ? then 'BOB'", (c, w) -> c.when(literal("BRUCE")).isEqualTo("BRUCE").then(literal("BOB")), "BRUCE"),
            testCase("when w.NAME = ? then 'BOB'", (c, w) -> c.when(WidgetRow::name).isEqualTo("BRUCE").then(literal("BOB")), "BRUCE"),
            testCase("when w.DESCRIPTION = ? then 'BOB'", (c, w) -> c.when(WidgetRow::description).isEqualTo("BRUCE").then(literal("BOB")), "BRUCE"),
            testCase("when w.NAME = ? then 'BOB'", (c, w) -> c.when("w", WidgetRow::name).isEqualTo("BRUCE").then(literal("BOB")), "BRUCE"),
            testCase("when w.DESCRIPTION = ? then 'BOB'", (c, w) -> c.when("w", WidgetRow::description).isEqualTo("BRUCE").then(literal("BOB")), "BRUCE"),
            testCase("when w.NAME = ? then 'BOB'", (c, w) -> c.when(w, WidgetRow::name).isEqualTo("BRUCE").then(literal("BOB")), "BRUCE"),
            testCase("when w.DESCRIPTION = ? then 'BOB'", (c, w) -> c.when(w, WidgetRow::description).isEqualTo("BRUCE").then(literal("BOB")), "BRUCE"),

            testCase("when ? = ? then 'BOB'", (c, w) -> c.when(3).isEqualTo(4).then("BOB"), 3, 4),
            testCase("when ? = ? then ?", (c, w) -> c.when(3).isEqualTo(4).then(value("BOB")), 3, 4, "BOB"),
            testCase("when ? = ? then w.NAME", (c, w) -> c.when(3).isEqualTo(4).then(WidgetRow::name), 3, 4),
            testCase("when ? = ? then w.DESCRIPTION", (c, w) -> c.when(3).isEqualTo(4).then(WidgetRow::description), 3, 4),
            testCase("when ? = ? then w.NAME", (c, w) -> c.when(3).isEqualTo(4).then("w", WidgetRow::name), 3, 4),
            testCase("when ? = ? then w.DESCRIPTION", (c, w) -> c.when(3).isEqualTo(4).then("w", WidgetRow::description), 3, 4),
            testCase("when ? = ? then w.NAME", (c, w) -> c.when(3).isEqualTo(4).then(w, WidgetRow::name), 3, 4),
            testCase("when ? = ? then w.DESCRIPTION", (c, w) -> c.when(3).isEqualTo(4).then(w, WidgetRow::description), 3, 4)
        );
    }

    @ParameterizedTest
    @MethodSource(names = "parametersForSubsequentClauses")
    void subsequentClauses(String expected, BiFunction<CaseExpression<String>,Alias<WidgetRow>,TypedExpression<String>> f, Object[] expectedArgs) {
        Database database = testDatabase(new AnsiDialect());
        Alias<WidgetRow> w = database.table(WidgetRow.class).as("w");

        database.from(w)
            .select(f.apply(ExpressionBuilder.when(literal(1)).isEqualTo(literal(2)).then("UNLIKELY"), w), "name")
            .list(sqlExecutor);

        verify(sqlExecutor).query(sql.capture(), args.capture(), any());
        assertThat(sql.getValue(), is("select case when 1 = 2 then 'UNLIKELY' " + expected + " end as name " +
            "from SIESTA.WIDGET w"));
        assertThat(args.getValue(), is(expectedArgs));
    }
}