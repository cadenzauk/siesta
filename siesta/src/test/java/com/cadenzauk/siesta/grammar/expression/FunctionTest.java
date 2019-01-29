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

import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Scope;
import com.cadenzauk.siesta.Transaction;
import com.cadenzauk.siesta.dialect.AnsiDialect;
import com.cadenzauk.siesta.model.TestRow;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Function;

import static com.cadenzauk.siesta.model.TestDatabase.testDatabase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
abstract class FunctionTest {
    @Mock
    protected Scope scope;
    @Mock
    private Transaction transaction;
    @Captor
    private ArgumentCaptor<String> sql;
    @Captor
    private ArgumentCaptor<Object[]> args;
    @Captor
    private ArgumentCaptor<RowMapper<?>> rowMapper;

    @ParameterizedTest
    @MethodSource("parametersForFunctionTest")
    void functionTest(Function<Alias<TestRow>, TypedExpression<?>> sutSupplier, String expectedSql, Object[] expectedArgs) {
        MockitoAnnotations.initMocks(this);
        Database database = testDatabase(new AnsiDialect());
        Alias<TestRow> alias = database.table(TestRow.class).as("s");

        database.from(alias)
            .select(sutSupplier.apply(alias), "foo")
            .list(transaction);

        verify(transaction).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select " + expectedSql + " as foo from SIESTA.TEST_TABLE s"));
        assertThat(args.getValue(), is(expectedArgs));
    }

    protected static Arguments testCase(Function<Alias<TestRow>,TypedExpression<?>> sutSupplier, String expectedSql, Object[] expectedArgs) {
        return arguments(sutSupplier, expectedSql, expectedArgs);
    }
}
