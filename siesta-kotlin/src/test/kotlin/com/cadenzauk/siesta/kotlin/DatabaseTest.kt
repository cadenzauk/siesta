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
import com.cadenzauk.siesta.grammar.expression.Aggregates.sum
import com.cadenzauk.siesta.grammar.expression.Case
import com.cadenzauk.siesta.grammar.expression.CoalesceFunction.coalesce
import com.cadenzauk.siesta.grammar.expression.TypedExpression.column
import com.cadenzauk.siesta.grammar.expression.TypedExpression.literal
import com.cadenzauk.siesta.grammar.expression.olap.Olap
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

    data class DataRow(
        val manufacturerId: Long,
        val name: String,
        val prevName: String,
        val description: String
    )

    data class ResultRow(
        val manufacturerId: Long,
        val complete: Int,
        val widgets: Int,
        val thingamibobs: Int,
    )

    @Test
    fun cteToView() {
        val database = TestDatabase.testDatabase(dataSource, dialect)
        val manufacturerId1 = newId()
        val manufacturerId2 = newId()
        val manufacturerId3 = newId()
        database.insert(
            KWidgetRow(
                widgetId = newId(),
                manufacturerId = manufacturerId1,
                name = "Widget",
                description = null
            ),
            KWidgetRow(
                widgetId = newId(),
                manufacturerId = manufacturerId1,
                name = "Gadget",
                description = "Doohickey"
            ),
            KWidgetRow(
                widgetId = newId(),
                manufacturerId = manufacturerId2,
                name = "Widget",
                description = null
            ),
            KWidgetRow(
                widgetId = newId(),
                manufacturerId = manufacturerId3,
                name = "Gadget",
                description = null
            ),
            KWidgetRow(
                widgetId = newId(),
                manufacturerId = manufacturerId3,
                name = "Doofer",
                description = "Thingamibob"
            ),
        )

        val data = database.with("data").of(
            database.from(KWidgetRow::class.java)
                .selectInto(DataRow::class.java)
                .with(KWidgetRow::manufacturerId).into(DataRow::manufacturerId)
                .with(coalesce(Olap.lag(KWidgetRow::name).partitionBy(KWidgetRow::manufacturerId).orderBy(KWidgetRow::widgetId)).orElse(literal(""))).into(DataRow::prevName)
                .with(KWidgetRow::name).into(DataRow::name)
                .with(coalesce(KWidgetRow::description).orElse("n/a")).into(DataRow::description)
        )
        val result = database.from(data, "d")
            .selectInto(ResultRow::class.java)
            .with(DataRow::manufacturerId).into(ResultRow::manufacturerId)
            .with(
                sum(Case.whenever(column(DataRow::name).isEqualTo("Gadget").and(DataRow::prevName).isEqualTo("Widget"))
                    .then(1)
                    .orElse(0)
                )
            ).into(ResultRow::complete)
            .with(
                sum(Case.whenever(column(DataRow::name).isEqualTo("Widget"))
                    .then(1)
                    .orElse(0)
                )
            ).into(ResultRow::widgets)
            .with(
                sum(Case.whenever(column(DataRow::description).isEqualTo("Thingamibob"))
                    .then(literal(1))
                    .orElse(literal(0))
                )
            ).into(ResultRow::thingamibobs)
            .groupBy(DataRow::manufacturerId)
            .orderBy(DataRow::manufacturerId)
            .list().onEach(::println)

        assertThat(
            result,
            equalTo(
                listOf(
                    ResultRow(manufacturerId1, 1, 1, 0),
                    ResultRow(manufacturerId2, 0, 1, 0),
                    ResultRow(manufacturerId3, 0, 0, 1),
                )
            )
        )
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
