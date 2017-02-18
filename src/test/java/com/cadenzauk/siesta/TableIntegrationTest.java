/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.siesta;

import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.siesta.spring.JdbcTemplateSqlExecutor;
import com.cadenzauk.siesta.testmodel.ManufacturerRow;
import com.cadenzauk.siesta.testmodel.WidgetRow;
import liquibase.integration.spring.SpringLiquibase;
import org.jetbrains.annotations.NotNull;
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
    public void selectFromDatabaseOneTable() {
        Database database = database();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        WidgetRow aWidget = WidgetRow.newBuilder()
            .widgetId(2)
            .manufacturerId(2)
            .name("Dodacky")
            .description(Optional.of("Thingamibob"))
            .build();

        database.insert(jdbcTemplate, aWidget);

        Optional<WidgetRow> theSame = database.from(WidgetRow.class)
            .where(WidgetRow::widgetId).isEqualTo(2L)
            .optional(JdbcTemplateSqlExecutor.of(jdbcTemplate));

        Assert.assertThat(theSame, is(Optional.of(aWidget)));
    }

    @Test
    public void selectFromDatabaseTwoTables() {
        Database database = database();
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
        database.insert(jdbcTemplate, aManufacturer);
        database.insert(jdbcTemplate, aWidget);

        Alias<WidgetRow> w = database.table(WidgetRow.class).as("w");
        Alias<ManufacturerRow> m = database.table(ManufacturerRow.class).as("m");
        Optional<WidgetRow> theSame = database.from(w)
            .join(m)
            .on(m, ManufacturerRow::manufacturerId).isEqualTo(w, WidgetRow::manufacturerId)
            .where(w, WidgetRow::name).isEqualTo("Dodacky")
            .and(w, WidgetRow::widgetId).isEqualTo(1L)
            .optional(JdbcTemplateSqlExecutor.of(jdbcTemplate))
            .map(Tuple2::item1);

        Assert.assertThat(theSame, is(Optional.of(aWidget)));
    }

    @NotNull
    private Database database() {
        Database database = Database.newBuilder().defaultSchema("TEST").build();
        database.table(ManufacturerRow.class, t -> t.builder(ManufacturerRow.Builder.class, ManufacturerRow.Builder::build));
        database.table(WidgetRow.class, t -> t.builder(WidgetRow.Builder.class, WidgetRow.Builder::build));
        return database;
    }

}