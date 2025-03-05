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

package com.cadenzauk.siesta.xtdb;

import com.cadenzauk.core.sql.testutil.PooledDataSource;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.Dialect;
import com.cadenzauk.siesta.SpringSiesta;
import com.cadenzauk.siesta.dialect.XtdbDialect;
import com.cadenzauk.siesta.jdbc.JdbcSqlExecutor;
import com.cadenzauk.siesta.model.JsonDataRow;
import com.cadenzauk.siesta.model.ManufacturerRow;
import com.cadenzauk.siesta.model.PartRow;
import com.cadenzauk.siesta.model.PartType;
import com.cadenzauk.siesta.model.PartWithTypeRow;
import com.cadenzauk.siesta.model.SaleRow;
import com.cadenzauk.siesta.model.SalesAreaRow;
import com.cadenzauk.siesta.model.SalespersonRow;
import com.cadenzauk.siesta.model.TestDatabaseFactory;
import com.cadenzauk.siesta.model.TestRow;
import com.cadenzauk.siesta.model.UuidTestRow;
import com.cadenzauk.siesta.model.WidgetRow;
import com.cadenzauk.siesta.model.WidgetViewRow;
import org.postgresql.ds.PGConnectionPoolDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class XtdbConfig {
    @Bean
    public DataSource dataSource() {
        PGConnectionPoolDataSource pool = new PGConnectionPoolDataSource();
        pool.setPortNumbers(new int[]{5433});
        return new PooledDataSource(pool);
    }

    @Bean
    public SpringSiesta springSiesta(Database database) {
        database.delete(ManufacturerRow.class).execute();
        database.delete(WidgetRow.class).execute();
        database.delete(PartRow.class).execute();
        database.delete(SalesAreaRow.class).execute();
        database.delete(SalespersonRow.class).execute();
        return null;
    }

    @Bean
    public Dialect dialect() {
        return new XtdbDialect();
    }

    @Bean
    public TestDatabaseFactory testDatabaseFactory() {
        return new TestDatabaseFactory() {
            @Override
            public Database testDatabase(DataSource dataSource) {
                return testDatabaseBuilder()
                    .defaultSqlExecutor(JdbcSqlExecutor.of(dataSource, 0))
                    .build();
            }

            @Override
            public Database testDatabase(DataSource dataSource, Dialect dialect) {
                return testDatabaseBuilder(dialect)
                    .defaultSqlExecutor(JdbcSqlExecutor.of(dataSource, 0))
                    .build();
            }

            @Override
            public Database testDatabase(Dialect dialect) {
                return testDatabaseBuilder(dialect).build();
            }

            @Override
            public Database.Builder testDatabaseBuilder() {
                return Database.newBuilder()
                    .defaultSchema("SIESTA")
                    .dialect(new XtdbDialect())
                    .table(ManufacturerRow.class, t -> t.column(ManufacturerRow::manufacturerId, "_id").builder(ManufacturerRow.Builder::build))
                    .table(WidgetRow.class, t -> t.column(WidgetRow::widgetId, "_id").builder(WidgetRow.Builder::build))
                    .table(PartRow.class, t -> t.column(PartRow::partId, "_id").builder(PartRow.Builder::build))
                    .table(PartWithTypeRow.class, t -> t.column(PartRow::partId, "_id"))
                    .table(WidgetViewRow.class, t -> t.column(WidgetViewRow::widgetId, "_id").builder(WidgetViewRow.Builder::build))
                    .table(SalespersonRow.class, t -> t.column(SalespersonRow::salespersonId, "_id").builder(SalespersonRow.Builder::build))
                    .table(SalesAreaRow.class, t -> t.column(SalesAreaRow::salesAreaId, "_id").builder(SalesAreaRow.Builder::build))
                    .table(SaleRow.class, t -> t.column(SaleRow::saleId, "_id").builder(SaleRow.Builder::build))
                    .table(TestRow.class, t -> t.column(TestRow::guid, "_id").builder(TestRow.Builder::build))
                    .table(UuidTestRow.class, t -> t.column(UuidTestRow::guid, "_id").builder(UuidTestRow.Builder::build))
                    .table(JsonDataRow.class, t -> t.column(JsonDataRow::jsonId, "_id"))
                    .enumByName(PartType.class);
            }

            @Override
            public Database.Builder testDatabaseBuilder(Dialect dialect) {
                return testDatabaseBuilder()
                    .dialect(dialect);
            }
        };
    }
}
