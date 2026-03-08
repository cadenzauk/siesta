/*
 * Copyright (c) 2026 Cadenza United Kingdom Limited
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

package com.cadenzauk.siesta.type;

import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.DatabaseOptions;
import com.cadenzauk.siesta.Dialect;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultUuidTest {

    @Mock
    private Database database;
    @Mock
    private ResultSet resultSet;
    @Mock
    private Dialect dialect;

    @Nested
    class GetColumnValueByName {
        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void getsBytesIfUuidIsNotSupportedRegardlessOfDatabaseOption(boolean uuidAsBinary) throws SQLException {
            var sut = new DefaultUuid(false);
            lenient().when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(uuidAsBinary);
            when(resultSet.getBytes("column")).thenReturn(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16});
            when(resultSet.wasNull()).thenReturn(false);

            var result = sut.getColumnValue(database, resultSet, "column");

            assertThat(result.toString(), equalTo("01020304-0506-0708-090a-0b0c0d0e0f10"));
            verifyNoMoreInteractions(resultSet);
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void getsBytesAndReturnsNullIfUuidIsNotSupportedRegardlessOfDatabaseOption(boolean uuidAsBinary) throws SQLException {
            var sut = new DefaultUuid(false);
            lenient().when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(uuidAsBinary);
            when(resultSet.getBytes("column")).thenReturn(null);
            when(resultSet.wasNull()).thenReturn(true);

            var result = sut.getColumnValue(database, resultSet, "column");

            assertThat(result, nullValue());
            verifyNoMoreInteractions(resultSet);
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void getsBytesIfUuidAsBytesIsSetRegardlessOfWhetherDialectSupportUuid(boolean uuidSupported) throws SQLException {
            var sut = new DefaultUuid(uuidSupported);
            lenient().when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(false);
            when(resultSet.getBytes("column")).thenReturn(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16});
            when(resultSet.wasNull()).thenReturn(false);

            var result = sut.getColumnValue(database, resultSet, "column");

            assertThat(result.toString(), equalTo("01020304-0506-0708-090a-0b0c0d0e0f10"));
            verifyNoMoreInteractions(resultSet);
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void getsBytesAndReturnsNullIfUuidAsBytesIsSetRegardlessOfWhetherDialectSupportUuid(boolean uuidSupported) throws SQLException {
            var sut = new DefaultUuid(uuidSupported);
            lenient().when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(false);
            when(resultSet.getBytes("column")).thenReturn(null);
            when(resultSet.wasNull()).thenReturn(true);

            var result = sut.getColumnValue(database, resultSet, "column");

            assertThat(result, nullValue());
            verifyNoMoreInteractions(resultSet);
        }

        @Test
        void getsUuidIfUuidAsBinaryNotSetAndUuidIsSupported() throws SQLException {
            var sut = new DefaultUuid(true);
            when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(true);
            UUID someUuid = UUID.randomUUID();
            when(resultSet.getObject("column", UUID.class)).thenReturn(someUuid);
            when(resultSet.wasNull()).thenReturn(false);

            var result = sut.getColumnValue(database, resultSet, "column");

            assertThat(result, equalTo(someUuid));
        }

        @Test
        void getsUuidAndReturnsNullIfUuidAsBinaryNotSetAndUuidIsSupported() throws SQLException {
            var sut = new DefaultUuid(true);
            when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(true);
            when(resultSet.getObject("column", UUID.class)).thenReturn(null);
            when(resultSet.wasNull()).thenReturn(true);

            var result = sut.getColumnValue(database, resultSet, "column");

            assertThat(result, nullValue());
        }
    }

    @Nested
    class GetColumnValueByIndex {
        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void getsBytesIfUuidIsNotSupportedRegardlessOfDatabaseOption(boolean uuidAsBinary) throws SQLException {
            var sut = new DefaultUuid(false);
            lenient().when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(uuidAsBinary);
            when(resultSet.getBytes(2)).thenReturn(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16});
            when(resultSet.wasNull()).thenReturn(false);

            var result = sut.getColumnValue(database, resultSet, 2);

            assertThat(result.toString(), equalTo("01020304-0506-0708-090a-0b0c0d0e0f10"));
            verifyNoMoreInteractions(resultSet);
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void getsBytesAndReturnsNullIfUuidIsNotSupportedRegardlessOfDatabaseOption(boolean uuidAsBinary) throws SQLException {
            var sut = new DefaultUuid(false);
            lenient().when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(uuidAsBinary);
            when(resultSet.getBytes(2)).thenReturn(null);
            when(resultSet.wasNull()).thenReturn(true);

            var result = sut.getColumnValue(database, resultSet, 2);

            assertThat(result, nullValue());
            verifyNoMoreInteractions(resultSet);
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void getsBytesIfUuidAsBytesIsSetRegardlessOfWhetherDialectSupportUuid(boolean uuidSupported) throws SQLException {
            var sut = new DefaultUuid(uuidSupported);
            lenient().when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(false);
            when(resultSet.getBytes(2)).thenReturn(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16});
            when(resultSet.wasNull()).thenReturn(false);

            var result = sut.getColumnValue(database, resultSet, 2);

            assertThat(result.toString(), equalTo("01020304-0506-0708-090a-0b0c0d0e0f10"));
            verifyNoMoreInteractions(resultSet);
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void getsBytesAndReturnsNullIfUuidAsBytesIsSetRegardlessOfWhetherDialectSupportUuid(boolean uuidSupported) throws SQLException {
            var sut = new DefaultUuid(uuidSupported);
            lenient().when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(false);
            when(resultSet.getBytes(2)).thenReturn(null);
            when(resultSet.wasNull()).thenReturn(true);

            var result = sut.getColumnValue(database, resultSet, 2);

            assertThat(result, nullValue());
            verifyNoMoreInteractions(resultSet);
        }

        @Test
        void getsUuidIfUuidAsBinaryNotSetAndUuidIsSupported() throws SQLException {
            var sut = new DefaultUuid(true);
            when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(true);
            UUID someUuid = UUID.randomUUID();
            when(resultSet.getObject(2, UUID.class)).thenReturn(someUuid);
            when(resultSet.wasNull()).thenReturn(false);

            var result = sut.getColumnValue(database, resultSet, 2);

            assertThat(result, equalTo(someUuid));
        }

        @Test
        void getsUuidAndReturnsNullIfUuidAsBinaryNotSetAndUuidIsSupported() throws SQLException {
            var sut = new DefaultUuid(true);
            when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(true);
            when(resultSet.getObject(2, UUID.class)).thenReturn(null);
            when(resultSet.wasNull()).thenReturn(true);

            var result = sut.getColumnValue(database, resultSet, 2);

            assertThat(result, nullValue());
        }
    }

    @Nested
    class SqlTypeWithNoParams {
        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void returnsBinaryIfUuidIsNotSupportedRegardlessOfDatabaseOption(boolean uuidAsBinary) {
            var sut = new DefaultUuid(false);
            lenient().when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(uuidAsBinary);
            when(database.dialect()).thenReturn(dialect);
            when(dialect.type(DbTypeId.BINARY)).thenReturn(new DefaultVarbinary("binary"));

            var result = sut.sqlType(database);

            assertThat(result, equalTo("binary(16)"));
            verifyNoMoreInteractions(resultSet);
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void returnsBinaryIfUuidAsBytesIsSetRegardlessOfWhetherDialectSupportUuid(boolean uuidSupported) {
            var sut = new DefaultUuid(uuidSupported);
            lenient().when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(false);
            when(database.dialect()).thenReturn(dialect);
            when(dialect.type(DbTypeId.BINARY)).thenReturn(new DefaultVarbinary("binary"));

            var result = sut.sqlType(database);

            assertThat(result, equalTo("binary(16)"));
            verifyNoMoreInteractions(resultSet);
        }

        @Test
        void returnsUuidIfUuidAsBinaryNotSetAndUuidIsSupported() {
            var sut = new DefaultUuid(true);
            when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(true);

            var result = sut.sqlType(database);

            assertThat(result, equalTo("uuid"));
        }

        @Test
        void returnsGivenUuidTypeIfUuidAsBinaryNotSetAndUuidIsSupported() {
            var sut = new DefaultUuid(true, "uniqueidentifier");
            when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(true);

            var result = sut.sqlType(database);

            assertThat(result, equalTo("uniqueidentifier"));
        }
    }

    @Nested
    class SqlTypeWithLengthParam {
        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void returnsBinaryIfUuidIsNotSupportedRegardlessOfDatabaseOption(boolean uuidAsBinary) {
            var sut = new DefaultUuid(false);
            lenient().when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(uuidAsBinary);
            when(database.dialect()).thenReturn(dialect);
            when(dialect.type(DbTypeId.BINARY)).thenReturn(new DefaultVarbinary("binary"));

            var result = sut.sqlType(database, 32);

            assertThat(result, equalTo("binary(32)"));
            verifyNoMoreInteractions(resultSet);
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void returnsBinaryIfUuidAsBytesIsSetRegardlesOfWhetherDialectSupportUuid(boolean uuidSupported) {
            var sut = new DefaultUuid(uuidSupported);
            lenient().when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(false);
            when(database.dialect()).thenReturn(dialect);
            when(dialect.type(DbTypeId.BINARY)).thenReturn(new DefaultVarbinary("binary"));

            var result = sut.sqlType(database, 32);

            assertThat(result, equalTo("binary(32)"));
            verifyNoMoreInteractions(resultSet);
        }

        @Test
        void returnsUuidIfUuidAsBinaryNotSetAndUuidIsSupported() {
            var sut = new DefaultUuid(true);
            when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(true);

            var result = sut.sqlType(database, 32);

            assertThat(result, equalTo("uuid"));
        }

        @Test
        void returnsGivenUuidTypeIfUuidAsBinaryNotSetAndUuidIsSupported() {
            var sut = new DefaultUuid(true, "uniqueidentifier");
            when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(true);

            var result = sut.sqlType(database, 32);

            assertThat(result, equalTo("uniqueidentifier"));
        }
    }

    @Nested
    class SqlTypeWithLengthAndPrecisionParams {
        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void returnsBinaryIfUuidIsNotSupportedRegardlessOfDatabaseOption(boolean uuidAsBinary) {
            var sut = new DefaultUuid(false);
            lenient().when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(uuidAsBinary);
            when(database.dialect()).thenReturn(dialect);
            when(dialect.type(DbTypeId.BINARY)).thenReturn(new DefaultVarbinary("binary"));

            var result = sut.sqlType(database, 32, 6);

            assertThat(result, equalTo("binary(32,6)"));
            verifyNoMoreInteractions(resultSet);
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void returnsBinaryIfUuidAsBytesIsSetRegardlesOfWhetherDialectSupportUuid(boolean uuidSupported) {
            var sut = new DefaultUuid(uuidSupported);
            lenient().when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(false);
            when(database.dialect()).thenReturn(dialect);
            when(dialect.type(DbTypeId.BINARY)).thenReturn(new DefaultVarbinary("binary"));

            var result = sut.sqlType(database, 32, 6);

            assertThat(result, equalTo("binary(32,6)"));
            verifyNoMoreInteractions(resultSet);
        }

        @Test
        void returnsUuidIfUuidAsBinaryNotSetAndUuidIsSupported() {
            var sut = new DefaultUuid(true);
            when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(true);

            var result = sut.sqlType(database, 32, 6);

            assertThat(result, equalTo("uuid"));
        }

        @Test
        void returnsGivenUuidTypeIfUuidAsBinaryNotSetAndUuidIsSupported() {
            var sut = new DefaultUuid(true, "uniqueidentifier");
            when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(true);

            var result = sut.sqlType(database, 32, 6);

            assertThat(result, equalTo("uniqueidentifier"));
        }
    }

    @Nested
    class ConvertToDatabase {
        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void returnsByteArrayIfUuidIsNotSupportedRegardlessOfDatabaseOption(boolean uuidAsBinary) {
            var sut = new DefaultUuid(false);
            lenient().when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(uuidAsBinary);
            when(database.dialect()).thenReturn(dialect);
            when(dialect.type(DbTypeId.BINARY)).thenReturn(new DefaultVarbinary("binary"));
            var uuid = UUID.fromString("01020304-0506-0708-090a-0b0c0d0e0f10");

            var result = sut.convertToDatabase(database, uuid);

            assertThat(result, equalTo(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16}));
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void returnsNullIfUuidIsNotSupportedRegardlessOfDatabaseOption(boolean uuidAsBinary) {
            var sut = new DefaultUuid(false);
            lenient().when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(uuidAsBinary);
            when(database.dialect()).thenReturn(dialect);
            when(dialect.type(DbTypeId.BINARY)).thenReturn(new DefaultVarbinary("binary"));

            var result = sut.convertToDatabase(database, null);

            assertThat(result, nullValue());
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void returnsByteArrayIfUuidAsBytesIsSetRegardlessOfWhetherDialectSupportUuid(boolean uuidSupported) {
            var sut = new DefaultUuid(uuidSupported);
            lenient().when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(false);
            when(database.dialect()).thenReturn(dialect);
            when(dialect.type(DbTypeId.BINARY)).thenReturn(new DefaultVarbinary("binary"));
            var uuid = UUID.fromString("01020304-0506-0708-090a-0b0c0d0e0f10");

            var result = sut.convertToDatabase(database, uuid);

            assertThat(result, equalTo(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16}));
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void returnsNullIfUuidAsBytesIsSetRegardlessOfWhetherDialectSupportUuid(boolean uuidSupported) {
            var sut = new DefaultUuid(uuidSupported);
            lenient().when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(false);
            when(database.dialect()).thenReturn(dialect);
            when(dialect.type(DbTypeId.BINARY)).thenReturn(new DefaultVarbinary("binary"));

            var result = sut.convertToDatabase(database, null);

            assertThat(result, nullValue());
        }

        @Test
        void returnsUuidIfUuidAsBinaryNotSetAndUuidIsSupported() {
            var sut = new DefaultUuid(true);
            when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(true);
            var uuid = UUID.fromString("01020304-0506-0708-090a-0b0c0d0e0f10");

            var result = sut.convertToDatabase(database, uuid);

            assertThat(result, equalTo(uuid));
        }

        @Test
        void returnsNullIfUuidAsBinaryNotSetAndUuidIsSupported() {
            var sut = new DefaultUuid(true);
            when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(true);

            var result = sut.convertToDatabase(database, null);

            assertThat(result, nullValue());
        }
    }

    @Nested
    class Literal {
        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void returnsByteArrayIfUuidIsNotSupportedRegardlessOfDatabaseOption(boolean uuidAsBinary) {
            var sut = new DefaultUuid(false);
            lenient().when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(uuidAsBinary);
            when(database.dialect()).thenReturn(dialect);
            when(dialect.type(DbTypeId.BINARY)).thenReturn(new DefaultVarbinary("binary"));
            var uuid = UUID.fromString("01020304-0506-0708-090a-0b0c0d0e0f10");

            var result = sut.literal(database, uuid);

            assertThat(result, equalTo("X'0102030405060708090a0b0c0d0e0f10'"));
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void returnsByteArrayIfUuidAsBytesIsSetRegardlessOfWhetherDialectSupportUuid(boolean uuidSupported) {
            var sut = new DefaultUuid(uuidSupported);
            lenient().when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(false);
            when(database.dialect()).thenReturn(dialect);
            when(dialect.type(DbTypeId.BINARY)).thenReturn(new DefaultVarbinary("binary"));
            var uuid = UUID.fromString("01020304-0506-0708-090a-0b0c0d0e0f10");

            var result = sut.literal(database, uuid);

            assertThat(result, equalTo("X'0102030405060708090a0b0c0d0e0f10'"));
        }

        @Test
        void returnsUuidIfUuidAsBinaryNotSetAndUuidIsSupported() {
            var sut = new DefaultUuid(true);
            when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(true);
            var uuid = UUID.fromString("01020304-0506-0708-090a-0b0c0d0e0f10");

            var result = sut.literal(database, uuid);

            assertThat(result, equalTo("'01020304-0506-0708-090a-0b0c0d0e0f10'"));
        }
    }
    @Nested
    class Parameter {
        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void returnsBinaryParameterIfUuidIsNotSupportedRegardlessOfDatabaseOption(boolean uuidAsBinary) {
            var sut = new DefaultUuid(false);
            lenient().when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(uuidAsBinary);
            when(database.dialect()).thenReturn(dialect);
            when(dialect.type(DbTypeId.BINARY)).thenReturn(new DefaultVarbinary("binary") {
                @Override
                public String parameter(Database database, Optional<byte[]> value) {
                    return "cast(? as binary(16))";
                }
            });
            var uuid = UUID.fromString("01020304-0506-0708-090a-0b0c0d0e0f10");

            var result = sut.parameter(database, Optional.of(uuid));

            assertThat(result, equalTo("cast(? as binary(16))"));
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        void returnsBinaryParameterIfUuidAsBytesIsSetRegardlessOfWhetherDialectSupportUuid(boolean uuidSupported) {
            var sut = new DefaultUuid(uuidSupported);
            lenient().when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(false);
            when(database.dialect()).thenReturn(dialect);
            when(dialect.type(DbTypeId.BINARY)).thenReturn(new DefaultVarbinary("binary") {
                @Override
                public String parameter(Database database, Optional<byte[]> value) {
                    return "cast(? as binary(16))";
                }
            });
            var uuid = UUID.fromString("01020304-0506-0708-090a-0b0c0d0e0f10");

            var result = sut.parameter(database, Optional.of(uuid));

            assertThat(result, equalTo("cast(? as binary(16))"));
        }

        @Test
        void returnsUuidIfUuidAsBinaryNotSetAndUuidIsSupported() {
            var sut = new DefaultUuid(true);
            when(database.isNotSet(DatabaseOptions.Option.UuidAsBinary)).thenReturn(true);
            var uuid = UUID.fromString("01020304-0506-0708-090a-0b0c0d0e0f10");

            var result = sut.parameter(database, Optional.of(uuid));

            assertThat(result, equalTo("?"));
        }
    }
}
