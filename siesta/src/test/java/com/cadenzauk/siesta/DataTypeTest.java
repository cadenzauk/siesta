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

package com.cadenzauk.siesta;

import com.cadenzauk.core.MockitoTest;
import com.cadenzauk.core.RandomValues;
import com.cadenzauk.core.lang.UncheckedAutoCloseable;
import com.cadenzauk.core.sql.ResultSetGet;
import com.cadenzauk.core.sql.SqlBiConsumer;
import com.cadenzauk.siesta.dialect.AnsiDialect;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import static com.cadenzauk.core.RandomValues.randomLocalDate;
import static com.cadenzauk.core.RandomValues.randomLocalDateTime;
import static com.cadenzauk.core.testutil.TemporalTestUtil.withTimeZone;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class DataTypeTest extends MockitoTest {
    @Mock
    private ResultSet rs;

    @Mock
    private Database db;

    @SuppressWarnings("unused")
    static Stream<Arguments> parametersForJavaClass() {
        return Stream.of(
            Arguments.of(DataType.BIG_DECIMAL, BigDecimal.class),
            Arguments.of(DataType.BYTE, Byte.class),
            Arguments.of(DataType.BYTE_ARRAY, byte[].class),
            Arguments.of(DataType.DOUBLE, Double.class),
            Arguments.of(DataType.FLOAT, Float.class),
            Arguments.of(DataType.INTEGER, Integer.class),
            Arguments.of(DataType.LOCAL_DATE, LocalDate.class),
            Arguments.of(DataType.LOCAL_DATE_TIME, LocalDateTime.class),
            Arguments.of(DataType.LONG, Long.class),
            Arguments.of(DataType.SHORT, Short.class),
            Arguments.of(DataType.STRING, String.class),
            Arguments.of(DataType.ZONED_DATE_TIME, ZonedDateTime.class)
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForJavaClass")
    void javaClass(DataType<?> dataType, Class<?> expectedClass) {
        Class<?> actual = dataType.javaClass();

        assertThat(actual, equalTo(expectedClass));
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> parametersForToDatabase() {
        return Stream.of(
            testCaseForToDatabase(DataType.BIG_DECIMAL, new BigDecimal(RandomUtils.nextDouble())),
            testCaseForToDatabase(DataType.BYTE, RandomValues.randomByte()),
            testCaseForToDatabase(DataType.BYTE_ARRAY, RandomUtils.nextBytes(40)),
            testCaseForToDatabase(DataType.DOUBLE, RandomUtils.nextDouble()),
            testCaseForToDatabase(DataType.FLOAT, RandomUtils.nextFloat()),
            testCaseForToDatabase(DataType.INTEGER, RandomUtils.nextInt()),
            testCaseForToDatabase(DataType.LONG, RandomUtils.nextLong()),
            testCaseForToDatabase(DataType.SHORT, RandomValues.randomShort()),
            testCaseForToDatabase(DataType.STRING, RandomStringUtils.random(20)),
            testCaseForToDatabase(DataType.BIG_DECIMAL, null),
            testCaseForToDatabase(DataType.BYTE, null),
            testCaseForToDatabase(DataType.BYTE_ARRAY, null),
            testCaseForToDatabase(DataType.DOUBLE, null),
            testCaseForToDatabase(DataType.FLOAT, null),
            testCaseForToDatabase(DataType.INTEGER, null),
            testCaseForToDatabase(DataType.LONG, null),
            testCaseForToDatabase(DataType.SHORT, null),
            testCaseForToDatabase(DataType.STRING, null),
            testCaseForToDatabase(DataType.LOCAL_DATE, null),
            testCaseForToDatabase(DataType.LOCAL_DATE_TIME, null),
            testCaseForToDatabase(DataType.ZONED_DATE_TIME, null)
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForToDatabase")
    <T> void toDatabase(DataType<T> dataType, T input, Object expected) {
        if (input != null) {
            when(db.dialect()).thenReturn(new AnsiDialect());
        }

        Object actual = dataType.toDatabase(db, input);

        assertThat(actual, is(expected));
    }

    @ParameterizedTest
    @MethodSource("parametersForToDatabase")
    <T> void toDatabaseOfOptional(DataType<T> dataType, T input, Object expected) {
        if (input != null) {
            when(db.dialect()).thenReturn(new AnsiDialect());
        }

        Object actual = dataType.toDatabase(db, Optional.ofNullable(input));

        assertThat(actual, is(expected));
    }

    @ParameterizedTest
    @MethodSource("timeZones")
    void toDatabaseLocalDate(String timeZone) {
        try (UncheckedAutoCloseable ignored = withTimeZone(timeZone)) {
            LocalDate input = RandomValues.randomLocalDate();
            Date expected = Date.valueOf(input);
            when(db.dialect()).thenReturn(new AnsiDialect());

            Object result = DataType.LOCAL_DATE.toDatabase(db, input);

            assertThat(result, is(expected));
        }
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> timeZonePairs() {
        return timeZones().flatMap(tz1 -> timeZones().map(tz2 -> Arguments.of(tz1, tz2)));
    }

    @ParameterizedTest
    @MethodSource("timeZones")
    void toDatabaseLocalDateTime(String jvmTimeZone) {
        try (UncheckedAutoCloseable ignored = withTimeZone(jvmTimeZone)) {
            LocalDateTime input = RandomValues.randomLocalDateTime();
            Timestamp expected = Timestamp.valueOf(input);
            when(db.dialect()).thenReturn(new AnsiDialect());

            Object result = DataType.LOCAL_DATE_TIME.toDatabase(db, input);

            assertThat(result, is(expected));
        }
    }

    @ParameterizedTest
    @MethodSource("timeZonePairs")
    void toDatabaseZonedDateTime(String dbTimeZone, String jvmTimeZone) {
        ZoneId dbZone = ZoneId.of(dbTimeZone);
        ZoneId jvmZone = ZoneId.of(jvmTimeZone);
        ZonedDateTime input = RandomValues.randomZonedDateTime(jvmZone);

        try (UncheckedAutoCloseable ignored = withTimeZone(jvmTimeZone)) {
            ZonedDateTime dbTime = input.withZoneSameInstant(dbZone);
            Timestamp expected = Timestamp.valueOf(dbTime.toLocalDateTime());
            when(db.databaseTimeZone()).thenReturn(dbZone);
            when(db.dialect()).thenReturn(new AnsiDialect());

            Object result = DataType.ZONED_DATE_TIME.toDatabase(db, input);

            assertThat(result, is(expected));
        }
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> parametersForGet() {
        return Stream.of(
            testCaseForGet(DataType.BIG_DECIMAL, ResultSet::getBigDecimal, new BigDecimal("5001.12")),
            testCaseForGet(DataType.BYTE, ResultSet::getByte, (byte) 127),
            testCaseForGet(DataType.BYTE_ARRAY, ResultSet::getBytes, new byte[]{0x02, 0x7f, -1}),
            testCaseForGet(DataType.DOUBLE, ResultSet::getDouble, 3.1415),
            testCaseForGet(DataType.FLOAT, ResultSet::getFloat, 2.71f),
            testCaseForGet(DataType.INTEGER, ResultSet::getInt, 451),
            testCaseForGet(DataType.LONG, ResultSet::getLong, 9223372036854775807L),
            testCaseForGet(DataType.SHORT, ResultSet::getShort, (short) 32767),
            testCaseForGet(DataType.STRING, ResultSet::getString, "ABC'DEF\"G")
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForGet")
    <T> void get(DataType<T> sut, SqlBiConsumer<ResultSet,String> resultSetExtractor, Optional<ZoneId> dbTimeZone, T expected) throws SQLException {
        resultSetExtractor.accept(rs, "someColumn");
        when(db.dialect()).thenReturn(new AnsiDialect());
        dbTimeZone.ifPresent(zone -> when(db.databaseTimeZone()).thenReturn(zone));

        Optional<T> result = sut.get(rs, "someColumn", db);

        assertThat(result, is(Optional.of(expected)));
    }

    @SuppressWarnings("unused")
    private static Stream<String> timeZones() {
        return Stream.of("UTC", "America/Anchorage", "Europe/London", "Europe/Berlin", "Africa/Johannesburg", "Pacific/Chatham");
    }

    @ParameterizedTest
    @MethodSource("timeZones")
    void getLocalDate(String timeZone) throws SQLException {
        try (UncheckedAutoCloseable ignored = withTimeZone(timeZone)) {
            LocalDate expected = randomLocalDate();
            when(db.dialect()).thenReturn(new AnsiDialect());
            when(rs.getDate(eq("someColumn"), any())).thenReturn(Date.valueOf(expected));

            Optional<LocalDate> result = DataType.LOCAL_DATE.get(rs, "someColumn", db);

            assertThat(result, is(Optional.of(expected)));
        }
    }

    @ParameterizedTest
    @MethodSource("timeZones")
    void getLocalDateTime(String timeZone) throws SQLException {
        try (UncheckedAutoCloseable ignored = withTimeZone(timeZone)) {
            LocalDateTime expected = randomLocalDateTime();
            when(db.dialect()).thenReturn(new AnsiDialect());
            when(rs.getTimestamp(eq("someColumn"), any())).thenReturn(Timestamp.valueOf(expected));

            Optional<LocalDateTime> result = DataType.LOCAL_DATE_TIME.get(rs, "someColumn", db);

            assertThat(result, is(Optional.of(expected)));
        }
    }

    @ParameterizedTest
    @MethodSource("timeZones")
    void getZonedDateTime(String timeZone) throws SQLException {
        ZonedDateTime expected = ZonedDateTime.of(randomLocalDateTime(), ZoneId.of("UTC"));
        Timestamp returnVal = Timestamp.from(expected.toInstant());
        when(rs.getTimestamp(eq("someColumn"), any())).thenReturn(returnVal);
        when(db.databaseTimeZone()).thenReturn(ZoneId.of(timeZone));
        when(db.dialect()).thenReturn(new AnsiDialect());

        Optional<ZonedDateTime> result = DataType.ZONED_DATE_TIME.get(rs, "someColumn", db);

        assertThat(result, is(Optional.of(expected)));
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> parametersForLiteral() {
        return Stream.of(
            literalTestCase(DataType.BIG_DECIMAL, new BigDecimal("5001.12"), "5001.12"),
            literalTestCase(DataType.BYTE, (byte) 127, "cast(127 as tinyint)"),
            literalTestCase(DataType.BYTE_ARRAY, new byte[]{0x02, 0x7f, -1}, "X'027fff'"),
            literalTestCase(DataType.DOUBLE, 3.1415, "3.1415"),
            literalTestCase(DataType.FLOAT, 2.71f, "cast(2.71 as real)"),
            literalTestCase(DataType.INTEGER, 451, "451"),
            literalTestCase(DataType.LOCAL_DATE, LocalDate.of(2014, 9, 12), "DATE '2014-09-12'"),
            literalTestCase(DataType.LOCAL_DATE_TIME, LocalDateTime.of(1951, 3, 31, 14, 10, 51, 5000), "TIMESTAMP '1951-03-31 14:10:51.000005'"),
            literalTestCase(DataType.LONG, 9223372036854775807L, "9223372036854775807"),
            literalTestCase(DataType.SHORT, (short) 32767, "cast(32767 as smallint)"),
            literalTestCase(DataType.STRING, "ABC'DEF\"G", "'ABC''DEF\"G'"),
            literalTestCase(DataType.ZONED_DATE_TIME, ZonedDateTime.of(2002, 7, 18, 2, 8, 18, 284590000, ZoneId.of("America/Los_Angeles")), ZoneId.of("America/Los_Angeles"), "TIMESTAMP '2002-07-18 02:08:18.284590'"),
            literalTestCase(DataType.ZONED_DATE_TIME, ZonedDateTime.of(2002, 7, 18, 2, 8, 18, 284590000, ZoneId.of("America/New_York")), ZoneId.of("America/Los_Angeles"), "TIMESTAMP '2002-07-17 23:08:18.284590'"),
            literalTestCase(DataType.ZONED_DATE_TIME, ZonedDateTime.of(2002, 7, 18, 2, 8, 18, 284590000, ZoneId.of("UTC")), ZoneId.of("Pacific/Chatham"), "TIMESTAMP '2002-07-18 14:53:18.284590'")
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForLiteral")
    <T> void literal(DataType<T> sut, T value, ZoneId dbTimeZone, String expected) {
        String result = sut.literal(Database.newBuilder().databaseTimeZone(dbTimeZone).build(), value);

        assertThat(result, is(expected));
    }

    private static <T> Arguments testCaseForToDatabase(DataType<T> dataType, T value) {
        return Arguments.of(dataType, value, value);
    }

    private static <T> Arguments testCaseForGet(DataType<T> dataType, ResultSetGet<T> get, T value) {
        return testCaseForGet(dataType, get, value, value);
    }

    private static <T, V> Arguments testCaseForGet(DataType<T> sut, ResultSetGet<V> get, V value, T expected) {
        return createTestCaseArgs(sut, (rs, col) -> when(get.get(rs, col)).thenReturn(value), Optional.empty(), expected);
    }

    private static <T> Arguments createTestCaseArgs(DataType<T> sut, SqlBiConsumer<ResultSet,String> resultSetConsumer, Optional<ZoneId> dbTimeZone, T expected) {
        return Arguments.of(sut, resultSetConsumer, dbTimeZone, expected);
    }

    private static <T> Arguments literalTestCase(DataType<T> sut, T value, String expected) {
        return Arguments.of(sut, value, ZoneId.systemDefault(), expected);
    }

    private static Arguments literalTestCase(DataType<ZonedDateTime> sut, ZonedDateTime value, ZoneId dbTimeZone, String expected) {
        return Arguments.of(sut, value, dbTimeZone, expected);
    }

}