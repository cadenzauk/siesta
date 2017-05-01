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

import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.test.model.SalespersonRow;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ObjectArrayArguments;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.xml.crypto.Data;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static com.cadenzauk.siesta.test.model.TestDatabase.testDatabase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BooleanExpressionTest {
    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private BooleanExpression sut;

    @Captor
    private ArgumentCaptor<BooleanExpression> appendArg;

    private static <T> Arguments testCase(BiFunction<Alias<SalespersonRow>,BooleanExpression,T> method, String expectedSql) {
        return ObjectArrayArguments.create(method, expectedSql);
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> argsForAnd() {
        return Stream.of(
            testCase((a, e) -> e.and(TypedExpression.column(SalespersonRow::firstName).isEqualTo("Fred")), "s.FIRST_NAME = ?"),
            testCase((a, e) -> e.and(TypedExpression.column(SalespersonRow::firstName)).isEqualTo("Fred"), "s.FIRST_NAME = ?"),
            testCase((a, e) -> e.and(SalespersonRow::firstName).isEqualTo("Fred"), "s.FIRST_NAME = ?"),
            testCase((a, e) -> e.and(SalespersonRow::middleNames).isEqualTo("Fred"), "s.MIDDLE_NAMES = ?"),
            testCase((a, e) -> e.and("s", SalespersonRow::firstName).isEqualTo("Fred"), "s.FIRST_NAME = ?"),
            testCase((a, e) -> e.and("s", SalespersonRow::middleNames).isEqualTo("Fred"), "s.MIDDLE_NAMES = ?"),
            testCase((a, e) -> e.and(a, SalespersonRow::firstName).isEqualTo("Fred"), "s.FIRST_NAME = ?"),
            testCase((a, e) -> e.and(a, SalespersonRow::middleNames).isEqualTo("Fred"), "s.MIDDLE_NAMES = ?")
        );
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> argsForOr() {
        return Stream.of(
            testCase((a, e) -> e.or(TypedExpression.column(SalespersonRow::firstName).isEqualTo("Fred")), "s.FIRST_NAME = ?"),
            testCase((a, e) -> e.or(TypedExpression.column(SalespersonRow::firstName)).isEqualTo("Fred"), "s.FIRST_NAME = ?"),
            testCase((a, e) -> e.or(SalespersonRow::firstName).isEqualTo("Fred"), "s.FIRST_NAME = ?"),
            testCase((a, e) -> e.or(SalespersonRow::middleNames).isEqualTo("Fred"), "s.MIDDLE_NAMES = ?"),
            testCase((a, e) -> e.or("s", SalespersonRow::firstName).isEqualTo("Fred"), "s.FIRST_NAME = ?"),
            testCase((a, e) -> e.or("s", SalespersonRow::middleNames).isEqualTo("Fred"), "s.MIDDLE_NAMES = ?"),
            testCase((a, e) -> e.or(a, SalespersonRow::firstName).isEqualTo("Fred"), "s.FIRST_NAME = ?"),
            testCase((a, e) -> e.or(a, SalespersonRow::middleNames).isEqualTo("Fred"), "s.MIDDLE_NAMES = ?")
        );
    }

    @ParameterizedTest
    @MethodSource(names = "argsForAnd")
    void testAnd(BiFunction<Alias<SalespersonRow>,BooleanExpression, BooleanExpression> method, String expectedSql) {
        MockitoAnnotations.initMocks(this);
        Database database = testDatabase();
        Alias<SalespersonRow> alias = database.table(SalespersonRow.class).as("s");
        Scope scope = new Scope(database, alias);
        when(sut.appendAnd(any())).thenReturn(sut);

        method.apply(alias, sut);

        verify(sut).appendAnd(appendArg.capture());
        assertThat(appendArg.getValue().sql(scope), is(expectedSql));
    }

    @ParameterizedTest
    @MethodSource(names = "argsForOr")
    void testOr(BiFunction<Alias<SalespersonRow>,BooleanExpression, BooleanExpression> method, String expectedSql) {
        MockitoAnnotations.initMocks(this);
        Database database = testDatabase();
        Alias<SalespersonRow> alias = database.table(SalespersonRow.class).as("s");
        Scope scope = new Scope(database, alias);
        when(sut.appendAnd(any())).thenReturn(sut);

        method.apply(alias, sut);

        verify(sut).appendOr(appendArg.capture());
        assertThat(appendArg.getValue().sql(scope), is(expectedSql));
    }
}