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

import co.unruly.matchers.StreamMatchers;
import com.cadenzauk.siesta.dialect.AnsiDialect;
import com.cadenzauk.siesta.dialect.H2Dialect;
import com.cadenzauk.siesta.grammar.dml.ExecutableStatementClause;
import com.cadenzauk.siesta.grammar.dml.ExpectingWhere;
import com.cadenzauk.siesta.grammar.dml.InWhereExpectingAnd;
import com.cadenzauk.siesta.grammar.expression.Label;
import com.cadenzauk.siesta.grammar.select.Select;
import com.cadenzauk.siesta.model.SalespersonRow;
import com.cadenzauk.siesta.model.WidgetRow;
import com.cadenzauk.siesta.name.UppercaseUnderscores;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static com.cadenzauk.siesta.grammar.expression.Aggregates.max;
import static com.cadenzauk.siesta.grammar.expression.TupleBuilder.tuple;
import static com.cadenzauk.siesta.grammar.expression.TypedExpression.literal;
import static com.cadenzauk.siesta.grammar.expression.olap.Olap.rowNumber;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class DatabaseTest {
    @Mock
    private SqlExecutor sqlExecutor;

    @Mock
    private Transaction transaction;

    @Captor
    private ArgumentCaptor<String> sqlCaptor;

    @Captor
    private ArgumentCaptor<Object[]> argCaptor;

    @SuppressWarnings("unused")
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
    void fromAlias() {
        Database database = database();

        String sql = database.from(database.table(Person.class).as("p"))
            .where(Person::id).isEqualTo(1)
            .sql();

        assertThat(sql, is("select p.ROW_ID as p_ROW_ID, " +
            "p.FIRST_NAME as p_FIRST_NAME, " +
            "p.MIDDLE_NAMES as p_MIDDLE_NAMES, p.SURNAME as p_SURNAME " +
            "from CUSTOMERS.CUSTOMER p " +
            "where p.ROW_ID = ?"));
    }

    @Test
    void fromClass() {
        Database database = database();

        String sql = database.from(Person.class)
            .where(Person::id).isEqualTo(1)
            .sql();

        assertThat(sql, is("select CUSTOMER.ROW_ID as CUSTOMER_ROW_ID, " +
            "CUSTOMER.FIRST_NAME as CUSTOMER_FIRST_NAME, " +
            "CUSTOMER.MIDDLE_NAMES as CUSTOMER_MIDDLE_NAMES, " +
            "CUSTOMER.SURNAME as CUSTOMER_SURNAME " +
            "from CUSTOMERS.CUSTOMER CUSTOMER " +
            "where CUSTOMER.ROW_ID = ?"));
    }

    @Test
    void fromClassAndAlias() {
        Database database = database();

        String sql = database.from(Person.class, "p")
            .where(Person::id).isEqualTo(1)
            .sql();

        assertThat(sql, is("select p.ROW_ID as p_ROW_ID, " +
            "p.FIRST_NAME as p_FIRST_NAME, " +
            "p.MIDDLE_NAMES as p_MIDDLE_NAMES, p.SURNAME as p_SURNAME " +
            "from CUSTOMERS.CUSTOMER p " +
            "where p.ROW_ID = ?"));
    }

    @Test
    void project() {
        Database customers = database();

        String sql = customers.from(Person.class)
            .select(Person::id)
            .where(Person::firstName).isEqualTo("Fred")
            .sql();

        assertThat(sql, is("select CUSTOMER.ROW_ID as CUSTOMER_ROW_ID " +
            "from CUSTOMERS.CUSTOMER CUSTOMER " +
            "where CUSTOMER.FIRST_NAME = ?"));
    }

    @Test
    void aggregate() {
        Database customers = database();

        String sql = customers.from(Person.class)
            .select(max(Person::id))
            .where(Person::firstName).isEqualTo("Fred")
            .sql();

        assertThat(sql, is("select max(CUSTOMER.ROW_ID) as max_CUSTOMER_ROW_ID " +
            "from CUSTOMERS.CUSTOMER CUSTOMER " +
            "where CUSTOMER.FIRST_NAME = ?"));
    }

    @Test
    void subQuery() {
        Database database = database();
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
            "from CUSTOMERS.CUSTOMER outer " +
            "where outer.ROW_ID = (select max(inner.ROW_ID) as max_inner_ROW_ID" +
            " from CUSTOMERS.CUSTOMER inner where inner.SURNAME = outer.SURNAME)"));
    }

    @Test
    void columnsFromMethodReferences() {
        Database database = database();

        Alias<Person> p = database.table(Person.class).as("p");
        String sql = database.from(p)
            .select(p, Person::firstName)
            .where(p, Person::id).isEqualTo(2)
            .sql();

        assertThat(sql, is("select p.FIRST_NAME as p_FIRST_NAME from CUSTOMERS.CUSTOMER p where p.ROW_ID = ?"));
    }

    @Test
    void insertZeroLength() {
        Database database = Database.newBuilder()
            .defaultSqlExecutor(sqlExecutor)
            .dialect(new H2Dialect())
            .build();

        int rows = database.insert();

        verifyZeroInteractions(sqlExecutor);
        assertThat(rows, is(0));
    }

    @Test
    void insertZeroLengthSqlExecutor() {
        Database database = Database.newBuilder()
            .dialect(new H2Dialect())
            .build();

        int rows = database.insert(sqlExecutor);

        verifyZeroInteractions(sqlExecutor);
        assertThat(rows, is(0));
    }

    @Test
    void insertZeroLengthTransaction() {
        Database database = Database.newBuilder()
            .defaultSqlExecutor(sqlExecutor)
            .dialect(new H2Dialect())
            .build();

        int rows = database.insert(transaction);

        verifyZeroInteractions(transaction);
        verifyZeroInteractions(sqlExecutor);
        assertThat(rows, is(0));
    }

    @Test
    void insertMultipleInOneStatement() {
        Database database = Database.newBuilder()
            .defaultSqlExecutor(sqlExecutor)
            .dialect(new H2Dialect())
            .build();
        SalespersonRow[] salespersons = ArrayUtils.toArray(
            IntegrationTest.aRandomSalesperson(),
            IntegrationTest.aRandomSalesperson());

        database.insert(salespersons);

        verify(sqlExecutor).update(sqlCaptor.capture(), argCaptor.capture());
        verifyNoMoreInteractions(sqlExecutor);
        assertThat(sqlCaptor.getValue(), is("insert into SIESTA.SALESPERSON " +
            "(SALESPERSON_ID, FIRST_NAME, MIDDLE_NAMES, SURNAME, NUMBER_OF_SALES, COMMISSION) " +
            "values (?, ?, ?, ?, ?, ?), " +
            "(?, ?, ?, ?, ?, ?)"));
        assertThat(argCaptor.getValue(), arrayContaining(
            salespersons[0].salespersonId(),
            salespersons[0].firstName(),
            salespersons[0].middleNames().orElse(null),
            salespersons[0].surname(),
            salespersons[0].numberOfSales(),
            salespersons[0].commission().orElse(null),
            salespersons[1].salespersonId(),
            salespersons[1].firstName(),
            salespersons[1].middleNames().orElse(null),
            salespersons[1].surname(),
            salespersons[1].numberOfSales(),
            salespersons[1].commission().orElse(null)
        ));
    }

    @Test
    void insertMultipleInOneStatementSqlExecutor() {
        Database database = Database.newBuilder()
            .defaultSqlExecutor(sqlExecutor)
            .dialect(new H2Dialect())
            .build();
        SalespersonRow[] salespersons = ArrayUtils.toArray(
            IntegrationTest.aRandomSalesperson(),
            IntegrationTest.aRandomSalesperson());

        database.insert(sqlExecutor, salespersons);

        verify(sqlExecutor).update(sqlCaptor.capture(), argCaptor.capture());
        verifyNoMoreInteractions(sqlExecutor);
        assertThat(sqlCaptor.getValue(), is("insert into SIESTA.SALESPERSON " +
            "(SALESPERSON_ID, FIRST_NAME, MIDDLE_NAMES, SURNAME, NUMBER_OF_SALES, COMMISSION) " +
            "values (?, ?, ?, ?, ?, ?), " +
            "(?, ?, ?, ?, ?, ?)"));
        assertThat(argCaptor.getValue(), arrayContaining(
            salespersons[0].salespersonId(),
            salespersons[0].firstName(),
            salespersons[0].middleNames().orElse(null),
            salespersons[0].surname(),
            salespersons[0].numberOfSales(),
            salespersons[0].commission().orElse(null),
            salespersons[1].salespersonId(),
            salespersons[1].firstName(),
            salespersons[1].middleNames().orElse(null),
            salespersons[1].surname(),
            salespersons[1].numberOfSales(),
            salespersons[1].commission().orElse(null)
        ));
    }

    @Test
    void insertMultipleInOneStatementTransaction() {
        Database database = Database.newBuilder()
            .dialect(new H2Dialect())
            .build();
        SalespersonRow[] salespersons = ArrayUtils.toArray(
            IntegrationTest.aRandomSalesperson(),
            IntegrationTest.aRandomSalesperson());

        database.insert(transaction, salespersons);

        verify(transaction).update(sqlCaptor.capture(), argCaptor.capture());
        verifyNoMoreInteractions(sqlExecutor);
        verifyNoMoreInteractions(transaction);
        assertThat(sqlCaptor.getValue(), is("insert into SIESTA.SALESPERSON " +
            "(SALESPERSON_ID, FIRST_NAME, MIDDLE_NAMES, SURNAME, NUMBER_OF_SALES, COMMISSION) " +
            "values (?, ?, ?, ?, ?, ?), " +
            "(?, ?, ?, ?, ?, ?)"));
        assertThat(argCaptor.getValue(), arrayContaining(
            salespersons[0].salespersonId(),
            salespersons[0].firstName(),
            salespersons[0].middleNames().orElse(null),
            salespersons[0].surname(),
            salespersons[0].numberOfSales(),
            salespersons[0].commission().orElse(null),
            salespersons[1].salespersonId(),
            salespersons[1].firstName(),
            salespersons[1].middleNames().orElse(null),
            salespersons[1].surname(),
            salespersons[1].numberOfSales(),
            salespersons[1].commission().orElse(null)
        ));
    }

    @Test
    void insertMultipleInOneStatementTransactionAsync() {
        Database database = Database.newBuilder()
            .dialect(new H2Dialect())
            .build();
        SalespersonRow[] salespersons = ArrayUtils.toArray(
            IntegrationTest.aRandomSalesperson(),
            IntegrationTest.aRandomSalesperson());
        when(transaction.updateAsync(any(), any())).thenReturn(completedFuture(2));

        database.insertAsync(transaction, salespersons);

        verify(transaction).updateAsync(sqlCaptor.capture(), argCaptor.capture());
        verifyNoMoreInteractions(sqlExecutor);
        verifyNoMoreInteractions(transaction);
        assertThat(sqlCaptor.getValue(), is("insert into SIESTA.SALESPERSON " +
            "(SALESPERSON_ID, FIRST_NAME, MIDDLE_NAMES, SURNAME, NUMBER_OF_SALES, COMMISSION) " +
            "values (?, ?, ?, ?, ?, ?), " +
            "(?, ?, ?, ?, ?, ?)"));
        assertThat(argCaptor.getValue(), arrayContaining(
            salespersons[0].salespersonId(),
            salespersons[0].firstName(),
            salespersons[0].middleNames().orElse(null),
            salespersons[0].surname(),
            salespersons[0].numberOfSales(),
            salespersons[0].commission().orElse(null),
            salespersons[1].salespersonId(),
            salespersons[1].firstName(),
            salespersons[1].middleNames().orElse(null),
            salespersons[1].surname(),
            salespersons[1].numberOfSales(),
            salespersons[1].commission().orElse(null)
        ));
    }

    @Test
    void insertMultipleInMultipleStatements() {
        Database database = Database.newBuilder()
            .defaultSqlExecutor(sqlExecutor)
            .dialect(new AnsiDialect())
            .build();
        SalespersonRow[] salespersons = ArrayUtils.toArray(
            IntegrationTest.aRandomSalesperson(),
            IntegrationTest.aRandomSalesperson());

        database.insert(salespersons);

        verify(sqlExecutor, times(2)).update(sqlCaptor.capture(), argCaptor.capture());
        verifyNoMoreInteractions(sqlExecutor);
        assertThat(sqlCaptor.getValue(), is("insert into SIESTA.SALESPERSON " +
            "(SALESPERSON_ID, FIRST_NAME, MIDDLE_NAMES, SURNAME, NUMBER_OF_SALES, COMMISSION) " +
            "values (?, ?, ?, ?, ?, ?)"));
        assertThat(argCaptor.getAllValues().get(0), arrayContaining(
            salespersons[0].salespersonId(),
            salespersons[0].firstName(),
            salespersons[0].middleNames().orElse(null),
            salespersons[0].surname(),
            salespersons[0].numberOfSales(),
            salespersons[0].commission().orElse(null)
        ));
        assertThat(argCaptor.getAllValues().get(1), arrayContaining(
            salespersons[1].salespersonId(),
            salespersons[1].firstName(),
            salespersons[1].middleNames().orElse(null),
            salespersons[1].surname(),
            salespersons[1].numberOfSales(),
            salespersons[1].commission().orElse(null)
        ));
    }


    @Test
    void insertMultipleInMultipleStatementsAsync() {
        Database database = Database.newBuilder()
            .defaultSqlExecutor(sqlExecutor)
            .dialect(new AnsiDialect())
            .build();
        SalespersonRow[] salespersons = ArrayUtils.toArray(
            IntegrationTest.aRandomSalesperson(),
            IntegrationTest.aRandomSalesperson());
        when(transaction.updateAsync(any(), any())).thenReturn(completedFuture(1));

        int rowsUpdated = database.insertAsync(transaction, salespersons).join();

        assertThat(rowsUpdated, equalTo(2));
        verify(transaction, times(2)).updateAsync(sqlCaptor.capture(), argCaptor.capture());
        verifyNoMoreInteractions(transaction);
        assertThat(sqlCaptor.getValue(), is("insert into SIESTA.SALESPERSON " +
            "(SALESPERSON_ID, FIRST_NAME, MIDDLE_NAMES, SURNAME, NUMBER_OF_SALES, COMMISSION) " +
            "values (?, ?, ?, ?, ?, ?)"));
        assertThat(argCaptor.getAllValues().get(0), arrayContaining(
            salespersons[0].salespersonId(),
            salespersons[0].firstName(),
            salespersons[0].middleNames().orElse(null),
            salespersons[0].surname(),
            salespersons[0].numberOfSales(),
            salespersons[0].commission().orElse(null)
        ));
        assertThat(argCaptor.getAllValues().get(1), arrayContaining(
            salespersons[1].salespersonId(),
            salespersons[1].firstName(),
            salespersons[1].middleNames().orElse(null),
            salespersons[1].surname(),
            salespersons[1].numberOfSales(),
            salespersons[1].commission().orElse(null)
        ));
    }

    @Test
    void update() {
        Database database = Database.newBuilder()
            .defaultSqlExecutor(sqlExecutor)
            .build();
        SalespersonRow salesperson = IntegrationTest.aRandomSalesperson();

        database.updateRow(salesperson);

        verify(sqlExecutor).update(sqlCaptor.capture(), argCaptor.capture());
        assertThat(sqlCaptor.getValue(), is("update SIESTA.SALESPERSON " +
            "set FIRST_NAME = ?, MIDDLE_NAMES = ?, SURNAME = ?, NUMBER_OF_SALES = ?, COMMISSION = ? " +
            "where SIESTA.SALESPERSON.SALESPERSON_ID = ?"));
        assertThat(argCaptor.getValue(), arrayContaining(
            salesperson.firstName(),
            salesperson.middleNames().orElse(null),
            salesperson.surname(),
            salesperson.numberOfSales(),
            salesperson.commission().orElse(null),
            salesperson.salespersonId()
        ));
    }

    @Test
    void updateSqlExecutor() {
        Database database = Database.newBuilder().build();
        SalespersonRow salesperson = IntegrationTest.aRandomSalesperson();

        database.updateRow(sqlExecutor, salesperson);

        verify(sqlExecutor).update(sqlCaptor.capture(), argCaptor.capture());
        assertThat(sqlCaptor.getValue(), is("update SIESTA.SALESPERSON " +
            "set FIRST_NAME = ?, MIDDLE_NAMES = ?, SURNAME = ?, NUMBER_OF_SALES = ?, COMMISSION = ? " +
            "where SIESTA.SALESPERSON.SALESPERSON_ID = ?"));
        assertThat(argCaptor.getValue(), arrayContaining(
            salesperson.firstName(),
            salesperson.middleNames().orElse(null),
            salesperson.surname(),
            salesperson.numberOfSales(),
            salesperson.commission().orElse(null),
            salesperson.salespersonId()
        ));
    }

    @Test
    void updateTransaction() {
        Database database = Database.newBuilder().build();
        SalespersonRow salesperson = IntegrationTest.aRandomSalesperson();

        database.updateRow(transaction, salesperson);

        verify(transaction).update(sqlCaptor.capture(), argCaptor.capture());
        assertThat(sqlCaptor.getValue(), is("update SIESTA.SALESPERSON " +
            "set FIRST_NAME = ?, MIDDLE_NAMES = ?, SURNAME = ?, NUMBER_OF_SALES = ?, COMMISSION = ? " +
            "where SIESTA.SALESPERSON.SALESPERSON_ID = ?"));
        assertThat(argCaptor.getValue(), arrayContaining(
            salesperson.firstName(),
            salesperson.middleNames().orElse(null),
            salesperson.surname(),
            salesperson.numberOfSales(),
            salesperson.commission().orElse(null),
            salesperson.salespersonId()
        ));
    }

    @Test
    void updateTransactionAsync() {
        Database database = Database.newBuilder().build();
        SalespersonRow salesperson = IntegrationTest.aRandomSalesperson();
        when(transaction.updateAsync(any(), any())).thenReturn(completedFuture(1));

        int rowsUpdated = database.updateRowAsync(transaction, salesperson).join();

        assertThat(rowsUpdated, equalTo(1));
        verify(transaction).updateAsync(sqlCaptor.capture(), argCaptor.capture());
        assertThat(sqlCaptor.getValue(), is("update SIESTA.SALESPERSON " +
            "set FIRST_NAME = ?, MIDDLE_NAMES = ?, SURNAME = ?, NUMBER_OF_SALES = ?, COMMISSION = ? " +
            "where SIESTA.SALESPERSON.SALESPERSON_ID = ?"));
        assertThat(argCaptor.getValue(), arrayContaining(
            salesperson.firstName(),
            salesperson.middleNames().orElse(null),
            salesperson.surname(),
            salesperson.numberOfSales(),
            salesperson.commission().orElse(null),
            salesperson.salespersonId()
        ));
    }

    @Test
    void updateAlias() {
        Database database = Database.newBuilder().build();

        String result = database.update(database.table(SalespersonRow.class).as("sp"))
            .set(SalespersonRow::numberOfSales).to(SalespersonRow::numberOfSales).plus(literal(1))
            .where(SalespersonRow::salespersonId).isEqualTo(literal(2L))
            .sql();

        assertThat(result, is("update SIESTA.SALESPERSON sp " +
            "set NUMBER_OF_SALES = sp.NUMBER_OF_SALES + 1 " +
            "where sp.SALESPERSON_ID = 2"));
    }

    @Test
    void updateClass() {
        Database database = Database.newBuilder().build();

        String result = database.update(SalespersonRow.class)
            .set(SalespersonRow::numberOfSales).to(SalespersonRow::numberOfSales).plus(literal(1))
            .where(SalespersonRow::salespersonId).isEqualTo(literal(2L))
            .sql();

        assertThat(result, is("update SIESTA.SALESPERSON " +
            "set NUMBER_OF_SALES = SIESTA.SALESPERSON.NUMBER_OF_SALES + 1 " +
            "where SIESTA.SALESPERSON.SALESPERSON_ID = 2"));
    }

    @Test
    void updateClassAndAlias() {
        Database database = Database.newBuilder().build();

        String result = database.update(SalespersonRow.class, "sp")
            .set(SalespersonRow::numberOfSales).to(SalespersonRow::numberOfSales).plus(literal(1))
            .where(SalespersonRow::salespersonId).isEqualTo(literal(2L))
            .sql();

        assertThat(result, is("update SIESTA.SALESPERSON sp " +
            "set NUMBER_OF_SALES = sp.NUMBER_OF_SALES + 1 " +
            "where sp.SALESPERSON_ID = 2"));
    }

    @Test
    void delete() {
        Database database = Database.newBuilder()
            .defaultSqlExecutor(sqlExecutor)
            .build();
        SalespersonRow salesperson = IntegrationTest.aRandomSalesperson();

        database.delete(salesperson);

        verify(sqlExecutor).update(sqlCaptor.capture(), argCaptor.capture());
        assertThat(sqlCaptor.getValue(), is("delete from SIESTA.SALESPERSON " +
            "where SIESTA.SALESPERSON.SALESPERSON_ID = ?"));
        assertThat(argCaptor.getValue(), arrayContaining(salesperson.salespersonId()));
    }

    @Test
    void deleteSqlExecutor() {
        Database database = Database.newBuilder().build();
        SalespersonRow salesperson = IntegrationTest.aRandomSalesperson();

        database.delete(sqlExecutor, salesperson);

        verify(sqlExecutor).update(sqlCaptor.capture(), argCaptor.capture());
        assertThat(sqlCaptor.getValue(), is("delete from SIESTA.SALESPERSON " +
            "where SIESTA.SALESPERSON.SALESPERSON_ID = ?"));
        assertThat(argCaptor.getValue(), arrayContaining(salesperson.salespersonId()));
    }

    @Test
    void deleteTransaction() {
        Database database = Database.newBuilder().build();
        SalespersonRow salesperson = IntegrationTest.aRandomSalesperson();

        database.delete(transaction, salesperson);

        verify(transaction).update(sqlCaptor.capture(), argCaptor.capture());
        assertThat(sqlCaptor.getValue(), is("delete from SIESTA.SALESPERSON " +
            "where SIESTA.SALESPERSON.SALESPERSON_ID = ?"));
        assertThat(argCaptor.getValue(), arrayContaining(salesperson.salespersonId()));
    }

    @Test
    void deleteTransactionAsync() {
        Database database = Database.newBuilder().build();
        SalespersonRow salesperson = IntegrationTest.aRandomSalesperson();
        when(transaction.updateAsync(any(), any())).thenReturn(completedFuture(1));

        int rowsUpdated = database.deleteRowAsync(transaction, salesperson).join();

        assertThat(rowsUpdated, equalTo(1));
        verify(transaction).updateAsync(sqlCaptor.capture(), argCaptor.capture());
        assertThat(sqlCaptor.getValue(), is("delete from SIESTA.SALESPERSON " +
            "where SIESTA.SALESPERSON.SALESPERSON_ID = ?"));
        assertThat(argCaptor.getValue(), arrayContaining(salesperson.salespersonId()));
    }

    @Test
    void deleteAlias() {
        Database database = Database.newBuilder().build();

        ExpectingWhere result = database.delete(database.table(SalespersonRow.class).as("sp"));

        assertThat(result.sql(), is("delete from SIESTA.SALESPERSON sp"));
    }

    @Test
    void deleteClass() {
        Database database = Database.newBuilder().build();

        InWhereExpectingAnd result = database.delete(SalespersonRow.class)
            .where(SalespersonRow::salespersonId).isEqualTo(literal(2L));

        assertThat(result.sql(), is("delete from SIESTA.SALESPERSON where SIESTA.SALESPERSON.SALESPERSON_ID = 2"));
    }

    @Test
    void deleteClassAndAlias() {
        Database database = Database.newBuilder().build();

        ExpectingWhere result = database.delete(SalespersonRow.class, "sp");

        assertThat(result.sql(), is("delete from SIESTA.SALESPERSON sp"));
    }

    @Test
    void selectTuple() {
        Database database = Database.newBuilder().build();
        Alias<SalespersonRow> sp = database.table(SalespersonRow.class).as("sp");

        String result = database.from(sp)
            .select(SalespersonRow::firstName)
            .where(tuple(SalespersonRow::firstName)
                .comma("sp", SalespersonRow::numberOfSales)
                .comma(sp, SalespersonRow::salespersonId))
            .isIn(database.from(SalespersonRow.class, "i")
                .select(SalespersonRow::middleNames)
                .comma(SalespersonRow::numberOfSales)
                .comma(SalespersonRow::salespersonId))
            .sql();

        assertThat(result, is("select sp.FIRST_NAME as sp_FIRST_NAME from SIESTA.SALESPERSON sp " +
            "where (sp.FIRST_NAME, sp.NUMBER_OF_SALES, sp.SALESPERSON_ID) " +
            "in (select i.MIDDLE_NAMES as i_MIDDLE_NAMES, i.NUMBER_OF_SALES as i_NUMBER_OF_SALES, i.SALESPERSON_ID as i_SALESPERSON_ID " +
            "from SIESTA.SALESPERSON i)"));
    }

    @Test
    void selectValueTuple() {
        Database database = Database.newBuilder().build();
        Alias<SalespersonRow> sp = database.table(SalespersonRow.class).as("sp");

        Select<String> select = database.from(sp)
            .select(SalespersonRow::firstName)
            .where(tuple(SalespersonRow::firstName)
                .comma("sp", SalespersonRow::numberOfSales)
                .comma(sp, SalespersonRow::salespersonId))
            .isIn(tuple(literal("Bob")).comma(10).comma(123L));

        assertThat(select.sql(), is("select sp.FIRST_NAME as sp_FIRST_NAME from SIESTA.SALESPERSON sp " +
            "where (sp.FIRST_NAME, sp.NUMBER_OF_SALES, sp.SALESPERSON_ID) " +
            "in (('Bob', ?, ?))"));
        assertThat(select.args(new Scope(database)), StreamMatchers.contains(10, 123L));
    }

    @Test
    void deleteValueTuple() {
        Database database = Database.newBuilder().build();
        Alias<SalespersonRow> sp = database.table(SalespersonRow.class).as("sp");

        ExecutableStatementClause delete = database.delete(sp)
            .where(tuple(SalespersonRow::firstName)
                .comma("sp", SalespersonRow::numberOfSales)
                .comma(sp, SalespersonRow::salespersonId))
            .isIn(tuple(literal("Bob")).comma(10).comma(123L));

        assertThat(delete.sql(), is("delete from SIESTA.SALESPERSON sp " +
            "where (sp.FIRST_NAME, sp.NUMBER_OF_SALES, sp.SALESPERSON_ID) " +
            "in (('Bob', ?, ?))"));
        assertThat(delete.args(), StreamMatchers.contains(10, 123L));
    }

    @ParameterizedTest
    @MethodSource("argsForThrowsWhenReferenceNotInScope")
    void throwsWhenReferenceNotInScope(Function<Database,Select<?>> select, String expectedMessage) {
        Database database = Database.newBuilder().build();

        calling(() -> System.out.println(select.apply(database).sql()))
            .shouldThrow(InvalidQueryException.class)
            .withMessage(is(expectedMessage));
    }

    public static Function<Database,Select<?>> select(Function<Database,Select<?>> it) {
        return it;
    }

    public static Stream<Arguments> argsForThrowsWhenReferenceNotInScope() {
        return Stream.of(
            Arguments.of(
                select(d -> d.from(SalespersonRow.class, "s").orderBy(WidgetRow::description)),
                "There is no alias for com.cadenzauk.siesta.model.WidgetRow in scope."),
            Arguments.of(
                select(d ->
                    d.from(
                        d.from(SalespersonRow.class, "s")
                            .select(SalespersonRow::salespersonId)
                            .comma(rowNumber().partitionBy(SalespersonRow::salespersonId), "rown"),
                        "o"
                    ).where(Label.of("rown", Long.class)).isEqualTo(1L)),
                "The column with the label of 'rown' is of type 'java.lang.Integer' and not 'class java.lang.Long'."),
            Arguments.of(
                select(d -> d.from(SalespersonRow.class, "s").orderBy("s", WidgetRow::description)),
                "Alias s is an alias for com.cadenzauk.siesta.model.SalespersonRow and not class com.cadenzauk.siesta.model.WidgetRow.")
        );
    }

        @NotNull
    private Database database() {
        return Database.newBuilder()
            .defaultSchema("CUSTOMERS")
            .namingStrategy(new UppercaseUnderscores())
            .build();
    }
}
