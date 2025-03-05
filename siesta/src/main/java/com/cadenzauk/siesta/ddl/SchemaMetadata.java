/*
 * Copyright (c) 2023 Cadenza United Kingdom Limited
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

import com.cadenzauk.core.lang.CompositeAutoCloseable;
import com.cadenzauk.core.sql.DatabaseMetaDataUtil;
import com.cadenzauk.core.sql.ForeignKeyName;
import com.cadenzauk.core.sql.QualifiedName;
import com.cadenzauk.core.sql.ResultSetUtil;
import com.cadenzauk.siesta.Dialect;

import java.sql.DatabaseMetaData;
import java.util.List;
import java.util.stream.Stream;

import static com.cadenzauk.core.sql.ResultSetUtil.getString;
import static java.util.stream.Collectors.toList;

public class SchemaMetadata implements AutoCloseable {
    private final CompositeAutoCloseable closer = new CompositeAutoCloseable();
    private final Dialect dialect;
    private final DatabaseMetaData databaseMetadata;

    public SchemaMetadata(DatabaseMetaData databaseMetadata, Dialect dialect) {
        this.databaseMetadata = databaseMetadata;
        this.dialect = dialect;
    }

    public Stream<QualifiedName> tableNames(String catalog, String schema) {
        return DatabaseMetaDataUtil.tableNames(databaseMetadata, catalog, schema, dialect::fixQualifiedName);
    }

    public Stream<ForeignKeyName> foreignKeyNames(String catalog, String schema) {
        List<QualifiedName> tableNames = tableNames(catalog, schema).collect(toList());
        return tableNames
            .stream()
            .flatMap(table -> ResultSetUtil.stream(
                    () -> databaseMetadata.getImportedKeys(table.catalog().orElse(null), table.schema().orElse(null), table.name().orElse(null)),
                    rs -> new ForeignKeyName(
                        dialect.fixQualifiedName(new QualifiedName(getString(rs, "FKTABLE_CAT"), getString(rs, "FKTABLE_SCHEM"), getString(rs, "FKTABLE_NAME"))),
                        getString(rs, "FK_NAME")))
                .distinct()
            );
    }

    @Override
    public void close() {
        closer.close();
    }
}
