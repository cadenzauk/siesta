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

package com.cadenzauk.siesta.oracle;

import com.cadenzauk.core.sql.testutil.PooledDataSource;
import com.cadenzauk.siesta.Dialect;
import com.cadenzauk.siesta.dialect.OracleDialect;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class OracleConfig {
    @Bean
    public DataSource dataSource() throws SQLException {
        OracleConnectionPoolDataSource pool = new OracleConnectionPoolDataSource();
        pool.setUser("siesta");
        pool.setPassword("siesta");
        pool.setURL("jdbc:oracle:thin:@127.0.0.1:1521/xepdb1");
        return new PooledDataSource(pool, "ALTER SESSION SET NLS_DATE_FORMAT='YYYY-MM-DD'");
    }

    @Bean
    public Dialect dialect() {
        return new OracleDialect();
    }
}
