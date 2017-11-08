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
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Scope;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Answers;
import org.mockito.Mock;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.apache.commons.lang3.ArrayUtils.toArray;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

class TypedExpressionTest extends MockitoTest {
    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private TypedExpression<Integer> sut;

    private static Arguments testCase(BiFunction<TypedExpression<Integer>,Alias<TestTable>,TypedExpression<Integer>> method, String expectedSql, Object... expectedArgs) {
        return Arguments.of(method, expectedSql, expectedArgs);
    }

    private static Stream<Arguments> parametersForArithmeticOperators() {
        return Stream.of(
            testCase((sut, a) -> sut.plus(4), " + ?", 4),
            testCase((sut, a) -> sut.plus(TypedExpression.literal(3)), " + 3"),
            testCase((sut, a) -> sut.plus(TestTable::mandatoryInt), " + t.MANDATORY_INT"),
            testCase((sut, a) -> sut.plus(TestTable::optionalInt), " + t.OPTIONAL_INT"),
            testCase((sut, a) -> sut.plus("t", TestTable::mandatoryInt), " + t.MANDATORY_INT"),
            testCase((sut, a) -> sut.plus("t", TestTable::optionalInt), " + t.OPTIONAL_INT"),
            testCase((sut, a) -> sut.plus(a, TestTable::mandatoryInt), " + t.MANDATORY_INT"),
            testCase((sut, a) -> sut.plus(a, TestTable::optionalInt), " + t.OPTIONAL_INT"),
            testCase((sut, a) -> sut.minus(4), " - ?", 4),

            testCase((sut, a) -> sut.minus(TypedExpression.literal(3)), " - 3"),
            testCase((sut, a) -> sut.minus(TestTable::mandatoryInt), " - t.MANDATORY_INT"),
            testCase((sut, a) -> sut.minus(TestTable::optionalInt), " - t.OPTIONAL_INT"),
            testCase((sut, a) -> sut.minus("t", TestTable::mandatoryInt), " - t.MANDATORY_INT"),
            testCase((sut, a) -> sut.minus("t", TestTable::optionalInt), " - t.OPTIONAL_INT"),
            testCase((sut, a) -> sut.minus(a, TestTable::mandatoryInt), " - t.MANDATORY_INT"),
            testCase((sut, a) -> sut.minus(a, TestTable::optionalInt), " - t.OPTIONAL_INT"),

            testCase((sut, a) -> sut.times(4), " * ?", 4),
            testCase((sut, a) -> sut.times(TypedExpression.literal(3)), " * 3"),
            testCase((sut, a) -> sut.times(TestTable::mandatoryInt), " * t.MANDATORY_INT"),
            testCase((sut, a) -> sut.times(TestTable::optionalInt), " * t.OPTIONAL_INT"),
            testCase((sut, a) -> sut.times("t", TestTable::mandatoryInt), " * t.MANDATORY_INT"),
            testCase((sut, a) -> sut.times("t", TestTable::optionalInt), " * t.OPTIONAL_INT"),
            testCase((sut, a) -> sut.times(a, TestTable::mandatoryInt), " * t.MANDATORY_INT"),
            testCase((sut, a) -> sut.times(a, TestTable::optionalInt), " * t.OPTIONAL_INT"),

            testCase((sut, a) -> sut.dividedBy(4), " / ?", 4),
            testCase((sut, a) -> sut.dividedBy(TypedExpression.literal(3)), " / 3"),
            testCase((sut, a) -> sut.dividedBy(TestTable::mandatoryInt), " / t.MANDATORY_INT"),
            testCase((sut, a) -> sut.dividedBy(TestTable::optionalInt), " / t.OPTIONAL_INT"),
            testCase((sut, a) -> sut.dividedBy("t", TestTable::mandatoryInt), " / t.MANDATORY_INT"),
            testCase((sut, a) -> sut.dividedBy("t", TestTable::optionalInt), " / t.OPTIONAL_INT"),
            testCase((sut, a) -> sut.dividedBy(a, TestTable::mandatoryInt), " / t.MANDATORY_INT"),
            testCase((sut, a) -> sut.dividedBy(a, TestTable::optionalInt), " / t.OPTIONAL_INT")
        );
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("parametersForArithmeticOperators")
    void arithmeticOperators(BiFunction<TypedExpression<Integer>,Alias<TestTable>,TypedExpression<Integer>> method, String expectedSql, Object[] expectedArgs) {
        Database database = Database.newBuilder().build();
        Alias<TestTable> alias = database.table(TestTable.class).as("t");
        Scope scope = new Scope(database, alias);
        when(sut.precedence()).thenReturn(Precedence.COLUMN);
        when(sut.sql(scope)).thenReturn("lhs");
        when(sut.args(scope)).thenReturn(Stream.of(1, 2));

        TypedExpression<Integer> result = method.apply(sut, alias);

        assertThat(result.sql(scope), is("lhs" + expectedSql));
        assertThat(result.args(scope).toArray(), is(ArrayUtils.addAll(toArray(1, 2), expectedArgs)));
    }

    private static Arguments testCase(Function<Alias<TestTable>,ExpressionBuilder<Integer,BooleanExpression>> method, String expectedSql, Object... expectedArgs) {
        return Arguments.of(method, expectedSql, expectedArgs);
    }

    private static Stream<Arguments> parametersForColumn() {
        return Stream.of(
            testCase(a -> TypedExpression.column(TestTable::mandatoryInt), "t.MANDATORY_INT"),
            testCase(a -> TypedExpression.column(TestTable::optionalInt), "t.OPTIONAL_INT"),
            testCase(a -> TypedExpression.column("t", TestTable::mandatoryInt), "t.MANDATORY_INT"),
            testCase(a -> TypedExpression.column("t", TestTable::optionalInt), "t.OPTIONAL_INT"),
            testCase(a -> TypedExpression.column(a, TestTable::mandatoryInt), "t.MANDATORY_INT"),
            testCase(a -> TypedExpression.column(a, TestTable::optionalInt), "t.OPTIONAL_INT")
        );
    }

    private static Stream<Arguments> parametersForCast() {
        return Stream.of(
            testCase(a -> TypedExpression.cast(1.0).asInteger(), "cast(? as integer)", 1.0),
            testCase(a -> TypedExpression.cast(TypedExpression.value(1.0)).asInteger(), "cast(? as integer)", 1.0)
        );
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource({"parametersForColumn", "parametersForCast"})
    void columnAncCast(Function<Alias<TestTable>,ExpressionBuilder<Integer,BooleanExpression>> method, String expectedSql, Object[]expectedArgs) {
        Database database = Database.newBuilder().build();
        Alias<TestTable> alias = database.table(TestTable.class).as("t");
        Scope scope = new Scope(database, alias);

        ExpressionBuilder<Integer,BooleanExpression> result = method.apply(alias);

        assertThat(result.sql(scope), is(expectedSql));
        assertThat(result.args(scope).toArray(), is(expectedArgs));
    }

    private static class TestTable {
        private final int mandatoryInt;
        private final Optional<Integer> optionalInt;

        public TestTable(int mandatoryInt, Optional<Integer> optionalInt) {
            this.mandatoryInt = mandatoryInt;
            this.optionalInt = optionalInt;
        }

        public int mandatoryInt() {
            return mandatoryInt;
        }

        public Optional<Integer> optionalInt() {
            return optionalInt;
        }
    }
}