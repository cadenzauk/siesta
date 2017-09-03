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
import com.cadenzauk.core.lang.CompositeAutoCloseable;
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.grammar.select.ExpectingJoin1;
import com.cadenzauk.siesta.grammar.select.InOrderByExpectingThen;
import com.cadenzauk.siesta.grammar.select.InWhereExpectingAnd;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ObjectArrayArguments;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.persistence.MappedSuperclass;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static com.cadenzauk.siesta.grammar.expression.Aggregates.max;
import static com.cadenzauk.siesta.grammar.expression.Aggregates.min;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.collection.IsArrayWithSize.arrayWithSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SelectTest extends MockitoTest {
    @SuppressWarnings("unused")
    @MappedSuperclass
    public static class Row1 {
        private String name;
        private String description;

        public String name() {
            return name;
        }

        public String description() {
            return description;
        }
    }

    @SuppressWarnings("unused")
    public static class Row2 extends Row1 {
        private Optional<String> comment;

        public Optional<String> comment() {
            return comment;
        }
    }

    @Mock
    private Transaction transaction;

    @Mock
    private Stream<Row1> stream;

    @Captor
    private ArgumentCaptor<String> sql;

    @Captor
    private ArgumentCaptor<Object[]> args;

    @Captor
    private ArgumentCaptor<RowMapper<?>> rowMapper;

    @Test
    void whereIsEqualToOneColumnWithoutAlias() {
        Database database = Database.newBuilder().defaultSchema("SIESTA").build();

        database.from(Row1.class)
            .where(Row1::name).isEqualTo(Row1::description)
            .optional(transaction);

        verify(transaction).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select ROW1.NAME as ROW1_NAME, ROW1.DESCRIPTION as ROW1_DESCRIPTION " +
            "from SIESTA.ROW1 ROW1 " +
            "where ROW1.NAME = ROW1.DESCRIPTION"));
        assertThat(args.getValue(), arrayWithSize(0));
    }

    @Test
    void whereIsEqualToOneColumnWithDefaultAlias() {
        Database database = Database.newBuilder().defaultSchema("SIESTA").build();

        database.from(Row1.class, "w")
            .where(Row1::name).isEqualTo(Row1::description)
            .optional(transaction);

        verify(transaction).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select w.NAME as w_NAME, w.DESCRIPTION as w_DESCRIPTION " +
            "from SIESTA.ROW1 w " +
            "where w.NAME = w.DESCRIPTION"));
        assertThat(args.getValue(), arrayWithSize(0));
    }

    @Test
    void whereIsEqualToOneColumnWithAlias() {
        Database database = Database.newBuilder().defaultSchema("SIESTA").build();

        database.from(Row1.class, "w")
            .where(Row1::name).isEqualTo("w", Row1::description)
            .optional(transaction);

        verify(transaction).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select w.NAME as w_NAME, w.DESCRIPTION as w_DESCRIPTION " +
            "from SIESTA.ROW1 w " +
            "where w.NAME = w.DESCRIPTION"));
        assertThat(args.getValue(), arrayWithSize(0));
    }

    @Test
    void whereIsEqualToOneValue() {
        Database database = Database.newBuilder().defaultSchema("SIESTA").build();

        database.from(Row1.class, "w")
            .where(Row1::description).isEqualTo("fred")
            .optional(transaction);

        verify(transaction).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select w.NAME as w_NAME, w.DESCRIPTION as w_DESCRIPTION " +
            "from SIESTA.ROW1 w " +
            "where w.DESCRIPTION = ?"));
        assertThat(args.getValue(), arrayContaining("fred"));
    }

    @Test
    void whereIsEqualToTwoValues() {
        Database database = Database.newBuilder().defaultSchema("SIESTA").build();

        database.from(Row1.class, "w")
            .where(Row1::description).isEqualTo("fred")
            .and(Row1::name).isEqualTo("bob")
            .list(transaction);

        verify(transaction).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select w.NAME as w_NAME, w.DESCRIPTION as w_DESCRIPTION " +
            "from SIESTA.ROW1 w " +
            "where w.DESCRIPTION = ? and w.NAME = ?"));
        assertThat(args.getValue(), arrayContaining("fred", "bob"));
    }

    @Test
    void optionalColumnInCondition() {
        Database database = Database.newBuilder().defaultSchema("SIESTA").build();

        database.from(Row2.class, "x")
            .where(Row2::name).isEqualTo(Row2::comment)
            .orderBy(Row2::description, Order.DESC)
            .list(transaction);

        verify(transaction).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select x.NAME as x_NAME, x.DESCRIPTION as x_DESCRIPTION, x.COMMENT as x_COMMENT " +
            "from SIESTA.ROW2 x " +
            "where x.NAME = x.COMMENT " +
            "order by x.DESCRIPTION desc"));
        assertThat(args.getValue(), arrayWithSize(0));
    }

    private static Arguments testCaseForWhere(BiFunction<Alias<Row2>,ExpectingJoin1<Row2>,InWhereExpectingAnd<Row2>> where, String expected) {
        return ObjectArrayArguments.create(where, expected);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> parametersForWhere() {
        return Stream.of(
            testCaseForWhere((a, s) -> s.where(max(Row2::description)).isEqualTo(a, Row2::name), "where max(q.DESCRIPTION) = q.NAME"),
            testCaseForWhere((a, s) -> s.where(Row2::description).isNotEqualTo(Row2::name), "where q.DESCRIPTION <> q.NAME"),
            testCaseForWhere((a, s) -> s.where(Row2::comment).isNotEqualTo(Row2::name), "where q.COMMENT <> q.NAME"),
            testCaseForWhere((a, s) -> s.where("q", Row2::description).isGreaterThan(Row2::name), "where q.DESCRIPTION > q.NAME"),
            testCaseForWhere((a, s) -> s.where("q", Row2::comment).isEqualTo(Row2::name), "where q.COMMENT = q.NAME"),
            testCaseForWhere((a, s) -> s.where(a, Row2::description).isEqualTo(Row2::name), "where q.DESCRIPTION = q.NAME"),
            testCaseForWhere((a, s) -> s.where(a, Row2::comment).isEqualTo(Row2::name), "where q.COMMENT = q.NAME")
        );
    }

    @ParameterizedTest
    @MethodSource(names = "parametersForWhere")
    void where(BiFunction<Alias<Row2>,ExpectingJoin1<Row2>,InWhereExpectingAnd<Row2>> where, String expected) {
        MockitoAnnotations.initMocks(this);
        Database database = Database.newBuilder().defaultSchema("SIESTA").build();

        Alias<Row2> alias = database.table(Row2.class).as("q");
        where.apply(alias, database.from(Row2.class, "q")).list(transaction);

        verify(transaction).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select q.NAME as q_NAME, q.DESCRIPTION as q_DESCRIPTION, q.COMMENT as q_COMMENT from SIESTA.ROW2 q " + expected));
        assertThat(args.getValue(), arrayWithSize(0));
    }

    private static Arguments testCaseForOrderByOnSelect(BiFunction<Alias<Row2>,ExpectingJoin1<Row2>,InOrderByExpectingThen<Row2>> orderBy, String expected) {
        return ObjectArrayArguments.create(orderBy, expected);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> parametersForOrderByOnSelect() {
        return Stream.of(
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(max(Row2::description)), "max(q.DESCRIPTION) asc"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(Row2::description), "q.DESCRIPTION asc"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(Row2::comment), "q.COMMENT asc"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy("q", Row2::description), "q.DESCRIPTION asc"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy("q", Row2::comment), "q.COMMENT asc"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(a, Row2::description), "q.DESCRIPTION asc"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(a, Row2::comment), "q.COMMENT asc"),

            testCaseForOrderByOnSelect((a, w) -> w.orderBy(min(Row2::comment), Order.DESC), "min(q.COMMENT) desc"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(Row2::name, Order.ASC), "q.NAME asc"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(Row2::name, Order.DESC), "q.NAME desc"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(Row2::comment, Order.DESC), "q.COMMENT desc"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy("q", Row2::name, Order.ASC), "q.NAME asc"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy("q", Row2::name, Order.DESC), "q.NAME desc"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy("q", Row2::comment, Order.DESC), "q.COMMENT desc"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(a, Row2::name, Order.DESC), "q.NAME desc"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(a, Row2::comment, Order.DESC), "q.COMMENT desc"),

            testCaseForOrderByOnSelect((a, w) -> w.orderBy(Row2::name).then(max(Row2::description)), "q.NAME asc, max(q.DESCRIPTION) asc"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(Row2::name).then(Row2::description), "q.NAME asc, q.DESCRIPTION asc"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(Row2::name).then(Row2::comment), "q.NAME asc, q.COMMENT asc"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(Row2::name).then("q", Row2::description), "q.NAME asc, q.DESCRIPTION asc"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(Row2::name).then("q", Row2::comment), "q.NAME asc, q.COMMENT asc"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(Row2::name).then(a, Row2::description), "q.NAME asc, q.DESCRIPTION asc"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(Row2::name).then(a, Row2::comment), "q.NAME asc, q.COMMENT asc")
        );
    }

    @ParameterizedTest
    @MethodSource(names = "parametersForOrderByOnSelect")
    void orderByOnSelect(BiFunction<Alias<Row2>,ExpectingJoin1<Row2>,InOrderByExpectingThen<Row2>> orderBy, String expected) {
        MockitoAnnotations.initMocks(this);
        Database database = Database.newBuilder().defaultSchema("SIESTA").build();
        Alias<Row2> alias = database.table(Row2.class).as("q");

        orderBy.apply(alias, database.from(alias)).optional(transaction);

        verify(transaction).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select q.NAME as q_NAME, q.DESCRIPTION as q_DESCRIPTION, q.COMMENT as q_COMMENT from SIESTA.ROW2 q order by " + expected));
        assertThat(args.getValue(), arrayWithSize(0));
    }


    private static Arguments testCaseForOrderByOnWhereClause(BiFunction<Alias<Row2>,InWhereExpectingAnd<Row2>,InOrderByExpectingThen<Row2>> orderBy, String expected) {
        return ObjectArrayArguments.create(orderBy, expected);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> parametersForOrderByOnWhereClause() {
        return Stream.of(
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(max(Row2::description)), "max(q.DESCRIPTION) asc"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(Row2::description), "q.DESCRIPTION asc"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(Row2::comment), "q.COMMENT asc"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy("q", Row2::description), "q.DESCRIPTION asc"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy("q", Row2::comment), "q.COMMENT asc"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(a, Row2::description), "q.DESCRIPTION asc"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(a, Row2::comment), "q.COMMENT asc"),

            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(min(Row2::comment), Order.DESC), "min(q.COMMENT) desc"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(Row2::name, Order.ASC), "q.NAME asc"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(Row2::name, Order.DESC), "q.NAME desc"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(Row2::comment, Order.DESC), "q.COMMENT desc"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy("q", Row2::name, Order.ASC), "q.NAME asc"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy("q", Row2::name, Order.DESC), "q.NAME desc"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy("q", Row2::comment, Order.DESC), "q.COMMENT desc"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(a, Row2::name, Order.DESC), "q.NAME desc"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(a, Row2::comment, Order.DESC), "q.COMMENT desc"),

            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(Row2::name).then(max(Row2::description)), "q.NAME asc, max(q.DESCRIPTION) asc"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(Row2::name).then(Row2::description), "q.NAME asc, q.DESCRIPTION asc"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(Row2::name).then(Row2::comment), "q.NAME asc, q.COMMENT asc"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(Row2::name).then("q", Row2::description), "q.NAME asc, q.DESCRIPTION asc"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(Row2::name).then("q", Row2::comment), "q.NAME asc, q.COMMENT asc"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(Row2::name).then(a, Row2::description), "q.NAME asc, q.DESCRIPTION asc"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(Row2::name).then(a, Row2::comment), "q.NAME asc, q.COMMENT asc")
        );
    }

    @ParameterizedTest
    @MethodSource(names = "parametersForOrderByOnWhereClause")
    void orderByOnWhereClause(BiFunction<Alias<Row2>,InWhereExpectingAnd<Row2>,InOrderByExpectingThen<Row2>> orderBy, String expected) {
        MockitoAnnotations.initMocks(this);

        Database database = Database.newBuilder().defaultSchema("SIESTA").build();
        Alias<Row2> alias = database.table(Row2.class).as("q");

        orderBy.apply(alias, database.from(alias).where(Row2::name).isEqualTo("joe")).optional(transaction);

        verify(transaction).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select q.NAME as q_NAME, q.DESCRIPTION as q_DESCRIPTION, q.COMMENT as q_COMMENT from SIESTA.ROW2 q where q.NAME = ? order by " + expected));
        assertThat(args.getValue(), arrayContaining("joe"));
    }

    @Test
    void optionalOfNoRowsIsEmpty() {
        Database database = Database.newBuilder().defaultSchema("SIESTA").build();
        when(transaction.query(any(), any(), any())).thenReturn(ImmutableList.of());

        Optional<Row1> result = database.from(Row1.class, "w")
            .optional(transaction);

        assertThat(result, is(Optional.empty()));
    }

    @Test
    void optionalOfOneRowIsRow() {
        Database database = Database.newBuilder().defaultSchema("SIESTA").build();
        Row1 row1 = new Row1();
        when(transaction.query(any(), any(), any())).thenReturn(ImmutableList.of(row1));

        Optional<Row1> result = database.from(Row1.class, "w")
            .optional(transaction);

        verify(transaction).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(result, is(Optional.of(row1)));
    }

    @Test
    void optionalOfTwoRowsIsException() {
        Database database = Database.newBuilder().defaultSchema("SIESTA").build();
        Row1 row1 = new Row1();
        when(transaction.query(any(), any(), any())).thenReturn(ImmutableList.of(row1, row1));

        calling(() -> database.from(Row1.class, "w").optional(transaction))
            .shouldThrow(IllegalArgumentException.class)
            .withMessage(startsWith("expected one element but was: "));

        verify(transaction).query(sql.capture(), args.capture(), rowMapper.capture());
    }

    @Test
    void list() {
        Database database = Database.newBuilder().defaultSchema("SIESTA").build();
        Row1 row1 = new Row1();
        Row1 row2 = new Row1();
        when(transaction.query(any(), any(), any())).thenReturn(ImmutableList.of(row1, row2));

        List<Row1> result = database.from(Row1.class, "w").list(transaction);

        assertThat(result, contains(row1, row2));
    }

    @Test
    void stream() {
        Database database = Database.newBuilder().defaultSchema("SIESTA").build();
        when(transaction.stream(any(), any(), Mockito.<RowMapper<Row1>>any())).thenReturn(stream);

        Stream<Row1> result;
        try (CompositeAutoCloseable autoCloseable = new CompositeAutoCloseable()) {
            result = database.from(Row1.class, "w").stream(transaction, autoCloseable);
        }

        assertThat(result, sameInstance(stream));
        verify(stream).close();
    }
}
