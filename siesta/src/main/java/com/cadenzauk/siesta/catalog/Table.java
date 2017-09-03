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

package com.cadenzauk.siesta.catalog;

import com.cadenzauk.core.function.Function1;
import com.cadenzauk.core.function.FunctionOptional1;
import com.cadenzauk.core.reflect.Factory;
import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.core.reflect.util.ClassUtil;
import com.cadenzauk.core.reflect.util.FieldUtil;
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.DataType;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.DynamicRowMapper;
import com.cadenzauk.siesta.Transaction;
import com.cadenzauk.siesta.catalog.TableColumn.ResultSetValue;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class Table<R> {
    private static final Logger LOG = LoggerFactory.getLogger(Table.class);
    private final Database database;
    private final TypeToken<R> rowType;
    private final String catalog;
    private final String schema;
    private final String tableName;
    private final Impl<?> impl;

    private <B> Table(Builder<R,B> builder) {
        database = builder.database;
        rowType = builder.rowType;
        catalog = builder.catalog;
        schema = builder.schema;
        tableName = builder.tableName;
        impl = new Impl<>(builder.newBuilder, builder.buildRow, builder.columns);
    }

    public TypeToken<R> rowType() {
        return rowType;
    }

    public Database database() {
        return database;
    }

    public String schema() {
        return schema;
    }

    public String tableName() {
        return tableName;
    }

    public Stream<Column<?,R>> columns() {
        return impl.columns.stream().map(Function.identity());
    }

    public RowMapper<R> rowMapper(String s) {
        return impl.rowMapper(Optional.of(s));
    }

    public DynamicRowMapper<R> dynamicRowMapper(String s) {
        return impl.dynamicRowMapper(Optional.of(s));
    }

    public String qualifiedName() {
        return database.dialect().qualifiedTableName(catalog, schema, tableName());
    }

    public Alias<R> as(String alias) {
        return Alias.of(this, alias);
    }

    public void insert(Transaction transaction, R[] rows) {
        if (database().dialect().supportsMultiInsert()) {
            impl.insert(transaction, rows);
        } else {
            Arrays.stream(rows).forEach(r -> impl.insert(transaction, r));
        }
    }

    public <T> Column<T,R> column(MethodInfo<R,T> methodInfo) {
        String columnName = database.columnNameFor(methodInfo);
        return database.dataTypeOf(methodInfo)
            .flatMap(dataType -> findColumn(dataType, columnName))
            .orElseThrow(() -> new IllegalArgumentException("No such column as " + columnName + " in " + qualifiedName()));
    }

    private <T> Optional<Column<T,R>> findColumn(DataType<T> dataType, String columnName) {
        return columnsOfType(dataType)
            .filter(c -> StringUtils.equals(c.name(), columnName))
            .findFirst();
    }

    private <T> Stream<Column<T,R>> columnsOfType(DataType<T> dataType) {
        return columns().flatMap(c -> c.as(dataType));
    }

    private class Impl<B> {
        private final Supplier<B> newBuilder;
        private final Function<B,R> buildRow;
        private final List<TableColumn<Object,R,B>> columns;

        Impl(Supplier<B> newBuilder, Function<B,R> buildRow, List<TableColumn<Object,R,B>> columns) {
            this.newBuilder = newBuilder;
            this.buildRow = buildRow;
            this.columns = ImmutableList.copyOf(columns);
        }

        @SuppressWarnings("unchecked")
        @SafeVarargs
        final void insert(Transaction transaction, R... rows) {
            if (rows.length == 0) {
                return;
            }
            int nCols = columns.size();
            String sql = String.format("insert into %s (%s) values %s",
                qualifiedName(),
                columns.stream().map(Column::name).collect(joining(", ")),
                IntStream.range(0, rows.length)
                    .mapToObj(i -> "(" + IntStream.range(0, nCols).mapToObj(j -> "?").collect(joining(", ")) + ")")
                    .collect(joining(", ")));
            LOG.debug(sql);

            Object[] args = Arrays.stream(rows)
                .flatMap(r -> columns
                    .stream()
                    .map(c -> c.getter()
                        .apply(r)
                        .map(v -> c.dataType().toDatabase(database, v))
                        .orElse(null)))
                .toArray();

            transaction.update(sql, args);
        }

        public RowMapper<R> rowMapper() {
            return rowMapper(Optional.empty());
        }

        public RowMapper<R> rowMapper(Optional<String> prefix) {
            return rs -> {
                List<ResultSetValue<B>> values = columns.stream()
                    .map(c -> c.extract(database, rs, prefix.orElse(tableName + "_") + c.name()))
                    .collect(toList());
                return values.stream().noneMatch(ResultSetValue::isPresent)
                    ? null
                    :  build(values);
            };
        }

        private R build(List<ResultSetValue<B>> values) {
            B builder = newBuilder.get();
            values.forEach(v -> v.apply(builder));
            return buildRow.apply(builder);
        }

        public DynamicRowMapper<R> dynamicRowMapper(Optional<String> prefix) {
            return new DynamicRowMapper<R>() {
                private final Set<String> labels = new HashSet<>();
                private final String labelPrefix = prefix.orElse(tableName + "_");

                @Override
                public void add(String targetColumn) {
                    labels.add(targetColumn);
                }

                @Override
                public R mapRow(ResultSet rs) {
                    List<ResultSetValue<B>> values = columns.stream()
                        .filter(c -> labels.contains(c.label(labelPrefix)))
                        .map(c -> c.extract(database, rs, prefix.orElse(tableName + "_") + c.name()))
                        .collect(toList());
                    return values.stream().noneMatch(ResultSetValue::isPresent)
                        ? null
                        :  build(values);
                }
            };
        }
    }

    public static final class Builder<R, B> {
        private final Database database;
        private final TypeToken<R> rowType;
        private final TypeToken<B> builderType;
        private final Function<B,R> buildRow;
        private final Set<String> excludedFields = new HashSet<>();
        private final List<TableColumn<Object,R,B>> columns = new ArrayList<>();
        private String catalog;
        private String schema;
        private String tableName;
        private Supplier<B> newBuilder;

        public Builder(Database database, TypeToken<R> rowType, TypeToken<B> builderType, Function<B,R> buildRow) {
            this.database = database;
            this.rowType = rowType;
            this.builderType = builderType;
            this.buildRow = buildRow;

            Optional<javax.persistence.Table> tableAnnotation = ClassUtil.annotation(rowType.getRawType(), javax.persistence.Table.class);
            this.catalog = tableAnnotation
                .map(javax.persistence.Table::catalog)
                .flatMap(OptionalUtil::ofBlankable)
                .orElse(database.defaultCatalog());
            this.schema = tableAnnotation
                .map(javax.persistence.Table::schema)
                .flatMap(OptionalUtil::ofBlankable)
                .orElse(database.defaultSchema());
            this.tableName = tableAnnotation
                .map(javax.persistence.Table::name)
                .flatMap(OptionalUtil::ofBlankable)
                .orElseGet(() -> database.namingStrategy().tableName(rowType.getRawType().getSimpleName()));
        }

        public Table<R> build() {
            if (newBuilder == null) {
                this.newBuilder = Factory.forType(builderType);
            }
            mappedClasses(rowType.getRawType())
                .flatMap(cls -> Arrays.stream(cls.getDeclaredFields()))
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .filter(f -> !FieldUtil.hasAnnotation(Transient.class, f))
                .filter(f -> !excludedFields.contains(f.getName()))
                .forEach(this::addField);
            return new Table<>(this);
        }

        private void addField(Field field) {
            if (Collection.class.isAssignableFrom(field.getType())) {
                // TODO! add collection support
                return;
            }
            columns.add(TableColumn.fromField(database, rowType, builderType, field));
        }

        private Stream<Class<?>> mappedClasses(Class<?> startingWith) {
            return Stream.concat(
                ClassUtil.superclass(startingWith)
                    .filter(cls -> ClassUtil.hasAnnotation(cls, MappedSuperclass.class))
                    .map(this::mappedClasses)
                    .orElseGet(Stream::empty),
                Stream.of(startingWith));
        }

        public Builder<R,B> catalog(String val) {
            catalog = val;
            return this;
        }

        public Builder<R,B> schema(String val) {
            schema = val;
            return this;
        }

        public Builder<R,B> tableName(String val) {
            tableName = val;
            return this;
        }

        public <BB> Builder<R,BB> builder(Function1<BB,R> buildRow) {
            MethodInfo<BB,R> buildMethod = MethodInfo.of(buildRow);
            return new Builder<>(database, rowType, buildMethod.declaringType(), buildRow)
                .catalog(catalog)
                .schema(schema)
                .tableName(tableName);
        }

        public <T> Builder<R,B> column(Function1<R,T> getter, BiConsumer<B,T> setter) {
            return mandatory(getter, setter, Optional.empty());
        }

        public <T> Builder<R,B> column(FunctionOptional1<R,T> getter, BiConsumer<B,Optional<T>> setter) {
            return optional(getter, setter, Optional.empty());
        }

        public <T> Builder<R,B> column(Function1<R,T> getter, BiConsumer<B,T> setter, Consumer<TableColumn.Builder<T,R,B>> init) {
            return mandatory(getter, setter, Optional.of(init));
        }

        public <T> Builder<R,B> column(FunctionOptional1<R,T> getter, BiConsumer<B,Optional<T>> setter, Consumer<TableColumn.Builder<T,R,B>> init) {
            return optional(getter, setter, Optional.of(init));
        }

        @SuppressWarnings("unchecked")
        private <T> Builder<R,B> mandatory(Function1<R,T> getter, BiConsumer<B,T> setter, Optional<Consumer<TableColumn.Builder<T,R,B>>> init) {
            MethodInfo<R,T> getterInfo = MethodInfo.of(getter);
            String name = getterInfo.method().getName();
            excludedFields.add(name);
            TableColumn.Builder<T,R,B> columnBuilder = TableColumn.mandatory(name, database.getDataTypeOf(getterInfo), rowType, getter, setter);
            init.ifPresent(x -> x.accept(columnBuilder));
            columns.add((TableColumn<Object,R,B>) columnBuilder.build());
            return this;
        }

        @SuppressWarnings("unchecked")
        private <T> Builder<R,B> optional(FunctionOptional1<R,T> getter, BiConsumer<B,Optional<T>> setter, Optional<Consumer<TableColumn.Builder<T,R,B>>> init) {
            MethodInfo<R,T> getterInfo = MethodInfo.of(getter);
            String name = getterInfo.method().getName();
            excludedFields.add(name);
            TableColumn.Builder<T,R,B> columnBuilder = TableColumn.optional(name, database.getDataTypeOf(getterInfo), rowType, getter, setter);
            init.ifPresent(x -> x.accept(columnBuilder));
            columns.add((TableColumn<Object,R,B>) columnBuilder.build());
            return this;
        }
    }
}
