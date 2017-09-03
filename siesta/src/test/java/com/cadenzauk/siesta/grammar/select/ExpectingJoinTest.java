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

package com.cadenzauk.siesta.grammar.select;

import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Transaction;
import com.cadenzauk.siesta.dialect.AnsiDialect;
import com.cadenzauk.siesta.model.SalespersonRow;
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

import static com.cadenzauk.siesta.model.TestDatabase.testDatabase;
import static org.apache.commons.lang3.ArrayUtils.toArray;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;

class ExpectingJoinTest {
    @Mock
    private Transaction transaction;
    @Captor
    private ArgumentCaptor<String> sql;
    @Captor
    private ArgumentCaptor<Object[]> args;
    @Captor
    private ArgumentCaptor<RowMapper<?>> rowMapper;

    private static Arguments testCase1(BiFunction<ExpectingJoin1<SalespersonRow>,Alias<SalespersonRow>[],ExpectingSelect<?>> method, String expectedSql) {
        return ObjectArrayArguments.create(method, expectedSql);
    }

    private static Arguments testCase2(BiFunction<ExpectingJoin2<SalespersonRow,SalespersonRow>,Alias<SalespersonRow>[],ExpectingSelect<?>> method, String expectedSql) {
        return testCase1((s, a) -> method.apply(s.join(a[1]).on(a[1], SalespersonRow::surname).isEqualTo(a[0], SalespersonRow::surname), a),
            "join SIESTA.SALESPERSON s2 on s2.SURNAME = s1.SURNAME " + expectedSql);
    }

    private static Arguments testCase3(BiFunction<ExpectingJoin3<SalespersonRow,SalespersonRow,SalespersonRow>,Alias<SalespersonRow>[],ExpectingSelect<?>> method, String expectedSql) {
        return testCase2((s, a) -> method.apply(s.join(a[2]).on(a[2], SalespersonRow::surname).isEqualTo(a[0], SalespersonRow::surname), a),
            "join SIESTA.SALESPERSON s3 on s3.SURNAME = s1.SURNAME " + expectedSql);
    }

    private static Arguments testCase4(BiFunction<ExpectingJoin4<SalespersonRow,SalespersonRow,SalespersonRow,SalespersonRow>,Alias<SalespersonRow>[],ExpectingSelect<?>> method, String expectedSql) {
        return testCase3((s, a) -> method.apply(s.join(a[3]).on(a[3], SalespersonRow::surname).isEqualTo(a[0], SalespersonRow::surname), a),
            "join SIESTA.SALESPERSON s4 on s4.SURNAME = s1.SURNAME " + expectedSql);
    }

    private static Arguments testCase5(BiFunction<ExpectingJoin5<SalespersonRow,SalespersonRow,SalespersonRow,SalespersonRow,SalespersonRow>,Alias<SalespersonRow>[],ExpectingSelect<?>> method, String expectedSql) {
        return testCase4((s, a) -> method.apply(s.join(a[4]).on(a[4], SalespersonRow::surname).isEqualTo(a[0], SalespersonRow::surname), a),
            "join SIESTA.SALESPERSON s5 on s5.SURNAME = s1.SURNAME " + expectedSql);
    }

    private static Arguments testCase6(BiFunction<ExpectingJoin6<SalespersonRow,SalespersonRow,SalespersonRow,SalespersonRow,SalespersonRow,SalespersonRow>,Alias<SalespersonRow>[],ExpectingSelect<?>> method, String expectedSql) {
        return testCase5((s, a) -> method.apply(s.join(a[5]).on(a[5], SalespersonRow::surname).isEqualTo(a[0], SalespersonRow::surname), a),
            "join SIESTA.SALESPERSON s6 on s6.SURNAME = s1.SURNAME " + expectedSql);
    }

    private static Arguments testCase7(BiFunction<ExpectingJoin7<SalespersonRow,SalespersonRow,SalespersonRow,SalespersonRow,SalespersonRow,SalespersonRow,SalespersonRow>,Alias<SalespersonRow>[],ExpectingSelect<?>> method, String expectedSql) {
        return testCase6((s, a) -> method.apply(s.join(a[6]).on(a[6], SalespersonRow::surname).isEqualTo(a[0], SalespersonRow::surname), a),
            "join SIESTA.SALESPERSON s7 on s7.SURNAME = s1.SURNAME " + expectedSql);
    }

