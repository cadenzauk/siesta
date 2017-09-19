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

package com.cadenzauk.siesta.model;

import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Dialect;
import com.cadenzauk.siesta.jdbc.JdbcSqlExecutor;

import javax.sql.DataSource;

public class TestDatabase {
    public static Database testDatabase(DataSource dataSource) {
        return testDatabaseBuilder()
            .defaultSqlExecutor(JdbcSqlExecutor.of(dataSource, 0))
            .build();
    }

    public static Database testDatabase(DataSource dataSource, Dialect dialect) {
        return testDatabaseBuilder(dialect)
            .defaultSqlExecutor(JdbcSqlExecutor.of(dataSource, 0))
            .build();
    }

    public static Database testDatabase(Dialect dialect) {
        return testDatabaseBuilder(dialect).build();
    }

    public static Database.Builder testDatabaseBuilder() {
        return Database.newBuilder()
            .defaultSchema("SIESTA")
            .table(ManufacturerRow.class, t -> t.builder(ManufacturerRow.Builder::build))
            .table(WidgetRow.class, t -> t.builder(WidgetRow.Builder::build))
            .table(PartRow.class, t -> t.builder(PartRow.Builder::build))
            .table(WidgetViewRow.class, t -> t.builder(WidgetViewRow.Builder::build));
    }

    public static Database.Builder testDatabaseBuilder(Dialect dialect) {
        return testDatabaseBuilder()
            .dialect(dialect);
    }
}
