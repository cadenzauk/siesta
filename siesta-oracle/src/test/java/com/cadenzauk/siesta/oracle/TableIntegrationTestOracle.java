package com.cadenzauk.siesta.oracle;

import com.cadenzauk.siesta.IntegrationTest;
import com.cadenzauk.siesta.TableIntegrationTest;
import liquibase.integration.spring.SpringLiquibase;
import oracle.jdbc.pool.OracleDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TableIntegrationTestOracle extends TableIntegrationTest {
    @Configuration
    public static class Config {
        @Bean
        public DataSource dataSource() throws SQLException {
            OracleDataSource oracleDataSource = new OracleDataSource();
            oracleDataSource.setUser("siesta");
            oracleDataSource.setPassword("siesta");
            oracleDataSource.setURL("jdbc:oracle:thin:@127.0.0.1:1521:xe");
            return oracleDataSource;
        }

        @Bean
        public SpringLiquibase springLiquibase() throws SQLException {
            SpringLiquibase springLiquibase = new SpringLiquibase();
            springLiquibase.setDataSource(dataSource());
            springLiquibase.setChangeLog("classpath:/changelog-test.xml");
            springLiquibase.setDefaultSchema("SIESTA");
            springLiquibase.setDropFirst(true);
            return springLiquibase;
        }
    }
}
