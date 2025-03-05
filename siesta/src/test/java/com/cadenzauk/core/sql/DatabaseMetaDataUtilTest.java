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

import com.cadenzauk.core.junit.NullValue;
import com.cadenzauk.core.junit.TestCase;
import com.cadenzauk.core.junit.TestCaseArgumentsProvider;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static co.unruly.matchers.StreamMatchers.contains;
import static com.cadenzauk.core.mockito.MockUtil.when;
import static com.cadenzauk.core.testutil.IsUtilityClass.isUtilityClass;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DatabaseMetaDataUtilTest {
    @Mock
    private DatabaseMetaData metaData;

    @Mock
    private ResultSet resultSet;

    @Test
    void isUtility() {
        assertThat(DatabaseMetaDataUtil.class, isUtilityClass());
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @NullValue("NULL")
    @TestCase({"NULL", "NULL", "BOB", ",,", ",,", "FRED,BOB,JOE", "true"})
    @TestCase({"NULL", "NULL", "Bob", ",,", ",,", "FRED,BOB,JOE", "true"})
    @TestCase({"NULL", "NULL", "bob", ",,", ",,", "FRED,BOB,JOE", "true"})
    @TestCase({"NULL", "NULL", "BOB", ",,", ",,", "fred,bob,joe", "true"})
    @TestCase({"NULL", "NULL", "Bob", ",,", ",,", "fred,bob,joe", "true"})
    @TestCase({"NULL", "NULL", "bob", ",,", ",,", "fred,bob,joe", "true"})

    @TestCase({"", "NULL", "BOB", ",,", ",,", "FRED,BOB,JOE", "true"})
    @TestCase({"", "NULL", "Bob", ",,", ",,", "FRED,BOB,JOE", "true"})
    @TestCase({"", "NULL", "bob", ",,", ",,", "FRED,BOB,JOE", "true"})
    @TestCase({"", "NULL", "BOB", ",,", ",,", "fred,bob,joe", "true"})
    @TestCase({"", "NULL", "Bob", ",,", ",,", "fred,bob,joe", "true"})
    @TestCase({"", "NULL", "bob", ",,", ",,", "fred,bob,joe", "true"})

    @TestCase({"NULL", "NULL", "BOB", "NULL,NULL,NULL", "NULL,NULL,NULL", "FRED,BOB,JOE", "true"})
    @TestCase({"NULL", "NULL", "Bob", "NULL,NULL,NULL", "NULL,NULL,NULL", "FRED,BOB,JOE", "true"})
    @TestCase({"NULL", "NULL", "bob", "NULL,NULL,NULL", "NULL,NULL,NULL", "FRED,BOB,JOE", "true"})
    @TestCase({"NULL", "NULL", "BOB", "NULL,NULL,NULL", "NULL,NULL,NULL", "fred,bob,joe", "true"})
    @TestCase({"NULL", "NULL", "Bob", "NULL,NULL,NULL", "NULL,NULL,NULL", "fred,bob,joe", "true"})
    @TestCase({"NULL", "NULL", "bob", "NULL,NULL,NULL", "NULL,NULL,NULL", "fred,bob,joe", "true"})

    @TestCase({"NULL", "NULL", "BOB", "C1,C2,C3", "S1,S2,S3", "FRED,BOB,JOE", "true"})
    @TestCase({"NULL", "NULL", "Bob", "C1,C2,C3", "S1,S2,S3", "FRED,BOB,JOE", "true"})
    @TestCase({"NULL", "NULL", "bob", "C1,C2,C3", "S1,S2,S3", "FRED,BOB,JOE", "true"})
    @TestCase({"NULL", "NULL", "BOB", "C1,C2,C3", "S1,S2,S3", "fred,bob,joe", "true"})
    @TestCase({"NULL", "NULL", "Bob", "C1,C2,C3", "S1,S2,S3", "fred,bob,joe", "true"})
    @TestCase({"NULL", "NULL", "bob", "C1,C2,C3", "S1,S2,S3", "fred,bob,joe", "true"})

    @TestCase({"NULL", "S2", "BOB", "C1,C2,C3", "S1,S2,S3", "FRED,BOB,JOE", "true"})
    @TestCase({"NULL", "S2", "Bob", "C1,C2,C3", "S1,S2,S3", "FRED,BOB,JOE", "true"})
    @TestCase({"NULL", "S2", "bob", "C1,C2,C3", "S1,S2,S3", "FRED,BOB,JOE", "true"})
    @TestCase({"NULL", "s2", "BOB", "C1,C2,C3", "S1,S2,S3", "fred,bob,joe", "true"})
    @TestCase({"NULL", "s2", "Bob", "C1,C2,C3", "S1,S2,S3", "fred,bob,joe", "true"})
    @TestCase({"NULL", "s2", "bob", "C1,C2,C3", "S1,S2,S3", "fred,bob,joe", "true"})

    @TestCase({"C2", "NULL", "BOB", "C1,C2,C3", "S1,S2,S3", "FRED,BOB,JOE", "true"})
    @TestCase({"C2", "NULL", "Bob", "C1,C2,C3", "S1,S2,S3", "FRED,BOB,JOE", "true"})
    @TestCase({"C2", "NULL", "bob", "C1,C2,C3", "S1,S2,S3", "FRED,BOB,JOE", "true"})
    @TestCase({"c2", "NULL", "BOB", "C1,C2,C3", "S1,S2,S3", "fred,bob,joe", "true"})
    @TestCase({"c2", "NULL", "Bob", "C1,C2,C3", "S1,S2,S3", "fred,bob,joe", "true"})
    @TestCase({"c2", "NULL", "bob", "C1,C2,C3", "S1,S2,S3", "fred,bob,joe", "true"})

    @TestCase({"C2", "S2", "BOB", "C1,C2,C3", "S1,S2,S3", "FRED,BOB,JOE", "true"})
    @TestCase({"C2", "S2", "Bob", "C1,C2,C3", "S1,S2,S3", "FRED,BOB,JOE", "true"})
    @TestCase({"C2", "S2", "bob", "C1,C2,C3", "S1,S2,S3", "FRED,BOB,JOE", "true"})
    @TestCase({"c2", "s2", "BOB", "C1,C2,C3", "S1,S2,S3", "fred,bob,joe", "true"})
    @TestCase({"c2", "s2", "Bob", "C1,C2,C3", "S1,S2,S3", "fred,bob,joe", "true"})
    @TestCase({"c2", "s2", "bob", "C1,C2,C3", "S1,S2,S3", "fred,bob,joe", "true"})

    @TestCase({"C2", "S2", "BOB", "NULL,NULL,NULL", "NULL,NULL,NULL", "FRED,BOB,JOE", "true"})
    @TestCase({"C2", "S2", "Bob", "NULL,NULL,NULL", "NULL,NULL,NULL", "FRED,BOB,JOE", "true"})
    @TestCase({"C2", "S2", "bob", "NULL,NULL,NULL", "NULL,NULL,NULL", "FRED,BOB,JOE", "true"})
    @TestCase({"c2", "s2", "BOB", "NULL,NULL,NULL", "NULL,NULL,NULL", "fred,bob,joe", "true"})
    @TestCase({"c2", "s2", "Bob", "NULL,NULL,NULL", "NULL,NULL,NULL", "fred,bob,joe", "true"})
    @TestCase({"c2", "s2", "bob", "NULL,NULL,NULL", "NULL,NULL,NULL", "fred,bob,joe", "true"})

    @TestCase({"NULL", "NULL", "BOBBY", ",,", ",,", "FRED,BOB,JOE", "false"})
    @TestCase({"NULL", "SX", "BOB", "C1,C2,C3", "S1,S2,S3", "FRED,BOB,JOE", "false"})
    @TestCase({"CX", "NULL", "BOB", "C1,C2,C3", "S1,S2,S3", "FRED,BOB,JOE", "false"})
    void tableExists(String catalog, String schema, String tableName, String[] actualCatalog, String[] actualSchema, String[] actualTable, boolean exists) throws SQLException {
        when(metaData.getTables(isNull(), isNull(), isNull(), eq(new String[] {"TABLE"})))
            .thenReturn(resultSet);
        when(resultSet.getString("TABLE_CAT"))
            .thenReturn(actualCatalog);
        when(resultSet.getString("TABLE_SCHEM"))
            .thenReturn(actualSchema);
        when(resultSet.getString("TABLE_NAME"))
            .thenReturn(actualTable);
        when(resultSet.next())
            .thenReturn(actualTable.length, TRUE)
            .thenReturn(FALSE);

        boolean result = DatabaseMetaDataUtil.tableExists(metaData, catalog, schema, tableName, Function.identity());

        verify(metaData).getTables(isNull(), isNull(), isNull(), eq(new String[] {"TABLE"}));
        assertThat(result, is(exists));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @NullValue("NULL")
    @TestCase({"NULL", "NULL", ",,", ",,", "FRED,BOB,JOE", "FRED,BOB,JOE"})
    @TestCase({"NULL", "NULL", "A,B,C", ",,", "FRED,BOB,JOE", "A..FRED,B..BOB,C..JOE"})
    @TestCase({"NULL", "NULL", ",,", "D,E,F", "FRED,BOB,JOE", "D.FRED,E.BOB,F.JOE"})
    @TestCase({"NULL", "NULL", "A,B,C", "D,E,F", "FRED,BOB,JOE", "A.D.FRED,B.E.BOB,C.F.JOE"})

    @TestCase({"A", "NULL", ",,", ",,", "FRED,BOB,JOE", "FRED,BOB,JOE"})
    @TestCase({"A", "NULL", "A,B,C", ",,", "FRED,BOB,JOE", "A..FRED"})
    @TestCase({"A", "NULL", "A,B,C", "D,E,F", "FRED,BOB,JOE", "A.D.FRED"})
    @TestCase({"X", "NULL", "A,B,C", "D,E,F", "FRED,BOB,JOE", ""})
    @TestCase({"A", "", ",,", ",,", "FRED,BOB,JOE", "FRED,BOB,JOE"})
    @TestCase({"A", "", "A,B,C", ",,", "FRED,BOB,JOE", "A..FRED"})
    @TestCase({"A", "", "A,B,C", "D,E,F", "FRED,BOB,JOE", "A.D.FRED"})

    @TestCase({"NULL", "E", ",,", ",,", "FRED,BOB,JOE", "FRED,BOB,JOE"})
    @TestCase({"NULL", "E", ",,", "D,E,F", "FRED,BOB,JOE", "E.BOB"})
    @TestCase({"NULL", "E", "A,B,C", "D,E,F", "FRED,BOB,JOE", "B.E.BOB"})
    @TestCase({"", "E", ",,", ",,", "FRED,BOB,JOE", "FRED,BOB,JOE"})
    @TestCase({"", "E", ",,", "D,E,F", "FRED,BOB,JOE", "E.BOB"})
    @TestCase({"", "E", "NULL,NULL,NULL", "NULL,NULL,NULL", "FRED,BOB,JOE", "FRED,BOB,JOE"})
    @TestCase({"", "E", "NULL,NULL,NULL", "D,E,F", "FRED,BOB,JOE", "E.BOB"})
    @TestCase({"", "E", "A,B,C", "D,E,F", "FRED,BOB,JOE", "B.E.BOB"})

    @TestCase({"C", "F", ",,", ",,", "FRED,BOB,JOE", "FRED,BOB,JOE"})
    @TestCase({"C", "F", ",,", "D,E,F", "FRED,BOB,JOE", "F.JOE"})
    @TestCase({"C", "F", "NULL,NULL,NULL", "NULL,NULL,NULL", "FRED,BOB,JOE", "FRED,BOB,JOE"})
    @TestCase({"C", "F", "NULL,NULL,NULL", "D,E,F", "FRED,BOB,JOE", "F.JOE"})
    @TestCase({"C", "F", "A,B,C", "D,E,F", "FRED,BOB,JOE", "C.F.JOE"})
    void tableNames(String catalog, String schema, String[] actualCatalog, String[] actualSchema, String[] actualTable, String[] expectedTableNames) throws SQLException {
        QualifiedName[] expectedTables = parseQualifiedNames(expectedTableNames);
        when(metaData.getTables(isNull(), isNull(), isNull(), eq(new String[] {"TABLE"})))
            .thenReturn(resultSet);
        when(resultSet.getString("TABLE_CAT"))
            .thenReturn(actualCatalog);
        when(resultSet.getString("TABLE_SCHEM"))
            .thenReturn(actualSchema);
        when(resultSet.getString("TABLE_NAME"))
            .thenReturn(actualTable);
        when(resultSet.next())
            .thenReturn(actualTable.length, TRUE)
            .thenReturn(FALSE);

        Stream<QualifiedName> result = DatabaseMetaDataUtil.tableNames(metaData, catalog, schema, Function.identity());

        assertThat(result, contains(expectedTables));
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseArgumentsProvider.class)
    @NullValue("NULL")
    @TestCase({"NULL", "NULL", "FRED,BOB,JOE", "FRED.REFA,BOB.REFB,BOB.REFC", "FRED.REFA,BOB.REFB,BOB.REFC"})
    @TestCase({"NULL", "NULL", "X.FRED,Y.BOB,Z.JOE", "X.FRED.REFA,Y.BOB.REFB,Y.BOB.REFC", "X.FRED.REFA,Y.BOB.REFB,Y.BOB.REFC"})
    @TestCase({"NULL", "NULL", "A..FRED,B..BOB,C..JOE", "A..FRED.REFA,B..BOB.REFB,B..BOB.REFC", "A..FRED.REFA,B..BOB.REFB,B..BOB.REFC"})
    @TestCase({"NULL", "NULL", "A.X.FRED,B.Y.BOB,C.Z.JOE", "A.X.FRED.REFA,B.Y.BOB.REFB,B.Y.BOB.REFC", "A.X.FRED.REFA,B.Y.BOB.REFB,B.Y.BOB.REFC"})

    @TestCase({"A", "NULL", "A..FRED,B..BOB,C..JOE", "A..FRED.REFA,B..BOB.REFB,B..BOB.REFC", "A..FRED.REFA"})
    @TestCase({"B", "NULL", "A..FRED,B..BOB,C..JOE", "A..FRED.REFA,B..BOB.REFB,B..BOB.REFC", "B..BOB.REFB,B..BOB.REFC"})
    @TestCase({"C", "NULL", "A..FRED,B..BOB,C..JOE", "A..FRED.REFA,B..BOB.REFB,B..BOB.REFC", ""})

    @TestCase({"A", "", "A..FRED,B..BOB,C..JOE", "A..FRED.REFA,B..BOB.REFB,B..BOB.REFC", "A..FRED.REFA"})
    @TestCase({"B", "", "A..FRED,B..BOB,C..JOE", "A..FRED.REFA,B..BOB.REFB,B..BOB.REFC", "B..BOB.REFB,B..BOB.REFC"})
    @TestCase({"C", "", "A..FRED,B..BOB,C..JOE", "A..FRED.REFA,B..BOB.REFB,B..BOB.REFC", ""})

    @TestCase({"A", "NULL", "A.X.FRED,B.Y.BOB,C.Z.JOE", "A.X.FRED.REFA,B.Y.BOB.REFB,B.Y.BOB.REFC", "A.X.FRED.REFA"})
    @TestCase({"B", "NULL", "A.X.FRED,B.Y.BOB,C.Z.JOE", "A.X.FRED.REFA,B.Y.BOB.REFB,B.Y.BOB.REFC", "B.Y.BOB.REFB,B.Y.BOB.REFC"})
    @TestCase({"C", "NULL", "A.X.FRED,B.Y.BOB,C.Z.JOE", "A.X.FRED.REFA,B.Y.BOB.REFB,B.Y.BOB.REFC", ""})

    @TestCase({"A", "", "A.X.FRED,B.Y.BOB,C.Z.JOE", "A.X.FRED.REFA,B.Y.BOB.REFB,B.Y.BOB.REFC", "A.X.FRED.REFA"})
    @TestCase({"B", "", "A.X.FRED,B.Y.BOB,C.Z.JOE", "A.X.FRED.REFA,B.Y.BOB.REFB,B.Y.BOB.REFC", "B.Y.BOB.REFB,B.Y.BOB.REFC"})
    @TestCase({"C", "", "A.X.FRED,B.Y.BOB,C.Z.JOE", "A.X.FRED.REFA,B.Y.BOB.REFB,B.Y.BOB.REFC", ""})

    @TestCase({"NULL", "X", "X.FRED,Y.BOB,Z.JOE", "X.FRED.REFA,Y.BOB.REFB,Y.BOB.REFC", "X.FRED.REFA"})
    @TestCase({"NULL", "Y", "X.FRED,Y.BOB,Z.JOE", "X.FRED.REFA,Y.BOB.REFB,Y.BOB.REFC", "Y.BOB.REFB,Y.BOB.REFC"})
    @TestCase({"NULL", "Z", "X.FRED,Y.BOB,Z.JOE", "X.FRED.REFA,Y.BOB.REFB,Y.BOB.REFC", ""})

    @TestCase({"", "X", "X.FRED,Y.BOB,Z.JOE", "X.FRED.REFA,Y.BOB.REFB,Y.BOB.REFC", "X.FRED.REFA"})
    @TestCase({"", "Y", "X.FRED,Y.BOB,Z.JOE", "X.FRED.REFA,Y.BOB.REFB,Y.BOB.REFC", "Y.BOB.REFB,Y.BOB.REFC"})
    @TestCase({"", "Z", "X.FRED,Y.BOB,Z.JOE", "X.FRED.REFA,Y.BOB.REFB,Y.BOB.REFC", ""})

    @TestCase({"NULL", "X", "A.X.FRED,B.Y.BOB,C.Z.JOE", "A.X.FRED.REFA,B.Y.BOB.REFB,B.Y.BOB.REFC", "A.X.FRED.REFA"})
    @TestCase({"NULL", "Y", "A.X.FRED,B.Y.BOB,C.Z.JOE", "A.X.FRED.REFA,B.Y.BOB.REFB,B.Y.BOB.REFC", "B.Y.BOB.REFB,B.Y.BOB.REFC"})
    @TestCase({"NULL", "Z", "A.X.FRED,B.Y.BOB,C.Z.JOE", "A.X.FRED.REFA,B.Y.BOB.REFB,B.Y.BOB.REFC", ""})

    @TestCase({"", "X", "A.X.FRED,B.Y.BOB,C.Z.JOE", "A.X.FRED.REFA,B.Y.BOB.REFB,B.Y.BOB.REFC", "A.X.FRED.REFA"})
    @TestCase({"", "Y", "A.X.FRED,B.Y.BOB,C.Z.JOE", "A.X.FRED.REFA,B.Y.BOB.REFB,B.Y.BOB.REFC", "B.Y.BOB.REFB,B.Y.BOB.REFC"})
    @TestCase({"", "Z", "A.X.FRED,B.Y.BOB,C.Z.JOE", "A.X.FRED.REFA,B.Y.BOB.REFB,B.Y.BOB.REFC", ""})

    @TestCase({"A", "X", "A.X.FRED,B.Y.BOB,C.Z.JOE", "A.X.FRED.REFA,B.Y.BOB.REFB,B.Y.BOB.REFC", "A.X.FRED.REFA"})
    @TestCase({"B", "Y", "A.X.FRED,B.Y.BOB,C.Z.JOE", "A.X.FRED.REFA,B.Y.BOB.REFB,B.Y.BOB.REFC", "B.Y.BOB.REFB,B.Y.BOB.REFC"})
    @TestCase({"C", "Z", "A.X.FRED,B.Y.BOB,C.Z.JOE", "A.X.FRED.REFA,B.Y.BOB.REFB,B.Y.BOB.REFC", ""})
    void foreignKeyNames(String catalog, String schema, String[] actualTableNames, String[] actualFks, String[] expectedFks) throws SQLException {
        whenGetTablesThen(actualTableNames);
        whenGetImportedKeysThen(actualTableNames, actualFks);

        Stream<ForeignKeyName> result = DatabaseMetaDataUtil.foreignKeyNames(metaData, catalog, schema, Function.identity());

        ForeignKeyName[] rStream = Arrays.stream(expectedFks)
            .filter(StringUtils::isNotBlank)
            .map(ForeignKeyName::parseString)
            .toArray(ForeignKeyName[]::new);
        assertThat(result, contains(rStream));
    }

    private QualifiedName[] parseQualifiedNames(String[] expectedTableNames) {
        return Arrays.stream(expectedTableNames)
            .filter(StringUtils::isNotBlank)
            .map(QualifiedName::parseString).toArray(QualifiedName[]::new);
    }

    private void whenGetTablesThen(String[] actualTableNames) throws SQLException {
        QualifiedName[] actualTables = parseQualifiedNames(actualTableNames);
        when(metaData.getTables(isNull(), isNull(), isNull(), eq(new String[] {"TABLE"})))
            .thenReturn(resultSet);
        when(resultSet.getString("TABLE_CAT"))
            .thenReturn(Arrays.stream(actualTables).map(x -> x.catalog().orElse(null)));
        when(resultSet.getString("TABLE_SCHEM"))
            .thenReturn(Arrays.stream(actualTables).map(x -> x.schema().orElse(null)));
        when(resultSet.getString("TABLE_NAME"))
            .thenReturn(Arrays.stream(actualTables).map(x -> x.name().orElse(null)));
        when(resultSet.next())
            .thenReturn(actualTables.length, TRUE)
            .thenReturn(FALSE);
    }

    private void whenGetImportedKeysThen(String[] actualTableNames, String[] actualFks) {
        ImmutableSet<QualifiedName> tables = ImmutableSet.copyOf(parseQualifiedNames(actualTableNames));
        Map<QualifiedName,List<ForeignKeyName>> foreignKeysPerTable = Arrays.stream(actualFks)
            .map(ForeignKeyName::parseString)
            .collect(Collectors.groupingBy(ForeignKeyName::table));
        Sets.SetView<QualifiedName> difference = Sets.difference(tables, foreignKeysPerTable.keySet());
        difference.forEach(q -> {
            try {
                ResultSet fkResultSet = Mockito.mock(ResultSet.class);
                String catalog = q.catalog().orElse(null);
                String schema = q.schema().orElse(null);
                String table = q.name().orElse(null);
                when(metaData.getImportedKeys(catalog, schema, table))
                    .thenReturn(fkResultSet);
                when(fkResultSet.next())
                    .thenReturn(FALSE);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        foreignKeysPerTable.forEach((key, value) -> {
            try {
                ResultSet fkResultSet = Mockito.mock(ResultSet.class);
                String catalog = key.catalog().orElse(null);
                String schema = key.schema().orElse(null);
                String table = key.name().orElse(null);
                when(metaData.getImportedKeys(catalog, schema, table))
                    .thenReturn(fkResultSet);
                when(fkResultSet.getString("FKTABLE_CAT"))
                    .thenReturn(catalog);
                when(fkResultSet.getString("FKTABLE_SCHEM"))
                    .thenReturn(schema);
                when(fkResultSet.getString("FKTABLE_NAME"))
                    .thenReturn(table);
                when(fkResultSet.getString("FK_NAME"))
                    .thenReturn(value.stream().map(ForeignKeyName::name));
                when(fkResultSet.next())
                    .thenReturn(value.size(), TRUE)
                    .thenReturn(FALSE);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
