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

package com.cadenzauk.siesta.dialect;

import com.cadenzauk.core.sql.ConnectionUtil;
import com.cadenzauk.core.sql.DataSourceUtil;
import com.cadenzauk.core.sql.RuntimeSqlException;
import com.cadenzauk.core.tuple.Tuple;
import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.siesta.Dialect;
import com.google.common.collect.ImmutableList;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class AutoDetectDialect {
    private static final List<Tuple2<Predicate<String>,Supplier<Dialect>>> DIALECTS = ImmutableList.of(
        Tuple.of(Pattern.compile("^DB2/.*").asPredicate(), Db2Dialect::new),
        Tuple.of(Pattern.compile("^H2.*").asPredicate(), H2Dialect::new),
        Tuple.of(Pattern.compile("^HSQL.*").asPredicate(), HSqlDialect::new),
        Tuple.of(Pattern.compile("^Firebird.*").asPredicate(), FirebirdDialect::new),
        Tuple.of(Pattern.compile("^Oracle.*").asPredicate(), OracleDialect::new),
        Tuple.of(Pattern.compile("^PostgreSQL.*").asPredicate(), PostgresDialect::new),
        Tuple.of(Pattern.compile("^Microsoft SQL\\s*Server.*").asPredicate(), SqlServerDialect::new)
    );

    public static Dialect from(DataSource dataSource) {
        try (Connection connection = DataSourceUtil.connection(dataSource)) {
            DatabaseMetaData metaData = ConnectionUtil.getMetaData(connection);
            String productName = metaData.getDatabaseProductName();
            return DIALECTS
                .stream()
                .filter(x -> x.item1().test(productName))
                .map(x -> x.item2().get())
                .findFirst()
                .orElseGet(AnsiDialect::new);
        } catch (SQLException e) {
            throw new RuntimeSqlException(e);
        }
    }
}
