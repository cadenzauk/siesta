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

package com.cadenzauk.core.sql;

import com.cadenzauk.core.util.UtilityClass;

import java.sql.DatabaseMetaData;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.cadenzauk.core.sql.QualifiedName.matchesCatalogAndSchema;
import static com.cadenzauk.core.sql.QualifiedName.matchesName;
import static com.cadenzauk.core.sql.ResultSetUtil.getString;
import static java.util.stream.Collectors.toList;

public final class DatabaseMetaDataUtil extends UtilityClass {
    public static boolean tableExists(DatabaseMetaData metaData, String catalog, String schema, String tableName, Function<QualifiedName, QualifiedName> fix) {
        return tableNames(metaData, catalog, schema, fix)
            .anyMatch(matchesName(tableName));
    }

    public static Stream<QualifiedName> tableNames(DatabaseMetaData metaData, String catalog, String schema, Function<QualifiedName, QualifiedName> fix) {
        return ResultSetUtil.stream(
                () -> metaData.getTables(null, null, null, new String[]{"TABLE"}),
                rs -> new QualifiedName(getString(rs, "TABLE_CAT"), getString(rs, "TABLE_SCHEM"), getString(rs, "TABLE_NAME")))
            .map(fix)
            .filter(matchesCatalogAndSchema(catalog, schema));
    }

    public static Stream<ForeignKeyName> foreignKeyNames(DatabaseMetaData metaData, String catalog, String schema, Function<QualifiedName, QualifiedName> fix) {
        List<QualifiedName> tableNames = tableNames(metaData, catalog, schema, fix).collect(toList());
        return tableNames
            .stream()
            .flatMap(table -> ResultSetUtil.stream(
                    () -> metaData.getImportedKeys(table.catalog().orElse(null), table.schema().orElse(null), table.name().orElse(null)),
                    rs -> new ForeignKeyName(
                        new QualifiedName(getString(rs, "FKTABLE_CAT"), getString(rs, "FKTABLE_SCHEM"), getString(rs, "FKTABLE_NAME")),
                        getString(rs, "FK_NAME")))
                .distinct()
            );
    }
}
