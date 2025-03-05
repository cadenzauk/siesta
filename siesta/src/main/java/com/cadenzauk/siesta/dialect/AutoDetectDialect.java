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

import com.cadenzauk.core.lang.CompositeAutoCloseable;
import com.cadenzauk.core.sql.ConnectionUtil;
import com.cadenzauk.core.sql.DataSourceUtil;
import com.cadenzauk.core.sql.PreparedStatementUtil;
import com.cadenzauk.core.sql.ResultSetUtil;
import com.cadenzauk.core.sql.RuntimeSqlException;
import com.cadenzauk.core.tuple.Tuple;
import com.cadenzauk.core.tuple.Tuple2;
import com.cadenzauk.siesta.Dialect;
import com.google.common.collect.ImmutableList;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class AutoDetectDialect {
    private static final List<Tuple2<Predicate<String>,Function<Connection,Dialect>>> DIALECTS = ImmutableList.of(
        Tuple.of(Pattern.compile("^DB2/.*").asPredicate(), conn -> new Db2Dialect()),
        Tuple.of(Pattern.compile("^Apache Derby.*").asPredicate(), conn -> new DerbyDialect()),
        Tuple.of(Pattern.compile("^H2.*").asPredicate(), AutoDetectDialect::createH2Dialect),
        Tuple.of(Pattern.compile("^HSQL.*").asPredicate(), conn -> new HSqlDialect()),
        Tuple.of(Pattern.compile("^Firebird.*").asPredicate(), AutoDetectDialect::createFirebirdDialect),
        Tuple.of(Pattern.compile("^Maria.*").asPredicate(), conn -> new MariaDbDialect()),
        Tuple.of(Pattern.compile("^MySQL.*").asPredicate(), conn -> new MySqlDialect()),
        Tuple.of(Pattern.compile("^Oracle.*").asPredicate(), conn -> new OracleDialect()),
        Tuple.of(Pattern.compile("^PostgreSQL.*").asPredicate(), conn -> new PostgresDialect()),
        Tuple.of(Pattern.compile("^Microsoft SQL\\s*Server.*").asPredicate(), conn -> new SqlServerDialect())
    );

    public static Dialect from(DataSource dataSource) {
        try (Connection connection = DataSourceUtil.connection(dataSource)) {
            DatabaseMetaData metaData = ConnectionUtil.getMetaData(connection);
            String productName = metaData.getDatabaseProductName();
            return DIALECTS
                .stream()
                .filter(x -> x.item1().test(productName))
                .map(x -> x.item2().apply(connection))
                .findFirst()
                .orElseGet(AnsiDialect::new);
        } catch (SQLException e) {
            throw new RuntimeSqlException(e);
        }
    }

    private static H2Dialect createH2Dialect(Connection connection) {
        try (CompositeAutoCloseable closer = new CompositeAutoCloseable()) {
            PreparedStatement preparedStatement = closer.add(ConnectionUtil.prepare(connection, "SELECT H2VERSION() as H2VERSION FROM DUAL"));
            ResultSet resultSet = closer.add(PreparedStatementUtil.executeQuery(preparedStatement));
            return closer.add(ResultSetUtil.stream(resultSet, rs -> ResultSetUtil.getString(rs, "H2VERSION")))
                .limit(1)
                .map(VersionNo::new)
                .map(H2Dialect::new)
                .findFirst()
                .orElseGet(H2Dialect::new);
        }
    }

    private static FirebirdDialect createFirebirdDialect(Connection connection) {
        try (CompositeAutoCloseable closer = new CompositeAutoCloseable()) {
            PreparedStatement preparedStatement = closer.add(ConnectionUtil.prepare(connection, "SELECT rdb$get_context('SYSTEM', 'ENGINE_VERSION') as version from rdb$database"));
            ResultSet resultSet = closer.add(PreparedStatementUtil.executeQuery(preparedStatement));
            return closer.add(ResultSetUtil.stream(resultSet, rs -> ResultSetUtil.getString(rs, "VERSION")))
                .limit(1)
                .map(VersionNo::new)
                .map(FirebirdDialect::new)
                .findFirst()
                .orElseGet(FirebirdDialect::new);
        }
    }
}
