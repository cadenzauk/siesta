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

import com.cadenzauk.siesta.Dialect;
import com.cadenzauk.siesta.dialect.function.FunctionSpec;
import com.cadenzauk.siesta.dialect.function.string.StringFunctionSpecs;
import com.cadenzauk.siesta.model.TestRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.Mock;

import java.util.stream.Stream;

import static com.cadenzauk.core.testutil.IsUtilityClass.isUtilityClass;
import static com.cadenzauk.siesta.grammar.expression.StringFunctions.instr;
import static com.cadenzauk.siesta.grammar.expression.StringFunctions.length;
import static com.cadenzauk.siesta.grammar.expression.StringFunctions.lower;
import static com.cadenzauk.siesta.grammar.expression.StringFunctions.substr;
import static com.cadenzauk.siesta.grammar.expression.StringFunctions.upper;
import static com.cadenzauk.siesta.grammar.expression.TypedExpression.value;
import static org.apache.commons.lang3.ArrayUtils.toArray;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class StringFunctionsTest extends FunctionTest {
    @Mock
    private TypedExpression<String> expression1;

    @Mock
    private TypedExpression<Integer> expression2;

    @Mock
    private Dialect dialect;

    @Mock
    private FunctionSpec substrFunction;

    @Test
    void isUtility() {
        assertThat(StringFunctions.class, isUtilityClass());
    }

    @Test
    void substrSql() {
        when(expression1.sql(scope)).thenReturn("sql1");
        when(expression2.sql(scope)).thenReturn("sql2");
        when(scope.dialect()).thenReturn(dialect);
        when(dialect.function(StringFunctionSpecs.SUBSTR)).thenReturn(substrFunction);
        when(substrFunction.sql(scope, toArray("sql1", "sql2"))).thenReturn("substring(sql2, sql1)");
        TypedExpression<String> sut = substr(expression1, expression2);

        String result = sut.sql(scope);

        assertThat(result, is("substring(sql2, sql1)"));
        verify(expression1).sql(scope);
        verify(expression2).sql(scope);
        verifyNoMoreInteractions(expression1, expression2);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> parametersForFunctionTest() {
        return Stream.of(
            testCase(s -> upper("ABC"), "upper(?)", toArray("ABC")),
            testCase(s -> upper(lower("ABC")), "upper(lower(?))", toArray("ABC")),
            testCase(s -> upper(TestRow::stringReq), "upper(s.STRING_REQ)", toArray()),
            testCase(s -> upper(TestRow::stringOpt), "upper(s.STRING_OPT)", toArray()),
            testCase(s -> upper("s", TestRow::stringReq), "upper(s.STRING_REQ)", toArray()),
            testCase(s -> upper("s", TestRow::stringOpt), "upper(s.STRING_OPT)", toArray()),
            testCase(s -> upper(s, TestRow::stringReq), "upper(s.STRING_REQ)", toArray()),
            testCase(s -> upper(s, TestRow::stringOpt), "upper(s.STRING_OPT)", toArray()),

            testCase(s -> lower("ABC"), "lower(?)", toArray("ABC")),
            testCase(s -> lower(upper("ABC")), "lower(upper(?))", toArray("ABC")),
            testCase(s -> lower(TestRow::stringReq), "lower(s.STRING_REQ)", toArray()),
            testCase(s -> lower(TestRow::stringOpt), "lower(s.STRING_OPT)", toArray()),
            testCase(s -> lower("s", TestRow::stringReq), "lower(s.STRING_REQ)", toArray()),
            testCase(s -> lower("s", TestRow::stringOpt), "lower(s.STRING_OPT)", toArray()),
            testCase(s -> lower(s, TestRow::stringReq), "lower(s.STRING_REQ)", toArray()),
            testCase(s -> lower(s, TestRow::stringOpt), "lower(s.STRING_OPT)", toArray()),

            testCase(s -> length("ABC"), "length(?)", toArray("ABC")),
            testCase(s -> length(lower("ABC")), "length(lower(?))", toArray("ABC")),
            testCase(s -> length(TestRow::stringReq), "length(s.STRING_REQ)", toArray()),
            testCase(s -> length(TestRow::stringOpt), "length(s.STRING_OPT)", toArray()),
            testCase(s -> length("s", TestRow::stringReq), "length(s.STRING_REQ)", toArray()),
            testCase(s -> length("s", TestRow::stringOpt), "length(s.STRING_OPT)", toArray()),
            testCase(s -> length(s, TestRow::stringReq), "length(s.STRING_REQ)", toArray()),
            testCase(s -> length(s, TestRow::stringOpt), "length(s.STRING_OPT)", toArray()),

            testCase(s -> substr("ABC", 1), "substr(?, ?)", toArray("ABC", 1)),
            testCase(s -> substr(upper(TestRow::stringReq), 2), "substr(upper(s.STRING_REQ), ?)", toArray(2)),
            testCase(s -> substr(TestRow::stringReq, 3), "substr(s.STRING_REQ, ?)", toArray(3)),
            testCase(s -> substr(TestRow::stringOpt, 4), "substr(s.STRING_OPT, ?)", toArray(4)),
            testCase(s -> substr("s", TestRow::stringReq, 3), "substr(s.STRING_REQ, ?)", toArray(3)),
            testCase(s -> substr("s", TestRow::stringOpt, 4), "substr(s.STRING_OPT, ?)", toArray(4)),
            testCase(s -> substr(s, TestRow::stringReq, 3), "substr(s.STRING_REQ, ?)", toArray(3)),
            testCase(s -> substr(s, TestRow::stringOpt, 4), "substr(s.STRING_OPT, ?)", toArray(4)),
            testCase(s -> substr(s.column(TestRow::stringOpt), value(5)), "substr(s.STRING_OPT, ?)", toArray(5)),
            testCase(s -> substr("ABCD", 1, 2), "substr(?, ?, ?)", toArray("ABCD", 1, 2)),

            testCase(s -> substr(upper(TestRow::stringReq), 2, 1), "substr(upper(s.STRING_REQ), ?, ?)", toArray(2, 1)),
            testCase(s -> substr(TestRow::stringReq, 3, 2), "substr(s.STRING_REQ, ?, ?)", toArray(3, 2)),
            testCase(s -> substr(TestRow::stringOpt, 4, 2), "substr(s.STRING_OPT, ?, ?)", toArray(4, 2)),
            testCase(s -> substr("s", TestRow::stringReq, 3, 2), "substr(s.STRING_REQ, ?, ?)", toArray(3, 2)),
            testCase(s -> substr("s", TestRow::stringOpt, 4, 2), "substr(s.STRING_OPT, ?, ?)", toArray(4, 2)),
            testCase(s -> substr(s, TestRow::stringReq, 3, 2), "substr(s.STRING_REQ, ?, ?)", toArray(3, 2)),
            testCase(s -> substr(s, TestRow::stringOpt, 4, 2), "substr(s.STRING_OPT, ?, ?)", toArray(4, 2)),
            testCase(s -> substr(s.column(TestRow::stringOpt), value(5), value(3)), "substr(s.STRING_OPT, ?, ?)", toArray(5, 3)),

            testCase(s -> instr("123", "ABC"), "instr(?, ?)", toArray("123", "ABC")),
            testCase(s -> instr(upper(TestRow::stringReq), "ABC"), "instr(upper(s.STRING_REQ), ?)", toArray("ABC")),
            testCase(s -> instr(TestRow::stringReq, "DEF"), "instr(s.STRING_REQ, ?)", toArray("DEF")),
            testCase(s -> instr(TestRow::stringOpt, "GHI"), "instr(s.STRING_OPT, ?)", toArray("GHI")),
            testCase(s -> instr("s", TestRow::stringReq, "KLM"), "instr(s.STRING_REQ, ?)", toArray("KLM")),
            testCase(s -> instr("s", TestRow::stringOpt, "NOP"), "instr(s.STRING_OPT, ?)", toArray("NOP")),
            testCase(s -> instr(s, TestRow::stringReq, "QRS"), "instr(s.STRING_REQ, ?)", toArray("QRS")),
            testCase(s -> instr(s, TestRow::stringOpt, "TUV"), "instr(s.STRING_OPT, ?)", toArray("TUV")),
            testCase(s -> instr(s.column(TestRow::stringOpt), lower(value("WXY"))), "instr(s.STRING_OPT, lower(?))", toArray("WXY"))
        );
    }

}
