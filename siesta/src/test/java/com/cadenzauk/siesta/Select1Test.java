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

import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.siesta.grammar.select.ExpectingJoin1;
import com.cadenzauk.siesta.grammar.select.ExpectingJoin2;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsArrayWithSize.arrayWithSize;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class Select1Test {
    @Mock
    private Transaction transaction;

    @Captor
    private ArgumentCaptor<String> sql;

    @Captor
    private ArgumentCaptor<Object[]> args;

    @Captor
    private ArgumentCaptor<RowMapper<?>> rowMapper;

    private static Arguments testCaseForJoin(BiFunction<Alias<Child>,ExpectingJoin1<Parent>,ExpectingJoin2<Parent,Child>> f, String expected) {
        return Arguments.of(f, expected);
    }

    private static Stream<Arguments> parametersForJoin() {
        return Stream.of(
            testCaseForJoin((c, s) -> s.join(c).on(Parent::id).isEqualTo(Child::parentId), "join SIESTA.CHILD c on p.ID = c.PARENT_ID"),
            testCaseForJoin((c, s) -> s.join(Child.class, "c").on(Parent::id).isEqualTo(Child::parentId), "join SIESTA.CHILD c on p.ID = c.PARENT_ID"),
            testCaseForJoin((c, s) -> s.join(Child.class, "c").on(Child::aliasId).isEqualTo(Parent::id), "join SIESTA.CHILD c on c.ALIAS_ID = p.ID"),
            testCaseForJoin((c, s) -> s.join(Child.class, "c").on(c, Child::parentId).isEqualTo(Parent::id), "join SIESTA.CHILD c on c.PARENT_ID = p.ID"),
            testCaseForJoin((c, s) -> s.join(Child.class, "c").on(c, Child::aliasId).isEqualTo(Parent::id), "join SIESTA.CHILD c on c.ALIAS_ID = p.ID"),

            testCaseForJoin((c, s) -> s.leftJoin(c).on(Parent::id).isEqualTo(Child::parentId), "left join SIESTA.CHILD c on p.ID = c.PARENT_ID"),
            testCaseForJoin((c, s) -> s.leftJoin(Child.class, "c").on(Child::aliasId).isEqualTo(Parent::id), "left join SIESTA.CHILD c on c.ALIAS_ID = p.ID"),
            testCaseForJoin((c, s) -> s.leftJoin(Child.class, "c").on("c", Child::parentId).isEqualTo(Parent::id), "left join SIESTA.CHILD c on c.PARENT_ID = p.ID"),
            testCaseForJoin((c, s) -> s.leftJoin(Child.class, "c").on("c", Child::aliasId).isEqualTo(Parent::id), "left join SIESTA.CHILD c on c.ALIAS_ID = p.ID"),

            testCaseForJoin((c, s) -> s.rightJoin(c).on(Parent::id).isEqualTo(Child::parentId), "right join SIESTA.CHILD c on p.ID = c.PARENT_ID"),
            testCaseForJoin((c, s) -> s.rightJoin(Child.class, "c").on(Parent::id).isEqualTo(Child::parentId), "right join SIESTA.CHILD c on p.ID = c.PARENT_ID"),

            testCaseForJoin((c, s) -> s.fullOuterJoin(c).on(Parent::id).isEqualTo(Child::parentId), "full outer join SIESTA.CHILD c on p.ID = c.PARENT_ID"),
            testCaseForJoin((c, s) -> s.fullOuterJoin(Child.class, "c").on(Parent::id).isEqualTo(Child::parentId), "full outer join SIESTA.CHILD c on p.ID = c.PARENT_ID")
        );
    }

    @ParameterizedTest
    @MethodSource("parametersForJoin")
    void join(BiFunction<Alias<Child>,ExpectingJoin1<Parent>,ExpectingJoin2<Parent,Child>> join, String expected) {
        MockitoAnnotations.initMocks(this);

        Database database = Database.newBuilder().defaultSchema("SIESTA").build();
        Alias<Parent> p = database.table(Parent.class).as("p");
        Alias<Child> c = database.table(Child.class).as("c");

        join.apply(c, database.from(p)).optional(transaction);

        verify(transaction).query(sql.capture(), args.capture(), rowMapper.capture());
        assertThat(sql.getValue(), is("select p.ID as p_ID, c.PARENT_ID as c_PARENT_ID, c.ALIAS_ID as c_ALIAS_ID from SIESTA.PARENT p " + expected));
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
