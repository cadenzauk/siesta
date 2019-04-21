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

import com.cadenzauk.core.RandomValues;
import com.cadenzauk.core.tuple.Tuple;
import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.siesta.ddl.TestSchema;
import com.cadenzauk.siesta.dialect.AutoDetectDialect;
import com.cadenzauk.siesta.model.SaleRow;
import com.cadenzauk.siesta.model.SalespersonRow;
import com.cadenzauk.siesta.model.TestDatabase;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.IntStream;

@ContextConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ExtendWith(SpringExtension.class)
public abstract class IntegrationTest {
    static {
        System.setProperty("log4j.rootCategory", "WARN");
    }

    private static final AtomicLong ids = new AtomicLong();

    @Resource
    protected DataSource dataSource;

    @Resource
    protected Dialect dialect;

    protected Tuple2<Long,Long> insertSalespeople(Database database, int n) {
        long lowerBound = ids.get();
        SalespersonRow[] salesPeople = IntStream.range(0, n)
            .mapToObj(i -> aRandomSalesperson())
            .toArray(SalespersonRow[]::new);
        database.insert(salesPeople);
        long upperBound = ids.get();
        return Tuple.of(lowerBound + 1, upperBound);
    }

    public static SalespersonRow aRandomSalesperson() {
        return SalespersonRow.newBuilder()
            .salespersonId(newId())
            .firstName(RandomStringUtils.randomAlphabetic(3, 12))
            .surname(RandomStringUtils.randomAlphabetic(5, 15))
            .build();
    }

    public static SalespersonRow aRandomSalesperson(Function<SalespersonRow.Builder,SalespersonRow.Builder> init) {
        return init.apply(
            SalespersonRow.newBuilder()
                .salespersonId(newId())
                .firstName(RandomStringUtils.randomAlphabetic(3, 12))
                .surname(RandomStringUtils.randomAlphabetic(5, 15)))
            .build();
    }

    public static SaleRow aRandomSale() {
        return SaleRow.newBuilder()
            .salespersonId(newId())
            .widgetId(newId())
            .quantity(RandomUtils.nextLong(10, 400))
            .price(RandomValues.randomBigDecimal(12, 2))
            .build();
    }

    protected static long newId() {
        return ids.incrementAndGet();
    }

    @Configuration
    public static class Config {
        @Bean
        public DataSource dataSource() {
            EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
            return builder
                .setName("SIESTA;LOCK_TIMEOUT=100")
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:/create-test-schema.ddl")
                .build();
        }

        @Bean
        public SpringSiesta springSiesta() {
            return new SpringSiesta()
                .setDropFirst(true)
                .setDatabase(database())
                .setSchemaDefinition(new TestSchema().schemaDefinition());
        }

        @Bean
        public Database database() {
            return TestDatabase.testDatabase(dataSource(), dialect());
        }

        @Bean
        public Dialect dialect() {
            return AutoDetectDialect.from(dataSource());
        }
    }
}
