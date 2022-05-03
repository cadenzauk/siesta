/*
 * Copyright (c) 2018 Cadenza United Kingdom Limited
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

package com.cadenzauk.siesta.ddl;

import com.cadenzauk.siesta.Dialect;
import com.cadenzauk.siesta.model.LockTestRow;
import com.cadenzauk.siesta.model.ManufacturerRow;
import com.cadenzauk.siesta.model.MoneyAmount;
import com.cadenzauk.siesta.model.PartRow;
import com.cadenzauk.siesta.model.PartWithTypeRow;
import com.cadenzauk.siesta.model.SalesAreaRow;
import com.cadenzauk.siesta.model.SalespersonRow;
import com.cadenzauk.siesta.model.TestRow;
import com.cadenzauk.siesta.model.WidgetRow;

import static com.cadenzauk.siesta.ddl.definition.action.Column.Constraints.foreignKey;
import static com.cadenzauk.siesta.ddl.definition.action.Column.Constraints.notNull;
import static com.cadenzauk.siesta.ddl.definition.action.Column.Constraints.primaryKey;
import static com.cadenzauk.siesta.ddl.definition.action.ColumnDataType.bigint;
import static com.cadenzauk.siesta.ddl.definition.action.ColumnDataType.binary;
import static com.cadenzauk.siesta.ddl.definition.action.ColumnDataType.character;
import static com.cadenzauk.siesta.ddl.definition.action.ColumnDataType.date;
import static com.cadenzauk.siesta.ddl.definition.action.ColumnDataType.decimal;
import static com.cadenzauk.siesta.ddl.definition.action.ColumnDataType.integer;
import static com.cadenzauk.siesta.ddl.definition.action.ColumnDataType.json;
import static com.cadenzauk.siesta.ddl.definition.action.ColumnDataType.jsonb;
import static com.cadenzauk.siesta.ddl.definition.action.ColumnDataType.smallint;
import static com.cadenzauk.siesta.ddl.definition.action.ColumnDataType.time;
import static com.cadenzauk.siesta.ddl.definition.action.ColumnDataType.timestamp;
import static com.cadenzauk.siesta.ddl.definition.action.ColumnDataType.varchar;
import static com.cadenzauk.siesta.grammar.expression.TypedExpression.column;

public class TestSchema {
    private final Dialect dialect;

    public TestSchema(Dialect dialect) {
        this.dialect = dialect;
    }

    public SchemaDefinition schemaDefinition() {
        return SchemaDefinition.newBuilder()
            .id("test schema")
            .createSequence(s -> s
                .id("create int sequence")
                .author("mark")
                .schemaName("SIESTA")
                .sequenceName("widget_seq")
                .startValue(1)
            )
            .createTable(t -> t
                .id("create widget table")
                .author("mark")
                .forClass(WidgetRow.class)
                .column(WidgetRow::widgetId, bigint(), notNull(), primaryKey())
                .column(WidgetRow::name, varchar(100), notNull())
                .column(WidgetRow::manufacturerId, bigint(), notNull())
                .column(WidgetRow::description, varchar(200))
                .column("INSERTION_TS", timestamp())
            )
            .createTable(t -> t
                .id("create part table")
                .author("mark")
                .schemaName("SIESTA")
                .forClass(PartRow.class)
                .column(PartRow::partId, bigint(), notNull(), primaryKey())
                .column(PartRow::widgetId, bigint(), notNull())
                .column(PartRow::description, varchar(200), notNull())
                .column(PartWithTypeRow::partType, varchar(100))
                .column("PURCHASE_PRICE_AMOUNT", decimal(15, 2))
                .column("PURCHASE_PRICE_CCY", character(3))
                .column("RETAIL_PRICE_AMOUNT", decimal(15, 2))
                .column("RETAIL_PRICE_CCY", character(3))
            )
            .createIndex(i -> i
                .id("create FK_PART_WIDGET_ID index")
                .author("mark")
                .schemaName("SIESTA")
                .tableName("PART")
                .indexName("FK_PART_WIDGET_ID")
                .unique(false)
                .column("WIDGET_ID")
            )
            .createTable(t -> t
                .id("create manufacturer table")
                .author("mark")
                .schemaName("SIESTA")
                .tableName("MANUFACTURER")
                .column(ManufacturerRow::manufacturerId, bigint(), notNull(), primaryKey())
                .column(ManufacturerRow::name, varchar(100), notNull())
                .column(ManufacturerRow::checked, date())
                .column("INSERTION_TS", timestamp())
            )
            .createTable(t -> t
                .id("create SALESPERSON table")
                .author("mark")
                .schemaName("SIESTA")
                .tableName("SALESPERSON")
                .column(SalespersonRow::salespersonId, bigint(), notNull(), primaryKey())
                .column(SalespersonRow::firstName, varchar(100), notNull())
                .column(SalespersonRow::surname, varchar(100), notNull())
                .column(SalespersonRow::middleNames, varchar(100))
                .column(SalespersonRow::numberOfSales, integer())
                .column(SalespersonRow::commission, decimal(10, 5))
            )
            .createTable(t -> t
                .id("create SALESPERSON temp table")
                .author("mark")
                .schemaName("SIESTA")
                .tableName("TMP_SALESPERSON")
                .globalTemporary()
                .column(SalespersonRow::salespersonId, bigint(), notNull())
                .column(SalespersonRow::firstName, varchar(100), notNull())
                .column(SalespersonRow::surname, varchar(100), notNull())
                .column(SalespersonRow::middleNames, varchar(100))
                .column(SalespersonRow::numberOfSales, integer())
                .column(SalespersonRow::commission, decimal(10, 5))
            )
            .createTable(t -> t
                .id("create test_table table")
                .author("mark")
                .schemaName("SIESTA")
                .tableName("TEST_TABLE")
                .column(TestRow::guid, binary(16), notNull(), primaryKey())
                .column(TestRow::stringReq, varchar(40))
                .column(TestRow::stringOpt, varchar(40))
                .column(TestRow::integerReq, integer())
                .column(TestRow::integerOpt, integer())
                .column(TestRow::decimalReq, decimal(15, 3))
                .column(TestRow::decimalOpt, decimal(15, 3))
                .column(TestRow::localDateReq, date())
                .column(TestRow::localDateOpt, date())
                .column(TestRow::localDateTimeReq, timestamp())
                .column(TestRow::localDateTimeOpt, timestamp())
                .column(TestRow::localTimeReq, time())
                .column(TestRow::localTimeOpt, time())
                .column(TestRow::utcDateTimeReq, timestamp())
                .column(TestRow::utcDateTimeOpt, timestamp())
            )
            .createTable(t -> t
                .id("create LOCK_TEST table")
                .author("mark")
                .schemaName("SIESTA")
                .tableName("LOCK_TEST")
                .column(LockTestRow::id, bigint(), notNull(), primaryKey())
                .column(LockTestRow::revision, integer(), notNull())
                .column(LockTestRow::updatedBy, varchar(80), notNull())
            )
            .createTable(t -> t
                .id("create sales_area table")
                .author("mark")
                .schemaName("SIESTA")
                .tableName("SALES_AREA")
                .column(SalesAreaRow::salesAreaId, bigint(), notNull(), primaryKey())
                .column(SalesAreaRow::salesAreaName, varchar(20), notNull())
                .column(SalesAreaRow::salespersonId, bigint(),
                    foreignKey("FK_SALES_AREA_SALESPERSON").references("SALESPERSON").column("SALESPERSON_ID"))
                .column(SalesAreaRow::salesCount, smallint())
                .column("INSERT_TIME", timestamp())
            )
            .createTable(t -> t
                .id("create a table with no primary key")
                .author("mark")
                .schemaName("SIESTA")
                .tableName("SALE")
                .column("SALESPERSON_ID", bigint(), notNull())
                .column("WIDGET_ID", bigint(), notNull())
                .column("QUANTITY", bigint(), notNull())
                .column("PRICE", decimal(15, 2), notNull())
            )
            .applyIf(dialect.supportsJsonFunctions(), b -> b
                .createTable(t -> t
                    .id("create a table with json")
                    .author("mark")
                    .schemaName("SIESTA")
                    .tableName("JSON_DATA")
                    .column("JSON_ID", bigint(), notNull(), primaryKey())
                    .column("DATA", json(1000))
                    .column("DATA_BINARY", jsonb(1000))
                )
            )
            .build();
    }
}
