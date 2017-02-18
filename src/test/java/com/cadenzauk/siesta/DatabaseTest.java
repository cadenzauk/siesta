/*
 * Copyright (c) 2017 Cadenza United Kingdom Limited.
 *
 * All rights reserved.  May not be used without permission.
 */

package com.cadenzauk.siesta;

import com.cadenzauk.siesta.name.UppercaseUnderscores;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Optional;

import static com.cadenzauk.siesta.Aggregates.max;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DatabaseTest {
    public static class Person {
        private int id;
        private String firstName;
        private Optional<String> middleNames;
        private String surname;

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

        assertThat(sql, is("select PERSON.ID as PERSON_ID, " +
            "PERSON.FIRST_NAME as PERSON_FIRST_NAME, " +
            "PERSON.MIDDLE_NAMES as PERSON_MIDDLE_NAMES, " +
            "PERSON.SURNAME as PERSON_SURNAME " +
            "from CUSTOMERS.PERSON as PERSON " +
            "where PERSON.ID = ?"));
    }

    @Test
    public void project() {
        Database customers = siesta();

        String sql = customers.from(Person.class)
            .select(Person::id)
            .where(Person::firstName).isEqualTo("Fred")
            .sql();

        assertThat(sql, is("select PERSON.ID as PERSON_ID " +
            "from CUSTOMERS.PERSON as PERSON " +
            "where PERSON.FIRST_NAME = ?"));
    }

    @Test
    public void aggregate() {
        Database customers = siesta();

        String sql = customers.from(Person.class)
            .select(max(Person::id))
            .where(Person::firstName).isEqualTo("Fred")
            .sql();

        assertThat(sql, is("select max(PERSON.ID) as max_PERSON_ID " +
            "from CUSTOMERS.PERSON as PERSON " +
            "where PERSON.FIRST_NAME = ?"));
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
            "from CUSTOMERS.PERSON as outer " +
            "where outer.ID = (select max(inner.ID) as max_inner_ID" +
            " from CUSTOMERS.PERSON as inner where inner.SURNAME = outer.SURNAME)"));
    }

    @Test
    public void columnsFromMethodReferences() {
        Database database = siesta();

        Alias<Person> p = database.table(Person.class).as("p");
        String sql = database.from(p)
            .select(p, Person::firstName)
            .where(p, Person::id).isEqualTo(2)
            .sql();

        assertThat(sql, is("select p.FIRST_NAME as p_FIRST_NAME from CUSTOMERS.PERSON as p where p.ID = ?"));
    }

    @NotNull
    private Database siesta() {
        return Database.newBuilder()
            .defaultSchema("CUSTOMERS")
            .namingStrategy(new UppercaseUnderscores())
            .build();
    }
}