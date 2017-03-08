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

import com.cadenzauk.siesta.grammar.select.ExpectingOrderBy;
import com.cadenzauk.siesta.grammar.select.ExpectingWhere;
import com.cadenzauk.siesta.grammar.select.InOrderByExpectingThen;
import com.cadenzauk.siesta.grammar.select.InWhereExpectingAnd;
import com.cadenzauk.siesta.grammar.select.ExpectingJoin1;
import com.google.common.collect.ImmutableList;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.persistence.MappedSuperclass;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import static com.cadenzauk.siesta.Aggregates.max;
import static com.cadenzauk.siesta.Aggregates.min;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.collection.IsArrayWithSize.arrayWithSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class SelectTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

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
    private SqlExecutor sqlExecutor;

    @Captor
    private ArgumentCaptor<String> sql;

    @Captor
    private ArgumentCaptor<Object[]> args;

    @Captor
    private ArgumentCaptor<RowMapper<?>> rowMapper;

    @Test
    public void whereIsEqualToOneColumnWithoutAlias() {
        Database database = Database.newBuilder().defaultSchema("TEST").build();

        database.from(Row1.class)
            .where(Row1::name).isEqualTo(Row1::description)
            .optional(sqlExecutor);

        verify(sqlExecutor).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select ROW1.NAME as ROW1_NAME, ROW1.DESCRIPTION as ROW1_DESCRIPTION " +
            "from TEST.ROW1 as ROW1 " +
            "where ROW1.NAME = ROW1.DESCRIPTION"));
        assertThat(args.getValue(), arrayWithSize(0));
    }

    @Test
    public void whereIsEqualToOneColumnWithDefaultAlias() {
        Database database = Database.newBuilder().defaultSchema("TEST").build();

        database.from(Row1.class, "w")
            .where(Row1::name).isEqualTo(Row1::description)
            .optional(sqlExecutor);

        verify(sqlExecutor).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select w.NAME as w_NAME, w.DESCRIPTION as w_DESCRIPTION " +
            "from TEST.ROW1 as w " +
            "where w.NAME = w.DESCRIPTION"));
        assertThat(args.getValue(), arrayWithSize(0));
    }

    @Test
    public void whereIsEqualToOneColumnWithAlias() {
        Database database = Database.newBuilder().defaultSchema("TEST").build();

        database.from(Row1.class, "w")
            .where(Row1::name).isEqualTo("w", Row1::description)
            .optional(sqlExecutor);

        verify(sqlExecutor).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select w.NAME as w_NAME, w.DESCRIPTION as w_DESCRIPTION " +
            "from TEST.ROW1 as w " +
            "where w.NAME = w.DESCRIPTION"));
        assertThat(args.getValue(), arrayWithSize(0));
    }

    @Test
    public void whereIsEqualToOneValue() {
        Database database = Database.newBuilder().defaultSchema("TEST").build();

        database.from(Row1.class, "w")
            .where(Row1::description).isEqualTo("fred")
            .optional(sqlExecutor);

        verify(sqlExecutor).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select w.NAME as w_NAME, w.DESCRIPTION as w_DESCRIPTION " +
            "from TEST.ROW1 as w " +
            "where w.DESCRIPTION = ?"));
        assertThat(args.getValue(), arrayContaining("fred"));
    }

    @Test
    public void whereIsEqualToTwoValues() {
        Database database = Database.newBuilder().defaultSchema("TEST").build();

        database.from(Row1.class, "w")
            .where(Row1::description).isEqualTo("fred")
            .and(Row1::name).isEqualTo("bob")
            .list(sqlExecutor);

        verify(sqlExecutor).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select w.NAME as w_NAME, w.DESCRIPTION as w_DESCRIPTION " +
            "from TEST.ROW1 as w " +
            "where (w.DESCRIPTION = ?) and (w.NAME = ?)"));
        assertThat(args.getValue(), arrayContaining("fred", "bob"));
    }

    @Test
    public void optionalColumnInCondition() {
        Database database = Database.newBuilder().defaultSchema("TEST").build();

        database.from(Row2.class, "x")
            .where(Row2::name).isEqualTo(Row2::comment)
            .orderBy(Row2::description, Order.DESCENDING)
            .list(sqlExecutor);

        verify(sqlExecutor).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select x.NAME as x_NAME, x.DESCRIPTION as x_DESCRIPTION, x.COMMENT as x_COMMENT " +
            "from TEST.ROW2 as x " +
            "where x.NAME = x.COMMENT " +
            "order by x.DESCRIPTION descending"));
        assertThat(args.getValue(), arrayWithSize(0));
    }

    private Object[] testCaseForWhere(BiFunction<Alias<Row2>,ExpectingWhere<Row2>,InWhereExpectingAnd<Row2>> f, String expected) {
        return new Object[] { f, expected };
    }

    @SuppressWarnings("unused")
    private Object[] parametersForWhere() {
        return new Object[]{
            testCaseForWhere((a, s) -> s.where(max(Row2::description)).isEqualTo(a, Row2::name), "where max(q.DESCRIPTION) = q.NAME"),
            testCaseForWhere((a, s) -> s.where(Row2::description).isNotEqualTo(Row2::name), "where q.DESCRIPTION <> q.NAME"),
            testCaseForWhere((a, s) -> s.where(Row2::comment).isNotEqualTo(Row2::name), "where q.COMMENT <> q.NAME"),
            testCaseForWhere((a, s) -> s.where("q", Row2::description).isGreaterThan(Row2::name), "where q.DESCRIPTION > q.NAME"),
            testCaseForWhere((a, s) -> s.where("q", Row2::comment).isEqualTo(Row2::name), "where q.COMMENT = q.NAME"),
            testCaseForWhere((a, s) -> s.where(a, Row2::description).isEqualTo(Row2::name), "where q.DESCRIPTION = q.NAME"),
            testCaseForWhere((a, s) -> s.where(a, Row2::comment).isEqualTo(Row2::name), "where q.COMMENT = q.NAME"),
        };
    }

    @Test
    @Parameters
    public void where(BiFunction<Alias<Row2>,ExpectingJoin1<Row2>,InWhereExpectingAnd<Row2>> where, String expected) {
        Database database = Database.newBuilder().defaultSchema("TEST").build();

        Alias<Row2> alias = database.table(Row2.class).as("q");
        where.apply(alias, database.from(Row2.class, "q")).list(sqlExecutor);

        verify(sqlExecutor).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select q.NAME as q_NAME, q.DESCRIPTION as q_DESCRIPTION, q.COMMENT as q_COMMENT from TEST.ROW2 as q " + expected));
        assertThat(args.getValue(), arrayWithSize(0));
    }

    private Object[] testCaseForOrderByOnSelect(BiFunction<Alias<Row2>,ExpectingOrderBy<Row2>,InOrderByExpectingThen<Row2>> f, String expected) {
        return new Object[] { f, expected };
    }

    @SuppressWarnings("unused")
    private Object[] parametersForOrderByOnSelect() {
        return new Object[]{
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(max(Row2::description)), "max(q.DESCRIPTION) ascending"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(Row2::description), "q.DESCRIPTION ascending"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(Row2::comment), "q.COMMENT ascending"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy("q", Row2::description), "q.DESCRIPTION ascending"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy("q", Row2::comment), "q.COMMENT ascending"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(a, Row2::description), "q.DESCRIPTION ascending"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(a, Row2::comment), "q.COMMENT ascending"),

            testCaseForOrderByOnSelect((a, w) -> w.orderBy(min(Row2::comment), Order.DESCENDING), "min(q.COMMENT) descending"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(Row2::name, Order.ASCENDING), "q.NAME ascending"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(Row2::name, Order.DESCENDING), "q.NAME descending"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(Row2::comment, Order.DESCENDING), "q.COMMENT descending"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy("q", Row2::name, Order.ASCENDING), "q.NAME ascending"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy("q", Row2::name, Order.DESCENDING), "q.NAME descending"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy("q", Row2::comment, Order.DESCENDING), "q.COMMENT descending"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(a, Row2::name, Order.DESCENDING), "q.NAME descending"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(a, Row2::comment, Order.DESCENDING), "q.COMMENT descending"),

            testCaseForOrderByOnSelect((a, w) -> w.orderBy(Row2::name).then(max(Row2::description)), "q.NAME ascending, max(q.DESCRIPTION) ascending"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(Row2::name).then(Row2::description), "q.NAME ascending, q.DESCRIPTION ascending"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(Row2::name).then(Row2::comment), "q.NAME ascending, q.COMMENT ascending"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(Row2::name).then("q", Row2::description), "q.NAME ascending, q.DESCRIPTION ascending"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(Row2::name).then("q", Row2::comment), "q.NAME ascending, q.COMMENT ascending"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(Row2::name).then(a, Row2::description), "q.NAME ascending, q.DESCRIPTION ascending"),
            testCaseForOrderByOnSelect((a, w) -> w.orderBy(Row2::name).then(a, Row2::comment), "q.NAME ascending, q.COMMENT ascending"),
        };
    }

    @Test
    @Parameters
    public void orderByOnSelect(BiFunction<Alias<Row2>,ExpectingJoin1<Row2>,InOrderByExpectingThen<Row2>> orderBy, String expected) {
        Database database = Database.newBuilder().defaultSchema("TEST").build();
        Alias<Row2> alias = database.table(Row2.class).as("q");

        orderBy.apply(alias, database.from(alias)).optional(sqlExecutor);

        verify(sqlExecutor).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select q.NAME as q_NAME, q.DESCRIPTION as q_DESCRIPTION, q.COMMENT as q_COMMENT from TEST.ROW2 as q order by " + expected));
        assertThat(args.getValue(), arrayWithSize(0));
    }

    private Object[] testCaseForOrderByOnWhereClause(BiFunction<Alias<Row2>,InWhereExpectingAnd<Row2>,InOrderByExpectingThen<Row2>> f, String expected) {
        return new Object[] { f, expected };
    }

    @SuppressWarnings("unused")
    private Object[] parametersForOrderByOnWhereClause() {
        return new Object[]{
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(max(Row2::description)), "max(q.DESCRIPTION) ascending"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(Row2::description), "q.DESCRIPTION ascending"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(Row2::comment), "q.COMMENT ascending"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy("q", Row2::description), "q.DESCRIPTION ascending"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy("q", Row2::comment), "q.COMMENT ascending"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(a, Row2::description), "q.DESCRIPTION ascending"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(a, Row2::comment), "q.COMMENT ascending"),

            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(min(Row2::comment), Order.DESCENDING), "min(q.COMMENT) descending"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(Row2::name, Order.ASCENDING), "q.NAME ascending"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(Row2::name, Order.DESCENDING), "q.NAME descending"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(Row2::comment, Order.DESCENDING), "q.COMMENT descending"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy("q", Row2::name, Order.ASCENDING), "q.NAME ascending"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy("q", Row2::name, Order.DESCENDING), "q.NAME descending"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy("q", Row2::comment, Order.DESCENDING), "q.COMMENT descending"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(a, Row2::name, Order.DESCENDING), "q.NAME descending"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(a, Row2::comment, Order.DESCENDING), "q.COMMENT descending"),

            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(Row2::name).then(max(Row2::description)), "q.NAME ascending, max(q.DESCRIPTION) ascending"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(Row2::name).then(Row2::description), "q.NAME ascending, q.DESCRIPTION ascending"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(Row2::name).then(Row2::comment), "q.NAME ascending, q.COMMENT ascending"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(Row2::name).then("q", Row2::description), "q.NAME ascending, q.DESCRIPTION ascending"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(Row2::name).then("q", Row2::comment), "q.NAME ascending, q.COMMENT ascending"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(Row2::name).then(a, Row2::description), "q.NAME ascending, q.DESCRIPTION ascending"),
            testCaseForOrderByOnWhereClause((a, w) -> w.orderBy(Row2::name).then(a, Row2::comment), "q.NAME ascending, q.COMMENT ascending"),
        };
    }

    @Test
    @Parameters
    public void orderByOnWhereClause(BiFunction<Alias<Row2>,InWhereExpectingAnd<Row2>,InOrderByExpectingThen<Row2>> orderBy, String expected) {
        Database database = Database.newBuilder().defaultSchema("TEST").build();
        Alias<Row2> alias = database.table(Row2.class).as("q");

        orderBy.apply(alias, database.from(alias).where(Row2::name).isEqualTo("joe")).optional(sqlExecutor);

        verify(sqlExecutor).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select q.NAME as q_NAME, q.DESCRIPTION as q_DESCRIPTION, q.COMMENT as q_COMMENT from TEST.ROW2 as q where q.NAME = ? order by " + expected));
        assertThat(args.getValue(), arrayContaining("joe"));
    }

    @Test
    public void optionalOfNoRowsIsEmpty() throws Exception {
        Database database = Database.newBuilder().defaultSchema("TEST").build();
        when(sqlExecutor.query(any(), any(), any())).thenReturn(ImmutableList.of());

        Optional<Row1> result = database.from(Row1.class, "w")
            .optional(sqlExecutor);

        assertThat(result, is(Optional.empty()));
    }

    @Test
    public void optionalOfOneRowIsRow() throws Exception {
        Database database = Database.newBuilder().defaultSchema("TEST").build();
        Row1 row1 = new Row1();
        when(sqlExecutor.query(any(), any(), any())).thenReturn(ImmutableList.of(row1));

        Optional<Row1> result = database.from(Row1.class, "w")
            .optional(sqlExecutor);

        verify(sqlExecutor).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(result, is(Optional.of(row1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void optionalOfTwoRowsIsException() throws Exception {
        Database database = Database.newBuilder().defaultSchema("TEST").build();
        Row1 row1 = new Row1();
        when(sqlExecutor.query(any(), any(), any())).thenReturn(ImmutableList.of(row1, row1));

        Optional<Row1> result = database.from(Row1.class, "w")
            .optional(sqlExecutor);

        verify(sqlExecutor).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(result, is(Optional.of(row1)));
    }

    @Test
    public void list() throws Exception {
        Database database = Database.newBuilder().defaultSchema("TEST").build();
        Row1 row1 = new Row1();
        Row1 row2 = new Row1();
        when(sqlExecutor.query(any(), any(), any())).thenReturn(ImmutableList.of(row1, row2));

        List<Row1> result = database.from(Row1.class, "w").list(sqlExecutor);

        assertThat(result, contains(row1, row2));
    }
}
