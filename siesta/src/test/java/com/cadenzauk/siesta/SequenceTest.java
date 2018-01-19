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
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.grammar.expression.SequenceExpression;
import com.cadenzauk.siesta.grammar.select.InProjectionExpectingComma1;
import com.cadenzauk.siesta.type.DbTypeId;
import com.cadenzauk.siesta.type.DefaultInteger;
import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class SequenceTest extends MockitoTest {
    @Mock
    private Database database;

    @Mock
    private Dialect dialect;

    @Mock
    private SqlExecutor sqlExecutor;

    @Mock
    private InProjectionExpectingComma1<Integer> select;

    @Mock
    private ResultSet resultSet;

    @Test
    void nextVal() {
        when(database.dialect()).thenReturn(dialect);
        when(dialect.nextFromSequence("TOM", "MYSCHEMA", "TEST_SEQ")).thenReturn("select next value from TEST_SEQ");
        Sequence<Integer> sut = createSut();

        SequenceExpression<Integer> result = sut.nextVal();

        assertThat(result.sql(new Scope(database)), is("select next value from TEST_SEQ"));
    }

    @Test
    void single() {
        when(database.select(Mockito.<SequenceExpression<Integer>>any(), eq("TEST_SEQ"))).thenReturn(select);
        when(database.getDefaultSqlExecutor()).thenReturn(sqlExecutor);
        when(select.single(sqlExecutor)).thenReturn(501);
        Sequence<Integer> sut = createSut();

        Integer single = sut.single();

        assertThat(single, is(501));
    }

    @Test
    void type() {
        Sequence<Integer> sut = createSut();

        TypeToken<Integer> result = sut.type();

        assertThat(result, is(TypeToken.of(Integer.class)));
    }

    @Test
    void rowMapper() throws SQLException {
        when(database.dialect()).thenReturn(dialect);
        when(dialect.type(DbTypeId.INTEGER)).thenReturn(new DefaultInteger());
        when(resultSet.getInt("bob")).thenReturn(1034);
        Sequence<Integer> sut = createSut();

        RowMapper<Integer> result = sut.rowMapper("bob");

        assertThat(result, notNullValue());
        assertThat(result.mapRow(resultSet), is(1034));
    }

    @Test
    void sql() {
        when(database.dialect()).thenReturn(dialect);
        when(dialect.nextFromSequence("TOM", "MYSCHEMA", "TEST_SEQ")).thenReturn("get the next sequence value");
        Sequence<Integer> sut = createSut();

        String result = sut.sql();

        assertThat(result, is("get the next sequence value"));
    }

    @NotNull
    private Sequence<Integer> createSut() {
        return Sequence.<Integer>newBuilder()
            .dataType(DataType.INTEGER)
            .sequenceName("TEST_SEQ")
            .catalog("TOM")
            .schema("MYSCHEMA")
            .database(database)
            .build();
    }
}