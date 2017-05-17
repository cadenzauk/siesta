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

import com.cadenzauk.siesta.dialect.Db2Dialect;
import org.junit.jupiter.api.Test;

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

    @Test
    void selectivityDb2() {
        Database database = Database.newBuilder()
            .defaultSchema("AP")
            .dialect(new Db2Dialect())
            .build();

        String sql = database.from(Invoice.class)
            .where(Invoice::id).selectivity(0.0001).isEqualTo(4)
            .sql();

        assertThat(sql, is("select INVOICE.ID as INVOICE_ID from AP.INVOICE INVOICE where INVOICE.ID = ? selectivity 0.000100"));
    }

    @Test
    void selectivityStandard() {
        Database database = Database.newBuilder()
            .defaultSchema("AP")
            .build();

        String sql = database.from(Invoice.class)
            .where(Invoice::id).selectivity(0.0001).isEqualTo(4)
            .sql();

        assertThat(sql, is("select INVOICE.ID as INVOICE_ID from AP.INVOICE INVOICE where INVOICE.ID = ?"));
    }
}