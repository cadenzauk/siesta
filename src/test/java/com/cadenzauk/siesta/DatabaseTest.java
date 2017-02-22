/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.siesta;

import com.cadenzauk.siesta.name.UppercaseUnderscores;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Optional;

import static com.cadenzauk.siesta.Aggregates.max;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DatabaseTest {
    @Table(name = "CUSTOMER")
    public static class Person {
        private int id;
        private String firstName;
        private Optional<String> middleNames;
        private String surname;

        @Column(name = "ROW_ID")
        public int id() {
            return id;
        }

        public String firstName() {
            return firstName;
        }

        public Optional<String> middleNames() {
            return middleNames;
        }

        public String surname() {
            return surname;
        }
    }

    @Test
    public void from() throws Exception {
        Database customers = siesta();

        String sql = customers.from(Person.class)
            .where(Person::id).isEqualTo(1)
            .sql();

        assertThat(sql, is("select CUSTOMER.ROW_ID as CUSTOMER_ROW_ID, " +
            "CUSTOMER.FIRST_NAME as CUSTOMER_FIRST_NAME, " +
            "CUSTOMER.MIDDLE_NAMES as CUSTOMER_MIDDLE_NAMES, " +
            "CUSTOMER.SURNAME as CUSTOMER_SURNAME " +
            "from CUSTOMERS.CUSTOMER as CUSTOMER " +
            "where CUSTOMER.ROW_ID = ?"));
    }

    @Test
    public void project() {
        Database customers = siesta();

        String sql = customers.from(Person.class)
            .select(Person::id)
            .where(Person::firstName).isEqualTo("Fred")
            .sql();

        assertThat(sql, is("select CUSTOMER.ROW_ID as CUSTOMER_ROW_ID " +
            "from CUSTOMERS.CUSTOMER as CUSTOMER " +
            "where CUSTOMER.FIRST_NAME = ?"));
    }

    @Test
    public void aggregate() {
        Database customers = siesta();

        String sql = customers.from(Person.class)
            .select(max(Person::id))
            .where(Person::firstName).isEqualTo("Fred")
            .sql();

        assertThat(sql, is("select max(CUSTOMER.ROW_ID) as max_CUSTOMER_ROW_ID " +
            "from CUSTOMERS.CUSTOMER as CUSTOMER " +
            "where CUSTOMER.FIRST_NAME = ?"));
    }

    @Test
    public void subQuery() {
        Database database = siesta();
        Alias<Person> outer = database.table(Person.class).as("outer");
        Alias<Person> inner = database.table(Person.class).as("inner");

        String sql = database.from(outer)
            .select(Person::firstName)
            .where(Person::id).isEqualTo(
                database.from(inner)
                    .select(max(Person::id))
                    .where(Person::surname).isEqualTo(outer, Person::surname))
            .sql();

        assertThat(sql, is("select outer.FIRST_NAME as outer_FIRST_NAME " +
            "from CUSTOMERS.CUSTOMER as outer " +
            "where outer.ROW_ID = (select max(inner.ROW_ID) as max_inner_ROW_ID" +
            " from CUSTOMERS.CUSTOMER as inner where inner.SURNAME = outer.SURNAME)"));
    }

    @Test
    public void columnsFromMethodReferences() {
        Database database = siesta();

        Alias<Person> p = database.table(Person.class).as("p");
        String sql = database.from(p)
            .select(p, Person::firstName)
            .where(p, Person::id).isEqualTo(2)
            .sql();

        assertThat(sql, is("select p.FIRST_NAME as p_FIRST_NAME from CUSTOMERS.CUSTOMER as p where p.ROW_ID = ?"));
    }

    @NotNull
    private Database siesta() {
        return Database.newBuilder()
            .defaultSchema("CUSTOMERS")
            .namingStrategy(new UppercaseUnderscores())
            .build();
    }
}