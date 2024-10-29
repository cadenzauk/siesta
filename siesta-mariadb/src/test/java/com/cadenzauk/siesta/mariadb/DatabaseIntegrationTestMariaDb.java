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

package com.cadenzauk.siesta.mariadb;

import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.DatabaseIntegrationTest;
import com.cadenzauk.siesta.Order;
import com.cadenzauk.siesta.grammar.select.Ordering;
import com.cadenzauk.siesta.model.SalespersonRow;
import com.cadenzauk.siesta.model.TestDatabase;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.stream.Collectors;

import static com.cadenzauk.siesta.grammar.expression.Aggregates.count;

@ContextConfiguration(classes = MariaDbConfig.class)
class DatabaseIntegrationTestMariaDb extends DatabaseIntegrationTest {
    @Test
    void pagingDemo() {
        Database database = TestDatabase.testDatabase(dataSource);
        Tuple2<Long, Long> firstAndLast = insertSalespeople(database, 8);
        System.out.println("The sales people have IDs from " + firstAndLast.item1()  + " to " + firstAndLast.item2());

        int pageSize = 3;
        List<Ordering> ordering = ImmutableList.of(Ordering.of("SURNAME", Order.ASC), Ordering.of("FIRST_NAME", Order.ASC));
        int count = database.from(SalespersonRow.class)
            .select(count())
            .where(SalespersonRow::salespersonId).isBetween(firstAndLast.item1()).and(firstAndLast.item2())
            .single();
        int numberOfPages = (count + pageSize - 1) / pageSize;
        for (int pageNo = 1; pageNo <= numberOfPages; pageNo++) {
            List<Long> pageOfResults = database.from(SalespersonRow.class, "s")
                .select(SalespersonRow::salespersonId)
                .where(SalespersonRow::salespersonId).isBetween(firstAndLast.item1()).and(firstAndLast.item2())
                .page(pageNo, pageSize, ordering)
                .list();
            System.out.println(pageOfResults.stream().map(Object::toString).collect(Collectors.joining(", ", "[", "]")));
        }
    }
}
