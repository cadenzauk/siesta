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

package com.cadenzauk.siesta.ddl.log.intercept;

import com.cadenzauk.core.lang.CompositeAutoCloseable;
import com.cadenzauk.core.sql.DatabaseMetaDataUtil;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.ddl.action.Action;
import com.cadenzauk.siesta.ddl.action.Priority;
import com.cadenzauk.siesta.ddl.definition.action.CreateIndexAction;
import com.cadenzauk.siesta.ddl.definition.action.CreateTableAction;
import com.cadenzauk.siesta.ddl.action.ActionInterceptor;
import com.cadenzauk.siesta.ddl.log.action.CreateActionLogTable;
import com.google.common.reflect.TypeToken;

import java.sql.DatabaseMetaData;
import java.util.stream.Stream;

import static com.cadenzauk.siesta.ddl.definition.action.Column.Constraints.notNull;
import static com.cadenzauk.siesta.ddl.definition.action.Column.Constraints.primaryKey;
import static com.cadenzauk.siesta.ddl.definition.action.ColumnDataType.timestamp;
import static com.cadenzauk.siesta.ddl.definition.action.ColumnDataType.varchar;

public class ActionLogTableCreator extends ActionInterceptor<CreateActionLogTable> {

    public static final String LOG_TABLE_NAME = "SCHEMA_ACTION_LOG";

    @Override
    public int priority() {
        return Priority.INITIALISATION;
    }

    @Override
    public TypeToken<CreateActionLogTable> supportedType() {
        return TypeToken.of(CreateActionLogTable.class);
    }

    @Override
    public Stream<Action> intercept(Database database, CreateActionLogTable action) {
        try (CompositeAutoCloseable closeable = new CompositeAutoCloseable()) {
            DatabaseMetaData metaData = database.execute("get database metadata", () -> database.getDefaultSqlExecutor().metadata(closeable));
            if (DatabaseMetaDataUtil.tableExists(metaData, action.catalog(), action.schemaName(), LOG_TABLE_NAME, database.dialect()::fixQualifiedName)) {
                return Stream.empty();
            }
        }
        return Stream.of(createTable(action));
    }

    private CreateTableAction createTable(CreateActionLogTable action) {
        return CreateTableAction.newBuilder()
            .definition("siesta")
            .id("create " + LOG_TABLE_NAME)
            .author("mark")
            .logged(false)
            .catalog(action.catalog())
            .schemaName(action.schemaName())
            .tableName(LOG_TABLE_NAME)
            .column("DEFINITION_ID", varchar(100), notNull(), primaryKey())
            .column("ACTION_ID", varchar(100), notNull(), primaryKey())
            .column("AUTHOR", varchar(100), notNull())
            .column("EXECUTED_BY", varchar(100), notNull())
            .column("EXECUTION_TS", timestamp(), notNull())
            .build();
    }

    private CreateIndexAction createPrimaryKeyIndex(CreateActionLogTable action) {
        return CreateIndexAction.newBuilder()
            .unique()
            .definition("siesta")
            .id("create " + LOG_TABLE_NAME + " primary key")
            .author("mark")
            .catalog(action.catalog())
            .schemaName(action.schemaName())
            .tableName(LOG_TABLE_NAME)
            .indexName("PK_" + LOG_TABLE_NAME)
            .columns("DEFINITION_ID", "ACTION_ID")
            .build();
    }
}
