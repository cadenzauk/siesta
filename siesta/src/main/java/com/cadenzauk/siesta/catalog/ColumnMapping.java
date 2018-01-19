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
import com.cadenzauk.core.stream.StreamUtil;
import com.cadenzauk.siesta.Alias;
import com.cadenzauk.siesta.DataType;
import com.cadenzauk.siesta.Database;
import com.cadenzauk.siesta.DynamicRowMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
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
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class ColumnMapping<R, B> {
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

    TypeToken<R> rowType() {
        return rowType;
    }

    Stream<TableColumn<?,R,B>> columns() {
        return columns.stream();
    }

    RowMapper<R> rowMapper(Alias<?> alias) {
        return rs -> {
            List<TableColumn.ResultSetValue<B>> values = columns.stream()
                .map(c -> c.extract(alias, rs, Optional.empty()))
                .collect(toList());
            return values.stream().noneMatch(TableColumn.ResultSetValue::isPresent)
                ? null
                : buildRow(values);
        };
    }

    DynamicRowMapper<R> dynamicRowMapper(Alias<?> alias) {
        return new DynamicRowMapper<R>() {
            private final Set<String> labels = new HashSet<>();

            @Override
            public void add(String targetColumn) {
                labels.add(targetColumn);
            }

            @Override
            public R mapRow(ResultSet rs) {
                List<TableColumn.ResultSetValue<B>> values = columns.stream()
                    .filter(c -> labels.contains(alias.inSelectClauseLabel(c.columnName())))
                    .map(c -> c.extract(alias, rs, Optional.empty()))
                    .collect(toList());
                return values.stream().noneMatch(TableColumn.ResultSetValue::isPresent)
                    ? null
                    : buildRow(values);
            }
        };
    }

    Object[] args(R[] rows) {
        return Arrays.stream(rows)
            .map(Optional::ofNullable)
            .flatMap(this::toDatabase)
            .toArray();
    }

    Stream<Object> toDatabase(Optional<R> row) {
        return columns
            .stream()
            .flatMap(c -> c.rowToDatabase(database, row));
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

    public static class Builder<R, B, S extends Builder<R,B,S>> {
        private final Set<String> excludedFields = new HashSet<>();
        private final Database database;
        private final TypeToken<R> rowType;
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
            if (fieldInfo.hasAnnotation(Embedded.class) || ClassUtil.hasAnnotation(fieldInfo.effectiveClass(), Embeddable.class)) {
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

        public <T> S column(Function1<R,T> getter, BiConsumer<B,T> setter) {
            return mandatory(getter, setter, Optional.empty());
        }

        public <T> S column(FunctionOptional1<R,T> getter, BiConsumer<B,Optional<T>> setter) {
            return optional(getter, setter, Optional.empty());
        }

        public <T> S column(Function1<R,T> getter, BiConsumer<B,T> setter, Consumer<PrimitiveColumn.Builder<T,R,B>> init) {
            return mandatory(getter, setter, Optional.of(init));
        }

        public <T> S column(FunctionOptional1<R,T> getter, BiConsumer<B,Optional<T>> setter, Consumer<PrimitiveColumn.Builder<T,R,B>> init) {
            return optional(getter, setter, Optional.of(init));
        }

        public <T> S embedded(@SuppressWarnings("unused") Class<T> klass, Function1<R,T> getter) {
            BiConsumer<B,T> setter = setter(getter);
            return embedded(getter, setter, Optional.empty());
        }

        public <T> S embedded(@SuppressWarnings("unused") Class<T> klass, FunctionOptional1<R,T> getter) {
            BiConsumer<B,Optional<T>> setter = setter(getter);
            return embedded(getter, setter, Optional.empty());
        }

        public <T> S embedded(@SuppressWarnings("unused") Class<T> klass, Function1<R,T> getter, Consumer<EmbeddedColumn.Builder<T,R,B>> init) {
            BiConsumer<B,T> setter = setter(getter);
            return embedded(getter, setter, Optional.of(init));
        }

        public <T> S embedded(@SuppressWarnings("unused") Class<T> klass, FunctionOptional1<R,T> getter, Consumer<EmbeddedColumn.Builder<T,R,B>> init) {
            BiConsumer<B,Optional<T>> setter = setter(getter);
            return embedded(getter, setter, Optional.of(init));
        }

        S overrides(Map<String,List<AttributeOverride>> overrides) {
            this.overrides = ImmutableMap.copyOf(overrides);
            return self();
        }

        private Optional<String> overrideColumnName(String propertyName) {
            return Optional.ofNullable(overrides.get(propertyName))
                .flatMap(o -> o.stream()
                    .findFirst()
                    .map(AttributeOverride::column)
                    .map(javax.persistence.Column::name));
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

        private <T> void setColumnName(PrimitiveColumn.Builder<T,R,B> columnBuilder, MethodInfo<R,T> getterInfo) {
            columnBuilder.columnName(determineColumnNameFor(getterInfo));
        }

        private <T> void setColumnName(EmbeddedColumn.Builder<T,R,B> columnBuilder, MethodInfo<R,T> getterInfo) {
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
            excludedFields.add(name);
            PrimitiveColumn.Builder<T,R,B> columnBuilder = PrimitiveColumn.mandatory(database, name, database.getDataTypeOf(getterInfo), getter, setter);
            setColumnName(columnBuilder, getterInfo);
            init.ifPresent(x -> x.accept(columnBuilder));
            columns.add(columnBuilder.build());
            return self();
        }

        private <T> S optional(FunctionOptional1<R,T> getter, BiConsumer<B,Optional<T>> setter, Optional<Consumer<PrimitiveColumn.Builder<T,R,B>>> init) {
            MethodInfo<R,T> getterInfo = MethodInfo.of(getter);
            String name = getterInfo.propertyName();
            excludedFields.add(name);
            PrimitiveColumn.Builder<T,R,B> columnBuilder = PrimitiveColumn.optional(database, name, database.getDataTypeOf(getterInfo), getter, setter);
            setColumnName(columnBuilder, getterInfo);
            init.ifPresent(x -> x.accept(columnBuilder));
            columns.add(columnBuilder.build());
            return self();
        }

        private <T> S embedded(Function1<R,T> getter, BiConsumer<B,T> setter, Optional<Consumer<EmbeddedColumn.Builder<T,R,B>>> init) {
            MethodInfo<R,T> getterInfo = MethodInfo.of(getter);
            String name = getterInfo.propertyName();
            excludedFields.add(name);
            EmbeddedColumn.Builder<T,R,B> columnBuilder = EmbeddedColumn.mandatory(database, name, getterInfo.effectiveType(), getter, setter);
            setColumnName(columnBuilder, getterInfo);
            init.ifPresent(x -> x.accept(columnBuilder));
            columns.add(columnBuilder.build());
            return self();
        }

        private <T> S embedded(FunctionOptional1<R,T> getter, BiConsumer<B,Optional<T>> setter, Optional<Consumer<EmbeddedColumn.Builder<T,R,B>>> init) {
            MethodInfo<R,T> getterInfo = MethodInfo.of(getter);
            String name = getterInfo.propertyName();
            excludedFields.add(name);
            EmbeddedColumn.Builder<T,R,B> columnBuilder = EmbeddedColumn.optional(database, name, getterInfo.effectiveType(), getter, setter);
            setColumnName(columnBuilder, getterInfo);
            init.ifPresent(x -> x.accept(columnBuilder));
            columns.add(columnBuilder.build());
            return self();
        }

        private <T> void addPrimitive(FieldInfo<R,T> fieldInfo) {
            DataType<T> dataType = database
                .dataTypeOf(fieldInfo).orElseThrow(() -> new IllegalArgumentException("Unable to determine the data type for " + fieldInfo));

            Field builderField = ClassUtil.findField(builderType.getRawType(), fieldInfo.name())
                .orElseThrow(() -> new IllegalArgumentException("Builder class " + builderType + " does not have a field " + fieldInfo.name() + "."));

            PrimitiveColumn.Builder<T,R,B> columnBuilder = PrimitiveColumn.optional(
                database,
                fieldInfo.name(),
                dataType,
                fieldInfo.optionalGetter(),
                Setter.forField(builderType, fieldInfo.effectiveClass(), builderField))
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

            EmbeddedColumn.Builder<T,R,B> columnBuilder = EmbeddedColumn.optional(
                database,
                fieldInfo.name(),
                fieldInfo.effectiveType(),
                fieldInfo.optionalGetter(),
                Setter.forField(builderType, fieldInfo.effectiveClass(), builderField))
                .overrides(overrides)
                .columnName(determineColumnNameFor(fieldInfo));

            columns.add(columnBuilder.build());
        }
    }
}
