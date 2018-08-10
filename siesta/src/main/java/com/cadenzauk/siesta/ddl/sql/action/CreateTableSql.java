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

package com.cadenzauk.siesta.ddl.sql.action;

import com.cadenzauk.core.stream.StreamUtil;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.ddl.definition.action.Column;
import com.cadenzauk.siesta.ddl.definition.action.CreateTableAction;

import static java.util.stream.Collectors.joining;

public class CreateTableSql extends SqlAction {
    private final CreateTableAction definition;

    public CreateTableSql(CreateTableAction definition) {
        this.definition = definition;
    }

    @Override
    public String sql(Database database) {
        return String.format("create table %s (%s%s%s)",
            definition.qualifiedName(database),
            definition.columns().map(column -> columnSql(database, column)).collect(joining(", ")),
            primaryKeySql(),
            foreignKeySql(database));
    }

    private String primaryKeySql() {
        String primaryKeyColumns = definition
            .columns()
            .filter(Column::primaryKey)
            .map(Column::columnName)
            .collect(joining(", "));
        return primaryKeyColumns.isEmpty() ? "" : String.format(", primary key (%s)", primaryKeyColumns);
    }

    private String foreignKeySql(Database database) {
        return definition
            .columns()
            .flatMap(c -> StreamUtil.of(c.foreignKey()))
            .map(fk -> String.format(", constraint %s foreign key(%s) references %s(%s)", fk.constraintName(), fk.columnNames(), fk.qualifiedReferencedTableName(database), fk.referencedColumnNames()))
            .collect(joining());
    }

    private String columnSql(Database database, Column column) {
        return String.format("%s %s%s",
            column.columnName(),
            column.dataType().sql(database),
            column.nullable() ? "" : " not null");
    }
}
