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

package com.cadenzauk.siesta.catalog;

import com.cadenzauk.core.stream.StreamUtil;
import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.RegularTableAlias;
import com.cadenzauk.siesta.dialect.AnsiDialect;
import org.junit.jupiter.api.Test;

import javax.persistence.AttributeOverride;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class TableBuilderTest {
    @Test
    void catalogFromDefault() {
        Database database = Database.newBuilder()
            .defaultCatalog("DEFAULT_CATALOG")
            .dialect(new AnsiDialect() {
                @Override
                public String qualifiedTableName(String catalog, String schema, String name) {
                    return Stream.of(catalog, schema, name)
                        .map(OptionalUtil::ofBlankable)
                        .flatMap(StreamUtil::of)
                        .collect(joining("."));
                }
            })
            .build();

        Table<NameInAnnotation> result = database.table(NameInAnnotation.class);

        assertThat(result.qualifiedName(), is("DEFAULT_CATALOG.NAME_IN_ANNOTATION"));
    }

    @Test
    void catalogFromBuilder() {
        Database database = Database.newBuilder()
            .defaultCatalog("DEFAULT_CATALOG")
            .table(CatalogInAnnotation.class, t -> t.catalog("BUILDER_CATALOG"))
            .dialect(new AnsiDialect() {
                @Override
                public String qualifiedTableName(String catalog, String schema, String name) {
                    return Stream.of(catalog, schema, name)
                        .map(OptionalUtil::ofBlankable)
                        .flatMap(StreamUtil::of)
                        .collect(joining("."));
                }
            })
            .build();

        Table<CatalogInAnnotation> result = database.table(CatalogInAnnotation.class);

        assertThat(result.qualifiedName(), is("BUILDER_CATALOG.CATALOG_IN_ANNOTATION"));
    }

    @Test
    void catalogFromAnnotation() {
        Database database = Database.newBuilder()
            .defaultCatalog("DEFAULT_CATALOG")
            .dialect(new AnsiDialect() {
                @Override
                public String qualifiedTableName(String catalog, String schema, String name) {
                    return Stream.of(catalog, schema, name)
                        .map(OptionalUtil::ofBlankable)
                        .flatMap(StreamUtil::of)
                        .collect(joining("."));
                }
            })
            .build();

        Table<CatalogInAnnotation> result = database.table(CatalogInAnnotation.class);

        assertThat(result.qualifiedName(), is("ANNOTATION_CATALOG.CATALOG_IN_ANNOTATION"));
    }

    @Test
    void schemaFromDefault() {
        Database database = Database.newBuilder()
            .defaultSchema("DEFAULT_SCHEMA")
            .build();

        Table<NameInAnnotation> result = database.table(NameInAnnotation.class);

        assertThat(result.qualifiedName(), is("DEFAULT_SCHEMA.NAME_IN_ANNOTATION"));
    }

    @Test
    void schemaFromBuilder() {
        Database database = Database.newBuilder()
            .defaultSchema("DEFAULT_SCHEMA")
            .table(SchemaInAnnotation.class, t -> t.schema("BUILDER_SCHEMA"))
            .build();

        Table<SchemaInAnnotation> result = database.table(SchemaInAnnotation.class);

        assertThat(result.qualifiedName(), is("BUILDER_SCHEMA.SCHEMA_IN_ANNOTATION"));
    }

    @Test
    void schemaFromAnnotation() {
        Database database = Database.newBuilder()
            .defaultSchema("DEFAULT_SCHEMA")
            .build();

        Table<SchemaInAnnotation> result = database.table(SchemaInAnnotation.class);

        assertThat(result.qualifiedName(), is("ANNOTATION_SCHEMA.SCHEMA_IN_ANNOTATION"));
    }

    @Test
    void schemaFromParentAnnotation() {
        Database database = Database.newBuilder()
            .defaultSchema("DEFAULT_SCHEMA")
            .build();

        Table<SchemaInParentAnnotation> result = database.table(SchemaInParentAnnotation.class);

        assertThat(result.qualifiedName(), is("ANNOTATION_SCHEMA.SCHEMA_IN_PARENT_ANNOTATION"));
    }

    @Test
    void givenSchemasInClassAndParentThenDerivedOneUsed() {
        Database database = Database.newBuilder()
            .defaultSchema("DEFAULT_SCHEMA")
            .build();

        Table<SchemaInAnnotationAndParentAnnotation> result = database.table(SchemaInAnnotationAndParentAnnotation.class);

        assertThat(result.qualifiedName(), is("DERIVED_SCHEMA.SCHEMA_IN_ANNOTATION_AND_PARENT_ANNOTATION"));
    }

    @Test
    void givenAnnotationWithoutSchemaButSchemaOnParentThenParentIsUsed() {
        Database database = Database.newBuilder()
            .defaultSchema("DEFAULT_SCHEMA")
            .build();

        Table<NameInAnnotationAndSchemaInParentAnnotation> result = database.table(NameInAnnotationAndSchemaInParentAnnotation.class);

        assertThat(result.qualifiedName(), is("ANNOTATION_SCHEMA.DERIVED_NAME_IN_ANNOTATION"));
    }

    @Test
    void nameFromClassName() {
        Database database = Database.newBuilder()
            .defaultSchema("DEFAULT_SCHEMA")
            .table(NameInAnnotation.class, t -> t.tableName("NAME_IN_BUILDER"))
            .build();

        Table<NoAnnotations> result = database.table(NoAnnotations.class);

        assertThat(result.qualifiedName(), is("DEFAULT_SCHEMA.NO_ANNOTATIONS"));
    }

    @Test
    void nameFromBuilder() {
        Database database = Database.newBuilder()
            .defaultSchema("DEFAULT_SCHEMA")
            .table(NameInAnnotation.class, t -> t.tableName("NAME_IN_BUILDER"))
            .build();

        Table<NameInAnnotation> result = database.table(NameInAnnotation.class);

        assertThat(result.qualifiedName(), is("DEFAULT_SCHEMA.NAME_IN_BUILDER"));
    }

    @Test
    void nameFromAnnotation() {
        Database database = Database.newBuilder()
            .defaultSchema("DEFAULT_SCHEMA")
            .build();

        Table<NameInAnnotation> result = database.table(NameInAnnotation.class);

        assertThat(result.qualifiedName(), is("DEFAULT_SCHEMA.NAME_IN_ANNOTATION"));
    }

    @Test
    void nameFromParentAnnotation() {
        Database database = Database.newBuilder()
            .defaultSchema("DEFAULT_SCHEMA")
            .build();

        Table<NameInParentAnnotation> result = database.table(NameInParentAnnotation.class);

        assertThat(result.qualifiedName(), is("DEFAULT_SCHEMA.NAME_IN_ANNOTATION"));
    }

    @Test
    void givenAnnotationWithoutNameButNameOnParentThenParentIsUsed() {
        Database database = Database.newBuilder()
            .defaultSchema("DEFAULT_SCHEMA")
            .build();

        Table<SchemaInAnnotationAndNameInParentAnnotation> result = database.table(SchemaInAnnotationAndNameInParentAnnotation.class);

        assertThat(result.qualifiedName(), is("DERIVED_SCHEMA_IN_ANNOTATION.NAME_IN_ANNOTATION"));
    }

    @Test
    void idFromBuilderMandatory() {
        Database database = Database.newBuilder()
            .defaultSchema("DEFAULT_SCHEMA")
            .table(NoAnnotations.class,
                t -> t.column(NoAnnotations::id, c -> c.identifier(true)))
            .build();
        Alias<NoAnnotations> alias = RegularTableAlias.of(database.table(NoAnnotations.class));

        String idSql = database.table(NoAnnotations.class)
            .columns()
            .flatMap(c -> c.idSql(alias))
            .collect(joining(" and "));

        assertThat(idSql, is("DEFAULT_SCHEMA.NO_ANNOTATIONS.ID = ?"));
    }

    @Test
    void idFromBuilderOptional() {
        Database database = Database.newBuilder()
            .defaultSchema("DEFAULT_SCHEMA")
            .table(NoAnnotations.class, t -> t
                .column(NoAnnotations::name, c -> c.identifier(true)))
            .build();
        Alias<NoAnnotations> alias = database.table(NoAnnotations.class).as("z");

        String idSql = database.table(NoAnnotations.class)
            .columns()
            .flatMap(c -> c.idSql(alias))
            .collect(joining(" and "));

        assertThat(idSql, is("z.NAME = ?"));
    }

    @Test
    void idFromAnnotation() {
        Database database = Database.newBuilder()
            .defaultSchema("DEFAULT_SCHEMA")
            .build();
        Alias<IdAnnotation> alias = RegularTableAlias.of(database.table(IdAnnotation.class));

        String idSql = database.table(IdAnnotation.class)
            .columns()
            .flatMap(c -> c.idSql(alias))
            .collect(joining(" and "));

        assertThat(idSql, is("DEFAULT_SCHEMA.ID_ANNOTATION.ID = ?"));
    }

    @Test
    void embeddedIdFromBuilder() {
        Database database = Database.newBuilder()
            .defaultSchema("DEFAULT_SCHEMA")
            .table(EmbeddedIdNoAnnotation.class, t -> t
                .embedded(Key.class, EmbeddedIdNoAnnotation::key, c -> c
                    .identifier(true)))
            .build();
        Alias<EmbeddedIdNoAnnotation> alias = RegularTableAlias.of(database.table(EmbeddedIdNoAnnotation.class));

        String idSql = database.table(EmbeddedIdNoAnnotation.class)
            .columns()
            .flatMap(c -> c.idSql(alias))
            .collect(joining(" and "));

        assertThat(idSql, is("DEFAULT_SCHEMA.EMBEDDED_ID_NO_ANNOTATION.KEY_IDENTIFIER = ? and DEFAULT_SCHEMA.EMBEDDED_ID_NO_ANNOTATION.KEY_VERSION = ?"));
    }

    @Test
    void embeddedIdFromAnnotation() {
        Database database = Database.newBuilder()
            .defaultSchema("DEFAULT_SCHEMA")
            .build();
        Alias<EmbeddedIdAnnotation> alias = database.table(EmbeddedIdAnnotation.class).as("t");

        String idSql = database.table(EmbeddedIdAnnotation.class)
            .columns()
            .flatMap(c -> c.idSql(alias))
            .collect(joining(" and "));

        assertThat(idSql, is("t.KEY_IDENTIFIER = ? and t.KEY_VERSION = ?"));
    }

    @Test
    void notInsertableFromBuilder() {
        Database database = Database.newBuilder()
            .defaultSchema("DEFAULT_SCHEMA")
            .table(NoAnnotations.class, t -> t
                .column(NoAnnotations::updateTime, c -> c
                    .insertable(false)))
            .build();

        String insertSql = database.table(NoAnnotations.class)
            .columns()
            .flatMap(Column::insertColumnSql)
            .collect(joining(", "));

        assertThat(database.table(NoAnnotations.class).column(NoAnnotations::id).insertable(), is(true));
        assertThat(database.table(NoAnnotations.class).column(NoAnnotations::name).insertable(), is(true));
        assertThat(database.table(NoAnnotations.class).column(NoAnnotations::updateTime).insertable(), is(false));
        assertThat(database.table(NoAnnotations.class).column(NoAnnotations::insertTime).insertable(), is(true));
        assertThat(insertSql, is("ID, NAME, INSERT_TIME"));
    }

    @Test
    void notInsertableFromAnnotation() {
        Database database = Database.newBuilder()
            .defaultSchema("DEFAULT_SCHEMA")
            .build();

        String insertSql = database.table(IdAnnotation.class)
            .columns()
            .flatMap(Column::insertColumnSql)
            .collect(joining(", "));

        assertThat(database.table(IdAnnotation.class).column(IdAnnotation::id).insertable(), is(true));
        assertThat(database.table(IdAnnotation.class).column(IdAnnotation::name).insertable(), is(true));
        assertThat(database.table(IdAnnotation.class).column(IdAnnotation::updateTime).insertable(), is(false));
        assertThat(database.table(IdAnnotation.class).column(IdAnnotation::insertTime).insertable(), is(true));
        assertThat(insertSql, is("ID, NAME, INSERT_TIME"));
    }

    @Test
    void embeddedNotInsertableFromBuilder() {
        Database database = Database.newBuilder()
            .defaultSchema("DEFAULT_SCHEMA")
            .table(EmbeddedColumnsNoAnnotations.class, t -> t
                .embedded(EmbeddableNoAnnotations.class, EmbeddedColumnsNoAnnotations::gross, c -> c
                    .insertable(false))
                .embedded(EmbeddableNoAnnotations.class, EmbeddedColumnsNoAnnotations::net))
            .build();

        String insertSql = database.table(EmbeddedColumnsNoAnnotations.class)
            .columns()
            .flatMap(Column::insertColumnSql)
            .collect(joining(", "));

        assertThat(database.table(EmbeddedColumnsNoAnnotations.class).column(EmbeddedColumnsNoAnnotations::gross).insertable(), is(false));
        assertThat(database.table(EmbeddedColumnsNoAnnotations.class).column(EmbeddedColumnsNoAnnotations::net).insertable(), is(true));
        assertThat(insertSql, is("NET_AMOUNT, NET_UNIT"));
    }

    @Test
    void embeddedNotInsertableFromBuilderOverride() {
        Database database = Database.newBuilder()
            .defaultSchema("DEFAULT_SCHEMA")
            .table(EmbeddedColumnsNoAnnotations.class, t -> t
                .embedded(EmbeddableNoAnnotations.class, EmbeddedColumnsNoAnnotations::gross, c -> c
                    .column(EmbeddableNoAnnotations::amount, o -> o.insertable(false)))
                .embedded(EmbeddableNoAnnotations.class, EmbeddedColumnsNoAnnotations::net, c -> c
                    .column(EmbeddableNoAnnotations::unit, o -> o.insertable(false))))
            .build();

        String insertSql = database.table(EmbeddedColumnsNoAnnotations.class)
            .columns()
            .flatMap(Column::insertColumnSql)
            .collect(joining(", "));

        assertThat(database.table(EmbeddedColumnsNoAnnotations.class).column(EmbeddedColumnsNoAnnotations::gross).insertable(), is(true));
        assertThat(database.table(EmbeddedColumnsNoAnnotations.class).column(EmbeddedColumnsNoAnnotations::net).insertable(), is(true));
        assertThat(insertSql, is("GROSS_UNIT, NET_AMOUNT"));
    }

    @Test
    void embeddedNotInsertableFromAnnotationOverride() {
        Database database = Database.newBuilder()
            .defaultSchema("DEFAULT_SCHEMA")
            .build();

        String insertSql = database.table(EmbeddedColumnsInsertOverride.class)
            .columns()
            .flatMap(Column::insertColumnSql)
            .collect(joining(", "));

        assertThat(database.table(EmbeddedColumnsInsertOverride.class).column(EmbeddedColumnsInsertOverride::gross).insertable(), is(true));
        assertThat(database.table(EmbeddedColumnsInsertOverride.class).column(EmbeddedColumnsInsertOverride::net).insertable(), is(true));
        assertThat(insertSql, is("GROSS_AMOUNT, GROSS_UNIT, NET_AMOUNT"));
    }

    @Test
    void notUpdatableFromBuilder() {
        Database database = Database.newBuilder()
            .defaultSchema("DEFAULT_SCHEMA")
            .table(NoAnnotations.class, t -> t
                .column(NoAnnotations::updateTime, c -> c
                    .updatable(false)))
            .build();

        String insertSql = database.table(NoAnnotations.class)
            .columns()
            .flatMap(Column::updateSql)
            .collect(joining(", "));

        assertThat(database.table(NoAnnotations.class).column(NoAnnotations::id).updatable(), is(true));
        assertThat(database.table(NoAnnotations.class).column(NoAnnotations::name).updatable(), is(true));
        assertThat(database.table(NoAnnotations.class).column(NoAnnotations::updateTime).updatable(), is(false));
        assertThat(database.table(NoAnnotations.class).column(NoAnnotations::insertTime).updatable(), is(true));
        assertThat(insertSql, is("ID = ?, NAME = ?, INSERT_TIME = ?"));
    }

    @Test
    void notUpdatableFromAnnotation() {
        Database database = Database.newBuilder()
            .defaultSchema("DEFAULT_SCHEMA")
            .build();

        String updateSql = database.table(IdAnnotation.class)
            .columns()
            .flatMap(Column::updateSql)
            .collect(joining(", "));

        assertThat(database.table(IdAnnotation.class).column(IdAnnotation::id).updatable(), is(false));
        assertThat(database.table(IdAnnotation.class).column(IdAnnotation::name).updatable(), is(true));
        assertThat(database.table(IdAnnotation.class).column(IdAnnotation::insertTime).updatable(), is(false));
        assertThat(database.table(IdAnnotation.class).column(IdAnnotation::updateTime).updatable(), is(true));
        assertThat(updateSql, is("NAME = ?, UPDATE_TIME = ?"));
    }

    @Test
    void embeddedNotUpdatableFromBuilder() {
        Database database = Database.newBuilder()
            .defaultSchema("DEFAULT_SCHEMA")
            .table(EmbeddedColumnsNoAnnotations.class, t -> t
                .embedded(EmbeddableNoAnnotations.class, EmbeddedColumnsNoAnnotations::gross, c -> c
                    .updatable(false))
                .embedded(EmbeddableNoAnnotations.class, EmbeddedColumnsNoAnnotations::net))
            .build();

        String updateSql = database.table(EmbeddedColumnsNoAnnotations.class)
            .columns()
            .flatMap(Column::updateSql)
            .collect(joining(", "));

        assertThat(database.table(EmbeddedColumnsNoAnnotations.class).column(EmbeddedColumnsNoAnnotations::gross).updatable(), is(false));
        assertThat(database.table(EmbeddedColumnsNoAnnotations.class).column(EmbeddedColumnsNoAnnotations::net).updatable(), is(true));
        assertThat(updateSql, is("NET_AMOUNT = ?, NET_UNIT = ?"));
    }

    @Test
    void embeddedNotUpdatableFromBuilderOverride() {
        Database database = Database.newBuilder()
            .defaultSchema("DEFAULT_SCHEMA")
            .table(EmbeddedColumnsNoAnnotations.class, t -> t
                .embedded(EmbeddableNoAnnotations.class, EmbeddedColumnsNoAnnotations::gross)
                .embedded(EmbeddableNoAnnotations.class, EmbeddedColumnsNoAnnotations::net, c -> c
                    .column(EmbeddableNoAnnotations::amount, o -> o.updatable(false))))
            .build();

        Table<EmbeddedColumnsNoAnnotations> table = database.table(EmbeddedColumnsNoAnnotations.class);
        String updateSql = table
            .columns()
            .flatMap(Column::updateSql)
            .collect(joining(", "));

        assertThat(table.column(EmbeddedColumnsNoAnnotations::gross).updatable(), is(true));
        assertThat(table.column(EmbeddedColumnsNoAnnotations::net).updatable(), is(true));
        assertThat(table.embedded(EmbeddedColumnsNoAnnotations::gross).column(EmbeddableNoAnnotations::amount).updatable(), is(true));
        assertThat(table.embedded(EmbeddedColumnsNoAnnotations::gross).column(EmbeddableNoAnnotations::unit).updatable(), is(true));
        assertThat(table.embedded(EmbeddedColumnsNoAnnotations::net).column(EmbeddableNoAnnotations::amount).updatable(), is(false));
        assertThat(table.embedded(EmbeddedColumnsNoAnnotations::net).column(EmbeddableNoAnnotations::unit).updatable(), is(true));
        assertThat(updateSql, is("GROSS_AMOUNT = ?, GROSS_UNIT = ?, NET_UNIT = ?"));
    }

    @Test
    void embeddedNotUpdatableFromAnnotationOverride() {
        Database database = Database.newBuilder()
            .defaultSchema("DEFAULT_SCHEMA")
            .build();

        Table<EmbeddedColumnsInsertOverride> table = database.table(EmbeddedColumnsInsertOverride.class);
        String updateSql = table
            .columns()
            .flatMap(Column::updateSql)
            .collect(joining(", "));

        assertThat(table.column(EmbeddedColumnsInsertOverride::gross).updatable(), is(true));
        assertThat(table.column(EmbeddedColumnsInsertOverride::net).updatable(), is(true));
        assertThat(table.embedded(EmbeddedColumnsInsertOverride::gross).column(EmbeddableNoAnnotations::amount).updatable(), is(false));
        assertThat(table.embedded(EmbeddedColumnsInsertOverride::gross).column(EmbeddableNoAnnotations::unit).updatable(), is(true));
        assertThat(table.embedded(EmbeddedColumnsInsertOverride::net).column(EmbeddableNoAnnotations::amount).updatable(), is(true));
        assertThat(table.embedded(EmbeddedColumnsInsertOverride::net).column(EmbeddableNoAnnotations::unit).updatable(), is(true));
        assertThat(updateSql, is("GROSS_UNIT = ?, NET_AMOUNT = ?, NET_UNIT = ?"));
    }

    @SuppressWarnings("unused")
    private static class NoAnnotations {
        private int id;
        private Optional<String> name;
        private Optional<ZonedDateTime> updateTime;
        private Optional<ZonedDateTime> insertTime;

        public int id() {
            return id;
        }

        public Optional<String> name() {
            return name;
        }

        public Optional<ZonedDateTime> updateTime() {
            return updateTime;
        }

        public Optional<ZonedDateTime> insertTime() {
            return insertTime;
        }
    }

    @javax.persistence.Table(catalog = "ANNOTATION_CATALOG")
    private static class CatalogInAnnotation {
    }

    @javax.persistence.Table(schema = "ANNOTATION_SCHEMA")
    private static class SchemaInAnnotation {
    }

    @SuppressWarnings("unused")
    @javax.persistence.Table(name = "NAME_IN_ANNOTATION")
    private static class NameInAnnotation {
    }

    private static class SchemaInParentAnnotation extends SchemaInAnnotation {
    }

    @javax.persistence.Table(schema = "DERIVED_SCHEMA")
    private static class SchemaInAnnotationAndParentAnnotation extends SchemaInAnnotation {
    }

    @javax.persistence.Table(name = "DERIVED_NAME_IN_ANNOTATION")
    private static class NameInAnnotationAndSchemaInParentAnnotation extends SchemaInAnnotation {
    }

    private static class NameInParentAnnotation extends NameInAnnotation {
    }

    @javax.persistence.Table(schema = "DERIVED_SCHEMA_IN_ANNOTATION")
    private static class SchemaInAnnotationAndNameInParentAnnotation extends NameInAnnotation {
    }

    @SuppressWarnings("unused")
    private static class IdAnnotation {
        @Id
        private int id;
        private String name;
        @javax.persistence.Column(insertable = false)
        private Optional<ZonedDateTime> updateTime;
        @javax.persistence.Column(updatable = false)
        private Optional<ZonedDateTime> insertTime;

        public int id() {
            return id;
        }

        public String name() {
            return name;
        }

        public Optional<ZonedDateTime> updateTime() {
            return updateTime;
        }

        public Optional<ZonedDateTime> insertTime() {
            return insertTime;
        }
    }

    @SuppressWarnings("unused")
    private static class Key {
        private long identifier;
        private int version;

        public long identifier() {
            return identifier;
        }

        public int version() {
            return version;
        }
    }

    @SuppressWarnings("unused")
    private static class EmbeddedIdNoAnnotation {
        private Key key;
        private Optional<String> description;

        public Key key() {
            return key;
        }

        public Optional<String> description() {
            return description;
        }
    }

    @SuppressWarnings("unused")
    private static class EmbeddedIdAnnotation {
        @EmbeddedId
        private Key key;
        private Optional<String> description;

        public Key key() {
            return key;
        }

        public Optional<String> description() {
            return description;
        }
    }

    @SuppressWarnings("unused")
    private static class EmbeddableNoAnnotations {
        private BigDecimal amount;
        private Optional<String> unit;

        public BigDecimal amount() {
            return amount;
        }

        public Optional<String> unit() {
            return unit;
        }
    }

    @SuppressWarnings("unused")
    private static class EmbeddedColumnsNoAnnotations {
        private EmbeddableNoAnnotations gross;
        private Optional<EmbeddableNoAnnotations> net;

        public EmbeddableNoAnnotations gross() {
            return gross;
        }

        public Optional<EmbeddableNoAnnotations> net() {
            return net;
        }
    }

    @SuppressWarnings("unused")
    private static class EmbeddedColumnsInsertOverride {
        @Embedded
        @AttributeOverride(name = "amount", column = @javax.persistence.Column(updatable = false))
        private EmbeddableNoAnnotations gross;
        @Embedded
        @AttributeOverride(name = "unit", column = @javax.persistence.Column(insertable = false))
        private EmbeddableNoAnnotations net;

        public EmbeddableNoAnnotations gross() {
            return gross;
        }

        public EmbeddableNoAnnotations net() {
            return net;
        }
    }
}