    private static Arguments testCase8(BiFunction<ExpectingJoin8<SalespersonRow,SalespersonRow,SalespersonRow,SalespersonRow,SalespersonRow,SalespersonRow,SalespersonRow,SalespersonRow>,Alias<SalespersonRow>[],ExpectingSelect<?>> method, String expectedSql) {
        return testCase7((s, a) -> method.apply(s.join(a[7]).on(a[7], SalespersonRow::surname).isEqualTo(a[0], SalespersonRow::surname), a),
            "join SIESTA.SALESPERSON s8 on s8.SURNAME = s1.SURNAME " + expectedSql);
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> argsForJoin() {
        return Stream.of(
            testCase1((s, a) -> s.join(a[8]).on(SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase1((s, a) -> s.join(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase1((s, a) -> s.leftJoin(a[8]).on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "left join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase1((s, a) -> s.leftJoin(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "left join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase1((s, a) -> s.rightJoin(a[8]).on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "right join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase1((s, a) -> s.rightJoin(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "right join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase1((s, a) -> s.fullOuterJoin(a[8]).on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "full outer join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase1((s, a) -> s.fullOuterJoin(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "full outer join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),

            testCase2((s, a) -> s.join(a[8]).on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase2((s, a) -> s.join(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase2((s, a) -> s.leftJoin(a[8]).on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "left join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase2((s, a) -> s.leftJoin(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "left join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase2((s, a) -> s.rightJoin(a[8]).on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "right join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase2((s, a) -> s.rightJoin(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "right join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase2((s, a) -> s.fullOuterJoin(a[8]).on(SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "full outer join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase2((s, a) -> s.fullOuterJoin(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "full outer join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),

            testCase3((s, a) -> s.join(a[8]).on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase3((s, a) -> s.join(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase3((s, a) -> s.leftJoin(a[8]).on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "left join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase3((s, a) -> s.leftJoin(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "left join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase3((s, a) -> s.rightJoin(a[8]).on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "right join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase3((s, a) -> s.rightJoin(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "right join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase3((s, a) -> s.fullOuterJoin(a[8]).on(SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "full outer join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase3((s, a) -> s.fullOuterJoin(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "full outer join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),

            testCase4((s, a) -> s.join(a[8]).on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase4((s, a) -> s.join(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase4((s, a) -> s.leftJoin(a[8]).on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "left join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase4((s, a) -> s.leftJoin(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "left join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase4((s, a) -> s.rightJoin(a[8]).on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "right join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase4((s, a) -> s.rightJoin(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "right join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase4((s, a) -> s.fullOuterJoin(a[8]).on(SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "full outer join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase4((s, a) -> s.fullOuterJoin(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "full outer join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),

            testCase5((s, a) -> s.join(a[8]).on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase5((s, a) -> s.join(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase5((s, a) -> s.leftJoin(a[8]).on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "left join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase5((s, a) -> s.leftJoin(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "left join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase5((s, a) -> s.rightJoin(a[8]).on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "right join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase5((s, a) -> s.rightJoin(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "right join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase5((s, a) -> s.fullOuterJoin(a[8]).on(SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "full outer join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase5((s, a) -> s.fullOuterJoin(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "full outer join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),

            testCase6((s, a) -> s.join(a[8]).on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase6((s, a) -> s.join(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase6((s, a) -> s.leftJoin(a[8]).on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "left join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase6((s, a) -> s.leftJoin(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "left join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase6((s, a) -> s.rightJoin(a[8]).on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "right join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase6((s, a) -> s.rightJoin(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "right join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase6((s, a) -> s.fullOuterJoin(a[8]).on(SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "full outer join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase6((s, a) -> s.fullOuterJoin(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "full outer join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),

            testCase7((s, a) -> s.join(a[8]).on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase7((s, a) -> s.join(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase7((s, a) -> s.leftJoin(a[8]).on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "left join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase7((s, a) -> s.leftJoin(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "left join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase7((s, a) -> s.rightJoin(a[8]).on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "right join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase7((s, a) -> s.rightJoin(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "right join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase7((s, a) -> s.fullOuterJoin(a[8]).on(SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "full outer join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase7((s, a) -> s.fullOuterJoin(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "full outer join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),

            testCase8((s, a) -> s.join(a[8]).on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase8((s, a) -> s.join(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase8((s, a) -> s.leftJoin(a[8]).on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "left join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase8((s, a) -> s.leftJoin(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "left join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase8((s, a) -> s.rightJoin(a[8]).on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "right join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase8((s, a) -> s.rightJoin(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "right join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase8((s, a) -> s.fullOuterJoin(a[8]).on(SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "full outer join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID"),
            testCase8((s, a) -> s.fullOuterJoin(SalespersonRow.class, "s9").on(a[8], SalespersonRow::salespersonId).isEqualTo(a[0], SalespersonRow::salespersonId), "full outer join SIESTA.SALESPERSON s9 on s9.SALESPERSON_ID = s1.SALESPERSON_ID")
        );
    }

    @ParameterizedTest
    @MethodSource(names = "argsForJoin")
    void testJoin(BiFunction<ExpectingJoin1<SalespersonRow>,Alias<SalespersonRow>[],ExpectingSelect<?>> method, String expectedSql) {
        MockitoAnnotations.initMocks(this);
        Database database = testDatabase(new AnsiDialect());
        @SuppressWarnings("unchecked") Alias<SalespersonRow>[] alias = toArray(
            database.table(SalespersonRow.class).as("s1"),
            database.table(SalespersonRow.class).as("s2"),
            database.table(SalespersonRow.class).as("s3"),
            database.table(SalespersonRow.class).as("s4"),
            database.table(SalespersonRow.class).as("s5"),
            database.table(SalespersonRow.class).as("s6"),
            database.table(SalespersonRow.class).as("s7"),
            database.table(SalespersonRow.class).as("s8"),
            database.table(SalespersonRow.class).as("s9")
        );

        method.apply(database.from(alias[0]), alias)
            .select(alias[0], SalespersonRow::firstName, "name")
            .list(transaction);

        verify(transaction).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select s1.FIRST_NAME as name from SIESTA.SALESPERSON s1 " + expectedSql));
        assertThat(args.getValue(), arrayWithSize(0));
    }
}