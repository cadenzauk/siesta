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

package com.cadenzauk.siesta.ddl;

import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.dialect.H2Dialect;
import com.cadenzauk.siesta.jdbc.JdbcSqlExecutor;
import com.cadenzauk.siesta.model.SalespersonRow;
import com.cadenzauk.siesta.model.TestDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

import static com.cadenzauk.siesta.IntegrationTest.aRandomSalesperson;

class SchemaGeneratorTest {
    @Test
    void generate() {
        Database database = TestDatabase.testDatabaseBuilder()
            .defaultSqlExecutor(JdbcSqlExecutor.of(dataSource()))
            .dialect(new H2Dialect())
            .build();
        SchemaGenerator schemaGenerator = new SchemaGenerator(true);
        schemaGenerator.generate(database, TestSchema::schemaDefinition);
        SalespersonRow salespersonRow = aRandomSalesperson();
        database.insert(salespersonRow);
    }

    private DataSource dataSource() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        return builder
            .setName("SIESTADB;LOCK_TIMEOUT=100")
            .setType(EmbeddedDatabaseType.H2)
            .addScript("classpath:/create-test-schema.ddl")
            .build();
    }
}
