/*
 * Copyright (c) 2018 Cadenza United Kingdom Limited
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

package com.cadenzauk.siesta.db2;

import com.cadenzauk.core.sql.testutil.PooledDataSource;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.IntegrationTest;
import com.cadenzauk.siesta.jdbc.JdbcSqlExecutor;
import com.ibm.db2.jcc.DB2ConnectionPoolDataSource;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

import static com.cadenzauk.siesta.model.TestDatabase.testDatabase;

@ContextConfiguration(classes = Db2Config.class)
class StatementCachingPerformanceTestDb2 extends IntegrationTest {
    private final static Logger LOG = LoggerFactory.getLogger(StatementCachingPerformanceTestDb2.class);

    @Test
    void cachingIsFaster() {
        Database database = testDatabase(dataSource);

        JdbcSqlExecutor cached = JdbcSqlExecutor.of(dataSource(20));
        JdbcSqlExecutor uncached = JdbcSqlExecutor.of(dataSource(0));

        for (int i = 0; i < 200; ++i) {
            database.insert(uncached, aRandomSalesperson());
        }
        for (int i = 0; i < 200; ++i) {
            database.insert(cached, aRandomSalesperson());
        }
        uncached.update("call sysproc.admin_cmd('RUNSTATS ON TABLE SIESTA.SALESPERSON AND SAMPLED DETAILED INDEXES ALL')", new Object[0]);
        uncached.update("call sysproc.admin_cmd('REORG TABLE SIESTA.SALESPERSON INPLACE')", new Object[0]);

        long start = System.nanoTime();
        for (int i = 0; i < 5000; ++i) {
            database.insert(uncached, aRandomSalesperson());
        }
        long middle = System.nanoTime();
        for (int i = 0; i < 5000; ++i) {
            database.insert(cached, aRandomSalesperson());
        }
        long end = System.nanoTime();
        LOG.info("Time Uncached = {} ms", TimeUnit.NANOSECONDS.toMillis(middle - start));
        LOG.info("Time Cached = {} ms", TimeUnit.NANOSECONDS.toMillis(end - middle));
    }

    private DataSource dataSource(int cacheSize) {
        DB2ConnectionPoolDataSource pool = new DB2ConnectionPoolDataSource();
        pool.setDatabaseName("SIESTA");
        pool.setDriverType(2);
        pool.setConcurrentAccessResolution(1);
        pool.setMaxStatements(cacheSize);
        return new PooledDataSource(pool);
    }

}
