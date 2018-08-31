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

package com.cadenzauk.siesta;

import com.cadenzauk.siesta.grammar.InvalidForeignKeyException;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.Table;
import java.util.Optional;

import static com.cadenzauk.core.testutil.FluentAssert.calling;
import static com.cadenzauk.siesta.grammar.expression.ForeignKeyExpression.foreignKey;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ForeignKeyTest {
    @Mock
    private SqlExecutor sqlExecutor;

    @Captor
    private ArgumentCaptor<String> sqlCaptor;

    @Captor
    private ArgumentCaptor<Object[]> argsCaptor;

    @Test
    void foreignKeyJoiningFromChildToParent() {
        Database database = Database.newBuilder()
            .defaultSchema("SIESTA")
            .table(Child.class, t -> t
                .foreignKey(Parent.class, f -> f
                    .column(Child::parentId).references(Parent::id)))
            .build();

        database.from(Child.class, "c")
            .join(Parent.class, "p").onForeignKey().from(Child.class)
            .select(Child::childId)
            .comma(Parent::id)
            .optional(sqlExecutor);

        verify(sqlExecutor).query(sqlCaptor.capture(), argsCaptor.capture(), any());
        assertThat(sqlCaptor.getValue(), is("select c.CHILD_ID as c_CHILD_ID, p.ID as p_ID " +
            "from SIESTA.CHILD c " +
            "join SIESTA.PARENT p on p.ID = c.PARENT_ID"));
    }

    @Test
    void invalidNamedForeignKeyJoiningFromChildToParent() {
        Database database = Database.newBuilder()
            .defaultSchema("SIESTA")
            .table(Child.class, t -> t
                .foreignKey(Parent.class, f -> f
                    .column(Child::parentId).references(Parent::id)))
            .build();

        calling(() ->
            database.from(Child.class, "c")
                .join(Parent.class, "p").onForeignKey("NOPE").from(Child.class)
                .sql())
            .shouldThrow(InvalidForeignKeyException.class)
            .withMessage(is("No foreign key called NOPE defined from SIESTA.CHILD to SIESTA.PARENT."));
    }

    @Test
    void noForeignKeyJoiningFromChildToParent() {
        Database database = Database.newBuilder()
            .defaultSchema("SIESTA")
            .build();

        calling(() ->
            database.from(Child.class, "c")
                .join(Parent.class, "p").onForeignKey().from(Child.class)
                .sql())
            .shouldThrow(InvalidForeignKeyException.class)
            .withMessage(is("No foreign keys defined from SIESTA.CHILD to SIESTA.PARENT."));
    }

    @Test
    void foreignKeyJoiningFromAnnotatedChildToParent() {
        Database database = Database.newBuilder()
            .defaultSchema("SIESTA")
            .build();

        database.from(AnnotatedChild.class, "c")
            .join(Parent.class, "p").onForeignKey("FK_PARENT").from(AnnotatedChild.class, "c")
            .select(AnnotatedChild::childId)
            .comma(Parent::id)
            .optional(sqlExecutor);

        verify(sqlExecutor).query(sqlCaptor.capture(), argsCaptor.capture(), any());
        assertThat(sqlCaptor.getValue(), is("select c.CHILD_ID as c_CHILD_ID, p.ID as p_ID " +
            "from SIESTA.CHILD c " +
            "join SIESTA.PARENT p on p.ID = c.PARENT_ID"));
    }

    @Test
    void foreignKeyJoiningFromAnnotatedChildToParentUsingAlias() {
        Database database = Database.newBuilder()
            .defaultSchema("SIESTA")
            .build();

        Alias<AnnotatedChild> c = database.table(AnnotatedChild.class).as("c");
        database.from(c)
            .join(Parent.class, "p").onForeignKey("FK_PARENT").from(c)
            .select(AnnotatedChild::childId)
            .comma(Parent::id)
            .optional(sqlExecutor);

        verify(sqlExecutor).query(sqlCaptor.capture(), argsCaptor.capture(), any());
        assertThat(sqlCaptor.getValue(), is("select c.CHILD_ID as c_CHILD_ID, p.ID as p_ID " +
            "from SIESTA.CHILD c " +
            "join SIESTA.PARENT p on p.ID = c.PARENT_ID"));
    }

    @Test
    void foreignKeyJoiningFromChildWithMultipleForeignKeysToParent() {
        Database database = Database.newBuilder()
            .defaultSchema("SIESTA")
            .table(Child.class, t -> t
                .foreignKey(Parent.class, f -> f
                    .name("FK_PARENT")
                    .column(Child::parentId).references(Parent::id))
                .foreignKey(Parent.class, f -> f
                    .name("FK_GRANDPARENT")
                    .column(Child::grandparentId).references(Parent::id))
            )
            .build();

        database.from(Child.class, "c")
            .join(Parent.class, "p").onForeignKey("FK_PARENT").from(Child.class, "c")
            .join(Parent.class, "gp").onForeignKey("FK_GRANDPARENT").from(Child.class)
            .select(Child::childId)
            .comma("p", Parent::id)
            .comma("gp", Parent::id)
            .optional(sqlExecutor);

        verify(sqlExecutor).query(sqlCaptor.capture(), argsCaptor.capture(), any());
        assertThat(sqlCaptor.getValue(), is("select c.CHILD_ID as c_CHILD_ID, p.ID as p_ID, gp.ID as gp_ID " +
            "from SIESTA.CHILD c " +
            "join SIESTA.PARENT p on p.ID = c.PARENT_ID " +
            "join SIESTA.PARENT gp on gp.ID = c.GRANDPARENT_ID"));
    }

    @Test
    void ambiguousForeignKey() {
        Database database = Database.newBuilder()
            .defaultSchema("SIESTA")
            .table(Child.class, t -> t
                .foreignKey(Parent.class, f -> f
                    .name("FK_PARENT")
                    .column(Child::parentId).references(Parent::id))
                .foreignKey(Parent.class, f -> f
                    .name("FK_GRANDPARENT")
                    .column(Child::grandparentId).references(Parent::id))
            )
            .build();

        calling(() ->
            database.from(Child.class, "c")
                .join(Parent.class, "p").onForeignKey().from(Child.class, "c")
                .sql())
            .shouldThrow(InvalidForeignKeyException.class)
            .withMessage(is("More than one foreign key from SIESTA.CHILD to SIESTA.PARENT.  Specify the one you want with onForeignKey(<name>)."));
    }

    @Test
    void foreignKeyJoiningFromChildToEitherParent() {
        Database database = Database.newBuilder()
            .defaultSchema("SIESTA")
            .table(Child.class, t -> t
                .foreignKey(Parent.class, f -> f
                    .name("FK_PARENT")
                    .column(Child::parentId).references(Parent::id)
                    .column(Child::distributionHash).references(Parent::distributionHash))
                .foreignKey(Parent.class, f -> f
                    .name("FK_GRANDPARENT")
                    .column(Child::grandparentId).references(Parent::id)
                    .column(Child::distributionHash).references(Parent::distributionHash))
            )
            .build();
        long randomChildId = RandomUtils.nextLong();

        database.from(Child.class, "c")
            .join(Parent.class, "p")
            .on(
                foreignKey("FK_PARENT").from(Child.class).to(Parent.class)
                    .or(foreignKey("FK_GRANDPARENT").from(Child.class, "c").to(Parent.class, "p")))
            .select(Child::childId)
            .comma("p", Parent::id)
            .where(Child::childId).isEqualTo(randomChildId)
            .optional(sqlExecutor);

        verify(sqlExecutor).query(sqlCaptor.capture(), argsCaptor.capture(), any());
        assertThat(sqlCaptor.getValue(), is("select c.CHILD_ID as c_CHILD_ID, p.ID as p_ID " +
            "from SIESTA.CHILD c " +
            "join SIESTA.PARENT p on (p.ID = c.PARENT_ID and p.DISTRIBUTION_HASH = c.DISTRIBUTION_HASH " +
            "or p.ID = c.GRANDPARENT_ID and p.DISTRIBUTION_HASH = c.DISTRIBUTION_HASH) " +
            "where c.CHILD_ID = ?"));
        assertThat(argsCaptor.getValue(), arrayContaining(randomChildId));
    }

    @Test
    void foreignKeyJoiningFromParentToChild() {
        Database database = Database.newBuilder()
            .defaultSchema("SIESTA")
            .table(Child.class, t -> t
                .foreignKey(Parent.class, f -> f
                    .column(Child::parentId).references(Parent::id)))
            .build();

        database.from(Parent.class, "p")
            .join(Child.class, "c").onForeignKey().to(Parent.class)
            .select(Parent::id)
            .comma(Child::childId)
            .optional(sqlExecutor);

        verify(sqlExecutor).query(sqlCaptor.capture(), argsCaptor.capture(), any());
        assertThat(sqlCaptor.getValue(), is("select p.ID as p_ID, c.CHILD_ID as c_CHILD_ID " +
            "from SIESTA.PARENT p " +
            "join SIESTA.CHILD c on c.PARENT_ID = p.ID"));
        assertThat(argsCaptor.getValue(), emptyArray());
    }

    @Test
    void foreignKeyJoiningFromParentToChildUsingAlias() {
        Database database = Database.newBuilder()
            .defaultSchema("SIESTA")
            .table(Child.class, t -> t
                .foreignKey(Parent.class, f -> f
                    .column(Child::parentId).references(Parent::id)))
            .build();
        Alias<Parent> p = database.table(Parent.class).as("p");

        database.from(p)
            .join(Child.class, "c").onForeignKey().to(p)
            .select(Parent::id)
            .comma(Child::childId)
            .optional(sqlExecutor);

        verify(sqlExecutor).query(sqlCaptor.capture(), argsCaptor.capture(), any());
        assertThat(sqlCaptor.getValue(), is("select p.ID as p_ID, c.CHILD_ID as c_CHILD_ID " +
            "from SIESTA.PARENT p " +
            "join SIESTA.CHILD c on c.PARENT_ID = p.ID"));
        assertThat(argsCaptor.getValue(), emptyArray());
    }

    @Test
    void foreignKeyReferencingOptional() {
        Database database = Database.newBuilder()
            .defaultSchema("SIESTA")
            .table(Child.class, t -> t
                .foreignKey(Child.class, f -> f
                    .column(Child::parentId).references(Child::grandparentId)))
            .build();

        database.from(Child.class, "c1")
            .join(Child.class, "c2").on(foreignKey().from(Child.class, "c2").to(Child.class, "c1"))
            .select("c1", Child::childId)
            .comma("c2", Child::childId)
            .optional(sqlExecutor);

        verify(sqlExecutor).query(sqlCaptor.capture(), argsCaptor.capture(), any());
        assertThat(sqlCaptor.getValue(), is("select c1.CHILD_ID as c1_CHILD_ID, c2.CHILD_ID as c2_CHILD_ID " +
            "from SIESTA.CHILD c1 " +
            "join SIESTA.CHILD c2 on c1.GRANDPARENT_ID = c2.PARENT_ID"));
        assertThat(argsCaptor.getValue(), emptyArray());
    }

    @Test
    void foreignKeyWithMultipleColumns() {
        Database database = Database.newBuilder()
            .defaultSchema("SIESTA")
            .table(Child.class, t -> t
                .foreignKey(Parent.class, f -> f
                    .column(Child::parentId).references(Parent::id)
                    .column(Child::distributionHash).references(Parent::distributionHash)))
            .build();

        database.from(Parent.class, "p")
            .join(Child.class, "c").onForeignKey().to(Parent.class, "p")
            .select(Parent::id)
            .comma(Child::childId)
            .optional(sqlExecutor);

        verify(sqlExecutor).query(sqlCaptor.capture(), argsCaptor.capture(), any());
        assertThat(sqlCaptor.getValue(), is("select p.ID as p_ID, c.CHILD_ID as c_CHILD_ID " +
            "from SIESTA.PARENT p " +
            "join SIESTA.CHILD c on c.PARENT_ID = p.ID and c.DISTRIBUTION_HASH = p.DISTRIBUTION_HASH"));
        assertThat(argsCaptor.getValue(), emptyArray());
    }

    @SuppressWarnings("unused")
    private static class Parent {
        private long id;
        private long distributionHash;

        public long id() {
            return id;
        }

        public long distributionHash() {
            return distributionHash;
        }
    }

    @SuppressWarnings("unused")
    private static class Child {
        private long childId;
        private long distributionHash;
        private long parentId;
        private Optional<Long> grandparentId;

        public long childId() {
            return childId;
        }

        public long distributionHash() {
            return distributionHash;
        }

        public long parentId() {
            return parentId;
        }

        public Optional<Long> grandparentId() {
            return grandparentId;
        }
    }

    @Table(name = "CHILD")
    @ForeignKey(parent = Parent.class, name = "FK_PARENT", references = {
        @Reference(property = "parentId", parentProperty = "id")
    })
    @ForeignKey(parent = Parent.class, name = "FK_GRANDPARENT", references = {
        @Reference(property = "grandparentId", parentProperty = "id")
    })
    @SuppressWarnings("unused")
    private static class AnnotatedChild {
        private long childId;
        private long parentId;
        private long grandparentId;

        public long childId() {
            return childId;
        }
    }
}