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
import com.cadenzauk.core.reflect.FieldInfo;
import com.cadenzauk.core.reflect.MethodInfo;
import com.cadenzauk.core.reflect.Setter;
import com.cadenzauk.core.reflect.util.ClassUtil;
import com.cadenzauk.core.reflect.util.FieldUtil;
import com.cadenzauk.core.sql.RowMapper;
import com.cadenzauk.core.sql.RowMapperFactory;
import com.cadenzauk.core.stream.StreamUtil;
import com.cadenzauk.core.util.OptionalUtil;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.DataType;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.DynamicRowMapperFactory;
import com.cadenzauk.siesta.NamingStrategy;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.cadenzauk.core.reflect.util.ClassUtil.hasAnnotation;
import static com.cadenzauk.core.util.OptionalUtil.or;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class ColumnMapping<R, B> implements ColumnCollection<R> {
    private final Database database;
    private final TypeToken<R> rowType;
    private final Supplier<B> newRowBuilder;
    private final Function<B,R> buildRow;
    private final List<TableColumn<?,R,B>> columns;

    ColumnMapping(Builder<R,B,?> builder) {
        database = builder.database;
        rowType = builder.rowType;
        newRowBuilder = builder.newBuilder;
        buildRow = builder.buildRow;
        columns = builder.columns;
    }

    @Override
    public TypeToken<R> rowType() {
        return rowType;
    }

    @Override
    public Stream<Column<?,R>> columns() {
        return columns.stream().map(Function.identity());
    }

    @Override
    public <T> Column<T,R> column(MethodInfo<R,T> methodInfo) {
        String propertyName = methodInfo.propertyName();
        return findColumn(methodInfo.effectiveType(), propertyName)
            .orElseThrow(() -> new IllegalArgumentException("No column for property " + propertyName + " in " + methodInfo.referringClass()));
    }

    @Override
    public <T> ColumnCollection<T> embedded(MethodInfo<R,T> methodInfo) {
        String propertyName = methodInfo.propertyName();
        return OptionalUtil.as(new TypeToken<ColumnCollection<T>>() {}, column(methodInfo))
            .orElseThrow(() -> new IllegalArgumentException("No column for property " + propertyName + " in " + methodInfo.referringClass()));
    }

    @Override
    public RowMapperFactory<R> rowMapperFactory(Alias<?> alias, Optional<String> defaultLabel) {
        return label -> rs -> {
            List<TableColumn.ResultSetValue<B>> values = columns.stream()
                .map(c -> c.extract(alias, rs, computeLabel(alias, label, defaultLabel, c)))
                .collect(toList());
            return values.stream().noneMatch(TableColumn.ResultSetValue::isPresent)
                ? null
                : buildRow(values);
        };
    }

    private Optional<String> computeLabel(Alias<?> alias, Optional<String> prefix, Optional<String> defaultLabel, TableColumn<?,R,B> col) {
        return or(
            prefix.map(p ->
                p + defaultLabel
                    .map(l -> labelOf(alias, l, col))
                    .orElseGet(() -> alias.inSelectClauseLabel(col.columnName()))),
            defaultLabel.map(l -> labelOf(alias, l, col)));
    }

    private String labelOf(Alias<?> alias, String defaultLabel, TableColumn<?,R,B> c) {
        NamingStrategy naming = alias.database().namingStrategy();
        return naming.embeddedName(defaultLabel, c.columnName());
    }

    DynamicRowMapperFactory<R> dynamicRowMapperFactoryFactory(Alias<?> alias) {
        return new DynamicRowMapperFactory<R>() {
            private final Set<String> labels = new HashSet<>();

            @Override
            public void add(String targetColumn) {
                labels.add(targetColumn);
            }

            @Override
            public RowMapper<R> rowMapper(Optional<String> label) {
                return rs -> {
                    List<TableColumn.ResultSetValue<B>> values = columns.stream()
                        .filter(c -> labels.contains(alias.inSelectClauseLabel(c.columnName())))
                        .map(c -> c.extract(alias, rs, label))
                        .collect(toList());
                    return values.stream().noneMatch(TableColumn.ResultSetValue::isPresent)
                        ? null
                        : buildRow(values);
                };
            }
        };
    }

    Object[] insertArgs(R[] rows) {
        return Arrays.stream(rows)
            .map(Optional::ofNullable)
            .flatMap(r -> columns().flatMap(c -> c.insertArgs(database, r)))
            .toArray();
    }

    Object[] updateArgs(R row) {
        return StreamUtil.ofNullable(row)
            .flatMap(r -> Stream.concat(
                columns().flatMap(c -> c.updateArgs(database, r)),
                columns().flatMap(c -> c.idArgs(database, r))))
            .toArray();
    }

    Object[] deleteArgs(R row) {
        return StreamUtil.ofNullable(row)
            .flatMap(r -> columns().flatMap(c -> c.idArgs(database, r)))
            .toArray();
    }

    <T> Optional<Column<T,R>> findColumn(TypeToken<T> type, String propertyName) {
        return columnsOfType(type)
            .filter(c -> StringUtils.equals(c.propertyName(), propertyName))
            .findFirst();
    }

    private <T> Stream<Column<T,R>> columnsOfType(TypeToken<T> dataType) {
        return columns().flatMap(c -> c.as(dataType));
    }

    private R buildRow(List<TableColumn.ResultSetValue<B>> values) {
        B builder = newRowBuilder.get();
        values.forEach(v -> v.apply(builder));
        return buildRow.apply(builder);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class Builder<R, B, S extends Builder<R,B,S>> {
        private final Set<String> excludedFields = new HashSet<>();
        protected final Database database;
        protected final TypeToken<R> rowType;
        private final TypeToken<B> builderType;
        private final Function<B,R> buildRow;
        private final List<TableColumn<?,R,B>> columns = new ArrayList<>();
        private Optional<String> childPrefix = Optional.empty();
        private Supplier<B> newBuilder = null;
        private Map<String,List<AttributeOverride>> overrides = ImmutableMap.of();

        protected Builder(Database database, TypeToken<R> rowType, TypeToken<B> builderType, Function<B,R> buildRow) {
            this.database = database;
            this.rowType = rowType;
            this.builderType = builderType;
            this.buildRow = buildRow;
        }

        @SuppressWarnings("unchecked")
        private S self() {
            return (S) this;
        }

        public S newBuilder(Supplier<B> val) {
            newBuilder = val;
            return self();
        }

        protected void childPrefix(Optional<String> val) {
            this.childPrefix = Optional.ofNullable(val).flatMap(Function.identity());
        }

        protected void finish() {
            if (newBuilder == null) {
                this.newBuilder = Factory.forType(builderType);
            }
            mappedClasses(rowType.getRawType())
                .flatMap(cls -> Arrays.stream(cls.getDeclaredFields()))
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .filter(f -> !FieldUtil.hasAnnotation(Transient.class, f))
                .filter(f -> !excludedFields.contains(f.getName()))
                .forEach(this::addField);
        }

        @SuppressWarnings("unchecked")
        private void addField(Field field) {
            FieldInfo<R,Object> fieldInfo = (FieldInfo<R,Object>) FieldInfo.of(rowType, field);
            if (fieldInfo.hasAnnotation(Embedded.class)
                || fieldInfo.hasAnnotation(EmbeddedId.class)
                || ClassUtil.hasAnnotation(fieldInfo.effectiveClass(), Embeddable.class)) {
                addEmbedded(fieldInfo);
            } else {
                addPrimitive(fieldInfo);
            }
        }

        private Stream<Class<?>> mappedClasses(Class<?> startingWith) {
            return Stream.concat(
                ClassUtil.superclass(startingWith)
                    .filter(cls -> hasAnnotation(cls, MappedSuperclass.class)
                        || hasAnnotation(cls, javax.persistence.Table.class)
                        || hasAnnotation(cls, Embeddable.class))
                    .map(this::mappedClasses)
                    .orElseGet(Stream::empty),
                Stream.of(startingWith));
        }

        public <T> S column(Function1<R,T> getter, String name) {
            BiConsumer<B,T> setter = setter(getter);
            return column(getter, setter, c -> c.columnName(name));
        }

        public <T> S column(FunctionOptional1<R,T> getter, String name) {
            BiConsumer<B,Optional<T>> setter = setter(getter);
            return column(getter, setter, c -> c.columnName(name));
        }

        public <T> S column(Function1<R,T> getter, Consumer<PrimitiveColumn.Builder<?,R,B>> init) {
            BiConsumer<B,T> setter = setter(getter);
            return mandatory(getter, setter, Optional.of(init::accept));
        }

        public <T> S column(FunctionOptional1<R,T> getter, Consumer<PrimitiveColumn.Builder<?,R,B>> init) {
            BiConsumer<B,Optional<T>> setter = setter(getter);
            return optional(getter, setter, Optional.of(init::accept));
        }

        public <T> S column(@SuppressWarnings("unused") Class<T> type, Function1<R,T> getter, Consumer<PrimitiveColumn.Builder<T,R,B>> init) {
            BiConsumer<B,T> setter = setter(getter);
            return mandatory(getter, setter, Optional.of(init));
        }

        public <T> S column(@SuppressWarnings("unused") Class<T> type, FunctionOptional1<R,T> getter, Consumer<PrimitiveColumn.Builder<T,R,B>> init) {
            BiConsumer<B,Optional<T>> setter = setter(getter);
            return optional(getter, setter, Optional.of(init));
        }

        public <T> S column(Function1<R,T> getter, BiConsumer<B,T> setter, Consumer<PrimitiveColumn.Builder<T,R,B>> init) {
            return mandatory(getter, setter, Optional.of(init));
        }

        public <T> S column(FunctionOptional1<R,T> getter, BiConsumer<B,Optional<T>> setter, Consumer<PrimitiveColumn.Builder<T,R,B>> init) {
            return optional(getter, setter, Optional.of(init));
        }

        public <T> S embedded(@SuppressWarnings("unused") Class<T> klass, Function1<R,T> getter) {
            BiConsumer<B,T> setter = setter(getter);
            return embedded(getter, setter, Function.identity());
        }

        public <T> S embedded(@SuppressWarnings("unused") Class<T> klass, FunctionOptional1<R,T> getter) {
            BiConsumer<B,Optional<T>> setter = setter(getter);
            return embedded(getter, setter, Function.identity());
        }

        public <T, TB> S embedded(@SuppressWarnings("unused") Class<T> klass, Function1<R,T> getter, Function<EmbeddedColumn.Builder<T,T,R,B>,EmbeddedColumn.Builder<T,TB,R,B>> init) {
            BiConsumer<B,T> setter = setter(getter);
            return embedded(getter, setter, init);
        }

        public <T, TB> S embedded(@SuppressWarnings("unused") Class<T> klass, FunctionOptional1<R,T> getter, Function<EmbeddedColumn.Builder<T,T,R,B>,EmbeddedColumn.Builder<T,TB,R,B>> init) {
            BiConsumer<B,Optional<T>> setter = setter(getter);
            return embedded(getter, setter, init);
        }

        S overrides(Map<String,List<AttributeOverride>> overrides) {
            this.overrides = ImmutableMap.copyOf(overrides);
            return self();
        }

        @SuppressWarnings("unchecked")
        protected Optional<? extends Column<Object,R>> findColumn(String propertyName) {
            return columns
                .stream()
                .filter(col -> StringUtils.equalsIgnoreCase(col.propertyName(), propertyName))
                .map(col -> (Column<Object,R>)col)
                .findFirst();
        }

        private Optional<String> overrideColumnName(String propertyName) {
            return override(propertyName, javax.persistence.Column::name).filter(StringUtils::isNotBlank);
        }

        private Optional<Boolean> overrideInsertable(String propertyName) {
            return override(propertyName, javax.persistence.Column::insertable);
        }

        private Optional<Boolean> overrideUpdatable(String propertyName) {
            return override(propertyName, javax.persistence.Column::updatable);
        }

        private <T> Optional<T> override(String propertyName, Function<javax.persistence.Column,T> function) {
            return Optional.ofNullable(overrides.get(propertyName))
                .flatMap(o -> o.stream()
                    .findFirst()
                    .map(AttributeOverride::column)
                    .map(function));
        }

        private <T> String determineColumnNameFor(MethodInfo<R,T> getterInfo) {
            return overrideColumnName(getterInfo.propertyName())
                .orElseGet(() -> {
                    String columnName = database.columnNameFor(getterInfo);
                    return childPrefix
                        .map(p -> database.namingStrategy().embeddedName(p, columnName))
                        .orElse(columnName);
                });
        }

        private <T> String determineColumnNameFor(FieldInfo<R,T> fieldInfo) {
            return overrideColumnName(fieldInfo.name())
                .orElseGet(() -> {
                    String columnName = database.columnNameFor(fieldInfo);
                    return childPrefix
                        .map(p -> database.namingStrategy().embeddedName(p, columnName))
                        .orElse(columnName);
                });
        }

        private <T> boolean determineInsertableFor(FieldInfo<R,T> fieldInfo) {
            return overrideInsertable(fieldInfo.name())
                .orElseGet(() -> {
                    Optional<javax.persistence.Column> annotation = fieldInfo.annotation(javax.persistence.Column.class);
                    return annotation.map(javax.persistence.Column::insertable)
                        .orElse(true);
                });
        }

        private <T> boolean determineUpdateableFor(FieldInfo<R,T> fieldInfo) {
            return overrideUpdatable(fieldInfo.name())
                .orElseGet(() -> {
                    Optional<javax.persistence.Column> annotation = fieldInfo.annotation(javax.persistence.Column.class);
                    return annotation.map(javax.persistence.Column::updatable)
                        .orElse(true);
                });
        }

        private <T> void setColumnName(PrimitiveColumn.Builder<T,R,B> columnBuilder, MethodInfo<R,T> getterInfo) {
            columnBuilder.columnName(determineColumnNameFor(getterInfo));
        }

        private <T> void setColumnName(EmbeddedColumn.Builder<T,T,R,B> columnBuilder, MethodInfo<R,T> getterInfo) {
            columnBuilder.columnName(determineColumnNameFor(getterInfo));
        }

        private <T> BiConsumer<B,T> setter(Function1<R,T> getter) {
            BiConsumer<B,Optional<T>> setter = setter(MethodInfo.of(getter));
            return (b, v) -> setter.accept(b, Optional.ofNullable(v));
        }

        private <T> BiConsumer<B,Optional<T>> setter(FunctionOptional1<R,T> getter) {
            return setter(MethodInfo.of(getter));
        }

        private <T> BiConsumer<B,Optional<T>> setter(MethodInfo<R,T> methodInfo) {
            FieldInfo<R,T> fieldInfo = FieldInfo.ofGetter(methodInfo).orElseThrow(IllegalArgumentException::new);
            Field builderField = ClassUtil.findField(builderType.getRawType(), fieldInfo.name())
                .orElseThrow(() -> new IllegalArgumentException("Builder class " + builderType + " does not have a field " + fieldInfo.name() + "."));
            return Setter.forField(builderType, fieldInfo.effectiveClass(), builderField);
        }

        private <T> S mandatory(Function1<R,T> getter, BiConsumer<B,T> setter, Optional<Consumer<PrimitiveColumn.Builder<T,R,B>>> init) {
            MethodInfo<R,T> getterInfo = MethodInfo.of(getter);
            String name = getterInfo.propertyName();
            PrimitiveColumn.Builder<T,R,B> columnBuilder = PrimitiveColumn.mandatory(database, name, database.getDataTypeOf(getterInfo), getter, setter);
            addPrimitive(init, getterInfo, name, columnBuilder);
            return self();
        }

        private <T> S optional(FunctionOptional1<R,T> getter, BiConsumer<B,Optional<T>> setter, Optional<Consumer<PrimitiveColumn.Builder<T,R,B>>> init) {
            MethodInfo<R,T> getterInfo = MethodInfo.of(getter);
            String name = getterInfo.propertyName();
            PrimitiveColumn.Builder<T,R,B> columnBuilder = PrimitiveColumn.optional(database, name, database.getDataTypeOf(getterInfo), getter, setter);
            addPrimitive(init, getterInfo, name, columnBuilder);
            return self();
        }

        private <T> void addPrimitive(Optional<Consumer<PrimitiveColumn.Builder<T,R,B>>> init, MethodInfo<R,T> getterInfo, String name, PrimitiveColumn.Builder<T,R,B> columnBuilder) {
            excludedFields.add(name);
            setColumnName(columnBuilder, getterInfo);
            init.ifPresent(x -> x.accept(columnBuilder));
            columns.add(columnBuilder.build());
        }

        private <T, TB> S embedded(Function1<R,T> getter, BiConsumer<B,T> setter, Function<EmbeddedColumn.Builder<T,T,R,B>,EmbeddedColumn.Builder<T,TB,R,B>> init) {
            MethodInfo<R,T> getterInfo = MethodInfo.of(getter);
            String name = getterInfo.propertyName();
            EmbeddedColumn.Builder<T,T,R,B> columnBuilder = EmbeddedColumn.mandatory(database, name, getterInfo.effectiveType(), getter, setter);
            addEmbedded(init, getterInfo, name, columnBuilder);
            return self();
        }

        private <T, TB> S embedded(FunctionOptional1<R,T> getter, BiConsumer<B,Optional<T>> setter, Function<EmbeddedColumn.Builder<T,T,R,B>,EmbeddedColumn.Builder<T,TB,R,B>> init) {
            MethodInfo<R,T> getterInfo = MethodInfo.of(getter);
            String name = getterInfo.propertyName();
            EmbeddedColumn.Builder<T,T,R,B> columnBuilder = EmbeddedColumn.optional(database, name, getterInfo.effectiveType(), getter, setter);
            addEmbedded(init, getterInfo, name, columnBuilder);
            return self();
        }

        private <T, TB> void addEmbedded(Function<EmbeddedColumn.Builder<T,T,R,B>,EmbeddedColumn.Builder<T,TB,R,B>> init, MethodInfo<R,T> getterInfo, String name, EmbeddedColumn.Builder<T,T,R,B> columnBuilder) {
            excludedFields.add(name);
            setColumnName(columnBuilder, getterInfo);
            columns.add(init.apply(columnBuilder).build());
        }

        private <T> void addPrimitive(FieldInfo<R,T> fieldInfo) {
            DataType<T> dataType = database
                .dataTypeOf(fieldInfo).orElseThrow(() -> new IllegalArgumentException("Unable to determine the data type for " + fieldInfo));

            Field builderField = ClassUtil.findField(builderType.getRawType(), fieldInfo.name())
                .orElseThrow(() -> new IllegalArgumentException("Builder class " + builderType + " does not have a field " + fieldInfo.name() + "."));

            Optional<Id> idAnnotation = fieldInfo.annotation(Id.class);

            PrimitiveColumn.Builder<T,R,B> columnBuilder = PrimitiveColumn.optional(
                database,
                fieldInfo.name(),
                dataType,
                fieldInfo.optionalGetter(),
                Setter.forField(builderType, fieldInfo.effectiveClass(), builderField))
                .identifier(idAnnotation.isPresent())
                .insertable(determineInsertableFor(fieldInfo))
                .updatable(determineUpdateableFor(fieldInfo))
                .columnName(determineColumnNameFor(fieldInfo));

            columns.add(columnBuilder.build());
        }

        private <T> void addEmbedded(FieldInfo<R,T> fieldInfo) {
            Map<String,List<AttributeOverride>> overrides = Stream.concat(
                StreamUtil.of(fieldInfo.annotation(AttributeOverride.class)),
                StreamUtil.of(fieldInfo.annotation(AttributeOverrides.class))
                    .flatMap(a -> Arrays.stream(a.value())))
                .collect(groupingBy(AttributeOverride::name));

            Field builderField = ClassUtil.findField(builderType.getRawType(), fieldInfo.name())
                .orElseThrow(() -> new IllegalArgumentException("Builder class " + builderType + " does not have a field " + fieldInfo.name() + "."));

            Optional<Id> idAnnotation = fieldInfo.annotation(Id.class);
            Optional<EmbeddedId> embeddedIdAnnotation = fieldInfo.annotation(EmbeddedId.class);

            EmbeddedColumn.Builder<T,T,R,B> columnBuilder = EmbeddedColumn.optional(
                database,
                fieldInfo.name(),
                fieldInfo.effectiveType(),
                fieldInfo.optionalGetter(),
                Setter.forField(builderType, fieldInfo.effectiveClass(), builderField))
                .overrides(overrides)
                .type(fieldInfo.effectiveType())
                .identifier(idAnnotation.isPresent() || embeddedIdAnnotation.isPresent())
                .insertable(determineInsertableFor(fieldInfo))
                .updatable(determineUpdateableFor(fieldInfo))
                .columnName(determineColumnNameFor(fieldInfo));

            columns.add(columnBuilder.build());
        }
    }
}
