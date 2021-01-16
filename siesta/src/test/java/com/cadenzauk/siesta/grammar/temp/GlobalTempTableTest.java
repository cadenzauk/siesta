/*
 * Copyright (c) 2020 Cadenza United Kingdom Limited
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

package com.cadenzauk.siesta.grammar.temp;

import com.cadenzauk.core.reflect.FieldInfo;
import com.cadenzauk.siesta.DataType;
import com.cadenzauk.siesta.Database;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.cadenzauk.core.mockito.MockUtil.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

@ExtendWith(MockitoExtension.class)
class GlobalTempTableTest {
    @Mock
    private Database database;

    public static class TestRow {
        long id;
    }

    @Test
    void tableName() {
        String randomTableName = RandomStringUtils.randomAlphabetic(20);
        when(database.dataTypeOf(FieldInfo.of(TestRow.class, "id", long.class))).thenReturn(Optional.of(DataType.LONG));
        when(database.columnName("id")).thenReturn("ID");
        TempTable<TestRow> sut = GlobalTempTable.newBuilder(database, TestRow.class)
                                            .tableName(randomTableName)
                                            .build();

        String result = sut.tableName();

        assertThat(result, is(randomTableName));
    }

    @Test
    void rowType() {
        when(database.dataTypeOf(FieldInfo.of(TestRow.class, "id", long.class))).thenReturn(Optional.of(DataType.LONG));
        when(database.columnName("id")).thenReturn("ID");
        TempTable<TestRow> sut = GlobalTempTable.newBuilder(database, TestRow.class).build();

        TypeToken<TestRow> result = sut.rowType();

        assertThat(result, is(TypeToken.of(TestRow.class)));
    }

    @Test
    void database() {
        when(database.dataTypeOf(FieldInfo.of(TestRow.class, "id", long.class))).thenReturn(Optional.of(DataType.LONG));
        when(database.columnName("id")).thenReturn("ID");
        TempTable<TestRow> sut = GlobalTempTable.newBuilder(database, TestRow.class).build();

        Database result = sut.database();

        assertThat(result, sameInstance(database));
    }

    @Test
    void onCommit() {
    }

    @Test
    void qualifiedTableName() {
    }

    @Test
    void as() {
    }

    @Test
    void columns() {
    }

    @Test
    void columnDefinitions() {
    }

    @Test
    void rowMapperFactory() {
    }

    @Test
    void insert() {
    }
}