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

import com.cadenzauk.core.lang.CompositeAutoCloseable;
import com.cadenzauk.core.sql.RuntimeSqlException;
import com.cadenzauk.siesta.model.LockTestRow;
import com.cadenzauk.siesta.model.TestDatabase;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public abstract class LockingIntegrationTest extends IntegrationTest {
    private static final Logger LOG = LoggerFactory.getLogger(LockingIntegrationTest.class);

    @Test
    void updateWithTimeout1() {
        assumeTrue(dialect.supportsLockTimeout(), "Database does not support lock timeouts.");
        long id = newId();
        Database database = TestDatabase.testDatabase(dataSource);
        Synchronization thread1 = new Synchronization();
        Synchronization thread2 = new Synchronization();
        CompletableFuture<Boolean> update1 = CompletableFuture.supplyAsync(() -> performUpdate(true, database, id, thread1));
        CompletableFuture<Boolean> update2 = CompletableFuture.supplyAsync(() -> performUpdate(true, database, id, thread2));

        database.insert(new LockTestRow(id, 1, "Initial"));
        thread1.select();
        thread1.update();
        thread2.select();
        thread2.update();
        thread1.commit();

        boolean update2Succeeded = update2.join();
        boolean update1Succeeded = update1.join();
        assertThat(update1Succeeded, is(true));
        assertThat(update2Succeeded, is(false));
    }

    @Test
    void updateWithTimeout2() {
        assumeTrue(dialect.supportsLockTimeout(), "Database does not support lock timeouts.");
        Database database = TestDatabase.testDatabase(dataSource);
        long id = newId();
        Synchronization thread1 = new Synchronization();
        Synchronization thread2 = new Synchronization();
        CompletableFuture<Boolean> update1 = CompletableFuture.supplyAsync(() -> performUpdate(true, database, id, thread1));
        CompletableFuture<Boolean> update2 = CompletableFuture.supplyAsync(() -> performUpdate(true, database, id, thread2));

        database.insert(new LockTestRow(id, 1, "Initial"));
        thread1.select();
        thread2.select();
        thread1.update();
        thread2.update();
        thread1.commit();

        boolean update2Succeeded = update2.join();
        boolean update1Succeeded = update1.join();
        assertThat(update1Succeeded, is(true));
        assertThat(update2Succeeded, is(false));
    }

    @Test
    void updateWithTimeout3() {
        assumeTrue(dialect.supportsLockTimeout(), "Database does not support lock timeouts.");
        Database database = TestDatabase.testDatabase(dataSource);
        long id = newId();
        Synchronization thread1 = new Synchronization();
        Synchronization thread2 = new Synchronization();
        CompletableFuture<Boolean> update1 = CompletableFuture.supplyAsync(() -> performUpdate(true, database, id, thread1));
        CompletableFuture<Boolean> update2 = CompletableFuture.supplyAsync(() -> performUpdate(true, database, id, thread2));

        database.insert(new LockTestRow(id, 1, "Initial"));
        thread1.select();
        thread1.update();
        thread2.select();
        thread1.commit();
        thread2.update();

        boolean update2Succeeded = update2.join();
        boolean update1Succeeded = update1.join();
        assertThat(update1Succeeded, is(true));
        assertThat(update2Succeeded, is(false));
    }

    @Test
    void updateWithoutTimeout() throws InterruptedException {
        long id = newId();
        Database database = TestDatabase.testDatabase(dataSource);
        Synchronization thread1 = new Synchronization();
        Synchronization thread2 = new Synchronization();

        database.insert(new LockTestRow(id, 1, "Initial"));
        CompletableFuture<Boolean> update1 = CompletableFuture.supplyAsync(() -> performUpdate(false, database, id, thread1));
        CompletableFuture<Boolean> update2 = CompletableFuture.supplyAsync(() -> performUpdate(false, database, id, thread2));

        thread1.select();
        thread2.select();
        thread1.update();
        thread2.updateAsync();
        TimeUnit.MILLISECONDS.sleep(100);
        thread1.commit();

        boolean update1Succeeded = update1.join();
        boolean update2Succeeded = update2.join();

        assertThat(update1Succeeded, is(true));
        assertThat(update2Succeeded, is(false));
    }

    @Test
    void insertsWithoutTimeout() throws InterruptedException {
        long id = newId();
        Database database = TestDatabase.testDatabase(dataSource);
        Synchronization thread1 = new Synchronization();
        Synchronization thread2 = new Synchronization();

        CompletableFuture<Boolean> update1 = CompletableFuture.supplyAsync(() -> performUpdate(false, database, id, thread1));
        CompletableFuture<Boolean> update2 = CompletableFuture.supplyAsync(() -> performUpdate(false, database, id, thread2));

        thread1.select();
        thread2.select();
        thread1.update();
        thread2.updateAsync();
        TimeUnit.MILLISECONDS.sleep(100);
        thread1.commit();

        boolean update1Succeeded = update1.join();
        boolean update2Succeeded = update2.join();

        assertThat(update1Succeeded, is(true));
        assertThat(update2Succeeded, is(false));
    }

    @Test
    void insertsWithTimeout() {
        assumeTrue(dialect.supportsLockTimeout(), "Database does not support lock timeouts.");
        long id = newId();
        Database database = TestDatabase.testDatabase(dataSource);
        Synchronization thread1 = new Synchronization();
        Synchronization thread2 = new Synchronization();

        CompletableFuture<Boolean> update1 = CompletableFuture.supplyAsync(() -> performUpdate(true, database, id, thread1));
        CompletableFuture<Boolean> update2 = CompletableFuture.supplyAsync(() -> performUpdate(true, database, id, thread2));

        thread1.select();
        thread1.update();
        thread2.select();
        thread2.update();
        thread1.commit();

        boolean update1Succeeded = update1.join();
        boolean update2Succeeded = update2.join();
        assertThat(update1Succeeded, is(true));
        assertThat(update2Succeeded, is(false));
    }

    private boolean performUpdate(boolean setLockTimeout, Database database, long id, Synchronization synchronization) {
        try (CompositeAutoCloseable closeable = new CompositeAutoCloseable()) {
            synchronization.waitToSelect();
            Transaction transaction = closeable.add(database.beginTransaction());

            Optional<Integer> currentRevision = database.from(LockTestRow.class)
                .select(LockTestRow::revision)
                .where(LockTestRow::id).isEqualTo(id)
                .optional(transaction);
            LOG.info("Current revision = {}", currentRevision);

            Optional<Integer> uncommittedRevision = database.from(LockTestRow.class)
                .select(LockTestRow::revision)
                .where(LockTestRow::id).isEqualTo(id)
                .withIsolation(IsolationLevel.UNCOMMITTED_READ)
                .optional(transaction);
            LOG.info("Uncommitted revision = {}", uncommittedRevision);
            if (uncommittedRevision.orElse(0) > currentRevision.orElse(0)) {
                LOG.info("Updated by another transaction - could bail at this point", currentRevision, uncommittedRevision);
            }

            synchronization.waitToUpdate();
            if (setLockTimeout) {
                closeable.add(database.withLockTimeout(transaction, 0, TimeUnit.SECONDS));
            }

            int updateCount = currentRevision.map(curr ->
                database.update(LockTestRow.class)
                    .set(LockTestRow::updatedBy).to(Thread.currentThread().getName())
                    .set(LockTestRow::revision).to(curr + 1)
                    .where(LockTestRow::id).isEqualTo(id)
                    .and(LockTestRow::revision).isEqualTo(curr)
                    .execute(transaction))
                .orElseGet(() ->
                    database.insert(transaction, new LockTestRow(id, 1, Thread.currentThread().getName())));
            LOG.info("Update count = {}", updateCount);
            if (updateCount == 0) {
                synchronization.finished();
                return false;
            }

            synchronization.waitToCommit();
            transaction.commit();
            LOG.info("Committed");

            synchronization.finished();
            return true;
        } catch (RuntimeSqlException e) {
            LOG.error("Update failed", e);
            synchronization.updateFailed();
            return false;
        }
    }

    private static class Synchronization {
        private final CompletableFuture<Void> beforeSelect = new CompletableFuture<>();
        private final CompletableFuture<Void> selectDone = new CompletableFuture<>();
        private final CompletableFuture<Void> beforeUpdate = new CompletableFuture<>();
        private final CompletableFuture<Void> updateDone = new CompletableFuture<>();
        private final CompletableFuture<Void> beforeCommit = new CompletableFuture<>();
        private final CompletableFuture<Void> commitDone = new CompletableFuture<>();

        private void select() {
            beforeSelect.complete(null);
            selectDone.join();
        }

        private void update() {
            beforeUpdate.complete(null);
            updateDone.join();
        }

        private void updateAsync() {
            beforeUpdate.complete(null);
        }

        private void commit() {
            beforeCommit.complete(null);
            commitDone.join();
        }

        private void waitToSelect() {
            beforeSelect.join();
        }

        private void waitToUpdate() {
            selectDone.complete(null);
            beforeUpdate.join();
        }

        private void waitToCommit() {
            updateDone.complete(null);
            LOG.info("Waiting to commit");
            beforeCommit.join();
        }

        private void finished() {
            updateDone.complete(null);
            commitDone.complete(null);
        }

        private void updateFailed() {
            updateDone.complete(null);
        }
    }

}
