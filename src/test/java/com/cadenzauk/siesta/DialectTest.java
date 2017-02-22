/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.siesta;

import com.cadenzauk.siesta.dialect.Db2Dialect;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class DialectTest {
    public static class Invoice {
        private int id;

        public int id() {
            return id;
        }
    }

    @Test
    public void selectivityDb2() {
        Database database = Database.newBuilder()
            .defaultSchema("AP")
            .dialect(new Db2Dialect())
            .build();

        String sql = database.from(Invoice.class)
            .where(Invoice::id).selectivity(0.0001).isEqualTo(4)
            .sql();

        assertThat(sql, is("select INVOICE.ID as INVOICE_ID from AP.INVOICE as INVOICE where INVOICE.ID = ? selectivity 0.000100"));
    }

    @Test
    public void selectivityStandard() {
        Database database = Database.newBuilder()
            .defaultSchema("AP")
            .build();

        String sql = database.from(Invoice.class)
            .where(Invoice::id).selectivity(0.0001).isEqualTo(4)
            .sql();

        assertThat(sql, is("select INVOICE.ID as INVOICE_ID from AP.INVOICE as INVOICE where INVOICE.ID = ?"));
    }
}