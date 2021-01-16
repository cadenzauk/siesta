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

import static com.cadenzauk.siesta.ddl.definition.action.Column.Constraints.foreignKey;
import static com.cadenzauk.siesta.ddl.definition.action.Column.Constraints.notNull;
import static com.cadenzauk.siesta.ddl.definition.action.Column.Constraints.primaryKey;
import static com.cadenzauk.siesta.ddl.definition.action.ColumnDataType.bigint;
import static com.cadenzauk.siesta.ddl.definition.action.ColumnDataType.binary;
import static com.cadenzauk.siesta.ddl.definition.action.ColumnDataType.character;
import static com.cadenzauk.siesta.ddl.definition.action.ColumnDataType.date;
import static com.cadenzauk.siesta.ddl.definition.action.ColumnDataType.decimal;
import static com.cadenzauk.siesta.ddl.definition.action.ColumnDataType.integer;
import static com.cadenzauk.siesta.ddl.definition.action.ColumnDataType.smallint;
import static com.cadenzauk.siesta.ddl.definition.action.ColumnDataType.time;
import static com.cadenzauk.siesta.ddl.definition.action.ColumnDataType.timestamp;
import static com.cadenzauk.siesta.ddl.definition.action.ColumnDataType.varchar;

public class TestSchema {
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
                .schemaName("SIESTA")
                .tableName("WIDGET")
                .column("WIDGET_ID", bigint(), notNull(), primaryKey())
                .column("NAME", varchar(100), notNull())
                .column("MANUFACTURER_ID", bigint(), notNull())
                .column("DESCRIPTION", varchar(200))
                .column("INSERTION_TS", timestamp())
            )
            .createTable(t -> t
                .id("create part table")
                .author("mark")
                .schemaName("SIESTA")
                .tableName("PART")
                .column("PART_ID", bigint(), notNull(), primaryKey())
                .column("WIDGET_ID", bigint(), notNull())
                .column("DESCRIPTION", varchar(200), notNull())
                .column("PART_TYPE", varchar(100))
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
                .column("MANUFACTURER_ID", bigint(), notNull(), primaryKey())
                .column("NAME", varchar(100), notNull())
                .column("CHECKED", date())
                .column("INSERTION_TS", timestamp())
            )
            .createTable(t -> t
                .id("create SALESPERSON table")
                .author("mark")
                .schemaName("SIESTA")
                .tableName("SALESPERSON")
                .column("SALESPERSON_ID", bigint(), notNull(), primaryKey())
                .column("FIRST_NAME", varchar(100), notNull())
                .column("SURNAME", varchar(100), notNull())
                .column("MIDDLE_NAMES", varchar(100))
                .column("NUMBER_OF_SALES", integer())
                .column("COMMISSION", decimal(10, 5))
            )
            .createTable(t -> t
                .id("create SALESPERSON temp table")
                .author("mark")
                .schemaName("SIESTA")
                .tableName("TMP_SALESPERSON")
                .globalTemporary()
                .column("SALESPERSON_ID", bigint(), notNull())
                .column("FIRST_NAME", varchar(100), notNull())
                .column("SURNAME", varchar(100), notNull())
                .column("MIDDLE_NAMES", varchar(100))
                .column("NUMBER_OF_SALES", integer())
                .column("COMMISSION", decimal(10, 5))
            )
            .createTable(t -> t
                .id("create test_table table")
                .author("mark")
                .schemaName("SIESTA")
                .tableName("TEST_TABLE")
                .column("GUID", binary(16), notNull(), primaryKey())
                .column("STRING_REQ", varchar(40))
                .column("STRING_OPT", varchar(40))
                .column("INTEGER_REQ", integer())
                .column("INTEGER_OPT", integer())
                .column("DECIMAL_REQ", decimal(15, 3))
                .column("DECIMAL_OPT", decimal(15, 3))
                .column("LOCAL_DATE_REQ", date())
                .column("LOCAL_DATE_OPT", date())
                .column("LOCAL_DATE_TIME_REQ", timestamp())
                .column("LOCAL_DATE_TIME_OPT", timestamp())
                .column("LOCAL_TIME_REQ", time())
                .column("LOCAL_TIME_OPT", time())
                .column("UTC_DATE_TIME_REQ", timestamp())
                .column("UTC_DATE_TIME_OPT", timestamp())
            )
            .createTable(t -> t
                .id("create LOCK_TEST table")
                .author("mark")
                .schemaName("SIESTA")
                .tableName("LOCK_TEST")
                .column("ID", bigint(), notNull(), primaryKey())
                .column("REVISION", integer(), notNull())
                .column("UPDATED_BY", varchar(80), notNull())
            )
            .createTable(t -> t
                .id("create sales_area table")
                .author("mark")
                .schemaName("SIESTA")
                .tableName("SALES_AREA")
                .column("SALES_AREA_ID", bigint(), notNull(), primaryKey())
                .column("SALES_AREA_NAME", varchar(20), notNull())
                .column("SALESPERSON_ID", bigint(),
                    foreignKey("FK_SALES_AREA_SALESPERSON").references("SALESPERSON").column("SALESPERSON_ID"))
                .column("SALES_COUNT", smallint())
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
            .build();
    }
}
