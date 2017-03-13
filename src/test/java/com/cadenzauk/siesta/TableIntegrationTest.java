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

import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.siesta.spring.JdbcTemplateSqlExecutor;
import com.cadenzauk.siesta.testmodel.ManufacturerRow;
import com.cadenzauk.siesta.testmodel.WidgetRow;
import liquibase.integration.spring.SpringLiquibase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Optional;

import static com.cadenzauk.siesta.testmodel.TestDatabase.testDatabase;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TableIntegrationTest {
    @Configuration
    public static class Config {
        @Bean
        public DataSource dataSource() {
            EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
            return builder
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:/create-test-schema.ddl")
                .build();
        }

        @Bean
        public SpringLiquibase springLiquibase() {
            SpringLiquibase springLiquibase = new SpringLiquibase();
            springLiquibase.setDataSource(dataSource());
            springLiquibase.setChangeLog("classpath:/changelog-test.xml");
            springLiquibase.setDefaultSchema("TEST");
            return springLiquibase;
        }
    }

    @Resource
    private DataSource dataSource;

    @Test
    public void selectFromDatabaseOneTable() {
        Database database = testDatabase();
        SqlExecutor sqlExecutor = JdbcTemplateSqlExecutor.of(dataSource);

        WidgetRow aWidget = WidgetRow.newBuilder()
            .widgetId(2)
            .manufacturerId(2)
            .name("Dodacky")
            .description(Optional.of("Thingamibob"))
            .build();

        database.insert(sqlExecutor, aWidget);

        Optional<WidgetRow> theSame = database.from(WidgetRow.class)
            .where(WidgetRow::widgetId).isEqualTo(2L)
            .optional(sqlExecutor);

        assertThat(theSame, is(Optional.of(aWidget)));
    }

    @Test
    public void selectFromDatabaseTwoTables() {
        Database database = testDatabase();
        SqlExecutor sqlExecutor = JdbcTemplateSqlExecutor.of(dataSource);
        ManufacturerRow aManufacturer = ManufacturerRow.newBuilder()
            .manufacturerId(2)
            .name("Makers")
            .build();
        WidgetRow aWidget = WidgetRow.newBuilder()
            .widgetId(1)
            .manufacturerId(2)
            .name("Dodacky")
            .description(Optional.of("Thingamibob"))
            .build();
        database.insert(sqlExecutor, aManufacturer);
        database.insert(sqlExecutor, aWidget);

        Alias<WidgetRow> w = database.table(WidgetRow.class).as("w");
        Alias<ManufacturerRow> m = database.table(ManufacturerRow.class).as("m");
        Optional<WidgetRow> theSame = database.from(w)
            .join(m)
            .on(m, ManufacturerRow::manufacturerId).isEqualTo(w, WidgetRow::manufacturerId)
            .where(w, WidgetRow::name).isEqualTo("Dodacky")
            .and(w, WidgetRow::widgetId).isEqualTo(1L)
            .optional(sqlExecutor)
            .map(Tuple2::item1);

        assertThat(theSame, is(Optional.of(aWidget)));
    }

    @Test
    public void update() {
        Database database = testDatabase();
        SqlExecutor sqlExecutor = JdbcTemplateSqlExecutor.of(dataSource);
        WidgetRow aWidget = WidgetRow.newBuilder()
            .widgetId(3)
            .manufacturerId(2)
            .name("Dodacky")
            .description(Optional.of("Thingamibob"))
            .build();
        database.insert(sqlExecutor, aWidget);

        database.update(WidgetRow.class)
            .set(WidgetRow::name).to("Sprocket")
            .set(WidgetRow::description).toNull()
            .where(WidgetRow::widgetId).isEqualTo(3L)
            .execute(sqlExecutor);

        Optional<WidgetRow> sprocket = database.from(WidgetRow.class)
            .where(WidgetRow::widgetId).isEqualTo(3L)
            .optional(sqlExecutor);

        assertThat(sprocket.map(WidgetRow::name), is(Optional.of("Sprocket")));
        assertThat(sprocket.flatMap(WidgetRow::description), is(Optional.empty()));
    }
}