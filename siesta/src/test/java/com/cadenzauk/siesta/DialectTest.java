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
import com.cadenzauk.siesta.dialect.H2Dialect;
import com.cadenzauk.siesta.dialect.OracleDialect;
import com.cadenzauk.siesta.dialect.PostgresDialect;
import com.cadenzauk.siesta.dialect.SqlServerDialect;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ObjectArrayArguments;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DialectTest {
    @SuppressWarnings("unused")
    public static class Invoice {
        private int id;

        public int id() {
            return id;
        }
    }

    private static Arguments testCase(Dialect dialect, String sql) {
        return ObjectArrayArguments.create(dialect, sql);
    }

    private static Stream<Arguments> parametersForFetchFirst() {
        return Stream.of(
            testCase(new AnsiDialect(), "select * from (select *, row_number() over() as x_row_number from (select * from invoices)) where x_row_number <= 10"),
            testCase(new Db2Dialect(), "select * from invoices fetch first 10 rows only"),
            testCase(new H2Dialect(), "select * from invoices limit 10"),
            testCase(new OracleDialect(), "select * from (select * from invoices) where rownum <= 10"),
            testCase(new PostgresDialect(), "select * from invoices offset 0 rows fetch next 10 rows only"),
            testCase(new SqlServerDialect(), "select top 10 * from invoices")
        );
    }

    @ParameterizedTest
    @MethodSource(names = "parametersForFetchFirst")
    void fetchFirst(Dialect dialect, String expectedSql) {
        String result = dialect.fetchFirst("select * from invoices", 10);

        assertThat(result, is(expectedSql));
    }

    private static Stream<Arguments> parametersForSelectivity() {
        return Stream.of(
            testCase(new AnsiDialect(), "select INVOICE.ID as INVOICE_ID from AP.INVOICE INVOICE where INVOICE.ID = ?"),
            testCase(new Db2Dialect(), "select INVOICE.ID as INVOICE_ID from AP.INVOICE INVOICE where INVOICE.ID = ? selectivity 0.000100"),
            testCase(new H2Dialect(), "select INVOICE.ID as INVOICE_ID from AP.INVOICE INVOICE where INVOICE.ID = ?"),
            testCase(new OracleDialect(), "select INVOICE.ID as INVOICE_ID from AP.INVOICE INVOICE where INVOICE.ID = ?"),
            testCase(new PostgresDialect(), "select INVOICE.ID as INVOICE_ID from AP.INVOICE INVOICE where INVOICE.ID = ?"),
            testCase(new OracleDialect(), "select INVOICE.ID as INVOICE_ID from AP.INVOICE INVOICE where INVOICE.ID = ?"),
            testCase(new SqlServerDialect(), "select INVOICE.ID as INVOICE_ID from AP.INVOICE INVOICE where INVOICE.ID = ?")
        );
    }

    @ParameterizedTest
    @MethodSource(names = "parametersForSelectivity")
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
}