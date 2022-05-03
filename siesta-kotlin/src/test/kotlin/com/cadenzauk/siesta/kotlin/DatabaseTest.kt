/*
 * Copyright (c) 2019 Cadenza United Kingdom Limited
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

package com.cadenzauk.siesta.kotlin

import co.unruly.matchers.OptionalMatchers.contains
import com.cadenzauk.siesta.Database
import com.cadenzauk.siesta.IntegrationTest
import com.cadenzauk.siesta.jdbc.JdbcSqlExecutor
import com.cadenzauk.siesta.json.Json
import com.cadenzauk.siesta.model.TestDatabase
import com.cadenzauk.siesta.type.DbTypeId
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.Table

data class Price(
    val amount: BigDecimal,
    val currency: String
)

data class Quantity(
    val amount: Int,
    val unit: String
)

data class Product(
    val code: String,
    val description: String
)

data class Order(
    val orderDate: LocalDate,
    val price: Price,
    val quantity: Quantity,
    val product: Product
)

@Table(name = "JSON_DATA", schema = "SIESTA")
data class KJsonRow(
    val jsonId: Long,
    val data: Order,
)

class DatabaseTest : IntegrationTest() {
    @Test
    fun canSelect() {
        val database = Database.newBuilder().build()

        val sql = database.from(KWidgetRow::class, "w")
            .select(KWidgetRow::description)
            .where("w", KWidgetRow::manufacturerId).isEqualTo(10)
            .sql()

        assertThat(
            sql,
            equalTo("select w.DESCRIPTION as w_DESCRIPTION from SIESTA.WIDGET w where w.MANUFACTURER_ID = ?")
        )
    }

    @Test
    fun canSelectFromDatabase() {
        val database = TestDatabase.testDatabase(dataSource, dialect)
        val aWidget = KWidgetRow(
            widgetId = newId(),
            manufacturerId = newId(),
            name = "Dodacky",
            description = "Thingamibob"
        )
        database.insert(aWidget)

        val theSame = database.from(KWidgetRow::class)
            .where(KWidgetRow::widgetId).isEqualTo(aWidget.widgetId)
            .optional()

        assertThat(theSame, contains(aWidget))
    }

    @Test
    fun `can insert and restore a json object`() {
        val anOrder = Order(
            orderDate = LocalDate.now(),
            price = Price(
                amount = BigDecimal("123.45"),
                currency = "USD"
            ),
            quantity = Quantity(
                amount = 1000,
                unit = "DOZEN"
            ),
            product = Product(
                code = "SPROC102",
                description = "Spacely's Special Sprocket"
            )
        )
        val objectMapper = ObjectMapper()
            .registerModule(Jdk8Module())
            .registerModule(JavaTimeModule())
            .registerModule(KotlinModule.Builder().build())
        val database = Database.newBuilder()
            .defaultSqlExecutor(JdbcSqlExecutor.of(dataSource, 0))
            .adapter(
                Order::class.java,
                DbTypeId.JSON,
                { Json(objectMapper.writeValueAsString(it)) },
                { objectMapper.readValue(it.data(), Order::class.java) })
            .build()
        val aRow = KJsonRow(newId(), anOrder)
        database.insert(aRow)

        val result = database.from(KJsonRow::class, "j").where(KJsonRow::jsonId).isEqualTo(aRow.jsonId).single()

        assertThat(result.data, equalTo(anOrder))
    }
}
