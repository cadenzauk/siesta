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

import com.cadenzauk.siesta.model.SalespersonRow;
import org.junit.AssumptionViolatedException;
import org.junit.Test;

import java.util.Optional;

import static com.cadenzauk.siesta.grammar.expression.Aggregates.count;
import static com.cadenzauk.siesta.model.TestDatabase.testDatabase;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public abstract class TransactionIntegrationTest extends IntegrationTest {
    @Test
    public void whenCommittedThenInserted() {
        Database database = testDatabase(dataSource, dialect);

        SalespersonRow salesperson = aRandomSalesperson();
        try (Transaction transaction = database.beginTransaction()) {
            database.insert(transaction, salesperson);
            transaction.commit();
        }

        Integer count = database.from(SalespersonRow.class)
            .select(count())
            .where(SalespersonRow::salespersonId).isEqualTo(salesperson.salespersonId())
            .single();
        assertThat(count, is(1));
    }

    @Test
    public void whenRollingBackNothingInserted() {
        Database database = testDatabase(dataSource, dialect);

        SalespersonRow salesperson = aRandomSalesperson();
        try (Transaction transaction = database.beginTransaction()) {
            database.insert(transaction, salesperson);
            transaction.rollback();
        }

        Integer count = database.from(SalespersonRow.class)
            .select(count())
            .where(SalespersonRow::salespersonId).isEqualTo(salesperson.salespersonId())
            .single();
        assertThat(count, is(0));
    }

    @Test
    public void whenUncommittedThenNotVisible() {
        Database database = testDatabase(dataSource, dialect);

        SalespersonRow salesperson = aRandomSalesperson();
        try (Transaction transaction = database.beginTransaction()) {
            database.insert(transaction, salesperson);

            Optional<Long> result = database.from(SalespersonRow.class)
                .select(SalespersonRow::salespersonId)
                .where(SalespersonRow::salespersonId).isEqualTo(salesperson.salespersonId())
                .withIsolation(IsolationLevel.REPEATABLE_READ)
                .keepLocks(LockLevel.UPDATE)
                .optional();

            assertThat(result.isPresent(), is(false));
        }
    }

    @Test
    public void whenUncommittedThenVisibleWithUr() {
        if (!dialect.supportsIsolationLevelInQuery()) {
            throw new AssumptionViolatedException("Database doesn't supprt isolation levels in query");
        }
        Database database = testDatabase(dataSource, dialect);

        SalespersonRow salesperson = aRandomSalesperson();
        try (Transaction transaction = database.beginTransaction()) {
            database.insert(transaction, salesperson);

            Integer count = database.from(SalespersonRow.class)
                .select(count())
                .where(SalespersonRow::salespersonId).isEqualTo(salesperson.salespersonId())
                .fetchFirst(1)
                .withIsolation(IsolationLevel.UNCOMMITTED_READ)
                .single();

            assertThat(count, is(1));
        }
    }
}
