package com.cadenzauk.siesta.db2;

import com.cadenzauk.core.sql.PooledDataSource;
import com.cadenzauk.siesta.Dialect;
import com.cadenzauk.siesta.TableIntegrationTest;
import com.cadenzauk.siesta.dialect.Db2Dialect;
import com.ibm.db2.jcc.DB2ConnectionPoolDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;
import java.sql.SQLException;

@ContextConfiguration
public class TableIntegrationTestDb2 extends TableIntegrationTest {
    @Configuration
    public static class Config {
        @Bean
        public DataSource dataSource() throws SQLException {
            DB2ConnectionPoolDataSource pool = new DB2ConnectionPoolDataSource();
            pool.setDatabaseName("SIESTA");
            return new PooledDataSource(pool);
        }

        @Bean
        public Dialect dialect() {
            return new Db2Dialect();
        }
    }
}
