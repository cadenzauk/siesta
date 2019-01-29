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

import com.cadenzauk.siesta.dialect.AnsiDialect;
import com.cadenzauk.siesta.dialect.Db2Dialect;
import com.cadenzauk.siesta.dialect.FirebirdDialect;
import com.cadenzauk.siesta.dialect.H2Dialect;
import com.cadenzauk.siesta.dialect.OracleDialect;
import com.cadenzauk.siesta.dialect.PostgresDialect;
import com.cadenzauk.siesta.dialect.SqlServerDialect;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.cadenzauk.siesta.grammar.expression.TypedExpression.literal;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class DialectTest {
    @SuppressWarnings("unused")
    public static class Invoice {
        private int id;

        public int id() {
            return id;
        }
    }

    private static Arguments testCase(Dialect dialect, String sql) {
        return arguments(dialect, sql);
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> parametersForFetchFirst() {
        return Stream.of(
            testCase(new AnsiDialect(), "select * from (select *, row_number() over() as x_row_number from (select * from invoices)) where x_row_number <= 10"),
            testCase(new Db2Dialect(), "select * from invoices fetch first 10 rows only"),
            testCase(new FirebirdDialect(), "select * from invoices rows 10"),
            testCase(new H2Dialect(), "select * from invoices limit 10"),
            testCase(new OracleDialect(), "select * from (select * from invoices) where rownum <= 10"),
            testCase(new PostgresDialect(), "select * from invoices offset 0 rows fetch next 10 rows only"),
            testCase(new SqlServerDialect(), "select top 10 * from invoices")
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForFetchFirst")
    void fetchFirst(Dialect dialect, String expectedSql) {
        String result = dialect.fetchFirst("select * from invoices", 10);

        assertThat(result, is(expectedSql));
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> parametersForSelectivity() {
        return Stream.of(
            testCase(new AnsiDialect(), "select INVOICE.ID as INVOICE_ID from AP.INVOICE INVOICE where INVOICE.ID = ?"),
            testCase(new Db2Dialect(), "select INVOICE.ID as INVOICE_ID from AP.INVOICE INVOICE where INVOICE.ID = ? selectivity 0.000100"),
            testCase(new FirebirdDialect(), "select INVOICE.ID as INVOICE_ID from INVOICE INVOICE where INVOICE.ID = cast(? as integer)"),
            testCase(new H2Dialect(), "select INVOICE.ID as INVOICE_ID from AP.INVOICE INVOICE where INVOICE.ID = ?"),
            testCase(new OracleDialect(), "select INVOICE.ID as INVOICE_ID from AP.INVOICE INVOICE where INVOICE.ID = ?"),
            testCase(new PostgresDialect(), "select INVOICE.ID as INVOICE_ID from AP.INVOICE INVOICE where INVOICE.ID = ?"),
            testCase(new OracleDialect(), "select INVOICE.ID as INVOICE_ID from AP.INVOICE INVOICE where INVOICE.ID = ?"),
            testCase(new SqlServerDialect(), "select INVOICE.ID as INVOICE_ID from AP.INVOICE INVOICE where INVOICE.ID = ?")
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForSelectivity")
    void selectivityIntegrationTest(Dialect dialect, String expectSql) {
        Database database = Database.newBuilder()
            .defaultSchema("AP")
            .dialect(dialect)
            .build();

        String sql = database.from(Invoice.class)
            .where(Invoice::id).selectivity(0.0001).isEqualTo(4)
            .sql();

        assertThat(sql, is(expectSql));
    }

    private static Arguments isolationLevelTest(Dialect dialect, IsolationLevel level, String expectSql) {
        return arguments(dialect, level, expectSql);
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> parametersForIsolationLevel() {
        return Stream.of(
            isolationLevelTest(new AnsiDialect(), IsolationLevel.UNCOMMITTED_READ, ""),
            isolationLevelTest(new AnsiDialect(), IsolationLevel.READ_COMMITTED, ""),
            isolationLevelTest(new AnsiDialect(), IsolationLevel.REPEATABLE_READ, ""),
            isolationLevelTest(new AnsiDialect(), IsolationLevel.SERIALIZABLE, ""),
            isolationLevelTest(new Db2Dialect(), IsolationLevel.UNCOMMITTED_READ, " with ur"),
            isolationLevelTest(new Db2Dialect(), IsolationLevel.READ_COMMITTED, " with cs"),
            isolationLevelTest(new Db2Dialect(), IsolationLevel.REPEATABLE_READ, " with rs"),
            isolationLevelTest(new Db2Dialect(), IsolationLevel.SERIALIZABLE, " with rr"),
            isolationLevelTest(new FirebirdDialect(), IsolationLevel.UNCOMMITTED_READ, ""),
            isolationLevelTest(new FirebirdDialect(), IsolationLevel.READ_COMMITTED, ""),
            isolationLevelTest(new FirebirdDialect(), IsolationLevel.REPEATABLE_READ, ""),
            isolationLevelTest(new FirebirdDialect(), IsolationLevel.SERIALIZABLE, ""),
            isolationLevelTest(new H2Dialect(), IsolationLevel.UNCOMMITTED_READ, ""),
            isolationLevelTest(new H2Dialect(), IsolationLevel.READ_COMMITTED, ""),
            isolationLevelTest(new H2Dialect(), IsolationLevel.REPEATABLE_READ, ""),
            isolationLevelTest(new H2Dialect(), IsolationLevel.SERIALIZABLE, ""),
            isolationLevelTest(new OracleDialect(), IsolationLevel.UNCOMMITTED_READ, ""),
            isolationLevelTest(new OracleDialect(), IsolationLevel.READ_COMMITTED, ""),
            isolationLevelTest(new OracleDialect(), IsolationLevel.REPEATABLE_READ, ""),
            isolationLevelTest(new OracleDialect(), IsolationLevel.SERIALIZABLE, ""),
            isolationLevelTest(new PostgresDialect(), IsolationLevel.UNCOMMITTED_READ, ""),
            isolationLevelTest(new PostgresDialect(), IsolationLevel.READ_COMMITTED, ""),
            isolationLevelTest(new PostgresDialect(), IsolationLevel.REPEATABLE_READ, ""),
            isolationLevelTest(new PostgresDialect(), IsolationLevel.SERIALIZABLE, ""),
            isolationLevelTest(new SqlServerDialect(), IsolationLevel.UNCOMMITTED_READ, ""),
            isolationLevelTest(new SqlServerDialect(), IsolationLevel.READ_COMMITTED, ""),
            isolationLevelTest(new SqlServerDialect(), IsolationLevel.REPEATABLE_READ, ""),
            isolationLevelTest(new SqlServerDialect(), IsolationLevel.SERIALIZABLE, "")
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForIsolationLevel")
    void isolationLevelIntegrationTest(Dialect dialect, IsolationLevel level, String expectSql) {
        Database database = Database.newBuilder()
            .defaultSchema("AP")
            .dialect(dialect)
            .build();

        String sql = database.from(Invoice.class)
            .where(Invoice::id).isEqualTo(literal(4))
            .withIsolation(level)
            .sql();

        String expectedResult = String.format("select INVOICE.ID as INVOICE_ID from %s INVOICE where INVOICE.ID = 4%s",
            dialect.qualifiedTableName("", "AP", "INVOICE"),
            expectSql);
        assertThat(sql, is(expectedResult));
    }
}