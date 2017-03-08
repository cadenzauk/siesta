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

import com.cadenzauk.siesta.grammar.select.ExpectingJoin1;
import com.cadenzauk.siesta.grammar.select.ExpectingJoin2;
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

import java.util.Optional;
import java.util.function.BiFunction;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsArrayWithSize.arrayWithSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(JUnitParamsRunner.class)
public class Select1Test {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private SqlExecutor sqlExecutor;

    @Captor
    private ArgumentCaptor<String> sql;

    @Captor
    private ArgumentCaptor<Object[]> args;

    @Captor
    private ArgumentCaptor<RowMapper<?>> rowMapper;

    private Object[] testCaseForJoin(BiFunction<Alias<Child>,ExpectingJoin1<Parent>,ExpectingJoin2<Parent,Child>> f, String expected) {
        return new Object[] { f, expected };
    }

    @SuppressWarnings("unused")
    private Object[] parametersForJoin() {
        return new Object[]{
            testCaseForJoin((c, s) -> s.join(c).on(Parent::id).isEqualTo(Child::parentId), "join TEST.CHILD as c on p.ID = c.PARENT_ID"),
            testCaseForJoin((c, s) -> s.join(Child.class, "c").on(Parent::id).isEqualTo(Child::parentId), "join TEST.CHILD as c on p.ID = c.PARENT_ID"),
            testCaseForJoin((c, s) -> s.join(Child.class, "c").on(Child::aliasId).isEqualTo(Parent::id), "join TEST.CHILD as c on c.ALIAS_ID = p.ID"),
            testCaseForJoin((c, s) -> s.join(Child.class, "c").on(c, Child::parentId).isEqualTo(Parent::id), "join TEST.CHILD as c on c.PARENT_ID = p.ID"),
            testCaseForJoin((c, s) -> s.join(Child.class, "c").on(c, Child::aliasId).isEqualTo(Parent::id), "join TEST.CHILD as c on c.ALIAS_ID = p.ID"),

            testCaseForJoin((c, s) -> s.leftJoin(c).on(Parent::id).isEqualTo(Child::parentId), "left join TEST.CHILD as c on p.ID = c.PARENT_ID"),
            testCaseForJoin((c, s) -> s.leftJoin(Child.class, "c").on(Child::aliasId).isEqualTo(Parent::id), "left join TEST.CHILD as c on c.ALIAS_ID = p.ID"),
            testCaseForJoin((c, s) -> s.leftJoin(Child.class, "c").on("c", Child::parentId).isEqualTo(Parent::id), "left join TEST.CHILD as c on c.PARENT_ID = p.ID"),
            testCaseForJoin((c, s) -> s.leftJoin(Child.class, "c").on("c", Child::aliasId).isEqualTo(Parent::id), "left join TEST.CHILD as c on c.ALIAS_ID = p.ID"),

            testCaseForJoin((c, s) -> s.rightJoin(c).on(Parent::id).isEqualTo(Child::parentId), "right join TEST.CHILD as c on p.ID = c.PARENT_ID"),
            testCaseForJoin((c, s) -> s.rightJoin(Child.class, "c").on(Parent::id).isEqualTo(Child::parentId), "right join TEST.CHILD as c on p.ID = c.PARENT_ID"),

            testCaseForJoin((c, s) -> s.fullOuterJoin(c).on(Parent::id).isEqualTo(Child::parentId), "full outer join TEST.CHILD as c on p.ID = c.PARENT_ID"),
            testCaseForJoin((c, s) -> s.fullOuterJoin(Child.class, "c").on(Parent::id).isEqualTo(Child::parentId), "full outer join TEST.CHILD as c on p.ID = c.PARENT_ID"),
        };
    }

    @Test
    @Parameters
    public void join(BiFunction<Alias<Child>,ExpectingJoin1<Parent>,ExpectingJoin2<Parent,Child>> join, String expected) {
        Database database = Database.newBuilder().defaultSchema("TEST").build();
        Alias<Parent> p = database.table(Parent.class).as("p");
        Alias<Child> c = database.table(Child.class).as("c");

        join.apply(c, database.from(p)).optional(sqlExecutor);

        verify(sqlExecutor).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select p.ID as p_ID, c.PARENT_ID as c_PARENT_ID, c.ALIAS_ID as c_ALIAS_ID from TEST.PARENT as p " + expected));
        assertThat(args.getValue(), arrayWithSize(0));
    }

    @SuppressWarnings("unused")
    public static class Parent {
        private int id;

        public int id() {
            return id;
        }
    }

    @SuppressWarnings("unused")
    public static class Child {
        private int parentId;
        private Optional<Integer> aliasId;

        public int parentId() {
            return parentId;
        }

        public Optional<Integer> aliasId() {
            return aliasId;
        }
    }
}
