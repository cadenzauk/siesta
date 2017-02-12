/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.   May not be used without permission.
 */

package com.cadenzauk.siesta;

import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.siesta.catalog.Table;
import com.cadenzauk.siesta.testmodel.ManufacturerRow;
import com.cadenzauk.siesta.testmodel.WidgetRow;
import liquibase.integration.spring.SpringLiquibase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Optional;

import static com.cadenzauk.siesta.Alias.column;
import static com.cadenzauk.siesta.Conditions.isEqualTo;
import static org.hamcrest.core.Is.is;

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
    public void selectFromDatabase() {
        Catalog catalog = Catalog.newBuilder().defaultSchema("TEST").build();
        catalog.table(ManufacturerRow.class, t -> t.builder(ManufacturerRow.Builder.class, ManufacturerRow.Builder::build));
        catalog.table(WidgetRow.class, t -> t.builder(WidgetRow.Builder.class, WidgetRow.Builder::build));
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
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
        catalog.table(ManufacturerRow.class).insert(jdbcTemplate, aManufacturer);
        catalog.table(WidgetRow.class).insert(jdbcTemplate, aWidget);

        Alias<WidgetRow> w = catalog.table(WidgetRow.class).as("w");
        Alias<ManufacturerRow> m = catalog.table(ManufacturerRow.class).as("m");
        Optional<WidgetRow> theSame = catalog.from(w)
            .join(m)
            .on(column(m, ManufacturerRow::manufacturerId), isEqualTo(column(w, WidgetRow::manufacturerId)))
            .where(column(w, WidgetRow::name), isEqualTo("Dodacky"))
            .and(column(w, WidgetRow::widgetId), isEqualTo(1L))
            .optional(jdbcTemplate)
            .map(Tuple2::item1);

        Assert.assertThat(theSame, is(Optional.of(aWidget)));
    }

//    @Test
//    public void selectFromDatabaseViaReflection() {
//        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//        Table<WidgetRow> table = aTable("TEST", "WIDGET", WidgetRow::newBuilder, WidgetRow.Builder::build, WidgetRow.class).buildReflectively();
//
//        WidgetRow aWidget = WidgetRow.newBuilder()
//            .widgetId(2)
//            .manufacturerId(2)
//            .name("Dodacky")
//            .description(Optional.of("Thingamibob"))
//            .build();
//
//        table.insert(jdbcTemplate, aWidget);
//
//        Optional<WidgetRow> theSame = Select.from(table)
//            .where(WidgetRow.WIDGET_ID, isEqualTo(2L))
//            .optional(jdbcTemplate);
//
//        Assert.assertThat(theSame, is(Optional.of(aWidget)));
//    }

}