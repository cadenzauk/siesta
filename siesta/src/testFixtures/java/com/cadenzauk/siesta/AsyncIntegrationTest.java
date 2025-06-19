/*
 * Copyright (c) 2025 Cadenza United Kingdom Limited
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

import com.cadenzauk.siesta.jdbc.JdbcSqlExecutor;
import com.cadenzauk.siesta.model.SalespersonRow;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

import static com.cadenzauk.core.concurrent.CompletableFutureUtil.allAsList;
import static com.cadenzauk.siesta.grammar.expression.TypedExpression.literal;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public abstract class AsyncIntegrationTest extends IntegrationTest {
    @Test
    public void testInsertAsync() {
        Database database = testDatabase(dataSource);
        try (Transaction tx = database.beginTransaction()) {
            List<Integer> rowCounts = allAsList(
                IntStream.range(0, 10).mapToObj(i -> database.insertAsync(tx, aRandomSalesperson()))
            ).join();

            int insertedRows = rowCounts.stream().mapToInt(Integer::intValue).sum();

            assertThat(insertedRows, is(10));
        }
    }

    @Test
    public void testInsertAsyncAndSelect() {
        Database database = testDatabaseBuilder(dialect)
            .defaultSqlExecutor(JdbcSqlExecutor.of(dataSource, 0, ForkJoinPool.commonPool(), Executors.newSingleThreadExecutor()))
            .build();
        List<SalespersonRow> salespersonRows = IntStream.range(0, 10).mapToObj(i -> aRandomSalesperson()).collect(toList());
        try (Transaction tx = database.beginTransaction()) {
            List<SalespersonRow> insertedRows = allAsList(
                salespersonRows.stream()
                    .map(row ->
                        database.insertAsync(tx, row)
                            .thenCompose(x ->
                                database.from(SalespersonRow.class, "s").where(SalespersonRow::salespersonId).isEqualTo(row.salespersonId()).optionalAsync(tx)
                            )
                    )
            ).thenApply(x ->
                x.stream().flatMap(Optional::stream).collect(toList())
            ).join();

            assertThat(insertedRows, containsInAnyOrder(salespersonRows.toArray()));
        }
    }

    @Test
    public void testUpdate() {
        Database database = testDatabaseBuilder(dialect)
            .defaultSqlExecutor(JdbcSqlExecutor.of(dataSource, 0, ForkJoinPool.commonPool(), Executors.newSingleThreadExecutor()))
            .build();
        SalespersonRow[] salespersonRows = IntStream.range(0, 10).mapToObj(i -> aRandomSalesperson()).toArray(SalespersonRow[]::new);
        try (Transaction tx = database.beginTransaction()) {
            List<Integer> updatedValues = database.insertAsync(tx, salespersonRows)
                .thenCompose(i ->
                    allAsList(
                        Arrays.stream(salespersonRows)
                            .map(r ->
                                database.update(SalespersonRow.class)
                                    .set(SalespersonRow::numberOfSales).to(literal(10))
                                    .where(SalespersonRow::salespersonId).isEqualTo(r.salespersonId())
                                    .executeAsync(tx)
                                    .thenCompose(n ->
                                        database.from(SalespersonRow.class, "s")
                                            .select(SalespersonRow::numberOfSales)
                                            .where(SalespersonRow::salespersonId).isEqualTo(r.salespersonId())
                                            .optionalAsync(tx)
                                    )
                            )
                    )
                ).thenApply(x ->
                    x.stream().flatMap(Optional::stream).collect(toList())
                ).join();

            assertThat(updatedValues, contains(Collections.nCopies(10, is(10))));
        }
    }

    @Test
    public void testUpdateRowAsync() {
        Database database = testDatabaseBuilder(dialect)
            .defaultSqlExecutor(JdbcSqlExecutor.of(dataSource, 0, ForkJoinPool.commonPool(), Executors.newSingleThreadExecutor()))
            .build();
        SalespersonRow[] salespersonRows = IntStream.range(0, 10).mapToObj(i -> aRandomSalesperson()).toArray(SalespersonRow[]::new);
        try (Transaction tx = database.beginTransaction()) {
            List<Integer> updatedValues = database.insertAsync(tx, salespersonRows)
                .thenCompose(i ->
                    allAsList(
                        Arrays.stream(salespersonRows)
                            .map(r ->
                                database.updateRowAsync(tx, SalespersonRow.newBuilder(r).numberOfSales(20).build())
                                    .thenCompose(n ->
                                        database.from(SalespersonRow.class, "s")
                                            .select(SalespersonRow::numberOfSales)
                                            .where(SalespersonRow::salespersonId).isEqualTo(r.salespersonId())
                                            .optionalAsync(tx)
                                    )
                            )
                    )
                ).thenApply(x ->
                    x.stream().flatMap(Optional::stream).collect(toList())
                ).join();

            assertThat(updatedValues, contains(Collections.nCopies(10, is(20))));
        }
    }

    @Test
    public void testDeleteAsync() {
        Database database = testDatabaseBuilder(dialect)
            .defaultSqlExecutor(JdbcSqlExecutor.of(dataSource, 0, ForkJoinPool.commonPool(), Executors.newSingleThreadExecutor()))
            .build();
        SalespersonRow[] salespersonRows = IntStream.range(0, 10).mapToObj(i -> aRandomSalesperson(b -> b.numberOfSales(i))).toArray(SalespersonRow[]::new);
        try (Transaction tx = database.beginTransaction()) {
            List<Integer> updatedValues = database.insertAsync(tx, salespersonRows)
                .thenCompose(i ->
                    allAsList(
                        Arrays.stream(salespersonRows)
                            .map(r ->
                                database.delete(SalespersonRow.class)
                                    .where(SalespersonRow::salespersonId).isEqualTo(r.salespersonId())
                                    .and(SalespersonRow::numberOfSales).isBetween(3).and(6)
                                    .executeAsync(tx)
                                    .thenCompose(n ->
                                        database.from(SalespersonRow.class, "s")
                                            .select(SalespersonRow::numberOfSales)
                                            .where(SalespersonRow::salespersonId).isEqualTo(r.salespersonId())
                                            .optionalAsync(tx)
                                    )
                            )
                    )
                ).thenApply(x ->
                    x.stream().flatMap(Optional::stream).collect(toList())
                ).join();

            assertThat(updatedValues, hasSize(6));
        }
    }

    @Test
    public void testDeleteRowAsync() {
        Database database = testDatabaseBuilder(dialect)
            .defaultSqlExecutor(JdbcSqlExecutor.of(dataSource, 0, ForkJoinPool.commonPool(), Executors.newSingleThreadExecutor()))
            .build();
        SalespersonRow[] salespersonRows = IntStream.range(0, 10).mapToObj(i -> aRandomSalesperson(b -> b.numberOfSales(i))).toArray(SalespersonRow[]::new);
        try (Transaction tx = database.beginTransaction()) {
            List<Integer> updatedValues = database.insertAsync(tx, salespersonRows)
                .thenCompose(i ->
                    allAsList(
                        Arrays.stream(salespersonRows)
                            .map(r ->
                                database.deleteRowAsync(tx, r)
                                    .thenCompose(n ->
                                        database.from(SalespersonRow.class, "s")
                                            .select(SalespersonRow::numberOfSales)
                                            .where(SalespersonRow::salespersonId).isEqualTo(r.salespersonId())
                                            .optionalAsync(tx)
                                    )
                            )
                    )
                ).thenApply(x ->
                    x.stream().flatMap(Optional::stream).collect(toList())
                ).join();

            assertThat(updatedValues, hasSize(0));
        }
    }
}
