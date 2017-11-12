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

import com.cadenzauk.core.MockitoTest;
import com.cadenzauk.siesta.dialect.AnsiDialect;
import com.cadenzauk.siesta.grammar.expression.Aggregates;
import com.cadenzauk.siesta.model.MoneyAmount;
import com.cadenzauk.siesta.model.PartRow;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Table;
import java.util.Optional;

import static com.cadenzauk.siesta.grammar.expression.TypedExpression.column;
import static com.cadenzauk.siesta.model.TestDatabase.testDatabase;
import static com.cadenzauk.siesta.model.TestDatabase.testDatabaseBuilder;
import static org.apache.commons.lang3.ArrayUtils.toArray;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;

class EmbeddedTest extends MockitoTest {
    @Mock
    private SqlExecutor sqlExecutor;

    @Captor
    private ArgumentCaptor<String> sql;

    @Captor
    private ArgumentCaptor<Object[]> args;

    @Test
    void embeddedInWhereClause() {
        Database database = testDatabaseBuilder()
            .defaultSchema("SIESTA")
            .table(PartRow.class, t -> t
                .embedded(MoneyAmount.class, PartRow::purchasePrice, pp -> pp
                    .columnName("PURCH_PRICE")
                    .column(MoneyAmount::amount, "PURCHASE_PRICE_AMT"))
                .embedded(MoneyAmount.class, PartRow::retailPrice, rp -> rp
                    .column(MoneyAmount::amount, "RETAIL_PRICE_AMT")))
            .build();

        database.from(PartRow.class)
            .select(Aggregates.countDistinct(column(PartRow::purchasePrice).dot(MoneyAmount::amount)))
            .where(PartRow::purchasePrice).dot(MoneyAmount::currency).isEqualTo("USD")
            .or(PartRow::retailPrice).dot(MoneyAmount::currency).isEqualTo("NZD")
            .optional(sqlExecutor);

        Mockito.verify(sqlExecutor).query(sql.capture(), args.capture(), any());
        assertThat(sql.getValue(), is("select count(distinct PART.PURCHASE_PRICE_AMT) as count_PART_PURCHASE_PRICE_AMT " +
            "from SIESTA.PART PART where PART.PURCH_PRICE_CCY = ? or PART.RETAIL_PRICE_CCY = ?"));
        assertThat(args.getValue(), is(toArray("USD", "NZD")));
    }

    @Test
    void embeddedInProjection() {
        Database database = testDatabase(new AnsiDialect());

        database.from(PartRow.class)
            .select(PartRow::purchasePrice, "pp")
            .comma(PartRow::retailPrice, "rp")
            .comma(column(PartRow::purchasePrice).dot(MoneyAmount::currency), "pc")
            .comma(column(PartRow::retailPrice).dot(MoneyAmount::currency), "rc")
            .where(PartRow::partId).isEqualTo(4L)
            .optional(sqlExecutor);

        Mockito.verify(sqlExecutor).query(sql.capture(), args.capture(), any());
        assertThat(sql.getValue(), is(
            "select PART.PURCHASE_PRICE_AMOUNT as pp_PURCHASE_PRICE_AMOUNT, " +
                "PART.PURCHASE_PRICE_CCY as pp_PURCHASE_PRICE_CCY, " +
                "PART.RETAIL_PRICE_AMOUNT as rp_RETAIL_PRICE_AMOUNT, " +
                "PART.RETAIL_PRICE_CCY as rp_RETAIL_PRICE_CCY, " +
                "PART.PURCHASE_PRICE_CCY as pc, " +
                "PART.RETAIL_PRICE_CCY as rc " +
                "from SIESTA.PART PART " +
                "where PART.PART_ID = ?"));
        assertThat(args.getValue(), is(toArray(4L)));
    }

    @Test
    void embeddedOverride() {
        Database database = Database.newBuilder().dialect(new AnsiDialect()).build();

        database.from(CustomerWithOverride.class)
            .select(CustomerWithOverride::address)
            .comma(column(CustomerWithOverride::address).dot(Address::line1), "l1")
            .comma(column(CustomerWithOverride::address).dot(Address::line2), "l2")
            .list(sqlExecutor);

        Mockito.verify(sqlExecutor).query(sql.capture(), args.capture(), any());
        assertThat(sql.getValue(), is("select " +
            "CUSTOMER.ADDRESS_LINE1 as CUSTOMER_ADDRESS_LINE1, " +
            "CUSTOMER.ADDR_LINE_2 as CUSTOMER_ADDR_LINE_2, " +
            "CUSTOMER.ADDRESS_LINE1 as l1, " +
            "CUSTOMER.ADDR_LINE_2 as l2 " +
            "from CUSTOMER CUSTOMER"));
    }

    @Embeddable
    private static class Address {
        private final String line1;
        private final Optional<String> line2;

        public Address(String line1, Optional<String> line2) {
            this.line1 = line1;
            this.line2 = line2;
        }

        public String line1() {
            return line1;
        }

        public Optional<String> line2() {
            return line2;
        }
    }

    @Table(name = "CUSTOMER")
    private static class CustomerWithOverride {
        @AttributeOverrides({
            @AttributeOverride(name = "line2", column = @Column(name = "ADDR_LINE_2"))
        })
        private final Address address;

        private CustomerWithOverride(Address address) {
            this.address = address;
        }

        public Address address() {
            return address;
        }
    }
}
